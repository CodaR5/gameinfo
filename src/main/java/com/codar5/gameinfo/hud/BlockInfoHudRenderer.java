package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameInfoNeoForge;
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
import net.neoforged.neoforge.client.gui.GuiLayer;
import java.lang.reflect.Field;

public class BlockInfoHudRenderer implements GuiLayer {

    private static Field currentBreakingProgressField;

    static {
        try {
            currentBreakingProgressField = net.minecraft.client.multiplayer.MultiPlayerGameMode.class.getDeclaredField("currentBreakingProgress");
            currentBreakingProgressField.setAccessible(true);
        } catch (Exception e) {
            currentBreakingProgressField = null;
        }
    }

    private float getPreciseBreakingProgress(Minecraft client) {
        if (currentBreakingProgressField != null && client.gameMode != null) {
            try {
                return (Float) currentBreakingProgressField.get(client.gameMode);
            } catch (Exception e) {
            }
        }
        int intProgress = client.gameMode.getDestroyStage();
        return intProgress >= 0 ? intProgress / 10.0f : -1.0f;
    }

    @Override
    public void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        if (!GameInfoNeoForge.config.showBlockInfo) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.level == null || client.player == null) return;
        HitResult hitResult = client.hitResult;

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos pos = blockHitResult.getBlockPos();
            Level world = client.level;
            BlockState blockState = world.getBlockState(pos);

            ItemStack itemStack = new ItemStack(blockState.getBlock());
            String blockName = blockState.getBlock().getName().getString();
            String positionInfo = String.format("%d, %d, %d", pos.getX(), pos.getY(), pos.getZ());
            ItemStack heldItem = client.player.getMainHandItem();

            String progressText = "";
            String toolIcon = "";
            if (!heldItem.isEmpty()) {
                boolean isCorrectTool = heldItem.isCorrectToolForDrops(blockState);
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

            float preciseProgress = getPreciseBreakingProgress(client);
            boolean isBreaking = preciseProgress >= 0;
            if (isBreaking) {
                progressText = String.format(" %.0f%%", preciseProgress * 100);
            }

            int screenWidth = client.getWindow().getGuiScaledWidth();

            String fullBlockName = blockName + toolIcon + progressText;
            Component nameText = Component.literal(fullBlockName);
            Component posText = Component.literal(positionInfo);

            int nameWidth = client.font.width(nameText);
            int posWidth = client.font.width(posText);
            int maxTextWidth = Math.max(nameWidth, posWidth);

            int itemSize = 16;
            int padding = 8;
            int textPadding = 6;
            int lineHeight = 11;
            int borderThickness = 1;

            int contentWidth = itemSize + textPadding + maxTextWidth;
            int contentHeight = itemSize;
            int totalWidth = contentWidth + padding * 2 + borderThickness * 2;
            int totalHeight = contentHeight + padding * 2 + borderThickness * 2;

            int startX = (screenWidth - totalWidth) / 2;
            int startY = 10;

            int borderColor = 0xFFAAAAAA;
            drawContext.fill(startX, startY, startX + totalWidth, startY + borderThickness, borderColor);
            drawContext.fill(startX, startY + totalHeight - borderThickness, startX + totalWidth, startY + totalHeight, borderColor);
            drawContext.fill(startX, startY, startX + borderThickness, startY + totalHeight, borderColor);
            drawContext.fill(startX + totalWidth - borderThickness, startY, startX + totalWidth, startY + totalHeight, borderColor);

            if (isBreaking) {
                int progressWidth = (int) ((totalWidth - borderThickness * 2) * preciseProgress);
                drawContext.fill(startX + borderThickness, startY + totalHeight - borderThickness,
                        startX + borderThickness + progressWidth, startY + totalHeight, 0xFFCC8800);
            }

            int bgX = startX + borderThickness;
            int bgY = startY + borderThickness;
            int bgWidth = totalWidth - borderThickness * 2;
            int bgHeight = totalHeight - borderThickness * 2;
            drawContext.fill(bgX, bgY, bgX + bgWidth, bgY + bgHeight, 0xB0000000);

            int itemX = bgX + padding;
            int itemY = bgY + padding;

            int textX = itemX + itemSize + textPadding;
            int nameY = bgY + padding + 1;
            int posY = nameY + lineHeight;

            drawContext.item(itemStack, itemX, itemY);

            if (toolIcon.isEmpty() && progressText.isEmpty()) {
                drawContext.text(client.font, nameText, textX, nameY, 0xFFE0E0E0, true);
            } else {
                Component blockNameOnly = Component.literal(blockName);
                drawContext.text(client.font, blockNameOnly, textX, nameY, 0xFFE0E0E0, true);

                int currentX = textX + client.font.width(blockNameOnly);

                if (!toolIcon.isEmpty()) {
                    Component iconText = Component.literal(toolIcon);
                    int iconColor = toolIcon.contains("✓") ? 0xFF70C070 : 0xFFC07070;
                    drawContext.text(client.font, iconText, currentX, nameY, iconColor, true);
                    currentX += client.font.width(iconText);
                }

                if (!progressText.isEmpty() && preciseProgress > 0) {
                    Component progressTextObj = Component.literal(progressText);
                    drawContext.text(client.font, progressTextObj, currentX, nameY, 0xFFCC8800, true);
                }
            }

            drawContext.text(client.font, posText, textX, posY, 0xFF70C0C0, true);
        }
    }
}