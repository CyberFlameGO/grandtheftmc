package net.grandtheftmc.core.tutorials;

import net.grandtheftmc.core.Core;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Help {
    private static Map<String, Collection<String>> helpData = new HashMap<>();
    private static YamlConfiguration helpConfig = Core.getSettings().getHelpConfig();

    public static void loadHelpData() {
        helpData.clear();
        for(String key : helpConfig.getConfigurationSection("help").getKeys(true)) {
            helpData.put(key.toLowerCase(), helpConfig.getStringList("help." + key));
        }
    }

    public static Optional<Collection<String>> getHelpMessage(String key) {
        return Optional.ofNullable(helpData.get(key.toLowerCase()));
    }

    public static Map<String, Collection<String>> getHelpData() {
        return helpData;
    }
}
