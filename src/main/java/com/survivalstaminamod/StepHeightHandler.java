package com.survivalstaminamod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber(modid = SurvivalStaminaMod.MOD_ID)
public class StepHeightHandler {

    private static final Map<UUID, Vec3> lastPositions = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) {
            return;
        }

        if (!(event.player instanceof ServerPlayer player)) {
            return;
        }

        UUID uuid = player.getUUID();
        Vec3 currentPos = player.position();
        Vec3 lastPos = lastPositions.getOrDefault(uuid, currentPos);

        Vec3 deltaPos = currentPos.subtract(lastPos);
        Vec3 xzDelta = new Vec3(deltaPos.x, 0.0, deltaPos.z);

        if (xzDelta.lengthSqr() == 0.0) {
            lastPositions.put(uuid, currentPos);
            return;
        }

        Vec3 serverDirection = xzDelta.normalize();

        Vec3 inputDir = InputDirectionStorage.getDirection(uuid);
        boolean hasInput = inputDir.lengthSqr() > 0.001;

        Vec3 useDirection = hasInput ? inputDir.normalize() : serverDirection;

        Vec3 rightOffset = new Vec3(useDirection.z, 0.0, -useDirection.x).normalize().scale(0.31);
        Vec3 leftOffset = new Vec3(-useDirection.z, 0.0, useDirection.x).normalize().scale(0.31);

        Vec3 baseStartPos = currentPos.add(0.0, 0.45, 0.0);
        Vec3 rightStartPos = baseStartPos.add(rightOffset);
        Vec3 leftStartPos = baseStartPos.add(leftOffset);

        Vec3 forward = useDirection.scale(1.0);

        Vec3 rightEndPos = rightStartPos.add(forward);
        Vec3 leftEndPos = leftStartPos.add(forward);

        Vec3 centerStartPos = baseStartPos;
        Vec3 centerEndPos = centerStartPos.add(forward);

        boolean isTargetBlock = false;
        boolean centerBlockedByNonTarget = false;

        ClipContext centerContext = new ClipContext(
                centerStartPos, centerEndPos,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
        );
        BlockHitResult centerHit = player.level().clip(centerContext);

        if (centerHit.getType() == HitResult.Type.BLOCK) {
            BlockState hitState = player.level().getBlockState(centerHit.getBlockPos());
            if (!StaminaConfig.canStepOn(hitState)) {
                centerBlockedByNonTarget = true;
            } else {
                isTargetBlock = true;
            }
        }

        if (!centerBlockedByNonTarget) {
            ClipContext rightContext = new ClipContext(
                    rightStartPos, rightEndPos,
                    ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
            );
            BlockHitResult rightHit = player.level().clip(rightContext);

            if (rightHit.getType() == HitResult.Type.BLOCK) {
                BlockState hitState = player.level().getBlockState(rightHit.getBlockPos());
                if (StaminaConfig.canStepOn(hitState)) {
                    isTargetBlock = true;
                }
            }

            if (!isTargetBlock) {
                ClipContext leftContext = new ClipContext(
                        leftStartPos, leftEndPos,
                        ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player
                );
                BlockHitResult leftHit = player.level().clip(leftContext);

                if (leftHit.getType() == HitResult.Type.BLOCK) {
                    BlockState hitState = player.level().getBlockState(leftHit.getBlockPos());
                    if (StaminaConfig.canStepOn(hitState)) {
                        isTargetBlock = true;
                    }
                }
            }
        }

        var stepAttr = player.getAttribute(ForgeMod.STEP_HEIGHT_ADDITION.get());
        if (stepAttr != null) {
            stepAttr.setBaseValue(centerBlockedByNonTarget ? 0.0 : (isTargetBlock ? 0.4 : 0.0));
        }

        lastPositions.put(uuid, currentPos);
    }
}