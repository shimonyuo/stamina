package com.survivalstaminamod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Overlay {

    private static final ResourceLocation VIGNETTE =
            new ResourceLocation("survivalstaminamod", "textures/gui/vignette.png");

    private static final float MAX_INTENSITY   = 0.9f;
    private static final float PULSE_STRENGTH  = 0.1f;
    private static final float PULSE_SPEED     = 0.05f;

    private float heartbeatTimer = 0.0f;

    public Overlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || mc.player == null) return;

        float health = mc.player.getHealth();
        float maxHealth = mc.player.getMaxHealth();

        float baseAlpha = (1.0f - (health / maxHealth)) * MAX_INTENSITY;

        if (baseAlpha <= 0.0001f) {
            heartbeatTimer = 0.0f;
            return;
        }

        heartbeatTimer += mc.getDeltaFrameTime() * PULSE_SPEED;
        float cycle = heartbeatTimer - (int)heartbeatTimer;

        float pulseOffset = 0.0f;
        if (cycle < 0.15f) {
            pulseOffset = (cycle / 0.15f);
        } else if (cycle < 0.35f) {
            float progress = (cycle - 0.15f) / 0.20f;
            pulseOffset = 1.0f - progress;
        } else {
            pulseOffset = 0.0f;
        }

        float finalAlpha = Mth.clamp(baseAlpha + pulseOffset * PULSE_STRENGTH, 0.0f, MAX_INTENSITY);

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        GuiGraphics gg = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        gg.setColor(1.0f, 1.0f, 1.0f, finalAlpha);
        gg.blit(VIGNETTE, 0, 0, 0, 0, w, h, w, h);

        gg.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}