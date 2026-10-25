package com.codar5.gameinfo.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record FurnaceDataPayload(BlockPos pos, ItemStack input, ItemStack fuel, ItemStack output) implements CustomPacketPayload {

    private static final String MOD_ID = "gameinfo";

    public static final CustomPacketPayload.Type<FurnaceDataPayload> TYPE =
            new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(MOD_ID, "furnace_data"));

    public static final StreamCodec<RegistryFriendlyByteBuf, FurnaceDataPayload> CODEC =
            StreamCodec.of(
                    (buf, payload) -> {
                        buf.writeBlockPos(payload.pos());
                        writeStack(buf, payload.input());
                        writeStack(buf, payload.fuel());
                        writeStack(buf, payload.output());
                    },
                    buf -> new FurnaceDataPayload(
                            buf.readBlockPos(),
                            readStack(buf),
                            readStack(buf),
                            readStack(buf)
                    )
            );

    private static void writeStack(RegistryFriendlyByteBuf buf, ItemStack stack) {
        if (stack.isEmpty()) {
            buf.writeBoolean(false);
        } else {
            buf.writeBoolean(true);
            ItemStack.STREAM_CODEC.encode(buf, stack);
        }
    }

    private static ItemStack readStack(RegistryFriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return ItemStack.EMPTY;
        }
        return ItemStack.STREAM_CODEC.decode(buf);
    }

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
