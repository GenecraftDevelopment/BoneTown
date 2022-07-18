package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.animation.InterpolationFramesReturn;
import com.chaosbuffalo.bonetown.core.animation.WeightedAnimationBlend;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;


public class FullBodyPoseLayer<T extends Entity & IBTAnimatedEntity<T>> extends LayerWithAnimation<T> {

    private boolean shouldLoop;
    private final WeightedAnimationBlend weightedBlend;

    public FullBodyPoseLayer(String name, ResourceLocation animName, T entity, boolean shouldLoop){
        super(name, animName, entity);
        this.shouldLoop = shouldLoop;
        this.weightedBlend = new WeightedAnimationBlend();
    }

    @Override
    public boolean shouldLoop() {
        return shouldLoop;
    }

    @Override
    void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BakedAnimation animation = getAnimation(BASE_SLOT);
        if (animation != null){
            InterpolationFramesReturn ret = animation.getInterpolationFrames(
                    currentTime - getStartTime(), shouldLoop(), partialTicks);
            weightedBlend.simpleBlend(ret.current, ret.next, ret.partialTick);
            outPose.copyPose(weightedBlend.getPose());
        }
    }
}
