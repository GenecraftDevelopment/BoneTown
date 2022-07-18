package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.client.render.layers.AnimatedArmorLayer;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public abstract class AnimatedLivingEntityRenderer<T extends LivingEntity & IBTAnimatedEntity<T>> extends
        BTAnimatedEntityRenderer<T> {

    protected AnimatedLivingEntityRenderer(EntityRendererProvider.Context renderManager,
                                           BTAnimatedModel model, float shadowSize) {
        super(renderManager, model, shadowSize);
        addRenderLayer(new AnimatedArmorLayer<>(this, model));
    }

    @Override
    public void handleEntityOrientation(PoseStack matrixStackIn, T entity, float partialTicks) {
        float renderYaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(-renderYaw));
    }
}
