package com.chaosbuffalo.bonetown.entity.animation_state.messages.layer;

import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.FriendlyByteBuf;

public class ChangeBlendWeightMessage extends AnimationLayerMessage {
    public static String CHANGE_BLEND_WEIGHT_TYPE = "CHANGE_BLEND_WEIGHT_TYPE";
    private final float blendWeight;

    static {
        NetworkDeserializers.layerMessageDeserializer.addNetworkDeserializer(CHANGE_BLEND_WEIGHT_TYPE,
                ChangeBlendWeightMessage::fromPacketBuffer);
    }

    @Override
    public void toPacketBuffer(FriendlyByteBuf buffer){
        super.toPacketBuffer(buffer);
        buffer.writeFloat(blendWeight);
    }

    public ChangeBlendWeightMessage(float blendWeight){
        super(CHANGE_BLEND_WEIGHT_TYPE);
        this.blendWeight = blendWeight;
    }

    public float getBlendWeight() {
        return blendWeight;
    }

    private static ChangeBlendWeightMessage fromPacketBuffer(FriendlyByteBuf buffer){
        float blendWeight = buffer.readFloat();
        return new ChangeBlendWeightMessage(blendWeight);
    }
}
