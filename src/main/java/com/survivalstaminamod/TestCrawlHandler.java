package com.survivalstaminamod;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber(modid = SurvivalStaminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TestCrawlHandler {

    private static boolean isCrawlToggled = false;
    private static boolean wasCPressedLastTick = false;

    // ==================== クライアント側 ====================
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        boolean cPressedNow = KeyBindingRegistry.TEST_CRAWL_KEY.isDown();

        if (mc.player.isPassenger()) {
            if (isCrawlToggled) {
                isCrawlToggled = false;

                // 本人側：1回だけ強制解除
                mc.player.setForcedPose(null);
                mc.player.setPose(Pose.STANDING);
                mc.player.refreshDimensions();

                NetworkHandler.CHANNEL.sendToServer(new TestCrawlPacket(false));
                System.out.println("[TestCrawl Client] 搭乗検知 → 1回だけ強制OFF");
            }
            wasCPressedLastTick = cPressedNow;
            return;
        }

        // 通常トグル処理
        if (cPressedNow && !wasCPressedLastTick) {
            isCrawlToggled = !isCrawlToggled;

            if (isCrawlToggled) {
                mc.player.setForcedPose(Pose.SWIMMING);
                NetworkHandler.CHANNEL.sendToServer(new TestCrawlPacket(true));
                System.out.println("[TestCrawl] トグル ON");
            } else {
                mc.player.setForcedPose(null);
                NetworkHandler.CHANNEL.sendToServer(new TestCrawlPacket(false));
                System.out.println("[TestCrawl] トグル OFF");
            }
        }

        wasCPressedLastTick = cPressedNow;
    }

    // ==================== サーバー側処理（ここを大幅修正） ====================
    public static void handleServerPacket(ServerPlayer player, boolean enable) {
        if (player == null) return;

        // 【重要変更】搭乗中なら、無条件で1回だけ強制解除する
        if (player.isPassenger()) {
            // すでに解除済みなら何もしない（1回だけ保証）
            if (player.getForcedPose() == Pose.SWIMMING || player.getPose() == Pose.SWIMMING) {
                player.setForcedPose(null);
                player.setPose(Pose.STANDING);
                player.refreshDimensions();

                // 他のプレイヤー全員に「解除された」ことを同期
                NetworkHandler.CHANNEL.send(
                        PacketDistributor.TRACKING_ENTITY.with(() -> player),
                        new TestCrawlStatePacket(player.getUUID(), false)
                );

                System.out.println("[TestCrawl Server] 搭乗検知 → 1回だけ強制解除を実行");
            }
            return;
        }

        // 通常のON/OFF処理（搭乗していないときのみ）
        if (enable) {
            player.setForcedPose(Pose.SWIMMING);
        } else {
            player.setForcedPose(null);
        }

        player.refreshDimensions();

        NetworkHandler.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY.with(() -> player),
                new TestCrawlStatePacket(player.getUUID(), enable)
        );
    }

    // Mixinから呼ばれる判定用
    public static boolean isVKeyPressed() {
        if (Minecraft.getInstance() == null || Minecraft.getInstance().player == null) {
            return false;
        }
        if (Minecraft.getInstance().player.isPassenger()) {
            return false;
        }
        return isCrawlToggled;
    }
}