package com.chaosbuffalo.bonetown.core.bonemf;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.animation.AnimationFrame;
import com.chaosbuffalo.bonetown.core.animation.BakedAnimation;
import com.chaosbuffalo.bonetown.core.animation.IPose;
import net.minecraft.util.ResourceLocation;
import org.joml.Matrix4d;
import javax.annotation.Nullable;
import java.util.*;


public class BoneMFSkeleton {
    private final List<BoneMFNode> boneArray;
    private final Map<String, BoneMFNode> boneIndex;
    private final Map<String, Integer> boneArrayIndex;
    private final Map<String, Integer> boneParentArrayIndex;
    private final BoneMFNode root;
    private final Map<ResourceLocation, BoneMFAnimation> animations;
    private final Map<ResourceLocation, BakedAnimation> bakedAnimations;
    private final Map<String, Matrix4d> boneInversions;
    private final Matrix4d[] inverseBindPose;
    private final AnimationFrame bindPose;

    public BoneMFSkeleton(BoneMFNode root){
        this.root = root;
        boneIndex = new HashMap<>();
        boneArrayIndex = new HashMap<>();
        boneParentArrayIndex = new HashMap<>();
        boneInversions = new HashMap<>();
        boneArray = root.getNodesOfType(BoneMFAttribute.AttributeTypes.SKELETON);
        animations = new HashMap<>();
        bakedAnimations = new HashMap<>();
        bindPose = new AnimationFrame();
        inverseBindPose = new Matrix4d[AnimationFrame.MAX_JOINTS];
        Arrays.fill(inverseBindPose, new Matrix4d());
        int count = 0;
        for (BoneMFNode node : boneArray){
            boneIndex.put(node.getName(), node);
            boneArrayIndex.put(node.getName(), count);
            if (node.getParent() != null){
                int parentId = getBoneId(node.getParent().getName());
                boneParentArrayIndex.put(node.getName(), parentId);
            } else {
                boneParentArrayIndex.put(node.getName(), -1);
            }
            Matrix4d bindPoseMat = new Matrix4d(node.calculateGlobalTransform());
            Matrix4d invertedBindPose = new Matrix4d(bindPoseMat).invertAffine();
            boneInversions.put(node.getName(), invertedBindPose);
            bindPose.setJointMatrix(count, bindPoseMat);
            inverseBindPose[count] = invertedBindPose;
            count++;

        }
    }

    public AnimationFrame getBindPose() {
        return bindPose;
    }

    public Matrix4d[] getInverseBindPose() {
        return inverseBindPose;
    }

    public int getBoneId(String name){
        return boneArrayIndex.getOrDefault(name, -1);
    }

    public int getBoneParentId(String name){
        return boneParentArrayIndex.get(name);
    }

    public BoneMFNode getBone(String name){
        return boneIndex.get(name);
    }

    public BoneMFNode getRoot() {
        return root;
    }

    public List<BoneMFNode> getBones(){
        return boneArray;
    }

    public void addAnimation(ResourceLocation animName, BoneMFAnimation animation){
        animations.put(animName, animation);
    }

    @Nullable
    public BakedAnimation bakeAnimation(ResourceLocation animName){

        BoneMFAnimation animation = getAnimation(animName);
        if (animation == null){
            return null;
        }
        List<IPose> frames = new ArrayList<>();
        for (long i = 0; i < animation.getFrameCount(); i++){
            frames.add(new AnimationFrame());
        }
        for (BoneMFNode bone : getBones()){
            int index = getBoneId(bone.getName());
            int parentIndex = getBoneParentId(bone.getName());
            BoneMFAnimationChannel channel = animation.getChannel(bone.getName());
            if (channel != null){
                int frameCount = 0;
                for (BoneMFNodeFrame nodeFrame : channel.getFrames()){
                    AnimationFrame frame = (AnimationFrame) frames.get(frameCount);
                    Matrix4d parentTransform;
                    Matrix4d frameTransform = bone.calculateLocalTransform(nodeFrame.getTranslation(),
                            nodeFrame.getRotation(), nodeFrame.getScale());
                    if (parentIndex != -1){
                        parentTransform = new Matrix4d(frame.getJointMatrix(parentIndex));
                    } else {
                        parentTransform = new Matrix4d();
                    }
                    frame.setJointMatrix(index, parentTransform.mulAffine(frameTransform));
                    frameCount++;
                }
            }
        }
        BoneTown.LOGGER.info("Baked animation: {}", animName.toString());
        return new BakedAnimation(animName, frames, animation.getFrameRate());
    }

    public void bakeAnimations(){
        Set<ResourceLocation> keys = getAnimations().keySet();
        for (ResourceLocation key : keys){
            BakedAnimation baked = bakeAnimation(key);
            if (baked != null){
                bakedAnimations.put(key, baked);
            }
        }
    }

    public Map<ResourceLocation, BoneMFAnimation> getAnimations() {
        return animations;
    }

    @Nullable
    public BoneMFAnimation getAnimation(ResourceLocation animName){
        return animations.get(animName);
    }

    @Nullable
    public BakedAnimation getBakedAnimation(ResourceLocation animName){
        return bakedAnimations.get(animName);
    }
}