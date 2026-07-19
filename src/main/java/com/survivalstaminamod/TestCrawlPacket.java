package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TestCrawlPacket {
    private final boolean enable;

    public TestCrawlPacket(boolean enable) {
        this.enable = enable;
    }

    public static void encode(TestCrawlPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.enable);
    }

    public static TestCrawlPacket decode(FriendlyByteBuf buf) {
        return new TestCrawlPacket(buf.readBoolean());
    }

    public static void handle(TestCrawlPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                TestCrawlHandler.handleServerPacket(player, msg.enable);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}