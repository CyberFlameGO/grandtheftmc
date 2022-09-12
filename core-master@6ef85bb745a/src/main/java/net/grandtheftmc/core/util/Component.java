package net.grandtheftmc.core.util;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by Luke Bingham on 04/07/2017.
 */
public interface Component<T, E extends JavaPlugin> extends Listener {

    /**
     * This will run when the plugin (E) is enabled.
     *
     * @param plugin The JavaPlugin
     */
    default T onEnable(E plugin) {
        return null;
    }

    /**
     * This will run when the plugin (E) is disabling.
     *
     * @param plugin The JavaPlugin
     */
    default T onDisable(E plugin) {
        return null;
    }

    default void log(boolean enabled) {
        StringBuilder log = new StringBuilder((enabled ? C.GREEN + "Component loaded" : C.RED + "Component disabled") + C.WHITE + ": " + C.YELLOW + getClass().getSimpleName() + C.GRAY);
        int classLength = getClass().getSimpleName().length(), max = 30, difference = max - classLength;
        for(int i = 0; i < difference; i++) log.append(".");
        log.append(disableAble() ? C.GREEN : C.RED).append(String.valueOf(disableAble()).toUpperCase());
        Bukkit.getConsoleSender().sendMessage(log.toString());
    }

    default boolean disableAble() {
        return true;
    }
}
