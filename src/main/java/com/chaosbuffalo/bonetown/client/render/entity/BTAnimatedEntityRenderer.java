package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.client.render.BTClientMathUtils;
import com.chaosbuffalo.bonetown.client.render.RenderDataManager;
import com.chaosbuffalo.bonetown.client.render.layers.IBTAnimatedLayerRenderer;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import org.joml.Matrix4d;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.List;


public abstract class BTAnimatedEntityRenderer<T extends Entity & IBTAnimatedEntity<T>> extends BTEntityRenderer<T> {
    private BTAnimatedModel animatedModel;
    private final List<IBTAnimatedLayerRenderer<T>> renderLayers;

    protected BTAnimatedEntityRenderer(EntityRendererProvider.Context ctx, BTAnimatedModel model, float shadowSize) {
        super(ctx, model);
        this.renderLayers = new ArrayList<>();
        this.animatedModel = model;
        this.shadowRadius = shadowSize;
    }

    @Override
    protected void setupModelRenderData(BTModel model) {
        this.modelRenderData = RenderDataManager.MANAGER.getAnimatedRenderDataForModel(
                (BTAnimatedModel) model, true);
    }

    public BTAnimatedModel getAnimatedModel() {
        return animatedModel;
    }

    public List<IBTAnimatedLayerRenderer<T>> getRenderLayers() {
        return renderLayers;
    }

    public void addRenderLayer(IBTAnimatedLayerRenderer<T> layer) {
        renderLayers.add(layer);
    }

    public void handleEntityOrientation(PoseStack matrixStackIn, T entity, float partialTicks) {

    }


    public void moveMatrixStackToBone(T entityIn, String boneName, PoseStack matrixStack, IPose pose) {
        BoneMFSkeleton skeleton = entityIn.getSkeleton();
        if (skeleton != null) {
            int boneId = skeleton.getBoneId(boneName);
            if (boneId != -1) {
                Matrix4d boneMat = pose.getJointMatrix(boneId);
                BTClientMathUtils.applyTransformToStack(boneMat, matrixStack);
            }
        }
    }

    protected float getTimeAlive(T entity, float partialTicks) {
        return entity.tickCount + partialTicks;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void drawModel(RenderType renderType, T entityIn, float entityYaw,
                          float partialTicks, PoseStack matrixStackIn,
                          Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTMaterial program, MultiBufferSource buffer) {
        matrixStackIn.pushPose();
        handleEntityOrientation(matrixStackIn, entityIn, partialTicks);

        final IPose framePose;
        // render model
        {
            program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
            framePose = entityIn.getAnimationComponent()
                    .getCurrentPose(partialTicks);

            BoneMFSkeleton skeleton = entityIn.getSkeleton();
            if (skeleton != null) {
                // we need to upload this every render because if multiple models are sharing one shader
                // the uniforms will be invalid for bind pose, we could consider caching last model rendered
                // with a particular program and skip it
                program.uploadInverseBindPose(skeleton.getInverseBindPose());
            }
            program.uploadAnimationFrame(framePose);

            modelRenderData.render();
            program.endRender(renderType);
        }


        // render layers
        for (IBTAnimatedLayerRenderer<T> layer : getRenderLayers()) {
            layer.render(matrixStackIn, buffer, packedLightIn, entityIn, framePose, partialTicks,
                    getTimeAlive(entityIn, partialTicks), program, projectionMatrix);
        }

        matrixStackIn.popPose();
    }
}
