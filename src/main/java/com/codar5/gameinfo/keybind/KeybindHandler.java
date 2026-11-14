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

/**
 * 按键绑定处理类。
 * 负责注册快捷键并处理按键触发事件。
 */
public class KeybindHandler {

    // U 键的 SDL 扫描码
    private static final int SDL_SCANCODE_U = 24;

    // 打开 GUI 的按键绑定
    private static KeyMapping openGuiKeyBinding;

    // 长按思索的按键绑定，需设为 public 以便在其他界面检测按键状态
    public static KeyMapping ponderKeyBinding;

    /**
     * 注册所有按键绑定和客户端 Tick 事件。
     */
    public static void register() {
        // 注册按键分类
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath(GameinfoClient.modName, "keybindings")
        );

        // 注册打开 GUI 的按键
        openGuiKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.gameinfo.open_gui", SDL_SCANCODE_U, category)
        );

        // 注册长按思索的按键，使用 G 键
        ponderKeyBinding = KeyMappingHelper.registerKeyMapping(
                new KeyMapping("key.gameinfo.ponder", 10, category)
        );

        // 注册客户端 Tick 事件，用于处理按键逻辑
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Minecraft mc = Minecraft.getInstance();

            // 处理打开 GUI 的按键点击
            while (openGuiKeyBinding.consumeClick()) {
                if (mc.gui.screen() == null) {
                    mc.gui.setScreen(new GameInfoConfigScreen(null));
                }
            }
        });
    }
}