package com.survivalstaminamod.mixin;

import com.survivalstaminamod.StaminaHudRenderer;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(KeyMapping.class)
public abstract class JumpKeyBlockMixin {

    @Inject(method = "isDown()Z", at = @At("HEAD"), cancellable = true)
    private void blockJumpKey(CallbackInfoReturnable<Boolean> cir) {
        KeyMapping thisKey = (KeyMapping) (Object) this;

        if (thisKey != Minecraft.getInstance().options.keyJump) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.isInWater() || mc.player.isPassenger()) {
            return;
        }

        if (StaminaHudRenderer.isRedZone()) {
            cir.setReturnValue(false);
        }
    }
}