package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.client.render.entity.TestRenderer;
import com.chaosbuffalo.bonetown.client.render.entity.TestZombieRenderer;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraftforge.client.event.EntityRenderersEvent;

public class BTRenderers {

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event){
        event.registerEntityRenderer(BTEntityTypes.TEST_ENTITY.get(), TestRenderer::new);
        event.registerEntityRenderer(BTEntityTypes.TEST_ZOMBIE.get(), TestZombieRenderer::new);
    }
}
