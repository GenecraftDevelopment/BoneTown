package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;

public interface IBTAnimatedLayerRenderer<T extends Entity & IBTAnimatedEntity<T>> {

    void render(PoseStack matrixStack, MultiBufferSource renderBuffer, int packedLight, T entityIn,
                IPose pose, float partialTicks, float ageInTicks, IBTMaterial currentMaterial, Matrix4f projectionMatrix);
}
