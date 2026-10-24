package com.codar5.gameinfo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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

public class ChunkLoader {
    private static final Map<String, Set<ChunkPos>> loadedChunks = new HashMap<>();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("chunk")
                .then(Commands.literal("load").executes(ChunkLoader::loadChunk))
                .then(Commands.literal("unload").executes(ChunkLoader::unloadChunk))
                .then(Commands.literal("list").executes(ChunkLoader::listChunks))
                .then(Commands.literal("check")
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                        .executes(ChunkLoader::checkChunk))))
        );
    }

    private static int loadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        BlockPos pos = source.getPlayer().blockPosition();
        ChunkPos chunkPos = ChunkPos.containing(pos);
        String worldKey = getWorldKey(world);
        loadedChunks.computeIfAbsent(worldKey, k -> new HashSet<>()).add(chunkPos);
        world.getChunkSource().updateChunkForced(chunkPos, true);
        source.sendSuccess(() -> Component.literal(String.format("§a已加载区块 [%d, %d] 在世界 %s", chunkPos.x(), chunkPos.z(), world.dimension().identifier())), true);
        return 1;
    }

    private static int unloadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        ServerPlayer player = source.getPlayer();
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

    private static int listChunks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        Set<ChunkPos> chunks = loadedChunks.get(getWorldKey(world));
        if (chunks == null || chunks.isEmpty()) {
            source.sendSuccess(() -> Component.literal("§e当前世界没有被此mod加载的区块"), false);
        } else {
            source.sendSuccess(() -> Component.literal("§a被此mod加载的区块:"), false);
            for (ChunkPos chunk : chunks) {
                source.sendSuccess(() -> Component.literal(String.format("§f  - [%d, %d]", chunk.x(), chunk.z())), false);
            }
        }
        return 1;
    }

    private static int checkChunk(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");
        ChunkPos chunkPos = ChunkPos.containing(new BlockPos(x, 0, z));
        boolean isLoaded = world.getChunkSource().hasChunk(chunkPos.x(), chunkPos.z());
        source.sendSuccess(() -> Component.literal(String.format("§f区块 [%d, %d] 状态: %s", chunkPos.x(), chunkPos.z(), isLoaded ? "§a已加载" : "§c未加载")), false);
        return 1;
    }

    private static String getWorldKey(Level world) {
        return world.dimension().identifier().toString();
    }
}