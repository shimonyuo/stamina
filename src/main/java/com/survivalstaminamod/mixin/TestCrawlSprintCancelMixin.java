package com.survivalstaminamod.mixin;

import com.survivalstaminamod.TestCrawlHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class TestCrawlSprintCancelMixin {

    /**
     * Player.tick() でダッシュ状態をチェックし、匍匐中なら強制的にキャンセル
     */
    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void injectSprintCancel(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        // 匍匐状態かつダッシュ中なら、強制的にダッシュをキャンセル
        if (TestCrawlHandler.isVKeyPressed() && player.isSprinting()) {
            player.setSprinting(false);
        }
    }
}