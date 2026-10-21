package com.codar5.gameinfo.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;

public class ColorPickerScreen extends Screen {
    private final Screen parent;
    private final ColorSelectedCallback callback;

    private final int[] colorPalette = {
            0xFFFFFFFF, 0xFFE0E0E0, 0xFFC0C0C0, 0xFFA0A0A0, 0xFF808080, 0xFF606060, 0xFF404040, 0xFF000000,
            0xFFFF0000, 0xFFFF4040, 0xFFFF8080, 0xFFFF4000, 0xFFCC0000, 0xFF800000, 0xFF400000, 0xFF800040,
            0xFF00FF00, 0xFF40FF40, 0xFF80FF80, 0xFF00FF40, 0xFF00CC00, 0xFF008000, 0xFF004000, 0xFF408000,
            0xFF0000FF, 0xFF4040FF, 0xFF8080FF, 0xFF0040FF, 0xFF0000CC, 0xFF000080, 0xFF000040, 0xFF004080
    };

    private EditBox colorInputField;
    private Button confirmCustomButton;
    private int previewColor = 0xFFFFFFFF;

    public interface ColorSelectedCallback {
        void onColorSelected(int color, String colorName);
    }

    public ColorPickerScreen(Screen parent, ColorSelectedCallback callback) {
        super(Component.translatable("gameinfo.color_picker.title"));
        this.parent = parent;
        this.callback = callback;
    }

    @Override
    protected void init() {
        int colorSize = 32;
        int spacing = 8;
        int cols = 8;
        int rows = 4;

        int gridWidth = cols * colorSize + (cols - 1) * spacing;
        int gridHeight = rows * colorSize + (rows - 1) * spacing;

        int startX = (this.width - gridWidth) / 2;
        int startY = 50;

        for (int i = 0; i < colorPalette.length; i++) {
            int row = i / cols;
            int col = i % cols;
            int x = startX + col * (colorSize + spacing);
            int y = startY + row * (colorSize + spacing);

            final int colorIndex = i;
            this.addRenderableWidget(new ColorButton(x, y, colorSize, colorSize, colorPalette[i],
                    button -> {
                        callback.onColorSelected(colorPalette[colorIndex], "");
                        this.onClose();
                    }));
        }

        int customY = startY + gridHeight + 25;
        int inputWidth = 100;
        int inputHeight = 20;

        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50;
        int elementStartX = (this.width - totalElementWidth) / 2;

        this.colorInputField = new EditBox(this.font,
                elementStartX, customY + 20, inputWidth, inputHeight,
                Component.translatable("gameinfo.color_picker.custom_title"));
        this.colorInputField.setHint(Component.translatable("gameinfo.color_picker.input_hint"));
        this.colorInputField.setValue("FFFFFFFF");
        this.colorInputField.setResponder(this::onColorInputChanged);
        this.addRenderableWidget(this.colorInputField);

        this.confirmCustomButton = this.addRenderableWidget(Button.builder(
                        Component.translatable("gameinfo.color_picker.use"),
                        button -> {
                            try {
                                String input = this.colorInputField.getValue().trim();
                                if (input.length() == 8) {
                                    int color = (int) Long.parseLong(input, 16);
                                    callback.onColorSelected(color, "");
                                    this.onClose();
                                }
                            } catch (NumberFormatException e) {
                                // ignore
                            }
                        })
                .bounds(elementStartX + inputWidth + 25 + 20 + 10, customY + 20, 50, 20)
                .build());

        this.addRenderableWidget(Button.builder(
                        Component.translatable("gameinfo.gui.button.cancel"),
                        button -> this.onClose())
                .bounds(elementStartX + inputWidth + 25 + 20 + 10, customY + 50, 50, 20)
                .build());
    }

    private void onColorInputChanged(String input) {
        try {
            if (input.length() == 8) {
                this.previewColor = (int) Long.parseLong(input, 16);
            }
        } catch (NumberFormatException e) {
            // ignore
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context) {
        context.fill(0, 0, this.width, this.height, 0x80000000);
        super.extractRenderState(context);

        context.centeredText(this.font, this.title, this.width / 2, 20, 0xFFFFD700);
        context.centeredText(this.font, Component.translatable("gameinfo.color_picker.presets"), this.width / 2, 35, CommonColors.WHITE);

        renderCustomColorArea(context);
    }

    private void renderCustomColorArea(GuiGraphicsExtractor context) {
        int customY = 50 + 4 * (32 + 8) - 8 + 25;

        int bgX = this.width / 2 - 130;
        int bgWidth = 260;
        int bgHeight = 80;
        context.fill(bgX, customY - 5, bgX + bgWidth, customY + bgHeight, 0x30333333);

        context.centeredText(this.font, Component.translatable("gameinfo.color_picker.custom_title"), this.width / 2, customY + 8, 0xFFAAFFAA);

        int inputWidth = 100;
        int totalElementWidth = inputWidth + 25 + 20 + 10 + 50;
        int elementStartX = (this.width - totalElementWidth) / 2;

        int previewSize = 20;
        int previewX = elementStartX + inputWidth + 25;
        int previewY = customY + 20;

        context.fill(previewX - 1, previewY - 1, previewX + previewSize + 1, previewY + previewSize + 1, 0xFF000000);
        context.fill(previewX, previewY, previewX + previewSize, previewY + previewSize, this.previewColor);

        context.text(this.font, Component.translatable("gameinfo.color_picker.format_hint"), this.width / 2 - 120, customY + 50, 0xFF888888);
    }

    @Override
    public void onClose() {
        this.minecraft.gui.setScreen(this.parent);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private static class ColorButton extends Button {
        private final int color;

        public ColorButton(int x, int y, int width, int height, int color, OnPress onPress) {
            super(x, y, width, height, Component.empty(), onPress, DEFAULT_NARRATION);
            this.color = color;
        }

        @Override
        protected void extractContents(GuiGraphicsExtractor context) {
            context.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, this.color);

            if (this.isHovered()) {
                int x = this.getX();
                int y = this.getY();
                int w = this.width;
                int h = this.height;
                int borderColor = 0xFFFFFFFF;
                context.fill(x - 1, y - 1, x + w + 1, y, borderColor);
                context.fill(x - 1, y + h, x + w + 1, y + h + 1, borderColor);
                context.fill(x - 1, y, x, y + h, borderColor);
                context.fill(x + w, y, x + w + 1, y + h, borderColor);
            }
        }
    }
}
