package com.example.demonspell.registry;

import com.example.demonspell.DemonSpellMod;
import com.example.demonspell.spell.DemonTransformationSpell;
import io.redspace.ironsspellbooks.api.registry.SpellRegistry;
import io.redspace.ironsspellbooks.api.spells.AbstractSpell;
import net.minecraftforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModSpells {
    public static final DeferredRegister<AbstractSpell> SPELLS =
            DeferredRegister.create(SpellRegistry.SPELL_REGISTRY_KEY, DemonSpellMod.MODID);

    public static final Supplier<AbstractSpell> DEMON_TRANSFORMATION =
            SPELLS.register("demon_transformation", DemonTransformationSpell::new);

    private ModSpells() {}
}
