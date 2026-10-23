package com.codar5.gameinfo.network;

import com.codar5.gameinfo.hud.HudOverlay;
import com.codar5.gameinfo.renderer.FurnaceDataCache;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class GameInfoNetwork {
    private static boolean payloadsRegistered = false;

    public static void registerPayloads() {
        if (payloadsRegistered) return;
        payloadsRegistered = true;

        PayloadTypeRegistry.playS2C().register(SlimeChunkSyncPayload.TYPE, SlimeChunkSyncPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(FurnaceDataPayload.TYPE, FurnaceDataPayload.CODEC);
    }

    public static void registerClientReceivers() {
        ClientPlayNetworking.registerGlobalReceiver(SlimeChunkSyncPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                long key = HudOverlay.chunkKey(payload.cx(), payload.cz());
                HudOverlay.slimeChunkCache.put(key, payload.isSlime());
            });
        });

        ClientPlayNetworking.registerGlobalReceiver(FurnaceDataPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                FurnaceDataCache.updateFurnaceData(payload.pos(), payload.input(), payload.fuel(), payload.output());
            });
        });
    }
}