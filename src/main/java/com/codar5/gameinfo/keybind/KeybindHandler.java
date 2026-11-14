/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
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
    private static final int SDL_SCANCODE_G = 10;

    private static KeyMapping openGuiKeyBinding;
    public static KeyMapping ponderKeyBinding;

    public static void register() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(GameinfoClient.modName, "keybindings")
        );

        openGuiKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.gameinfo.open_gui", SDL_SCANCODE_U, category)
        );

        ponderKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.gameinfo.ponder", SDL_SCANCODE_G, category)
        );

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Minecraft mc = Minecraft.getInstance();
            while (openGuiKeyBinding.consumeClick()) {
                if (mc.gui.screen() == null) {
                    mc.gui.setScreen(new GameInfoConfigScreen(null));
                }
            }
        });
    }
}