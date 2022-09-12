package net.grandtheftmc.gtm.items;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.items.GameItem.ItemType;
import net.grandtheftmc.guns.weapon.Weapon;

public class GameItemCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.gameitem")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }

        boolean consoleGive = false;
        if (!(s instanceof Player)) {
            if(!(args.length >= 1 && args[0].equalsIgnoreCase("give"))) {
                s.sendMessage(Lang.NOTPLAYER.s());
                return true;
            }
            else consoleGive = true;
        }

//        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/gameitem add <itemName> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem adds <itemName> <sellPrice> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem addweapon <itemName> <weapon> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem addvehicle <itemName> <vehicle> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem addarmorupgrade <itemName> <armorupgrade> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem adddrug <itemName> <drug> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem remove <itemName>"));
            s.sendMessage(Utils.f("&c/gameitem displayName <itemName> [displayName]"));
            s.sendMessage(Utils.f("&c/gameitem sellprice <itemName> <price>"));
            s.sendMessage(Utils.f("&c/gameitem get <itemName> <amount>"));
            s.sendMessage(Utils.f("&c/gameitem give <player> <itemName> <amount>"));
            s.sendMessage(Utils.f("&c/gameitem list [page]"));
            s.sendMessage(Utils.f("&c/gameitem load"));
            s.sendMessage(Utils.f("&c/gameitem save"));
            return true;
        }
        ItemManager im = GTM.getItemManager();
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/gameitem add <itemName> [displayName]"));
                    return true;
                }
                ItemStack item = ((Player) s).getInventory().getItemInMainHand();
                if (item == null) {
                    ((Player) s).sendMessage(Lang.GAMEITEMS.f("&cYou need to hold an item in your hand!"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }

                String displayName;
                if (args.length > 2) {
                    displayName = args[2];
                    for (int i = 3; i < args.length; i++)
                        displayName += ' ' + args[i];
                } else {
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();

                }
                im.addItem(new GameItem(args[1], item, displayName));
                s.sendMessage(Lang.GAMEITEMS
                        .f("&7You added an item with name &a" + args[1] + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "adds": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem adds <itemName> <sellPrice> [displayName]"));
                    return true;
                }
                ItemStack item = ((Player) s).getInventory().getItemInMainHand();
                if (item == null) {
                    ((Player) s).sendMessage(Lang.GAMEITEMS.f("&cYou need to hold an item in your hand!"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                double price;
                try {
                    price = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be a number! (double)"));
                    return true;
                }
                if (price < 0) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be 0 or higher!"));
                    return true;

                }
                String displayName;
                if (args.length > 3) {
                    displayName = args[3];
                    for (int i = 4; i < args.length; i++)
                        displayName = displayName + ' ' + args[i];
                } else {
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                }
                gi = im.addItem(new GameItem(args[1], item, displayName, price));
                s.sendMessage(Lang.GAMEITEMS.f("&7You added an item with name &a" + args[1] + "&7 and sell price &a$&l"
                        + gi.getSellPrice() + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "addweapon": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem addweapon <itemName> <weapon> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                Optional<Weapon<?>> w = GTM.getWastedGuns().getWeaponManager().getWeapon(args[2]);
                if (w == null || !w.isPresent()) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That weapon does not exist!"));
                    return true;
                }
                Weapon weapon = w.get();
                if (weapon == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That weapon does not exist!"));
                    return true;
                }
                String displayName;
                if (args.length > 3) {
                    displayName = args[3];
                    for (int i = 4; i < args.length; i++)
                        displayName = displayName + ' ' + args[i];
                } else {
                    ItemStack item = weapon.getBaseItemStack();
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                }
                im.addItem(new GameItem(ItemType.WEAPON, args[1], weapon.getName(), displayName));
                s.sendMessage(Lang.GAMEITEMS.f("&7You added an item with name &a" + args[1] + "&7 and weapon &a"
                        + weapon.getUniqueIdentifier() + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "adddrug": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem addweapon <itemName> <weapon> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                Optional<Drug> drug = ((DrugService) GTM.getInstance().getDrugManager().getService()).getDrug(args[2]);
                if (drug.isPresent()) {
                    DrugItem drugItem = DrugItem.getByDrug(drug.get());
                    if (drugItem == null) {
                        s.sendMessage(Lang.GAMEITEMS.f("&7That drug does not exist!"));
                        return true;
                    }
                    String displayName;
                    if (args.length > 3) {
                        displayName = args[3];
                        for (int i = 4; i < args.length; i++)
                            displayName = displayName + ' ' + args[i];
                    } else {
                        ItemStack item = drugItem.getItemStack();
                        ItemMeta meta = item.getItemMeta();
                        displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                    }
                    im.addItem(new GameItem(ItemType.DRUG, args[1], drug.get().getName(), displayName));
                    s.sendMessage(Lang.GAMEITEMS.f("&7You added an item with name &a" + args[1] + "&7 and drug &a"
                            + drug.get().getName() + "&7 and Display Name " + displayName + "&7!"));
                }
                return true;
            }
            case "addweapons": {
                if (args.length < 4) {
                    s.sendMessage(Utils.f("&c/gameitem addweapons <itemName> <weapon> <sellPrice> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                Optional<Weapon<?>> w = GTM.getWastedGuns().getWeaponManager().getWeapon(args[2]);
                if (w == null || !w.isPresent()) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That weapon does not exist!"));
                    return true;
                }
                Weapon weapon = w.get();
                if (weapon == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That weapon does not exist!"));
                    return true;
                }
                double price;
                try {
                    price = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be a number! (double)"));
                    return true;
                }
                if (price < 0) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be 0 or higher!"));
                    return true;
                }
                String displayName;
                if (args.length > 4) {
                    displayName = args[4];
                    for (int i = 5; i < args.length; i++)
                        displayName = displayName + ' ' + args[i];
                } else {
                    ItemStack item = weapon.getBaseItemStack();
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                }
                im.addItem(new GameItem(ItemType.WEAPON, args[1], weapon.getName(), displayName, price, 0));
                s.sendMessage(Lang.GAMEITEMS
                        .f("&7You added an item with name &a" + args[1] + "&7 and weapon &a" + weapon.getName()
                                + "&7 and sell price &a$&l" + price + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "addvehicle": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem addvehicle <itemName> <vehicle> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                Optional<VehicleProperties> v = GTM.getWastedVehicles().getVehicle(args[2]);
                if (v == null || !v.isPresent()) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That vehicle does not exist!"));
                    return true;
                }
                VehicleProperties vehicle = v.get();
                if (vehicle == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That vehicle does not exist!"));
                    return true;
                }
                String displayName;
                if (args.length > 3) {
                    displayName = args[3];
                    for (int i = 4; i < args.length; i++)
                        displayName += ' ' + args[i];
                } else {
                    ItemStack item = vehicle.getItem();
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                }
                im.addItem(new GameItem(ItemType.VEHICLE, args[1], vehicle.getIdentifier(), displayName));
                s.sendMessage(Lang.GAMEITEMS.f("&7You added an item with name &a" + args[1] + "&7 and vehicle &a"
                        + vehicle.getIdentifier() + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "addvehicles": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem addweapons <itemName> <vehicle> <sellPrice> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                Optional<VehicleProperties> v = GTM.getWastedVehicles().getVehicle(args[2]);
                if (v == null || !v.isPresent()) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That vehicle does not exist!"));
                    return true;
                }
                VehicleProperties vehicle = v.get();
                if (vehicle == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That vehicle does not exist!"));
                    return true;
                }
                double price;
                try {
                    price = Double.parseDouble(args[3]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be a number! (double)"));
                    return true;
                }
                if (price < 0) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be 0 or higher!"));
                    return true;
                }
                String displayName;
                if (args.length > 4) {
                    displayName = args[4];
                    for (int i = 5; i < args.length; i++)
                        displayName += ' ' + args[i];
                } else {
                    ItemStack item = vehicle.getItem();
                    ItemMeta meta = item.getItemMeta();
                    displayName = meta == null || meta.getDisplayName() == null ? item.getType().name() : meta.getDisplayName();
                }
                im.addItem(new GameItem(ItemType.VEHICLE, args[1], vehicle.getIdentifier(), displayName, price, 0));
                s.sendMessage(Lang.GAMEITEMS
                        .f("&7You added an item with name &a" + args[1] + "&7 and vehicle &a" + vehicle.getIdentifier()
                                + "&7 and sell price &a$&l" + price + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "addarmorupgrade": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem addarmorupgrade <itemName> <armorupgrade> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi != null) {
                    im.removeItem(gi);
                    ((Player) s).sendMessage(Lang.GAMEITEMS
                            .f("&7That item already existed, so it has been deleted and replaced with the new one."));
                }
                ArmorUpgrade upgrade = ArmorUpgrade.getArmorUpgrade(args[2]);
                if (upgrade == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That vehicle does not exist!"));
                    return true;
                }
                String displayName = Utils.f("&b&l" + upgrade.getDisplayName() + " Armor Upgrade: &a" + upgrade.getDisplayName());
                im.addItem(new GameItem(args[1], upgrade, displayName));
                s.sendMessage(Lang.GAMEITEMS.f("&7You added an item with name &a" + args[1] + "&7 and armor upgrade &a"
                        + upgrade.getDisplayName() + "&7 and Display Name " + displayName + "&7!"));
                return true;
            }
            case "remove": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/gameitem remove <itemName>"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That GameItem does not exist!"));
                    return true;
                }
                im.removeItem(gi);
                s.sendMessage(Lang.GAMEITEMS.f("&7GameItem &a" + gi.getName() + "&7 was removed!"));
                return true;
            }
            case "displayname": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/gameitem displayName <itemName> [displayName]"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That GameItem does not exist!"));
                    return true;
                }
                String displayName = args[2];
                for (int i = 3; i < args.length; i++)
                    displayName = displayName + ' ' + args[i];
                gi.setDisplayName(displayName);
                s.sendMessage(Lang.GAMEITEMS.f("&7You set the display name of GameItem &a" + gi.getName() + "&7 to &a"
                        + gi.getDisplayName() + '!'));
                return true;
            }
            case "sellprice": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/gameitem sellprice <itemName> <price>"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That GameItem does not exist!"));
                    return true;
                }
                double price;
                try {
                    price = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be a number! (double)"));
                    return true;
                }
                if (price < 0) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The price must be 0 or higher!"));
                    return true;

                }
                gi.setSellPrice(price);
                s.sendMessage(Lang.GAMEITEMS.f("&7You set the sell price of GameItem &a" + gi.getName() + "&7 to &a$&l"
                        + gi.getSellPrice() + '!'));
                return true;
            }
            case "get": {
                if (args.length < 2) {
                    s.sendMessage(Utils.f("&c/gameitem get <itemName> <amount>"));
                    return true;
                }
                GameItem gi = im.getItem(args[1]);
                if (gi == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That GameItem does not exist!"));
                    return true;
                }
                ItemStack item = gi.getItem();
                if (args.length > 2)
                    try {
                        item.setAmount(Integer.parseInt(args[2]));
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.GAMEITEMS.f("&7The amount must be a number! (integer)"));
                        return true;
                    }
                ((Player) s).getInventory().addItem(item);
                s.sendMessage(Lang.GAMEITEMS.f((args.length > 2 ? "&a" + args[2] + "&7 of " : "") + "&7GameItem &a"
                        + gi.getName() + "&7 was added to your inventory!"));
                return true;
            }
            case "give": {
                if (args.length < 3) {
                    s.sendMessage(Utils.f("&c/gameitem give <player> <itemName> <amount>"));
                    return true;
                }
                Player pl = Bukkit.getPlayer(args[1]);
                if (pl == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That player is not online!"));
                    return true;
                }
                GameItem gi = im.getItem(args[2]);
                if (gi == null) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7That GameItem does not exist!"));
                    return true;
                }
                ItemStack item = gi.getItem();
                if (args.length > 3)
                    try {
                        item.setAmount(Integer.parseInt(args[3]));
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.GAMEITEMS.f("&7The amount must be a number! (integer)"));
                        return true;
                    }
                pl.getInventory().addItem(item);
                s.sendMessage(Lang.GAMEITEMS.f("&7You gave " + (args.length > 3 ? "&a" + args[3] + "&7 of " : "")
                        + "GameItem &a" + gi.getName() + "&7 to &a" + pl.getName() + '!'));
                return true;
            }
            case "list":
                List<GameItem> items = GTM.getItemManager().getItems();
                int page = 1;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]);
                    } catch (NumberFormatException e) {
                        s.sendMessage(Lang.GAMEITEMS.f("&cThe page must be a number!"));
                        return true;
                    }
                }
                if (page < 1) {
                    s.sendMessage(Lang.GAMEITEMS.f("&7The page must be a positive number!"));
                    return true;
                }
                int pages = items.size() / 6 + 1;
                s.sendMessage(Utils.f(" &7&m---------------&7[&a&l Game Items &7Page &a" + page + "&7/&a" + pages
                        + " &7&m]---------------"));
                Iterator<GameItem> it = items.iterator();
                for (int i = 0; i < page * 6; i++) {
                    if (!it.hasNext())
                        return true;
                    GameItem item = it.next();
                    if (i < page * 6 - 6)
                        continue;
                    s.sendMessage(Utils.f("&a&l" + item.getType() + "&a " + item.getName()
                            + " &7| &7Display Name: &r" + item.getDisplayName()
                            + (item.getSellPrice() >= 0 ? "&7 Sell Price: &a$&l" + item.getSellPrice() : "")));
                }
                return true;
            case "load":
                GTM.getSettings().setItemsConfig(Utils.loadConfig("items"));
                GTM.getItemManager().loadItems();
                s.sendMessage(Lang.GAMEITEMS.f("&7Loaded GameItems!"));
                return true;
            case "save":
                GTM.getItemManager().saveItems();
                s.sendMessage(Lang.GAMEITEMS.f("&7Saved GameItems!"));
                return true;
            default:
                s.sendMessage(Utils.f("&c/gameitem add <itemName> [displayName]"));
                s.sendMessage(Utils.f("&c/gameitem addweapon <itemName> [displayName]"));
                s.sendMessage(Utils.f("&c/gameitem addvehicle <itemName> [displayName]"));
                s.sendMessage(Utils.f("&c/gameitem remove <itemName>"));
                s.sendMessage(Utils.f("&c/gameitem displayName <itemName> [displayName]"));
                s.sendMessage(Utils.f("&c/gameitem sellprice <itemName> <price>"));
                s.sendMessage(Utils.f("&c/gameitem get <itemName>"));
                s.sendMessage(Utils.f("&c/gameitem give <player> <itemName>"));
                s.sendMessage(Utils.f("&c/gameitem list [page]"));
                s.sendMessage(Utils.f("&c/gameitem load"));
                s.sendMessage(Utils.f("&c/gameitem save"));
                return true;
        }
    }
}