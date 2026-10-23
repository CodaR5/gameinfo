package com.codar5.gameinfo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record FurnaceDataPayload(BlockPos pos, ItemStack input, ItemStack fuel, ItemStack output) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<FurnaceDataPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("gameinfo", "furnace_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceDataPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        BlockPos.STREAM_CODEC.encode(buf, payload.pos());
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.input());
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.fuel());
                        ItemStack.OPTIONAL_STREAM_CODEC.encode(buf, payload.output());
                    },
                    buf -> new FurnaceDataPayload(
                            BlockPos.STREAM_CODEC.decode(buf),
                            ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                            ItemStack.OPTIONAL_STREAM_CODEC.decode(buf),
                            ItemStack.OPTIONAL_STREAM_CODEC.decode(buf)
                    )
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}