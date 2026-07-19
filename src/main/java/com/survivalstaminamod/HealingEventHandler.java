package com.survivalstaminamod;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HealingEventHandler {

    private static boolean shouldDisplay = false;
    private static float displayTimer = 0.0f;
    private static int lastResetTick = -100;

    @SubscribeEvent
    public static void onLivingHeal(LivingHealEvent event) {
        if (event.getEntity().level().isClientSide) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        NetworkHandler.CHANNEL.send(
                net.minecraftforge.network.PacketDistributor.PLAYER.with(() -> player),
                new HealingPacket()
        );
    }

    public static void triggerDisplay() {
        shouldDisplay = true;
        displayTimer = 0.0f;
    }

    public static void tick(float partialTick, int currentTick) {
        if (currentTick < 10 || currentTick < lastResetTick) {
            shouldDisplay = false;
            displayTimer = 0.0f;
            lastResetTick = currentTick;
            return;
        }
        if (currentTick - lastResetTick < 10) {
            return;
        }
        if (shouldDisplay) {
            displayTimer += partialTick / 20.0f;
        }
    }

    public static boolean shouldDisplay() {
        return shouldDisplay;
    }

    public static float getDisplayTimer() {
        return displayTimer;
    }

    public static void stopDisplay() {
        shouldDisplay = false;
        displayTimer = 0.0f;
    }
}