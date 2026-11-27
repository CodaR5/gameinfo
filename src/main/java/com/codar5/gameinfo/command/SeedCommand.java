/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.command;

import com.codar5.gameinfo.network.WorldSeedStore;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;

public class SeedCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("giseed")
                .then(Commands.literal("set")
                        .then(Commands.argument("seed", LongArgumentType.longArg())
                                .executes(context -> {
                                    long seed = LongArgumentType.getLong(context, "seed");
                                    WorldSeedStore.setManual(seed);
                                    feedback("§aGameinfo: slime-chunk seed " + seed + " saved for this server");
                                    return 1;
                                })))
                .then(Commands.literal("clear")
                        .executes(context -> {
                            WorldSeedStore.clearManual();
                            feedback("§eGameinfo: saved seed cleared for this server");
                            return 1;
                        }))
                .then(Commands.literal("show")
                        .executes(context -> {
                            long seed = WorldSeedStore.resolve();
                            if (seed == 0L) {
                                feedback("§cGameinfo: no seed known for this server");
                            } else {
                                feedback("§aGameinfo: using slime-chunk seed " + seed);
                            }
                            return 1;
                        }))
        );
    }

    private static void feedback(String message) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(message));
        }
    }
}
