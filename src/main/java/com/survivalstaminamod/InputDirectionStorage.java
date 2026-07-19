package com.survivalstaminamod;

import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class InputDirectionStorage {
    private static final Map<UUID, Vec3> clientInputDirections = new HashMap<>();

    public static void setDirection(UUID uuid, Vec3 dir) {
        clientInputDirections.put(uuid, dir);
    }

    public static Vec3 getDirection(UUID uuid) {
        return clientInputDirections.getOrDefault(uuid, Vec3.ZERO);
    }

    // 必要に応じてクリア（例: ログアウト時）だがテストでは不要
}