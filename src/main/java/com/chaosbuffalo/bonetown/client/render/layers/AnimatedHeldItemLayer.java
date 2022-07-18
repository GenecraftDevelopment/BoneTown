package com.chaosbuffalo.bonetown.client.render.layers;

import com.chaosbuffalo.bonetown.client.render.entity.BTAnimatedEntityRenderer;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.materials.IBTMaterial;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import com.chaosbuffalo.bonetown.entity.IHasHandBones;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnimatedHeldItemLayer<T extends LivingEntity & IBTAnimatedEntity<T> & IHasHandBones>
        extends BTAnimatedLayerRenderer<T> {


    public AnimatedHeldItemLayer(BTAnimatedEntityRenderer<T> entityRenderer) {
        super(entityRenderer);
    }


    @Override
    public void render(PoseStack matrixStack, MultiBufferSource renderBuffer, int packedLight, T entityIn,
                       IPose pose, float partialTicks, float ageInTicks, IBTMaterial currentMaterial,
                       Matrix4f projectionMatrix) {

        ItemStack rightHandItem = entityIn.getMainHandItem();
        ItemStack leftHandItem =  entityIn.getOffhandItem();

        if (!leftHandItem.isEmpty() || !rightHandItem.isEmpty()) {
            renderItemInHand(entityIn, pose, rightHandItem, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND,
                    true, matrixStack, renderBuffer, packedLight);
            renderItemInHand(entityIn, pose, leftHandItem, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    false, matrixStack, renderBuffer, packedLight);
        }
    }

    private void renderItemInHand(T entity, IPose pose, ItemStack itemStack,
                                  ItemTransforms.TransformType cameraTransform,
                                  boolean mainHand, PoseStack matrixStack,
                                  MultiBufferSource bufferIn, int packedLight) {
        if (!itemStack.isEmpty()) {
            matrixStack.pushPose();
            final var renderer = getEntityRenderer();

            String boneName = mainHand ? entity.getLeftHandBoneName() : entity.getRightHandBoneName();
            renderer.moveMatrixStackToBone(entity, boneName, matrixStack, pose);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0f));
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));

            Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemStack, cameraTransform,
                    mainHand, matrixStack, bufferIn, packedLight);
            matrixStack.popPose();
        }

    }
}
