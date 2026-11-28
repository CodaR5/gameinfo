/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModList;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;

public final class UpdateChecker {
    public enum Status { IDLE, CHECKING, UP_TO_DATE, UPDATE_AVAILABLE, FAILED }

    private static final String MOD_ID = "gameinfo";
    private static final String ENDPOINT = "https://api.github.com/repos/CodaR5/gameinfo/releases/latest";
    private static final long CHECK_INTERVAL_MS = 6L * 60L * 60L * 1000L;
    private static final long FAILURE_RETRY_MS = 10L * 60L * 1000L;
    private static final File CACHE_FILE = new File("config" + File.separator + "gameinfo-update.json");

    private static volatile Status status = Status.IDLE;
    private static volatile String latestTag = null;
    private static volatile String latestVersion = null;
    private static volatile String downloadUrl = null;
    private static volatile long lastCheckTime = 0L;
    private static volatile boolean announced = false;
    private static volatile boolean manualPending = false;
    private static volatile boolean checkInProgress = false;
    private static boolean cacheLoaded = false;

    private UpdateChecker() {
    }

    public static String getCurrentVersion() {
        try {
            return ModList.get().getModContainerById(MOD_ID)
                    .map(container -> container.getModInfo().getVersion().toString())
                    .orElse("0.0.0");
        } catch (Throwable t) {
            return "0.0.0";
        }
    }

    public static Status getStatus() {
        return status;
    }

    public static boolean hasUpdate() {
        return status == Status.UPDATE_AVAILABLE;
    }

    public static String getLatestVersion() {
        return latestVersion;
    }

    public static String getLatestTag() {
        return latestTag;
    }

    public static String getDownloadUrl() {
        return downloadUrl;
    }

    public static void checkForUpdates() {
        loadCache();
        if (checkInProgress) return;
        if (System.currentTimeMillis() - lastCheckTime < CHECK_INTERVAL_MS) return;
        startCheck();
    }

    public static void requestManualCheck() {
        loadCache();
        manualPending = true;
        latestTag = null;
        latestVersion = null;
        downloadUrl = null;
        startCheck();
    }

    private static void startCheck() {
        if (checkInProgress) return;
        checkInProgress = true;
        status = Status.CHECKING;
        Thread thread = new Thread(UpdateChecker::runCheck, "GameInfo-UpdateChecker");
        thread.setDaemon(true);
        thread.start();
    }

    private static void runCheck() {
        boolean success = false;
        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) new URL(ENDPOINT).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(8000);
            connection.setReadTimeout(8000);
            connection.setRequestProperty("Accept", "application/vnd.github+json");
            connection.setRequestProperty("X-GitHub-Api-Version", "2022-11-28");
            connection.setRequestProperty("User-Agent", MOD_ID + "/" + getCurrentVersion());

            int code = connection.getResponseCode();
            if (code == HttpURLConnection.HTTP_OK) {
                StringBuilder body = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        body.append(line);
                    }
                }
                success = parse(body.toString());
                if (!success) {
                    System.err.println("[GameInfo] Update payload did not contain a usable tag_name");
                    status = Status.FAILED;
                }
            } else {
                System.err.println("[GameInfo] Update check returned HTTP " + code + (code == 403 ? " (rate limited)" : ""));
                status = Status.FAILED;
            }
        } catch (Throwable t) {
            System.err.println("[GameInfo] Update check failed: " + t);
            status = Status.FAILED;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            long now = System.currentTimeMillis();
            lastCheckTime = success ? now : Math.max(0L, now - CHECK_INTERVAL_MS + FAILURE_RETRY_MS);
            checkInProgress = false;
            saveCache();
        }
    }

    private static boolean parse(String body) {
        JsonObject json;
        try {
            JsonElement element = JsonParser.parseString(body);
            if (element == null || !element.isJsonObject()) return false;
            json = element.getAsJsonObject();
        } catch (Throwable t) {
            System.err.println("[GameInfo] Failed to parse update payload: " + t.getMessage());
            return false;
        }

        if (!json.has("tag_name") || json.get("tag_name").isJsonNull()) return false;
        String tag = json.get("tag_name").getAsString();
        String url = json.has("html_url") && !json.get("html_url").isJsonNull() ? json.get("html_url").getAsString() : null;

        latestTag = tag;
        latestVersion = normalize(tag);
        downloadUrl = url;
        status = isNewer(latestVersion, normalize(getCurrentVersion())) ? Status.UPDATE_AVAILABLE : Status.UP_TO_DATE;
        return true;
    }

    private static String normalize(String raw) {
        if (raw == null) return "0";
        String value = raw.trim();
        value = value.replaceFirst("^[vV]", "");
        value = value.replaceFirst("(?i)^release[-_ ]?", "");
        value = value.replaceFirst("^[vV]", "");
        int plus = value.indexOf('+');
        if (plus > 0) {
            value = value.substring(0, plus);
        }
        return value.isEmpty() ? "0" : value;
    }

    private static boolean isNewer(String candidate, String current) {
        try {
            return new DefaultArtifactVersion(candidate).compareTo(new DefaultArtifactVersion(current)) > 0;
        } catch (Throwable t) {
            return compareFallback(candidate, current) > 0;
        }
    }

    private static int compareFallback(String left, String right) {
        String[] a = left.split("[.\\-_ ]");
        String[] b = right.split("[.\\-_ ]");
        int length = Math.max(a.length, b.length);
        for (int i = 0; i < length; i++) {
            String sa = i < a.length ? a[i] : "0";
            String sb = i < b.length ? b[i] : "0";
            boolean na = sa.matches("\\d+");
            boolean nb = sb.matches("\\d+");
            int result;
            if (na && nb) {
                result = new BigInteger(sa).compareTo(new BigInteger(sb));
            } else if (na) {
                result = 1;
            } else if (nb) {
                result = -1;
            } else {
                result = sa.compareToIgnoreCase(sb);
            }
            if (result != 0) return result;
        }
        return 0;
    }

    public static void announceIfUpdateAvailable() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;

        if (manualPending && status != Status.CHECKING && status != Status.IDLE) {
            manualPending = false;
            if (status == Status.UPDATE_AVAILABLE) {
                announced = true;
                client.player.sendSystemMessage(updateMessage());
            } else if (status == Status.UP_TO_DATE) {
                client.player.sendSystemMessage(Component.translatableWithFallback(
                        "gameinfo.update.up_to_date",
                        "[GameInfo] You are running the latest version (%s).",
                        getCurrentVersion()));
            } else if (status == Status.FAILED) {
                client.player.sendSystemMessage(Component.translatableWithFallback(
                        "gameinfo.update.failed",
                        "[GameInfo] Update check failed. Check the log for details."));
            }
            return;
        }

        if (status == Status.UPDATE_AVAILABLE && !announced) {
            announced = true;
            client.player.sendSystemMessage(updateMessage());
        }
    }

    private static Component updateMessage() {
        return Component.translatableWithFallback(
                "gameinfo.update.available",
                "[GameInfo] Version %s is available (you have %s). Download: %s",
                latestTag == null ? "?" : latestTag,
                getCurrentVersion(),
                downloadUrl == null ? "" : downloadUrl);
    }

    private static void loadCache() {
        if (cacheLoaded) return;
        cacheLoaded = true;
        if (!CACHE_FILE.isFile()) return;
        try (Reader reader = new InputStreamReader(new FileInputStream(CACHE_FILE), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element == null || !element.isJsonObject()) return;
            JsonObject json = element.getAsJsonObject();
            if (json.has("lastCheck") && !json.get("lastCheck").isJsonNull()) {
                lastCheckTime = json.get("lastCheck").getAsLong();
            }
            if (json.has("tag") && !json.get("tag").isJsonNull()) {
                latestTag = json.get("tag").getAsString();
            }
            if (json.has("version") && !json.get("version").isJsonNull()) {
                latestVersion = json.get("version").getAsString();
            }
            if (json.has("url") && !json.get("url").isJsonNull()) {
                downloadUrl = json.get("url").getAsString();
            }
            if (latestVersion != null) {
                status = isNewer(latestVersion, normalize(getCurrentVersion())) ? Status.UPDATE_AVAILABLE : Status.UP_TO_DATE;
            }
        } catch (Throwable ignored) {
        }
    }

    private static void saveCache() {
        try {
            File parent = CACHE_FILE.getParentFile();
            if (parent != null && !parent.isDirectory()) {
                parent.mkdirs();
            }
            JsonObject json = new JsonObject();
            json.addProperty("lastCheck", lastCheckTime);
            if (latestTag != null) json.addProperty("tag", latestTag);
            if (latestVersion != null) json.addProperty("version", latestVersion);
            if (downloadUrl != null) json.addProperty("url", downloadUrl);
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(CACHE_FILE), StandardCharsets.UTF_8)) {
                writer.write(json.toString());
            }
        } catch (Throwable ignored) {
        }
    }
}
