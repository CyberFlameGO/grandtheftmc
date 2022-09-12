package net.grandtheftmc.gtm.items;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class KitCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (s instanceof Player && !s.hasPermission("command.kit")) {
            MenuManager.openMenu((Player) s, "kits");
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/kit add <name> <cost> <delay> [permission] "));
            s.sendMessage(Utils.f("&c/kit setcost <name> <cost>"));
            s.sendMessage(Utils.f("&c/kit setdelay <name> <delay>"));
            s.sendMessage(Utils.f("&c/kit give <player> <kit>"));
            s.sendMessage(Utils.f("&c/kit give [r=5] <kit> <x,y,z>"));
            s.sendMessage(Utils.f("&c/kit setpermission <name> <permission/none>"));
            s.sendMessage(Utils.f("&c/kit set <name>"));
            s.sendMessage(Utils.f("&c/kit list [page]"));
            s.sendMessage(Utils.f("&c/kit load"));
            s.sendMessage(Utils.f("&c/kit save"));
            return true;
        }
        ItemManager im = GTM.getItemManager();
        switch (args[0]) {
            case "setcost": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/kit setcost <name> <cost>"));
                    return true;
                }
                String name = args[1];
                Kit kit = im.getKit(name);
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                    return true;
                }
                double cost = 0;
                try {
                    cost = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.KITS.f("&7The cost must be a number!"));
                }
                kit.setCost(cost);
                s.sendMessage(
                        Lang.KITS.f("&7The cost of kit &a" + kit.getName() + "&7 has been set to &a$&l" + cost + "&7!"));
                return true;
            }
            case "setdelay": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/kit setdelay <name> <delay>"));
                    return true;
                }
                String name = args[1];
                Kit kit = im.getKit(name);
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                    return true;
                }
                int delay = 0;
                try {
                    delay = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.KITS.f("&7The cost must be a number!"));
                }
                kit.setDelay(delay);
                s.sendMessage(
                        Lang.KITS.f("&7The delay of kit &a" + kit.getName() + "&7 has been set to &a" + delay + "&7!"));
                return true;
            }
            case "setpermission": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/kit setpermission <name> <permission/none>"));
                    return true;
                }
                String name = args[1];
                Kit kit = im.getKit(name);
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                    return true;
                }
                kit.setPermission("none".equalsIgnoreCase(args[2]) ? null : args[2]);
                s.sendMessage(
                        Lang.KITS.f("&7The delay of kit &a" + kit.getName() + "&7 has been set to &a" + args[2] + "&7!"));
                return true;
            }
            case "set": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }
                Player player = (Player)s;
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/kit set <name>"));
                    return true;
                }
                String name = args[1];
                Kit kit = im.getKit(name);
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                    return true;
                }
                PlayerInventory inv = player.getInventory();
                List<KitItem> contents = new ArrayList<>();
                for (int i = 0; i <= 35; i++) {
                    ItemStack item = inv.getItem(i);
                    if (item == null || i == 16 || i == 17)
                        continue;
                    GameItem gameItem = im.getItem(item);
                    if (gameItem == null) {
                        s.sendMessage(Lang.KITS.f("&7The item in slot &a" + i
                                + "&7 of your inventory is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    contents.add(new KitItem(gameItem, item.getAmount()));
                }
                kit.setContents(contents);
                if (inv.getHelmet() != null) {
                    GameItem item = im.getItem(inv.getHelmet());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your helmet slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    kit.setHelmet(new KitItem(item, inv.getHelmet().getAmount()));
                }
                if (inv.getChestplate() != null) {
                    GameItem item = im.getItem(inv.getChestplate());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your chestplate slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    kit.setChestPlate(new KitItem(item, inv.getChestplate().getAmount()));
                }
                if (inv.getLeggings() != null) {
                    GameItem item = im.getItem(inv.getLeggings());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your leggings slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    kit.setLeggings(new KitItem(item, inv.getLeggings().getAmount()));
                }
                if (inv.getBoots() != null) {
                    GameItem item = im.getItem(inv.getBoots());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your boots slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    kit.setBoots(new KitItem(item, inv.getBoots().getAmount()));
                }
                if (inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() != Material.AIR) {
                    GameItem item = im.getItem(inv.getItemInOffHand());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your offhand item slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    kit.setOffHand(new KitItem(item, inv.getItemInOffHand().getAmount()));
                }
                s.sendMessage(
                        Lang.KITS.f("&7You set the contents, armor and offhand item for kit &a" + kit.getName() + "&7!"));
                return true;
            }
            case "add": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }
                Player player = (Player)s;
                if (args.length < 4) {
                    s.sendMessage(Utils.f("&c/kit add <name> <cost> <delay> [permission]"));
                    return true;
                }
                String name = args[1];
                Kit kit = im.getKit(name);
                if (kit != null) {
                    s.sendMessage(Lang.KITS.f("&7A kit with that names already exists!"));
                    return true;
                }
                double cost = 0;
                int delay = 0;
                try {
                    cost = Double.parseDouble(args[2]);
                    delay = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.KITS.f("&7The cost/delay must be a number!"));
                }
                String permission = args.length > 4 ? args[4] : null;
                PlayerInventory inv = player.getInventory();
                List<KitItem> contents = new ArrayList<>();
                for (int i = 0; i <= 35; i++) {
                    ItemStack item = inv.getItem(i);
                    if (item == null || i == 16 || i == 17)
                        continue;
                    GameItem gameItem = im.getItem(item);
                    if (gameItem == null) {
                        s.sendMessage(Lang.KITS.f("&7The item in slot &a" + i
                                + "&7 of your inventory is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    contents.add(new KitItem(gameItem, item.getAmount()));
                }
                KitItem helmet = null;
                KitItem chestPlate = null;
                KitItem leggings = null;
                KitItem boots = null;
                KitItem offHand = null;
                if (inv.getHelmet() != null) {
                    GameItem item = im.getItem(inv.getHelmet());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your helmet slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    helmet = new KitItem(item, inv.getHelmet().getAmount());
                }
                if (inv.getChestplate() != null) {
                    GameItem item = im.getItem(inv.getChestplate());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your chestplate slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    chestPlate = new KitItem(item, inv.getChestplate().getAmount());
                }
                if (inv.getLeggings() != null) {
                    GameItem item = im.getItem(inv.getLeggings());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your leggings slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    leggings = new KitItem(item, inv.getLeggings().getAmount());
                }
                if (inv.getBoots() != null) {
                    GameItem item = im.getItem(inv.getBoots());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your boots slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    boots = new KitItem(item, inv.getBoots().getAmount());
                }
                if (inv.getItemInOffHand() != null && inv.getItemInOffHand().getType() != Material.AIR) {
                    GameItem item = im.getItem(inv.getItemInOffHand());
                    if (item == null) {
                        s.sendMessage(Lang.KITS.f(
                                "&7The item in your offhand item slot is not registered as a GameItem! Use &a/additem&7 or &a/addweapon&7 to add it!"));
                        return true;
                    }
                    offHand = new KitItem(item, inv.getItemInOffHand().getAmount());
                }
                s.sendMessage(Lang.KITS.f("&7Your kit with name &a" + name + "&7 has been added!"));
                im.addKit(new Kit(name, cost, delay, contents, helmet, chestPlate, leggings, boots, offHand, permission, null));
                return true;
            }
            case "list":
                List<Kit> kits = GTM.getItemManager().getKits();
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.KITS.f("&cThe page must be a number!"));
                        return true;
                    }
                }
                if (page < 1) {
                    s.sendMessage(Lang.KITS.f("&7The page must be a positive number!"));
                    return true;
                }
                int pages = kits.size() / 6 + 1;
                s.sendMessage(Utils.f(
                        " &7&m---------------&7[&a&l Kits &7Page &a" + page + "&7/&a" + pages + " &7&m]---------------"));
                Iterator<Kit> it = kits.iterator();
                for (int i = 0; i < page * 6; i++) {
                    if (!it.hasNext())
                        return true;
                    Kit kit = it.next();
                    if (i < page * 6 - 6)
                        continue;
                    s.sendMessage(Utils
                            .f(kit.getDisplayName() + "&7 | Cost: &a$&l" + kit.getCost() + "&7 Delay: &a&l" + kit.getDelay()
                                    + (kit.getPermission() == null ? "" : "&7 Permission: &a" + kit.getPermission())));
                }
                return true;
            case "load":
                GTM.getSettings().setKitsConfig(Utils.loadConfig("kits"));
                GTM.getItemManager().loadKits();
                s.sendMessage(Lang.KITS.f("&7Loaded Kits!"));
                return true;
            case "save":
                GTM.getItemManager().saveKits();
                s.sendMessage(Lang.KITS.f("&7Saved Kits!"));
                return true;
            case "give":
                if (args.length != 3) {
                    if(args[1].contains("[r=")) {
                        Kit kit = im.getKit(args[2]);
                        if (kit == null) {
                            s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                            return true;
                        }
                        Location point = new Location(Bukkit.getWorld("minesantos"), 0, 0, 0);
                        int radius;
                        try {
                            String[] test = args[1].split("=");
                            radius = Integer.valueOf(test[1].replace("]", ""));
                            if(args.length == 4) {
                                String[] cords = args[3].split(",");
                                point.setX(Integer.valueOf(cords[0]));
                                point.setY(Integer.valueOf(cords[1]));
                                point.setZ(Integer.valueOf(cords[2]));
                            } else {
                                s.sendMessage(Utils.f("Error in your syntax"));
                                s.sendMessage(Utils.f("Example: /kit give [r=5] hobo x,y,z"));
                                return true;
                            }
                        } catch (Exception exception) {
                            s.sendMessage(Utils.f("Error in your syntax"));
                            return true;
                        }
                        for(LivingEntity entity : point.getWorld().getLivingEntities()) {
                            if(entity.getLocation().distance(point) > radius) continue;
                            if(entity.getType() == EntityType.PLAYER) {
                                Player target = (Player)entity;
                                im.giveKitItems(target, GTM.getUserManager().getLoadedUser(target.getUniqueId()), kit);
                            }
                        }
                        return true;
                    } else {
                        s.sendMessage(Utils.f("&c/kit give <player> <kit>"));
                        return true;
                    }
                }
                String name = args[2];
                Kit kit = im.getKit(name);
                if (kit == null) {
                    s.sendMessage(Lang.KITS.f("&7That kit does not exist!"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if(target == null) {
                    s.sendMessage(Lang.KITS.f("&7Player not found"));
                    return false;
                }
                im.giveKitItems(target, GTM.getUserManager().getLoadedUser(target.getUniqueId()), kit);
                s.sendMessage(Lang.KITS.f("&7Kit " + kit.getDisplayName() + " &7has been given to " + target.getDisplayName()));
                return true;
            default:
                s.sendMessage(Utils.f("&c/kit add <name> <cost> <delay> [permission]"));
                s.sendMessage(Utils.f("&c/kit setcost <name> <cost>"));
                s.sendMessage(Utils.f("&c/kit setdelay <name> <delay>"));
                s.sendMessage(Utils.f("&c/kit give <player> <kit>"));
                s.sendMessage(Utils.f("&c/kit give [r=5] <kit> <x,y,z>"));
                s.sendMessage(Utils.f("&c/kit setpermission <name> <permission/none>"));
                s.sendMessage(Utils.f("&c/kit set <name>"));
                s.sendMessage(Utils.f("&c/kit list [page]"));
                s.sendMessage(Utils.f("&c/kit load"));
                s.sendMessage(Utils.f("&c/kit save"));
                return true;
        }
    }

}
