package com.survivalstaminamod;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ClientJumpHandler {

    private static boolean justLanded = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.START) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        boolean isJumpPressedNow = mc.options.keyJump.isDown();
        boolean isOnGroundNow = mc.player.onGround();
        boolean isInWater = mc.player.isInWater();

        if (isOnGroundNow && !justLanded) {
            justLanded = true;
        }
        if (isJumpPressedNow && isOnGroundNow && !isInWater) {

            NetworkHandler.CHANNEL.sendToServer(new JumpPressedPacket());
        }
    }
}