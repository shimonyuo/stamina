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

    // サーバー側：SWIMMING 姿勢に入った時のティック時刻を記録
    private static final Map<UUID, Integer> swimmingStartTick = new HashMap<>();

    // クライアント側＋サーバー側：現在の AABB 拡張進捗（0.0 ～ 1.0）
    public static final Map<UUID, Float> aabbExpansionProgress = new HashMap<>();

    private static final float AABB_EXPANSION_MAX = 0.4f;
    private static final int ANIMATION_TICKS = 20;  // 20ティックで +0.4 に到達（1秒間）

    // ==================== サーバー側処理 ====================
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        UUID uuid = player.getUUID();
        Pose currentPose = player.getPose();

        float newProgress = 0.0f;
        boolean shouldSend = false;

        // ==================== SWIMMING 姿勢の開始を検知 ====================
        if (currentPose == Pose.SWIMMING) {
            if (!swimmingStartTick.containsKey(uuid)) {
                // SWIMMING 姿勢に入った瞬間
                swimmingStartTick.put(uuid, player.tickCount);
                newProgress = 0.0f;
                shouldSend = true;
            } else {
                // すでに SWIMMING 中 → アニメーション進捗を更新
                int elapsedTicks = player.tickCount - swimmingStartTick.get(uuid);
                newProgress = Math.min((float) elapsedTicks / ANIMATION_TICKS, 1.0f);
                shouldSend = true;  // 毎ティック送信して常に最新値を同期
            }
        } else {
            // SWIMMING 姿勢から離脱 → 状態をクリア
            if (swimmingStartTick.containsKey(uuid)) {
                swimmingStartTick.remove(uuid);
                newProgress = 0.0f;
                shouldSend = true;
            }
        }

        // サーバー側でも progress を保持
        if (shouldSend) {
            aabbExpansionProgress.put(uuid, newProgress);

            // 全クライアントに同期
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

    // ==================== クライアント側処理 ====================
    /**
     * パケットから受け取った expansion progress を更新
     * Mixin から毎フレーム参照される
     */
    public static void updateAABBExpansionProgress(UUID playerUUID, float progress) {
        aabbExpansionProgress.put(playerUUID, progress);
    }

    /**
     * 現在の AABB 拡張幅を取得
     * Mixin から毎フレーム呼ばれる
     *
     * @param player プレイヤー
     * @return 拡張幅（0.0 ～ 0.4）
     */
    public static float getCurrentAABBExpansion(Player player) {
        if (player.getPose() != Pose.SWIMMING) {
            return 0.0f;  // SWIMMING 以外は拡張しない
        }

        UUID uuid = player.getUUID();
        float progress = aabbExpansionProgress.getOrDefault(uuid, 0.0f);

        // リニアなアニメーション
        return progress * AABB_EXPANSION_MAX;
    }
}