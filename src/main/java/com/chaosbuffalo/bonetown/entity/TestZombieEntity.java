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
import net.minecraft.world.entity.Mob;
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

    public TestZombieEntity(final EntityType<? extends TestZombieEntity> type, final Level worldIn) {
        super(type, worldIn);
        animatedModel = (BTAnimatedModel) BTModels.BIPED;
        skeleton = animatedModel.getSkeleton().orElse(null);
        animationComponent = new AnimationComponent<>(this);
        setupAnimationComponent();
    }

    @Override
    public InteractionResult interactAt(Player p_19980_, Vec3 p_19981_, InteractionHand p_19982_) {
        this.setTarget(p_19980_);
        return super.interactAt(p_19980_, p_19981_, p_19982_);
    }

    protected void setupAnimationComponent() {
        if (this.skeleton == null) return;
        final var animations = this.skeleton
                .getAnimations()
                .keySet();

        final var idleAnimation = animations
                .stream()
                .filter(resourceLocation -> resourceLocation.getPath().contains("idle"))
                .findFirst();
        final var moveAnimation = animations
                .stream()
                .filter(resourceLocation -> resourceLocation.getPath().contains("move"))
                .findFirst();

        // layers
        {
            final var defaultState = new AnimationState<>("default", this);

            // locomotion layer
            if (idleAnimation.isPresent() && moveAnimation.isPresent()) {
                final var locomotionLayer = new LocomotionLayer<>("locomotion",
                        idleAnimation.get(),
                        moveAnimation.get(),
                        this, true
                );
                defaultState.addLayer(locomotionLayer);
            }

            // head layer
            {
                final var headTrackingLayer = new HeadTrackingLayer<>("head", this,
                        "mixamorig:Head");
                defaultState.addLayer(headTrackingLayer);
            }

            animationComponent.addAnimationState(defaultState);
        }

        // default state
        animationComponent.pushState("default");
    }

    public TestZombieEntity(Level worldIn, double x, double y, double z) {
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
        return "mixamorig:RightHand";
    }

    @Override
    public String getLeftHandBoneName() {
        return "mixamorig:LeftHand";
    }
}
