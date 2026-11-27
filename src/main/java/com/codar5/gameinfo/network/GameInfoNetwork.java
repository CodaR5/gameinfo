/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.network;

import com.codar5.gameinfo.hud.HudOverlay;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = "gameinfo")
public class GameInfoNetwork {
    private static boolean payloadsRegistered = false;

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        if (payloadsRegistered) return;
        payloadsRegistered = true;

        PayloadRegistrar registrar = event.registrar("1");
        
        registrar.playToClient(
                FurnaceDataPayload.TYPE,
                FurnaceDataPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    HudOverlay.serverFurnaceCache.put(payload.pos(), new ItemStack[]{
                            payload.input(), payload.fuel(), payload.output()
                    });
                })
        );
        
        registrar.playToClient(
                SlimeChunkSyncPayload.TYPE,
                SlimeChunkSyncPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    long key = ((long) payload.cx() & 0xFFFFFFFFL) | (((long) payload.cz() & 0xFFFFFFFFL) << 32);
                    HudOverlay.slimeChunkCache.put(key, payload.isSlime());
                })
        );
    }
}
