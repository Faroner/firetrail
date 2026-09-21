package com.example.firetrail;

import com.example.firetrail.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FireTrailMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DarkFormServerEvents {
    private static final String UNTIL = "firetrail_dark_form_until";

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        long until = player.getPersistentData().getLong(UNTIL);
        if (until > 0 && player.level().getGameTime() >= until) {
            player.getPersistentData().remove(UNTIL);
            ModNetwork.setDarkForm(player, false);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DarkFormState.set(player.getUUID(), false);
        }
    }

    private DarkFormServerEvents() {}

    public static void setUntil(ServerPlayer player, long gameTime) {
        player.getPersistentData().putLong(UNTIL, gameTime);
    }
}
