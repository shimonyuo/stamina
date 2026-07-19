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
            cir.setReturnValue(Vec3.ZERO);
        }
    }
}