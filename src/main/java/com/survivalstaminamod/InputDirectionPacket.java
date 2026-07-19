package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class InputDirectionPacket {
    private final Vec3 direction;  // 正規化されたXZ方向（長さ1 or ゼロ）

    public InputDirectionPacket(Vec3 direction) {
        this.direction = direction;
    }

    public static void encode(InputDirectionPacket msg, FriendlyByteBuf buf) {
        buf.writeDouble(msg.direction.x);
        buf.writeDouble(msg.direction.y);
        buf.writeDouble(msg.direction.z);
    }

    public static InputDirectionPacket decode(FriendlyByteBuf buf) {
        return new InputDirectionPacket(new Vec3(
                buf.readDouble(),
                buf.readDouble(),
                buf.readDouble()
        ));
    }

    public static void handle(InputDirectionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player != null) {
                InputDirectionStorage.setDirection(player.getUUID(), msg.direction);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}