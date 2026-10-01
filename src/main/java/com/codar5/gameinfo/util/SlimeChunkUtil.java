package com.codar5.gameinfo.util;

import net.minecraft.util.RandomSource;

public class SlimeChunkUtil {

    public static boolean isSlimeChunk(long seed, int chunkX, int chunkZ) {
        RandomSource random = RandomSource.create(
                seed
                        + (long)(chunkX * chunkX * 4987142)
                        + (long)(chunkX * 5947611)
                        + (long)(chunkZ * chunkZ * 4392871L)
                        + (long)(chunkZ * 389711)
                        ^ 987234911L
        );
        return random.nextInt(10) == 0;
    }
}