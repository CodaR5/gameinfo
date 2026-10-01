package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {


    public static void register() {
        registerOpenGui();
    }

    public static void registerOpenGui() {
        KeyMapping openGuiKeyBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.gameinfo.open_gui", // 翻译键
                GLFW.GLFW_KEY_U, // U键
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath(GameinfoClient.modName, "keybindings"))
        ));
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
