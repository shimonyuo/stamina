package com.survivalstaminamod.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract AABB getBoundingBox();
    @Shadow public abstract Level level();
    @Shadow public abstract boolean isRemoved();
    @Shadow public abstract Vec3 getDeltaMovement();
    @Shadow public abstract void setDeltaMovement(Vec3 vec);

    @Inject(method = "move", at = @At("HEAD"))
    private void pushOutFromOverlappingBlocks(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (this.isRemoved()) {
            return;
        }

        if (!((Entity)(Object)this instanceof Player player)) {
            return;
        }

        GameType gameMode;
        if (player instanceof ServerPlayer serverPlayer) {
            gameMode = serverPlayer.gameMode.getGameModeForPlayer();
        } else {
            gameMode = Minecraft.getInstance().gameMode.getPlayerMode();
        }

        if (gameMode != GameType.SURVIVAL && gameMode != GameType.CREATIVE && gameMode != GameType.ADVENTURE) {
            return;
        }

        AABB collisionBox = this.getBoundingBox();
        Vec3 playerCenter = collisionBox.getCenter();

        if (level().noCollision((Entity)(Object)this, collisionBox)) {
            return;
        }

        List<Vec3> blockCenters = new ArrayList<>();

        BlockPos minPos = BlockPos.containing(collisionBox.minX, collisionBox.minY, collisionBox.minZ);
        BlockPos maxPos = BlockPos.containing(collisionBox.maxX, collisionBox.maxY, collisionBox.maxZ);

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    VoxelShape shape = level().getBlockState(pos).getCollisionShape(level(), pos);

                    if (!shape.isEmpty()) {
                        AABB blockBox = new AABB(pos).inflate(1, 1, 1);
                        if (Shapes.joinIsNotEmpty(shape.move(pos.getX(), pos.getY(), pos.getZ()), Shapes.create(collisionBox), BooleanOp.AND)) {
                            Vec3 center = new Vec3(
                                    pos.getX() + 0.5,
                                    pos.getY() + 0.5,
                                    pos.getZ() + 0.5
                            );
                            blockCenters.add(center);
                        }
                    }
                }
            }
        }

        if (!blockCenters.isEmpty()) {
            Vec3 totalPush = Vec3.ZERO;
            double pushStrength = 0.2;

            for (Vec3 blockCenter : blockCenters) {
                Vec3 toBlock = blockCenter.subtract(playerCenter);
                double dx = toBlock.x;
                double dz = toBlock.z;

                double angle = Math.atan2(dz, dx) * (180.0 / Math.PI);

                double normalized = Math.round(angle / 90.0) * 90.0;

                if (normalized > 180.0) {
                    normalized -= 360.0;
                } else if (normalized < -180.0) {
                    normalized += 360.0;
                }

                double oppositeAngle;
                if (normalized >= 0) {
                    oppositeAngle = normalized - 180.0;  // 正の値 → -180加算
                } else {
                    oppositeAngle = normalized + 180.0;  // 負の値 → +180加算
                }

                double rad = Math.toRadians(oppositeAngle);
                double pushX = Math.cos(rad) * pushStrength;
                double pushZ = Math.sin(rad) * pushStrength;

                totalPush = totalPush.add(pushX, 0.0, pushZ);
            }

            Vec3 currentMotion = this.getDeltaMovement();
            this.setDeltaMovement(currentMotion.add(totalPush));
        }
    }
}