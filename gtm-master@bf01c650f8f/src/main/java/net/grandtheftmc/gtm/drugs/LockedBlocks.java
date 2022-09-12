package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class LockedBlocks {
    private Collection<Location> lockedBlocks;
    private YamlConfiguration config;

    public LockedBlocks() {
        this.lockedBlocks = new ArrayList<>();
        this.config = GTM.getSettings().getDrugBlocksConfig();
        load();
    }

    public void load() {
        if (!config.getStringList("blocks").isEmpty()) {
            for (String stringLoc : config.getStringList("blocks")) {
                Optional<Location> location = GTMUtils.deserializeLocation(stringLoc);
                if (location.isPresent()) this.lockedBlocks.add(location.get());
            }
        }
    }

    public void save() {
        List<String> serializedLocs = new ArrayList<>();
        this.lockedBlocks.forEach(location -> {
            serializedLocs.add(GTMUtils.serializeLocation(location));
        });
        config.set("blocks", serializedLocs);
        Utils.saveConfig(config, "drugblocks");
    }

    public Collection<Location> getLocations() {
        return this.lockedBlocks;
    }

}
