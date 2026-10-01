package com.codar5.gameinfo.network;

import com.codar5.gameinfo.hud.HudOverlay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GameInfoNetwork {

    public static void registerPayloads() {
        PayloadTypeRegistry.clientboundPlay().register(FurnaceDataPayload.TYPE, FurnaceDataPayload.CODEC);
    }

    public static void registerClientReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(FurnaceDataPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                HudOverlay.serverFurnaceCache.put(
                        payload.pos(),
                        new ItemStack[]{payload.input(), payload.fuel(), payload.output()}
                );
            });
        });
    }

    public static void sendToPlayer(ServerPlayer player, FurnaceDataPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}