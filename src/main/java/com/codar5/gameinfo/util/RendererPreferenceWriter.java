package com.codar5.gameinfo.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class RendererPreferenceWriter {

    private static final String PREFERRED_KEY = "preferredGraphicsBackend:\"opengl\"";
    private static final String PREFERENCE_PREFIX = "preferredGraphicsBackend:";

    public static void forceOpenGLPreference() {
        Path optionsPath = Paths.get("options.txt");

        if (!Files.exists(optionsPath)) {
            System.out.println("[GameInfo]options.txt not found, skipping OpenGL preference");
            return;
        }

        try {
            List<String> lines = Files.readAllLines(optionsPath);
            int preferenceIndex = -1;

            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith(PREFERENCE_PREFIX)) {
                    if (line.equals(PREFERRED_KEY)) {
                        return;
                    }
                    preferenceIndex = i;
                    break;
                }
            }

            if (preferenceIndex >= 0) {
                lines.set(preferenceIndex, PREFERRED_KEY);
                System.out.println("[GameInfo]Overwrote graphics backend preference to OpenGL in options.txt");
            } else {
                lines.add(PREFERRED_KEY);
                System.out.println("[GameInfo]Appended OpenGL preference to options.txt");
            }

            Files.write(optionsPath, lines);
        } catch (IOException e) {
            System.err.println("[GameInfo]Failed to modify options.txt:" + e.getMessage());
        }
    }
}