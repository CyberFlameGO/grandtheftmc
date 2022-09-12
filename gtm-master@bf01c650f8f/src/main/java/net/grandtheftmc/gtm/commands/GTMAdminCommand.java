package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import net.grandtheftmc.gtm.users.CompassTarget;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.wastedbarrels.WastedBarrel;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import net.grandtheftmc.houses.users.UserHouseChest;
import org.bukkit.Bukkit;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class GTMAdminCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        if (!s.hasPermission("command.admin")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/gtmadmin setkillcounter <player> <amnt>"));
            s.sendMessage(Utils.f("&c/gtmadmin release <player>"));
            s.sendMessage(Utils.f("&c/gtmadmin target <player>"));
            s.sendMessage(Utils.f("&c/gtmadmin resetjobdelay <player>"));
            s.sendMessage(Utils.f("&c/gtmadmin kitexpiries <player>"));
            s.sendMessage(Utils.f("&c/gtmadmin trashcan"));
            s.sendMessage(Utils.f("&c/gtmadmin barrel <add/remove>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "barrel": {
                if (args.length == 1) {
                    player.sendMessage(Utils.f("&c/gtmadmin barrel add - &7Add a Flammable Barrel at your location"));
                    player.sendMessage(Utils.f("&c/gtmadmin barrel remove - &7Remove the Flammable Barrel nearest to you"));
                    return true;
                }
                if ("spawn".equalsIgnoreCase(player.getWorld().getName())) {
                    player.sendMessage(Utils.f("&7Barrels cannot be in Spawn."));
                    return true;
                }
                if ("add".equalsIgnoreCase(args[1])) {
                    GTM.getBarrelManager().spawnWastedBarrel(player.getLocation()).getArmorStand().playEffect(EntityEffect.WOLF_HEARTS);
                } else if ("remove".equalsIgnoreCase(args[1])) {
                    player.getNearbyEntities(10, 10, 10).stream()
                            .filter(entity -> entity.getType() == EntityType.ARMOR_STAND)
                            .forEach(entity -> {
                                if (((ArmorStand) entity).getHelmet().getType() != Material.TNT) return;
                                if (!entity.hasMetadata("WastedBarrel")) return;
                                ArmorStand armorStand = (ArmorStand) entity;
                                armorStand.setHelmet(null);
                                armorStand.remove();
                                player.sendMessage(armorStand.getLocation().toString());
                                WastedBarrel wastedBarrel = (WastedBarrel) armorStand.getMetadata("WastedBarrel").get(0).value();
                                GTM.getBarrelManager().getWastedBarrels().remove(wastedBarrel);
                            });
                    player.sendMessage(Utils.f("&7Barrel(s) removed!"));
                } else {
                    player.sendMessage(Utils.f("&c/gtmadmin barrel add - &7Add a Flammable Barrel at your location"));
                    player.sendMessage(Utils.f("&c/gtmadmin barrel remove - &7Remove the Flammable Barrel nearest to you"));
                }
                return true;
            }
            case "trashcan": {
                ItemStack item = Utils.createItem(Material.DROPPER, "&7&lTrash Can");
                //Utils.b(ArmorUpgrade.TANK.getEnchantment().getName());
               // item.addUnsafeEnchantment(ArmorUpgrade.TANK.getEnchantment(), 1);
                for (Enchantment e : item.getEnchantments().keySet())
                    Utils.b(e.getName());
                player.getInventory().addItem(item);
                s.sendMessage(Utils.f("&7A Trash Can was added to your inventory. Place it so players can sell items in it."));
                return true;
            }
            case "kitexpiries": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/gtmadmin kitexpiries <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                s.sendMessage(Utils.f("&7Player &a" + p.getName() + "&7 has the following kit expiries:"));
                for (Map.Entry<String, Long> entry : user.getKitExpiries().entrySet())
                    s.sendMessage(entry.getKey() + ": expiry " + entry.getValue() + " time left " + (entry.getValue() - System.currentTimeMillis()));
                s.sendMessage("KitExpiriesString: " + user.getKitExpiriesString());
                return true;
            }
            case "resetjobdelay": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/gtmadmin resetjobdelay <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                user.setLastJobMode(-1);
                s.sendMessage(Utils.f("&aYou reset &a" + p.getName() + "&7's job mode delay!"));
                return true;
            }
            case "setkillcounter": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/gtmadmin setkillcounter <player> <amnt>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                int amnt;
                try {
                    amnt = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe amnt must be a number!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                user.setKillCounter(amnt);
                player.sendMessage(Utils.f("&aYou set " + p.getName() + "'s killcounter to " + amnt + '!'));
                return true;
            }
            case "release": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/gtmadmin release <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                user.setJailTimer(-1);
                p.teleport(GTM.getWarpManager().getSpawn().getLocation());
                s.sendMessage(Utils.f("&aYou released " + p.getName() + " from jail!"));
                return true;
            }
            case "target": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/gtmadmin target <player>"));
                    return true;
                }
                Player p = Bukkit.getPlayer(args[1]);
                if (p == null) {
                    s.sendMessage(Utils.f("&cThat player is not online!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.setCompassTarget(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), new CompassTarget(p));
                s.sendMessage(Utils.f("&aYou set your compass target to &a" + p.getName() + '!'));
                return true;
            }
            case "hchest": {//gtmadmin hchest playerName houseId chestId
                if (args.length < 4) {
                    s.sendMessage(C.ERROR + "Usage: /gtmadmin [hchest/phchest] playerName houseId chestId");
                    return true;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    s.sendMessage(C.ERROR + "This player is not online.");
                    return true;
                }

                int houseId = -1, chestId = -1;
                try {
                    houseId = Integer.parseInt(args[2]);
                    chestId = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(C.ERROR + "The requested Id's couldn't be parsed. Use numerals.");
                    return true;
                }

                if (houseId < 0 || chestId < 0) {
                    s.sendMessage(C.ERROR + "The requested Id's couldn't be parsed. Use numerals.");
                    return true;
                }

                HouseUser houseUser = Houses.getUserManager().getLoadedUser(target.getUniqueId());
                if (houseUser == null) {
                    s.sendMessage(C.ERROR + "This player does not own a house.");
                    return true;
                }

                UserHouse userHouse = houseUser.getUserHouse(houseId);
                if (userHouse == null) {
                    s.sendMessage(C.ERROR + "This player does not own this house.");
                    return true;
                }

                UserHouseChest houseChest = userHouse.getChest(chestId);
                if (houseChest == null) {
                    s.sendMessage(C.ERROR + "The house chest with id " + chestId + ", could not be found.");
                    return true;
                }

                if (houseChest.getContents() == null) {
                    s.sendMessage(C.ERROR + "This chest doesn't contain items.");
                    return true;
                }

                new FakeChestInv(6, houseId, chestId, houseChest.getContents()).openInventory((Player) s);
                s.sendMessage(C.GREEN + "Opening fake content inventory.");
            }
            default:
                s.sendMessage(Utils.f("&c/gtmadmin setkillcounter <player> <amnt>"));
                s.sendMessage(Utils.f("&c/gtmadmin release <player>"));
                s.sendMessage(Utils.f("&c/gtmadmin target <player>"));
                s.sendMessage(Utils.f("&c/gtmadmin resetjobdelay <player>"));
                s.sendMessage(Utils.f("&c/gtmadmin kitexpiries <player>"));
                s.sendMessage(Utils.f("&c/gtmadmin trashcan"));
                s.sendMessage(Utils.f("&c/gtmadmin barrel"));
                s.sendMessage(Utils.f("&c/gtmadmin gravity <modifier>"));
                return true;
        }
    }

    public class FakeChestInv extends CoreMenu {

        public FakeChestInv(int rows, int houseId, int chestId, ItemStack[] contents) {
            super(rows, "Fake contents {" + houseId + "," + chestId + "}", CoreMenuFlag.RESET_CURSOR_ON_OPEN, CoreMenuFlag.CLOSE_ON_NULL_CLICK);

            for (int i = 0; i < contents.length; i++) {
                ItemStack item = contents[i];
                if (item == null) continue;

                addItem(new MenuItem(i, item, false));
            }
        }
    }
}
