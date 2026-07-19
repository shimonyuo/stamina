package com.survivalstaminamod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealingEventHandler {

    private static boolean shouldDisplay = false;
    private static float displayTimer = 0.0f;
    private static int lastResetTick = -100;

    // ─────────────────────────────
    // サーバー側の処理
    // ─────────────────────────────

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        // サーバー側のみ処理
        if (event.getEntity().level().isClientSide) return;

        // プレイヤーのみ
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        // クライアントにパケット送信
        NetworkHandler.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new HealingPacket()
        );
    }

    // ─────────────────────────────
    // クライアント側の処理
    // ─────────────────────────────

    // パケット受信時に呼ばれる
    public static void triggerDisplay() {
        shouldDisplay = true;
        displayTimer = 0.0f;
    }

    public static void tick(float partialTick, int currentTick) {
        // ワールド再入場検知
        if (currentTick < 10 || currentTick < lastResetTick) {
            shouldDisplay = false;
            displayTimer = 0.0f;
            lastResetTick = currentTick;
            return;
        }

        // 初期化から10ティック以内は処理しない
        if (currentTick - lastResetTick < 10) {
            return;
        }

        // タイマー進行
        if (shouldDisplay) {
            displayTimer += partialTick / 20.0f;
        }
    }

    public static boolean shouldDisplay() {
        return shouldDisplay;
    }

    public static float getDisplayTimer() {
        return displayTimer;
    }

    public static void stopDisplay() {
        shouldDisplay = false;
        displayTimer = 0.0f;
    }
}