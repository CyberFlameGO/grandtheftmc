package net.grandtheftmc.core.util;

import net.grandtheftmc.core.Core;
import org.bukkit.Bukkit;

/**
 * Created by Luke Bingham on 29/08/2017.
 */
public class ServerUtil {

    private static final boolean DEBUG;
    static { DEBUG = Core.getSettings().getNumber() <= 0; }

    public static void runTask(Runnable runnable) {
        Bukkit.getScheduler().runTask(Core.getInstance(), runnable);
    }

    public static void runTaskLater(Runnable runnable, long time) {
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), runnable, time);
    }

    public static void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), runnable);
    }

    public static void runTaskLaterAsync(Runnable runnable, long time) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getInstance(), runnable, time);
    }

    public static void debug(String str) {
        if(DEBUG) Bukkit.getConsoleSender().sendMessage(str);
    }
}
