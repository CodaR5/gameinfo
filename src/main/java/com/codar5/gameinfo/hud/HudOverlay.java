package com.codar5.gameinfo.hud;

import com.codar5.gameinfo.GameinfoClient;
import com.codar5.gameinfo.config.GameInfoConfig;
import com.codar5.gameinfo.renderer.FurnaceDataCache;
import com.codar5.gameinfo.util.SlimeChunkUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.SharedConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.CommonColors;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class HudOverlay implements HudElement {
    static Minecraft client;
    static Font textRenderer;
    static int DEFAULT_COLOR = CommonColors.WHITE;
    static int DEFAULT_HEIGHT = 10;
    static int color = DEFAULT_COLOR;
    static GameInfoConfig config;

    private static final int SLIME_CHUNK_COLOR = 0xFF55FF55;

    public static final ConcurrentHashMap<BlockPos, ItemStack[]> serverFurnaceCache = new ConcurrentHashMap<>();
    public static final Map<Long, Boolean> slimeChunkCache = new HashMap<>();

    public HudOverlay() {
        client = Minecraft.getInstance();
        textRenderer = client.font;
        config = GameinfoClient.config;
    }

    static EquipmentSlot[] armorSlots = {EquipmentSlot.OFFHAND, EquipmentSlot.MAINHAND, EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD,};

    public void toggleHudVisibility() {
        GameInfoConfig config = GameinfoClient.config;
        config.enabled = !config.enabled;
        config.saveConfig();
    }

    public static void logger(String message) {
        System.out.println("[DEBUG]： " + message);
    }

    private int getMainColor() {
        return 0xFF000000 | (config.color & 0xFFFFFF);
    }

    private int getDefaultColor() {
        return 0xFF000000 | (DEFAULT_COLOR & 0xFFFFFF);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor drawContext, DeltaTracker tickCounter) {
        if (textRenderer == null) {
            textRenderer = client.font;
            if (textRenderer == null) {
                logger("文本渲染器仍然为null，无法渲染HUD信息");
            }
        }

        Player player = client.player;
        if (player == null) return;
        if (!config.enabled) return;

        int xPos = config.xPos;
        int yPos = config.yPos;
        color = config.color;

        yPos += renderFPS(drawContext, xPos, yPos);
        yPos += renderTimeAndDays(drawContext, xPos, yPos);
        yPos += renderCoordinates(drawContext, xPos, yPos);
        yPos += renderNetherCoordinates(drawContext, xPos, yPos);
        yPos += renderBiome(drawContext, xPos, yPos);

        renderRemark(drawContext);
        renderPlayerEquipment(drawContext);
        renderFurnaceInfo(drawContext);
    }

    private void renderFurnaceInfo(GuiGraphicsExtractor ctx) {
        if (!config.showFurnaceInfo) return;
        if (client.level == null || client.hitResult == null) return;
        if (client.hitResult.getType() != HitResult.Type.BLOCK) return;

        BlockPos pos = ((BlockHitResult) client.hitResult).getBlockPos().immutable();

        if (!(client.level.getBlockState(pos).getBlock() instanceof AbstractFurnaceBlock)) return;

        ItemStack input  = ItemStack.EMPTY;
        ItemStack fuel   = ItemStack.EMPTY;
        ItemStack output = ItemStack.EMPTY;
        boolean gotData = false;

        ItemStack[] cached = serverFurnaceCache.get(pos);
        if (cached != null) {
            input  = cached[0];
            fuel   = cached[1];
            output = cached[2];
            gotData = true;
        }

        if (!gotData && client.level.getBlockEntity(pos) instanceof AbstractFurnaceBlockEntity clientFurnace) {
            input  = clientFurnace.getItem(0);
            fuel   = clientFurnace.getItem(1);
            output = clientFurnace.getItem(2);
            if (!input.isEmpty() || !fuel.isEmpty() || !output.isEmpty()) {
                gotData = true;
            }
        }

        if (!gotData) {
            FurnaceDataCache.FurnaceItems fc = FurnaceDataCache.getFurnaceData(pos);
            if (fc != null) {
                input  = fc.input;
                fuel   = fc.fuel;
                output = fc.output;
            }
        }

        if (client.player != null && client.player.containerMenu instanceof AbstractFurnaceMenu menu) {
            try {
                ItemStack m0 = menu.getSlot(0).getItem();
                ItemStack m1 = menu.getSlot(1).getItem();
                ItemStack m2 = menu.getSlot(2).getItem();
                FurnaceDataCache.updateFurnaceData(pos, m0, m1, m2);
                input  = m0;
                fuel   = m1;
                output = m2;
            } catch (Exception ignored) {
            }
        }

        int panelWidth = 160;
        int panelHeight = 78;
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int panelX = (screenWidth - panelWidth) / 2;
        int panelY = screenHeight / 2 + 40;

        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xC0000000);
        ctx.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFFAAAAAA);
        ctx.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFFAAAAAA);
        ctx.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFFAAAAAA);
        ctx.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFFAAAAAA);

        Component title = Component.translatable("gameinfo.hud.furnace.title");
        ctx.centeredText(textRenderer, title, panelX + panelWidth / 2, panelY + 5, getMainColor());

        int rowX = panelX + 8;
        int rowY = panelY + 22;
        drawFurnaceRow(ctx, input,  "gameinfo.hud.furnace.input",  rowX, rowY);
        rowY += 18;
        drawFurnaceRow(ctx, fuel,   "gameinfo.hud.furnace.fuel",   rowX, rowY);
        rowY += 18;
        drawFurnaceRow(ctx, output, "gameinfo.hud.furnace.output", rowX, rowY);
    }

    private void drawFurnaceRow(GuiGraphicsExtractor ctx, ItemStack stack, String labelKey, int x, int y) {
        Component label = Component.translatable(labelKey);
        ctx.text(textRenderer, label, x, y + 4, getMainColor(), true);
        int labelWidth = textRenderer.width(label);

        int itemX = x + labelWidth + 6;
        if (!stack.isEmpty()) {
            ctx.item(stack, itemX, y);
            String name = stack.getHoverName().getString();
            int count = stack.getCount();
            String text = count > 1 ? name + " x" + count : name;
            ctx.text(textRenderer, Component.literal(text), itemX + 20, y + 5, getDefaultColor(), true);
        } else {
            ctx.text(textRenderer, Component.translatable("gameinfo.hud.furnace.empty"), itemX, y + 5, 0xFF808080, true);
        }
    }

    private void renderPlayerEquipment(GuiGraphicsExtractor drawContext) {
        if (!config.showEquipment) return;
        LocalPlayer player = client.player;
        if (player == null || client.gui.hud.isHidden()) return;
        int screenWidth = client.getWindow().getGuiScaledWidth();
        int screenHeight = client.getWindow().getGuiScaledHeight();
        int startX = screenWidth / 2 + 91 + 10;
        int startY = screenHeight - 22;

        List<ItemStack> durableItems = getShowItemStacks(player);
        for (int i = 0; i < durableItems.size(); i++) {
            ItemStack stack = durableItems.get(i);
            int currentY = startY - (i * 20);
            drawContext.item(stack, startX, currentY);
            int maxDamage = stack.getMaxDamage();
            int durability = stack.getMaxDamage() - stack.getDamageValue();
            String durabilityText = String.format("%d/%d", durability, maxDamage);
            int textX = startX + 20;
            int textY = currentY + 5;
            drawContext.text(textRenderer, durabilityText, textX, textY, getMainColor(), true);
        }
    }

    private static @NotNull List<ItemStack> getShowItemStacks(LocalPlayer player) {
        List<ItemStack> durableItems = new ArrayList<>();
        long currentTime = System.currentTimeMillis();

        for (EquipmentSlot slot : armorSlots) {
            ItemStack stack = player.getItemBySlot(slot);
            if (stack.isEmpty() || !stack.isDamageableItem()) {
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            if (DurabilityRecord.hasExperienceMending(stack)) {
                DurabilityRecord.durabilityTracker.remove(slot);
                continue;
            }
            if (DurabilityRecord.hasDurabilityChangedRecently(slot, stack, currentTime)) {
                durableItems.add(stack);
            }
        }
        return durableItems;
    }

    private void renderRemark(GuiGraphicsExtractor drawContext) {
        if (!config.remark) return;
        Component versionLabel = Component.translatable("gameinfo.hud.version");
        String versionValue;
        if (config.version != null && !config.version.trim().isEmpty()) {
            versionValue = config.version;
        } else {
            versionValue = SharedConstants.getCurrentVersion().name();
        }
        int y = 2;
        int x = client.getWindow().getGuiScaledWidth() - textRenderer.width(versionLabel) - textRenderer.width(versionValue) - 2;
        drawContext.text(textRenderer, versionLabel, x, y, getMainColor(), true);
        x += textRenderer.width(versionLabel);
        drawContext.text(textRenderer, versionValue, x, y, getDefaultColor(), true);
    }

    private int renderFPS(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showFPS) return 0;
        int fps = client.getFps();
        Component fpsLabel = Component.translatable("gameinfo.hud.fps");
        drawContext.text(textRenderer, fpsLabel, x, y, getMainColor(), true);
        int width = textRenderer.width(fpsLabel);
        Component fpsValue = Component.literal(fps == -1
                ? Component.translatable("gameinfo.hud.unknown").getString()
                : String.valueOf(fps));
        drawContext.text(textRenderer, fpsValue, x + width, y, getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderTimeAndDays(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showTimeAndDays) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();

        long dayTime = world.getOverworldClockTime();
        long timeOfDay = dayTime % 24000;
        int hours = (int) ((6 + (timeOfDay / 1000)) % 24);
        int minutes = (int) ((timeOfDay % 1000) * 60 / 1000);
        int days = (int) (dayTime / 24000);

        Component daysLabel = Component.translatable("gameinfo.hud.days");
        drawContext.text(textRenderer, daysLabel, x, y, getMainColor(), true);
        int width = textRenderer.width(daysLabel);
        drawContext.text(textRenderer, Component.literal(String.valueOf(days)), x + width, y, getDefaultColor(), true);
        width += textRenderer.width(String.valueOf(days));

        Component timeLabel = Component.translatable("gameinfo.hud.time");
        drawContext.text(textRenderer, timeLabel, x + width, y, getMainColor(), true);
        width += textRenderer.width(timeLabel);

        String formatDate = String.format("%02d:%02d", hours, minutes);
        drawContext.text(textRenderer, Component.literal(formatDate), x + width, y, getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderCoordinates(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showCoordinates) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        BlockPos pos = player.blockPosition();
        String directionString = getDirectionString();
        Component xyzLabel = Component.translatable("gameinfo.hud.xyz");
        drawContext.text(textRenderer, xyzLabel, x, y, getMainColor(), true);
        int width = textRenderer.width(xyzLabel);
        String xyz = String.format("%d %d %d - %s", pos.getX(), pos.getY(), pos.getZ(), directionString);
        drawContext.text(textRenderer, Component.literal(xyz), x + width, y, getDefaultColor(), true);

        if (config.showSlimeChunk && isSlimeChunk(pos)) {
            int xyzWidth = textRenderer.width(xyz);
            Component slimeText = Component.literal(" - ").append(Component.translatable("gameinfo.hud.slime_chunk"));
            drawContext.text(textRenderer, slimeText, x + width + xyzWidth, y, SLIME_CHUNK_COLOR, true);
        }

        return DEFAULT_HEIGHT;
    }

    private boolean isSlimeChunk(BlockPos pos) {
        if (client.level == null) return false;
        if (!client.level.dimension().equals(Level.OVERWORLD)) return false;

        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        long chunkKey = ((long) chunkX & 0xFFFFFFFFL) | (((long) chunkZ & 0xFFFFFFFFL) << 32);

        if (client.hasSingleplayerServer()) {
            var server = client.getSingleplayerServer();
            if (server != null) {
                long seed = server.overworld().getSeed();
                if (seed == 0L) return false;
                return SlimeChunkUtil.isSlimeChunk(seed, chunkX, chunkZ);
            }
        }

        return slimeChunkCache.getOrDefault(chunkKey, false);
    }

    private int renderNetherCoordinates(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showNetherCoordinates) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        String coordinateText = "";
        String labelKey = "";
        if (world.dimension().equals(Level.OVERWORLD)) {
            coordinateText = String.format("%d %d", pos.getX() / 8, pos.getZ() / 8);
            labelKey = "gameinfo.hud.nether";
        } else if (world.dimension().equals(Level.NETHER)) {
            coordinateText = String.format("%d %d", pos.getX() * 8, pos.getZ() * 8);
            labelKey = "gameinfo.hud.overworld";
        } else {
            return 0;
        }

        Component coordinateLabel = Component.translatable(labelKey);
        drawContext.text(textRenderer, coordinateLabel, x, y, getMainColor(), true);
        int width = textRenderer.width(coordinateLabel);
        drawContext.text(textRenderer, Component.literal(coordinateText), x + width, y, getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private int renderBiome(GuiGraphicsExtractor drawContext, int x, int y) {
        if (!config.showBiome) return 0;
        LocalPlayer player = client.player;
        if (player == null) return 0;
        Level world = player.level();
        BlockPos pos = player.blockPosition();

        Holder<Biome> biomeEntry = world.getBiome(pos);
        Identifier biomeId = biomeEntry.unwrapKey().map(ResourceKey::identifier).orElse(null);
        String biomeName;
        if (biomeId == null) {
            biomeName = Component.translatable("gameinfo.hud.unknown_biome").getString();
        } else {
            String translationKey = "biome." + biomeId.getNamespace() + "." + biomeId.getPath();
            biomeName = Component.translatable(translationKey).getString();
        }
        Component biomeLabel = Component.translatable("gameinfo.hud.biome");
        drawContext.text(textRenderer, biomeLabel, x, y, getMainColor(), true);
        int width = textRenderer.width(biomeLabel);
        drawContext.text(textRenderer, Component.literal(biomeName), x + width, y, getDefaultColor(), true);
        return DEFAULT_HEIGHT;
    }

    private String getDirectionString() {
        Direction direction = null;
        if (client.player != null) {
            direction = client.player.getDirection();
        }
        if (direction == null) return Component.translatable("gameinfo.direction.unknown").getString();
        String key = switch (direction) {
            case NORTH -> "gameinfo.direction.north";
            case SOUTH -> "gameinfo.direction.south";
            case EAST -> "gameinfo.direction.east";
            case WEST -> "gameinfo.direction.west";
            default -> "gameinfo.direction.unknown";
        };
        return Component.translatable(key).getString();
    }
}
