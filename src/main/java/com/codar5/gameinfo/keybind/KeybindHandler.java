/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.keybind;

import com.codar5.gameinfo.GameInfoNeoForge;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = GameInfoNeoForge.MOD_ID, value = Dist.CLIENT)
public class KeybindHandler {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            ResourceLocation.fromNamespaceAndPath(GameInfoNeoForge.MOD_ID, "category"), 
            "key.categories.gameinfo"
    );
    
    public static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.gameinfo.open_gui", 
            InputConstants.Type.KEYBOARD, 
            InputConstants.KEY_U, 
            CATEGORY
    );
    
    public static final KeyMapping PONDER_KEY = new KeyMapping(
            "key.gameinfo.ponder", 
            InputConstants.Type.KEYBOARD, 
            InputConstants.KEY_G, 
            CATEGORY
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GUI_KEY);
        event.register(PONDER_KEY);
    }
}
