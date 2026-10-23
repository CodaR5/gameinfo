package com.codar5.gameinfo.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.EntityHitResult;

public class EntityHealthHudRenderer {
    public static void render(GuiGraphics graphics, Minecraft client) {
        if (client.hitResult != null && client.hitResult instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof LivingEntity living) {
                float health = living.getHealth();
                float maxHealth = living.getMaxHealth();
                String name = living.getName().getString();
                graphics.drawString(client.font, name + ": " + health + "/" + maxHealth, 10, 170, 0xFFFF5555);
            }
        }
    }
}