package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;

public class TestEntity extends Entity {

    public TestEntity(final EntityType<? extends TestEntity> entityType, final Level world) {
        super(entityType, world);
        setBoundingBox(new AABB(-1D, -1D, -1.0D, 1.0D, 1.0D, 1.0D));
        //ignoreFrustumCheck = true;
    }

    public TestEntity(Level worldIn, double x, double y, double z){
        this(worldIn);
        setPos(x, y, z);
    }

    public TestEntity(final Level world) {
        this(BTEntityTypes.TEST_ENTITY(), world);
    }


    @Override
    protected void defineSynchedData() {

    }

    @Override
    protected void readAdditionalSaveData(CompoundTag p_20052_) {

    }

    @Override
    protected void addAdditionalSaveData(CompoundTag p_20139_) {

    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
