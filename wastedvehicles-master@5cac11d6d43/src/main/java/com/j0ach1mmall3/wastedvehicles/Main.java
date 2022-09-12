package com.j0ach1mmall3.wastedvehicles;

import com.j0ach1mmall3.jlib.commands.Command;
import com.j0ach1mmall3.jlib.inventory.JLibItem;
import com.j0ach1mmall3.jlib.nms.nbt.NBTTag;
import com.j0ach1mmall3.jlib.plugin.JLibPlugin;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleCreateEvent;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.*;
import com.j0ach1mmall3.wastedvehicles.commands.FixVehiclesCommandHandler;
import com.j0ach1mmall3.wastedvehicles.commands.GiveVehicleCommandHandler;
import com.j0ach1mmall3.wastedvehicles.commands.WVReloadCommandHandler;
import com.j0ach1mmall3.wastedvehicles.config.Config;
import com.j0ach1mmall3.wastedvehicles.listeners.JetpackListener;
import com.j0ach1mmall3.wastedvehicles.listeners.VehicleListener;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class Main extends JLibPlugin<Config> {
    private final Set<ArmorStand> entityQueue = Collections.newSetFromMap(new ConcurrentHashMap<>());

    @Override
    public void onEnable() {
        this.reload();
        new JetpackListener(this);
        new VehicleListener(this);
        new GiveVehicleCommandHandler(this).registerCommand(new Command("GiveVehicle", "wv.givevehicle", ChatColor.RED + "Usage: /givevehicle <identifier> <player>"));
        new WVReloadCommandHandler(this).registerCommand(new Command("WVReload", "wv.reload", ChatColor.RED + "Usage: /wvreload"));
        new FixVehiclesCommandHandler(this).registerCommand(new Command("fixvehicles", "wv.fix", ChatColor.RED + "Usage: /fixvehicles"));
        new BukkitRunnable() {
            @Override
            public void run() {
                for(World world : Bukkit.getWorlds()) {

                    if (world.getName().equals("spawn")) continue;

                    for (Entity entity : world.getEntities()) {
                        if (entity.getType() != EntityType.ARMOR_STAND) continue;
                        if (entity.hasMetadata("WastedVehicle") || entity.hasMetadata("WastedVehiclePassenger"))
                            continue;
                        if(entity.hasMetadata("CUSTOM")) continue;
                        if (entity.getCustomName() == null || entity.getCustomName().isEmpty()) {
                            entity.remove();
                        }
                    }
                }
            }
        }.runTaskLater(this, 500);

        if(Core.getSettings().getType() != ServerType.GTM) return;
        ServerUtil.runTaskLater(() -> {
            for(VehicleProperties vehicle : this.config.getVehicleProperties()) {
                Core.log(vehicle.getIdentifier());

                World world = Bukkit.getWorld("spawn");
                if(world == null) continue;

                Location loc = null;
                switch (vehicle.getIdentifier()) {
                    case "Zentorno": loc = new Location(world, -277.5, 26, 226.5, 0, 0); break;//
                    case "Entity_XF": loc = new Location(world, -287.5, 26, 228.5, 0, 0); break;
                    case "9F": loc = new Location(world, -272.5, 26, 225.9, 0, 0); break;//
                    case "ArmoredKuruma": loc = new Location(world, -267.5, 26, 224.5, 0, 0); break;//
                    case "Primo": loc = new Location(world, -282.5, 26, 227.5, 0, 0); break;//
                    case "BMX": loc = new Location(world, -296.5, 26, 224.5, -180, 0); break;//
                    case "Rhino": loc = new Location(world, -269.5, 26, 240.5, 90, 0); break;//
                    case "Hydra": loc = new Location(world, -286.5, 26.3, 213.5, 0, 20); break;//
                    case "Maverick": loc = new Location(world, -271.5, 26, 213.5, 0, 0); break;//
                    case "AttackMaverick": loc = new Location(world, -279.5, 26, 213.5, 0, 0); break;//
                    case "Dinghy": loc = new Location(world, -264.25, 24.4, 213.5, 0, 0); break;//
//                    case "Akuma": loc = new Location(world, ); break;

                    default: break;
                }

                //If location is null, continue the loop.
                if(loc == null) continue;
                if (!loc.getChunk().isLoaded()) loc.getChunk().load();

                //Remove existing entities if any.
                world.getNearbyEntities(loc, 1, 1, 1).stream().filter(entity -> entity instanceof ArmorStand).forEach(Entity::remove);

                //Create new showcase entity.
                ArmorStand stand = world.spawn(loc, ArmorStand.class);
                stand.setHelmet(vehicle.getItem() == null ? new ItemStack(Material.DIRT) : vehicle.getItem());
                stand.setMetadata("VehicleStatue", new FixedMetadataValue(this, vehicle));
                stand.setMetadata("CUSTOM", new FixedMetadataValue(this, VehicleProperties.class));
                stand.setInvulnerable(true);
                stand.setGravity(false);
                stand.setVisible(false);
                stand.setCanPickupItems(false);
                stand.setArms(false);
                stand.setBasePlate(false);
                stand.setAI(false);
                stand.setMarker(true);
                stand.setRemoveWhenFarAway(false);
            }
        }, 15*20);
    }

    @Override
    public void onDisable() {
        for(World world : Bukkit.getWorlds()) {

            if (world.getName().equals("spawn")) continue;

            for (Entity entity : world.getEntities()) {
                if (entity.getType() != EntityType.ARMOR_STAND) continue;
                if (entity.hasMetadata("WastedVehicle")) {
                    WastedVehicle vehicle = (WastedVehicle) entity.getMetadata("WastedVehicle").get(0).value();
                    vehicle.onDestroy((ArmorStand) entity);
                }
                if (entity.hasMetadata("WastedVehiclePassenger")) {
                    entity.remove();
                }
            }
        }
    }

    public void reload() {
        this.config = new Config(this);
    }

    public Set<ArmorStand> getEntityQueue() {
        return this.entityQueue;
    }

    public Optional<VehicleProperties> getVehicle(String identifier) {
        return this.config.getVehicleProperties().stream().filter(v -> v.getIdentifier().equalsIgnoreCase(identifier)).findFirst();
    }

    public Optional<VehicleProperties> getVehicle(ItemStack itemStack) {
        return this.config.getVehicleProperties().stream().filter(v -> Objects.equals(v.getItem().getData(), itemStack.getData()) && itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName()).findFirst();
    }

    public ItemStack addHealth(ItemStack itemStack, double health) {
        try {
            JLibItem jLibItem = new JLibItem(itemStack);
            NBTTag nbtTag = jLibItem.getNBTTag();
            Map<String, NBTTag> map = nbtTag.getMap();
            map.put("WastedVehicleHealth", new NBTTag(NBTTag.DOUBLE, (Object) health));
            nbtTag.setMap(map);
            jLibItem.setNbtTag(nbtTag);
            return jLibItem.getItemStack();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArmorStand spawnVehicle(VehicleProperties vehicleProperties, Location location, Player player, double health) {
        WastedVehicle vehicle = null;
        switch (vehicleProperties.getVehicleType()) {
            case CAR:
                vehicle = new Car(this, vehicleProperties);
                break;
            case HELICOPTER:
                vehicle = new Helicopter(this, vehicleProperties);
                break;
            case PLANE:
                vehicle = new Plane(this, vehicleProperties);
                break;
            case BOAT:
                vehicle = new Boat(this, vehicleProperties);
            break;
            case SUBMARINE:
                vehicle = new Submarine(this, vehicleProperties);
                break;
        }
        VehicleCreateEvent event = new VehicleCreateEvent(vehicle, player);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            ArmorStand armorStand = location.getWorld().spawn(location.add(0, 1, 0), ArmorStand.class);
            vehicle.onCreate(armorStand, player, location, health);
            armorStand.setMetadata("WastedVehicle", new FixedMetadataValue(this, vehicle));
            this.entityQueue.add(armorStand);
            return armorStand;
        }
        return null;
    }

    public String formatHealth(double health, double maxHealth) {
        double amount = Math.floor((health <= 1 ? 0 : health) / maxHealth * 10);
        ChatColor color = amount > 6 ? ChatColor.GREEN : amount > 4 ? ChatColor.YELLOW : amount > 2 ? ChatColor.RED : ChatColor.DARK_RED;
        String s = color.toString();
        for(int i = 0; i < amount; i++) {
            s += "▍";
        }
        s += ChatColor.GRAY;
        for(int i = 0; s.length() < 14; i++) {
            s += "▍";
        }
        return color + s;
    }
}
