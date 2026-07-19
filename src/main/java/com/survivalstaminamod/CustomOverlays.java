package com.survivalstaminamod;

import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class CustomOverlays {

    @SubscribeEvent
    public static void registerOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("healing_overlay", new HealingOverlayRenderer());
        event.registerAboveAll("crawling_overlay", new CrawlingOverlayRenderer());
        event.registerAboveAll("stamina_hud", new StaminaHudRenderer());
    }
}