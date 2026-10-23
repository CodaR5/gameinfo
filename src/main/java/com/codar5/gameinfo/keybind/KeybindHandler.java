package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {
    public static KeyMapping openGuiKey;

    public static void register() {
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.gameinfo.open_gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_U,
                "category.gameinfo"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new GameInfoConfigScreen(null));
                }
            }
        });
    }
}