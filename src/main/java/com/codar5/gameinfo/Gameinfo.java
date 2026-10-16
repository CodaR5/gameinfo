package com.codar5.gameinfo;

import com.codar5.gameinfo.network.FurnaceDataPayload;
import com.codar5.gameinfo.network.GameInfoNetwork;
import com.codar5.gameinfo.network.SlimeChunkSyncPayload;
import com.codar5.gameinfo.util.RendererPreferenceWriter;
import com.codar5.gameinfo.util.SlimeChunkUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Gameinfo implements ModInitializer, PreLaunchEntrypoint {

    @Override
    public void onPreLaunch() {
        RendererPreferenceWriter.forceOpenGLPreference();
    }

    @Override
    public void onInitialize() {
        GameInfoNetwork.registerPayloads();

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (ServerPlayer sp : server.getPlayerList().getPlayers()) {
                if (!(sp.level() instanceof ServerLevel level)) continue;

                BlockPos center = sp.blockPosition();
                BlockPos min = center.offset(-6, -3, -6);
                BlockPos max = center.offset(6, 3, 6);
                for (BlockPos p : BlockPos.betweenClosed(min, max)) {
                    BlockEntity be = level.getBlockEntity(p);
                    if (be instanceof AbstractFurnaceBlockEntity f) {
                        GameInfoNetwork.sendToPlayer(sp, new FurnaceDataPayload(
                                p.immutable(),
                                f.getItem(0).copy(),
                                f.getItem(1).copy(),
                                f.getItem(2).copy()
                        ));
                    }
                }

                if (sp.tickCount % 10 == 0) {
                    BlockPos bp = sp.blockPosition();
                    int cx = bp.getX() >> 4;
                    int cz = bp.getZ() >> 4;
                    long seed = level.getSeed();
                    boolean isSlime = SlimeChunkUtil.isSlimeChunk(seed, cx, cz);
                    GameInfoNetwork.sendToPlayer(sp,
                            new SlimeChunkSyncPayload(cx, cz, isSlime));
                }
            }
        });
    }
}