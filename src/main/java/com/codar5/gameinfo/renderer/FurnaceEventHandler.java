package com.codar5.gameinfo.renderer;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.world.level.block.Blocks;

public class FurnaceEventHandler {
    
    public static void registerEvents() {
        // 监听方块破坏事件
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            // 检查是否是熔炉方块
            if (state.is(Blocks.FURNACE) || state.is(Blocks.BLAST_FURNACE) || state.is(Blocks.SMOKER)) {
                // 从缓存中删除数据
                FurnaceDataCache.removeFurnaceData(pos);
                System.out.println("Removed furnace data for position: " + pos);
//                FurnaceDataCache.debug();
            }
        });
    }
}