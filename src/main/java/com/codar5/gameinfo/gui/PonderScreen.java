/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

/**
 * 思索展示界面。
 * 带有从底部快速平滑升起的页面弹出动画，并动态适配按钮文本。
 */
public class PonderScreen extends Screen {
    private final Screen parent;
    private final PonderTopic topic;
    private int currentPage = 0;

    private ItemStack cachedIcon = null;
    private Button actionButton;

    // 动画时间控制
    private long startTime;
    private static final long ANIM_DURATION = 350L; // 350 毫秒的升起动画

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

        // 创建按钮，点击时执行翻页或关闭逻辑
        this.actionButton = Button.builder(Component.translatable("ponder.gameinfo.ui.next"), button -> {
            if (currentPage < this.topic.description.size() - 1) {
                currentPage++;
                updateButtonText();
            } else {
                this.onClose();
            }
        }).bounds(this.width / 3 + 40, this.height - 40, 120, 20).build();

        this.addRenderableWidget(this.actionButton);

        // 初始化按钮文本
        updateButtonText();
    }

    /**
     * 动态更新按钮文本。
     * 如果当前是最后一页，显示“关闭”；否则显示“下一页”。
     */
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
    public void extractRenderState(GuiGraphicsExtractor context, int mouseX, int mouseY, float delta) {
        // 1. 计算动画真实流逝时间
        long elapsed = System.currentTimeMillis() - this.startTime;
        float rawProgress = Mth.clamp((float) elapsed / ANIM_DURATION, 0.0f, 1.0f);

        // 2. Ease-Out-Cubic 缓动曲线
        float ease = 1.0f - (float) Math.pow(1.0f - rawProgress, 3);

        // 3. 计算整体面板从底部升起的偏移量 (150px 到 0)
        float translateY = (1.0f - ease) * 150.0f;

        // 4. 全屏暗色背景（延迟淡入）
        int maskAlpha = (int) (180 * ease);
        context.fill(0, 0, this.width, this.height, (maskAlpha << 24) | 0x101010);

        int splitPoint = this.width / 3;
        int centerY = this.height / 2;
        int stageX = splitPoint / 2;
        int stageY = centerY - 20;

        // === 开始整体面板矩阵偏移 ===
        var poseStack = context.pose();
        poseStack.pushMatrix();
        // 将整个面板向下偏移，随着动画进行逐渐归零
        poseStack.translate(0, translateY);

        // 绘制中间分割线
        context.fill(splitPoint, 0, splitPoint + 2, this.height, 0x60FFFFFF);

        // 绘制左侧展示区背景
        context.fill(stageX - 40, stageY - 40, stageX + 40, stageY + 40, 0x80000000);
        context.fill(stageX - 40, stageY + 40, stageX + 40, stageY + 42, 0xFFFFD700);

        // 绘制物品图标
        if (this.cachedIcon != null) {
            poseStack.pushMatrix();
            poseStack.translate(stageX, stageY);
            float iconScale = 2.5f + (1.5f * ease);
            poseStack.scale(iconScale, iconScale);
            context.item(this.cachedIcon, -8, -8);
            poseStack.popMatrix();
        }

        // 绘制标题
        int titleColor = ((int) (255 * ease) << 24) | 0xFFD700;
        context.centeredText(this.font, this.topic.title, stageX, stageY + 55, titleColor);

        // 绘制右侧说明文字
        int textX = splitPoint + 40;
        int textY = 60;

        int descAlpha = (int) (255 * ease);
        context.text(this.font, Component.translatable("ponder.gameinfo.ui.description"), textX, textY, (descAlpha << 24) | 0xAAAAAA, true);
        textY += 25;

        for (int i = 0; i <= currentPage; i++) {
            if (i < this.topic.description.size()) {
                float lineEase = 1.0f - (float) Math.pow(1.0f - Mth.clamp((rawProgress - i * 0.05f) / 0.8f, 0.0f, 1.0f), 3);
                int lineColor = ((int) (255 * lineEase) << 24) | 0xFFFFFF;
                if (lineEase > 0) {
                    context.text(this.font, this.topic.description.get(i), textX, (int)(textY + (1.0f - lineEase) * 20.0f), lineColor, true);
                }
                textY += 16;
            }
        }

        // 文本逻辑已完全交由 updateButtonText() 处理

        poseStack.popMatrix();

        // 渲染原版 Button 控件
        super.extractRenderState(context, mouseX, mouseY, delta);
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