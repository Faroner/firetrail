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

public class FireTrailEntity extends Entity {
    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(FireTrailEntity.class, EntityDataSerializers.INT);

    public FireTrailEntity(EntityType<? extends FireTrailEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setLifeTicks(int ticks) {
        entityData.set(LIFE, ticks);
    }

    @Override
    protected void defineSynchedData() {
        entityData.define(LIFE, 80);
    }

    @Override
    public void tick() {
        super.tick();

        int life = entityData.get(LIFE);
        if (life <= 0) {
            discard();
            return;
        }

        entityData.set(LIFE, life - 1);

        setPos(getX() + getDeltaMovement().x,
                getY() + getDeltaMovement().y,
                getZ() + getDeltaMovement().z);

        if (level().isClientSide) {
            spawnParticles();
        }
    }

    private void spawnParticles() {
        for (int i = 0; i < 3; i++) {
            double ox = (random.nextDouble() - 0.5) * 0.35;
            double oy = (random.nextDouble() - 0.5) * 0.35;
            double oz = (random.nextDouble() - 0.5) * 0.35;

            level().addParticle(
                    ParticleTypes.FLAME,
                    getX() + ox,
                    getY() + oy,
                    getZ() + oz,
                    0.0, 0.015, 0.0
            );
        }

        level().addParticle(
                ParticleTypes.SMOKE,
                getX(), getY(), getZ(),
                0.0, 0.01, 0.0
        );
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setLifeTicks(tag.getInt("Life"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", entityData.get(LIFE));
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
