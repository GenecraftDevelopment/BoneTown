package com.chaosbuffalo.bonetown.client.render.entity;


import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class TestZombieRenderer extends AnimatedBipedRenderer<TestZombieEntity> {

    public static final ResourceLocation ZOMBIE_TEXTURE = new ResourceLocation(
            "textures/entity/zombie/zombie.png");

    public TestZombieRenderer(final EntityRendererProvider.Context renderManager) {
        super(renderManager, (BTAnimatedModel) BTModels.BIPED, 1.0f);
    }


    @Override
    public @NotNull ResourceLocation getTextureLocation(TestZombieEntity entity) {
        return ZOMBIE_TEXTURE;
    }
}

