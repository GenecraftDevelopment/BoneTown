package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.client.render.entity.BTAnimatedEntityRenderer;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class BTAnimatedLayerRenderer<T extends Entity & IBTAnimatedEntity<T>>
        implements IBTAnimatedLayerRenderer<T> {

    private final BTAnimatedEntityRenderer<T> entityRenderer;

    public BTAnimatedLayerRenderer(BTAnimatedEntityRenderer<T> entityRenderer){
        this.entityRenderer = entityRenderer;
    }

    public BTAnimatedEntityRenderer<T> getEntityRenderer() {
        return entityRenderer;
    }

    public abstract void render(PoseStack matrixStack, MultiBufferSource renderBuffer, int packedLight, T entityIn,
                                IPose pose, float partialTicks, float ageInTicks, IBTMaterial currentMaterial, Matrix4f projectionMatrix);
}
