package net.grandtheftmc.vice.lootcrates;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class LootCrateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        if (!player.hasPermission("lootcrate.admin")) {
            player.sendMessage(Lang.NOPERM.s());
            return true;
        }
        UUID uuid = player.getUniqueId();
        ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/lootcrates add"));
            s.sendMessage(Utils.f("&c/lootcrates remove"));
            s.sendMessage(Utils.f("&c/lootcrates cooldown <minutes> "));
            s.sendMessage(Utils.f("&c/lootcrates check"));
            s.sendMessage(Utils.f("&c/lootcrates restock"));
            s.sendMessage(Utils.f("&c/lootcrates list [page]"));

            s.sendMessage(Utils.f("&c/lootcrates items <itemName> <chance> <min> <max>"));
            s.sendMessage(Utils.f("&c/lootcrates removeitem <itemName>"));
            s.sendMessage(Utils.f("&c/lootcrates load"));
            s.sendMessage(Utils.f("&c/lootcrates save"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "add":
                s.sendMessage(Lang.LOOTCRATES.f("&7Right click on the chest you want to turn in to a Loot Crate. Make sure the chest has display name &e&lLoot Crate&7!"));
                user.setBooleanToStorage(BooleanStorageType.ADDING_LOOTCRATE, true);
                return true;
            case "remove":
                s.sendMessage(Lang.LOOTCRATES.f("&7Right click on the chest you would like to remove as a Loot Crate."));
                user.setBooleanToStorage(BooleanStorageType.REMOVING_LOOTCRATE, true);
                return true;
            case "cooldown":
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/lootcrate cooldown <minutes> "));
                    return true;
                }
                try {
                    Vice.getCrateManager().setCooldown(Integer.parseInt(args[1]));
                } catch (NumberFormatException e) {
                    s.sendMessage(Utils.f("&cThe cooldown must be a number measured in minutes!"));
                    return true;
                }
                s.sendMessage(Lang.LOOTCRATES.f("&7The cooldown on Loot Crates was set to &a"
                        + Vice.getCrateManager().getCooldown() + " minutes&7!"));
                return true;
            case "check":
                s.sendMessage(Lang.LOOTCRATES
                        .f("&7Right click on the Loot Crate of which you would like to check the cooldown;"));
                user.setBooleanToStorage(BooleanStorageType.CHECKING_LOOTCRATE, true);
                return true;
            case "restock":
                s.sendMessage(Lang.LOOTCRATES.f("&7Right click the Loot Crate which you want to restock."));
                user.setBooleanToStorage(BooleanStorageType.RESTOCKING_LOOTCRATE, true);
                return true;
            case "removeitem": {
                if (args.length != 2) {
                    s.sendMessage(Utils.f("&c/lootcrate removeitem <itemName>"));
                    return true;
                }
                GameItem item = Vice.getItemManager().getItem(args[1]);
                if (item == null) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7That GameItem does not exist!"));
                    return true;
                }
                LootItem lootItem = Vice.getCrateManager().getItem(item);
                if (lootItem == null) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7That GameItem is not added to Loot Crates!"));
                    return true;
                }
                Vice.getCrateManager().removeItem(lootItem);
                s.sendMessage(Lang.LOOTCRATES.f("&7GameItem &a" + item.getName() + "&7 was removed from LootCrates!"));
                return true;
            }
            case "items":
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/lootcrate items <itemName> <chance> [min] [max]"));
                    return true;
                }
                GameItem item = Vice.getItemManager().getItem(args[1]);
                if (item == null) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7That GameItem does not exist!"));
                    return true;
                }

                double chance;
                int min;
                int max;
                try {
                    chance = Double.parseDouble(args[2]);
                    min = args.length > 3 ? Integer.parseInt(args[3]) : 1;
                    max = args.length > 4 ? Integer.parseInt(args[4]) : min;
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7The chance must be a double, min and max must be integers!"));
                    return true;
                }
                if (min > max) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7The maximum must be greater than or equal to the minimum!"));
                    return true;
                }
                LootItem lootItem = Vice.getCrateManager().getItem(item);
                if (lootItem == null)
                    Vice.getCrateManager().addItem(new LootItem(item.getName(), chance, min, max, false, Area.DropType.DEFAULT));
                else {
                    lootItem.setChance(chance);
                    lootItem.setMin(min);
                    lootItem.setMax(max);
                }
                s.sendMessage(Lang.LOOTCRATES
                        .f("&7You added GameItem &a" + item.getName() + "&7 to LootCrates with a chance of &a" + chance
                                + "&7, a min of &a" + min + "&7 and a max of &a" + max + "&7!"));
                return true;
            case "list":
                List<LootCrate> crates = Vice.getCrateManager().getCrates();
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.LOOTCRATES.f("&cThe page must be a number!"));
                        return true;
                    }
                }
                if (page < 1) {
                    s.sendMessage(Lang.LOOTCRATES.f("&7The page must be a positive number!"));
                    return true;
                }
                int pages = crates.size() / 6 + 1;
                s.sendMessage(Utils.f(" &7&m---------------&7[&e&l Loot Crates List &7Page &e" + page + "&7/&e" + pages
                        + " &7&m]---------------"));
                Iterator<LootCrate> it = crates.iterator();
                for (int i = 0; i < page * 6; i++) {
                    if (!it.hasNext())
                        return true;
                    LootCrate cr = it.next();
                    if (i < page * 6 - 6)
                        continue;
                    s.sendMessage(Utils.f("&e" + Utils.blockLocationToString(cr.getLocation())));
                }
                return true;
            case "load":
                Vice.getSettings().setLootCratesConfig(Utils.loadConfig("lootcrates"));
                Vice.getSettings().setLootConfig(Utils.loadConfig("loot"));
                Vice.getCrateManager().loadCrates();
                s.sendMessage(Lang.LOOTCRATES.f("&7Loaded LootCrates!"));
                return true;
            case "save":
                Vice.getCrateManager().saveCrates();
                s.sendMessage(Lang.LOOTCRATES.f("&7Saved LootCrates!"));
                return true;
            default:
                s.sendMessage(Utils.f("&c/lootcrates add"));
                s.sendMessage(Utils.f("&c/lootcrates remove"));
                s.sendMessage(Utils.f("&c/lootcrates cooldown <minutes> "));
                s.sendMessage(Utils.f("&c/lootcrates check"));
                s.sendMessage(Utils.f("&c/lootcrates restock"));
                s.sendMessage(Utils.f("&c/lootcrates list [page]"));
                s.sendMessage(Utils.f("&c/lootcrates items <itemName> <chance> <min> <max>"));
                s.sendMessage(Utils.f("&c/lootcrates removeitem <itemName>"));
                s.sendMessage(Utils.f("&c/lootcrates load"));
                s.sendMessage(Utils.f("&c/lootcrates save"));
                return true;
        }
    }

}