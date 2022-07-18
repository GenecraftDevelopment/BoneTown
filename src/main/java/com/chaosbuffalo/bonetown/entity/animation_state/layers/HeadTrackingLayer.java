package com.chaosbuffalo.bonetown.entity.animation_state.layers;

import com.chaosbuffalo.bonetown.core.animation.IPose;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFNode;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.entity.IBTAnimatedEntity;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.joml.Matrix4d;
import org.joml.Vector4d;

public class HeadTrackingLayer<T extends LivingEntity & IBTAnimatedEntity<T>> extends AnimationLayerBase<T> {

    private final String boneName;
    private float rotLimit;

    public HeadTrackingLayer(String name, T entity, String headBoneName){
        super(name, entity);
        boneName = headBoneName;
        rotLimit = 85.0f;
    }

    public float getRotLimit() {
        return rotLimit;
    }

    public void setRotLimit(float rotLimit) {
        this.rotLimit = rotLimit;
    }

    public String getBoneName(){
        return boneName;
    }


    @Override
    public void doLayerWork(IPose basePose, int currentTime, float partialTicks, IPose outPose) {
        BoneMFSkeleton skeleton = getEntity().getSkeleton();
        if (skeleton == null){
            return;
        }
        BoneMFNode bone = skeleton.getBone(getBoneName());
        T entity = getEntity();
        if (bone != null) {
            float bodyYaw = Mth.lerp(partialTicks, entity.yBodyRotO, entity.yBodyRot);
            float headYaw = Mth.lerp(partialTicks, entity.yHeadRotO, entity.yHeadRotO);
            boolean shouldSit = entity.isPassenger() && (entity.getVehicle() != null &&
                    entity.getVehicle().shouldRiderSit());
            float netHeadYaw = headYaw - bodyYaw;
            if (shouldSit && entity.getVehicle() instanceof LivingEntity vehicle) {
                bodyYaw = Mth.lerp(partialTicks, vehicle.yBodyRotO, vehicle.yBodyRot);
                netHeadYaw = headYaw - bodyYaw;
                float wrapped = Mth.wrapDegrees(netHeadYaw);
                if (wrapped < -getRotLimit()) {
                    wrapped = -getRotLimit();
                }

                if (wrapped >= getRotLimit()) {
                    wrapped = getRotLimit();
                }

                bodyYaw = headYaw - wrapped;
                if (wrapped * wrapped > 2500.0F) {
                    bodyYaw += wrapped * 0.2F;
                }
                netHeadYaw = headYaw - bodyYaw;
            }
            float headPitch = Mth.lerp(partialTicks, entity.xRotO, entity.getXRot());
            float rotateY = netHeadYaw * ((float) Math.PI / 180F);
            boolean isFlying = entity.getFallFlyingTicks() > 4;
            float rotateX;
            if (isFlying) {
                rotateX = (-(float) Math.PI / 4F);
            } else {
                rotateX = headPitch * ((float) Math.PI / 180F);
            }
            Vector4d headRotation = new Vector4d(rotateX, rotateY, 0.0, 1.0);
            Matrix4d headTransform = bone.calculateLocalTransform(bone.getTranslation(),
                    headRotation, bone.getScaling());
            int boneId = skeleton.getBoneId(getBoneName());
            int parentBoneId = skeleton.getBoneParentId(getBoneName());
            Matrix4d parentTransform;
            if (parentBoneId != -1) {
                parentTransform = new Matrix4d(outPose.getJointMatrix(parentBoneId));
            } else {
                parentTransform = new Matrix4d();
            }
            outPose.setJointMatrix(boneId, parentTransform.mulAffine(headTransform));
        }
    }
}
