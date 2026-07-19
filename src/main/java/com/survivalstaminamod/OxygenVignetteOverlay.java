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

public class OxygenVignetteOverlay {

    private static final ResourceLocation OXYGEN_VIGNETTE =
            new ResourceLocation("survivalstaminamod", "textures/gui/vignette2.png");

    // ─────────────────────────────
    // 調整項目（ここだけ触ってください）
    // ─────────────────────────────
    private static final float MAX_INTENSITY = 1.0f;   // 酸素0のときの最大透明度（0.0～1.0）
    // ─────────────────────────────

    public OxygenVignetteOverlay() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        // VanillaのVIGNETTEの奥（後ろ）に描画したいので、同じPostイベントで処理
        if (!event.getOverlay().id().equals(VanillaGuiOverlay.VIGNETTE.id())) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.level == null || mc.player == null) return;

        // 水中酸素を取得（0〜300が通常範囲）
        float air = mc.player.getAirSupply();
        float maxAir = mc.player.getMaxAirSupply();

        // 酸素が満タンに近いほど透明、減るほど濃くなる
        float alpha = (1.0f - (air / maxAir)) * MAX_INTENSITY;

        if (alpha <= 0.0001f) {
            return;   // 酸素がほぼ満タンなら描画しない
        }

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        GuiGraphics gg = event.getGuiGraphics();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        // 酸素ビネットを描画（体力ビネットより奥に表示される）
        gg.setColor(1.0f, 1.0f, 1.0f, alpha);
        gg.blit(OXYGEN_VIGNETTE, 0, 0, 0, 0, w, h, w, h);

        gg.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.disableBlend();
    }
}