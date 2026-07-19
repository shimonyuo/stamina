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

    // ─────────────────────────────
    // 調整項目（ここを変更してください）
    // ─────────────────────────────
    private static final float BASE_SCALE_FACTOR = 0.115f;     // 基準解像度（縦1017）でのスケール
    private static final float BASE_OFFSET_Y = 290f;           // 基準解像度（縦1017）でのオフセット
    private static final float BASE_OFFSET_X = 98f;
    private static final int ORIGINAL_WIDTH = 512;
    private static final int ORIGINAL_HEIGHT = 512;
    private static final int CROP_SIZE = 256;
    private static final float FADE_TIME = 0.5f;

    // クロールアイコンを下にずらす量（基準解像度でのピクセル単位）
    // サバイバル/アドベンチャーモードの時だけ加算
    private static final float BASE_CROWD_ICON_Y_OFFSET = 60f;

    // 基準となる画面縦サイズ（これを元に比例計算）
    private static final float BASE_SCREEN_HEIGHT = 1017f;
    // ─────────────────────────────

    private static float fadeTimer = 0.0f;
    private static boolean wasCrawling = false;
    private static boolean isInitialized = false;

    @Override
    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // ワールド入場直後の初期化（1回だけ実行）
        if (mc.player.tickCount < 5 || !isInitialized) {
            fadeTimer = 0.0f;
            // 新しいVキー匍匐システムの状態を取得
            boolean isCrawlingNow = TestCrawlHandler.isVKeyPressed();
            wasCrawling = isCrawlingNow;
            isInitialized = true;

            // 匍匐中でなければ何も表示しない
            if (!isCrawlingNow) {
                return;
            }
        }

        // 現在のVキー匍匐状態を取得
        boolean isCrawling = TestCrawlHandler.isVKeyPressed();
        float deltaTime = mc.getDeltaFrameTime() / 20.0f;

        if (isCrawling) {
            // 匍匐中 → 完全に不透明、フェードタイマーリセット
            fadeTimer = 0.0f;
            wasCrawling = true;
        } else if (wasCrawling) {
            // 匍匐終了 → フェードアウト開始
            fadeTimer += deltaTime;
        }

        // フェードアウト完了したら状態をリセット
        if (!isCrawling && fadeTimer >= FADE_TIME) {
            wasCrawling = false;
            fadeTimer = 0.0f;
            return;
        }

        // 匍匐中でもなく、フェード中でもない場合は何も表示しない
        if (!isCrawling && !wasCrawling) {
            return;
        }

        // アルファ値計算（フェードアウトのみ）
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

        // 解像度比例の倍率
        float resolutionScale = physH / BASE_SCREEN_HEIGHT;

        // 比例調整された値
        float adjustedOffsetX = BASE_OFFSET_X * resolutionScale;
        float adjustedOffsetY = BASE_OFFSET_Y * resolutionScale;
        float adjustedScaleFactor = BASE_SCALE_FACTOR * resolutionScale;
        float adjustedCrowdOffset = 0f;

        // サバイバル/アドベンチャーモードの時だけ追加オフセット
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