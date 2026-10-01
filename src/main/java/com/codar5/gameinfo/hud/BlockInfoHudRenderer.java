package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameinfoClient;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import java.lang.reflect.Field;

public class BlockInfoHudRenderer implements HudElement {

    private static Field currentBreakingProgressField;

    static {
        try {
            // 尝试获取currentBreakingProgress字段
            currentBreakingProgressField = net.minecraft.client.multiplayer.MultiPlayerGameMode.class.getDeclaredField("currentBreakingProgress");
            currentBreakingProgressField.setAccessible(true);
        } catch (Exception e) {
            // 如果反射失败，字段保持null
            currentBreakingProgressField = null;
        }
    }

    private float getPreciseBreakingProgress(Minecraft client) {
        if (currentBreakingProgressField != null && client.gameMode != null) {
            try {
                return (Float) currentBreakingProgressField.get(client.gameMode);
            } catch (Exception e) {
                // 反射失败，回退到原方法
            }
        }
        // 回退到原来的方法
        int intProgress = client.gameMode.getDestroyStage();
        return intProgress >= 0 ? intProgress / 10.0f : -1.0f;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        // 检查配置是否启用方块信息显示
        if (!GameinfoClient.config.showBlockInfo) return;

        Minecraft client = Minecraft.getInstance();
        // 确保客户端和世界存在
        if (client == null || client.level == null || client.player == null) return;
        // 获取玩家正在看的目标
        HitResult hitResult = client.hitResult;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            Level world = client.level;
            BlockState blockState = world.getBlockState(pos);

            // 获取方块对应的物品堆栈
            ItemStack itemStack = new ItemStack(blockState.getBlock());

            // 获取方块名称
            String blockName = blockState.getBlock().getName().getString();

            // 获取坐标信息
            String positionInfo = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());

            // 获取玩家手持物品
            ItemStack heldItem = client.player.getMainHandItem();

            // 检查工具有效性
            String progressText = "";
            String toolIcon = "";
            if (!heldItem.isEmpty()) {
                // 检查工具是否正确类型
                boolean isCorrectTool = heldItem.isCorrectToolForDrops(blockState);
                // 检查是否需要工具
                boolean requiresTool = blockState.requiresCorrectToolForDrops();
                Block block = blockState.getBlock();
                boolean isUnbreakableBlock = block == Blocks.BEDROCK || block == Blocks.BARRIER;
                boolean canHarvest;
                if (isUnbreakableBlock) {
                    canHarvest = false;
                } else if (requiresTool) {
                    canHarvest = isCorrectTool;
                } else {
                    canHarvest = true;
                }
                toolIcon = canHarvest ? " ✓" : " ✗";
            }

            // 检查挖掘进度
            float preciseProgress = getPreciseBreakingProgress(client);
            boolean isBreaking = preciseProgress >= 0;

            // 如果正在挖掘，添加进度百分比
            if (isBreaking) {
                progressText = String.format(" %.0f%%", preciseProgress * 100);
            }

            // 获取屏幕尺寸
            int screenWidth = client.getWindow().getGuiScaledWidth();

            // 创建完整的物品名称（包含工具图标和进度）
            String fullBlockName = blockName + toolIcon + progressText;
            Component nameText = Component.literal(fullBlockName);
            Component posText = Component.literal(positionInfo);

            // 计算文本宽度
            int nameWidth = client.font.width(nameText);
            int posWidth = client.font.width(posText);
            int maxTextWidth = Math.max(nameWidth, posWidth);

            // 定义布局参数
            int itemSize = 16; // 物品图标大小
            int padding = 8; // 内边距
            int textPadding = 6; // 图标和文字之间的间距
            int lineHeight = 11; // 行高
            int borderThickness = 1; // 边框厚度

            // 计算总宽度和高度
            int contentWidth = itemSize + textPadding + maxTextWidth;
            int contentHeight = itemSize; // 使用物品图标高度
            int totalWidth = contentWidth + padding * 2 + borderThickness * 2;
            int totalHeight = contentHeight + padding * 2 + borderThickness * 2;

            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10; // 距离屏幕顶部的距离

            // 绘制边框 (淡灰色)
            int borderColor = 0xFFAAAAAA; // 淡灰色边框
            drawContext.fill(startX, startY, startX + totalWidth, startY + borderThickness, borderColor); // 上边框
            drawContext.fill(startX, startY + totalHeight - borderThickness, startX + totalWidth, startY + totalHeight, borderColor); // 下边框
            drawContext.fill(startX, startY, startX + borderThickness, startY + totalHeight, borderColor); // 左边框
            drawContext.fill(startX + totalWidth - borderThickness, startY, startX + totalWidth, startY + totalHeight, borderColor); // 右边框

            // 绘制进度条作为下边框
            if (isBreaking) {
                int progressWidth = (int) ((totalWidth - borderThickness * 2) * preciseProgress);
                drawContext.fill(startX + borderThickness, startY + totalHeight - borderThickness,
                        startX + borderThickness + progressWidth, startY + totalHeight, 0xFFCC8800); // 更淡的橙色
            }

            // 绘制背景（更淡的黑色半透明）
            int bgX = startX + borderThickness;
            int bgY = startY + borderThickness;
            int bgWidth = totalWidth - borderThickness * 2;
            int bgHeight = totalHeight - borderThickness * 2;
            drawContext.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0xB0000000); // 更淡的背景

            // 计算物品图标位置（垂直居中）
            int itemX = bgX + padding;
            int itemY = bgY + padding;

            // 计算文字位置
            int textX = itemX + itemSize + textPadding;
            int nameY = bgY + padding + 1; // 物品名称位置
            int posY = nameY + lineHeight; // 坐标位置

            // 绘制物品图标
            drawContext.item(itemStack, itemX, itemY);

            // 绘制文本
            // 物品名称（淡白色），工具图标根据状态着色，进度百分比用橙色
            if (toolIcon.isEmpty() && progressText.isEmpty()) {
                drawContext.text(client.font, nameText, textX, nameY, 0xFFE0E0E0, true); // 淡白色
            } else {
                // 分别绘制物品名称、工具图标和进度百分比
                Component blockNameOnly = Component.literal(blockName);
                drawContext.text(client.font, blockNameOnly, textX, nameY, 0xFFE0E0E0, true); // 淡白色

                int currentX = textX + client.font.width(blockNameOnly);

                // 绘制工具图标
                if (!toolIcon.isEmpty()) {
                    Component iconText = Component.literal(toolIcon);
                    int iconColor = toolIcon.contains("✓") ? 0xFF70C070 : 0xFFC07070; // 更淡的绿色和红色
                    drawContext.text(client.font, iconText, currentX, nameY, iconColor, true);
                    currentX += client.font.width(iconText);
                }

                // 绘制进度百分比 并且不是0
                if (!progressText.isEmpty() && preciseProgress > 0) {
                    Component progressTextObj = Component.literal(progressText);
                    drawContext.text(client.font, progressTextObj, currentX, nameY, 0xFFCC8800, true); // 淡橙色
                }
            }

            // 坐标信息（淡青色）
            drawContext.text(client.font, posText, textX, posY, 0xFF70C0C0, true); // 更淡的青色
        }
    }
}
