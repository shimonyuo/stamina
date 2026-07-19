package com.survivalstaminamod.mixin;

import com.survivalstaminamod.StaminaSystem;
import com.survivalstaminamod.TestCrawlHandler;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class StaminaSpeedDebuff {

    private static final float NORMAL_MULTIPLIER = 0.94f;
    private static final float YELLOW_MULTIPLIER = 0.88f;
    private static final float RED_MULTIPLIER = 0.85f;
    private static final float WALK_ADDITIONAL_MULTIPLIER = 0.8f;
    private static final float SNEAK_CRAWL_MULTIPLIER = 0.5f;
    private static final float WATER_MULTIPLIER = 0.9f;

    @ModifyVariable(
            method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
            at = @At("HEAD"),
            argsOnly = true
    )
    private Vec3 modifyTravelInput(Vec3 travelVector) {
        Player player = (Player)(Object)this;

        if (player.getAbilities().flying || player.isFallFlying()) {
            return travelVector;
        }

        if (player.isInWater()) {
            return travelVector.multiply(WATER_MULTIPLIER, WATER_MULTIPLIER, WATER_MULTIPLIER);
        }

        float stamina = StaminaSystem.getStamina(player);
        float multiplier = player.isSprinting()
                ? (stamina < 10.0f ? RED_MULTIPLIER : stamina < 60.0f ? YELLOW_MULTIPLIER : NORMAL_MULTIPLIER)
                : NORMAL_MULTIPLIER * WALK_ADDITIONAL_MULTIPLIER;

        if (TestCrawlHandler.isVKeyPressed() ||
                (player.getPose() == Pose.SWIMMING && !player.isInWater()) ||
                player.isShiftKeyDown()) {
            multiplier *= SNEAK_CRAWL_MULTIPLIER;
        }

        Vec3 normalized = travelVector.normalize();
        return new Vec3(
                normalized.x * multiplier,
                travelVector.y,
                normalized.z * multiplier
        );
    }

    @Inject(method = "travel(Lnet/minecraft/world/phys/Vec3;)V", at = @At("TAIL"))
    private void applyMultiplierToVelocity(Vec3 travelVector, CallbackInfo ci) {
        Player player = (Player)(Object)this;

        if (player.getAbilities().flying || player.isFallFlying()) return;

        if (player.isInWater()) {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(motion.multiply(WATER_MULTIPLIER, WATER_MULTIPLIER, WATER_MULTIPLIER));
            return;
        }

        float stamina = StaminaSystem.getStamina(player);
        float multiplier = player.isSprinting()
                ? (stamina < 10.0f ? RED_MULTIPLIER : stamina < 60.0f ? YELLOW_MULTIPLIER : NORMAL_MULTIPLIER)
                : NORMAL_MULTIPLIER * WALK_ADDITIONAL_MULTIPLIER;

        if (TestCrawlHandler.isVKeyPressed() ||
                (player.getPose() == Pose.SWIMMING && !player.isInWater()) ||
                player.isShiftKeyDown()) {
            multiplier *= SNEAK_CRAWL_MULTIPLIER;
        }

        Vec3 motion = player.getDeltaMovement();
        player.setDeltaMovement(
                motion.x * multiplier,
                motion.y,
                motion.z * multiplier
        );
    }
}