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
            // Stay in front of the caster while the little "high-five" animation plays.
            var look = owner.getLookAngle().normalize();
            double distance;
            if (age <= 7) {
                distance = 1.9D - (age / 7.0D) * 0.9D;
            } else if (age <= 11) {
                distance = 1.0D;
            } else {
                distance = 1.0D + Math.min(0.9D, (age - 11) / 9.0D * 0.9D);
            }

            setPos(owner.getX() + look.x * distance,
                    owner.getY(),
                    owner.getZ() + look.z * distance);
            setYRot(owner.getYRot() + 180.0F);
            setXRot(0.0F);
        }

        if (level().isClientSide) {
            spawnAnimationParticles(age);
        }

        if (age >= 20) {
            discard();
        }
    }

    private void spawnAnimationParticles(int age) {
        // Contact moment: a bright magical "high-five" flash.
        if (age == 11 || age == 12) {
            for (int i = 0; i < 18; i++) {
                double a = random.nextDouble() * Math.PI * 2.0;
                double r = random.nextDouble() * 0.55D;
                level().addParticle(ParticleTypes.END_ROD,
                        getX() + Math.cos(a) * r,
                        getY() + 1.15D + random.nextDouble() * 0.5D,
                        getZ() + Math.sin(a) * r,
                        Math.cos(a) * 0.04D, 0.06D, Math.sin(a) * 0.04D);
            }
            level().addParticle(ParticleTypes.FLASH, getX(), getY() + 1.2D, getZ(), 0, 0, 0);
        }
        if (age >= 13 && age <= 18 && random.nextFloat() < 0.45F) {
            level().addParticle(ParticleTypes.END_ROD,
                    getX() + (random.nextDouble() - 0.5D) * 0.6D,
                    getY() + 0.7D + random.nextDouble() * 1.2D,
                    getZ() + (random.nextDouble() - 0.5D) * 0.6D,
                    0, 0.02D, 0);
        }
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
