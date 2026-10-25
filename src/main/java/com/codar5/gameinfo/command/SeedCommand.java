package com.codar5.gameinfo.command;

import com.codar5.gameinfo.network.WorldSeedStore;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class SeedCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, net.minecraft.commands.CommandBuildContext buildContext) {
        dispatcher.register(ClientCommands.literal("giseed")
                .then(ClientCommands.literal("set")
                        .then(ClientCommands.argument("seed", LongArgumentType.longArg())
                                .executes(context -> {
                                    long seed = LongArgumentType.getLong(context, "seed");
                                    WorldSeedStore.setManual(seed);
                                    feedback(context.getSource(), "Gameinfo: slime-chunk seed " + seed + " saved for this server");
                                    return 1;
                                })))
                .then(ClientCommands.literal("clear")
                        .executes(context -> {
                            WorldSeedStore.clearManual();
                            feedback(context.getSource(), "Gameinfo: saved seed cleared for this server");
                            return 1;
                        }))
                .then(ClientCommands.literal("show")
                        .executes(context -> {
                            long seed = WorldSeedStore.resolve();
                            if (seed == 0L) {
                                feedback(context.getSource(), "Gameinfo: no seed known for this server (overlay stays off)");
                            } else {
                                feedback(context.getSource(), "Gameinfo: using slime-chunk seed " + seed);
                            }
                            return 1;
                        }))
        );
    }

    private static void feedback(FabricClientCommandSource source, String message) {
        source.sendFeedback(Component.literal(message));
    }
}
