package com.codar5.gameinfo.network;

import com.codar5.gameinfo.GameinfoClient;
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
        GameinfoClient.config.serverSeeds.put(key, seed);
        GameinfoClient.config.saveConfig();
    }

    public static void clearManual() {
        String key = getServerKey();
        GameinfoClient.config.serverSeeds.remove(key);
        GameinfoClient.config.saveConfig();
    }

    public static long resolve() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.hasSingleplayerServer() && mc.getSingleplayerServer() != null) {
            long seed = mc.getSingleplayerServer().overworld().getSeed();
            if (seed != 0L) return seed;
        }

        String key = getServerKey();
        return GameinfoClient.config.serverSeeds.getOrDefault(key, 0L);
    }
}
