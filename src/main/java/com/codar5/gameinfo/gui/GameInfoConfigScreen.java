package com.codar5.gameinfo.gui;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.config.GameInfoConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;
    private GameInfoConfig backupConfig;

    private int currentTab = 0;
    private final List<TabButton> tabButtons = new ArrayList<>();
    private static final String[] TAB_KEYS = {"gameinfo.gui.tab.toggles", "gameinfo.gui.tab.color"};

    private Button fpsButton;
    private Button timeButton;
    private Button slimeChunkButton;
    private Button coordButton;
    private Button netherButton;
    private Button biomeButton;
    private Button remarkButton;
    private Button showEquipmentButton;
    private Button showFurnaceInfoButton;
    private Button showEntityInfoButton;
    private Button showBlockInfoButton;
    private Button enableButton;

    private EditBox xPosField;
    private EditBox yPosField;
    private EditBox versionField;
    private Button colorButton;

    public GameInfoConfigScreen(Screen parent) {
        super(Component.translatable("gameinfo.gui.title"));
        this.parent = parent;
        this.config = GameinfoClient.config;
        this.backupConfig = new GameInfoConfig();
        this.backupConfig.copyFrom(this.config);
    }

    @Override
    protected void init() {
        this.clearWidgets();
        createTabButtons();
        if (currentTab == 0) {
            createInfoToggleTab();
        } else if (currentTab == 1) {
            createColorSettingTab();
        }
        createBottomButtons();
    }

    private void createTabButtons() {
        tabButtons.clear();
        int centerX = this.width / 2;
        int tabWidth = 80;
        int tabHeight = 20;
        int startX = centerX - (TAB_KEYS.length * tabWidth) / 2;

        for (int i = 0; i < TAB_KEYS.length; i++) {
            final int tabIndex = i;
            TabButton tabButton = new TabButton(
                    startX + i * tabWidth, 35,
                    tabWidth, tabHeight,
                    TAB_KEYS[i],
                    tabIndex == currentTab,
                    () -> switchTab(tabIndex)
            );
            tabButtons.add(tabButton);
            this.addRenderableWidget(tabButton);
        }
    }

    private void switchTab(int newTab) {
        if (newTab != currentTab) {
            currentTab = newTab;
            this.init();
        }
    }

    private void createInfoToggleTab() {
        int centerX = this.width / 2;
        int startY = 80;
        int buttonWidth = 100;
        int buttonHeight = 20;
        int spacing = 25;
        int sideMargin = 40;

        int leftX = centerX - buttonWidth - sideMargin;
        this.fpsButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.fps", () -> config.showFPS, value -> config.showFPS = value,
                leftX, startY, buttonWidth, buttonHeight));
        this.timeButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.time", () -> config.showTimeAndDays, value -> config.showTimeAndDays = value,
                leftX, startY + spacing, buttonWidth, buttonHeight));
        this.coordButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.coordinates", () -> config.showCoordinates, value -> config.showCoordinates = value,
                leftX, startY + spacing * 2, buttonWidth, buttonHeight));
        this.netherButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.nether", () -> config.showNetherCoordinates, value -> config.showNetherCoordinates = value,
                leftX, startY + spacing * 3, buttonWidth, buttonHeight));
        this.enableButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.enabled", () -> config.enabled, value -> {
                    config.enabled = value;
                    GameinfoClient.logger("Enable HUD: " + value);
                },
                leftX, startY + spacing * 4, buttonWidth, buttonHeight));
        this.slimeChunkButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.slime_chunk", () -> config.showSlimeChunk, value -> config.showSlimeChunk = value,
                leftX, startY + spacing * 5, buttonWidth, buttonHeight));

        int rightX = centerX + sideMargin;
        this.biomeButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.biome", () -> config.showBiome, value -> config.showBiome = value,
                rightX, startY, buttonWidth, buttonHeight));
        this.showEquipmentButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.equipment", () -> config.showEquipment, value -> config.showEquipment = value,
                rightX, startY + spacing, buttonWidth, buttonHeight));
        this.remarkButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.remark", () -> config.remark, value -> config.remark = value,
                rightX, startY + spacing * 2, buttonWidth, buttonHeight));
        this.showEntityInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.entity_info", () -> config.showEntityInfo, value -> config.showEntityInfo = value,
                rightX, startY + spacing * 3, buttonWidth, buttonHeight));
        this.showBlockInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.block_info", () -> config.showBlockInfo, value -> config.showBlockInfo = value,
                rightX, startY + spacing * 4, buttonWidth, buttonHeight));
        this.showFurnaceInfoButton = this.addRenderableWidget(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.furnace_info", () -> config.showFurnaceInfo, value -> config.showFurnaceInfo = value,
                rightX, startY + spacing * 5, buttonWidth, buttonHeight));
    }

    private void createColorSettingTab() {
        int centerX = this.width / 2;
        int startY = 95;
        int spacing = 30;

        this.xPosField = this.addRenderableWidget(GuiHelper.createNumberField(
                this.font, "gameinfo.gui.label.field.x_pos", () -> config.xPos, value -> config.xPos = value,
                centerX - 65, startY, 50, 20));
        this.yPosField = this.addRenderableWidget(GuiHelper.createNumberField(
                this.font, "gameinfo.gui.label.field.y_pos", () -> config.yPos, value -> config.yPos = value,
                centerX + 15, startY, 50, 20));

        this.colorButton = this.addRenderableWidget(Button.builder(
                        Component.translatable("gameinfo.gui.button.select_color"),
                        button -> this.minecraft.gui.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
                            config.color = color;
                        })))
                .bounds(centerX - 50, startY + spacing, 100, 20)
                .build());

        int versionY = startY + spacing * 2 + 10;
        this.versionField = this.addRenderableWidget(GuiHelper.createTextField(
                this.font, "gameinfo.gui.label.field.version",
                () -> config.version,
                value -> config.version = value,
                centerX - 60, versionY, 120, 20));
    }

    private void createBottomButtons() {
        int centerX = this.width / 2;
        int bottomY = this.height - 40;

        this.addRenderableWidget(GuiHelper.createButton("gameinfo.gui.button.save", () -> {
            config.saveConfig();
            backupConfig.copyFrom(config);
            this.onClose();
        }, centerX - 55, bottomY, 50, 20));

        this.addRenderableWidget(GuiHelper.createButton("gameinfo.gui.button.cancel", () -> {
            config.copyFrom(backupConfig);
            this.onClose();
        }, centerX + 5, bottomY, 50, 20));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, this.width, this.height, 0x50000000);
        super.extractRenderState(context, mouseX, mouseY, delta);
        context.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFD700);

        int tabContentY = 60;
        int tabContentHeight = this.height - 110;
        context.fill(20, tabContentY, this.width - 20, tabContentY + tabContentHeight, 0x20000000);

        if (currentTab == 0) {
            renderInfoToggleTab(context);
        } else if (currentTab == 1) {
            renderColorSettingTab(context);
        }
    }

    private void renderInfoToggleTab(GuiGraphicsExtractor context) {
        int centerX = this.width / 2;
        context.centeredText(this.font, Component.translatable("gameinfo.gui.section.select_info"), centerX, 70, 0xFFAAFFAA);
    }

    private void renderColorSettingTab(GuiGraphicsExtractor context) {
        int centerX = this.width / 2;
        int startY = 95;
        int spacing = 30;
        int labelGap = 5;

        context.centeredText(this.font, Component.translatable("gameinfo.gui.section.customize"), centerX, 70, 0xFFAAFFAA);

        Component xLabel = Component.translatable("gameinfo.gui.label.x_pos");
        int xLabelX = centerX - 65 - labelGap - this.font.width(xLabel);
        context.text(this.font, xLabel, xLabelX, startY + 6, 0xFFFFFFFF);

        Component yLabel = Component.translatable("gameinfo.gui.label.y_pos");
        int yLabelX = centerX + 15 - labelGap - this.font.width(yLabel);
        context.text(this.font, yLabel, yLabelX, startY + 6, 0xFFFFFFFF);

        int colorSize = 24;
        int colorX = centerX + 65;
        int colorY = startY + spacing - 2;
        context.fill(colorX - 1, colorY - 1, colorX + colorSize + 1, colorY + colorSize + 1, 0xFF000000);
        context.fill(colorX, colorY, colorX + colorSize, colorY + colorSize, config.color);

        int versionY = startY + spacing * 2 + 10;
        Component versionLabel = Component.translatable("gameinfo.gui.label.version");
        int versionLabelX = centerX - 60 - labelGap - this.font.width(versionLabel);
        context.text(this.font, versionLabel, versionLabelX, versionY + 6, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class TabButton extends Button {
        private final boolean isActive;

        public TabButton(int x, int y, int width, int height, String translationKey, boolean isActive, Runnable onPress) {
            super(x, y, width, height, Component.translatable(translationKey), button -> onPress.run(), DEFAULT_NARRATION);
            this.isActive = isActive;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor context, int mouseX, int mouseY, float deltaTicks) {
            int bgColor = isActive ? 0xFF4A4A4A : 0xFF2A2A2A;
            int textColor = isActive ? 0xFFFFD700 : 0xFFCCCCCC;
            if (this.isHovered() && !isActive) {
                bgColor = 0xFF3A3A3A;
            }
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
            if (isActive) {
                context.fill(this.getX() + 1, this.getY() + this.height - 1, this.getX() + this.width - 1, this.getY() + this.height, bgColor);
            }
            int textX = this.getX() + this.width / 2;
            int textY = this.getY() + (this.height - 8) / 2;
            context.centeredText(Minecraft.getInstance().font, this.getMessage(), textX, textY, textColor);
        }
    }
}