package com.survivalstaminamod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class OxygenVignetteOverlay {

    private static final ResourceLocation OXYGEN_VIGNETTE =
            new ResourceLocation("survivalstaminamod", "textures/gui/vignette2.png");

    private static final float MAX_INTENSITY = 1.0f;

    public OxygenVignetteOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || mc.player == null) return;

        float air = mc.player.getAirSupply();
        float maxAir = mc.player.getMaxAirSupply();

        float alpha = (1.0f - (air / maxAir)) * MAX_INTENSITY;

        if (alpha <= 0.0001f) {
            return;
        }

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        GuiGraphics gg = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gg.setColor(1.0f, 1.0f, 1.0f, alpha);
        gg.blit(OXYGEN_VIGNETTE, 0, 0, 0, 0, w, h, w, h);

        gg.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}