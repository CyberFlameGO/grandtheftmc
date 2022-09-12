package net.grandtheftmc.vice.world;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.world.obsidianbreaker.ObsidianManager;
import net.grandtheftmc.vice.world.warps.WarpManager;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Timothy Lampen on 8/25/2017.
 */
public class WorldManager {

    private List<ViceSelection> zones =new ArrayList<>();
    private final ObsidianManager om;
    private final WarpManager wm;

    public WorldManager(){
        om = new ObsidianManager();
        wm = new WarpManager();
    }

    public void load() {
        wm.loadWarps();
        this.loadZones();
    }

    public void save() {
        wm.saveWarps();
        this.saveZones();
    }

    private void saveZones(){
        YamlConfiguration c = Vice.getSettings().getZoneConfig();
        for(String s : c.getConfigurationSection("").getKeys(false)) {
            c.set(s, null);//clearing past zones
        }
        for(ViceSelection zone : this.zones) {
            StringBuilder flags = new StringBuilder();
            if(zone.getFlags().size()>0) {
                zone.getFlags().forEach(flag -> {
                    flags.append(flag + ":");
                });
                flags.deleteCharAt(flags.length() - 1);
            }
            c.set(zone.getName() + ".flags", flags.toString());
            c.set(zone.getName() + ".highcorner", ViceUtils.serializeLocation(zone.getHighCorner()));
            c.set(zone.getName() + ".lowcorner", ViceUtils.serializeLocation(zone.getLowCorner()));
        }
        Utils.saveConfig(c, "zones");
    }

    private void loadZones(){
        YamlConfiguration c = Vice.getSettings().getZoneConfig();
        if(c.getConfigurationSection("")==null)
            return;
        for(String name : c.getConfigurationSection("").getKeys(false)) {
            List<ZoneFlag> flags = new ArrayList<>();
            String seralizedFlags = c.getString(name + ".flags");
            if(seralizedFlags.contains(":")) {
                for(String s : seralizedFlags.split(":")) {
                    flags.add(ZoneFlag.valueOf(s));
                }
            }
            else {
                flags.add(ZoneFlag.valueOf(seralizedFlags));
            }
            Location highCorner = ViceUtils.deserializeLocation(c.getString(name + ".highcorner")).get();
            Location lowCorner = ViceUtils.deserializeLocation(c.getString(name + ".lowcorner")).get();
            this.zones.add(new ViceSelection(name, flags, lowCorner, highCorner, false));

        }
    }

    public Optional<ViceSelection> getZone(String name) {
        return this.zones.stream().filter(zone -> zone.getName().equalsIgnoreCase(name)).findFirst();
    }

    public List<ViceSelection> getZones(Location loc) {
        return this.zones.stream().filter(zone -> zone.isInRegion(loc)).collect(Collectors.toList());
    }

    public ObsidianManager getObsidianManager() {
        return om;
    }


    public List<ViceSelection> getZones() {
        return zones;
    }

    public void addZone(ViceSelection selection) {
        this.zones.add(selection);
    }

    public boolean removeZone(String name) {
        List<ViceSelection> clone = this.zones;
        for(ViceSelection z : clone) {
            if(z.getName().equalsIgnoreCase(name)) {
                this.zones.remove(z);
                return true;
            }
        }
        return false;
    }

    public WarpManager getWarpManager() {
        return wm;
    }
}
