package net.grandtheftmc.gtm.drugs;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.drugs.item.DrugDealerItem;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class DrugDealer {
    private final Collection<Location> dealerLocations = new ArrayList<>();
    private final Set<DrugDealerItem> drugDealerItems = new HashSet<>();
    private final BukkitTask dealerTask;
    private ArmorStand stand = null;

    public DrugDealer() {
        loadLocations();
        loadDealerItems();
        dealerTask = new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    if (stand != null) {
                        Collection<ArmorStand> dealers = new ArrayList<>();
                        for (LivingEntity entity : stand.getWorld().getLivingEntities()) {
                            if (entity.getType() != EntityType.ARMOR_STAND) continue;
                            ArmorStand armorStand = (ArmorStand) entity;
                            if (armorStand.getHelmet().getType() == GTM.getItemManager().getItem("rastahat").getItem().getType()
                                    && armorStand.getItemInHand().getType() == GTM.getItemManager().getItem("joint").getItem().getType()) {
                                dealers.add(armorStand);
                            }
                        }
                        if (!dealers.isEmpty()) {
                            for (ArmorStand armorStand : dealers) {
                                armorStand.setHealth(0);
                                armorStand.remove();
                            }
                        }
                    }
                    Optional<Location> randomLoc = getRandomLoc();
                    randomLoc.ifPresent(location -> setLocation(location));
                } catch (Exception e) {
                    e.printStackTrace();
                    cancel();
                }
            }
        }.runTaskTimer(GTM.getInstance(), 200, 34000);
    }

    public void stop() {
        saveLocations();
        if (stand != null) stand.remove();
        dealerTask.cancel();
    }

    public boolean isDrugDealer(Entity e) {
        return e.equals(stand);
    }

    public void addDealerLoc(Location loc) {
        dealerLocations.add(loc);
    }

    public void loadDealerItems() {
        YamlConfiguration c = GTM.getSettings().getDrugDealerConfig();
        DrugService service = (DrugService) GTM.getDrugManager().getService();
        service.getDrugs().stream().forEach(drug -> {
            DrugItem item = DrugItem.getByDrug(drug);
            if (item != null) {
                int stockMin = c.getString("drugs." + drug.getName().toLowerCase() + ".min") != null ? c.getInt("drugs." + drug.getName().toLowerCase() + ".min") : 1;
                int stockMax = c.getString("drugs." + drug.getName().toLowerCase() + ".max") != null ? c.getInt("drugs." + drug.getName().toLowerCase() + ".max") : 10;
                int chance = c.getString("drugs." + drug.getName().toLowerCase() + ".chance") != null ? c.getInt("drugs." + drug.getName().toLowerCase() + ".chance") : 5;
                int minPrice = c.getString("drugs." + drug.getName().toLowerCase() + ".minprice") != null ? c.getInt("drugs." + drug.getName().toLowerCase() + ".minprice") : -1;
                int maxPrice = c.getString("drugs." + drug.getName().toLowerCase() + ".maxprice") != null ? c.getInt("drugs." + drug.getName().toLowerCase() + ".maxprice") : -1;

                if (stockMax == -1 || stockMin == -1 || chance == -1 || minPrice == -1 || maxPrice == -1) {
                    GTM.getInstance().getLogger().log(Level.SEVERE, "Unable to parse number for drug: " + drug.getName().toLowerCase() + " min=" + stockMin + " max=" + stockMax + " chance=" + chance + " minPrice=" + minPrice + " maxPrice=" + maxPrice);
                }
                drugDealerItems.add(new DrugDealerItem(item, stockMin, stockMax, chance, minPrice, maxPrice));
            } else {
                GTM.getInstance().getLogger().log(Level.SEVERE, "Unable to find DrugItem for drug: " + drug);
            }
        });
    }

    public void rerollStock() {
        drugDealerItems.stream().forEach(DrugDealerItem::reroll);
    }

    public void loadLocations() {
        GTM.getSettings().setDrugDealerConfig(Utils.loadConfig("drugdealer"));
        dealerLocations.clear();
        YamlConfiguration c = GTM.getSettings().getDrugDealerConfig();
        if (!c.getStringList("locs").isEmpty()) {
            for (String loc : c.getStringList("locs")) {
                GTMUtils.deserializeLocation(loc).ifPresent(dealerLocations::add);
            }
        }
    }

    public void saveLocations() {
        YamlConfiguration c = GTM.getSettings().getDrugDealerConfig();
        List<String> locs = dealerLocations.stream().map(GTMUtils::serializeLocation).collect(Collectors.toList());
        c.set("locs", locs);
        Utils.saveConfig(c, "drugdealer");
    }

    public Set<DrugDealerItem> getItems() {
        return drugDealerItems;
    }

    public Collection<Location> getDealerLocations() {
        return dealerLocations;
    }

    private void initStand(Location loc) {
        if (stand != null) {
            stand.setHealth(0);
            stand.remove();
        }
        stand = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setHealth(stand.getMaxHealth());
        stand.setCustomName(Utils.f("&8&lDrug Dealer"));
        stand.setCustomNameVisible(true);
        stand.setAI(false);
        stand.setCollidable(false);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);
        stand.setGravity(false);
        stand.setRemoveWhenFarAway(false);
        stand.setBasePlate(false);
        stand.setArms(true);
        stand.setVisible(true);
        stand.setHelmet(GTM.getItemManager().getItem("rastahat").getItem());
        stand.setChestplate(GTM.getItemManager().getItem("shirt").getItem());
        stand.setLeggings(GTM.getItemManager().getItem("pants").getItem());
        stand.setBoots(GTM.getItemManager().getItem("nikes").getItem());
        stand.setItemInHand(GTM.getItemManager().getItem("joint").getItem());
    }

    public void setLocation(Location loc) {
        if (stand == null || stand.isDead() || !stand.isVisible() || !stand.isValid()) initStand(loc);
        stand.teleport(loc);
        rerollStock();
    }

    public Optional<Location> getRandomLoc() {
        if (stand == null || this.dealerLocations.size() == 1) return this.dealerLocations.stream().findFirst();
        List<Location> locs = this.dealerLocations
                .stream()
                .filter(location -> !location.equals(stand.getLocation()))
                .collect(Collectors.toList());
        return Optional.of(locs.get(ThreadLocalRandom.current().nextInt(locs.size())));
    }

    public LivingEntity dealerStand() {
        return this.stand;
    }

}
