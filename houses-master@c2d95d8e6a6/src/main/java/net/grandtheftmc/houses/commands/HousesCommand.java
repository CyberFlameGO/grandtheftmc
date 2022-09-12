package net.grandtheftmc.houses.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.HouseUtils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.Blocks;
import net.grandtheftmc.houses.houses.EditableBlock;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseDoor;
import net.grandtheftmc.houses.houses.PremiumHouseGuest;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;

public class HousesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        boolean isPremium = "premiumhouses".equalsIgnoreCase(cmd.getName());
        if (args.length == 0) {
            if (!s.hasPermission("houses.admin.all")) {
                s.sendMessage(Utils.f("&cYou don't have permission to execute this command!"));
                return true;
            }
            s.sendMessage(Lang.HOUSES.f("&7&lHouses Help"));
            s.sendMessage(Utils.f("&3/" + label + "&7 add"));
            s.sendMessage(Utils.f("&3/" + label + "&7 edit &a<id>"));
            s.sendMessage(Utils.f("&3/" + label + "&7 stop"));
            s.sendMessage(Utils.f("&3/" + label + "&7 setprice &a<price>"));
            s.sendMessage(Utils.f("&3/" + label + "&7 remove &a<id>"));
            s.sendMessage(Utils.f("&3/" + label + "&7 list &a[page]"));
            s.sendMessage(Utils.f("&3/" + label + "&7 setowner &a<id> <player>"));
            s.sendMessage(Utils.f("&3/" + label + "&7 removeowner &a<id>" + (isPremium ? "" : " <player>")));
            s.sendMessage(Utils.f("&3/" + label + "&7 tp &a<id>"));
            s.sendMessage(Utils.f("&3/" + label + "&7 info &a<id>"));
            if (isPremium) {
                s.sendMessage(Utils.f("&3/" + label + "&7 owner &a<id>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 addblocks/delblocks &a<we>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 reset &a<id>"));
            }
            s.sendMessage(Utils.f("&3/" + label + "&7 player &a<player>"));
            return true;
        }
        HousesManager hm = Houses.getManager();
        if (!s.hasPermission("houses.admin.all")) {
            if (!s.hasPermission("houses.admin." + args[0].toLowerCase())) {
                s.sendMessage(Utils.f("&cYou don't have permission to execute this command!"));
                return true;
            }
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Utils.f("&cYou are not a player!"));
                    return true;
                }
                Player player = (Player) s;
                UUID uuid = player.getUniqueId();
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/" + label + " add"));
                    return true;
                }
                if (isPremium) {
                    hm.createPremiumHouse(result -> {
                        if (result == null) {
                            ServerUtil.runTask(() -> s.sendMessage("Something went wrong, contact Luke. (#err02784)"));
                            return;
                        }

                        ServerUtil.runTask(() -> {
                            user.setEditingHouse(null);
                            user.setEditingPremiumHouse(result);
                            Core.getUserManager().getLoadedUser(uuid).setEditMode(true);
                            s.sendMessage(Utils.f(Lang.HOUSES + "&7A premium house with id &a" + result.getId() + "&7 has been created! You are now editing this premium house, you should set the price, add doors and add chests."));
                        });
                    });
                    return true;
                }
                hm.createHouse(result -> {
                    if (result == null) {
                        ServerUtil.runTask(() -> s.sendMessage("Something went wrong, contact Luke. (#err02783)"));
                        return;
                    }

                    ServerUtil.runTask(() -> {
                        user.setEditingHouse(result);
                        user.setEditingPremiumHouse(null);
                        Core.getUserManager().getLoadedUser(uuid).setEditMode(true);
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7A house with id &a" + result.getId() + "&7 has been created! You are now editing this house, you should set the price, add doors and add chests."));
                    });
                });
//                user.setEditingHouse(house);
//                user.setEditingPremiumHouse(null);
//                Core.getUserManager().getLoadedUser(uuid).setEditMode(true);
//                s.sendMessage(Utils.f(Lang.HOUSES + "&7A house with id &a" + house.getId()
//                        + "&7 has been created! You are now editing this house, you should set the price, add doors and add chests."));
                return true;
            }
            case "edit": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Utils.f("&cYou are not a player!"));
                    return true;
                }
                Player player = (Player) s;
                UUID uuid = player.getUniqueId();
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/" + label + " edit <id>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }
                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7No premium house with that ID exists!"));
                        return true;
                    }
                    user.setAddingChests(false);
                    user.setAddingDoor(null);
                    user.setAddingPremiumDoor(null);
                    user.setRemovingChests(false);
                    user.setRemovingDoor(false);
                    user.setEditingPremiumHouse(house);
                    user.setEditingHouse(null);
                    Core.getUserManager().getLoadedUser(uuid).setEditMode(true);
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are now editing premium house &a" + house.getId() + "&7."));
                    return true;
                }
                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7No house with that ID exists!"));
                    return true;
                }
                user.setAddingChests(false);
                user.setAddingDoor(null);
                user.setAddingPremiumDoor(null);
                user.setRemovingChests(false);
                user.setRemovingDoor(false);
                user.setEditingPremiumHouse(null);
                user.setEditingHouse(house);
                Core.getUserManager().getLoadedUser(uuid).setEditMode(true);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are now editing house &a" + house.getId() + "&7."));
                return true;

            }
            case "stop": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Utils.f("&cYou are not a player!"));
                    return true;
                }
                if (args.length != 1) {
                    s.sendMessage(Utils.f("&c/" + label + " stop <id>"));
                    return true;
                }
                Player player = (Player) s;
                UUID uuid = player.getUniqueId();
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                if (isPremium) {
                    PremiumHouse premiumHouse = user.getEditingPremiumHouse();
                    if (premiumHouse == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any premium house!"));
                        return true;
                    }
                    user.setAddingChests(false);
                    user.setAddingDoor(null);
                    user.setAddingPremiumDoor(null);
                    user.setRemovingChests(false);
                    user.setRemovingDoor(false);
                    user.setEditingHouse(null);
                    user.setEditingPremiumHouse(null);
                    s.sendMessage(Utils.f(
                            Lang.HOUSES + "&7You are no longer editing premium house &a" + premiumHouse.getId() + "&7."));
                    return true;
                }
                House house = user.getEditingHouse();
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house!"));
                    return true;
                }
                user.setAddingChests(false);
                user.setAddingDoor(null);
                user.setAddingPremiumDoor(null);
                user.setRemovingChests(false);
                user.setRemovingDoor(false);
                user.setEditingHouse(null);
                user.setEditingPremiumHouse(null);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You are no longer editing house &a" + house.getId() + "&7."));
                return true;
            }
            case "setprice": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Utils.f("&cYou are not a player!"));
                    return true;
                }
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/" + label + " setprice <amnt>"));
                    return true;
                }
                Player player = (Player) s;
                UUID uuid = player.getUniqueId();
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                int amnt;
                try {
                    amnt = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The amount must be a number!"));
                    return true;
                }
                if (amnt < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The amount must be bigger than 0!"));
                    return true;
                }
                if (isPremium) {
                    PremiumHouse house = user.getEditingPremiumHouse();
                    if (house == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any premium house!"));
                        return true;
                    }
                    house.setPermits(amnt);
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You set the price of premium house &a" + house.getId()
                            + "&7 to &3&l" + house.getPermits() + " Permits&7."));
                    return true;
                }
                House house = user.getEditingHouse();
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You are not editing any house!"));
                    return true;
                }
                house.setPrice(amnt);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You set the price of house &a" + house.getId() + "&7 to &a$&l" + house.getPrice() + "&7."));
                return true;
            }
            case "remove": {
                if (args.length > 3) {
                    s.sendMessage(Utils.f("&c/" + label + " remove <id>"));
                    return true;
                }
                if (args.length == 2) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7Are you sure you want to do this? Type &3/" + label
                            + " remove &a<id>&3 confirm&7 to proceed..."));
                    return true;
                }
                if (!Objects.equals("confirm", args[2])) {
                    s.sendMessage(Utils.f("&c/" + label + " remove <id>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }
                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7That premium house does not exist!"));
                        return true;
                    }
                    hm.removePremiumHouse(house);
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7You removed the premium house &a" + house.getId() + "&7."));
                    return true;
                }
                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7That house does not exist!"));
                    return true;
                }
                hm.removeHouse(house);
                if (s instanceof Player) {
                    HouseUser user = Houses.getUserManager().getLoadedUser(((Player) s).getUniqueId());
                    user.setAddingChests(false);
                    user.setAddingDoor(null);
                    user.setAddingPremiumDoor(null);
                    user.setRemovingChests(false);
                    user.setRemovingDoor(false);
                    user.setEditingHouse(null);
                    user.setEditingPremiumHouse(null);
                }
                Utils.insertLogLater((s instanceof Player) ? ((Player)s).getUniqueId() : UUID.randomUUID(), s.getName(), "remove" + (isPremium ? "Premium" : "") + "House", "REMOVE_" + (isPremium ? "PREMIUM" : "") + "_HOUSE", (isPremium ? "Premium" : "") + "House ID: " + id, -1, -1);
                s.sendMessage(Utils.f(Lang.HOUSES + "&7You removed the house &a" + house.getId() + "&7."));
                return true;
            }
            case "list": {
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }

                int batch = 8;
                int start = batch * (page - 1);

                if (isPremium) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Premium Houses: &a" + hm.getPremiumHouses().size()));

                    for (int i = start; i < start + batch; i++) {
                        if (hm.getPremiumHouses().size() > i) {
                            PremiumHouse premiumHouse = hm.getPremiumHouse(i + 1);
                            s.sendMessage(Utils.f(" &3&lID: &a" + premiumHouse.getId() + " &3&lPermits: &a" + premiumHouse.getPermits() + " &3&lChests: &a" + premiumHouse.getAmountOfChests() + " &3&lDoors: &a" + premiumHouse.getDoors().size()));
                        }
                    }

//                    for (PremiumHouse premiumHouse : hm.getPremiumHouses()) {
//                        if (premiumHouse != null) {
//                            n++;
//                            s.sendMessage(Utils.f(" &3&lID: &a" + premiumHouse.getId() + " &3&lPermits: &a" + premiumHouse.getPermits() + " &3&lChests: &a" + premiumHouse.getAmountOfChests() + " &3&lDoors: &a" + premiumHouse.getDoors().size()));
//                        }
//                        if (n == 8) break;
//                    }
                    return true;
                }
                else {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal Houses: &a" + hm.getHouses().size()));

                    for (int i = start; i < start + batch; i++) {
                        if (hm.getHouses().size() > i) {
                            House house = hm.getHouse(i + 1);
                            s.sendMessage(Utils.f(" &3&lID: &a" + house.getId() + " &3&lPrice: &a" + house.getPrice() + " &3&lChests: &a" + house.getAmountOfChests() + " &3&lDoors: &a" + house.getDoors().size()));
                        }
                    }

//                    int n = 0;
//                    for (int i = 0; i < hm.getHouses().size(); i++) {
//                        House house = hm.getHouse(start + i);
//                        if (house != null) {
//                            n++;
//                            s.sendMessage(Utils.f(" &3&lID: &a" + house.getId() + " &3&lPrice: &a" + house.getPrice() + " &3&lChests: &a" + house.getAmountOfChests() + " &3&lDoors: &a" + house.getDoors().size()));
//                        }
//                        if (n == 8) break;
//                    }
                }
                return true;
            }
            case "unowned": {
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                }
                int start = (page << 3) - 7;
                if (isPremium) {
                    List<PremiumHouse> unowned = new ArrayList<>();
                    hm.getPremiumHouses().forEach(premiumHouse -> {
                        if (premiumHouse.getOwner() == null) unowned.add(premiumHouse);
                    });
                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &3&lTotal &a&l(Unowned) &3&lPremium Houses: &a"
                            + unowned.size()));
                    int n = 0;
                    for (int i = 0; i < unowned.size(); i++) {
                        if (unowned.size() <= start + i) break;
                        PremiumHouse house = unowned.get(start + i);
                        if (house != null && house.getOwner() == null) {
                            n++;
                            s.sendMessage(Utils.f(" &3&lID: &a" + house.getId() + " &3&lPermits: &a" + house.getPermits()
                                    + " &3&lChests: &a" + house.getAmountOfChests() + " &3&lDoors: &a"
                                    + house.getDoors().size()));
                        }
                        if (n == 8) break;
                    }
                } else {
                    s.sendMessage(Lang.HOUSES.f("&7Premium Houses only! (/ph)"));
                }
                return true;
            }
            case "setowner": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/" + label + " setowner <id> <player>"));
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[2]);
                if (isPremium) {
                	
                	// if player is not online
                    if (player == null) {
                        if (!(s instanceof Player)) {
                            s.sendMessage(Lang.NOTPLAYER.s());
                            return true;
                        }
                        s.sendMessage(Lang.HOUSES.f("&7Please hold on while data is pulled from the database for the offline player."));
                        UUID senderUUID = ((Player) s).getUniqueId();
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                UUID uuid = null;
                                String name = null;
                                
                                uuid = UserDAO.getUuidByName(args[2]);
                                if (uuid != null){
                                	// this call is perhaps for case sensitivity
                                	name = UserDAO.getNameByUuid(uuid);
                                }

//                                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                                	try (PreparedStatement statement = connection.prepareStatement("select uuid,lastname from users where lastname='" + args[2] + "';")) {
//                                        try (ResultSet result = statement.executeQuery()) {
//                                            uuid = UUID.fromString(result.getString("uuid"));
//                                            name = result.getString("lastname");
//                                        }
//                                    }
//                                } catch (SQLException e) {
//                                    e.printStackTrace();
//                                }

                                UUID finalUuid = uuid;
                                String finalName = name;
                                new BukkitRunnable() {
                                    @Override
                                    public void run() {
                                        Player sender = Bukkit.getPlayer(senderUUID);
                                        if (sender == null) return;
                                        PremiumHouse house = hm.getPremiumHouse(id);
                                        if (house == null) {
                                            s.sendMessage(Utils.f(Lang.HOUSES + "&7That premium house does not exist!"));
                                            return;
                                        }

                                        Player newOwner = Bukkit.getPlayer(finalUuid);
                                        if (newOwner != null) {

                                            house.forceSetOwner(player);
                                            Utils.insertLogLater(sender.getUniqueId(), sender.getName(), "setOwner" + (isPremium ? "Premium" : "") + "HouseTo" + finalName, "SET_OWNER_" + (isPremium ? "PREMIUM" : "") + "_HOUSE", (isPremium ? "Premium" : "") + "House ID: " + id, -1, -1);
                                            s.sendMessage(Lang.HOUSES
                                                    .f("&7Player &a" + player.getName() + "&7 now owns premium house &a" + house.getId() + "&7!"));
                                            return;
                                        }

                                        Player old = Bukkit.getPlayer(house.getOwner());
                                        if (old != null) {
                                            HouseUser user = Houses.getUserManager().getLoadedUser(old.getUniqueId());
                                            old.sendMessage(Lang.HOUSES.f("&7You no longer own premium house &a" + house.getId() + "&7!"));
                                            if (user.isInsidePremiumHouse(house.getId()))
                                                user.teleportInOrOutPremiumHouse(old, house);
                                        }

                                        house.setOwner(finalUuid, finalName, true);
                                    }
                                }.runTask(Houses.getInstance());
                            }
                        }.runTaskAsynchronously(Houses.getInstance());
                        return true;
                    }
                    
                    
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7That premium house does not exist!"));
                        return true;
                    }
                    
              
                    
                    house.forceSetOwner(player);
                    s.sendMessage(Lang.HOUSES
                            .f("&7Player &a" + player.getName() + "&7 now owns premium house &a" + house.getId() + "&7!"));
                    return true;
                }

                if (player == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7That player is not online!"));
                    return true;
                }

                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7That house does not exist!"));
                    return true;
                }

                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                house.addOwner(player, user);
                s.sendMessage(Lang.HOUSES.f("&7Player &a" + player.getName() + "&7 now owns house &a" + house.getId() + "&7!"));
                return true;
            }
            case "removeowner": {
                if (isPremium) {
                    if (args.length != 2) {
                        s.sendMessage(Utils.f("&c/" + label + " removeowner <id>"));
                        return true;
                    }
                } else if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/" + label + " removeowner <id> <player>"));
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }

                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }

                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Utils.f(Lang.HOUSES + "&7That premium house does not exist!"));
                        return true;
                    }
                    s.sendMessage(Lang.HOUSES.f("&7Player &a" + house.getOwner() + "&7 no longer owns premium house &a" + house.getId() + "&7!"));
                    house.removeOwner(true);
                    return true;

                }

                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7That house does not exist!"));
                    return true;
                }

                Player player = Bukkit.getPlayer(args[2]);
                if (player == null) {
//                    Core.sql.updateAsyncLater("delete from " + Core.name() + "_houses where houseId=" + id + " and name='" + args[2] + "';");
                    ServerUtil.runTaskAsync(() -> {
//                        BaseDatabase.runCustomQuery("delete from " + Core.name() + "_houses where houseId=" + id + " and name='" + args[2] + "';");

                    });
                    s.sendMessage(Lang.HOUSES.f("&7That player is not online, so the house was removed from them in the database."));
                    return true;
                }

                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                if (!user.ownsHouse(id)) {
                    s.sendMessage(Lang.HOUSES.f("&7That player does not own house &a" + id + "&7!"));
                    return true;
                }
                house.removeOwner(player, user);
                s.sendMessage(Lang.HOUSES
                        .f("&7Player &a" + player.getName() + "&7 no longer owns house &a" + house.getId() + "&7!"));
                Utils.insertLogLater(s instanceof Player ? ((Player)s).getUniqueId() : UUID.randomUUID(), s.getName(), "removeOwner" + (isPremium ? "Premium" : "") + "House" , "REMOVE_OWNER_" + (isPremium ? "PREMIUM" : "") + "_HOUSE", (isPremium ? "Premium" : "") + "House ID: " + id, -1, -1);
                return true;
            }
            case "owners":
            case "owner": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/houses owner" + (isPremium ? "" : "s") + " <id>"));
                    return true;
                }

                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }

                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }

                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Lang.HOUSES.f("&7That premium house does not exist!"));
                        return true;
                    }
                    s.sendMessage(Lang.HOUSES.f("&7The owner of premium house &a" + house.getId() + "&7 is &a" + house.getOwner() + "&7!"));
                    return true;
                }

                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                    return true;
                }

                s.sendMessage(Lang.HOUSES.f("&7Pulling house owners from the database! Please wait a couple of (milli)seconds!"));
                int houseId = house.getId();
                UUID uuid = ((Player) s).getUniqueId();

//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        List<String> names = new ArrayList<>();
//                        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_houses where houseId=" + houseId + ';')) {
//                                try (ResultSet result = statement.executeQuery()) {
//                                    while (result.next()) {
//                                        names.add(result.getString("name"));
//                                    }
//                                }
//                            }
//                        } catch (SQLException e) {
//                            e.printStackTrace();
//                        }
//
//                        List<String> copy = new ArrayList<>(names);
//                        new BukkitRunnable() {
//                            @Override
//                            public void run() {
//                                Player player = Bukkit.getPlayer(uuid);
//                                if (player == null)
//                                    return;
//                                player.sendMessage(Lang.HOUSES.f("&7Owners of House &a" + houseId + "&7:"));
//                                String msg = "";
//                                for (String s : copy)
//                                    msg = msg + "&a" + s + "&7, ";
//                                if (msg.endsWith("&7, "))
//                                    msg = msg.substring(0, msg.length() - 4);
//                                player.sendMessage(Utils.f(msg));
//                            }
//                        }.runTask(Houses.getInstance());
//                    }
//                }.runTaskAsynchronously(Houses.getInstance());

                return true;
            }
            case "player":
                if (args.length != 2 && args.length != 3) {
                    s.sendMessage(Utils.f("&c/" + label + " player <player> [page]"));
                    return true;
                }
                int page = 1;
                if (args.length == 3)
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException ignored) {
                    }
                int start = (page << 3) - 7;
                int end = page << 3;
                Player player = Bukkit.getPlayer(args[1]);
                if (isPremium) {
                    List<PremiumHouse> allHouses = Houses.getHousesManager().getPremiumHouses().stream().filter(house -> house.getOwnerName() != null && house.getOwnerName().equalsIgnoreCase(args[1])).collect(Collectors.toList());
                    List<PremiumHouse> listHouses = start < 0 || end > allHouses.size() ? allHouses : allHouses.subList(start, end);
                    if (allHouses.isEmpty() || listHouses == null) {
                        s.sendMessage(Lang.HOUSES.f("&7That player has no houses!"));
                        return true;
                    }

                    s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &a&l" + (player == null ? args[1] : player.getName()) + "&3&l's Premium Houses: &a" + allHouses.size()));
                    for (PremiumHouse house : listHouses) {
                        s.sendMessage(Utils.f(" &3&lID: &a" + house.getId() + " &3&lPermits: &a" + house.getPermits() + " &3&lChests: &a" + house.getAmountOfChests() + " &3&lDoors: &a" + house.getDoors().size()));
                    }
                    return true;
                }

                if (player == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That player is not online!"));
                    return true;
                }

                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                List<UserHouse> houses = user.getHouses(start, end);
                if (houses.isEmpty() || houses == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That player has no houses!"));
                    return true;
                }

                s.sendMessage(Utils.f(Lang.HOUSES + "&3&lPage &a" + page + " &a&l" + player.getName() + "&3&l's Houses: &a" + user.getHouses().size()));
                for (UserHouse house : houses) {
                    House h = hm.getHouse(house.getId());
                    s.sendMessage(Utils.f(" &3&lID: &a" + house.getId() + " &3&lPrice: &a" + h.getPrice() + " &3&lChests: &a" + h.getAmountOfChests() + " &3&lDoors: &a" + h.getDoors().size()));
                }
                return true;
            case "tp": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/" + label + " tp <id>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }
                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Lang.HOUSES.f("&7That premium house does not exist!"));
                        return true;
                    }
                    PremiumHouseDoor door = house.getDoor();
                    if (door == null) {
                        s.sendMessage(Lang.HOUSES.f("&7That premium house doesn't have any doors!"));
                        return true;
                    }
                    ((Player) s).teleport(door.getOutsideLocation());
                    s.sendMessage(Lang.HOUSES.f("&7You were teleported to the outside location of door &a" + door.getId()
                            + "&7 of premium house &a" + house.getId() + "&7!"));
                    return true;
                }
                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                    return true;
                }
                HouseDoor door = house.getDoor();
                if (door == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That house doesn't have any doors!"));
                    return true;
                }
                ((Player) s).teleport(door.getOutsideLocation());
                s.sendMessage(Lang.HOUSES.f("&7You were teleported to the outside location of door &a" + door.getId()
                        + "&7 of house &a" + house.getId() + "&7!"));
                return true;
            }
            case "info":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/" + label + " info <id>"));
                    return true;
                }
                int id;
                try {
                    id = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be a number!"));
                    return true;
                }
                if (id < 1) {
                    s.sendMessage(Utils.f(Lang.HOUSES + "&7The id must be bigger than 0!"));
                    return true;
                }
                if (isPremium) {
                    PremiumHouse house = hm.getPremiumHouse(id);
                    if (house == null) {
                        s.sendMessage(Lang.HOUSES.f("&7That premium house does not exist!"));
                        return true;
                    }
                    s.sendMessage(Lang.HOUSES.f("&3&lInfo of Premium House &a&l" + house.getId()));
                    s.sendMessage(Utils.f("&3&lChests: &a&l" + house.getAmountOfChests()));
                    s.sendMessage(Utils.f("&3&lDoors: &a&l" + house.getDoors().size()));
                    s.sendMessage(Utils.f("&3&lSigns: &a&l" + house.getSigns().size()));
                    s.sendMessage(Utils.f("&3&lPermits: &a&l" + house.getPermits()));
                    if (house.isOwned()) {
                        s.sendMessage(Utils.f("&3&lOwner: &a&l" + house.getOwnerName()));
                        String guests = "";
                        for (PremiumHouseGuest guest : house.getGuests())
                            guests = guests + "&a" + guest.getName() + "&7, ";
                        if (guests.endsWith("&7, "))
                            guests = guests.substring(0, guests.length() - 2);
                        guests += ".";
                        s.sendMessage(Utils.f("&3&lGuests: &a&l" + guests));
                    }
                    return true;

                }
                House house = hm.getHouse(id);
                if (house == null) {
                    s.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                    return true;
                }
                s.sendMessage(Lang.HOUSES.f("&3&lInfo of House &a&l" + house.getId()));
                s.sendMessage(Utils.f("&3&lChests: &a&l" + house.getAmountOfChests()));
                s.sendMessage(Utils.f("&3&lDoors: &a&l" + house.getDoors().size()));
                s.sendMessage(Utils.f("&3&lSigns: &a&l" + house.getSigns().size()));
                s.sendMessage(Utils.f("&3&lPrice: &a&l" + house.getPrice()));
                return true;
            case "addblocks":
                player = (Player) s;
                user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.getEditingPremiumHouse() != null) {
                    if (args.length == 2 && args[1].equalsIgnoreCase("we")) {
                        if (Houses.getWorldEdit().isPresent()) {
                            WorldEditPlugin we = Houses.getWorldEdit().get();
                            if (we.getSelection(player) != null) {
                                Selection selection = we.getSelection(player);
                                Collection<Block> blocks = HouseUtils.getBlocks(selection.getMinimumPoint(), selection.getMaximumPoint());
                                int before = user.getEditingPremiumHouse().getEditableBlocks().size();
                                blocks.forEach(block -> {
                                    if (Blocks.getMaterials().contains(block.getType())) {
                                        user.getEditingPremiumHouse().addEditableBlock(block.getLocation(), block.getType(), block.getData(), res -> {});
                                    }
                                });
                                int after = user.getEditingPremiumHouse().getEditableBlocks().size();
                                player.sendMessage(Lang.HOUSES.f("&a" + String.valueOf(after - before) + " &7blocks have been added."));
                            } else {
                                player.sendMessage(Lang.HOUSES.f("&7You must make a selection with WorldEdit first!"));
                            }
                        } else {
                            player.sendMessage(Lang.HOUSES.f("&cError! Contact a manager."));
                        }
                        return true;
                    }
                    if (user.isAddingBlocks()) {
                        user.setAddingBlocks(false);
                        player.sendMessage(Lang.HOUSES.f("&7You're no longer adding blocks."));
                    } else {
                        if (user.isRemovingBlocks()) user.setRemovingBlocks(false);
                        user.setAddingBlocks(true);
                        player.sendMessage(Lang.HOUSES.f("&7You're now adding blocks."));
                    }
                }
                return true;
            case "delblocks":
                player = (Player) s;
                user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.getEditingPremiumHouse() != null) {
                    if (args.length == 2 && args[1].equalsIgnoreCase("we")) {
                        if (Houses.getWorldEdit().isPresent()) {
                            WorldEditPlugin we = Houses.getWorldEdit().get();
                            if (we.getSelection(player) != null) {
                                Selection selection = we.getSelection(player);
                                Collection<EditableBlock> blocks = new ArrayList<>();
                                int before = user.getEditingPremiumHouse().getEditableBlocks().size();
                                user.getEditingPremiumHouse().getEditableBlocks().forEach(editableBlock -> {
                                    if (selection.contains(editableBlock.getLocation())) {
                                        blocks.add(editableBlock);
                                    }
                                });
                                user.getEditingPremiumHouse().getEditableBlocks().removeAll(blocks);
                                int after = user.getEditingPremiumHouse().getEditableBlocks().size();
                                player.sendMessage(Lang.HOUSES.f("&a" + String.valueOf(before - after) + " &7blocks have been removed."));
                            } else {
                                player.sendMessage(Lang.HOUSES.f("&7You must make a selection with WorldEdit first!"));
                            }
                        } else {
                            player.sendMessage(Lang.HOUSES.f("&cError! Contact a manager."));
                        }
                        return true;
                    }
                    if (user.isRemovingBlocks()) {
                        user.setRemovingBlocks(false);
                        player.sendMessage(Lang.HOUSES.f("&7You're no longer removing blocks."));
                    } else {
                        if (user.isAddingBlocks()) user.setAddingBlocks(false);
                        user.setRemovingBlocks(true);
                        player.sendMessage(Lang.HOUSES.f("&7You're now removing blocks."));
                    }
                }
                return true;
            case "wipedata":
                if (s instanceof Player) {
                    s.sendMessage("console only");
                    return true;
                }
                Houses.getHousesManager().getPremiumHouses().forEach(premiumHouse -> premiumHouse.removeOwner(true));
                Houses.getHousesManager().save();
                s.sendMessage(Lang.HOUSES.f("&cAll house data wiped"));
                return true;
            case "reset":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/" + label + " reset <id>"));
                    return true;
                }
                player = (Player) s;
                String houseId = args[1];
                PremiumHouse premiumHouse = Houses.getHousesManager().getPremiumHouse(Integer.valueOf(houseId));
                if (premiumHouse == null) {
                    s.sendMessage(Lang.HOUSES.f("&7Could not find house with ID " + houseId));
                    return true;
                }
                if (!premiumHouse.isOwned()) {
                    s.sendMessage(Lang.HOUSES.f("&7That house isn't owned!"));
                    return true;
                }
                Utils.insertLogLater(player.getUniqueId(), player.getName(), "reset" + (isPremium ? "Premium" : "") + "House" , "RESET_" + (isPremium ? "PREMIUM" : "") + "_HOUSE", (isPremium ? "Premium" : "") + "House ID: " + houseId, -1, -1);
                Houses.getHousesManager().forceSell(premiumHouse, player);
                return true;
            default:
                s.sendMessage(Lang.HOUSES.f("&7&lHouses Help"));
                s.sendMessage(Utils.f("&3/" + label + "&7 add"));
                s.sendMessage(Utils.f("&3/" + label + "&7 edit &a<id>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 stop"));
                s.sendMessage(Utils.f("&3/" + label + "&7 setprice &a<price>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 remove &a<id>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 list &a[page]"));
                s.sendMessage(Utils.f("&3/" + label + "&7 setowner &a<id> <player>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 removeowner &a<id>" + (isPremium ? "" : " <player>")));
                s.sendMessage(Utils.f("&3/" + label + "&7 tp &a<id>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 info &a<id>"));
                s.sendMessage(Utils.f("&3/" + label + "&7 player &a<player>"));
                if (isPremium) {
                    s.sendMessage(Utils.f("&3/" + label + "&7 owner &a<id>"));
                    s.sendMessage(Utils.f("&3/" + label + "&7 addblocks/delblocks &a<we>"));
                    s.sendMessage(Utils.f("&3/" + label + "&7 reset &a<id>"));
                }
                return true;
        }
    }
}
