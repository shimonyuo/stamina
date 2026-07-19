package com.survivalstaminamod.mixin;

import com.survivalstaminamod.TestCrawlHandler;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class TestCrawlSprintCancelMixin {

    @Inject(
            method = "tick",
            at = @At("HEAD")
    )
    private void injectSprintCancel(CallbackInfo ci) {
        Player player = (Player)(Object)this;

        if (TestCrawlHandler.isVKeyPressed() && player.isSprinting()) {
            player.setSprinting(false);
        }
    }
}