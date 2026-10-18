package com.codar5.gameinfo.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SlimeChunkSyncPayload(int chunkX, int chunkZ, boolean isSlime)
        implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SlimeChunkSyncPayload> TYPE =
            new CustomPacketPayload.Type<>(
                    Identifier.fromNamespaceAndPath("gameinfo", "slime_chunk_sync")
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeChunkSyncPayload> CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_INT, SlimeChunkSyncPayload::chunkX,
                    ByteBufCodecs.VAR_INT, SlimeChunkSyncPayload::chunkZ,
                    ByteBufCodecs.BOOL, SlimeChunkSyncPayload::isSlime,
                    SlimeChunkSyncPayload::new
            );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
