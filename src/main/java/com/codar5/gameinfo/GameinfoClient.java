package com.codar5.gameinfo;

import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.hud.HudOverlay;
import com.codar5.gameinfo.keybind.KeybindHandler;
import com.codar5.gameinfo.network.GameInfoNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;

public class GameinfoClient implements ClientModInitializer {
    public static GameInfoConfig config;

    @Override
    public void onInitializeClient() {
        config = new GameInfoConfig();
        KeybindHandler.register();
        GameInfoNetwork.registerPayloads();
        GameInfoNetwork.registerClientReceivers();

        HudRenderCallback.EVENT.register((guiGraphics, tickDelta) -> {
            if (Minecraft.getInstance().player != null && !Minecraft.getInstance().options.hideGui) {
                HudOverlay.render(guiGraphics, tickDelta);
            }
        });
    }

    public static void logger(String message) {
        System.out.println("[GameInfo] " + message);
    }
}