package com.example.firetrail.network;

import com.example.firetrail.DarkFormState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record DarkFormPacket(UUID playerId, boolean active) {
    public static void encode(DarkFormPacket message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerId);
        buffer.writeBoolean(message.active);
    }

    public static DarkFormPacket decode(FriendlyByteBuf buffer) {
        return new DarkFormPacket(buffer.readUUID(), buffer.readBoolean());
    }

    public static void handle(DarkFormPacket message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DarkFormState.set(message.playerId, message.active));
        context.setPacketHandled(true);
    }
}
