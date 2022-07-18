package com.chaosbuffalo.bonetown.init;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.entity.TestEntity;
import com.chaosbuffalo.bonetown.entity.TestZombieEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class BTEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES
            = new DeferredRegister<>(ForgeRegistries.ENTITIES, BoneTown.MODID);

    public static final String TEST_NAME = "test";
    public static final String TEST_ZOMBIE_NAME = "test_zombie";

    public static final RegistryObject<EntityType<TestZombieEntity>> TEST_ZOMBIE = ENTITY_TYPES.register(
            TEST_ZOMBIE_NAME, () ->
            EntityType.Builder.<TestZombieEntity>create(TestZombieEntity::new, EntityClassification.MONSTER)
                    .size(EntityType.ZOMBIE.getWidth(), EntityType.ZOMBIE.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_ZOMBIE_NAME).toString())
    );

    public static final RegistryObject<EntityType<TestEntity>> TEST_ENTITY = ENTITY_TYPES.register(
            TEST_NAME, () ->
            EntityType.Builder.<TestEntity>create(TestEntity::new, EntityClassification.MISC)
                    .size(EntityType.PIG.getWidth(), EntityType.PIG.getHeight())
                    .build(new ResourceLocation(BoneTown.MODID, TEST_NAME).toString())
    );

}
