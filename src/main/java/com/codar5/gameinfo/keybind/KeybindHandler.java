package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;

public class KeybindHandler {

    private static final int SDL_SCANCODE_U = 24;

    public static void register() {
        KeyMapping openGuiKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping(
                        "key.gameinfo.open_gui",
                        SDL_SCANCODE_U,
                        KeyMapping.Category.register(Identifier.fromNamespaceAndPath(GameinfoClient.modName, "keybindings"))
                )
        );
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKeyBinding.consumeClick()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.gui.screen() == null) {
                    mc.gui.setScreen(new GameInfoConfigScreen(null));
                }
            }
        });
    }
}