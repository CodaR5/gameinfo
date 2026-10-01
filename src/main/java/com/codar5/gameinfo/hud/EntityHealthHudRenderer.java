package com.codar5.gameinfo.hud;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
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

public class EntityHealthHudRenderer implements HudElement {

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        // 检查配置是否启用生物信息显示
        if (!com.codar5.gameinfo.GameinfoClient.config.showEntityInfo) return;
        
        Minecraft client = Minecraft.getInstance();
        // 确保客户端和世界存在
        if (client == null || client.level == null || client.player == null) return;
        
        // 获取玩家正在看的目标
        HitResult hitResult = client.hitResult;

        // 只处理实体类型的目标
        if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            renderEntityInfo(drawContext, (EntityHitResult) hitResult, client);
        }
    }

    private void renderEntityInfo(GuiGraphicsExtractor drawContext, EntityHitResult entityHitResult, Minecraft client) {
        Entity entity = entityHitResult.getEntity();
        
        // 只显示生物实体的信息
        if (!(entity instanceof LivingEntity livingEntity)) return;

        // 获取实体名称
        String entityName = entity.getName().getString();
        
        // 获取血量信息
        float health = livingEntity.getHealth();
        float maxHealth = livingEntity.getMaxHealth();
        String healthInfo = String.format("血量: %.1f/%.1f", health, maxHealth);
        
        // 获取屏幕尺寸
        int screenWidth = client.getWindow().getGuiScaledWidth();

        // 创建文本对象
        Component nameText = Component.literal(entityName);
        Component healthText = Component.literal(healthInfo);

        // 计算文本宽度
        int nameWidth = client.font.width(nameText);
        int healthWidth = client.font.width(healthText);
        int maxTextWidth = Math.max(nameWidth, healthWidth);

        // 定义布局参数
        int itemSize = 16; // 图标大小
        int padding = 8; // 内边距
        int textPadding = 4; // 图标和文字之间的间距
        int lineHeight = 10; // 行高
        int barHeight = 3; // 血量条高度
        int barPadding = 2; // 血量条间距
        
        // 计算总宽度和位置
        int totalWidth = itemSize + textPadding + maxTextWidth + padding * 2;
        int totalHeight = Math.max(itemSize, lineHeight * 2 + barHeight + barPadding) + padding * 2;
        
        int startX = (screenWidth - totalWidth) / 2;
        int startY = 10; // 距离屏幕顶部的距离

        // 绘制半透明背景（红色调表示生物）
        drawContext.fill(startX, startY, startX + totalWidth, startY + totalHeight, 0x80400000);

        // 计算图标位置（垂直居中）
        int itemX = startX + padding;
        int itemY = startY + padding + (totalHeight - padding * 2 - itemSize) / 2;

        // 计算文字位置
        int textAreaX = itemX + itemSize + textPadding;
        int textAreaWidth = maxTextWidth;
        int nameY = startY + padding + 2; // 名称位置
        int healthY = nameY + lineHeight + 2; // 血量位置
        int barY = healthY + lineHeight + barPadding; // 血量条位置
        
        // 计算名称居中位置
        int nameTextX = textAreaX + (textAreaWidth - nameWidth) / 2;
        int healthTextX = textAreaX; // 血量保持左对齐

        // 根据实体类型选择显示的图标
        ItemStack displayItem = getEntityDisplayItem(entity);

        // 绘制图标
        drawContext.item(displayItem, itemX, itemY);

        // 绘制文本
        drawContext.text(client.font, nameText, nameTextX, nameY, 0xFFFFFFFF, true);
        
        // 根据血量比例决定血量文字颜色
        int healthColor = getHealthColor(health, maxHealth);
        drawContext.text(client.font, healthText, healthTextX, healthY, healthColor, true);
        
        // 绘制血量条
        renderHealthBar(drawContext, health, maxHealth, textAreaX, barY, maxTextWidth);
    }

    private ItemStack getEntityDisplayItem(Entity entity) {
        // 根据实体类型返回对应的显示物品
        if (entity instanceof Player) {
            return new ItemStack(Items.PLAYER_HEAD);
        } else if (entity instanceof Mob) {
            // 敌对生物用剑表示
            return new ItemStack(Items.IRON_SWORD);
        } else if (entity instanceof Animal) {
            // 动物用小麦表示
            return new ItemStack(Items.WHEAT);
        } else {
            // 其他生物用生物蛋表示
            return new ItemStack(Items.EGG);
        }
    }

    private int getHealthColor(float health, float maxHealth) {
        float healthPercentage = health / maxHealth;
        
        if (healthPercentage > 0.6f) {
            return 0xFF00FF00; // 绿色 - 健康
        } else if (healthPercentage > 0.3f) {
            return 0xFFFFFF00; // 黄色 - 中等
        } else {
            return 0xFFFF0000; // 红色 - 危险
        }
    }

    private void renderHealthBar(GuiGraphicsExtractor drawContext, float health, float maxHealth, int x, int y, int width) {
        int barHeight = 3;
        float healthPercentage = health / maxHealth;
        
        // 绘制血量条背景
        drawContext.fill(x, y, x + width, y + barHeight, 0xFF333333);
        
        // 绘制血量条
        int healthWidth = (int) (width * healthPercentage);
        int healthColor = getHealthColor(health, maxHealth);
        drawContext.fill(x, y, x + healthWidth, y + barHeight, healthColor);
    }
}
