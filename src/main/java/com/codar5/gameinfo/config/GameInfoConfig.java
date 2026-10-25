package com.codar5.gameinfo.config;

import com.codar5.gameinfo.util.HexUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import net.minecraft.util.CommonColors;

public class GameInfoConfig {
    public boolean showFPS = true;
    public boolean showTimeAndDays = true;
    public boolean showSlimeChunk = true;
    public boolean showCoordinates = true;
    public boolean showNetherCoordinates = true;
    public boolean showBiome = true;
    public int color = CommonColors.YELLOW;
    public Integer xPos = 3;
    public Integer yPos = 3;
    public boolean remark = true;
    public String version = "";
    public boolean showEquipment = true;
    public boolean showFurnaceInfo = false;
    public boolean showEntityInfo = false;
    public boolean showBlockInfo = false;
    public boolean enabled = true;

    public Map<String, Long> serverSeeds = new HashMap<>();

    private static final String CONFIG_FILE = "config" + File.separator + "gameinfo.properties";

    public GameInfoConfig() {
        loadConfig();
    }

    public void copyFrom(GameInfoConfig other) {
        this.showFPS = other.showFPS;
        this.showTimeAndDays = other.showTimeAndDays;
        this.showSlimeChunk = other.showSlimeChunk;
        this.showCoordinates = other.showCoordinates;
        this.showNetherCoordinates = other.showNetherCoordinates;
        this.showBiome = other.showBiome;
        this.color = other.color;
        this.xPos = other.xPos;
        this.yPos = other.yPos;
        this.remark = other.remark;
        this.version = other.version;
        this.showEquipment = other.showEquipment;
        this.showFurnaceInfo = other.showFurnaceInfo;
        this.showEntityInfo = other.showEntityInfo;
        this.showBlockInfo = other.showBlockInfo;
        this.enabled = other.enabled;
        this.serverSeeds = new HashMap<>(other.serverSeeds);
    }

    public void loadConfig() {
        File configFile = new File(CONFIG_FILE);
        if (!configFile.exists()) {
            saveConfig();
        }
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(configFile)) {
            properties.load(input);
            showFPS = Boolean.parseBoolean(properties.getProperty("showFPS", "true"));
            showTimeAndDays = Boolean.parseBoolean(properties.getProperty("showTimeAndDays", "true"));
            showSlimeChunk = Boolean.parseBoolean(properties.getProperty("showSlimeChunk", "true"));
            showCoordinates = Boolean.parseBoolean(properties.getProperty("showCoordinates", "true"));
            showNetherCoordinates = Boolean.parseBoolean(properties.getProperty("showNetherCoordinates", "true"));
            showBiome = Boolean.parseBoolean(properties.getProperty("showBiome", "true"));
            color = (int) Long.parseLong(properties.getProperty("color", "0xFFFFFF00").replace("0x", ""), 16);
            xPos = Integer.parseInt(properties.getProperty("xPos", "3"));
            yPos = Integer.parseInt(properties.getProperty("yPos", "3"));
            remark = Boolean.parseBoolean(properties.getProperty("remark", "true"));
            version = properties.getProperty("version", "");
            showEquipment = Boolean.parseBoolean(properties.getProperty("showEquipment", "true"));
            showFurnaceInfo = Boolean.parseBoolean(properties.getProperty("showFurnaceInfo", "false"));
            showEntityInfo = Boolean.parseBoolean(properties.getProperty("showEntityInfo", "false"));
            showBlockInfo = Boolean.parseBoolean(properties.getProperty("showBlockInfo", "false"));
            enabled = Boolean.parseBoolean(properties.getProperty("enabled", "true"));

            String seedJson = properties.getProperty("serverSeeds", "{}");
            Type type = new TypeToken<Map<String, Long>>(){}.getType();
            serverSeeds = new Gson().fromJson(seedJson, type);
            if (serverSeeds == null) serverSeeds = new HashMap<>();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig() {
        Properties properties = new Properties();
        properties.setProperty("showFPS", Boolean.toString(showFPS));
        properties.setProperty("showTimeAndDays", Boolean.toString(showTimeAndDays));
        properties.setProperty("showSlimeChunk", Boolean.toString(showSlimeChunk));
        properties.setProperty("showCoordinates", Boolean.toString(showCoordinates));
        properties.setProperty("showNetherCoordinates", Boolean.toString(showNetherCoordinates));
        properties.setProperty("showBiome", Boolean.toString(showBiome));
        properties.setProperty("color", HexUtil.toHex(color));
        properties.setProperty("xPos", xPos.toString());
        properties.setProperty("yPos", yPos.toString());
        properties.setProperty("remark", Boolean.toString(remark));
        properties.setProperty("version", version);
        properties.setProperty("showEquipment", Boolean.toString(showEquipment));
        properties.setProperty("showFurnaceInfo", Boolean.toString(showFurnaceInfo));
        properties.setProperty("showEntityInfo", Boolean.toString(showEntityInfo));
        properties.setProperty("showBlockInfo", Boolean.toString(showBlockInfo));
        properties.setProperty("enabled", Boolean.toString(enabled));
        properties.setProperty("serverSeeds", new Gson().toJson(serverSeeds));

        try (FileOutputStream output = new FileOutputStream(CONFIG_FILE)) {
            properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
