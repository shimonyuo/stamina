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

    @Inject(
            method = "getBoundingBox",
            at = @At("RETURN"),
            cancellable = true
    )
    private void injectSwimmingAABBExpansion(CallbackInfoReturnable<AABB> cir) {
        Entity entity = (Entity)(Object)this;

        if (!(entity instanceof Player player)) {
            return;
        }

        float expansion = SwimmingAABBExpansion.getCurrentAABBExpansion(player);

        if (expansion <= 0.0f) {
            return;
        }

        AABB originalAABB = cir.getReturnValue();

        AABB expandedAABB = originalAABB.inflate(expansion / 2.0D, 0.0D, expansion / 2.0D);

        cir.setReturnValue(expandedAABB);
    }
}