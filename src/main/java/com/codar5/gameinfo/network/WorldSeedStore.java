/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.network;

import com.codar5.gameinfo.GameInfoNeoForge;
import net.minecraft.client.Minecraft;

public class WorldSeedStore {
    private static String getServerKey() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer()) {
            return "singleplayer";
        }
        if (mc.getCurrentServer() != null) {
            return mc.getCurrentServer().ip;
        }
        return "unknown";
    }

    public static void setManual(long seed) {
        String key = getServerKey();
        GameInfoNeoForge.config.serverSeeds.put(key, seed);
        GameInfoNeoForge.config.saveConfig();
    }

    public static void clearManual() {
        String key = getServerKey();
        GameInfoNeoForge.config.serverSeeds.remove(key);
        GameInfoNeoForge.config.saveConfig();
    }

    public static long resolve() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
            long seed = mc.getSingleplayerServer().overworld().getSeed();
            if (seed != 0L) return seed;
        }
        String key = getServerKey();
        return GameInfoNeoForge.config.serverSeeds.getOrDefault(key, 0L);
    }
}