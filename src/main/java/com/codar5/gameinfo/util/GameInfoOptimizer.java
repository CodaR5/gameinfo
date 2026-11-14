/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.util;

import com.codar5.gameinfo.hud.HudOverlay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

/**
 * GameInfo 内置的性能优化器。
 * 旨在减少内存分配、释放无用缓存，提升 Minecraft 整体运行帧率。
 */
public class GameInfoOptimizer {
    private static int tickCounter = 0;

    /**
     * 注册优化事件。
     * 必须在客户端初始化时调用。
     */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;

            // 每 600 tick（约 30 秒）清理一次不必要的缓存，防止内存泄漏引发掉帧
            if (tickCounter >= 600) {
                tickCounter = 0;
                cleanupCaches(client);
            }
        });
    }

    private static void cleanupCaches(Minecraft client) {
        // 清理玩家离开世界后残留的 HUD 缓存
        if (client.level == null) {
            HudOverlay.serverFurnaceCache.clear();
            HudOverlay.slimeChunkCache.clear();
        } else {
            // 如果熔炉缓存过大（比如你跑图看了一万个熔炉），清理一半，释放显存/内存
            if (HudOverlay.serverFurnaceCache.size() > 128) {
                HudOverlay.serverFurnaceCache.clear();
            }
        }
    }

    /**
     * 极速拼接器，替代原版耗时的 String.format。
     * 用于 HUD 渲染，减少 CPU 和 GC 压力。
     */
    public static String fastFormat(String prefix, int val1, int val2, int val3) {
        return prefix + val1 + " " + val2 + " " + val3;
    }
}