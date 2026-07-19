package com.survivalstaminamod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SurvivalStaminaMod.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientInputHandler {

    private static Vec3 lastSentDirection = Vec3.ZERO;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || Minecraft.getInstance().player == null) {
            return;
        }

        LocalPlayer player = Minecraft.getInstance().player;

        double forward = player.input.forwardImpulse;
        double strafe = player.input.leftImpulse;

        float yawRad = (float) Math.toRadians(player.getYRot());
        double x = strafe * Math.cos(yawRad) - forward * Math.sin(yawRad);
        double z = strafe * Math.sin(yawRad) + forward * Math.cos(yawRad);

        Vec3 worldDir = new Vec3(x, 0.0, z);

        if (worldDir.lengthSqr() > 0.001) {
            worldDir = worldDir.normalize();
        } else {
            worldDir = Vec3.ZERO;
        }

        if (!worldDir.equals(lastSentDirection)) {
            NetworkHandler.CHANNEL.sendToServer(new InputDirectionPacket(worldDir));
            lastSentDirection = worldDir;
        }
    }
}