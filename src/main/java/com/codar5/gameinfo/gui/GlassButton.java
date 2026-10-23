package com.codar5.gameinfo.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class GlassButton extends Button {
    public GlassButton(int x, int y, int width, int height, Component message, OnPress onPress) {
        super(x, y, width, height, message, onPress, DEFAULT_NARRATION);
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int bgColor = this.isHovered() ? 0x80FFFFFF : 0x40000000;
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, bgColor);
        guiGraphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFFFF);
    }
}