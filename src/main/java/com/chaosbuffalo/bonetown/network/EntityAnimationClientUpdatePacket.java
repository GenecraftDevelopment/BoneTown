package com.chaosbuffalo.bonetown.network;


import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.AnimationMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class EntityAnimationClientUpdatePacket {
    private int entityId;
    private List<AnimationMessage> messages;

    public EntityAnimationClientUpdatePacket(Entity entity, List<AnimationMessage> messages){
        entityId = entity.getId();
        this.messages = messages;
    }

    public EntityAnimationClientUpdatePacket(FriendlyByteBuf buffer){
        entityId = buffer.readInt();
        int count = buffer.readInt();
        messages = new ArrayList<>();
        for (int i=0; i < count; i++){
            AnimationMessage message = NetworkDeserializers.animationMessageDeserializer.deserialize(buffer);
            if (message == null){
                BoneTown.LOGGER.error("Error decoding EntityAnimationClientUpdatePacket for Entity: {}",
                        entityId);
                break;
            }
            messages.add(message);
        }
    }

    public void toBytes(FriendlyByteBuf buffer){
        buffer.writeInt(entityId);
        buffer.writeInt(messages.size());
        for (AnimationMessage message : messages){
            message.toPacketBuffer(buffer);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier){
        NetworkEvent.Context ctx = supplier.get();
        ctx.enqueueWork(() -> {
            Level world = Minecraft.getInstance().level;
            if (world == null) {
                return;
            }
            Entity entity = world.getEntity(entityId);
            if (entity instanceof IBTAnimatedEntity){
                AnimationComponent<?> component = ((IBTAnimatedEntity<?>) entity).getAnimationComponent();
                for (AnimationMessage message : messages){
                    component.updateState(message);
                }
            }
        });
        ctx.setPacketHandled(true);
    }


}
