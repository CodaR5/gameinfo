package com.codar5.gameinfo.util;

public class HexUtil {

    /**
     * 十进制转 16 进制字符串（带 0x 前缀，8位大写）
     */
    public static String toHex(int value) {
        return String.format("0x%08X", value);
    }

    /**
     * 16 进制字符串转十进制 int，支持带或不带 0x 前缀
     */
    public static int toDecimal(String hexString) {
        String cleaned = hexString.startsWith("0x") || hexString.startsWith("0X")
                ? hexString.substring(2)
                : hexString;
        return (int) Long.parseLong(cleaned, 16);
    }
}
