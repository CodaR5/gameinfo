package com.codar5.gameinfo;

import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.hud.HudOverlay;
import com.codar5.gameinfo.keybind.KeybindHandler;
import com.codar5.gameinfo.network.GameInfoNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.resources.Identifier;

public class GameinfoClient implements ClientModInitializer {
    public static GameInfoConfig config;

    @Override
    public void onInitializeClient() {
        config = new GameInfoConfig();

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                Identifier.parse("gameinfo:hud_overlay"),
                new HudOverlay()
        );

        GameInfoNetwork.registerClientReceivers();
        KeybindHandler.register();
    }

    public static void logger(String message) {
        System.out.println("[GameInfo] " + message);
    }
}