package com.chaosbuffalo.bonetown.client.render.entity;


import com.chaosbuffalo.bonetown.client.render.RenderDataManager;
import com.chaosbuffalo.bonetown.client.render.render_data.IBTRenderDataContainer;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.core.materials.MaterialResourceManager;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.HashMap;

@OnlyIn(Dist.CLIENT)
public abstract class BTEntityRenderer<T extends Entity> extends EntityRenderer<T> {

    private BTModel model;
    protected IBTRenderDataContainer modelRenderData;
    HashMap<T, Boolean> activeEntities;


    protected BTEntityRenderer(EntityRendererProvider.Context ctx, BTModel model) {
        super(ctx);
        this.model = model;
        this.activeEntities = new HashMap<>();
        setupModelRenderData(model);

    }

    protected void setupModelRenderData(BTModel model){
        this.modelRenderData = RenderDataManager.MANAGER.getRenderDataForModel(model, true);
    }


    @Override
    public boolean shouldRender(@NotNull T p_114491_, @NotNull Frustum p_114492_, double p_114493_, double p_114494_, double p_114495_) {
        return super.shouldRender(p_114491_, p_114492_, p_114493_, p_114494_, p_114495_);
    }

    public BTModel getModel() {
        return model;
    }

    @Nullable
    protected RenderType getRenderType(T entityType, boolean isVisible, boolean visibleToPlayer) {
        ResourceLocation resourcelocation = this.getTextureLocation(entityType);
        if (visibleToPlayer) {
            return RenderType.entityTranslucent(resourcelocation);
        } else if (isVisible) {
            return RenderType.entityCutoutNoCull(resourcelocation);
        } else {
            return entityType.isCurrentlyGlowing() ? RenderType.outline(resourcelocation) : null;
        }
    }

    protected boolean isVisible(T livingEntityIn) {
        return !livingEntityIn.isInvisible();
    }

    
    public void drawModel(RenderType renderType, T entityIn, float entityYaw, float partialTicks,
                          PoseStack matrixStackIn, Matrix4f projectionMatrix, int packedLightIn,
                          int packedOverlay, IBTMaterial program, MultiBufferSource buffer){

        program.initRender(renderType, matrixStackIn, projectionMatrix, packedLightIn, packedOverlay);
        modelRenderData.render();
        program.endRender(renderType);
    }

    private void initializeRender(IBTMaterial program){
        modelRenderData.upload();
    }


    @Override
    @ParametersAreNonnullByDefault
    public void render(T entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn,
                       MultiBufferSource bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        boolean visible = this.isVisible(entityIn);
        boolean visibleToPlayer = !visible
                && !entityIn.isInvisibleTo(Minecraft.getInstance().player);
        final var rendertype = this.getRenderType(entityIn, visible, visibleToPlayer);
        // buffer
        {
            bufferIn.getBuffer(rendertype);
        }
        final var program = MaterialResourceManager.INSTANCE.getShaderProgram(model.getProgramName());
        if (!modelRenderData.isInitialized()){
           initializeRender(program);
        }
        final var gameRenderer = Minecraft.getInstance().gameRenderer;
        final var projMatrix = gameRenderer
                .getProjectionMatrix(90); // TODO - Calculate FOV

        int packedOverlay;
        if (entityIn instanceof LivingEntity)
            packedOverlay = LivingEntityRenderer.getOverlayCoords((LivingEntity) entityIn, partialTicks);
         else
            packedOverlay = OverlayTexture.NO_OVERLAY;

        drawModel(rendertype, entityIn,
                entityYaw, partialTicks, matrixStackIn,
                projMatrix, packedLightIn,
                packedOverlay, program, bufferIn);
    }




}
