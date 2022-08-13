package com.chaosbuffalo.bonetown.entity.animation_state;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.IAnimationLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.layer.AnimationLayerMessage;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

public class AnimationState<T extends Entity & IBTAnimatedEntity<T>> {

    private final List<IAnimationLayer<T>> layers;
    private final Map<String, IAnimationLayer<T>> layerIndex;
    private final T entity;
    private final BoneMFSkeleton skeleton;
    private final String name;
    private Function<AABB, AABB> bboxModifier;

    public AnimationState(String name, T entity){
        this.name = name;
        this.entity = entity;
        this.layerIndex = new HashMap<>();
        this.layers = new ArrayList<>();
        this.skeleton = entity.getSkeleton();
        if (skeleton == null) {
            BoneTown.LOGGER.error("Animation state: {} broken, can't find skeleton", name);
        }
    }

    public AnimationState(String name, T entity, Function<AABB, AABB> bboxModifier){
        this(name, entity);
        this.bboxModifier = bboxModifier;
    }

    public String getName() {
        return name;
    }

    public void tickState(int currentTicks){
        for (IAnimationLayer<T> layer : layers){
            if (layer.shouldRun()){
                layer.tick(currentTicks);
            }
        }
    }

    public AABB applyStateToBoundingBox(AABB boundingBox){
        if (bboxModifier != null){
            return bboxModifier.apply(boundingBox);
        } else {
            return boundingBox;
        }
    }

    public void addLayer(IAnimationLayer<T> layer){
        layers.add(layer);
        layerIndex.put(layer.getLayerName(), layer);
    }

    public void clearLayers(){
        layers.clear();
        layerIndex.clear();
    }

    public void consumeLayerMessage(String layerName, AnimationLayerMessage message){
        IAnimationLayer<T> layer = getLayer(layerName);
        if (layer != null){
            layer.consumeLayerMessage(message);
        }
    }

    public boolean isValid() {
        return skeleton != null;
    }

    public BoneMFSkeleton getSkeleton() {
        return skeleton;
    }

    public T getEntity() {
        return entity;
    }

    public void startLayer(String name, int currentTicks){
       IAnimationLayer<T> layer = layerIndex.get(name);
       if (layer != null){
           layer.setStartTime(currentTicks);
           layer.setActive(true);
       }
    }

    public void stopLayer(String name){
        IAnimationLayer<T> layer = layerIndex.get(name);
        if (layer != null){
            layer.setActive(false);
        }
    }

    @Nullable
    public IAnimationLayer<T> getLayer(String name){
        return layerIndex.get(name);
    }

    public void leaveState(){
        for (IAnimationLayer<T> layer : layers){
            layer.setActive(false);
        }
    }

    public void enterState(int startTime){
        for (IAnimationLayer<T> layer : layers){
            if (layer.shouldAutoStart()){
                layer.setActive(true);
                layer.setStartTime(startTime);
            }
        }
    }

    public void applyToPose(int currentTicks, float partialTicks, IPose workFrame){
        if (isValid()) {
            for (IAnimationLayer<T> layer : layers) {
                layer.processLayer(skeleton.getBindPose(), currentTicks, partialTicks, workFrame);
            }
        }
    }
}
