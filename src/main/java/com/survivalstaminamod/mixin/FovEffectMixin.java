package com.survivalstaminamod.mixin;

import net.minecraft.client.player.AbstractClientPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public class FovEffectMixin {
    @Inject(method = "getFieldOfViewModifier", at = @At("HEAD"), cancellable = true)
    private void disableFovEffects(CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(1.0f);
    }
}