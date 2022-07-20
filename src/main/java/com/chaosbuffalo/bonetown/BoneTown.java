package com.chaosbuffalo.bonetown;


import com.chaosbuffalo.bonetown.core.BoneTownRegistry;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.core.proxy.ClientProxy;
import com.chaosbuffalo.bonetown.core.proxy.IBTProxy;
import com.chaosbuffalo.bonetown.core.proxy.ServerProxy;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import com.chaosbuffalo.bonetown.init.BTRenderers;
import com.chaosbuffalo.bonetown.network.PacketHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod("bonetown")
public class BoneTown
{
    public static final Logger LOGGER = LogManager.getLogger();
    public static final String MODID = "bonetown";
    public static IBTProxy proxy;

    public BoneTown() {
        proxy = DistExecutor.runForDist(() -> ClientProxy::new, () -> ServerProxy::new);
        proxy.registerHandlers();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::entitySetup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::rendererSetup);
        BTEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.setupHandler();
        BoneTownRegistry.MODEL_REGISTRY.getEntries().forEach((x) -> x.getValue().load());
        BoneTownRegistry.ADDITIONAL_ANIMATION_REGISTRY.getEntries().forEach((x) -> x.getValue().load());
        BoneTownRegistry.MODEL_REGISTRY.getEntries().forEach((x) -> x.getValue().getModel().getSkeleton()
                .ifPresent(BoneMFSkeleton::bakeAnimations));
        BoneTownRegistry.ARMOR_MODEL_REGISTRY.getEntries().forEach((x) -> x.getValue().load());
        BoneTownRegistry.MODEL_REGISTRY.getEntries().forEach((x) -> {
            if (x.getValue() instanceof BTAnimatedModel){
                ((BTAnimatedModel) x.getValue()).bakeArmors();
            }
        });
    }

    private void rendererSetup(final EntityRenderersEvent.RegisterRenderers event) {
        BTRenderers.registerRenderers(event);
    }

    private void entitySetup(final EntityAttributeCreationEvent event) {
        BTEntityTypes.registerAttributes(event);
    }
}
