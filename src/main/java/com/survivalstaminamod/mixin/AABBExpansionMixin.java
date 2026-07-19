package com.survivalstaminamod.mixin;

import com.survivalstaminamod.SwimmingAABBExpansion;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class AABBExpansionMixin {

    /**
     * getBoundingBox() を Mixin で拦截
     * 返却前に現在の AABB に拡張幅を適用
     *
     * 毎フレーム呼ばれるため、アニメーション効果が生まれる
     */
    @Inject(
            method = "getBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injectSwimmingAABBExpansion(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity)(Object)this;

        // プレイヤーのみ対象
        if (!(entity instanceof Player player)) {
            return;
        }

        // 現在の拡張幅を取得（SWIMMING 時のみ 0 以上）
        float expansion = SwimmingAABBExpansion.getCurrentAABBExpansion(player);

        if (expansion <= 0.0f) {
            // 拡張なし
            return;
        }

        // 元の AABB を取得
        AABB originalAABB = cir.getReturnValue();

        // 水平方向（X・Z）に拡張
        // inflate() は各方向に指定された距離だけ拡張するため、
        // 幅を 0.4 増やすには、両側に 0.2 ずつ拡張 = expansion / 2.0
        AABB expandedAABB = originalAABB.inflate(expansion / 2.0D, 0.0D, expansion / 2.0D);

        cir.setReturnValue(expandedAABB);
    }
}