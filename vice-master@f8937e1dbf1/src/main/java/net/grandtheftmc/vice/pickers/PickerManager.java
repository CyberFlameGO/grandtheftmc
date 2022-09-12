package net.grandtheftmc.vice.pickers;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PickerManager {

    public PickerManager() {
        this.load();
    }

    private NPC copSalary;

    private void load() {
        YamlConfiguration c = Vice.getSettings().getPickersConfig();
        if (c.get("copSalary") != null) this.addCopSalary(Utils.teleportLocationFromString(c.getString("copSalary")));
    }


    public void addCopSalary(Location location) {
        if (location == null) return;
        if (this.copSalary == null) {
            this.copSalary = CitizensAPI.getNPCRegistry().createNPC(EntityType.VILLAGER, Utils.f("&3&lCarl"));
            this.copSalary.spawn(location);
            this.copSalary.setProtected(true);
        }
        this.copSalary.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public void save() {
        YamlConfiguration c = Vice.getSettings().getPickersConfig();
        c.set("copSalary", this.copSalary == null ? null : Utils.teleportLocationToString(this.copSalary.getStoredLocation()));
        if(this.copSalary!=null)
            this.copSalary.destroy();
        Utils.saveConfig(c, "pickers");
    }
}
