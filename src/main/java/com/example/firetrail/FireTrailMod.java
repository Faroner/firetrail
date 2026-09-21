package com.example.firetrail;

import com.example.firetrail.registry.ModEntities;
import com.example.firetrail.registry.ModSpells;
import com.example.firetrail.network.ModNetwork;
import net.minecraftforge.eventbus.api.IEventBus;
import software.bernie.geckolib.GeckoLib;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FireTrailMod.MODID)
public final class FireTrailMod {
    public static final String MODID = "firetrail";

    public FireTrailMod() {
        GeckoLib.initialize();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModSpells.SPELLS.register(bus);
        ModNetwork.register();
        ModEntities.ENTITIES.register(bus);
    }
}
