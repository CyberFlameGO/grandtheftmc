package net.grandtheftmc.vice.utils;

import org.bukkit.Material;

/**
 * Created by Timothy Lampen on 7/9/2017.
 */
public class StringUtils {

    public static String getPrettyItemString(Material material) {
        return createPrettyEnumString(material.toString());
    }

    private static String createPrettyEnumString(String baseString) {
        String[] substrings = baseString.split("_");
        String prettyString = "";
        int size = 1;

        for (String string : substrings) {
            prettyString = prettyString.concat(getCapitalized(string));

            if (size < substrings.length) {
                prettyString = prettyString.concat(" ");
            }

            size++;
        }

        return prettyString;
    }

    public static String getCapitalized(String target) {
        return target.substring(0, 1).toUpperCase() + target.substring(1).toLowerCase();
    }
}
