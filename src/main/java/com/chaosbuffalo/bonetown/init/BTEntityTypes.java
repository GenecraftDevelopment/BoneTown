package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BTEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES
            = DeferredRegister.create(ForgeRegistries.ENTITIES, BoneTown.MODID);

    public static final String TEST_NAME = "test";
    public static final String TEST_ZOMBIE_NAME = "test_zombie";

    public static final RegistryObject<EntityType<TestZombieEntity>> TEST_ZOMBIE = ENTITY_TYPES.register(
            TEST_ZOMBIE_NAME, () ->
                    EntityType.Builder.<TestZombieEntity>of(TestZombieEntity::new, MobCategory.MONSTER)
                            .sized(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                            .build(new ResourceLocation(BoneTown.MODID, TEST_ZOMBIE_NAME).toString())
    );

    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPES.register(
            TEST_NAME, () ->
                    EntityType.Builder.<TestEntity>of(TestEntity::new, MobCategory.MISC)
                            .sized(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                            .build(new ResourceLocation(BoneTown.MODID, TEST_NAME).toString())
    );

    public static void registerAttributes(EntityAttributeCreationEvent event) {
        event.put(TEST_ZOMBIE.get(), Zombie.createAttributes().build());
    }
}
