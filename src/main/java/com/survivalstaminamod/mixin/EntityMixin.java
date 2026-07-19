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

    // 毎フレーム、重なっているブロックの反対方向に水平押し出し
    @Inject(method = "move", at = @At("HEAD"))
    private void pushOutFromOverlappingBlocks(MoverType type, Vec3 movement, CallbackInfo ci) {
        if (this.isRemoved()) {
            return;
        }

        // プレイヤー以外は何もしない
        if (!((Entity)(Object)this instanceof Player player)) {
            return;
        }

        GameType gameMode;
        if (player instanceof ServerPlayer serverPlayer) {
            // Forge/NeoForge では interactionManager が存在しない場合があるので
            // gameMode を直接取得（1.20.1+ の安定した方法）
            gameMode = serverPlayer.gameMode.getGameModeForPlayer();
        } else {
            // クライアント側フォールバック
            gameMode = Minecraft.getInstance().gameMode.getPlayerMode();
        }

        if (gameMode != GameType.SURVIVAL && gameMode != GameType.CREATIVE && gameMode != GameType.ADVENTURE) {
            return;  // SPECTATOR などでは押し出し無効
        }

        AABB collisionBox = this.getBoundingBox();
        Vec3 playerCenter = collisionBox.getCenter();

        // 完全に空いている → 何も押し出さない
        if (level().noCollision((Entity)(Object)this, collisionBox)) {
            return;
        }

        // 重なっているブロックの中心位置リストを取得
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
            double pushStrength = 0.2;  // 1フレームあたりの押し出し量（調整可能）

            for (Vec3 blockCenter : blockCenters) {
                Vec3 toBlock = blockCenter.subtract(playerCenter);
                double dx = toBlock.x;
                double dz = toBlock.z;

                // atan2で角度計算（-180～180）
                double angle = Math.atan2(dz, dx) * (180.0 / Math.PI);

                // 90度単位に丸める（4方向に統一）
                double normalized = Math.round(angle / 90.0) * 90.0;

                // -180 ～ 180 の範囲に正規化
                if (normalized > 180.0) {
                    normalized -= 360.0;
                } else if (normalized < -180.0) {
                    normalized += 360.0;
                }

                // 符号で反転（正反対の方向を計算）
                double oppositeAngle;
                if (normalized >= 0) {
                    oppositeAngle = normalized - 180.0;  // 正の値 → -180加算
                } else {
                    oppositeAngle = normalized + 180.0;  // 負の値 → +180加算
                }

                // ラジアンに変換して押し出し計算
                double rad = Math.toRadians(oppositeAngle);
                double pushX = Math.cos(rad) * pushStrength;
                double pushZ = Math.sin(rad) * pushStrength;

                // 各ブロックごとに押し出しを加算
                totalPush = totalPush.add(pushX, 0.0, pushZ);
            }

            // 現在の移動に総押し出しを追加
            Vec3 currentMotion = this.getDeltaMovement();
            this.setDeltaMovement(currentMotion.add(totalPush));
        }
    }
}