package org.makaron.ua.mseller.gui.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.md_5.bungee.api.ChatColor;

public final class HexUtil {

    private HexUtil() {
    }
    public static String color(String message) {
        if (message == null) {
            return "";
        } else {
            Pattern pattern = Pattern.compile("&#([A-Fa-f0-9]{6})");
            Matcher matcher = pattern.matcher(message);
            StringBuffer buffer = new StringBuffer();

            while (matcher.find()) {
                String color = matcher.group(1);
                String replacement = ChatColor.of("#" + color).toString();
                matcher.appendReplacement(buffer, replacement);
            }
            matcher.appendTail(buffer);

            return ChatColor.translateAlternateColorCodes('&', buffer.toString());
        }
    }
}