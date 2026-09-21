package com.example.firetrail.spell;

import com.example.firetrail.network.ModNetwork;
import com.example.firetrail.DarkFormServerEvents;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class DarkSpawnSpell extends AbstractSpell {
    private static final ResourceLocation SPELL_ID =
            new ResourceLocation("firetrail", "dark_spawn");

    private final DefaultConfig defaultConfig = new DefaultConfig()
            .setMinRarity(SpellRarity.COMMON)
            .setSchoolResource(SchoolRegistry.ENDER_RESOURCE)
            .setMaxLevel(5)
            .setCooldownSeconds(20)
            .build();

    public DarkSpawnSpell() {
        baseSpellPower = 1;
        spellPowerPerLevel = 0;
        castTime = 0;
        baseManaCost = 15;
        manaCostPerLevel = 3;
    }

    @Override
    public CastType getCastType() { return CastType.INSTANT; }

    @Override
    public DefaultConfig getDefaultConfig() { return defaultConfig; }

    @Override
    public ResourceLocation getSpellResource() { return SPELL_ID; }

    @Override
    public Optional<SoundEvent> getCastFinishSound() {
        return Optional.of(SoundEvents.ENDERMAN_STARE);
    }

    @Override
    public void onCast(Level level, int spellLevel, LivingEntity entity,
                       CastSource castSource, MagicData playerMagicData) {
        if (!level.isClientSide && entity instanceof ServerPlayer player) {
            ModNetwork.setDarkForm(player, true);
            DarkFormServerEvents.setUntil(player, level.getGameTime() + 600L);
            level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.PLAYERS, 0.8F, 0.55F);
        }
        super.onCast(level, spellLevel, entity, castSource, playerMagicData);
    }
}
