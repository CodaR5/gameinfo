/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.network;

import com.codar5.gameinfo.hud.HudOverlay;
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
                    HudOverlay.serverFurnaceCache.put(payload.pos(), new net.minecraft.world.item.ItemStack[]{
                            payload.input(), payload.fuel(), payload.output()
                    });
                })
        );
        
        registrar.playToServer(
                FurnaceDataPayload.TYPE,
                FurnaceDataPayload.CODEC,
                (payload, context) -> context.enqueueWork(() -> {
                    net.minecraft.server.level.ServerPlayer player = (net.minecraft.server.level.ServerPlayer) context.player();
                    if (player.level().getBlockEntity(payload.pos()) instanceof net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity furnace) {
                        net.neoforged.neoforge.network.PacketDistributor.sendToPlayer(player, new FurnaceDataPayload(
                                payload.pos(),
                                furnace.getItem(0),
                                furnace.getItem(1),
                                furnace.getItem(2)
                        ));
                    }
                })
        );
    }
}
