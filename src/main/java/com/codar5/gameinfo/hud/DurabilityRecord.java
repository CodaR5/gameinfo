package com.codar5.gameinfo.hud;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class DurabilityRecord {
    ItemStack itemStack;
    int lastDurability;
    long lastChangeTime;

    public DurabilityRecord(ItemStack itemStack, int lastDurability, long lastChangeTime) {
        this.itemStack = itemStack;
        this.lastDurability = lastDurability;
        this.lastChangeTime = lastChangeTime;
    }

    public static int WAIT_TIME = 30000;

    public static final Map<EquipmentSlot, DurabilityRecord> durabilityTracker = new HashMap<>();

    public static boolean hasExperienceMending(ItemStack stack) {
        try {
            return stack.getEnchantments().keySet().stream()
                    .anyMatch(holder -> holder.unwrapKey()
                            .map(key -> key.identifier().getPath().equals("mending"))
                            .orElse(false));
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean hasDurabilityChangedRecently(EquipmentSlot slot, ItemStack stack, long currentTime) {
        int currentDurability = stack.getMaxDamage() - stack.getDamageValue();
        DurabilityRecord record = durabilityTracker.get(slot);
        if (record == null) {
            durabilityTracker.put(slot, new DurabilityRecord(stack, currentDurability, currentTime));
            return true;
        }

        if (!ItemStack.matches(record.itemStack, stack)) {
            durabilityTracker.put(slot, new DurabilityRecord(stack, currentDurability, currentTime));
            return true;
        }

        if (record.lastDurability != currentDurability) {
            record.lastDurability = currentDurability;
            record.lastChangeTime = currentTime;
            record.itemStack = stack;
            return true;
        }
        long timeSinceLastChange = currentTime - record.lastChangeTime;
        return timeSinceLastChange < WAIT_TIME;
    }
}