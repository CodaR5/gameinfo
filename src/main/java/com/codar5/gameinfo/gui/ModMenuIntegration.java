package com.codar5.gameinfo.gui;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenuIntegration implements ModMenuApi {
   public ModMenuIntegration() {
   }

   public ConfigScreenFactory<?> getModConfigScreenFactory() {
      return (parent) -> new GameInfoConfigScreen(parent);
   }
}
