/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record FurnaceDataPayload(BlockPos pos, ItemStack input, ItemStack fuel, ItemStack output) implements CustomPacketPayload {
    public static final Type<FurnaceDataPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath("gameinfo", "furnace_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceDataPayload> CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, FurnaceDataPayload::pos,
            ItemStack.OPTIONAL_STREAM_CODEC, FurnaceDataPayload::input,
            ItemStack.OPTIONAL_STREAM_CODEC, FurnaceDataPayload::fuel,
            ItemStack.OPTIONAL_STREAM_CODEC, FurnaceDataPayload::output,
            FurnaceDataPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
