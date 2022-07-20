package com.chaosbuffalo.bonetown.entity.animation_state.messages;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.FriendlyByteBuf;

public class PopStateMessage extends AnimationMessage {
    public static final String POP_STATE = "POP_STATE";

    static {
        NetworkDeserializers.animationMessageDeserializer.addNetworkDeserializer(POP_STATE,
                PopStateMessage::fromPacketBuffer);
        AnimationComponent.addMessageHandler(POP_STATE, PopStateMessage::handleMessage);
    }

    private static void handleMessage(AnimationComponent<?> component, AnimationMessage message){
        component.popState();
    }

    private static PopStateMessage fromPacketBuffer(FriendlyByteBuf buffer){
        return new PopStateMessage();
    }

    @Override
    public void toPacketBuffer(FriendlyByteBuf buffer) {
        super.toPacketBuffer(buffer);
    }

    public PopStateMessage() {
        super(POP_STATE);
    }

}
