package com.codar5.gameinfo.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
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
                .then(Commands.literal("load")
                        .executes(this::loadChunk))
                .then(Commands.literal("unload")
                        .executes(this::unloadChunk))
                .then(Commands.literal("list")
                        .executes(this::listChunks))
                .then(Commands.literal("check")
                        .then(Commands.argument("x", IntegerArgumentType.integer())
                                .then(Commands.argument("z", IntegerArgumentType.integer())
                                        .executes(this::checkChunk))))
        );
    }

    private int loadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        BlockPos pos = source.getPlayer().blockPosition();
        ChunkPos chunkPos = new ChunkPos(pos);
        String worldKey = getWorldKey(world);

        loadedChunks.computeIfAbsent(worldKey, k -> new HashSet<>()).add(chunkPos);

        world.getChunkSource().updateChunkForced(chunkPos, true);

        source.sendSuccess(() -> Component.literal(
                        String.format("§a已加载区块 [%d, %d] 在世界 %s",
                                chunkPos.x, chunkPos.z, world.dimension().location())),
                true);

        return 1;
    }

    private int unloadChunk(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        ServerPlayer player = source.getPlayer();

        ChunkPos chunkPos = new ChunkPos(player.blockPosition());
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks != null && chunks.remove(chunkPos)) {
            world.getChunkSource().updateChunkForced(chunkPos, false);
            source.sendSuccess(() -> Component.literal(
                            String.format("§c已取消加载区块 [%d, %d] 在世界 %s",
                                    chunkPos.x, chunkPos.z, world.dimension().location())),
                    true);

            if (chunks.isEmpty()) {
                loadedChunks.remove(worldKey);
            }
        } else {
            source.sendSuccess(() -> Component.literal(
                            String.format("§e区块 [%d, %d] 未被此mod加载",
                                    chunkPos.x, chunkPos.z)),
                    false);
        }

        return 1;
    }

    private int listChunks(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        if (chunks == null || chunks.isEmpty()) {
            source.sendSuccess(() -> Component.literal(
                            String.format("§e当前世界 %s 没有被此mod加载的区块",
                                    world.dimension().location())),
                    false);
        } else {
            source.sendSuccess(() -> Component.literal(
                            String.format("§a世界 %s 中被此mod加载的区块:",
                                    world.dimension().location())),
                    false);

            for (ChunkPos chunk : chunks) {
                source.sendSuccess(() -> Component.literal(
                                String.format("§f  - [%d, %d]", chunk.x, chunk.z)),
                        false);
            }
        }

        return 1;
    }

    private int checkChunk(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        Level world = source.getLevel();
        int x = IntegerArgumentType.getInteger(context, "x");
        int z = IntegerArgumentType.getInteger(context, "z");
        BlockPos blockPos = new BlockPos(x, 0, z);
        ChunkPos chunkPos = new ChunkPos(blockPos);
        String worldKey = getWorldKey(world);

        Set<ChunkPos> chunks = loadedChunks.get(worldKey);
        boolean isThisMod = chunks != null && chunks.contains(chunkPos);
        boolean isLoaded = world.getChunkSource().hasChunk(chunkPos.x, chunkPos.z);

        source.sendSuccess(() -> Component.literal(
                        String.format("§f区块 [%d, %d] 状态检查: 是否加载: %b, 是否被此mod设置为永久加载: %b", chunkPos.x, chunkPos.z, isLoaded, isThisMod)),
                false);

        int loadedChunkCount = world.getChunkSource().getLoadedChunksCount();

        if (isLoaded) {
            source.sendSuccess(() -> Component.literal(
                            String.format("§a区块 [%d, %d] 已加载，当前已加载区块数量为：%d", chunkPos.x, chunkPos.z, loadedChunkCount)),
                    false);
        } else {
            source.sendSuccess(() -> Component.literal(
                            String.format("§c区块 [%d, %d] 未加载", chunkPos.x, chunkPos.z)),
                    false);
        }

        return 1;
    }

    private String getWorldKey(Level world) {
        return world.dimension().location().toString();
    }
}