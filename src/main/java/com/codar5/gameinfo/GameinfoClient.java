/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo;

import com.codar5.gameinfo.command.ChunkLoader;
import com.codar5.gameinfo.command.GameInfoCommand;
import com.codar5.gameinfo.command.SeedCommand;
import com.codar5.gameinfo.command.SeedChatListener;
import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.hud.BlockInfoHudRenderer;
import com.codar5.gameinfo.hud.EntityHealthHudRenderer;
import com.codar5.gameinfo.hud.HudOverlay;
import com.codar5.gameinfo.keybind.KeybindHandler;
import com.codar5.gameinfo.network.GameInfoNetwork;
import com.codar5.gameinfo.renderer.FurnaceEventHandler;
import com.codar5.gameinfo.util.GameInfoOptimizer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;

public class GameinfoClient implements ClientModInitializer {
    public static String modName = "gameinfo";
    public static GameInfoConfig config = new GameInfoConfig();
    public static GameInfoCommand command = new GameInfoCommand();
    public static ChunkLoader chunkLoader = new ChunkLoader();
    public static HudOverlay hudOverlay = new HudOverlay();
    public static BlockInfoHudRenderer blockInfoHudRenderer = new BlockInfoHudRenderer();
    public static EntityHealthHudRenderer entityHealthHudRenderer = new EntityHealthHudRenderer();

    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register(command);
        ClientCommandRegistrationCallback.EVENT.register(new SeedCommand());
        CommandRegistrationCallback.EVENT.register(chunkLoader);
        
        // 注册聊天拦截器，用于捕获 /giseed set 指令
        SeedChatListener.register();
        
        KeybindHandler.register();

        GameInfoNetwork.registerPayloads();
        GameInfoNetwork.registerClientReceiver();

        GameInfoOptimizer.register();

        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(modName, "custom_text"), hudOverlay);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(modName, "block_info"), blockInfoHudRenderer);
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath(modName, "entity_health"), entityHealthHudRenderer);
        
        FurnaceEventHandler.registerEvents();

        ClientPlayConnectionEvents.DISCONNECT.register((handler, c) -> {
            HudOverlay.slimeChunkCache.clear();
            HudOverlay.serverFurnaceCache.clear();
        });
    }

    public static void logger(String message) {
        System.out.println("[DEBUG]： " + message);
    }
}
