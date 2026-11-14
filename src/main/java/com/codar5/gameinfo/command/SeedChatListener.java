/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.command;

import com.codar5.gameinfo.network.WorldSeedStore;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

public class SeedChatListener {
    public static void register() {
        // 拦截玩家发送的聊天消息
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (message.startsWith("/giseed set ")) {
                String seedStr = message.substring("/giseed set ".length()).trim();
                try {
                    long seed = Long.parseLong(seedStr);
                    WorldSeedStore.setManual(seed);
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("§aGameinfo: slime-chunk seed " + seed + " saved for this server"));
                    }
                } catch (NumberFormatException e) {
                    if (Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.sendSystemMessage(Component.literal("§cGameinfo: Invalid seed format!"));
                    }
                }
                return false; // 返回 false 代表拦截此消息，不发送给服务器
            }
            return true; // 其他消息正常放行
        });
    }
}
