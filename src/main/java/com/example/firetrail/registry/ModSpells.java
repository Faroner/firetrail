package com.example.firetrail.registry;

import com.example.firetrail.FireTrailMod;
import com.example.firetrail.spell.FireTrailSpell;
import com.example.firetrail.spell.HighFiveSpell;
import com.example.firetrail.spell.DarkSpawnSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, FireTrailMod.MODID);

    public static final Supplier<AbstractSpell> FIRE_TRAIL =
            SPELLS.register("fire_trail", FireTrailSpell::new);

    public static final Supplier<AbstractSpell> HIGH_FIVE =
            SPELLS.register("high_five", HighFiveSpell::new);

    public static final Supplier<AbstractSpell> DARK_SPAWN =
            SPELLS.register("dark_spawn", DarkSpawnSpell::new);

    private ModSpells() {}
}
