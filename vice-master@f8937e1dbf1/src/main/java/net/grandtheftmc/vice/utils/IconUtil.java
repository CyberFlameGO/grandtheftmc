package net.grandtheftmc.vice.utils;

import net.grandtheftmc.core.util.C;

public class IconUtil {

    public static String r(char icon, int amount) {
        return C.RESET + icon + C.GRAY + "x" + amount + C.RESET;
    }

    public static String r(char icon) {
        return C.RESET + icon + C.GRAY + "x1" + C.RESET;
    }
}
