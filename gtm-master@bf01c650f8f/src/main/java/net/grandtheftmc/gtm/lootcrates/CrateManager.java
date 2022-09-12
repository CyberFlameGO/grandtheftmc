package net.grandtheftmc.gtm.lootcrates;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.guns.GTMGuns;

public class CrateManager {

    public CrateManager() {
        this.loadCrates();
        this.startSchedule();
    }

    private List<LootItem> items = new ArrayList<>();
    private List<LootCrate> crates = new ArrayList<>();
    private int cooldown = 30; // in MINUTES

    private int taskId = -1;

    public List<LootItem> getItems() {
        return this.items;
    }

    public List<LootCrate> getCrates() {
        return this.crates;
    }


    public void loadCrates() {
        YamlConfiguration c = GTM.getSettings().getLootConfig();
        this.items = new ArrayList<>();
        for (String identifier : c.getKeys(false)) {
        	
        	// get the game item id, if one exists, otherwise default to this identifier
        	String gameItemID = c.getString(identifier + ".gameitem", identifier);
            String type = c.getString(identifier + ".type");
            
            try {
                GameItem item = GTM.getItemManager().getItem(gameItemID);
                if (item == null && (type == null || !type.equals("SKIN"))) {
                    Core.error("Error loading game item identifier='" + identifier + "', gameItemID='" + gameItemID + "' for Loot Crates!");
                    continue;
                }
                
                double chance = 100;
                if (c.getString(identifier + ".chance") != null)
                    chance = c.getDouble(identifier + ".chance");
                int min = 1;
                if (c.getString(identifier + ".min") != null)
                    min = c.getInt(identifier + ".min");
                int max = 64;
                if (c.getString(identifier + ".max") != null)
                    max = c.getInt(identifier + ".max");
                
                // rarity rating for weapon
                int stars = 1;
                if (c.getString(identifier + ".stars") != null)
                	stars = c.getInt(identifier + ".stars");
                
                // clamp bounds
                if (stars < 1){
                	stars = 1;
                }
                if (stars > GTMGuns.MAX_STARS){
                	stars = GTMGuns.MAX_STARS; 
                }
                
                LootItem lootItem = new LootItem(identifier, gameItemID, chance, min, max, stars, false);
                this.items.add(lootItem);
            } catch (Exception e) {
                Core.error("Error loading loot item identifier='" + identifier + "', gameItemID='" + gameItemID + "' for Loot Crates!");
            }
        }

        if (!Core.getSettings().isSister()) {
            for (DrugItem drugItem : ((DrugService) GTM.getInstance().getDrugManager().getService()).getAllDrugItems()) {
                this.items.add(new LootItem(drugItem.drug().getName(), drugItem.drug().getName(), 8, 1, 5, 0, true));
            }
        }

        c = GTM.getSettings().getLootCratesConfig();
        this.crates = new ArrayList<>();
        if (c.get("lootcrates") != null)
            this.crates.addAll(c.getStringList("lootcrates").stream().map(s -> new LootCrate(Utils.blockLocationFromString(s))).collect(Collectors.toList()));
        this.cooldown = c.getInt("cooldown");
    }

    public void saveCrates() {
        YamlConfiguration c = GTM.getSettings().getLootConfig();
        for (String s : c.getKeys(false)) {
            if (c.contains(s + ".type")) continue;
            c.set(s, null);
        }
        for (LootItem item : this.items) {
            if (item.getIdentifier().contains("skin")) continue;

            if(!item.isDrug()) {
                String identifier = item.getIdentifier();
                
                // legacy compatibility
                if (identifier == null){
                	identifier = item.getItemName();
                }
                
                c.set(identifier + ".gameitem", item.getItemName());
                c.set(identifier + ".chance", item.getChance());
                c.set(identifier + ".min", item.getMin());
                c.set(identifier + ".max", item.getMax());
                c.set(identifier + ".stars", item.getStars());
            }
        }
        Utils.saveConfig(c, "loot");

        c = GTM.getSettings().getLootCratesConfig();
        List<String> list = this.crates.stream().map(crate -> Utils.blockLocationToString(crate.getLocation())).collect(Collectors.toList());
        c.set("lootcrates", list);
        c.set("cooldown", this.cooldown);
        Utils.saveConfig(c, "lootcrates");
    }

    public void startSchedule() {
        if (this.taskId != -1) Bukkit.getScheduler().cancelTask(this.taskId);
        this.taskId = new BukkitRunnable() {
            @Override
            public void run() {
                GTM.getCrateManager().getCrates().forEach(LootCrate::tick);
            }
        }.runTaskTimer(GTM.getInstance(), 20, 20).getTaskId();
    }

    public LootCrate getCrate(Location location) {
        return this.crates.stream().filter(crate -> Objects.equals(crate.getLocation(), location)).findFirst().orElse(null);
    }

    public void addCrate(Location location) {
        if (this.getCrate(location) == null)
            this.crates.add(new LootCrate(location));
    }

    public void removeCrate(Location location) {
        LootCrate crate = this.getCrate(location);
        if (crate != null) {
            crate.removeHologram();
            this.crates.remove(crate);
        }
    }

    public int getCooldown() {
        return this.cooldown;
    }

    public void setCooldown(int i) {
        this.cooldown = i;
    }

    public void addItem(LootItem lootItem) {
        this.items.add(lootItem);
    }

    public LootItem getItem(GameItem gameItem) {
        return this.items.stream().filter(item -> Objects.equals(item.getGameItem(), gameItem)).findFirst().orElse(null);
    }

    public void removeItem(LootItem lootItem) {
        this.items.remove(lootItem);
    }

}
