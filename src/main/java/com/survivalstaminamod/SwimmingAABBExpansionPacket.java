package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class SwimmingAABBExpansionPacket {
    private final UUID playerUUID;
    private final float expansionProgress;

    public SwimmingAABBExpansionPacket(UUID playerUUID, float expansionProgress) {
        this.playerUUID = playerUUID;
        this.expansionProgress = expansionProgress;
    }

    public static void encode(SwimmingAABBExpansionPacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeFloat(msg.expansionProgress);
    }

    public static SwimmingAABBExpansionPacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        float expansionProgress = buf.readFloat();
        return new SwimmingAABBExpansionPacket(uuid, expansionProgress);
    }

    public static void handle(SwimmingAABBExpansionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // クライアント側で expansion progress を更新
            SwimmingAABBExpansion.updateAABBExpansionProgress(msg.playerUUID, msg.expansionProgress);
        });
        ctx.get().setPacketHandled(true);
    }
}