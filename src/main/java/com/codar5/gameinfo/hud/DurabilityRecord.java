package com.codar5.gameinfo.hud;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class DurabilityRecord {
    public static final Map<EquipmentSlot, Long> durabilityTracker = new HashMap<>();
    public static final Map<EquipmentSlot, Integer> lastDurability = new HashMap<>();

    public static boolean hasExperienceMending(ItemStack stack) {
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        for (var holder : enchantments.keySet()) {
            if (holder.unwrapKey().map(k -> k.equals(Enchantments.MENDING)).orElse(false)) {
                return enchantments.getLevel(holder) > 0;
            }
        }
        return false;
    }

    public static boolean hasDurabilityChangedRecently(EquipmentSlot slot, ItemStack stack, long currentTime) {
        int currentDurability = stack.getMaxDamage() - stack.getDamageValue();
        Integer last = lastDurability.get(slot);
        if (last == null || last != currentDurability) {
            lastDurability.put(slot, currentDurability);
            durabilityTracker.put(slot, currentTime);
            return true;
        }
        Long lastTime = durabilityTracker.get(slot);
        return lastTime != null && (currentTime - lastTime) < 3000;
    }
}