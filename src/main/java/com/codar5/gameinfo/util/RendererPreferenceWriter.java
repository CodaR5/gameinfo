package com.codar5.gameinfo.util;

import com.codar5.gameinfo.GameinfoClient;

public class RendererPreferenceWriter {
    public static void forceOpenGL() {
        GameinfoClient.logger("Forcing OpenGL Renderer");
        System.setProperty("fabric.game.renderer", "opengl");
    }
}