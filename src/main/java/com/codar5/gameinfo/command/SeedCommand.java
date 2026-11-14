/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.command;

import com.codar5.gameinfo.network.WorldSeedStore;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

public class SeedCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext buildContext) {
        // 移除所有带参数的 argument，只保留纯文本指令
        dispatcher.register(ClientCommands.literal("giseed")
                .then(ClientCommands.literal("clear")
                        .executes(context -> {
                            WorldSeedStore.clearManual();
                            feedback("Gameinfo: saved seed cleared for this server");
                            return 1;
                        }))
                .then(ClientCommands.literal("show")
                        .executes(context -> {
                            long seed = WorldSeedStore.resolve();
                            if (seed == 0L) {
                                feedback("Gameinfo: no seed known for this server");
                            } else {
                                feedback("Gameinfo: using slime-chunk seed " + seed);
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
