package com.survivalstaminamod;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HudHandler {

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        // 体力、空腹度、経験値バー、防具体力UI、水中酸素ゲージを非表示
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
                event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
                event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type() ||
                event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
                event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {   // ← これを追加

            event.setCanceled(true);
        }
    }
}