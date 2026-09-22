package com.example.demonspell.network;

import com.example.demonspell.DemonFormState;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public record DemonFormPacket(UUID playerId, boolean active) {
    public static void encode(DemonFormPacket message, FriendlyByteBuf buffer) {
        buffer.writeUUID(message.playerId);
        buffer.writeBoolean(message.active);
    }

    public static DemonFormPacket decode(FriendlyByteBuf buffer) {
        return new DemonFormPacket(buffer.readUUID(), buffer.readBoolean());
    }

    public static void handle(DemonFormPacket message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DemonFormState.set(message.playerId, message.active));
        context.setPacketHandled(true);
    }
}
