package com.codar5.gameinfo;

import com.codar5.gameinfo.command.ChunkLoader;
import com.codar5.gameinfo.command.GameInfoCommand;
import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.gui.GameInfoConfigScreen;
import com.codar5.gameinfo.hud.HudOverlay;
import com.codar5.gameinfo.network.SlimeChunkSyncPayload;
import com.codar5.gameinfo.util.SlimeChunkUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@Mod(value = "gameinfo", dist = Dist.CLIENT)
public class GameInfoNeoForge {
    public static final String MOD_ID = "gameinfo";
    public static GameInfoConfig config;
    public static int tickCounter = 0;

    public GameInfoNeoForge(ModContainer container) {
        config = new GameInfoConfig();
        container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> new GameInfoConfigScreen(screen));
    }

    @EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT)
    public static class ClientEvents {
        @SubscribeEvent
        public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
            event.registerAbove(VanillaGuiLayers.CHAT, Identifier.fromNamespaceAndPath(MOD_ID, "hud"), new HudOverlay());
        }

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            GameInfoCommand.register(event.getDispatcher());
            ChunkLoader.register(event.getDispatcher());
        }
    }

    @EventBusSubscriber(modid = MOD_ID)
    public static class ServerEvents {
        @SubscribeEvent
        public static void onServerTick(ServerTickEvent.Post event) {
            tickCounter++;
            if (tickCounter % 10 != 0) return;

            for (ServerPlayer player : event.getServer().getPlayerList().getPlayers()) {
                if (!player.level().dimension().equals(Level.OVERWORLD)) continue;

                BlockPos pos = player.blockPosition();
                int cx = pos.getX() >> 4;
                int cz = pos.getZ() >> 4;

                long seed = event.getServer().overworld().getSeed();
                if (seed == 0L) continue;

                boolean isSlime = SlimeChunkUtil.isSlimeChunk(seed, cx, cz);
                PacketDistributor.sendToPlayer(player, new SlimeChunkSyncPayload(cx, cz, isSlime));
            }
        }
    }
}
