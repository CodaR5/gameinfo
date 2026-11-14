/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ChunkLoader implements CommandRegistrationCallback {
    private static final Map<String, Set<ChunkPos>> loadedChunks = new HashMap<>();

    @Override
    public void register(CommandDispatcher<CommandSourceStack> commandDispatcher, CommandBuildContext commandRegistryAccess, Commands.CommandSelection registrationEnvironment) {
        commandDispatcher.register(Commands.literal("chunk")
                .then(Commands.literal("load").executes(this::loadChunk))
                .then(Commands.literal("unload").executes(this::unloadChunk))
                .then(Commands.literal("list").executes(this::listChunks))
                .then(Commands.literal("check").executes(this::checkChunk)) // 去掉参数，直接检测脚下
        );
    }

    private int loadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        
        BlockPos pos = player.blockPosition();
        ChunkPos chunkPos = ChunkPos.containing(pos);
        String worldKey = getWorldKey(world);

        loadedChunks.computeIfAbsent(worldKey, k -> new HashSet<>()).add(chunkPos);
        world.getChunkSource().updateChunkForced(chunkPos, true);

        source.sendSuccess(() -> Component.literal(String.format("§a已加载区块 [%d, %d]", chunkPos.x(), chunkPos.z())), true);
        return 1;
    }

    private int unloadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        ChunkPos chunkPos = ChunkPos.containing(player.blockPosition());
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks != null && chunks.remove(chunkPos)) {
            world.getChunkSource().updateChunkForced(chunkPos, false);
            source.sendSuccess(() -> Component.literal(String.format("§c已取消加载区块 [%d, %d]", chunkPos.x(), chunkPos.z())), true);
            if (chunks.isEmpty()) loadedChunks.remove(worldKey);
        } else {
            source.sendSuccess(() -> Component.literal(String.format("§e区块 [%d, %d] 未被此mod加载", chunkPos.x(), chunkPos.z())), false);
        }
        return 1;
    }

    private int listChunks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks == null || chunks.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e当前世界没有被此mod加载的区块"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§a当前世界中加载的区块:"), false);
            for (ChunkPos chunk : chunks) {
                source.sendSuccess(() -> Component.literal(String.format("§f - [%d, %d]", chunk.x(), chunk.z())), false);
            }
        }
        return 1;
    }

    private int checkChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;
        
        Level world = player.level();
        ChunkPos chunkPos = ChunkPos.containing(player.blockPosition());
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        boolean isThisMod = chunks != null && chunks.contains(chunkPos);
        boolean isLoaded = world.getChunkSource().hasChunk(chunkPos.x(), chunkPos.z());

        source.sendSuccess(() -> Component.literal(String.format("§f当前区块 [%d, %d] 状态: %s, Mod锁定: %b", chunkPos.x(), chunkPos.z(), isLoaded ? "§a已加载" : "§c未加载", isThisMod)), false);
        return 1;
    }

    private String getWorldKey(Level world) {
        return world.dimension().identifier().toString();
    }
}
