package com.example.demonspell.client;

import com.example.demonspell.DemonFormState;
import com.example.demonspell.DemonSpellMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DemonSpellMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DemonFormEffects {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !DemonFormState.isActive(player.getUUID())) return;

        if (player.tickCount % 2 == 0) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
            double radius = 0.35 + player.getRandom().nextDouble() * 0.4;
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 0.15 + player.getRandom().nextDouble() * 1.7;
            double z = player.getZ() + Math.sin(angle) * radius;
            player.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.01, 0);
        }
    }

    private DemonFormEffects() {}
}
