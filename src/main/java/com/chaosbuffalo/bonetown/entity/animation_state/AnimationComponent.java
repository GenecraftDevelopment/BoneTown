package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.animation.Pose;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import com.chaosbuffalo.bonetown.network.EntityAnimationClientUpdatePacket;
import com.chaosbuffalo.bonetown.network.PacketHandler;
import com.mojang.datafixers.types.templates.CompoundList;
import com.mojang.math.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiConsumer;

public class AnimationComponent<T extends Entity & IBTAnimatedEntity<T>> implements INBTSerializable<CompoundTag> {

    private final T entity;
    private int ticks;
    private final Map<String, AnimationState<T>> animationStates;
    private final Pose workFrame;
    private static final Pose DEFAULT_FRAME = new Pose();
    private int lastPoseFetch;
    private float lastPartialTicks;
    public static final String INVALID_STATE = "invalid";
    public final Stack<String> stateStack;

    private final List<AnimationMessage> syncQueue;

    private static final Map<String, BiConsumer<AnimationComponent<?>, AnimationMessage>> messageHandlers = new HashMap<>();

    public static void addMessageHandler(String messageType, BiConsumer<AnimationComponent<?>, AnimationMessage> handler){
        messageHandlers.put(messageType, handler);
    }

    public AnimationComponent(T entity){
        this.entity = entity;
        ticks = 0;
        this.syncQueue = new ArrayList<>();
        this.stateStack = new Stack<>();
        animationStates = new HashMap<>();
        workFrame = new Pose();
        if (entity.getSkeleton() != null){
            workFrame.setJointCount(entity.getSkeleton().getBones().size());
        }
        lastPoseFetch = -1;
        lastPartialTicks = 0;
    }

    public void addAnimationState(AnimationState<T> animState){
        animationStates.put(animState.getName(), animState);
    }

    public void removeAnimationState(String name){
        animationStates.remove(name);
    }

    public void startLayer(String stateName, String name){
        AnimationState<T> state = getState(stateName);
        if (state != null){
            state.startLayer(name, ticks);
        }
    }

    public void distributeLayerMessage(String stateName, String layerName, AnimationLayerMessage message){
        AnimationState<T> state = getState(stateName);
        if (state != null){
            state.consumeLayerMessage(layerName, message);
        }
    }

    public void updateState(AnimationMessage message){
        if (getEntity().level.isClientSide()){
            syncQueue.add(message);
        }
        if (messageHandlers.containsKey(message.getType())){
            messageHandlers.get(message.getType()).accept(this, message);
        } else {
            BoneTown.LOGGER.warn("AnimationMessage type {} not handled by {}", message.getType(),
                    getEntity().toString());
        }

    }

    public void stopLayer(String stateName, String name){
        AnimationState<T> state = getState(stateName);
        if (state != null){
            state.stopLayer(name);
        }
    }

    public void pushState(String stateName){
        if (!getCurrentStateName().equals(INVALID_STATE)){
            AnimationState<T> state = getState(getCurrentStateName());
            if (state != null){
                state.leaveState();
            }
        }

        AnimationState<T> newState = getState(stateName);
        if (newState != null){
            stateStack.push(stateName);
            newState.enterState(ticks);
        } else {
            stateStack.push(INVALID_STATE);
        }
    }

    public void popState(){
        if (!getCurrentStateName().equals(INVALID_STATE)){
            AnimationState<T> state = getCurrentState();
            if (state != null){
                state.leaveState();
            }
        }
        stateStack.pop();
        String newState = getCurrentStateName();
        if (!newState.equals(INVALID_STATE)){
            AnimationState<T> state = getState(newState);
            if (state != null){
                state.enterState(ticks);
            } else {
                stateStack.pop();
                stateStack.push(INVALID_STATE);
            }
        }
    }


    protected boolean isSamePose(float partialTicks){
        return ticks == lastPoseFetch && partialTicks == lastPartialTicks;
    }


    public IPose getCurrentPose(){
        return getCurrentPose(0);
    }

    public IPose getCurrentPose(float partialTicks){
        if (isSamePose(partialTicks)){
            return workFrame;
        }
        if (getCurrentStateName().equals(INVALID_STATE)){
            BoneTown.LOGGER.warn("Animation for entity: {} currently in invalid state", getEntity().toString());
            return DEFAULT_FRAME;
        }
        AnimationState<T> state = getState(getCurrentStateName());
        if (state != null){
            state.applyToPose(ticks, partialTicks, workFrame);
            lastPoseFetch = ticks;
            lastPartialTicks = partialTicks;
            return workFrame;
        } else {
            BoneTown.LOGGER.warn("Animation for entity: {} state not found: {}",
                    getEntity().toString(), getCurrentStateName());
            return DEFAULT_FRAME;
        }
    }

    public String getCurrentStateName() {
        if (stateStack.empty()){
            return INVALID_STATE;
        }
        return stateStack.peek();
    }

    public AnimationState<T> getCurrentState(){
        return getState(getCurrentStateName());
    }


    @Nullable
    public AnimationState<T> getState(String state){
        return animationStates.get(state);
    }

    public T getEntity(){
        return entity;
    }


    public int getTicks() {
        return ticks;
    }

    public void update() {
        ticks++;
        Level world = getEntity().getLevel();
        if (world.isClientSide){
            if (syncQueue.size() > 0){
                EntityAnimationClientUpdatePacket packet = new EntityAnimationClientUpdatePacket(
                        getEntity(),
                        syncQueue);
                PacketDistributor.TRACKING_ENTITY.with(this::getEntity)
                        .send(PacketHandler.getNetworkChannel().toVanillaPacket(
                                packet, NetworkDirection.PLAY_TO_CLIENT));
                syncQueue.clear();
            }
        }
        AnimationState<T> state = getState(getCurrentStateName());
        if (state != null){
            state.tickState(ticks);
        }
    }



    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag stack = new ListTag();
        stateStack.forEach((stateName) -> {
            stack.add(StringTag.valueOf(stateName));
        });
        tag.put("stateStack", stack);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        if (nbt.contains("stateStack")){
            stateStack.clear();
            ListTag stack = nbt.getList("stateStack", Tag.TAG_STRING);
            stack.forEach(tag -> {
                String stateName = tag.getAsString();
                stateStack.push(stateName);
            });
            pushState(stateStack.pop());
        }
    }
}
