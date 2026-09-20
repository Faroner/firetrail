package com.example.firetrail.spell;

import com.example.firetrail.entity.FireTrailEntity;
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
import net.minecraft.world.level.Level;

import java.util.Optional;

public class FireTrailSpell extends AbstractSpell {
    private static final ResourceLocation SPELL_ID =
            new ResourceLocation("firetrail", "fire_trail");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.FIRE_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(4)
            .build();

    public FireTrailSpell() {
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 8;
        this.manaCostPerLevel = 1;
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
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BLAZE_SHOOT);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity,
                       CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            FireTrailEntity trail =
                    new FireTrailEntity(ModEntities.FIRE_TRAIL.get(), level);

            var look = entity.getLookAngle().normalize();
            var spawn = entity.getEyePosition().add(look.scale(1.25));

            trail.moveTo(spawn.x, spawn.y, spawn.z);
            trail.setDeltaMovement(look.scale(0.38D));
            trail.setLifeTicks(80);

            level.addFreshEntity(trail);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
