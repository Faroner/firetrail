package com.example.demonspell;

import com.example.demonspell.network.ModNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = DemonSpellMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DemonFormServerEvents {
    private static final String UNTIL = "demonspell_form_until";

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (!(event.player instanceof ServerPlayer player)) return;

        long until = player.getPersistentData().getLong(UNTIL);
        if (until > 0 && player.level().getGameTime() >= until) {
            player.getPersistentData().remove(UNTIL);
            player.removeEffect(com.example.demonspell.registry.ModEffects.DEMON_TRANSFORMATION.get());
            ModNetwork.setDemonForm(player, false);
        }
    }

    @SubscribeEvent
    public static void loggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ModNetwork.syncPlayerTo(player);
        }
    }

    @SubscribeEvent
    public static void logout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            DemonFormState.set(player.getUUID(), false);
        }
    }

    public static void setUntil(ServerPlayer player, long gameTime) {
        player.getPersistentData().putLong(UNTIL, gameTime);
    }

    private DemonFormServerEvents() {}
}
