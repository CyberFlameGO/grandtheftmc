package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LockedBlocks {
    private Collection<Location> lockedBlocks;
    private YamlConfiguration config;

    public LockedBlocks() {
        this.lockedBlocks = new ArrayList<>();
        this.config = Vice.getSettings().getDrugBlocksConfig();
        load();
    }

    public void load() {
        if (!config.getStringList("blocks").isEmpty()) {
            for (String stringLoc : config.getStringList("blocks")) {
                Location location = Utils.blockLocationFromString(stringLoc);
                if (location != null) this.lockedBlocks.add(location);
            }
        }
    }

    public void save() {
        List<String> serializedLocs = new ArrayList<>();
        this.lockedBlocks.forEach(location -> {
            if (location != null)
                serializedLocs.add(Utils.blockLocationToString(location));
        });
        config.set("blocks", serializedLocs);
        Utils.saveConfig(config, "drugblocks");
    }

    public Collection<Location> getLocations() {
        return this.lockedBlocks;
    }

}
