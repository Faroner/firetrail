package com.example.firetrail.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FireTrailEntity extends Entity {
    private static final EntityDataAccessor<Integer> LIFE =
            SynchedEntityData.defineId(FireTrailEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(FireTrailEntity.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> RADIUS =
            SynchedEntityData.defineId(FireTrailEntity.class, EntityDataSerializers.FLOAT);

    private UUID ownerUUID;
    private final Map<UUID, Integer> hitCooldowns = new HashMap<>();

    public FireTrailEntity(EntityType<? extends FireTrailEntity> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setLifeTicks(int ticks) { entityData.set(LIFE, ticks); }
    public void setOwner(LivingEntity owner) { this.ownerUUID = owner.getUUID(); }
    public void setDamage(float damage) { entityData.set(DAMAGE, damage); }
    public void setRadius(float radius) { entityData.set(RADIUS, radius); }

    @Override
    protected void defineSynchedData() {
        entityData.define(LIFE, 100);
        entityData.define(DAMAGE, 5.0F);
        entityData.define(RADIUS, 2.0F);
    }

    @Override
    public void tick() {
        super.tick();

        int life = entityData.get(LIFE);
        if (life <= 0) {
            if (!level().isClientSide) {
                createFinalExplosion();
            }
            discard();
            return;
        }
        entityData.set(LIFE, life - 1);

        setPos(getX() + getDeltaMovement().x,
                getY() + getDeltaMovement().y,
                getZ() + getDeltaMovement().z);

        if (!level().isClientSide) {
            damageNearbyEntities();
        } else {
            spawnParticles();
        }

        hitCooldowns.replaceAll((uuid, ticks) -> ticks - 1);
        hitCooldowns.entrySet().removeIf(entry -> entry.getValue() <= 0);
    }

    private void createFinalExplosion() {
        // Spectacular magical impact without destroying blocks.
        level().playSound(null, blockPosition(),
                net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.PLAYERS, 1.2F, 0.9F);

        for (int i = 0; i < 36; i++) {
            double angle = (Math.PI * 2.0 * i) / 36.0;
            double radius = 1.0 + random.nextDouble() * 2.5;
            double x = getX() + Math.cos(angle) * radius;
            double z = getZ() + Math.sin(angle) * radius;
            double y = getY() + 0.2 + random.nextDouble() * 1.8;

            level().addParticle(ParticleTypes.FLAME, x, y, z,
                    Math.cos(angle) * 0.08, 0.08 + random.nextDouble() * 0.08,
                    Math.sin(angle) * 0.08);
            level().addParticle(ParticleTypes.LAVA, x, y, z,
                    0.0, 0.04, 0.0);
        }

        for (int i = 0; i < 18; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double radius = random.nextDouble() * 2.0;
            level().addParticle(ParticleTypes.SMOKE,
                    getX() + Math.cos(angle) * radius,
                    getY() + 0.4 + random.nextDouble() * 1.5,
                    getZ() + Math.sin(angle) * radius,
                    0.0, 0.06, 0.0);
        }

        level().addParticle(ParticleTypes.EXPLOSION_EMITTER, getX(), getY() + 0.4, getZ(), 0, 0, 0);

        // Final burst damages nearby mobs once, but never the caster.
        double radius = entityData.get(RADIUS) + 1.5;
        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class, getBoundingBox().inflate(radius), this::canHit)) {
            Entity owner = ownerUUID == null ? null : level().getPlayerByUUID(ownerUUID);
            DamageSource source = owner == null
                    ? level().damageSources().magic()
                    : level().damageSources().indirectMagic(this, owner);
            if (target.hurt(source, entityData.get(DAMAGE) * 1.5F)) {
                target.setSecondsOnFire(4);
            }
        }
    }

    private void damageNearbyEntities() {
        double radius = entityData.get(RADIUS);
        for (LivingEntity target : level().getEntitiesOfClass(
                LivingEntity.class, getBoundingBox().inflate(radius), this::canHit)) {

            UUID uuid = target.getUUID();
            if (hitCooldowns.containsKey(uuid)) continue;

            Entity owner = ownerUUID == null ? null : level().getPlayerByUUID(ownerUUID);
            DamageSource source = owner == null
                    ? level().damageSources().magic()
                    : level().damageSources().indirectMagic(this, owner);

            if (target.hurt(source, entityData.get(DAMAGE))) {
                // Keeps the wave powerful without hitting the same mob every tick.
                hitCooldowns.put(uuid, 10);
                target.setSecondsOnFire(3);
            }
        }
    }

    private boolean canHit(LivingEntity target) {
        return target.isAlive() && !target.getUUID().equals(ownerUUID);
    }

    private void spawnParticles() {
        double radius = entityData.get(RADIUS);

        // Large fiery core.
        for (int i = 0; i < 10; i++) {
            double ox = (random.nextDouble() - 0.5) * radius * 2.0;
            double oz = (random.nextDouble() - 0.5) * radius * 2.0;
            double oy = random.nextDouble() * 0.8;
            level().addParticle(ParticleTypes.FLAME, getX() + ox, getY() + oy, getZ() + oz,
                    0.0, 0.03 + random.nextDouble() * 0.04, 0.0);
        }

        for (int i = 0; i < 4; i++) {
            double ox = (random.nextDouble() - 0.5) * radius * 1.6;
            double oz = (random.nextDouble() - 0.5) * radius * 1.6;
            level().addParticle(ParticleTypes.LAVA, getX() + ox, getY() + 0.15, getZ() + oz,
                    0.0, 0.02, 0.0);
        }

        level().addParticle(ParticleTypes.SMOKE, getX(), getY() + 0.4, getZ(),
                0.0, 0.05, 0.0);

        // Leaves a visible fiery ribbon behind the moving wave.
        if (random.nextFloat() < 0.8F) {
            level().addParticle(ParticleTypes.FLAME,
                    getX() - getDeltaMovement().x * 2.0,
                    getY() + 0.1,
                    getZ() - getDeltaMovement().z * 2.0,
                    0.0, 0.02, 0.0);
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        setLifeTicks(tag.getInt("Life"));
        setDamage(tag.getFloat("Damage"));
        setRadius(tag.getFloat("Radius"));
        if (tag.hasUUID("Owner")) ownerUUID = tag.getUUID("Owner");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Life", entityData.get(LIFE));
        tag.putFloat("Damage", entityData.get(DAMAGE));
        tag.putFloat("Radius", entityData.get(RADIUS));
        if (ownerUUID != null) tag.putUUID("Owner", ownerUUID);
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
