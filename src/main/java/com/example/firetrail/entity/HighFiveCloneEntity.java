package com.example.firetrail.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.UUID;

public class HighFiveCloneEntity extends Entity implements GeoEntity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(HighFiveCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(HighFiveCloneEntity.class, EntityDataSerializers.INT);
    private static final RawAnimation HIGH_FIVE = RawAnimation.begin().thenPlay("animation.high_five_clone.high_five");

    private final AnimatableInstanceCache geoCache = GeckoLibUtil.createInstanceCache(this);

    public HighFiveCloneEntity(EntityType<? extends HighFiveCloneEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setOwner(UUID uuid) { entityData.set(OWNER, Optional.ofNullable(uuid)); }
    public UUID getOwnerUUID() { return entityData.get(OWNER).orElse(null); }
    public Entity getOwnerEntity() {
        UUID uuid = getOwnerUUID();
        return uuid == null ? null : level().getPlayerByUUID(uuid);
    }
    public int getAge() { return entityData.get(AGE); }
    public float getAnimationSeconds() { return getAge() / 20.0F; }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER, Optional.empty());
        entityData.define(AGE, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "high_five", 0, this::animationPredicate));
    }

    private <E extends HighFiveCloneEntity> PlayState animationPredicate(AnimationState<E> state) {
        float seconds = getAnimationSeconds();
        if (seconds < 1.35F) {
            return state.setAndContinue(HIGH_FIVE);
        }
        return PlayState.STOP;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return geoCache; }

    @Override
    public void tick() {
        super.tick();
        int age = entityData.get(AGE) + 1;
        entityData.set(AGE, age);

        Entity owner = getOwnerEntity();
        if (owner != null) {
            var look = owner.getLookAngle().normalize();
            double distance;
            if (age <= 9) {
                double t = smoothStep(age / 9.0D);
                distance = 1.65D + (1.0D - 1.65D) * t;
            } else if (age <= 17) {
                distance = 1.0D;
            } else {
                double t = smoothStep(Math.min(1.0D, (age - 17) / 8.0D));
                distance = 1.0D + 0.55D * t;
            }

            setPos(owner.getX() + look.x * distance, owner.getY(), owner.getZ() + look.z * distance);
            setYRot(owner.getYRot() + 180.0F);
            setXRot(0.0F);

            if (age == 12 && !level().isClientSide && owner instanceof LivingEntity living) {
                living.swing(InteractionHand.MAIN_HAND, true);
                level().playSound(null, blockPosition(), SoundEvents.PLAYER_ATTACK_STRONG,
                        net.minecraft.sounds.SoundSource.PLAYERS, 0.75F, 1.35F);
            }
        }

        if (level().isClientSide) spawnParticles(age);
        if (age == 25 && !level().isClientSide && owner instanceof Player player) {
            player.heal(10.0F);
            level().playSound(null, player.blockPosition(), SoundEvents.PLAYER_LEVELUP,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.85F, 1.15F);
        }

        if (age >= 26) discard();
    }

    private void spawnParticles(int age) {
        if (age == 12 || age == 13) {
            for (int i = 0; i < 18; i++) {
                double a = random.nextDouble() * Math.PI * 2;
                double r = random.nextDouble() * 0.35;
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + Math.cos(a) * r, getY() + 1.2 + random.nextDouble() * 0.35,
                        getZ() + Math.sin(a) * r, Math.cos(a) * 0.025, 0.04, Math.sin(a) * 0.025);
            }
            level().addParticle(ParticleTypes.FLASH, getX(), getY() + 1.35, getZ(), 0, 0, 0);
        }
        if (age >= 18 && random.nextFloat() < 0.7F) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX() + (random.nextDouble() - 0.5) * 0.5,
                    getY() + random.nextDouble() * 1.8,
                    getZ() + (random.nextDouble() - 0.5) * 0.5,
                    0, 0.03, 0);
        }
    }

    private static double smoothStep(double x) {
        x = Math.max(0, Math.min(1, x));
        return x * x * (3 - 2 * x);
    }

    @Override protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(AGE, tag.getInt("Age"));
        if (tag.hasUUID("Owner")) setOwner(tag.getUUID("Owner"));
    }
    @Override protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", getAge());
        if (getOwnerUUID() != null) tag.putUUID("Owner", getOwnerUUID());
    }
    @Override public Packet<ClientGamePacketListener> getAddEntityPacket() { return NetworkHooks.getEntitySpawningPacket(this); }
}
