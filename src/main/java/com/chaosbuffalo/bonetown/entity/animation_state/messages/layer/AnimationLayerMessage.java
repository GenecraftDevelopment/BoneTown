package com.chaosbuffalo.bonetown.entity.animation_state.messages.layer;


import net.minecraft.network.FriendlyByteBuf;

public abstract class AnimationLayerMessage {
    private String messageType;

    public String getMessageType() {
        return messageType;
    }

    public AnimationLayerMessage(String messageType){
        this.messageType = messageType;
    }

    public void toPacketBuffer(FriendlyByteBuf buffer) {
        buffer.writeUtf(messageType);
    }


}
