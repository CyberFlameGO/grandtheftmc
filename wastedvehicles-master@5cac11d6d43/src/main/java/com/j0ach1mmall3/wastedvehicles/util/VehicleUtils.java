package com.j0ach1mmall3.wastedvehicles.util;

import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class VehicleUtils {
    private VehicleUtils() {}

    public static Location[] getBoundingBox(Entity entity) {
        try {
            Object handle = ReflectionAPI.getHandle((Object) entity);
            Object boundingBox = handle.getClass().getMethod("getBoundingBox").invoke(handle);
            Location loc1 = new Location(entity.getWorld(), (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("a").get(boundingBox),
                    (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("b").get(boundingBox),
                    (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("c").get(boundingBox));
            Location loc2 = new Location(entity.getWorld(), (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("d").get(boundingBox),
                    (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("e").get(boundingBox),
                    (double)ReflectionAPI.getNmsClass("AxisAlignedBB").getField("f").get(boundingBox));
            return new Location[]{loc1, loc2};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setYaw(Entity vehicle, float yaw) {
        Object handle = ReflectionAPI.getHandle((Object) vehicle);
        try {
            handle.getClass().getField("yaw").set(handle, yaw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setPitch(Entity vehicle, float pitch) {
        Object handle = ReflectionAPI.getHandle((Object) vehicle);
        try {
            handle.getClass().getField("pitch").set(handle, pitch);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void makeJumping(Entity entity) {
        Object handle = ReflectionAPI.getHandle((Object) entity);
        try {
            handle.getClass().getField("P").set(handle, 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static EulerAngle toEulerAngle(Vector vector) {
        return new EulerAngle(vector.getX(), vector.getY(), vector.getZ());
    }

    public static Vector multiply(EulerAngle eulerAngle, double factor) {
        return new Vector(eulerAngle.getX() * factor, eulerAngle.getY() * factor, eulerAngle.getZ() * factor);
    }

    public static void teleport(Entity entity, Location location) {
        net.minecraft.server.v1_12_R1.Entity en = ((CraftEntity) entity).getHandle();
        en.setPosition(location.getX(), location.getY(), location.getZ());
    }
}