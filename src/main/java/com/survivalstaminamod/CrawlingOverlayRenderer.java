package com.survivalstaminamod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CrawlingOverlayRenderer implements IGuiOverlay {

    private static final ResourceLocation CRAWLING_TEXTURE =
            new ResourceLocation("survivalstaminamod", "textures/gui/gui.png");

    private static final float BASE_SCALE_FACTOR = 0.115f;
    private static final float BASE_OFFSET_Y = 290f;
    private static final float BASE_OFFSET_X = 98f;
    private static final int ORIGINAL_WIDTH = 512;
    private static final int ORIGINAL_HEIGHT = 512;
    private static final int CROP_SIZE = 256;
    private static final float FADE_TIME = 0.5f;
    private static final float BASE_CROWD_ICON_Y_OFFSET = 60f;
    private static final float BASE_SCREEN_HEIGHT = 1017f;
    private static float fadeTimer = 0.0f;
    private static boolean wasCrawling = false;
    private static boolean isInitialized = false;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        if (mc.player.tickCount < 5 || !isInitialized) {
            fadeTimer = 0.0f;
            boolean isCrawlingNow = TestCrawlHandler.isVKeyPressed();
            wasCrawling = isCrawlingNow;
            isInitialized = true;

            if (!isCrawlingNow) {
                return;
            }
        }

        boolean isCrawling = TestCrawlHandler.isVKeyPressed();
        float deltaTime = mc.getDeltaFrameTime() / 20.0f;

        if (isCrawling) {
            fadeTimer = 0.0f;
            wasCrawling = true;
        } else if (wasCrawling) {
            fadeTimer += deltaTime;
        }

        if (!isCrawling && fadeTimer >= FADE_TIME) {
            wasCrawling = false;
            fadeTimer = 0.0f;
            return;
        }

        if (!isCrawling && !wasCrawling) {
            return;
        }

        float alpha = 1.0f;
        if (!isCrawling && wasCrawling) {
            float fadeProgress = fadeTimer / FADE_TIME;
            alpha = Math.max(0.0f, 1.0f - fadeProgress);
        }

        if (alpha <= 0.0f) return;

        double scale = mc.getWindow().getGuiScale();
        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1f / (float)scale, 1f / (float)scale, 1f);

        float physW = screenWidth * (float)scale;
        float physH = screenHeight * (float)scale;

        float resolutionScale = physH / BASE_SCREEN_HEIGHT;

        float adjustedOffsetX = BASE_OFFSET_X * resolutionScale;
        float adjustedOffsetY = BASE_OFFSET_Y * resolutionScale;
        float adjustedScaleFactor = BASE_SCALE_FACTOR * resolutionScale;
        float adjustedCrowdOffset = 0f;

        GameType gameMode = mc.gameMode.getPlayerMode();
        if (gameMode == GameType.SURVIVAL || gameMode == GameType.ADVENTURE) {
            adjustedCrowdOffset = BASE_CROWD_ICON_Y_OFFSET * resolutionScale;
        }

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        float cx = physW * 0.5f + adjustedOffsetX;
        float cy = physH * 0.5f + adjustedOffsetY + adjustedCrowdOffset;

        int displayWidth = (int)(CROP_SIZE * adjustedScaleFactor);
        int displayHeight = (int)(CROP_SIZE * adjustedScaleFactor);

        int posX = (int)(cx - displayWidth / 2f);
        int posY = (int)(cy - displayHeight / 2f);

        guiGraphics.pose().pushPose();

        guiGraphics.pose().translate(posX + displayWidth / 2f, posY + displayHeight / 2f, 0);
        guiGraphics.pose().scale(adjustedScaleFactor, adjustedScaleFactor, 1.0f);
        guiGraphics.pose().translate(-CROP_SIZE / 2f, -CROP_SIZE / 2f, 0);

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, alpha);

        guiGraphics.blit(CRAWLING_TEXTURE, 0, 0, 0, 256, CROP_SIZE, CROP_SIZE, ORIGINAL_WIDTH, ORIGINAL_HEIGHT);

        guiGraphics.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        guiGraphics.pose().popPose();

        guiGraphics.pose().popPose();
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }
}