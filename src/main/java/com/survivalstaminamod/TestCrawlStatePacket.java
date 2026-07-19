package com.survivalstaminamod;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

public class TestCrawlStatePacket {
    private final UUID playerUUID;
    private final boolean isCrawling;

    public TestCrawlStatePacket(UUID playerUUID, boolean isCrawling) {
        this.playerUUID = playerUUID;
        this.isCrawling = isCrawling;
    }

    public static void encode(TestCrawlStatePacket msg, FriendlyByteBuf buf) {
        buf.writeUUID(msg.playerUUID);
        buf.writeBoolean(msg.isCrawling);
    }

    public static TestCrawlStatePacket decode(FriendlyByteBuf buf) {
        return new TestCrawlStatePacket(buf.readUUID(), buf.readBoolean());
    }

    public static void handle(TestCrawlStatePacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            Player player = Minecraft.getInstance().level.getPlayerByUUID(msg.playerUUID);
            if (player != null) {
                if (msg.isCrawling) {
                    player.setForcedPose(Pose.SWIMMING);
                } else {
                    player.setForcedPose(null);
                }
                player.refreshDimensions();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}