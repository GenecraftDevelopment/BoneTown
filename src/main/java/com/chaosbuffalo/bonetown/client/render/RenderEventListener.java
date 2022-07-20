package com.chaosbuffalo.bonetown.client.render;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.FORGE)
public class RenderEventListener {


    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void cameraSetup(EntityViewRenderEvent.CameraSetup event){
        PoseStack matrixStack = new PoseStack();
        matrixStack.mulPose(Vector3f.ZP.rotationDegrees(event.getRoll()));
        matrixStack.mulPose(Vector3f.XP.rotationDegrees(event.getPitch()));
        matrixStack.mulPose(Vector3f.YP.rotationDegrees(event.getYaw() + 180.0F));
        GlobalRenderInfo.INFO.setCurrentFrameGlobal(matrixStack);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void renderLast(TickEvent.RenderTickEvent event){
        RenderDataManager.MANAGER.tick();
    }
}
