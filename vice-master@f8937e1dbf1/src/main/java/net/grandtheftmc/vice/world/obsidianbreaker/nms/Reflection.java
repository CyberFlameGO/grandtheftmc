package net.grandtheftmc.vice.world.obsidianbreaker.nms;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * Created by Timothy Lampen on 7/2/2017.
 */
public class Reflection implements NMS {
    private boolean failed = false;

    @Override
    public void sendCrackEffect(Location location, int damage) {
        if(!failed) {
            try {
                int x = location.getBlockX(), y = location.getBlockY(), z = location.getBlockZ();
                Object worldHandle = location.getWorld().getClass().getMethod("getHandle").invoke(location.getWorld());
                Object blockPosition = ReflectionUtils.getNMSClass("BlockPosition").getConstructor(Integer.TYPE, Integer.TYPE, Integer.TYPE).newInstance(x, y, z);
                int dimension = worldHandle.getClass().getField("dimension").getInt(worldHandle);
                Object packet = ReflectionUtils.getNMSClass("PacketPlayOutBlockBreakAnimation")
                        .getConstructor(Integer.TYPE, ReflectionUtils.getNMSClass("BlockPosition"), Integer.TYPE)
                        .newInstance(location.hashCode(), blockPosition, damage);
                Object serverHandle = Bukkit.getServer().getClass().getMethod("getHandle").invoke(Bukkit.getServer());
                serverHandle.getClass()
                        .getMethod("sendPacketNearby", ReflectionUtils.getNMSClass("EntityHuman"), Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Integer.TYPE, ReflectionUtils.getNMSClass("Packet"))
                        .invoke(serverHandle, null, x, y, z, 30, dimension, packet);
            } catch(Exception e) {
                failed = true;
                System.err.println("[ObsidianBreaker] Generic reflection failed. Visible block cracks are disabled. Please contact the plugin author.");
            }
        }
    }
}