package com.chaosbuffalo.bonetown.core.materials;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.mojang.blaze3d.shaders.Program;
import com.mojang.blaze3d.shaders.Shader;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.RenderType;

public interface IBTMaterial extends Shader {
    void initRender(RenderType renderType, PoseStack matrixStackIn, Matrix4f projection,
                    int packedLight, int packedOverlay);

    void endRender(RenderType renderType);

    void setupUniforms();

    void uploadAnimationFrame(IPose pose);

    void uploadInverseBindPose(IPose pose);
}
