package com.survivalstaminamod.mixin;

import com.survivalstaminamod.TestCrawlHandler;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerRenderer.class)
public abstract class TestCrawlRenderOffsetMixin {

    /**
     * getRenderOffset() をオーバーライド
     * Vキー匍匐中は常に (0, 0, 0) を返す
     * （スニーク時の -0.125 Y オフセットをキャンセル）
     */
    @Inject(
            method = "getRenderOffset",
            at = @At("HEAD"),
            cancellable = true
    )
    private void injectRenderOffsetCancel(
            AbstractClientPlayer pPlayer,
            float pPartialTick,
            CallbackInfoReturnable<Vec3> cir) {

        if (TestCrawlHandler.isVKeyPressed()) {
            // オフセットなし（常に (0, 0, 0)）
            cir.setReturnValue(Vec3.ZERO);
        }
    }
}