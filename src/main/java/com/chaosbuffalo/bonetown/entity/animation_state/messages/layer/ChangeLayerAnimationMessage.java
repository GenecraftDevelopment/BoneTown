package com.chaosbuffalo.bonetown.entity.animation_state.messages.layer;

import com.chaosbuffalo.bonetown.entity.animation_state.layers.LayerWithAnimation;
import com.chaosbuffalo.bonetown.network.NetworkDeserializers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public class ChangeLayerAnimationMessage extends AnimationLayerMessage {
    public static String CHANGE_ANIMATION_TYPE = "CHANGE_ANIMATION_TYPE";
    private final String slot;
    private final ResourceLocation anim;

    static {
        NetworkDeserializers.layerMessageDeserializer.addNetworkDeserializer(CHANGE_ANIMATION_TYPE,
                ChangeLayerAnimationMessage::fromPacketBuffer);
    }

    @Override
    public void toPacketBuffer(FriendlyByteBuf buffer){
        super.toPacketBuffer(buffer);
        buffer.writeUtf(getSlot());
        buffer.writeResourceLocation(getAnim());
    }

    private static ChangeLayerAnimationMessage fromPacketBuffer(FriendlyByteBuf buffer){
        String slot = buffer.readUtf();
        ResourceLocation animName = buffer.readResourceLocation();
        return new ChangeLayerAnimationMessage(animName, slot);
    }

    public ChangeLayerAnimationMessage(ResourceLocation newAnim) {
        this(newAnim, LayerWithAnimation.BASE_SLOT);
    }

    public ChangeLayerAnimationMessage(ResourceLocation newAnim, String slot) {
        super(CHANGE_ANIMATION_TYPE);
        this.anim = newAnim;
        this.slot = slot;
    }

    public ResourceLocation getAnim() {
        return anim;
    }

    public String getSlot() {
        return slot;
    }
}
