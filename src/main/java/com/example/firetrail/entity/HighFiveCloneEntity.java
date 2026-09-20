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
            // At the contact moment the real player swings their main hand too,
            // so the animation looks like a genuine two-sided high-five.
            if (age == 10 && !level().isClientSide && owner instanceof net.minecraft.world.entity.LivingEntity living) {
                living.swing(net.minecraft.world.InteractionHand.MAIN_HAND, true);
            }

            // The clone stays exactly one block in front of the caster.
            var look = owner.getLookAngle().normalize();
            setPos(owner.getX() + look.x * 1.0D,
                    owner.getY(),
                    owner.getZ() + look.z * 1.0D);
            setYRot(owner.getYRot() + 180.0F);
            setXRot(0.0F);
        }

        if (level().isClientSide) {
            spawnAnimationParticles(age);
        }

        // 1 second cast: the clone is present for the whole cast and
        // disappears immediately after the high-five moment.
        if (age >= 19) {
            discard();
        }
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
        if (t < 6.0F || t > 16.0F) return 0.0F;
        float x = (t - 6.0F) / 10.0F;
        return (float) Math.sin(x * Math.PI);
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
