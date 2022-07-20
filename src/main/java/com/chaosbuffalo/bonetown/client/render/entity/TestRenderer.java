package com.chaosbuffalo.bonetown.client.render.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class TestRenderer extends BTEntityRenderer<TestEntity> {

    public static final ResourceLocation TEST_TEXTURE = new ResourceLocation(BoneTown.MODID,
            "bonetown/textures/test_cube.png");

    public TestRenderer(final EntityRendererProvider.Context ctx) {
        super(ctx, BTModels.TEST_CUBE);
        BoneTown.LOGGER.info("Creating test renderer");
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(TestEntity entity) {
        return TEST_TEXTURE;
    }
}
