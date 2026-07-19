package com.survivalstaminamod.mixin;

import net.minecraft.client.model.PlayerModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerModel.class)
public abstract class TestCrawlPlayerModelMixin {

    @Inject(
            method = "setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V",
            at = @At("HEAD")
    )
    private void cancelCrouchWhenSwimming(
            LivingEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float ageInTicks,
            float netHeadYaw,
            float headPitch,
            CallbackInfo ci) {

        if (entity instanceof Player player) {
            if (player.getPose() == Pose.SWIMMING ||
                    (player.getForcedPose() != null && player.getForcedPose() == Pose.SWIMMING)) {

                PlayerModel<?> model = (PlayerModel<?>) (Object) this;
                model.crouching = false;
            }
        }
    }
}