package com.codar5.gameinfo.renderer;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

public class FurnaceDataCache {
    private static final Map<BlockPos, FurnaceItems> furnaceData = new HashMap<>();

    public static long lastTime = 0L;

    public static class FurnaceItems {
        public final ItemStack input;
        public final ItemStack fuel;
        public final ItemStack output;

        public FurnaceItems(ItemStack input, ItemStack fuel, ItemStack output) {
            this.input = input;
            this.fuel = fuel;
            this.output = output;
        }
    }

    public static void updateFurnaceData(BlockPos pos, ItemStack input, ItemStack fuel, ItemStack output) {
        furnaceData.put(pos, new FurnaceItems(input, fuel, output));
    }

    public static FurnaceItems getFurnaceData(BlockPos pos) {
        return furnaceData.getOrDefault(pos, new FurnaceItems(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY));
    }

    public static void removeFurnaceData(BlockPos pos) {
        furnaceData.remove(pos);
    }
}