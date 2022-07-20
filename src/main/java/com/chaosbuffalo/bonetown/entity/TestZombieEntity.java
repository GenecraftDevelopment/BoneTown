package com.chaosbuffalo.bonetown.entity;

import com.chaosbuffalo.bonetown.BoneTown;
import com.chaosbuffalo.bonetown.core.bonemf.BoneMFSkeleton;
import com.chaosbuffalo.bonetown.core.model.BTAnimatedModel;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationComponent;
import com.chaosbuffalo.bonetown.entity.animation_state.AnimationState;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.FullBodyPoseLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.HeadTrackingLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.LocomotionLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.layers.SubTreePoseLayer;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.PopStateMessage;
import com.chaosbuffalo.bonetown.entity.animation_state.messages.PushStateMessage;
import com.chaosbuffalo.bonetown.init.BTEntityTypes;
import com.chaosbuffalo.bonetown.init.BTModels;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public class TestZombieEntity extends Zombie
        implements IBTAnimatedEntity<TestZombieEntity>, IHasHandBones {

    private AnimationComponent<TestZombieEntity> animationComponent;
    BTAnimatedModel animatedModel;
    BoneMFSkeleton skeleton;
    private static final ResourceLocation IDLE_ANIM = new ResourceLocation(
            BoneTown.MODID, "biped.idle");
    private static final ResourceLocation RUN_ANIM = new ResourceLocation(
            BoneTown.MODID, "biped.running");
    private static final ResourceLocation ZOMBIE_ARMS_ANIM = new ResourceLocation(
            BoneTown.MODID, "biped.zombie_arms");
    private static final ResourceLocation BACKFLIP_ANIM = new ResourceLocation(
            BoneTown.MODID, "biped.backflip");

    public TestZombieEntity(final EntityType<? extends TestZombieEntity> type, final Level worldIn) {
        super(type, worldIn);
        animatedModel = (BTAnimatedModel) BTModels.BIPED;
        skeleton = animatedModel.getSkeleton().orElse(null);
        animationComponent = new AnimationComponent<>(this);
        setupAnimationComponent();
    }


    @Override
    public InteractionResult interactAt(Player player, Vec3 vec, InteractionHand hand) {
        if (getLevel().isClientSide && hand.equals(player.getUsedItemHand())){
            animationComponent.updateState(new PushStateMessage("flip"));
            setNoAi(true);
        }
        return super.interactAt(player, vec, hand);
    }

    protected void setupAnimationComponent() {
        AnimationState<TestZombieEntity> defaultState = new AnimationState<>("default", this);
        HeadTrackingLayer<TestZombieEntity> headTrackingLayer = new HeadTrackingLayer<>("head", this,
                "bn_head");
        LocomotionLayer<TestZombieEntity> locomotionLayer = new LocomotionLayer<>("locomotion",
                IDLE_ANIM, RUN_ANIM,
                this, true);
        SubTreePoseLayer<TestZombieEntity> armsLayer = new SubTreePoseLayer<>("arms",
                ZOMBIE_ARMS_ANIM, this, true, "bn_chest");
        defaultState.addLayer(locomotionLayer);
        defaultState.addLayer(armsLayer);
        defaultState.addLayer(headTrackingLayer);
        animationComponent.addAnimationState(defaultState);
        animationComponent.pushState("default");
        AnimationState<TestZombieEntity> flipState = new AnimationState<>("flip", this);
        FullBodyPoseLayer<TestZombieEntity> flipLayer = new FullBodyPoseLayer<>("flip", BACKFLIP_ANIM,
                this, false);
        flipState.addLayer(flipLayer);
        flipLayer.setEndCallback(() -> {
            if (level.isClientSide){
                animationComponent.updateState(new PopStateMessage());
                setNoAi(false);
            }
        });
        animationComponent.addAnimationState(flipState);
    }

    public TestZombieEntity(Level worldIn, double x, double y, double z){
        this(worldIn);
        setPos(x, y, z);
    }

    @Override
    public void tick() {
        super.tick();
        animationComponent.update();
    }

    public TestZombieEntity(final Level world) {
        this(BTEntityTypes.TEST_ZOMBIE.get(), world);
    }

    @Override
    public AnimationComponent<TestZombieEntity> getAnimationComponent() {
        return animationComponent;
    }

    @Nullable
    @Override
    public BoneMFSkeleton getSkeleton() {
        return skeleton;
    }

    @Override
    public String getRightHandBoneName() {
        return "bn_hand_r";
    }

    @Override
    public String getLeftHandBoneName() {
        return "bn_hand_l";
    }
}
