package com.codar5.gameinfo.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BlockInfoHudRenderer {
    public static void render(GuiGraphics graphics, Minecraft client) {
        if (client.hitResult != null && client.hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) client.hitResult;
            if (client.level != null) {
                var state = client.level.getBlockState(blockHit.getBlockPos());
                String blockName = state.getBlock().getName().getString();
                graphics.drawString(client.font, "Block: " + blockName, 10, 150, 0xFFFFFFFF);
            }
        }
    }
}