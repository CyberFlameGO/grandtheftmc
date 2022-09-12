package net.grandtheftmc.gtm.wastedbarrels;

import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

public class BarrelManager {
    private Collection<WastedBarrel> wastedBarrels;
    private Collection<Location> unloadedBarrels;

    public BarrelManager() {
        if (this.wastedBarrels == null) this.wastedBarrels = new ArrayList<>();
        if (this.unloadedBarrels == null) this.unloadedBarrels = new ArrayList<>();
        new BukkitRunnable() {
            @Override
            public void run() {
                BarrelManager.this.loadBarrels();
            }
        }.runTaskLater(GTM.getInstance(), 100);
    }

    public Collection<WastedBarrel> getWastedBarrels() {
        return this.wastedBarrels;
    }

    public Collection<Location> getUnloadedBarrels() {
        return this.unloadedBarrels;
    }

    public WastedBarrel spawnWastedBarrel(Location location) {
        ArmorStand armorStand = location.getWorld().spawn(location, ArmorStand.class);
        armorStand.setHelmet(new ItemStack(Material.TNT));
        armorStand.setBasePlate(false);
        armorStand.setSmall(false);
        armorStand.setArms(false);
        armorStand.setAI(false);
        armorStand.setCanPickupItems(false);
        armorStand.setGravity(false);
        armorStand.setMaxHealth(50.0D);
        armorStand.setHealth(50.0D);
        armorStand.setVisible(false);
        armorStand.setSilent(true);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setHelmet(new ItemStack(Material.TNT));

        try {
            Object handle = ReflectionAPI.getHandle((Object) armorStand);
            handle.getClass().getMethod("setSize", float.class, float.class).invoke(handle, 1.00F, 1.50F);
        } catch (Exception e) {
            e.printStackTrace();
        }

        WastedBarrel wastedBarrel = new WastedBarrel(armorStand);
        armorStand.setMetadata("WastedBarrel", new FixedMetadataValue(GTM.getInstance(), wastedBarrel));
        return wastedBarrel;
    }

    public void loadBarrels() {
        for (String string : GTM.getSettings().getBarrelsConfig().getStringList("barrels")) {
            Optional<Location> loc = GTMUtils.deserializeLocation(string);
            if (!loc.isPresent()) continue;
            Optional<ArmorStand> barrel = Arrays.stream(loc.get().getChunk().getEntities()).filter(entity -> !(entity.getLocation().distance(loc.get()) > 2)).filter(entity -> entity.getType() == EntityType.ARMOR_STAND).map(entity -> (ArmorStand) entity).filter(armorStand -> armorStand.getHelmet().getType() == Material.TNT).findFirst().map(Optional::of).orElse(Optional.empty());
            if (barrel.isPresent()) {
                new WastedBarrel(barrel.get());
            } else {
                this.unloadedBarrels.add(loc.get());
            }
        }
    }

    public void unloadBarrels() {
        YamlConfiguration barrelsConfig = GTM.getSettings().getBarrelsConfig();
        Collection<String> barrelLocations = new ArrayList<>();
        this.wastedBarrels.forEach(wastedBarrel -> {
            barrelLocations.add(GTMUtils.serializeLocation(wastedBarrel.getArmorStand().getLocation()));
            wastedBarrel.respawn();
        });
        barrelsConfig.set("barrels", barrelLocations);
        Utils.saveConfig(barrelsConfig, "barrels");
    }
}
