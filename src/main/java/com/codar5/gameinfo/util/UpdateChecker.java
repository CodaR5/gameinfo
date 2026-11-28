/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UpdateChecker {
    private static final String MOD_ID = "gameinfo";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/CodaR5/gameinfo/releases/latest";
    private static final String CURRENT_VERSION = "4.0.8.alpha";

    public static void checkForUpdates() {
        Thread thread = new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(GITHUB_API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Accept", "application/vnd.github.v3+json");
                connection.setRequestProperty("User-Agent", MOD_ID + "-UpdateChecker");

                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        handleResponse(response.toString());
                    }
                }
            } catch (Exception e) {
                System.err.println("[GameInfo] Failed to check for updates: " + e.getMessage());
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private static void handleResponse(String responseBody) {
        try {
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(responseBody, JsonObject.class);
            
            if (json != null && json.has("tag_name")) {
                String latestVersion = json.get("tag_name").getAsString();
                String htmlUrl = json.get("html_url").getAsString();

                if (isNewerVersion(latestVersion, CURRENT_VERSION)) {
                    String message = String.format("§e[GameInfo] §a发现新版本! §7当前: %s §7-> §a最新: %s\n§b下载链接: %s",
                            CURRENT_VERSION, latestVersion, htmlUrl);
                    
                    Minecraft.getInstance().execute(() -> {
                        if (Minecraft.getInstance().player != null) {
                            Minecraft.getInstance().player.sendSystemMessage(Component.literal(message));
                        }
                    });
                }
            }
        } catch (Exception e) {
            System.err.println("[GameInfo] Failed to parse update response: " + e.getMessage());
        }
    }

    private static boolean isNewerVersion(String latest, String current) {
        String latestClean = latest.replaceFirst("^[vV]", "");
        String currentClean = current.replaceFirst("^[vV]", "");
        
        try {
            String[] latestParts = latestClean.split("[.-]");
            String[] currentParts = currentClean.split("[.-]");

            int length = Math.max(latestParts.length, currentParts.length);
            for (int i = 0; i < length; i++) {
                int latestPart = i < latestParts.length ? parseIntSafe(latestParts[i]) : 0;
                int currentPart = i < currentParts.length ? parseIntSafe(currentParts[i]) : 0;
                
                if (latestPart > currentPart) return true;
                if (latestPart < currentPart) return false;
            }
        } catch (Exception e) {
            return !latestClean.equals(currentClean);
        }
        return false;
    }

    private static int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
