package com.survivalstaminamod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class HealingOverlayRenderer implements IGuiOverlay {

    private static final ResourceLocation HEALING_TEXTURE =
            new ResourceLocation("survivalstaminamod", "textures/gui/healing.png");

    private static final float BASE_SCALE_FACTOR = 0.18f;
    private static final float BASE_OFFSET_Y = 155f;
    private static final int ORIGINAL_WIDTH = 512;
    private static final int ORIGINAL_HEIGHT = 512;
    private static final float REMAIN_TIME = 3.0f;
    private static final float FADE_TIME = 2.0f;
    private static final float BASE_SCREEN_HEIGHT = 1017f;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int currentTick = mc.player.tickCount;

        HealingEventHandler.tick(partialTick, currentTick);

        if (!HealingEventHandler.shouldDisplay()) {
            return;
        }

        float timer = HealingEventHandler.getDisplayTimer();

        if (timer >= REMAIN_TIME + FADE_TIME) {
            HealingEventHandler.stopDisplay();
            return;
        }

        float alpha = 1.0f;
        if (timer > REMAIN_TIME) {
            float fadeProgress = (timer - REMAIN_TIME) / FADE_TIME;
            alpha = Math.max(0.0f, 1.0f - fadeProgress);
        }

        if (alpha <= 0.0f) return;

        double scale = mc.getWindow().getGuiScale();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1f / (float)scale, 1f / (float)scale, 1f);
        float physW = screenWidth * (float)scale;
        float physH = screenHeight * (float)scale;
        float resolutionScale = physH / BASE_SCREEN_HEIGHT;
        float adjustedScaleFactor = BASE_SCALE_FACTOR * resolutionScale;
        float adjustedOffsetY = BASE_OFFSET_Y * resolutionScale;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        float cx = physW * 0.5f;
        float cy = physH * 0.5f + adjustedOffsetY;

        int displayWidth = (int)(ORIGINAL_WIDTH * adjustedScaleFactor);
        int displayHeight = (int)(ORIGINAL_HEIGHT * adjustedScaleFactor);

        int posX = (int)(cx - displayWidth / 2f);
        int posY = (int)(cy - displayHeight / 2f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(posX + displayWidth / 2f, posY + displayHeight / 2f, 0);
        guiGraphics.pose().scale(adjustedScaleFactor, adjustedScaleFactor, 1.0f);
        guiGraphics.pose().translate(-ORIGINAL_WIDTH / 2f, -ORIGINAL_HEIGHT / 2f, 0);

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha);
        guiGraphics.blit(HEALING_TEXTURE, 0, 0, 0, 0, ORIGINAL_WIDTH, ORIGINAL_HEIGHT, ORIGINAL_WIDTH, ORIGINAL_HEIGHT);
        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.pose().popPose();

        guiGraphics.pose().popPose();

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}