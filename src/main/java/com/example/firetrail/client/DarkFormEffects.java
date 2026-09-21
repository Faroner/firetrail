package com.example.firetrail.client;

import com.example.firetrail.DarkFormState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import com.example.firetrail.FireTrailMod;

@Mod.EventBusSubscriber(modid = FireTrailMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class DarkFormEffects {
    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !DarkFormState.isActive(player.getUUID())) return;

        if (player.tickCount % 2 == 0) {
            double angle = player.getRandom().nextDouble() * Math.PI * 2.0;
            double radius = 0.35 + player.getRandom().nextDouble() * 0.35;
            double x = player.getX() + Math.cos(angle) * radius;
            double y = player.getY() + 0.2 + player.getRandom().nextDouble() * 1.55;
            double z = player.getZ() + Math.sin(angle) * radius;
            player.level().addParticle(ParticleTypes.SMOKE, x, y, z, 0, 0.012, 0);
        }
    }

    private DarkFormEffects() {}
}
