package com.codar5.gameinfo.renderer;

import com.codar5.gameinfo.network.FurnaceDataPayload;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class FurnaceEventHandler {

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                HitResult hitResult = player.pick(5.0D, 0.0F, false);
                if (hitResult.getType() == HitResult.Type.BLOCK) {
                    BlockPos pos = ((BlockHitResult) hitResult).getBlockPos();
                    if (player.level().getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity furnace) {
                        FurnaceDataPayload payload = new FurnaceDataPayload(
                                pos,
                                furnace.getItem(0),
                                furnace.getItem(1),
                                furnace.getItem(2)
                        );
                        ServerPlayNetworking.send(player, payload);
                    }
                }
            }
        });
    }
}