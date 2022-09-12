package net.grandtheftmc.gtm.utils;

 import java.util.Arrays;
 import java.util.List;

/**
 * Represents a particle color
 */
@SuppressWarnings("WeakerAccess")
public class ParticleColor {
    // Standard Minecraft Chat Colors
    public static final ParticleColor BLACK = new ParticleColor(0, 0, 0); // 0x000000
    public static final ParticleColor DARK_BLUE = new ParticleColor(0, 0, 170); // 0x0000AA
    public static final ParticleColor DARK_GREEN = new ParticleColor(0, 170, 0); // 0x00AA00
    public static final ParticleColor DARK_AQUA = new ParticleColor(0, 170, 170); // 0x00AAAA
    public static final ParticleColor DARK_RED = new ParticleColor(170, 0, 0); // 0xAA0000
    public static final ParticleColor DARK_PURPLE = new ParticleColor(170, 0, 170); // 0xAA00AA
    public static final ParticleColor GOLD = new ParticleColor(255, 170, 0); // 0xFFAA00
    public static final ParticleColor GRAY = new ParticleColor(170, 170, 170); // 0xAAAAAA
    public static final ParticleColor DARK_GRAY = new ParticleColor(85, 85, 85); // 0x555555
    public static final ParticleColor BLUE = new ParticleColor(85, 85, 255); // 0x5555FF
    public static final ParticleColor GREEN = new ParticleColor(85, 255, 85); // 0x55FF55
    public static final ParticleColor AQUA = new ParticleColor(85, 255, 255); // 0x55FFFF
    public static final ParticleColor RED = new ParticleColor(255, 85, 85); // 0xFF5555
    public static final ParticleColor LIGHT_PURPLE = new ParticleColor(255, 85, 255); // 0xFF55FF
    public static final ParticleColor YELLOW = new ParticleColor(255, 255, 85); // 0xFFFF55
    public static final ParticleColor WHITE = new ParticleColor(255, 255, 255); // 0xFFFFFF

    /**
     * List of all pre-named colors
     */
    public static final List<String> names = Arrays.asList(
            "black",
            "dark-blue",
            "dark-green",
            "dark-aqua",
            "dark-red",
            "dark-purple",
            "gold",
            "gray",
            "dark-gray",
            "blue",
            "green",
            "aqua",
            "red",
            "light-purple",
            "yellow",
            "white"
    );

    private final float red;
    private final float green;
    private final float blue;

    /**
     * Creates a new particle color from RGB values 0 - 255
     *
     * @param red   Red value
     * @param green Green value
     * @param blue  Blue value
     */
    public ParticleColor(int red, int green, int blue) {
        // values of 0 are changed to 0.0001 for black
        // due to the way the particle packet works
        this.red = (red == 0 ? 0.0001F : red) / 255;
        this.green = (green == 0 ? 0.0001F : green) / 255;
        this.blue = (blue == 0 ? 0.0001F : blue) / 255;
    }

    /**
     * Get the red value
     * <p>
     * The values 0.0 - 1.0 represent 0 - 255
     *
     * @return Red value
     */
    public float getRed() {
        return red;
    }

    /**
     * Get the green value
     * <p>
     * The values 0.0 - 1.0 represent 0 - 255
     *
     * @return Green value
     */
    public float getGreen() {
        return green;
    }

    /**
     * Get the blue value
     * <p>
     * The values 0.0 - 1.0 represent 0 - 255
     *
     * @return Blue value
     */
    public float getBlue() {
        return blue;
    }

    /**
     * Get the hexadecimal color code for this color
     *
     * @return Hexadecimal color code
     */
    public String getHex() {
        return String.format("#%02x%02x%02x", (int) (red * 255), (int) (green * 255), (int) (blue * 255)).toUpperCase();
    }

    /**
     * Get color by name or hex code
     * <p>
     * Invalid colors will default to RED
     *
     * @param color Color name or hex code
     * @return ParticleColor
     */
    public static ParticleColor getColor(String color) {
        ParticleColor actual = getColorExact(color);
        return actual == null ? RED : actual;
    }

    /**
     * Get color by name or hex code
     * <p>
     * Invalid colors return NULL
     *
     * @param color Color name or hex code
     * @return ParticleColor
     */
    public static ParticleColor getColorExact(String color) {
        switch (color.toUpperCase()) {
            case "BLACK":
                return BLACK;
            case "DARK_BLUE":
                return DARK_BLUE;
            case "DARK_GREEN":
                return DARK_GREEN;
            case "DARK_AQUA":
                return DARK_AQUA;
            case "DARK_RED":
                return DARK_RED;
            case "DARK_PURPLE":
                return DARK_PURPLE;
            case "GOLD":
                return GOLD;
            case "GRAY":
                return GRAY;
            case "DARK_GRAY":
                return DARK_GRAY;
            case "BLUE":
                return BLUE;
            case "GREEN":
                return GREEN;
            case "AQUA":
                return AQUA;
            case "RED":
                return RED;
            case "LIGHT_PURPLE":
                return LIGHT_PURPLE;
            case "YELLOW":
                return YELLOW;
            case "WHITE":
                return WHITE;
        }

        if (color.startsWith("#")) {
            color = color.substring(1); // remove # sign if present
        }

        ParticleColor particleColor = null;

        if (color.length() < 6) {
            return null;
        }

        try {
            particleColor = new ParticleColor(
                    Integer.valueOf(color.substring(0, 2), 16),
                    Integer.valueOf(color.substring(2, 4), 16),
                    Integer.valueOf(color.substring(4, 6), 16)
            );
        } catch (Exception ignore) {
        }

        return particleColor;
    }

    /**
     * Get a human readable String representation of this color
     *
     * @return Human readable String representation of color
     */
    @Override
    public String toString() {
        return "ParticleColor[red:[" +
                red +
                "], green:[" +
                green +
                "], blue:[" +
                blue +
                "]]";
    }
}