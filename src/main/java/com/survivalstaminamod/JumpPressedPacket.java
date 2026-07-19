package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class JumpPressedPacket {

    public JumpPressedPacket() {}

    public static void encode(JumpPressedPacket msg, FriendlyByteBuf buf) {
    }

    public static JumpPressedPacket decode(FriendlyByteBuf buf) {
        return new JumpPressedPacket();
    }

    public static void handle(JumpPressedPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                StaminaSystem.onJumpKeyPressed(player);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}