/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class PonderScreen extends Screen {
    private final Screen parent;
    private final PonderTopic topic;
    private int currentPage = 0;

    private ItemStack cachedIcon = null;
    private Button actionButton;

    private long startTime;
    private static final long ANIM_DURATION = 350L;

    public PonderScreen(Screen parent, PonderTopic topic) {
        super(Component.translatable("ponder.gameinfo.ui.title"));
        this.parent = parent;
        this.topic = topic;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();

        try {
            this.cachedIcon = new ItemStack(this.topic.icon);
        } catch (Exception ignored) {
            this.cachedIcon = null;
        }

        this.actionButton = Button.builder(Component.translatable("ponder.gameinfo.ui.next"), button -> {
            if (currentPage < this.topic.description.size() - 1) {
                currentPage++;
                updateButtonText();
            } else {
                this.onClose();
            }
        }).bounds(this.width / 3 + 40, this.height - 40, 120, 20).build();

        this.addRenderableWidget(this.actionButton);
        updateButtonText();
    }

    private void updateButtonText() {
        if (this.actionButton != null) {
            if (currentPage >= this.topic.description.size() - 1) {
                this.actionButton.setMessage(Component.translatable("ponder.gameinfo.ui.close"));
            } else {
                this.actionButton.setMessage(Component.translatable("ponder.gameinfo.ui.next"));
            }
        }
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor context) {
        long elapsed = System.currentTimeMillis() - this.startTime;

        float rawProgress = (float) elapsed / ANIM_DURATION;
        rawProgress = Math.max(0.0f, Math.min(1.0f, rawProgress));

        float ease = 1.0f - (float) Math.pow(1.0f - rawProgress, 3);
        float translateY = (1.0f - ease) * 150.0f;

        int maskAlpha = (int) (180 * ease);
        context.fill(0, 0, this.width, this.height, (maskAlpha << 24) | 0x101010);

        int splitPoint = this.width / 3;
        int centerY = this.height / 2;
        int stageX = splitPoint / 2;
        int stageY = centerY - 20;

        var poseStack = context.pose();
        poseStack.pushMatrix();
        poseStack.translate(0, translateY);

        context.fill(splitPoint, 0, splitPoint + 2, this.height, 0x60FFFFFF);
        context.fill(stageX - 40, stageY - 40, stageX + 40, stageY + 40, 0x80000000);
        context.fill(stageX - 40, stageY + 40, stageX + 40, stageY + 42, 0xFFFFD700);

        if (this.cachedIcon != null) {
            poseStack.pushMatrix();
            poseStack.translate(stageX, stageY);
            float iconScale = 2.5f + (1.5f * ease);
            poseStack.scale(iconScale, iconScale);
            context.item(this.cachedIcon, -8, -8);
            poseStack.popMatrix();
        }

        int titleColor = ((int) (255 * ease) << 24) | 0xFFD700;
        context.centeredText(this.font, this.topic.title, stageX, stageY + 55, titleColor);

        int textX = splitPoint + 40;
        int textY = 60;

        int descAlpha = (int) (255 * ease);
        context.text(this.font, Component.translatable("ponder.gameinfo.ui.description"), textX, textY, (descAlpha << 24) | 0xAAAAAA, true);
        textY += 25;

        for (int i = 0; i <= currentPage; i++) {
            if (i < this.topic.description.size()) {
                float lineProgress = (rawProgress - i * 0.05f) / 0.8f;
                lineProgress = Math.max(0.0f, Math.min(1.0f, lineProgress));
                float lineEase = 1.0f - (float) Math.pow(1.0f - lineProgress, 3);

                int lineColor = ((int) (255 * lineEase) << 24) | 0xFFFFFF;
                if (lineEase > 0) {
                    context.text(this.font, this.topic.description.get(i), textX, (int)(textY + (1.0f - lineEase) * 20.0f), lineColor, true);
                }
                textY += 16;
            }
        }

        poseStack.popMatrix();

        super.extractRenderState(context);
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