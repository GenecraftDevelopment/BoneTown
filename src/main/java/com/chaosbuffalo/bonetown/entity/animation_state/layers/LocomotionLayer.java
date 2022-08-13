package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class LocomotionLayer<T extends LivingEntity & IBTAnimatedEntity<T>> extends BlendTwoPoseLayer<T> {

    public LocomotionLayer(String name, ResourceLocation idle, ResourceLocation run, T entity, boolean shouldLoop) {
        super(name, idle, run, entity, shouldLoop, 0.0f);
        computeDuration();
    }

    @Override
    protected float getBlendAmount() {
        return getEntity().getDeltaMovement().length() > 0.08 ? 1 : 0;
    }

    private void computeDuration(){
        BakedAnimation anim = getAnimation(SECOND_SLOT);
        if (anim != null){
            duration = anim.getTotalTicks();
        }
    }
}
