package com.survivalstaminamod;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HealingPacket {

    // パケットにデータは不要（回復したという通知のみ）

    public HealingPacket() {
    }

    // パケットをバイトデータにエンコード（送信用）
    public static void encode(HealingPacket packet, FriendlyByteBuf buf) {
        // データなし
    }

    // バイトデータからパケットをデコード（受信用）
    public static HealingPacket decode(FriendlyByteBuf buf) {
        return new HealingPacket();
    }

    // パケット受信時の処理（クライアント側で実行）
    public static void handle(HealingPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            // クライアント側でGUI表示フラグを立てる
            HealingEventHandler.triggerDisplay();
        });
        ctx.get().setPacketHandled(true);
    }
}