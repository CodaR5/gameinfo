package com.codar5.gameinfo.network;

import com.codar5.gameinfo.hud.HudOverlay;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class GameInfoNetwork {

    private static boolean payloadsRegistered = false;

    public static long chunkKey(int x, int z) {
        return (long) x & 0xFFFFFFFFL | ((long) z & 0xFFFFFFFFL) << 32;
    }

    public static void registerPayloads() {
        if (payloadsRegistered) return;
        payloadsRegistered = true;
        PayloadTypeRegistry.clientboundPlay().register(FurnaceDataPayload.TYPE, FurnaceDataPayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(SlimeChunkSyncPayload.TYPE, SlimeChunkSyncPayload.CODEC);
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

        ClientPlayNetworking.registerGlobalReceiver(SlimeChunkSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                HudOverlay.slimeChunkCache.put(
                        chunkKey(payload.chunkX(), payload.chunkZ()),
                        payload.isSlime()
                );
            });
        });
    }

    public static void sendToPlayer(ServerPlayer player, FurnaceDataPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToPlayer(ServerPlayer player, SlimeChunkSyncPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}