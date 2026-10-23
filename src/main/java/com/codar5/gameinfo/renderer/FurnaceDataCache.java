package com.codar5.gameinfo.renderer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class FurnaceDataCache {
    public static class FurnaceItems {
        public ItemStack input;
        public ItemStack fuel;
        public ItemStack output;

        public FurnaceItems(ItemStack input, ItemStack fuel, ItemStack output) {
            this.input = input;
            this.fuel = fuel;
            this.output = output;
        }
    }

    private static final Map<BlockPos, FurnaceItems> cache = new ConcurrentHashMap<>();

    public static void updateFurnaceData(BlockPos pos, ItemStack input, ItemStack fuel, ItemStack output) {
        cache.put(pos, new FurnaceItems(input, fuel, output));
    }

    public static FurnaceItems getFurnaceData(BlockPos pos) {
        return cache.get(pos);
    }

    public static void clear() {
        cache.clear();
    }
}