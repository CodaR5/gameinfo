package com.codar5.gameinfo.gui;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.config.GameInfoConfig;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class GameInfoConfigScreen extends Screen {
    private final Screen parent;
    private GameInfoConfig config;
    private GameInfoConfig backupConfig;

    private static final int CONTENT_TOP = 46;
    private static final int CONTENT_BOTTOM_MARGIN = 56;
    private static final int COLUMN_WIDTH = 100;
    private static final int COLUMN_GAP = 24;
    private static final int ROW_HEIGHT = 24;
    private static final int SECTION_TITLE_H = 22;
    private static final int SECTION_GAP = 16;
    private static final int SCROLL_STEP = 18;

    private static final int BG_COLOR = 0xA0000000;
    private static final int TITLE_COLOR = 0xFFFFD700;
    private static final int SECTION_COLOR = 0xFFAAFFAA;
    private static final int LABEL_COLOR = 0xFFFFFFFF;
    private static final int DIVIDER_COLOR = 0x60FFD700;
    private static final int SUB_DIVIDER_COLOR = 0x40FFFFFF;
    private static final int SCROLLBAR_TRACK = 0x40000000;
    private static final int SCROLLBAR_THUMB = 0xFFFFD700;

    private static final int LABEL_GAP = 5;
    private static final int FIELD_WIDTH_SMALL = 60;
    private static final int SECOND_GROUP_GAP = 10;
    private static final int COLOR_BOX_SIZE = 20;
    private static final int COLOR_BOX_GAP = 4;
    private static final int COLOR_BTN_W = 100;

    private int scrollOffset = 0;
    private int contentHeight = 0;
    private int maxScroll = 0;

    private final Map<AbstractWidget, Integer> widgetBaseY = new HashMap<>();

    private EditBox xPosField;
    private EditBox yPosField;
    private EditBox versionField;
    private Button colorButton;
    private Button saveButton;
    private Button cancelButton;

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
        this.widgetBaseY.clear();
        this.scrollOffset = 0;

        this.contentHeight = computeContentHeight();
        int viewHeight = this.height - CONTENT_TOP - CONTENT_BOTTOM_MARGIN;
        this.maxScroll = Math.max(0, contentHeight - viewHeight);

        createBottomButtons();
        createContent();
    }

    private int computeContentHeight() {
        int h = 0;
        h += SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP;
        h += SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP;
        h += SECTION_TITLE_H + ROW_HEIGHT * 2;
        h += 20;
        return h;
    }

    private int basicSectionTitleBaseY() {
        return 0;
    }

    private int basicSectionContentBaseY() {
        return SECTION_TITLE_H;
    }

    private int advancedSectionTitleBaseY() {
        return SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP;
    }

    private int advancedSectionContentBaseY() {
        return advancedSectionTitleBaseY() + SECTION_TITLE_H;
    }

    private int colorSectionTitleBaseY() {
        return advancedSectionContentBaseY() + ROW_HEIGHT * 3 + SECTION_GAP;
    }

    private int colorSectionContentBaseY() {
        return colorSectionTitleBaseY() + SECTION_TITLE_H;
    }

    private int colorSectionFieldLeft() {
        int leftBlockW = FIELD_WIDTH_SMALL + SECOND_GROUP_GAP + COLOR_BOX_SIZE + COLOR_BOX_GAP + COLOR_BTN_W;
        return this.width / 2 - leftBlockW / 2;
    }

    private int colorSectionSecondGroupLeft() {
        return colorSectionFieldLeft() + FIELD_WIDTH_SMALL + SECOND_GROUP_GAP;
    }

    private void addScrolled(AbstractWidget widget, int baseY) {
        widgetBaseY.put(widget, baseY);
        widget.setY(CONTENT_TOP + baseY - scrollOffset);
        this.addRenderableWidget(widget);
    }

    private void createContent() {
        int centerX = this.width / 2;
        int leftX = centerX - COLUMN_WIDTH - COLUMN_GAP / 2;
        int rightX = centerX + COLUMN_GAP / 2;

        int basicTop = basicSectionContentBaseY();
        int rowY = basicTop;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.fps", () -> config.showFPS, v -> config.showFPS = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.nether", () -> config.showNetherCoordinates, v -> config.showNetherCoordinates = v,
                rightX, rowY, COLUMN_WIDTH, 20), rowY);
        rowY += ROW_HEIGHT;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.time", () -> config.showTimeAndDays, v -> config.showTimeAndDays = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.biome", () -> config.showBiome, v -> config.showBiome = v,
                rightX, rowY, COLUMN_WIDTH, 20), rowY);
        rowY += ROW_HEIGHT;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.coordinates", () -> config.showCoordinates, v -> config.showCoordinates = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.enabled", () -> config.enabled, v -> {
                    config.enabled = v;
                    GameinfoClient.logger("Enable HUD: " + v);
                },
                rightX, rowY, COLUMN_WIDTH, 20), rowY);

        int advancedTop = advancedSectionContentBaseY();
        rowY = advancedTop;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.equipment", () -> config.showEquipment, v -> config.showEquipment = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.block_info", () -> config.showBlockInfo, v -> config.showBlockInfo = v,
                rightX, rowY, COLUMN_WIDTH, 20), rowY);
        rowY += ROW_HEIGHT;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.remark", () -> config.remark, v -> config.remark = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.furnace_info", () -> config.showFurnaceInfo, v -> config.showFurnaceInfo = v,
                rightX, rowY, COLUMN_WIDTH, 20), rowY);
        rowY += ROW_HEIGHT;

        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.entity_info", () -> config.showEntityInfo, v -> config.showEntityInfo = v,
                leftX, rowY, COLUMN_WIDTH, 20), rowY);
        addScrolled(GuiHelper.createToggleButton(
                "gameinfo.gui.toggle.slime_chunk", () -> config.showSlimeChunk, v -> config.showSlimeChunk = v,
                rightX, rowY, COLUMN_WIDTH, 20), rowY);

        int colorTop = colorSectionContentBaseY();
        int fieldLeft = colorSectionFieldLeft();
        int secondGroupLeft = colorSectionSecondGroupLeft();

        this.xPosField = GuiHelper.createNumberField(
                this.font, "gameinfo.gui.label.field.x_pos", () -> config.xPos, v -> config.xPos = v,
                fieldLeft, colorTop, FIELD_WIDTH_SMALL, 20);
        addScrolled(this.xPosField, colorTop);

        int colorBtnX = secondGroupLeft + COLOR_BOX_SIZE + COLOR_BOX_GAP;
        this.colorButton = Button.builder(
                        Component.translatable("gameinfo.gui.button.select_color"),
                        b -> this.minecraft.gui.setScreen(new ColorPickerScreen(this, (color, colorName) -> {
                            config.color = color;
                        })))
                .bounds(colorBtnX, colorTop, COLOR_BTN_W, 20)
                .build();
        addScrolled(this.colorButton, colorTop);

        int yRowY = colorTop + ROW_HEIGHT;
        this.yPosField = GuiHelper.createNumberField(
                this.font, "gameinfo.gui.label.field.y_pos", () -> config.yPos, v -> config.yPos = v,
                fieldLeft, yRowY, FIELD_WIDTH_SMALL, 20);
        addScrolled(this.yPosField, yRowY);

        Component versionLabel = Component.translatable("gameinfo.gui.label.version");
        int versionFieldX = secondGroupLeft + this.font.width(versionLabel) + LABEL_GAP;
        this.versionField = GuiHelper.createTextField(
                this.font, "gameinfo.gui.label.field.version",
                () -> config.version,
                v -> config.version = v,
                versionFieldX, yRowY, COLOR_BTN_W, 20);
        addScrolled(this.versionField, yRowY);
    }

    private void createBottomButtons() {
        int centerX = this.width / 2;
        int bottomY = this.height - 36;

        this.saveButton = GuiHelper.createButton("gameinfo.gui.button.save", () -> {
            config.saveConfig();
            backupConfig.copyFrom(config);
            this.onClose();
        }, centerX - 55, bottomY, 50, 20);
        this.addRenderableWidget(this.saveButton);

        this.cancelButton = GuiHelper.createButton("gameinfo.gui.button.cancel", () -> {
            config.copyFrom(backupConfig);
            this.onClose();
        }, centerX + 5, bottomY, 50, 20);
        this.addRenderableWidget(this.cancelButton);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (maxScroll > 0) {
            int newOffset = scrollOffset - (int)(vertical * SCROLL_STEP);
            newOffset = Math.max(0, Math.min(maxScroll, newOffset));
            if (newOffset != scrollOffset) {
                scrollOffset = newOffset;
                applyScroll();
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    private void applyScroll() {
        for (Map.Entry<AbstractWidget, Integer> e : widgetBaseY.entrySet()) {
            e.getKey().setY(CONTENT_TOP + e.getValue() - scrollOffset);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context) {
        context.fill(0, 0, this.width, this.height, BG_COLOR);

        int centerX = this.width / 2;

        context.centeredText(this.font, this.title, centerX, 18, TITLE_COLOR);
        context.fill(centerX - 100, 34, centerX + 100, 35, DIVIDER_COLOR);

        int contentBottom = this.height - CONTENT_BOTTOM_MARGIN;
        context.enableScissor(0, CONTENT_TOP - 4, this.width, contentBottom);

        super.extractRenderState(context);

        int basicTitleY = CONTENT_TOP + basicSectionTitleBaseY() + 6 - scrollOffset;
        context.centeredText(this.font,
                Component.translatable("gameinfo.gui.section.basic_info"),
                centerX, basicTitleY, SECTION_COLOR);

        int advancedTitleY = CONTENT_TOP + advancedSectionTitleBaseY() + 6 - scrollOffset;
        context.fill(centerX - 120, advancedTitleY - 6, centerX + 120, advancedTitleY - 5, SUB_DIVIDER_COLOR);
        context.centeredText(this.font,
                Component.translatable("gameinfo.gui.section.advanced_info"),
                centerX, advancedTitleY, SECTION_COLOR);

        int colorTitleY = CONTENT_TOP + colorSectionTitleBaseY() + 6 - scrollOffset;
        context.fill(centerX - 120, colorTitleY - 6, centerX + 120, colorTitleY - 5, SUB_DIVIDER_COLOR);
        context.centeredText(this.font,
                Component.translatable("gameinfo.gui.section.customize"),
                centerX, colorTitleY, SECTION_COLOR);

        int colorTop = CONTENT_TOP + colorSectionContentBaseY() - scrollOffset;
        int fieldLeft = colorSectionFieldLeft();
        int secondGroupLeft = colorSectionSecondGroupLeft();
        int labelRight = fieldLeft - LABEL_GAP;

        Component xLabel = Component.translatable("gameinfo.gui.label.x_pos");
        context.text(this.font, xLabel,
                labelRight - this.font.width(xLabel), colorTop + 6, LABEL_COLOR);

        int colorBoxX = secondGroupLeft;
        context.fill(colorBoxX - 1, colorTop - 1,
                colorBoxX + COLOR_BOX_SIZE + 1, colorTop + COLOR_BOX_SIZE + 1, 0xFF000000);
        context.fill(colorBoxX, colorTop,
                colorBoxX + COLOR_BOX_SIZE, colorTop + COLOR_BOX_SIZE, config.color);

        int yRowY = colorTop + ROW_HEIGHT;
        Component yLabel = Component.translatable("gameinfo.gui.label.y_pos");
        context.text(this.font, yLabel,
                labelRight - this.font.width(yLabel), yRowY + 6, LABEL_COLOR);

        Component versionLabel = Component.translatable("gameinfo.gui.label.version");
        context.text(this.font, versionLabel,
                secondGroupLeft, yRowY + 6, LABEL_COLOR);

        if (maxScroll > 0) {
            int barX = this.width - 10;
            int barTop = CONTENT_TOP;
            int barBottom = contentBottom;
            int barHeight = barBottom - barTop;
            context.fill(barX, barTop, barX + 4, barBottom, SCROLLBAR_TRACK);
            int thumbHeight = Math.max(20, barHeight * barHeight / (barHeight + maxScroll));
            int thumbTop = barTop + (barHeight - thumbHeight) * scrollOffset / maxScroll;
            context.fill(barX, thumbTop, barX + 4, thumbTop + thumbHeight, SCROLLBAR_THUMB);
        }

        context.disableScissor();

        this.saveButton.extractRenderState(context);
        this.cancelButton.extractRenderState(context);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
