/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.util;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class UpdateChecker {
    private static final String MOD_ID = "gameinfo";
    private static final String GITHUB_API_URL = "https://api.github.com/repos/CodaR5/gameinfo/releases/latest";
    private static final String CURRENT_VERSION = FabricLoader.getInstance()
            .getModContainer(MOD_ID)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("unknown");

    public static void checkForUpdates() {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GITHUB_API_URL))
                .timeout(Duration.ofSeconds(10))
                .header("Accept", "application/vnd.github.v3+json")
                .header("User-Agent", MOD_ID + "-UpdateChecker")
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenAccept(UpdateChecker::handleResponse)
                .exceptionally(e -> {
                    System.err.println("[GameInfo] Failed to check for updates: " + e.getMessage());
                    return null;
                });
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
