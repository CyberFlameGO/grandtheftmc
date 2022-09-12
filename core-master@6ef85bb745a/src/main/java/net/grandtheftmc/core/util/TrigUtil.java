package net.grandtheftmc.core.util;

import org.bukkit.Location;

public class TrigUtil {

    public static float wrapAngleTo180_float(float value) {
        value %= 360.0F;
        if (value >= 180.0F) value -= 360.0F;
        if (value < -180.0F) value += 360.0F;
        return value;
    }

    public static double getDirection(Location from, Location to) {
        return wrapAngleTo180_float((float) (Math.atan2(to.getZ() - from.getZ(), to.getX() - from.getX()) * 180.0D / Math.PI) - 90.0F);
    }
}
