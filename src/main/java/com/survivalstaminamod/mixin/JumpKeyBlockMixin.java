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

        // ジャンプキー以外は何もしない
        if (thisKey != Minecraft.getInstance().options.keyJump) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        // ==================== 条件判定 ====================

        // 1. 水中または乗り物に乗っている場合はジャンプを許可
        if (mc.player.isInWater() || mc.player.isPassenger()) {
            return;                    // ブロックせず、通常通りジャンプ可能
        }

        // 2. スタミナがレッドゾーンの場合はジャンプをブロック
        if (StaminaHudRenderer.isRedZone()) {
            cir.setReturnValue(false); // ジャンプキー入力を無効化
        }

        // その他の場合は通常通りジャンプ可能（何もしない）
    }
}