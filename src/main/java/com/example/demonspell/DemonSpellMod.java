package com.example.demonspell;

import com.example.demonspell.network.ModNetwork;
import com.example.demonspell.registry.ModSpells;
import com.example.demonspell.registry.ModEffects;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(DemonSpellMod.MODID)
public final class DemonSpellMod {
    public static final String MODID = "demonspell";

    public DemonSpellMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSpells.SPELLS.register(bus);
        ModEffects.EFFECTS.register(bus);
        ModNetwork.register();
    }
}
