package com.codar5.gameinfo.gui;

import java.util.function.BiConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final BiConsumer<Integer, String> onColorSelected;
    private EditBox hexField;

    private static final int[] PRESET_COLORS = {
            0xFFFFFFFF, 0xFFC0C0C0, 0xFFA0A0A0, 0xFF808080, 0xFF606060, 0xFF404040, 0xFF202020, 0xFF000000,
            0xFFFF0000, 0xFFFF6060, 0xFFFFAAAA, 0xFFFF8000, 0xFFFFC000, 0xFF800000, 0xFF600000, 0xFF800080,
            0xFF00FF00, 0xFF60FF60, 0xFFAAFFAA, 0xFF00C000, 0xFF008000, 0xFF006000, 0xFF004000, 0xFF80FF00,
            0xFF0000FF, 0xFF6060FF, 0xFFAAAAFF, 0xFF0080FF, 0xFF0000C0, 0xFF000080, 0xFF000040, 0xFF008080
    };

    public ColorPickerScreen(Screen parent, BiConsumer<Integer, String> onColorSelected) {
        super(Component.translatable("gameinfo.color_picker.title"));
        this.parent = parent;
        this.onColorSelected = onColorSelected;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;
        int colorSize = 30;
        int padding = 8;
        int cols = 8;
        int totalWidth = cols * (colorSize + padding) - padding;
        int gridStartX = centerX - totalWidth / 2;
        int gridStartY = centerY - 100;

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            final int color = PRESET_COLORS[i];
            int row = i / cols;
            int col = i % cols;
            int x = gridStartX + col * (colorSize + padding);
            int y = gridStartY + row * (colorSize + padding);

            this.addRenderableWidget(Button.builder(Component.empty(), b -> {
                this.hexField.setValue(String.format("%08X", color));
            }).bounds(x, y, colorSize, colorSize).build());
        }

        int inputY = centerY + 85;
        this.hexField = new EditBox(this.font, centerX - 120, inputY, 80, 20, Component.translatable("gameinfo.color_picker.input_hint"));
        this.hexField.setValue("FFFFFFFF");
        this.addRenderableWidget(this.hexField);

        this.addRenderableWidget(Button.builder(Component.translatable("gameinfo.color_picker.use"), b -> {
            try {
                String val = this.hexField.getValue();
                if (val.length() == 6) val = "FF" + val;
                int color = (int) Long.parseLong(val, 16);
                this.onColorSelected.accept(color, val);
                this.onClose();
            } catch (Exception ignored) {}
        }).bounds(centerX + 10, inputY, 50, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("取消"), b -> {
            this.onClose();
        }).bounds(centerX + 65, inputY, 50, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        graphics.drawCenteredString(this.font, this.title, centerX, centerY - 130, 0xFFFFFFFF);
        graphics.drawCenteredString(this.font, Component.literal("预设颜色"), centerX, centerY - 115, 0xFFAAAAAA);

        super.render(graphics, mouseX, mouseY, delta);

        int colorSize = 30;
        int padding = 8;
        int cols = 8;
        int totalWidth = cols * (colorSize + padding) - padding;
        int gridStartX = centerX - totalWidth / 2;
        int gridStartY = centerY - 100;

        int currentColor = 0;
        try {
            String val = this.hexField.getValue();
            if (val.length() == 6) val = "FF" + val;
            currentColor = (int) Long.parseLong(val, 16);
        } catch (Exception ignored) {}

        for (int i = 0; i < PRESET_COLORS.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int px = gridStartX + col * (colorSize + padding);
            int py = gridStartY + row * (colorSize + padding);

            if (PRESET_COLORS[i] == currentColor) {
                graphics.fill(px - 2, py - 2, px + colorSize + 2, py + colorSize + 2, 0xFFFFFFFF);
            } else {
                graphics.fill(px - 1, py - 1, px + colorSize + 1, py + colorSize + 1, 0xFF000000);
            }
            graphics.fill(px, py, px + colorSize, py + colorSize, PRESET_COLORS[i]);
        }

        int inputY = centerY + 85;
        graphics.drawCenteredString(this.font, Component.literal("自定义颜色(ARGB)"), centerX, inputY - 15, 0xFFFFFFFF);

        int colorX = centerX - 30;
        graphics.fill(colorX - 1, inputY - 1, colorX + 21, inputY + 21, 0xFF000000);
        graphics.fill(colorX, inputY, colorX + 20, inputY + 20, currentColor);

        graphics.drawCenteredString(this.font, Component.literal("格式: AARRGGBB 例如: FFFF0000"), centerX, centerY + 120, 0xFFAAAAAA);
    }

    @Override
    public void onClose() {
        this.minecraft.setScreen(this.parent);
    }
}