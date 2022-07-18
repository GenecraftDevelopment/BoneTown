package com.chaosbuffalo.bonetown.core.materials;


import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.mojang.blaze3d.shaders.Program;

public class AnimatedMaterial extends BTMaterial {

    public MaterialUniform jointsUniform;
    public MaterialUniform inverseBindPoseUniform;

    public AnimatedMaterial(int program, Program vert, Program frag) {
        super(program, vert, frag);
        jointsUniform = new MaterialUniform("joints_matrix",
                MaterialUniform.UniformType.vecmat4x4, AnimationFrame.MAX_JOINTS, this);
        uniforms.add(jointsUniform);
        inverseBindPoseUniform = new MaterialUniform("inverse_bind_pose",
                MaterialUniform.UniformType.vecmat4x4, AnimationFrame.MAX_JOINTS, this);
        uniforms.add(inverseBindPoseUniform);
    }

    @Override
    public void uploadInverseBindPose(IPose pose){
        inverseBindPoseUniform.set(pose.getJointCount(), pose.getJointMatrices());
        inverseBindPoseUniform.upload();
    }

    @Override
    public void uploadAnimationFrame(IPose pose) {
        jointsUniform.set(pose.getJointCount(), pose.getJointMatrices());
        jointsUniform.upload();
    }
}
