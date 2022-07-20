package com.chaosbuffalo.bonetown.core;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.BTAdditionalAnimationEntry;
import com.chaosbuffalo.bonetown.core.model.BTArmorModelEntry;
import com.chaosbuffalo.bonetown.core.model.BTModel;
import com.chaosbuffalo.bonetown.core.materials.BTMaterialEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BoneTownRegistry {

    public static IForgeRegistry<BTModel> MODEL_REGISTRY = null;
    public static IForgeRegistry<BTMaterialEntry> MATERIAL_REGISTRY = null;
    public static IForgeRegistry<BTAdditionalAnimationEntry> ADDITIONAL_ANIMATION_REGISTRY = null;
    public static IForgeRegistry<BTArmorModelEntry> ARMOR_MODEL_REGISTRY = null;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public static void createRegistries(NewRegistryEvent event) {
        BoneTown.LOGGER.info("Registering Bone Town Registries");

        event.create(
                new RegistryBuilder<BTModel>()
                        .setName(new ResourceLocation(BoneTown.MODID, "models"))
                        .setType(BTModel.class)
                        .setIDRange(0, Integer.MAX_VALUE - 1)
                        .allowModification(),
                registry -> MODEL_REGISTRY = registry
        );

        event.create(
                new RegistryBuilder<BTMaterialEntry>()
                        .setName(new ResourceLocation(BoneTown.MODID, "materials"))
                        .setType(BTMaterialEntry.class)
                        .setIDRange(0, Integer.MAX_VALUE - 1)
                        .allowModification(),
                registry -> MATERIAL_REGISTRY = registry
        );

        event.create(
                new RegistryBuilder<BTAdditionalAnimationEntry>()
                        .setName(new ResourceLocation(BoneTown.MODID, "z_add_animations"))
                        .setType(BTAdditionalAnimationEntry.class)
                        .setIDRange(0, Integer.MAX_VALUE - 1)
                        .allowModification(),
                registry -> ADDITIONAL_ANIMATION_REGISTRY = registry
        );

        event.create(
                new RegistryBuilder<BTArmorModelEntry>()
                        .setName(new ResourceLocation(BoneTown.MODID, "z_armor_models"))
                        .setType(BTArmorModelEntry.class)
                        .setIDRange(0, Integer.MAX_VALUE - 1)
                        .allowModification(),
                registry -> ARMOR_MODEL_REGISTRY = registry
        );

    }
}
