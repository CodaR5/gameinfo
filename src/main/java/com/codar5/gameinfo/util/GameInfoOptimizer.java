/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.util;

import com.codar5.gameinfo.hud.HudOverlay;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;

public class GameInfoOptimizer {
    private static int tickCounter = 0;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            tickCounter++;
            if (tickCounter >= 600) {
                tickCounter = 0;
                cleanupCaches(client);
            }
        });
    }

    private static void cleanupCaches(Minecraft client) {
        if (client.level == null) {
            HudOverlay.serverFurnaceCache.clear();
            HudOverlay.slimeChunkCache.clear();
        } else {
            if (HudOverlay.serverFurnaceCache.size() > 128) {
                HudOverlay.serverFurnaceCache.clear();
            }
        }
    }

    public static String fastFormat(String prefix, int val1, int val2, int val3) {
        return prefix + val1 + " " + val2 + " " + val3;
    }
}