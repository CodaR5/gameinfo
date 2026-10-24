package com.codar5.gameinfo.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record SlimeChunkSyncPayload(int cx, int cz, boolean isSlime) implements CustomPacketPayload {
    public static final Type<SlimeChunkSyncPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("gameinfo", "slime_chunk_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlimeChunkSyncPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeInt(payload.cx());
                buf.writeInt(payload.cz());
                buf.writeBoolean(payload.isSlime());
            },
            buf -> new SlimeChunkSyncPayload(buf.readInt(), buf.readInt(), buf.readBoolean())
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}