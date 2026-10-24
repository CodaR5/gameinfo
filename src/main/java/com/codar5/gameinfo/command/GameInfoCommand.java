package com.codar5.gameinfo.command;

import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.GameInfoNeoForge;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GameInfoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gameinfo")
                .then(Commands.literal("fps").executes(ctx -> toggle(ctx, "fps")))
                .then(Commands.literal("time").executes(ctx -> toggle(ctx, "time")))
                .then(Commands.literal("coordinates").executes(ctx -> toggle(ctx, "coordinates")))
                .then(Commands.literal("nether").executes(ctx -> toggle(ctx, "nether")))
                .then(Commands.literal("biome").executes(ctx -> toggle(ctx, "biome")))
                .then(Commands.literal("equipment").executes(ctx -> toggle(ctx, "equipment")))
                .then(Commands.literal("slime").executes(ctx -> toggle(ctx, "slime")))
                .executes(ctx -> {
                    ctx.getSource().sendSuccess(() -> Component.literal("§aGameInfo Commands: fps, time, coordinates, nether, biome, equipment, slime"), false);
                    return 1;
                })
        );
    }

    private static int toggle(com.mojang.brigadier.context.CommandContext<CommandSourceStack> ctx, String type) {
        GameInfoConfig config = GameInfoNeoForge.config;
        switch (type) {
            case "fps": config.showFPS = !config.showFPS; break;
            case "time": config.showTimeAndDays = !config.showTimeAndDays; break;
            case "coordinates": config.showCoordinates = !config.showCoordinates; break;
            case "nether": config.showNetherCoordinates = !config.showNetherCoordinates; break;
            case "biome": config.showBiome = !config.showBiome; break;
            case "equipment": config.showEquipment = !config.showEquipment; break;
            case "slime": config.showSlimeChunk = !config.showSlimeChunk; break;
        }
        config.saveConfig();
        ctx.getSource().sendSuccess(() -> Component.literal("§aToggled " + type), false);
        return 1;
    }
}