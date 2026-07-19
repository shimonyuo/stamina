package com.survivalstaminamod;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomOverlays {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        // 回復オーバーレイを最上位に登録
        event.registerAboveAll("healing_overlay", new HealingOverlayRenderer());

        // 匍匐オーバーレイを最上位に登録
        event.registerAboveAll("crawling_overlay", new CrawlingOverlayRenderer());

        // CustomOverlays.java の registerOverlays メソッド内
        event.registerAboveAll("stamina_hud", new StaminaHudRenderer());
    }
}