package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameInfoNeoForge;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.gui.GuiLayer;

public class EntityHealthHudRenderer implements GuiLayer {

    @Override
    public void render(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        if (!GameInfoNeoForge.config.showEntityInfo) return;

        Minecraft client = Minecraft.getInstance();
        if (client == null || client.level == null || client.player == null) return;

        HitResult hitResult = client.hitResult;

        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            renderEntityInfo(drawContext, (EntityHitResult) hitResult, client);
        }
    }

    private void renderEntityInfo(GuiGraphicsExtractor drawContext, EntityHitResult entityHitResult, Minecraft client) {
        Entity entity = entityHitResult.getEntity();

        if (!(entity instanceof LivingEntity livingEntity)) return;

        String entityName = entity.getName().getString();
        float health = livingEntity.getHealth();
        float maxHealth = livingEntity.getMaxHealth();
        String healthInfo = String.format("血量: %.1f/%.1f", health, maxHealth);

        int screenWidth = client.getWindow().getGuiScaledWidth();

        Component nameText = Component.literal(entityName);
        Component healthText = Component.literal(healthInfo);

        int nameWidth = client.font.width(nameText);
        int healthWidth = client.font.width(healthText);
        int maxTextWidth = Math.max(nameWidth, healthWidth);

        int itemSize = 16;
        int padding = 8;
        int textPadding = 4;
        int lineHeight = 10;
        int barHeight = 3;
        int barPadding = 2;

        int totalWidth = itemSize + textPadding + maxTextWidth + padding * 2;
        int totalHeight = Math.max(itemSize, lineHeight * 2 + barHeight + barPadding) + padding * 2;

        int startX = (screenWidth - totalWidth) / 2;
        int startY = 10;

        drawContext.fill(startX, startY, startX + totalWidth, startY + totalHeight, 0x80400000);

        int itemX = startX + padding;
        int itemY = startY + padding + (totalHeight - padding * 2 - itemSize) / 2;

        int textAreaX = itemX + itemSize + textPadding;
        int textAreaWidth = maxTextWidth;
        int nameY = startY + padding + 2;
        int healthY = nameY + lineHeight + 2;
        int barY = healthY + lineHeight + barPadding;

        int nameTextX = textAreaX + (textAreaWidth - nameWidth) / 2;
        int healthTextX = textAreaX;

        ItemStack displayItem = getEntityDisplayItem(entity);

        drawContext.item(displayItem, itemX, itemY);
        drawContext.text(client.font, nameText, nameTextX, nameY, 0xFFFFFFFF, true);

        int healthColor = getHealthColor(health, maxHealth);
        drawContext.text(client.font, healthText, healthTextX, healthY, healthColor, true);

        renderHealthBar(drawContext, health, maxHealth, textAreaX, barY, maxTextWidth);
    }

    private ItemStack getEntityDisplayItem(Entity entity) {
        if (entity instanceof Player) {
            return new ItemStack(Items.PLAYER_HEAD);
        } else if (entity instanceof Mob) {
            return new ItemStack(Items.IRON_SWORD);
        } else if (entity instanceof Animal) {
            return new ItemStack(Items.WHEAT);
        } else {
            return new ItemStack(Items.EGG);
        }
    }

    private int getHealthColor(float health, float maxHealth) {
        float healthPercentage = health / maxHealth;

        if (healthPercentage > 0.6f) {
            return 0xFF00FF00;
        } else if (healthPercentage > 0.3f) {
            return 0xFFFFFF00;
        } else {
            return 0xFFFF0000;
        }
    }

    private void renderHealthBar(GuiGraphicsExtractor drawContext, float health, float maxHealth, int x, int y, int width) {
        int barHeight = 3;
        float healthPercentage = health / maxHealth;

        drawContext.fill(x, y, x + width, y + barHeight, 0xFF333333);

        int healthWidth = (int) (width * healthPercentage);
        int healthColor = getHealthColor(health, maxHealth);
        drawContext.fill(x, y, x + healthWidth, y + barHeight, healthColor);
    }
}