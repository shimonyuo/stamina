package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
import java.util.UUID;

public class StaminaUpdatePacket {
    private final UUID playerUUID;
    private final float staminaValue;

    public StaminaUpdatePacket(UUID playerUUID, float staminaValue) {
        this.playerUUID = playerUUID;
        this.staminaValue = staminaValue;
    }

    public static void encode(StaminaUpdatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeFloat(msg.staminaValue);
    }

    public static StaminaUpdatePacket decode(FriendlyByteBuf buf) {
        UUID uuid = buf.readUUID();
        float stamina = buf.readFloat();
        return new StaminaUpdatePacket(uuid, stamina);
    }

    public static void handle(StaminaUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // ★★★ クライアント側のスタミナマップを更新 ★★★
            StaminaSystem.staminaMap.put(msg.playerUUID, msg.staminaValue);
        });
        ctx.get().setPacketHandled(true);
    }
}