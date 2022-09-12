package net.grandtheftmc.vice.lootcrates;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.drugs.DrugService;
import net.grandtheftmc.vice.drugs.items.DrugItem;
import net.grandtheftmc.vice.items.GameItem;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CrateManager {

    public CrateManager() {
        this.loadCrates();
        this.startSchedule();
    }

    protected HashMap<Area.DropType, List<LootItem>> dropItems = Maps.newHashMap();
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
        YamlConfiguration c = Vice.getSettings().getLootConfig();
        this.items = new ArrayList<>();
        for (String s : c.getKeys(false)) {
            try {
                GameItem item = Vice.getItemManager().getItem(s);
                if (item == null) {
                    Core.error("Error loading game items '" + s + "' for Loot Crates!");
                    continue;
                }
                double chance = 100;
                if (c.getString(s + ".chance") != null)
                    chance = c.getDouble(s + ".chance");
                int min = 1;
                if (c.getString(s + ".min") != null)
                    min = c.getInt(s + ".min");
                int max = 64;
                if (c.getString(s + ".max") != null)
                    max = c.getInt(s + ".max");

                Area.DropType type = Area.DropType.DEFAULT;
                if (c.getString(s + ".type") != null)
                    type = Area.DropType.valueOf(c.getString(s + ".type"));

                this.items.add(new LootItem(s, chance, min, max, false, type));
            } catch (Exception e) {
                Core.error("Error loading loot items '" + s + "' for Loot Crates!");
            }
        }

//        for(DrugItem drugItem : ((DrugService)Vice.getInstance().getDrugManager().getService()).getAllDrugItems()){
//            this.items.add(new LootItem(drugItem.getDrug().getName(), 8, 1, 5, true));
//        }

        for (Area.DropType dropType : Area.DropType.values()) {
            List<LootItem> temp = new ArrayList<>();
            YamlConfiguration config = Utils.loadConfig(dropType.name() + "_loot");
            for (String s : c.getKeys(false)) {
                try {
                    GameItem item = Vice.getItemManager().getItem(s);
                    if (item == null) {
                        Core.error("Error loading game items '" + s + "' for Loot Crates!");
                        continue;
                    }
                    double chance = 100;
                    if (c.getString(s + ".chance") != null)
                        chance = c.getDouble(s + ".chance");
                    int min = 1;
                    if (c.getString(s + ".min") != null)
                        min = c.getInt(s + ".min");
                    int max = 64;
                    if (c.getString(s + ".max") != null)
                        max = c.getInt(s + ".max");

                    Area.DropType type = Area.DropType.DEFAULT;
                    if (c.getString(s + ".type") != null)
                        type = Area.DropType.valueOf(c.getString(s + ".type"));

                    temp.add(new LootItem(s, chance, min, max, false, type));
                } catch (Exception e) {
                    Core.error("Error loading loot items '" + s + "' for Loot Crates!");
                }
            }

            this.dropItems.put(dropType, temp);
        }

        c = Vice.getSettings().getLootCratesConfig();
        this.crates = new ArrayList<>();
        if (c.get("lootcrates") != null)
            this.crates.addAll(c.getStringList("lootcrates").stream().map(s -> new LootCrate(Utils.blockLocationFromString(s))).collect(Collectors.toList()));
        this.cooldown = c.getInt("cooldown");
    }

    public void saveCrates() {
        YamlConfiguration c = Vice.getSettings().getLootConfig();
        for (String s : c.getKeys(false))
            c.set(s, null);
        for (LootItem item : this.items) {
            if(!item.isDrug()) {
                String s = item.getItem();
                c.set(s + ".chance", item.getChance());
                c.set(s + ".min", item.getMin());
                c.set(s + ".max", item.getMax());
                c.set(s + ".type", item.getDropType().name());
            }
        }
        Utils.saveConfig(c, "loot");

        c = Vice.getSettings().getLootCratesConfig();
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
                Vice.getCrateManager().getCrates().forEach(LootCrate::tick);
            }
        }.runTaskTimer(Vice.getInstance(), 20, 20).getTaskId();
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
