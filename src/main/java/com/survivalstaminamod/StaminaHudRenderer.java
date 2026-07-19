package com.survivalstaminamod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class StaminaHudRenderer implements IGuiOverlay {

    private static boolean isCurrentlyRedZone = false;

    public static boolean isRedZone() {
        return isCurrentlyRedZone;
    }

    private static final float BASE_STAMINA_CENTER_OFFSET_Y = 280f;
    private static final float BASE_STAMINA_MAX_WIDTH = 150f;
    private static final float BASE_STAMINA_HEIGHT = 2f;

    private static final int STAMINA_COLOR_NORMAL = 0xFFFFFFFF;
    private static final int STAMINA_COLOR_YELLOW = 0xFFFFFF00;
    private static final int STAMINA_COLOR_RED    = 0xFFFF0000;

    private static final float BASE_HUNGER_CENTER_OFFSET_Y = 300f;
    private static final float BASE_HUNGER_MAX_WIDTH = 150f;
    private static final float BASE_HUNGER_HEIGHT = 2f;

    private static final int HUNGER_COLOR_NORMAL = 0xFFFFFFFF; // 白（13〜20）
    private static final int HUNGER_COLOR_YELLOW = 0xFFFFFF00; // 黄（3〜12）
    private static final int HUNGER_COLOR_RED    = 0xFFFF0000; // 赤（0〜2）
    private static final int BLACK_BACKGROUND = 0xFF000000;
    private static float currentHungerDisplayWidth = 0f;
    private static final float HUNGER_ANIM_SPEED = 0.06f;

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("survivalstaminamod", "textures/gui/gui.png");

    private static final int BASE_DISPLAY_ICON_SIZE = 16;
    private static final int BASE_ICON_OFFSET_X = -29;
    private static final int BASE_CROP_SIZE = 256;
    private static final float BASE_HUD_Y_OFFSET = 60f;
    private static final float BASE_SCREEN_HEIGHT = 1017f;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        Player player = mc.player;

        float additionalOffset = 0f;
        GameType gameMode = mc.gameMode.getPlayerMode();
        if (gameMode == GameType.SURVIVAL || gameMode == GameType.ADVENTURE) {
            additionalOffset = BASE_HUD_Y_OFFSET;
        }

        double scale = mc.getWindow().getGuiScale();
        float physW = screenWidth * (float)scale;
        float physH = screenHeight * (float)scale;
        float resolutionScale = physH / BASE_SCREEN_HEIGHT;

        float adjustedStaminaOffsetY = BASE_STAMINA_CENTER_OFFSET_Y * resolutionScale;
        float adjustedHungerOffsetY = BASE_HUNGER_CENTER_OFFSET_Y * resolutionScale;
        float adjustedHudOffset = additionalOffset * resolutionScale;
        float adjustedIconSize = BASE_DISPLAY_ICON_SIZE * resolutionScale;
        float adjustedIconOffsetX = BASE_ICON_OFFSET_X * resolutionScale;
        float adjustedCropSize = BASE_CROP_SIZE * resolutionScale;

        guiGraphics.pose().pushPose();
        guiGraphics.pose().scale(1f / (float)scale, 1f / (float)scale, 1f);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();

        float cx = physW * 0.5f;

        float staminaCy = physH * 0.5f + adjustedStaminaOffsetY + adjustedHudOffset;
        float staminaLeft = cx - BASE_STAMINA_MAX_WIDTH * resolutionScale * 0.5f;
        float staminaTop = staminaCy - BASE_STAMINA_HEIGHT * resolutionScale * 0.5f;

        guiGraphics.fill((int)staminaLeft, (int)staminaTop,
                (int)(staminaLeft + BASE_STAMINA_MAX_WIDTH * resolutionScale), (int)(staminaTop + BASE_STAMINA_HEIGHT * resolutionScale),
                BLACK_BACKGROUND);

        int staminaIconX = (int)(staminaLeft + adjustedIconOffsetX);
        int staminaIconY = (int)(staminaCy - adjustedIconSize / 2f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(staminaIconX + adjustedIconSize / 2f, staminaIconY + adjustedIconSize / 2f, 0);
        guiGraphics.pose().scale(adjustedIconSize / BASE_CROP_SIZE, adjustedIconSize / BASE_CROP_SIZE, 1.0f);
        guiGraphics.pose().translate(-BASE_CROP_SIZE / 2f, -BASE_CROP_SIZE / 2f, 0);

        guiGraphics.blit(GUI_TEXTURE,
                0, 0,
                0, 0,
                BASE_CROP_SIZE, BASE_CROP_SIZE,
                512, 512);

        guiGraphics.pose().popPose();

        float hungerCy = physH * 0.5f + adjustedHungerOffsetY + adjustedHudOffset;
        float hungerTop = hungerCy - BASE_HUNGER_HEIGHT * resolutionScale * 0.5f;
        float hungerLeft = cx - BASE_HUNGER_MAX_WIDTH * resolutionScale * 0.5f;

        guiGraphics.fill((int)hungerLeft, (int)hungerTop,
                (int)(hungerLeft + BASE_HUNGER_MAX_WIDTH * resolutionScale), (int)(hungerTop + BASE_HUNGER_HEIGHT * resolutionScale),
                BLACK_BACKGROUND);

        int hungerIconX = (int)(hungerLeft + adjustedIconOffsetX);
        int hungerIconY = (int)(hungerCy - adjustedIconSize / 2f);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(hungerIconX + adjustedIconSize / 2f, hungerIconY + adjustedIconSize / 2f, 0);
        guiGraphics.pose().scale(adjustedIconSize / BASE_CROP_SIZE, adjustedIconSize / BASE_CROP_SIZE, 1.0f);
        guiGraphics.pose().translate(-BASE_CROP_SIZE / 2f, -BASE_CROP_SIZE / 2f, 0);

        guiGraphics.blit(GUI_TEXTURE,
                0, 0,
                BASE_CROP_SIZE, 0,
                BASE_CROP_SIZE, BASE_CROP_SIZE,
                512, 512);

        guiGraphics.pose().popPose();

        int staminaColor;
        float stamina = StaminaSystem.getStamina(player);
        isCurrentlyRedZone = (stamina < 10.0f);
        if (stamina < 10.0f) {
            staminaColor = STAMINA_COLOR_RED;
        } else if (stamina < 60.0f) {
            staminaColor = STAMINA_COLOR_YELLOW;
        } else {
            staminaColor = STAMINA_COLOR_NORMAL;
        }

        float staminaWidth = BASE_STAMINA_MAX_WIDTH * resolutionScale * (stamina / 100f);
        int drawStaminaWidth = (int) Math.ceil(staminaWidth);
        guiGraphics.fill((int)staminaLeft, (int)staminaTop,
                (int)(staminaLeft + drawStaminaWidth), (int)(staminaTop + BASE_STAMINA_HEIGHT * resolutionScale),
                staminaColor);

        int foodLevel = player.getFoodData().getFoodLevel();

        int hungerColor;
        if (foodLevel <= 2) {
            hungerColor = HUNGER_COLOR_RED;
        } else if (foodLevel <= 12) {
            hungerColor = HUNGER_COLOR_YELLOW;
        } else {
            hungerColor = HUNGER_COLOR_NORMAL;
        }

        float targetHungerWidth = BASE_HUNGER_MAX_WIDTH * resolutionScale * (foodLevel / 20.0f);

        if (player.tickCount <= 5) currentHungerDisplayWidth = targetHungerWidth;
        if (Math.abs(targetHungerWidth - currentHungerDisplayWidth) > 0.001f) {
            float diff = targetHungerWidth - currentHungerDisplayWidth;
            float step = Math.min(HUNGER_ANIM_SPEED, Math.abs(diff));
            if (diff < 0) step = -step;
            if (Math.abs(diff) <= Math.abs(step)) currentHungerDisplayWidth = targetHungerWidth;
            else currentHungerDisplayWidth += step;
        }

        int drawWidth = (int) Math.ceil(currentHungerDisplayWidth);
        if (drawWidth > 0) {
            guiGraphics.fill((int)hungerLeft, (int)hungerTop,
                    (int)(hungerLeft + drawWidth), (int)(hungerTop + BASE_HUNGER_HEIGHT * resolutionScale),
                    hungerColor);
        }

        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
        guiGraphics.pose().popPose();
    }
}