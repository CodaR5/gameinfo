package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.GameInfoNeoForge;
import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = GameInfoNeoForge.MOD_ID, value = Dist.CLIENT)
public class KeybindHandler {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(Identifier.fromNamespaceAndPath(GameInfoNeoForge.MOD_ID, "keybindings"));

    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.gameinfo.open_gui",
            InputConstants.Type.KEYBOARD,
            InputConstants.KEY_U,
            CATEGORY
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GUI_KEY);
        event.registerCategory(CATEGORY);
    }

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (OPEN_GUI_KEY.consumeClick()) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gui.screen() == null) {
                mc.gui.setScreen(new GameInfoConfigScreen(null));
            }
        }
    }
}