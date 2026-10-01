package com.codar5.gameinfo.command;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.util.HexUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;

public class GameInfoCommand implements ClientCommandRegistrationCallback {

    @Override
    public void register(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandRegistryAccess) {
        LiteralArgumentBuilder<FabricClientCommandSource> root = ClientCommands.literal("gameinfo");

        addBoolean(root, "fps", v -> GameinfoClient.config.showFPS = v);
        addBoolean(root, "time", v -> GameinfoClient.config.showTimeAndDays = v);
        addBoolean(root, "coords", v -> GameinfoClient.config.showCoordinates = v);
        addBoolean(root, "nether", v -> GameinfoClient.config.showNetherCoordinates = v);
        addBoolean(root, "biome", v -> GameinfoClient.config.showBiome = v);
        addBoolean(root, "remark", v -> GameinfoClient.config.remark = v);
        addBoolean(root, "enable", v -> GameinfoClient.config.enabled = v);

        addInteger(root, "xpos", v -> GameinfoClient.config.xPos = v);
        addInteger(root, "ypos", v -> GameinfoClient.config.yPos = v);

        addColor(root, "color");

        dispatcher.register(root);
    }

    private void addBoolean(LiteralArgumentBuilder<FabricClientCommandSource> root, String name, Consumer<Boolean> setter) {
        root.then(ClientCommands.literal(name)
                .then(ClientCommands.argument("state", BoolArgumentType.bool())
                        .executes(ctx -> {
                            setter.accept(BoolArgumentType.getBool(ctx, "state"));
                            GameinfoClient.config.saveConfig();
                            return 1;
                        })));
    }

    private void addInteger(LiteralArgumentBuilder<FabricClientCommandSource> root, String name, IntConsumer setter) {
        root.then(ClientCommands.literal(name)
                .then(ClientCommands.argument("position", IntegerArgumentType.integer())
                        .executes(ctx -> {
                            setter.accept(IntegerArgumentType.getInteger(ctx, "position"));
                            GameinfoClient.config.saveConfig();
                            return 1;
                        })));
    }

    private void addColor(LiteralArgumentBuilder<FabricClientCommandSource> root, String name) {
        root.then(ClientCommands.literal(name)
                .then(ClientCommands.argument("color", StringArgumentType.string())
                        .executes(ctx -> {
                            String color = StringArgumentType.getString(ctx, "color");
                            GameinfoClient.config.color = HexUtil.toDecimal("0xFF" + color);
                            GameinfoClient.config.saveConfig();
                            return 1;
                        })));
    }
}