package com.codar5.gameinfo.util;

public class HexUtil {

    public static String toHex(int value) {
        return String.format("0x%08X", value);
    }

    public static int toDecimal(String hexString) {
        String cleaned = hexString.startsWith("0x") || hexString.startsWith("0X")
                ? hexString.substring(2)
                : hexString;
        return (int) Long.parseLong(cleaned, 16);
    }
}