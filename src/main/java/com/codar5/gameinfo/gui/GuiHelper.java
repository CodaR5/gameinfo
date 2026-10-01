package com.codar5.gameinfo.gui;

import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class GuiHelper {

    public static Button createToggleButton(String translationKey, Supplier<Boolean> getter, Consumer<Boolean> setter, int x, int y, int width, int height) {
        return Button.builder(
                        buildToggleLabel(translationKey, getter.get()),
                        button -> {
                            boolean newValue = !getter.get();
                            setter.accept(newValue);
                            button.setMessage(buildToggleLabel(translationKey, newValue));
                        })
                .bounds(x, y, width, height)
                .build();
    }

    private static Component buildToggleLabel(String translationKey, boolean value) {
        return Component.translatable(translationKey)
                .append(Component.translatable("gameinfo.gui.separator"))
                .append(Component.translatable(value ? "gameinfo.gui.toggle.on" : "gameinfo.gui.toggle.off"));
    }

    public static EditBox createNumberField(Font textRenderer, String translationKey, Supplier<Integer> getter, Consumer<Integer> setter, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.translatable(translationKey));
        field.setValue(String.valueOf(getter.get()));
        field.setResponder(text -> {
            try {
                setter.accept(Integer.parseInt(text));
            } catch (NumberFormatException ignored) {
            }
        });
        return field;
    }

    public static EditBox createTextField(Font textRenderer, String translationKey, Supplier<String> getter, Consumer<String> setter, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.translatable(translationKey));
        field.setValue(getter.get());
        field.setResponder(setter);
        return field;
    }

    public static Button createButton(String translationKey, Runnable action, int x, int y, int width, int height) {
        return Button.builder(Component.translatable(translationKey), button -> action.run())
                .bounds(x, y, width, height)
                .build();
    }

    public static Button createOptionButton(String translationKey, String[] options, Supplier<Integer> currentIndex, Consumer<Integer> setter, int x, int y, int width, int height) {
        return Button.builder(
                        Component.translatable(translationKey).append(": " + options[currentIndex.get()]),
                        button -> {
                            int newIndex = (currentIndex.get() + 1) % options.length;
                            setter.accept(newIndex);
                            button.setMessage(Component.translatable(translationKey).append(": " + options[newIndex]));
                        })
                .bounds(x, y, width, height)
                .build();
    }

    public static EditBox createValidatedNumberField(Font textRenderer, String translationKey, Supplier<Integer> getter, Consumer<Integer> setter, int min, int max, int x, int y, int width, int height) {
        EditBox field = new EditBox(textRenderer, x, y, width, height, Component.translatable(translationKey));
        field.setValue(String.valueOf(getter.get()));
        field.setResponder(text -> {
            try {
                int value = Integer.parseInt(text);
                if (value >= min && value <= max) {
                    setter.accept(value);
                }
            } catch (NumberFormatException ignored) {
            }
        });
        return field;
    }
}