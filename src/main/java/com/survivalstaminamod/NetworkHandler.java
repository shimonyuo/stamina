package com.survivalstaminamod;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class NetworkHandler {
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation("survivalstaminamod", "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        CHANNEL.registerMessage(
                packetId++,
                HealingPacket.class,
                HealingPacket::encode,
                HealingPacket::decode,
                HealingPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                InputDirectionPacket.class,
                InputDirectionPacket::encode,
                InputDirectionPacket::decode,
                InputDirectionPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                JumpPressedPacket.class,
                JumpPressedPacket::encode,
                JumpPressedPacket::decode,
                JumpPressedPacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                StaminaUpdatePacket.class,
                StaminaUpdatePacket::encode,
                StaminaUpdatePacket::decode,
                StaminaUpdatePacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                TestCrawlPacket.class,
                TestCrawlPacket::encode,
                TestCrawlPacket::decode,
                TestCrawlPacket::handle
        );

        CHANNEL.registerMessage(
                packetId++,
                TestCrawlStatePacket.class,
                TestCrawlStatePacket::encode,
                TestCrawlStatePacket::decode,
                TestCrawlStatePacket::handle
        );
        CHANNEL.registerMessage(
                packetId++,
                SwimmingAABBExpansionPacket.class,
                SwimmingAABBExpansionPacket::encode,
                SwimmingAABBExpansionPacket::decode,
                SwimmingAABBExpansionPacket::handle
        );
    }
}