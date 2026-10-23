package com.codar5.gameinfo;

import com.codar5.gameinfo.command.ChunkLoader;
import com.codar5.gameinfo.command.GameInfoCommand;
import com.codar5.gameinfo.network.GameInfoNetwork;
import com.codar5.gameinfo.network.SlimeChunkSyncPayload;
import com.codar5.gameinfo.util.SlimeChunkUtil;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import com.codar5.gameinfo.renderer.FurnaceEventHandler;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Gameinfo implements ModInitializer {
    public static final String MOD_ID = "gameinfo";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        GameInfoNetwork.registerPayloads();
        FurnaceEventHandler.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            GameInfoCommand.register(dispatcher);
            new ChunkLoader().register(dispatcher, registryAccess, environment);
        });

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (server.getTickCount() % 10 == 0) {
                long seed = server.overworld().getSeed();
                if (seed != 0L) {
                    for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                        ChunkPos pos = player.chunkPosition();
                        boolean isSlime = SlimeChunkUtil.isSlimeChunk(seed, pos.x, pos.z);
                        ServerPlayNetworking.send(player, new SlimeChunkSyncPayload(pos.x, pos.z, isSlime));
                    }
                }
            }
        });
    }
}