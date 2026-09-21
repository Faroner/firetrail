package com.example.firetrail.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.Optional;
import java.util.UUID;

public class HighFiveCloneEntity extends Entity {
    private static final EntityDataAccessor<Optional<UUID>> OWNER =
            SynchedEntityData.defineId(HighFiveCloneEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    private static final EntityDataAccessor<Integer> AGE =
            SynchedEntityData.defineId(HighFiveCloneEntity.class, EntityDataSerializers.INT);

    public HighFiveCloneEntity(EntityType<? extends HighFiveCloneEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setOwner(UUID uuid) {
        entityData.set(OWNER, Optional.ofNullable(uuid));
    }

    public UUID getOwnerUUID() {
        return entityData.get(OWNER).orElse(null);
    }

    public Entity getOwnerEntity() {
        UUID uuid = getOwnerUUID();
        return uuid == null ? null : level().getPlayerByUUID(uuid);
    }

    public int getAge() {
        return entityData.get(AGE);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(OWNER, Optional.empty());
        entityData.define(AGE, 0);
    }

    @Override
    public void tick() {
        super.tick();
        int age = entityData.get(AGE) + 1;
        entityData.set(AGE, age);

        Entity owner = getOwnerEntity();
        if (owner != null) {
            // At the contact moment the real player swings their main hand too.
            // The clone itself stops moving for the contact so neither model jitters.
            if (age == 10 && !level().isClientSide && owner instanceof net.minecraft.world.entity.LivingEntity living) {
                living.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
            }

            // Smooth three-part choreography:
            // 1) approach from 1.65 -> 1.0 blocks,
            // 2) hold still for the high-five,
            // 3) gently drift back before disappearing.
            var look = owner.getLookAngle().normalize();
            double distance;
            if (age <= 8) {
                float t = smoothStep(age / 8.0F);
                distance = 1.65D + (1.0D - 1.65D) * t;
            } else if (age <= 13) {
                // Exact contact pose: freeze the clone at one block from the player.
                distance = 1.0D;
            } else {
                float t = smoothStep(Math.min(1.0F, (age - 13) / 6.0F));
                distance = 1.0D + 0.55D * t;
            }

            setPos(owner.getX() + look.x * distance,
                    owner.getY(),
                    owner.getZ() + look.z * distance);
            // Face the player with the owner's current yaw; the contact section is
            // position-stable so the hand-to-hand pose stays visually locked.
            setYRot(owner.getYRot() + 180.0F);
            setXRot(0.0F);
        }

        if (level().isClientSide) {
            spawnAnimationParticles(age);
        }

        // Give the visual exchange a clean finish before the cast completes.
        if (age >= 19) {
            discard();
        }
    }

    private static float smoothStep(float t) {
        t = Math.max(0.0F, Math.min(1.0F, t));
        return t * t * (3.0F - 2.0F * t);
    }

    private void spawnAnimationParticles(int age) {
        // The hands meet around age 10-12.
        if (age == 10 || age == 11) {
            for (int i = 0; i < 22; i++) {
                double a = random.nextDouble() * Math.PI * 2.0;
                double r = random.nextDouble() * 0.45D;
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + Math.cos(a) * r,
                        getY() + 1.15D + random.nextDouble() * 0.55D,
                        getZ() + Math.sin(a) * r,
                        Math.cos(a) * 0.045D, 0.07D, Math.sin(a) * 0.045D);
            }
            level().addParticle(ParticleTypes.FLASH, getX(), getY() + 1.25D, getZ(), 0, 0, 0);
        }
        if (age >= 12 && age <= 17 && random.nextFloat() < 0.5F) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX() + (random.nextDouble() - 0.5D) * 0.7D,
                    getY() + 0.7D + random.nextDouble() * 1.2D,
                    getZ() + (random.nextDouble() - 0.5D) * 0.7D,
                    0, 0.025D, 0);
        }
    }

    /**
     * 0..1 animation progress for the clone's high-five swing.
     * The hand moves toward the player, reaches the contact point,
     * then returns.
     */
    public float getHighFiveAnimation(float partialTick) {
        float t = getAge() + partialTick;
        // A soft reach: arm rises first, reaches the contact pose,
        // then eases back instead of snapping.
        if (t < 5.0F || t > 17.0F) return 0.0F;
        if (t <= 10.5F) {
            return smoothStep((t - 5.0F) / 5.5F);
        }
        if (t <= 12.5F) {
            return 1.0F;
        }
        return 1.0F - smoothStep((t - 12.5F) / 4.5F);
    }

    public float getHighFiveReach(float partialTick) {
        float t = getAge() + partialTick;
        if (t < 5.0F || t > 13.0F) return 0.0F;
        return smoothStep(Math.min(1.0F, (t - 5.0F) / 5.5F));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        entityData.set(AGE, tag.getInt("Age"));
        if (tag.hasUUID("Owner")) setOwner(tag.getUUID("Owner"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Age", getAge());
        if (getOwnerUUID() != null) tag.putUUID("Owner", getOwnerUUID());
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
