package com.example.demonspell.network;

import com.example.demonspell.DemonFormState;
import com.example.demonspell.DemonSpellMod;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(DemonSpellMod.MODID, "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, DemonFormPacket.class,
                DemonFormPacket::encode,
                DemonFormPacket::decode,
                DemonFormPacket::handle,
                java.util.Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    public static void setDemonForm(ServerPlayer player, boolean active) {
        DemonFormState.set(player.getUUID(), active);
        CHANNEL.send(PacketDistributor.ALL.noArg(), new DemonFormPacket(player.getUUID(), active));
    }

    public static void syncPlayerTo(ServerPlayer joiningPlayer) {
        for (ServerPlayer player : joiningPlayer.server.getPlayerList().getPlayers()) {
            boolean active = DemonFormState.isActive(player.getUUID());
            CHANNEL.send(PacketDistributor.PLAYER.with(() -> joiningPlayer),
                    new DemonFormPacket(player.getUUID(), active));
        }
    }

    private ModNetwork() {}
}
