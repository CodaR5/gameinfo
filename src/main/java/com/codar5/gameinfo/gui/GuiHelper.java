package com.codar5.gameinfo.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class GuiHelper {
    public static Button createToggleButton(String key, Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int w, int h) {
        return Button.builder(getToggleText(key, getter.get()), b -> {
            boolean newVal = !getter.get();
            setter.accept(newVal);
            b.setMessage(getToggleText(key, newVal));
        }).bounds(x, y, w, h).build();
    }

    private static Component getToggleText(String key, boolean value) {
        String status = value ? Component.translatable("gameinfo.gui.toggle.on").getString() : Component.translatable("gameinfo.gui.toggle.off").getString();
        return Component.translatable(key).append(": ").append(status);
    }

    public static Button createButton(String key, Runnable action, int x, int y, int w, int h) {
        return Button.builder(Component.translatable(key), b -> action.run()).bounds(x, y, w, h).build();
    }

    public static EditBox createNumberField(Font font, String key, Supplier<Integer> getter, Consumer<Integer> setter, int x, int y, int w, int h) {
        EditBox box = new EditBox(font, x, y, w, h, Component.translatable(key));
        box.setValue(String.valueOf(getter.get()));
        box.setResponder(s -> {
            try {
                setter.accept(Integer.parseInt(s));
            } catch (NumberFormatException ignored) {}
        });
        return box;
    }

    public static EditBox createTextField(Font font, String key, Supplier<String> getter, Consumer<String> setter, int x, int y, int w, int h) {
        EditBox box = new EditBox(font, x, y, w, h, Component.translatable(key));
        box.setValue(getter.get());
        box.setResponder(setter);
        return box;
    }
}