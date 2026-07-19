package com.survivalstaminamod;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkDirection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
@Mod.EventBusSubscriber(modid = "survivalstaminamod", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class StaminaSystem {
    public static final Map<UUID, Float> staminaMap = new HashMap<>();
    public static final float MAX_STAMINA = 100.0f;
    public static final float DASH_COST_PER_SECOND = 2.5f;
    public static final float RECOVERY_PER_SECOND = 5.0f;
    public static final float JUMP_COST = 20.0f;

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        float savedStamina = player.getPersistentData().contains("SurvivalStaminaMod:stamina")
                ? player.getPersistentData().getFloat("SurvivalStaminaMod:stamina")
                : MAX_STAMINA;
        savedStamina = clamp(savedStamina, 0f, MAX_STAMINA);
        staminaMap.put(uuid, savedStamina);
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        staminaMap.put(uuid, MAX_STAMINA);
    }
    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();
        UUID uuid = player.getUUID();
        float currentStamina = staminaMap.getOrDefault(uuid, MAX_STAMINA);
        player.getPersistentData().putFloat("SurvivalStaminaMod:stamina", currentStamina);
    }
    public static float getStamina(Player player) {
        return staminaMap.getOrDefault(player.getUUID(), MAX_STAMINA);
    }
    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isClient() || event.phase != TickEvent.Phase.END) return;
        Player player = event.player;
        UUID uuid = player.getUUID();
        float current = staminaMap.getOrDefault(uuid, MAX_STAMINA);
        GameType gameMode = (player instanceof ServerPlayer sp)
                ? sp.gameMode.getGameModeForPlayer()
                : GameType.SURVIVAL;
        boolean allowDecrease = (gameMode == GameType.SURVIVAL || gameMode == GameType.ADVENTURE);
        boolean allowRecovery = true;
        if (allowDecrease && player.isSprinting()) {
            current -= DASH_COST_PER_SECOND / 20f;
        }
        if (allowRecovery) {
            if (allowDecrease) {
                if (!player.isSprinting() && !player.onClimbable() && !player.isInWater()) {
                    current += RECOVERY_PER_SECOND / 20f;
                }
            } else {
                current += RECOVERY_PER_SECOND / 20f;
            }
        }
        current = clamp(current, 0f, MAX_STAMINA);
        float previousStamina = staminaMap.getOrDefault(uuid, MAX_STAMINA);
        staminaMap.put(uuid, current);
        if (Math.abs(current - previousStamina) > 0.01f) {
            if (player instanceof ServerPlayer serverPlayer) {
                NetworkHandler.CHANNEL.sendTo(
                        new StaminaUpdatePacket(uuid, current),
                        serverPlayer.connection.connection, // 自分だけ
                        NetworkDirection.PLAY_TO_CLIENT
                );
            }
        }
    }
    public static void onJumpKeyPressed(Player player) {
        if (player.level().isClientSide()) return;

        GameType gameMode = (player instanceof ServerPlayer sp)
                ? sp.gameMode.getGameModeForPlayer()
                : GameType.SURVIVAL;

        if (gameMode != GameType.SURVIVAL && gameMode != GameType.ADVENTURE) {
            return;
        }

        UUID uuid = player.getUUID();
        float current = staminaMap.getOrDefault(uuid, MAX_STAMINA);
        current -= JUMP_COST;
        current = clamp(current, 0f, MAX_STAMINA);
        staminaMap.put(uuid, current);

        if (player instanceof ServerPlayer serverPlayer) {
            NetworkHandler.CHANNEL.sendTo(
                    new StaminaUpdatePacket(uuid, current),
                    serverPlayer.connection.connection,
                    NetworkDirection.PLAY_TO_CLIENT
            );
        }
    }
}