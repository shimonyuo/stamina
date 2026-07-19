package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HealingPacket {
    public HealingPacket() {
    }

    public static void encode(HealingPacket packet, FriendlyByteBuf buf) {
    }

    public static HealingPacket decode(FriendlyByteBuf buf) {
        return new HealingPacket();
    }

    public static void handle(HealingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            HealingEventHandler.triggerDisplay();
        });
        ctx.get().setPacketHandled(true);
    }
}