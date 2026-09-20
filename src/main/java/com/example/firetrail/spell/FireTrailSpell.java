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
            .setCooldownSeconds(5)
            .build();

    public FireTrailSpell() {
        this.baseSpellPower = 1;
        this.spellPowerPerLevel = 0;
        this.castTime = 0;
        this.baseManaCost = 12;
        this.manaCostPerLevel = 2;
    }

    @Override
    public CastType getCastType() { return CastType.INSTANT; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public ResourceLocation getSpellResource() { return SPELL_ID; }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.BLAZE_SHOOT);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity,
                       CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide) {
            FireTrailEntity wave = new FireTrailEntity(ModEntities.FIRE_TRAIL.get(), level);

            var look = entity.getLookAngle().normalize();
            var spawn = entity.getEyePosition().add(look.scale(1.5));

            wave.moveTo(spawn.x, spawn.y, spawn.z);
            // Fast enough to feel like a wave, but still visible.
            wave.setDeltaMovement(look.scale(0.65D));
            // 0.65 blocks/tick × 46 ticks ≈ 30 blocks maximum range.
            wave.setLifeTicks(46);
            wave.setOwner(entity);

            // Each level expands both the impact and the damage.
            wave.setDamage(5.0F + (spellLevel - 1) * 2.5F);
            wave.setRadius(2.0F + (spellLevel - 1) * 0.45F);

            level.addFreshEntity(wave);
        }

        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
