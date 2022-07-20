package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import net.minecraft.network.FriendlyByteBuf;

public abstract class AnimationMessage {

    public void toPacketBuffer(FriendlyByteBuf buffer){
        buffer.writeUtf(getType());
    }

    private String type;

    public String getType() {
        return type;
    }

    public AnimationMessage(String type){
        this.type = type;
    }
}
