package com.example.firetrail.network;

import com.example.firetrail.DarkFormState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.UUID;
import java.util.function.Supplier;

public final class ModNetwork {
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("firetrail", "main"),
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals);

    private static int id = 0;

    public static void register() {
        CHANNEL.registerMessage(id++, DarkFormPacket.class,
                DarkFormPacket::encode, DarkFormPacket::decode, DarkFormPacket::handle);
    }

    public static void setDarkForm(ServerPlayer player, boolean active) {
        DarkFormState.set(player.getUUID(), active);
        CHANNEL.send(PacketDistributor.ALL.noArg(), new DarkFormPacket(player.getUUID(), active));
    }

    private ModNetwork() {}
}
