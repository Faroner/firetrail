package com.example.demonspell.registry;

import com.example.demonspell.DemonSpellMod;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, DemonSpellMod.MODID);

    public static final RegistryObject<MobEffect> DEMON_TRANSFORMATION =
            EFFECTS.register("demon_transformation", () -> new MobEffect(net.minecraft.world.effect.MobEffectCategory.BENEFICIAL, 0x7A22C7) {});

    private ModEffects() {}
}
