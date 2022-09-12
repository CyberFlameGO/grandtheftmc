package net.grandtheftmc.core.util;

import java.util.regex.Pattern;

/**
 * Created by Luke Bingham on 25/07/2017.
 */
public class C {
    public static final char COLOR_CHAR = '\u00A7';

    public static final String BOLD = COLOR_CHAR + "l";
    public static final String STRIKE = COLOR_CHAR + "m";
    public static final String UNDERLINE = COLOR_CHAR + "n";
    public static final String MAGIC = COLOR_CHAR + "k";
    public static final String ITALIC = COLOR_CHAR + "o";
    public static final String RESET = COLOR_CHAR + "r";

    public static final String BLACK = COLOR_CHAR + "0";
    public static final String DARK_BLUE = COLOR_CHAR + "1";
    public static final String DARK_GREEN = COLOR_CHAR + "2";
    public static final String DARK_AQUA = COLOR_CHAR + "3";
    public static final String DARK_RED = COLOR_CHAR + "4";
    public static final String DARK_PURPLE = COLOR_CHAR + "5";
    public static final String GOLD = COLOR_CHAR + "6";
    public static final String GRAY = COLOR_CHAR + "7";
    public static final String DARK_GRAY = COLOR_CHAR + "8";
    public static final String BLUE = COLOR_CHAR + "9";
    public static final String GREEN = COLOR_CHAR + "a";
    public static final String AQUA = COLOR_CHAR + "b";
    public static final String RED = COLOR_CHAR + "c";
    public static final String LIGHT_PURPLE = COLOR_CHAR + "d";
    public static final String YELLOW = COLOR_CHAR + "e";
    public static final String WHITE = COLOR_CHAR + "f";

    //Key colors
    public static final String ERROR = RED, WARNING = YELLOW, INFO = GREEN, SUCCESS = GREEN;

    public static boolean isNumeral(String color) {
        Pattern pattern = Pattern.compile("^(" + COLOR_CHAR + ")\\d$");
        return pattern.matcher(color).find();
    }
}
