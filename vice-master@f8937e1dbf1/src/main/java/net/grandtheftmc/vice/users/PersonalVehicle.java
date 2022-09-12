package net.grandtheftmc.vice.users;

import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.events.TPEvent;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Created by Liam on 24/09/2016.
 */
public class PersonalVehicle {

    private String vehicle;
    private double health = -1;
    private UUID entityUUID;
    private boolean stolen;

    public PersonalVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getVehicle() {
        return this.vehicle;
    }

    public void setVehicle(String vehicle) {
        this.vehicle = vehicle;
    }

    public String getDisplayName() {
        VehicleProperties p = this.getVehicleProperties();
        return p == null ? "Error" : p.getItem().getItemMeta().getDisplayName();
    }

    public VehicleProperties getVehicleProperties() {
        Optional<VehicleProperties> opt = Vice.getWastedVehicles().getVehicle(this.vehicle);
        return opt.orElse(null);
    }

    public String getFormattedHealth() {
        if (this.isDestroyed()) return "&c&lDestroyed";
        return Vice.getWastedVehicles().formatHealth(this.getHealth(), this.getVehicleProperties().getMaxHealth());
    }

    public double getHealth() {
        return this.health < 0 ? this.getVehicleProperties().getMaxHealth() : this.health;
    }

    public boolean isStolen() {
        if (this.getEntity() == null) this.stolen = false;
        return this.stolen;
    }

    public double getPrice() {
        GameItem item = Vice.getItemManager().getItemFromVehicle(this.vehicle);
        return item == null ? -1 : item.getSellPrice() * 2;
    }

    public double getSellPrice() {
        return this.getPrice() / 2 - this.getRepairPrice();
    }

    public double getRepairPrice() {
        if(!this.isDestroyed()) return 0;
        return this.getPrice() / 5;
    }

    public void setStolen(boolean b) {
        this.stolen = b;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public UUID getEntityUUID() {
        return this.entityUUID;
    }

    public void setEntityUUID(UUID entityUUID) {
        this.entityUUID = entityUUID;
    }

    public ArmorStand getEntity() {
        if (this.entityUUID == null) return null;
        for (World world : Bukkit.getWorlds())
            for (ArmorStand e : world.getEntitiesByClass(ArmorStand.class))
                if (Objects.equals(e.getUniqueId(), this.entityUUID)) {
                    this.health = e.getHealth();
                    return e;
                }
        this.entityUUID = null;
        return null;
    }

    public boolean isDestroyed() {
        return this.getHealth() <= 1;
    }

    public void updateVehicleInDatabase(Player player, double health) {
        VehicleProperties v = this.getVehicleProperties();
        this.health = health;
        if (v != null) {
            ServerUtil.runTaskAsync(() -> ViceUserDAO.updateVehicles(player.getUniqueId(), v, this.isStolen() ? 0 : this.health));
//            Core.sql.updateAsyncLater("update " + Core.name() + " set `" + v.getIdentifier().toLowerCase() + ":info`='" + (this.isStolen() ? 0 : this.health) + "' where uuid='" + player.getUniqueId() + "';");
        }
    }

    public boolean onMap() {
        return this.getEntity() != null;
    }

    public void call(Player player, User user, ViceUser viceUser) {
        this.teleport(player, user, viceUser, false);
    }

    public void sendAway(Player player, User user, ViceUser viceUser) {
        this.teleport(player, user, viceUser, true);
    }

    public void teleport(Player player, User user, ViceUser viceUser, boolean sendAway) {
        ViceUtils.giveGameItems(player);
        UUID uuid = player.getUniqueId();
        if (viceUser.getVehicleTaskId() != -1)
            Bukkit.getScheduler().cancelTask(viceUser.getVehicleTaskId());
        if (viceUser.isInCombat()) {
            player.sendMessage(Lang.COMBATTAG.f("&7You can't " + (sendAway ? "send away" : "call") + " your vehicle in combat!"));
            return;
        }
        if (user.isInTutorial()) return;
        if (!sendAway && Objects.equals("spawn", player.getWorld().getName())) {
            player.sendMessage(Lang.VEHICLES.f("&7You can't call vehicles to spawn!"));
            return;
        }
        if (viceUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't " + (sendAway ? "send away" : "call") + " your vehicle in jail!"));
            return;
        }
        if (this.isDestroyed()) {
            player.sendMessage(Lang.VEHICLES.f("&7Your " + this.getDisplayName() + "&7 was destroyed, call the mechanic to fix it first!"));
            return;
        }
        if (sendAway && !this.onMap()) {
            player.sendMessage(Lang.VEHICLES.f("&7Your vehicle can not be sent away!"));
            return;
        }
        if (this.stolen) {
            player.sendMessage(Lang.VEHICLES.f("&7Your " + this.getDisplayName() + "&7 was stolen!"));
            return;
        }
        if (!viceUser.hasMoney(200)) {
            player.sendMessage(Lang.VEHICLES.f("&7You can't afford to pay &c$&l200&7 for driver!"));
            return;
        }
        player.sendMessage(Lang.VEHICLES.f("&7A driver is coming to " + (sendAway ? "pick up" : "drop off") + " your " + this.getDisplayName() + "&7!"));
        viceUser.setVehicleTimer(ViceUtils.getWarpDelay(user.getUserRank()));
        viceUser.setBooleanToStorage(BooleanStorageType.SEND_AWAY, sendAway);
        viceUser.setVehicleTaskId(new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null) {
                    this.cancel();
                    return;
                }
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(uuid);
                int timer = viceUser.getVehicleTimer();
                boolean sendAway = viceUser.getBooleanFromStorage(BooleanStorageType.SEND_AWAY);
                PersonalVehicle vehicle = viceUser.getPersonalVehicle();

                if (timer == 15 || timer == 10 || (timer <= 5 && timer > 0)) {
                    player.sendMessage(Lang.VEHICLES.f("&7Your vehicle is " + (sendAway ? "being picked up" : "arriving") + " in " + timer + " &7second"
                            + (timer == 1 ? "" : "s") + '!'));
                    if (timer == 1)
                        player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.5F, 1);
                    else
                        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F / timer, 2);
                }
                if (timer == 0) {
                    if (vehicle == null) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f("&7You have no vehicle to teleport!"));
                        return;
                    }
                    if (viceUser.isInCombat()) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.COMBATTAG.f("&7You can't " + (sendAway ? "send away" : "call") + " your vehicle in combat!"));
                        return;
                    }
                    if (user.isInTutorial()) return;
                    if (viceUser.isArrested()) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.JAIL.f("&7You can't " + (sendAway ? "send away" : "call") + " your vehicle in jail!"));
                        return;
                    }
                    if(Objects.equals("spawn", player.getWorld().getName())) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.JAIL.f("&7You can't " + (sendAway ? "send away" : "call") + " your vehicle in spawn!"));
                        return;
                    }
                    if (vehicle.isDestroyed()) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f("&7Your " + vehicle.getDisplayName() + "&7 was destroyed, call the mechanic to fix it first!"));
                        return;
                    }
                    if (sendAway && !vehicle.onMap()) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f("&7Your vehicle can not be sent away!"));
                        return;
                    }
                    if (vehicle.stolen) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f("&7Your " + vehicle.getDisplayName() + "&7 was stolen!"));
                        return;
                    }
                    if (!viceUser.hasMoney(200)) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f("&7You can't afford to pay &c$&l200&7 for driver!"));
                        return;
                    }
                    TPEvent e = new TPEvent(player, player,
                            sendAway ? TPEvent.TPType.VEHICLE_SEND_AWAY : TPEvent.TPType.VEHICLE_CALL).call();
                    if (e.isCancelled()) {
                        viceUser.cancelVehicleTeleport();
                        player.sendMessage(Lang.VEHICLES.f(e.getCancelMessage()));
                        return;
                    }
                    PersonalVehicle next = viceUser.getNextVehicle();
                    viceUser.cancelVehicleTeleport();
                    if (sendAway) {
                        if (!vehicle.sendAway(player, true)) return;
                    } else if (!vehicle.teleport(player, true)) return;
                    viceUser.takeMoney(200);
                    player.sendMessage(Lang.MONEY_TAKE.f("200"));
                    ViceUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), viceUser);
                    if (sendAway && next != null) {
                        viceUser.setPersonalVehicle(null);
                        viceUser.setNextVehicle(null);
                        viceUser.setPersonalVehicle(player, Core.getUserManager().getLoadedUser(uuid), next);
                    }
                    MenuManager.updateMenu(player, "vehicles");
                    MenuManager.updateMenu(player, "personalvehicle");
                    return;
                }
                viceUser.setVehicleTimer(timer - 1);
            }
        }.runTaskTimer(Vice.getInstance(), 20, 20).getTaskId());
    }

    private Location getLocationAround(Player player) {
        VehicleProperties prop = this.getVehicleProperties();
        for (int i = 0; i < 50; i++) {
            Location l = player.getLocation().add(Random.getInt(-7, 7), 0, Random.getInt(-7, 7));
            Material m = l.getBlock().getRelative(BlockFace.DOWN).getType();
            if (m == Material.AIR)
                m = l.getBlock().getRelative(BlockFace.DOWN).getRelative(BlockFace.DOWN).getType();
            if (prop.getAllowedBlocks().contains(m.toString())) return l;
        }
        return null;
    }

    private boolean teleport(Player player) {
        return this.teleport(player, false);
    }

    private boolean teleport(Player player, boolean sendMessage) {
        ArmorStand e = this.getEntity();
        if (e == null) {
            VehicleProperties v = this.getVehicleProperties();
            if (v == null) {
                if (sendMessage)
                    player.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                return false;
            }
            Location loc = this.getLocationAround(player);
            if (loc == null) {
                player.sendMessage(Lang.VEHICLES.f("&7Your vehicle can not be placed near you!"));
                return false;
            }
            ArmorStand wv = Vice.getWastedVehicles().spawnVehicle(v, loc, player, (this.health > v.getMaxHealth() || this.health < 0) ? v.getMaxHealth() : this.health);
            this.entityUUID = wv.getUniqueId();
            if (sendMessage)
                player.sendMessage(Lang.VEHICLES.f("&7The driver dropped off your vehicle!"));
            return true;
        }
        if (this.stolen) {
            if (sendMessage)
                player.sendMessage(Lang.VEHICLES.f("&7Your " + this.getDisplayName() + "&7 was stolen!"));
            return false;
        }
        Location loc = this.getLocationAround(player);
        if (loc == null) {
            player.sendMessage(Lang.VEHICLES.f("&7Your vehicle can not be placed near you!"));
            return false;
        }
        e.teleport(loc);
        if (sendMessage)
            player.sendMessage(Lang.VEHICLES.f("&7The driver dropped off your vehicle!"));
        return true;
    }

    private boolean sendAway(Player player) {
        return this.sendAway(player, false);
    }

    private boolean sendAway(Player player, boolean sendMessage) {
        ArmorStand e = this.getEntity();
        if (this.isDestroyed()) {
            if (sendMessage)
                player.sendMessage(Lang.VEHICLES.f("&7Your " + this.getDisplayName() + "&7 is destroyed!"));
            return false;
        }
        if (e == null) {
            if (sendMessage)
                player.sendMessage(Lang.VEHICLES.f("&7You can't send away your vehicle!"));
            return false;
        }
        if (e.getPassenger() != null) {
            if (sendMessage)
                player.sendMessage(Lang.VEHICLES.f("&7Your vehicle is not empty!"));
            return false;
        }
        ((WastedVehicle) e.getMetadata("WastedVehicle").get(0).value()).onDismount(e);
        ((WastedVehicle) e.getMetadata("WastedVehicle").get(0).value()).getPassengers().forEach(passenger -> {
            passenger.eject();
            passenger.remove();
        });
        Vice.getWastedVehicles().getEntityQueue().remove(e);
        e.remove();
        this.entityUUID = null;
        if (sendMessage)
            player.sendMessage(Lang.VEHICLES.f("&7The driver picked up your vehicle!"));
        return true;
    }


}
