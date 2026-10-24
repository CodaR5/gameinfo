package com.codar5.gameinfo;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.fml.ModList;
import com.codar5.gameinfo.gui.GameInfoConfigScreen;

@EventBusSubscriber(modid = GameInfoNeoForge.MOD_ID, value = Dist.CLIENT)
public class ClientSetupHandler {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ModList.get().getModContainerById(GameInfoNeoForge.MOD_ID).ifPresent(container -> {
                container.registerExtensionPoint(IConfigScreenFactory.class, (minecraft, screen) -> new GameInfoConfigScreen(screen));
            });
        });
    }
}