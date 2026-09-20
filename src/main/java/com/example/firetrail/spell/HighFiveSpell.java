package com.example.firetrail.spell;

import com.example.firetrail.entity.HighFiveCloneEntity;
import com.example.firetrail.registry.ModEntities;
import io.redspace.ironsspellbooks.api.config.DefaultConfig;
import io.redspace.ironsspellbooks.api.magic.MagicData;
import io.redspace.ironsspellbooks.api.registry.SchoolRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import io.redspace.ironsspellbooks.api.spells.CastSource;
import io.redspace.ironsspellbooks.api.spells.CastType;
import io.redspace.ironsspellbooks.api.spells.SpellRarity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class HighFiveSpell extends AbstractSpell {
    private static final ResourceLocation SPELL_ID =
            new ResourceLocation("firetrail", "high_five");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.HOLY_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(8)
            .build();

    public HighFiveSpell() {
        baseSpellPower = 1;
        spellPowerPerLevel = 0;
        castTime = 20; // 1 second
        baseManaCost = 10;
        manaCostPerLevel = 2;
    }

    @Override
    public CastType getCastType() {
        return CastType.INSTANT;
    }

    @Override
    public DefaultConfig getDefaultConfig() {
        return defaultConfig;
    }

    @Override
    public ResourceLocation getSpellResource() {
        return SPELL_ID;
    }

    @Override
    public Optional<SoundEvent> getCastStartSound() {
        return Optional.of(SoundEvents.PLAYER_ATTACK_STRONG);
    }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.PLAYER_LEVELUP);
    }

    @Override
    public void onServerPreCast(Level level, int spellLevel, LivingEntity entity, MagicData playerMagicData) {
        if (!level.isClientSide) {
            HighFiveCloneEntity clone = new HighFiveCloneEntity(ModEntities.HIGH_FIVE_CLONE.get(), level);
            clone.setOwner(entity.getUUID());
            var look = entity.getLookAngle().normalize();
            clone.moveTo(entity.getX() + look.x * 1.9D, entity.getY(),
                    entity.getZ() + look.z * 1.9D);
            level.addFreshEntity(clone);
        }
        super.onServerPreCast(level, spellLevel, entity, playerMagicData);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity,
                       CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof Player player) {
            // The high-five lands after the one-second cast.
            player.heal(10.0F);
            level.playSound(null, player.blockPosition(),
                    SoundEvents.PLAYER_LEVELUP, net.minecraft.sounds.SoundSource.PLAYERS,
                    0.9F, 1.15F);
            for (int i = 0; i < 18; i++) {
                double a = level.random.nextDouble() * Math.PI * 2.0;
                double r = level.random.nextDouble() * 0.7D;
                level.addParticle(net.minecraft.core.particles.ParticleTypes.HEART,
                        player.getX() + Math.cos(a) * r,
                        player.getY() + 1.0D + level.random.nextDouble() * 1.0D,
                        player.getZ() + Math.sin(a) * r,
                        0, 0.04D, 0);
            }
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
