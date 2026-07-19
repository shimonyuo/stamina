package com.survivalstaminamod;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class HudHandler {

    @SubscribeEvent
    public void onRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type() ||
                event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type() ||
                event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type() ||
                event.getOverlay() == VanillaGuiOverlay.ARMOR_LEVEL.type() ||
                event.getOverlay() == VanillaGuiOverlay.AIR_LEVEL.type()) {

            event.setCanceled(true);
        }
    }
}