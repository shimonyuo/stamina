package com.survivalstaminamod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientJumpHandler {

    // 地面に着地した瞬間に1回だけリセットするためのフラグ
    private static boolean justLanded = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        boolean isJumpPressedNow = mc.options.keyJump.isDown();
        boolean isOnGroundNow = mc.player.onGround();
        boolean isInWater = mc.player.isInWater();

        // ==================== 地面着地時の1回リセット ====================
        if (isOnGroundNow && !justLanded) {
            // 地面に着地した瞬間だけ1回リセット
            justLanded = true;
            // ここで「ジャンプ検知の記憶」をリセットしたい場合は必要に応じてフラグをクリア
        }
        // ============================================================

        // ==================== ジャンプ検知本体 ====================
        // 条件：ジャンプキーが押されている + 地上にいる + 水中ではない
        if (isJumpPressedNow && isOnGroundNow && !isInWater) {

            // ここでサーバーにジャンプパケットを送信
            NetworkHandler.CHANNEL.sendToServer(new JumpPressedPacket());

            // 連続ジャンプを可能にするため、キー長押し中も毎ティック送信したい場合は
            // wasJumpPressed のような記憶はここでは使わない
        }
        // ============================================================
    }

    // 必要に応じて外部からリセットしたい場合用
    public static void resetJumpDetection() {
        justLanded = false;
    }
}