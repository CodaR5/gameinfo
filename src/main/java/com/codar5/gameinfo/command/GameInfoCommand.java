package com.codar5.gameinfo.command;

import com.codar5.gameinfo.GameinfoClient;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class GameInfoCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("gameinfo")
                .then(Commands.literal("fps").then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean val = BoolArgumentType.getBool(context, "value");
                    GameinfoClient.config.showFPS = val;
                    GameinfoClient.config.saveConfig();
                    context.getSource().sendSuccess(() -> Component.literal("FPS display: " + val), false);
                    return 1;
                })))
                .then(Commands.literal("time").then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean val = BoolArgumentType.getBool(context, "value");
                    GameinfoClient.config.showTimeAndDays = val;
                    GameinfoClient.config.saveConfig();
                    context.getSource().sendSuccess(() -> Component.literal("Time display: " + val), false);
                    return 1;
                })))
                .then(Commands.literal("coords").then(Commands.argument("value", BoolArgumentType.bool()).executes(context -> {
                    boolean val = BoolArgumentType.getBool(context, "value");
                    GameinfoClient.config.showCoordinates = val;
                    GameinfoClient.config.saveConfig();
                    context.getSource().sendSuccess(() -> Component.literal("Coords display: " + val), false);
                    return 1;
                })))
        );
    }
}