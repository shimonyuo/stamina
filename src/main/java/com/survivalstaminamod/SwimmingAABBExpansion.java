package com.survivalstaminamod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SwimmingAABBExpansion {

    private static final Map<UUID, Integer> swimmingStartTick = new HashMap<>();

    public static final Map<UUID, Float> aabbExpansionProgress = new HashMap<>();

    private static final float AABB_EXPANSION_MAX = 0.4f;
    private static final int ANIMATION_TICKS = 20;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        UUID uuid = player.getUUID();
        Pose currentPose = player.getPose();

        float newProgress = 0.0f;
        boolean shouldSend = false;

        if (currentPose == Pose.SWIMMING) {
            if (!swimmingStartTick.containsKey(uuid)) {
                swimmingStartTick.put(uuid, player.tickCount);
                newProgress = 0.0f;
                shouldSend = true;
            } else {
                int elapsedTicks = player.tickCount - swimmingStartTick.get(uuid);
                newProgress = Math.min((float) elapsedTicks / ANIMATION_TICKS, 1.0f);
                shouldSend = true;
            }
        } else {
            if (swimmingStartTick.containsKey(uuid)) {
                swimmingStartTick.remove(uuid);
                newProgress = 0.0f;
                shouldSend = true;
            }
        }

        if (shouldSend) {
            aabbExpansionProgress.put(uuid, newProgress);

            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.CHANNEL.send(
                        PacketDistributor.ALL.noArg(),
                        new SwimmingAABBExpansionPacket(uuid, newProgress)
                );
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        UUID uuid = event.getEntity().getUUID();
        swimmingStartTick.remove(uuid);
        aabbExpansionProgress.remove(uuid);
    }

    public static void updateAABBExpansionProgress(UUID playerUUID, float progress) {
        aabbExpansionProgress.put(playerUUID, progress);
    }

    public static float getCurrentAABBExpansion(Player player) {
        if (player.getPose() != Pose.SWIMMING) {
            return 0.0f;
        }

        UUID uuid = player.getUUID();
        float progress = aabbExpansionProgress.getOrDefault(uuid, 0.0f);

        return progress * AABB_EXPANSION_MAX;
    }
}