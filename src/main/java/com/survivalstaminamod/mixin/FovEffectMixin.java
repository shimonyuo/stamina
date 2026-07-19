package com.survivalstaminamod.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class FovEffectMixin {

    /**
     * getFovを乗っ取り、常に1.0f（変更なし）を返す
     * これによりダッシュ、飛行、水中、エフェクトによる視野角変更を無効化
     */
    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    private void disableFovEffects(CallbackInfoReturnable<Float> cir) {
        // 常に1.0fを返す = 視野角変更なし
        cir.setReturnValue(1.0f);
    }
}