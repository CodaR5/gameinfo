/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.gui;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.keybind.KeybindHandler;
import com.codar5.gameinfo.network.WorldSeedStore;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

/**
 * GameInfo 的配置界面。
 * 支持滚动、分页显示功能开关，并支持长按 G 键触发专属思索功能。
 */
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
    private final Map<AbstractWidget, PonderTopic> ponderMap = new HashMap<>();

    private boolean isPonderHeld = false;
    private int holdTicks = 0;
    private final int MAX_HOLD_TICKS = 10;
    private AbstractWidget currentHoveredWidget = null;

    private EditBox xPosField;
    private EditBox yPosField;
    private EditBox versionField;
    private EditBox seedField;
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
        this.ponderMap.clear();
        this.scrollOffset = 0;
        this.holdTicks = 0;
        this.isPonderHeld = false;
        this.currentHoveredWidget = null;

        this.contentHeight = computeContentHeight();
        int viewHeight = this.height - CONTENT_TOP - CONTENT_BOTTOM_MARGIN;
        this.maxScroll = Math.max(0, contentHeight - viewHeight);

        createBottomButtons();
        createContent();
    }

    @Override
    public void tick() {
        super.tick();
        if (isPonderHeld && currentHoveredWidget != null) {
            holdTicks++;
            if (holdTicks >= MAX_HOLD_TICKS) {
                // 将当前的配置界面(this)作为父级传入思索界面
                this.minecraft.gui.setScreen(new PonderScreen(this, ponderMap.get(currentHoveredWidget)));
                isPonderHeld = false;
                holdTicks = 0;
            }
        } else {
            holdTicks = 0;
        }
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (KeybindHandler.ponderKeyBinding != null && KeybindHandler.ponderKeyBinding.matches(event)) {
            this.isPonderHeld = true;
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        if (KeybindHandler.ponderKeyBinding != null && KeybindHandler.ponderKeyBinding.matches(event)) {
            this.isPonderHeld = false;
            this.holdTicks = 0;
            return true;
        }
        return super.keyReleased(event);
    }

    private void updateHoveredWidget(double mouseX, double mouseY) {
        currentHoveredWidget = null;
        for (Map.Entry<AbstractWidget, PonderTopic> entry : ponderMap.entrySet()) {
            AbstractWidget w = entry.getKey();
            if (w.visible && w.isMouseOver(mouseX, mouseY)) {
                currentHoveredWidget = w;
                break;
            }
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        super.mouseMoved(mouseX, mouseY);
        updateHoveredWidget(mouseX, mouseY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        if (maxScroll > 0) {
            int newOffset = scrollOffset - (int)(vertical * SCROLL_STEP);
            newOffset = Math.max(0, Math.min(maxScroll, newOffset));
            if (newOffset != scrollOffset) {
                scrollOffset = newOffset;
                applyScroll();
                updateHoveredWidget(mouseX, mouseY);
                return true;
            }
        }
        return super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
    }

    private int computeContentHeight() {
        int h = 0;
        h += SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP;
        h += SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP;
        h += SECTION_TITLE_H + ROW_HEIGHT * 3;
        h += 20;
        return h;
    }

    private int basicSectionTitleBaseY() { return 0; }
    private int basicSectionContentBaseY() { return SECTION_TITLE_H; }
    private int advancedSectionTitleBaseY() { return SECTION_TITLE_H + ROW_HEIGHT * 3 + SECTION_GAP; }
    private int advancedSectionContentBaseY() { return advancedSectionTitleBaseY() + SECTION_TITLE_H; }
    private int colorSectionTitleBaseY() { return advancedSectionContentBaseY() + ROW_HEIGHT * 3 + SECTION_GAP; }
    private int colorSectionContentBaseY() { return colorSectionTitleBaseY() + SECTION_TITLE_H; }

    private int colorSectionTotalWidth() {
        int versionLabelWidth = this.font.width(Component.translatable("gameinfo.gui.label.version"));
        return FIELD_WIDTH_SMALL + SECOND_GROUP_GAP + versionLabelWidth + LABEL_GAP + COLOR_BTN_W;
    }

    private int colorSectionFieldLeft() {
        return this.width / 2 - colorSectionTotalWidth() / 2;
    }

    private int colorSectionSecondGroupLeft() {
        return colorSectionFieldLeft() + FIELD_WIDTH_SMALL + SECOND_GROUP_GAP;
    }

    private void addScrolled(AbstractWidget widget, int baseY) {
        widgetBaseY.put(widget, baseY);
        widget.setY(CONTENT_TOP + baseY - scrollOffset);
        this.addRenderableWidget(widget);
    }

    private void addPonder(AbstractWidget widget, PonderTopic topic) {
        ponderMap.put(widget, topic);
    }

    private void createContent() {
        int centerX = this.width / 2;
        int leftX = centerX - COLUMN_WIDTH - COLUMN_GAP / 2;
        int rightX = centerX + COLUMN_GAP / 2;

        int basicTop = basicSectionContentBaseY();
        int rowY = basicTop;

        Button btnFps = GuiHelper.createToggleButton("gameinfo.gui.toggle.fps", () -> config.showFPS, v -> config.showFPS = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnFps, rowY);
        addPonder(btnFps, PonderTopic.getFps());

        Button btnNether = GuiHelper.createToggleButton("gameinfo.gui.toggle.nether", () -> config.showNetherCoordinates, v -> config.showNetherCoordinates = v, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnNether, rowY);
        addPonder(btnNether, PonderTopic.getNether());

        rowY += ROW_HEIGHT;

        Button btnTime = GuiHelper.createToggleButton("gameinfo.gui.toggle.time", () -> config.showTimeAndDays, v -> config.showTimeAndDays = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnTime, rowY);
        addPonder(btnTime, PonderTopic.getTime());

        Button btnBiome = GuiHelper.createToggleButton("gameinfo.gui.toggle.biome", () -> config.showBiome, v -> config.showBiome = v, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnBiome, rowY);
        addPonder(btnBiome, PonderTopic.getBiome());

        rowY += ROW_HEIGHT;

        Button btnCoord = GuiHelper.createToggleButton("gameinfo.gui.toggle.coordinates", () -> config.showCoordinates, v -> config.showCoordinates = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnCoord, rowY);
        addPonder(btnCoord, PonderTopic.getCoordinates());

        Button btnEnabled = GuiHelper.createToggleButton("gameinfo.gui.toggle.enabled", () -> config.enabled, v -> {
            config.enabled = v;
            GameinfoClient.logger("Enable HUD: " + v);
        }, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnEnabled, rowY);
        addPonder(btnEnabled, PonderTopic.getEnabled());

        int advancedTop = advancedSectionContentBaseY();
        rowY = advancedTop;

        Button btnEquip = GuiHelper.createToggleButton("gameinfo.gui.toggle.equipment", () -> config.showEquipment, v -> config.showEquipment = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnEquip, rowY);
        addPonder(btnEquip, PonderTopic.getEquipment());

        Button btnBlock = GuiHelper.createToggleButton("gameinfo.gui.toggle.block_info", () -> config.showBlockInfo, v -> config.showBlockInfo = v, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnBlock, rowY);
        addPonder(btnBlock, PonderTopic.getBlockInfo());

        rowY += ROW_HEIGHT;

        Button btnRemark = GuiHelper.createToggleButton("gameinfo.gui.toggle.remark", () -> config.remark, v -> config.remark = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnRemark, rowY);
        addPonder(btnRemark, PonderTopic.getRemark());

        Button btnFurnace = GuiHelper.createToggleButton("gameinfo.gui.toggle.furnace_info", () -> config.showFurnaceInfo, v -> config.showFurnaceInfo = v, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnFurnace, rowY);
        addPonder(btnFurnace, PonderTopic.getFurnaceInfo());

        rowY += ROW_HEIGHT;

        Button btnEntity = GuiHelper.createToggleButton("gameinfo.gui.toggle.entity_info", () -> config.showEntityInfo, v -> config.showEntityInfo = v, leftX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnEntity, rowY);
        addPonder(btnEntity, PonderTopic.getEntityHealth());

        Button btnSlime = GuiHelper.createToggleButton("gameinfo.gui.toggle.slime_chunk", () -> config.showSlimeChunk, v -> config.showSlimeChunk = v, rightX, rowY, COLUMN_WIDTH, 20);
        addScrolled(btnSlime, rowY);
        addPonder(btnSlime, PonderTopic.getSlimeChunk());

        int colorTop = colorSectionContentBaseY();
        int fieldLeft = colorSectionFieldLeft();
        int secondGroupLeft = colorSectionSecondGroupLeft();
        int totalWidth = colorSectionTotalWidth();

        this.xPosField = GuiHelper.createNumberField(this.font, "gameinfo.gui.label.field.x_pos", () -> config.xPos, v -> config.xPos = v, fieldLeft, colorTop, FIELD_WIDTH_SMALL, 20);
        addScrolled(this.xPosField, colorTop);

        int colorBtnX = secondGroupLeft + COLOR_BOX_SIZE + COLOR_BOX_GAP;
        this.colorButton = Button.builder(Component.translatable("gameinfo.gui.button.select_color"), b -> this.minecraft.gui.setScreen(new ColorPickerScreen(this, (color, colorName) -> config.color = color)))
                .bounds(colorBtnX, colorTop, COLOR_BTN_W, 20).build();
        addScrolled(this.colorButton, colorTop);

        int yRowY = colorTop + ROW_HEIGHT;
        this.yPosField = GuiHelper.createNumberField(this.font, "gameinfo.gui.label.field.y_pos", () -> config.yPos, v -> config.yPos = v, fieldLeft, yRowY, FIELD_WIDTH_SMALL, 20);
        addScrolled(this.yPosField, yRowY);

        Component versionLabel = Component.translatable("gameinfo.gui.label.version");
        int versionFieldX = secondGroupLeft + this.font.width(versionLabel) + LABEL_GAP;
        this.versionField = GuiHelper.createTextField(this.font, "gameinfo.gui.label.field.version", () -> config.version, v -> config.version = v, versionFieldX, yRowY, COLOR_BTN_W, 20);
        addScrolled(this.versionField, yRowY);

        int seedRowY = colorTop + ROW_HEIGHT * 2;
        long currentSeed = WorldSeedStore.resolve();
        String seedStr = currentSeed == 0L ? "" : String.valueOf(currentSeed);
        this.seedField = new EditBox(this.font, fieldLeft, seedRowY, totalWidth, 20, Component.translatable("gameinfo.gui.label.field.seed"));
        this.seedField.setMaxLength(25);
        this.seedField.setValue(seedStr);
        this.seedField.setHint(Component.translatable("gameinfo.gui.label.field.seed"));

        if (Minecraft.getInstance().hasSingleplayerServer()) {
            this.seedField.setEditable(false);
            this.seedField.setValue("Auto (Singleplayer)");
        } else {
            this.seedField.setResponder(text -> {
                try {
                    if (text.trim().isEmpty()) {
                        WorldSeedStore.clearManual();
                    } else {
                        WorldSeedStore.setManual(Long.parseLong(text.trim()));
                    }
                } catch (NumberFormatException ignored) {
                }
            });
        }
        addScrolled(this.seedField, seedRowY);
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

    private void applyScroll() {
        for (Map.Entry<AbstractWidget, Integer> e : widgetBaseY.entrySet()) {
            e.getKey().setY(CONTENT_TOP + e.getValue() - scrollOffset);
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        updateHoveredWidget(mouseX, mouseY);

        context.fill(0, 0, this.width, this.height, BG_COLOR);

        int centerX = this.width / 2;

        context.centeredText(this.font, this.title, centerX, 18, TITLE_COLOR);
        context.fill(centerX - 100, 34, centerX + 100, 35, DIVIDER_COLOR);

        int contentBottom = this.height - CONTENT_BOTTOM_MARGIN;
        context.enableScissor(0, CONTENT_TOP - 4, this.width, contentBottom);

        super.extractRenderState(context, mouseX, mouseY, delta);

        int basicTitleY = CONTENT_TOP + basicSectionTitleBaseY() + 6 - scrollOffset;
        context.centeredText(this.font, Component.translatable("gameinfo.gui.section.basic_info"), centerX, basicTitleY, SECTION_COLOR);

        int advancedTitleY = CONTENT_TOP + advancedSectionTitleBaseY() + 6 - scrollOffset;
        context.fill(centerX - 120, advancedTitleY - 6, centerX + 120, advancedTitleY - 5, SUB_DIVIDER_COLOR);
        context.centeredText(this.font, Component.translatable("gameinfo.gui.section.advanced_info"), centerX, advancedTitleY, SECTION_COLOR);

        int colorTitleY = CONTENT_TOP + colorSectionTitleBaseY() + 6 - scrollOffset;
        context.fill(centerX - 120, colorTitleY - 6, centerX + 120, colorTitleY - 5, SUB_DIVIDER_COLOR);
        context.centeredText(this.font, Component.translatable("gameinfo.gui.section.customize"), centerX, colorTitleY, SECTION_COLOR);

        int colorTop = CONTENT_TOP + colorSectionContentBaseY() - scrollOffset;
        int fieldLeft = colorSectionFieldLeft();
        int secondGroupLeft = colorSectionSecondGroupLeft();
        int labelRight = fieldLeft - LABEL_GAP;

        Component xLabel = Component.translatable("gameinfo.gui.label.x_pos");
        context.text(this.font, xLabel, labelRight - this.font.width(xLabel), colorTop + 6, LABEL_COLOR);

        int colorBoxX = secondGroupLeft;
        context.fill(colorBoxX - 1, colorTop - 1, colorBoxX + COLOR_BOX_SIZE + 1, colorTop + COLOR_BOX_SIZE + 1, 0xFF000000);
        context.fill(colorBoxX, colorTop, colorBoxX + COLOR_BOX_SIZE, colorTop + COLOR_BOX_SIZE, config.color);

        int yRowY = colorTop + ROW_HEIGHT;
        Component yLabel = Component.translatable("gameinfo.gui.label.y_pos");
        context.text(this.font, yLabel, labelRight - this.font.width(yLabel), yRowY + 6, LABEL_COLOR);

        Component versionLabel = Component.translatable("gameinfo.gui.label.version");
        context.text(this.font, versionLabel, secondGroupLeft, yRowY + 6, LABEL_COLOR);

        int seedRowY = colorTop + ROW_HEIGHT * 2;
        Component seedLabel = Component.translatable("gameinfo.gui.label.seed");
        context.text(this.font, seedLabel, labelRight - this.font.width(seedLabel), seedRowY + 6, LABEL_COLOR);

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

        this.saveButton.extractRenderState(context, mouseX, mouseY, delta);
        this.cancelButton.extractRenderState(context, mouseX, mouseY, delta);

        if (currentHoveredWidget != null) {
            int px = mouseX + 10;
            int py = mouseY + 10;
            Component hoverText = Component.translatable("ponder.gameinfo.ui.hold");
            int textW = this.font.width(hoverText);

            if (isPonderHeld) {
                int progressW = 80;
                int progressH = 6;
                context.fill(px - 2, py - 2, px + Math.max(progressW, textW) + 2, py + progressH + 2, 0xCC000000);
                context.fill(px, py, px + (int)(progressW * ((float)holdTicks / MAX_HOLD_TICKS)), py + progressH, 0xFF55FF55);
                context.text(this.font, hoverText, px, py - 12, 0xFFFFFFFF, true);
            } else {
                context.fill(px - 2, py - 2, px + textW + 2, py + 10, 0xCC000000);
                context.text(this.font, hoverText, px, py, 0xFFFFFFFF, true);
            }
        }
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