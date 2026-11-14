/**
 *认准作者Coda_R5，谁敢冒充我我打死他。
 */
package com.codar5.gameinfo.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.ArrayList;
import java.util.List;

public class PonderTopic {
    public final Item icon;
    public final Component title;
    public final List<Component> description;

    public PonderTopic(Item icon, String titleKey, String... descKeys) {
        this.icon = icon;
        this.title = Component.translatable(titleKey);
        this.description = new ArrayList<>();
        for (String key : descKeys) {
            this.description.add(Component.translatable(key));
        }
    }

    public static PonderTopic getFps() {
        return new PonderTopic(Items.CLOCK, "ponder.gameinfo.title.fps", "ponder.gameinfo.desc.fps.1", "ponder.gameinfo.desc.fps.2");
    }

    public static PonderTopic getNether() {
        return new PonderTopic(Items.FLINT_AND_STEEL, "ponder.gameinfo.title.nether", "ponder.gameinfo.desc.nether.1", "ponder.gameinfo.desc.nether.2");
    }

    public static PonderTopic getTime() {
        return new PonderTopic(Items.COMPASS, "ponder.gameinfo.title.time", "ponder.gameinfo.desc.time.1", "ponder.gameinfo.desc.time.2");
    }

    public static PonderTopic getBiome() {
        return new PonderTopic(Items.GRASS_BLOCK, "ponder.gameinfo.title.biome", "ponder.gameinfo.desc.biome.1");
    }

    public static PonderTopic getCoordinates() {
        return new PonderTopic(Items.MAP, "ponder.gameinfo.title.coord", "ponder.gameinfo.desc.coord.1", "ponder.gameinfo.desc.coord.2");
    }

    public static PonderTopic getEnabled() {
        return new PonderTopic(Items.LEVER, "ponder.gameinfo.title.enabled", "ponder.gameinfo.desc.enabled.1");
    }

    public static PonderTopic getEquipment() {
        return new PonderTopic(Items.DIAMOND_CHESTPLATE, "ponder.gameinfo.title.equip", "ponder.gameinfo.desc.equip.1", "ponder.gameinfo.desc.equip.2");
    }

    public static PonderTopic getBlockInfo() {
        return new PonderTopic(Items.IRON_PICKAXE, "ponder.gameinfo.title.block", "ponder.gameinfo.desc.block.1", "ponder.gameinfo.desc.block.2");
    }

    public static PonderTopic getRemark() {
        return new PonderTopic(Items.NAME_TAG, "ponder.gameinfo.title.remark", "ponder.gameinfo.desc.remark.1");
    }

    public static PonderTopic getFurnaceInfo() {
        return new PonderTopic(Items.FURNACE, "ponder.gameinfo.title.furnace", "ponder.gameinfo.desc.furnace.1", "ponder.gameinfo.desc.furnace.2");
    }

    public static PonderTopic getEntityHealth() {
        return new PonderTopic(Items.IRON_SWORD, "ponder.gameinfo.title.entity", "ponder.gameinfo.desc.entity.1", "ponder.gameinfo.desc.entity.2");
    }

    public static PonderTopic getSlimeChunk() {
        return new PonderTopic(Items.SLIME_BALL, "ponder.gameinfo.title.slime", "ponder.gameinfo.desc.slime.1", "ponder.gameinfo.desc.slime.2", "ponder.gameinfo.desc.slime.3");
    }
}