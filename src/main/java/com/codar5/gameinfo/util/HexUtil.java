package com.codar5.gameinfo.util;

public class HexUtil {
    public static String toHex(int color) {
        return String.format("%08X", color);
    }

    public static int fromHex(String hex) {
        try {
            return (int) Long.parseLong(hex.replace("0x", "").replace("#", ""), 16);
        } catch (NumberFormatException e) {
            return 0xFFFFFFFF;
        }
    }
}