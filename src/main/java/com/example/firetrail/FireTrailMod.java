package com.example.firetrail;

import com.example.firetrail.registry.ModEntities;
import com.example.firetrail.registry.ModSpells;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FireTrailMod.MODID)
public final class FireTrailMod {
    public static final String MODID = "firetrail";

    public FireTrailMod() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSpells.SPELLS.register(bus);
        ModEntities.ENTITIES.register(bus);
    }
}
