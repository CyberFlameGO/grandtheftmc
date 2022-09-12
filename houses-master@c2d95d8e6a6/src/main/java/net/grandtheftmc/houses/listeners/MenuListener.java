package net.grandtheftmc.houses.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuClickEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.menus.MenuOpenEvent;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.*;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class MenuListener implements Listener {

    public void setPhoneDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52})
            e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6})
            e.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51})
            e.setItem(i, grayGlass);
    }

    public void setPhoneDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52})
            inv.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6})
            inv.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51})
            inv.setItem(i, grayGlass);
    }

    public void setConfirmDefaults(MenuOpenEvent e) {
        this.setConfirmDefaults(e, "&a&lConfirm", "&c&lCancel");
    }

    public void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage);
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52})
            e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6})
            e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,})
            e.setItem(i, grayGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48})
            e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51})
            e.setItem(i, redGlass);
    }

    @EventHandler
    public void onMenuOpen(MenuOpenEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        switch (menu.getName()) {
            case "houses": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                List<UserHouse> houses = user.getHouses();
                List<PremiumHouse> premiumHouses = user.getPremiumHousesAsGuest();
                Iterator<UserHouse> it = houses.iterator();
                Iterator<PremiumHouse> it2 = premiumHouses.iterator();
                int size = houses.size() + premiumHouses.size();
                int max = user.getMaxHouses(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid));
                for (int i = 0; i < 20; i++) {
                    PremiumHouse premiumHouse = it.hasNext() ? null : it2.hasNext() ? it2.next() : null;
                    UserHouse userHouse = it.hasNext() ? it.next() : null;
                    if (premiumHouse == null && userHouse == null)
                        break;
                    if (premiumHouse != null) {
                        e.setItem(slots[i],
                                Utils.addGlow(Utils.createItem(Material.IRON_DOOR, "&3&lPremium House: &a&l" + premiumHouse.getId(),
                                        Arrays.asList("&7Permits: &a&l" + premiumHouse.getPermits(), "&7Chests: &a&l" + premiumHouse.getChests().size(),
                                                "&7Owned by &a" + (player.getUniqueId().equals(premiumHouse.getOwner()) ? "me" : premiumHouse.getOwnerName()) + "&7."))));
                        continue;
                    }
                    House house = Houses.getHousesManager().getHouse(userHouse.getId());
                    e.setItem(slots[i], Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(),
                            Arrays.asList("&7Price: &a$&l" + house.getPrice(), "&7Chests: &a&l" + house.getChests().size())));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.IRON_DOOR, "&3&lMy Houses", "&7Click on a house to open its menu!"), Color.fromRGB(102, 127, 51)));
                if (size > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));
                e.setItem(51, Utils.createItem(Material.BOOK, "&3&lMax Houses: &a&l" + size + "&7/&a&l" + max,
                        size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get access to more houses!" : "&7You can own &a" + (max - size) + "&7 more houses!"));
                return;
            }
            case "house": {
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                int id = user.getMenuHouseId();
                House house = Houses.getManager().getHouse(id);
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.CHEST, "&3&lChests: &a&l" + house.getAmountOfChests()));
                e.setItem(13, Utils.createItem(Material.PAPER, "&3&lPrice: &a$&l" + house.getPrice()));
                e.setItem(15, Utils.createItem(Material.DARK_OAK_DOOR_ITEM, "&3&lDoors: &a&l" + house.getDoors().size()));
                if (user.ownsHouse(house.getId()))
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lSell House", Collections.singletonList("&7Reward: &a$&l" + (house.getPrice() / 2))));
                else
                    e.setItem(31, Utils.createItem(Material.SLIME_BALL, "&a&lBuy House", Collections.singletonList("&7Price: &a$&l" + house.getPrice())));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the houses menu!"));
                e.setItem(49, Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + id, "&7A place to crash!"));
                int size = user.getHouses().size() + user.getPremiumHousesAsGuest().size();
                int max = user.getMaxHouses(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid));
                e.setItem(51, Utils.createItem(Material.BOOK, "&3&lMax Houses: &a&l" + size + "&7/&a&l" + max,
                        size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get access to more houses!" : "&7You can own &a" + (max - size) + "&7 more houses!"));
                return;
            }
            case "premiumhouse": {
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                int id = user.getMenuHouseId();
                PremiumHouse house = Houses.getManager().getPremiumHouse(id);
                this.setPhoneDefaults(e);

                e.setItem(11, Utils.createItem(Material.CHEST, "&3&lChests: &a&l" + house.getAmountOfChests()));
                e.setItem(13, Utils.createItem(Material.PAINTING, "&3&lPermits: &a&l" + house.getPermits(),
                        "&7You have &a&l" + GTM.getUserManager().getLoadedUser(uuid).getPermits() + "&7 Permits!"));
                e.setItem(15, Utils.createItem(Material.IRON_DOOR, "&3&lDoors: &a&l" + house.getDoors().size()));
                if (!house.getEditableBlocks().isEmpty()) {
                    e.setItem(29, Utils.createItem(Material.SNOW_BLOCK, "&3&lCustomize House"));
                }
                if (!house.isOwned())
                    e.setItem(31, Utils.createItem(Material.SLIME_BALL, "&a&lBuy House", Collections.singletonList("&7Price: &a&l" + house.getPermits() + " Permits")));
                else if (player.getUniqueId().equals(house.getOwner()))
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lSell House", Collections.singletonList("&7Reward: &a&l" + house.getPermits() + " Permits")));
                else
                    e.setItem(31, Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, "&3&lOwner: &a&l" + house.getOwnerName()), house.getOwnerName()));
                e.setItem(33, Utils.createItem(Material.TRIPWIRE_HOOK, "&3&lView Guests"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the houses menu!"));
                e.setItem(49, Utils.createItem(Material.IRON_DOOR, "&3&lPremium House: &a&l" + id, "&7A place to crash!"));
                int size = user.getHouses().size() + user.getPremiumHousesAsGuest().size();
                int max = user.getMaxHouses(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid));
                e.setItem(51, Utils.createItem(Material.BOOK, "&3&lMax Houses: &a&l" + size + "&7/&a" + max,
                        size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get access to more houses!" : "&7You can own &a" + (max - size) + "&7 more houses!"));
                return;
            }
            case "buyhouse":
                this.setConfirmDefaults(e, "&a&lClick to buy this house!", "&c&lClick to cancel!");
                return;
            case "sellhouse":
                this.setConfirmDefaults(e, "&a&lClick to sell this house!", "&c&lClick to cancel!");
                return;
            case "buypremiumhouse":
                this.setConfirmDefaults(e, "&a&lClick to buy this premium house!", "&c&lClick to cancel!");
                return;
            case "sellpremiumhouse":
                this.setConfirmDefaults(e, "&a&lClick to sell this premium house!", "&c&lClick to cancel!");
                return;
            case "guests": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                int id = user.getMenuHouseId();
                PremiumHouse house = Houses.getManager().getPremiumHouse(id);
                List<PremiumHouseGuest> guests = house.getGuests();
                Iterator<PremiumHouseGuest> it = guests.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    PremiumHouseGuest guest = it.next();
                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2, "&3&l" + guest.getName()), guest.getName()));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the premium house menu!"));
                if (house.isOwner(uuid)) {
                    e.setItem(49, Utils.createItem(Material.SLIME_BALL, "&a&lAdd Guest"));
                    e.setItem(51, Utils.createItem(Material.INK_SACK, 1, "&c&lRemove Guest"));
                }
                if (guests.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));
                return;
            }
            case "removeguests": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51};
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                int id = user.getMenuHouseId();
                PremiumHouse house = Houses.getManager().getPremiumHouse(id);
                List<PremiumHouseGuest> guests = house.getGuests();
                Iterator<PremiumHouseGuest> it = guests.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    PremiumHouseGuest guest = it.next();
                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2, "&3&l" + guest.getName(), "&7Click to remove!"), guest.getName()));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the premium house menu!"));
                e.setItem(49, Utils.createItem(Material.SLIME_BALL, "&a&lAdd Guest"));
                e.setItem(51, Utils.createItem(Material.INK_SACK, 1, "&c&lStop Removing Guests"));
                if (guests.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));
                return;
            }
            case "houseshelp": {
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.EMPTY_MAP, "&3&lWhat are houses?", "", "&7A house is a safezone where you can store items in chests."));
                e.setItem(12, Utils.createItem(Material.EMPTY_MAP, "&3&lCan I share my house with someone?", "", "&7Multiple people can buy the same house.",
                        "&7Inside of the house, everyone is invisible", "&7and the chests are unique to every owner."));
                e.setItem(13, Utils.createItem(Material.EMPTY_MAP, "&3&lWhat's so special about Premium Houses?", "", "&7The owner of a Premium House can add guests to it",
                        "&7who can also open the chests!"));
                e.setItem(14, Utils.createItem(Material.EMPTY_MAP, "&3&lHow can I buy a Premium House?", "", "&7Premium Houses can only be purchased through donating.",
                        "&7Get House Permits at &a" + Core.getSettings().getStoreLink() + "&7", "&7to buy a Premium House!"));
                e.setItem(15, Utils.createItem(Material.EMPTY_MAP, "&3&lHow many houses can I buy?", "", "&7Ranking up gets you access to more houses,",
                        "&7as well as getting a donor rank at &a" + Core.getSettings().getStoreLink() + "&7!"));
                e.setItem(20, Utils.createItem(Material.EMPTY_MAP, "&3&lFor how long do I get to keep my house?", "", "&7After you buy it, you can keep it forever!"));
                e.setItem(21, Utils.createItem(Material.EMPTY_MAP, "&3&lDo I get my money back when I sell a house?", "", "&7When you sell a house, you get back half of the price.",
                        "&7If you sell a Premium House, all of the Permits will get refunded!"));
                e.setItem(22, Utils.createItem(Material.EMPTY_MAP, "&3&lCan I give my permits to someone else?", "", "&7No, you may not. Sorry!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the houses menu!"));
                return;
            }
            case "editblocks": {
                int count = -1;
                for (Blocks material : Blocks.values()) {
                    if (count == 53) break;
                    count += 1;
                    ItemStack itemStack = new ItemStack(material.getType());
                    itemStack.setDurability(material.getData());
                    e.setItem(count, itemStack);
                }
                return;
            }
            case "buytrashcan": {
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                PremiumHouseTrashcan trashcan = user.getOpenTrashcan();
                PremiumHouse house = Houses.getHousesManager().getPremiumHouse(user.getInsidePremiumHouse());
                this.setPhoneDefaults(e);

                e.setItem(11, Utils.createItem(Material.CHEST, "&3&lChests: &a&l" + house.getAmountOfChests()));
                e.setItem(13, Utils.createItem(Material.PAINTING, "&3&lPermits: &a&l" + house.getPermits(),
                        "&7You have &a&l" + GTM.getUserManager().getLoadedUser(uuid).getPermits() + "&7 Permits!"));
                e.setItem(15, Utils.createItem(Material.IRON_DOOR, "&3&lDoors: &a&l" + house.getDoors().size()));

                e.setItem(31, Utils.createItem(Material.SLIME_BALL, "&a&lBuy Trashcan", Collections.singletonList("&7Price: &a&l5 Permits")));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Close this menu"));
                e.setItem(49, Utils.createItem(Material.DROPPER, "&3&lTrash Can: &a&l" + trashcan.getId(), "&7Sell your loot!"));

                int size = user.getHouses().size() + user.getPremiumHousesAsGuest().size();
                int max = user.getMaxHouses(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid));
                e.setItem(51, Utils.createItem(Material.BOOK, "&3&lMax Houses: &a&l" + size + "&7/&a" + max,
                        size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get access to more houses!" : "&7You can own &a" + (max - size) + "&7 more houses!"));
                return;
            }
            case "confirmtrashcanbuy": {
                this.setConfirmDefaults(e, "&a&lClick to buy this Trash Can for 5 permits", "&c&lClick to cancel!");
                return;
            }
            default:
                break;
        }
    }

    @EventHandler
    public void onMenuClick(MenuClickEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        ItemStack item = e.getItem();
        Inventory inv = e.getInv();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
        switch (menu.getName()) {
            case "houses":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case IRON_DOOR:
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if ("Find House".equals(s))
                            return;
                        if (s.startsWith("Premium House: ")) {
                            int id;
                            try {
                                id = Integer.parseInt(s.replace("Premium House: ", ""));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7That House ID is invalid!"));
                                return;
                            }
                            PremiumHouse house = Houses.getHousesManager().getPremiumHouse(id);
                            if (house == null) {
                                player.sendMessage(Lang.HOUSES.f("&7That premium house does not exist!"));
                                return;
                            }
                            user.setMenuHouseId(house.getId());
                            MenuManager.openMenu(player, "premiumhouse");
                            return;
                        } else if (s.startsWith("House: ")) {
                            int id;
                            try {
                                id = Integer.parseInt(s.replace("House: ", ""));
                            } catch (NumberFormatException ex) {
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7That House ID is invalid!"));
                                return;
                            }
                            House house = Houses.getHousesManager().getHouse(id);
                            if (house == null) {
                                player.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                                return;
                            }
                            user.setMenuHouseId(house.getId());
                            MenuManager.openMenu(player, "house");
                            return;
                        }
                        return;
                    case ARROW:
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
                        List<UserHouse> houses = user.getHouses();
                        List<PremiumHouse> premiumHouses = user.getPremiumHousesAsGuest();
                        Iterator<UserHouse> it = houses.iterator();
                        Iterator<PremiumHouse> it2 = premiumHouses.iterator();
                        int size = houses.size() + premiumHouses.size();
                        int max = user.getMaxHouses(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid));
                        for (int i = 0; i < (page * 20); i++) {
                            PremiumHouse premiumHouse = it.hasNext() ? null : it2.hasNext() ? it2.next() : null;
                            UserHouse userHouse = it.hasNext() ? it.next() : null;
                            if (premiumHouse == null && userHouse == null)
                                break;
                            if (i < (page - 1) * 20)
                                continue;
                            if (premiumHouse != null) {
                                inv.setItem(slots[i - (page - 1) * 20],
                                        Utils.addGlow(Utils.createItem(Material.IRON_DOOR, "&3&lPremium House: &a&l" + premiumHouse.getId(),
                                                Arrays.asList("Permits: &a&l" + premiumHouse.getPermits(), "&7Chests: &a&l" + premiumHouse.getChests().size(),
                                                        "&7Owned by &a" + (player.getUniqueId().equals(premiumHouse.getOwner()) ? "me" : premiumHouse.getOwnerName()) + '.'))));
                                continue;
                            }

                            House house = Houses.getHousesManager().getHouse(userHouse.getId());
                            inv.setItem(slots[i - (page - 1) * 20], Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(),
                                    Arrays.asList("&7Price: &$a&l" + house.getPrice(), "&7Chests: &a&l" + house.getChests().size())));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&3llPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.IRON_DOOR, "&3&lMy Houses", "&7Click on a house to open its menu!"), Color.fromRGB(102, 127, 51)));
                        if ((houses.size() + premiumHouses.size()) > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));
                        inv.setItem(51, Utils.createItem(Material.BOOK, "&3&lMax Houses: &a&l" + size + "&7/&a" + max,
                                size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get access to more houses!" : "&7You can own &a" + (max - size) + "&7 more houses!"));
                        return;
                    default:
                        return;

                }
            case "house":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case SLIME_BALL:
                        MenuManager.openMenu(player, "buyhouse");
                        return;
                    case INK_SACK:
                        MenuManager.openMenu(player, "sellhouse");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "houses");
                    default:
                        return;
                }
            case "editblocks":
                if (item == null) return;
                if (!user.isInsidePremiumHouse()) {
                    return;
                }
                if (user.getEditingBlock() == null) return;
                if (!Blocks.getMaterials().contains(item.getType())) return;
                user.getEditingBlock().setType(item.getType(), false);
                user.getEditingBlock().setData(item.getData().getData());
                user.setEditingBlock(null);
                player.sendMessage(Lang.HOUSES.f("&7Block has been set to &a" + item.getType().name() + "&7!"));
                user.setLastUsedMaterial(Blocks.match(item.getType(), item.getData().getData()));
                player.closeInventory();
                return;
            case "premiumhouse":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case SLIME_BALL:
                        MenuManager.openMenu(player, "buypremiumhouse");
                        return;
                    case INK_SACK:
                        MenuManager.openMenu(player, "sellpremiumhouse");
                        return;
                    case TRIPWIRE_HOOK:
                        MenuManager.openMenu(player, "guests");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "houses");
                        return;
                    case SNOW_BLOCK:
                        PremiumHouse house = Houses.getHousesManager().getPremiumHouse(user.getMenuHouseId());
                        if (!house.getOwnerName().equalsIgnoreCase(player.getName())) return;
                        if (!user.isInsidePremiumHouse()) {
                            user.teleportInOrOutPremiumHouse(player, house);
                        }
                        if (user.isChangingBlocks()) {
                            player.sendMessage(Lang.HOUSES.f("&7House Editing disabled"));
                        } else {
                            player.sendMessage(Core.getAnnouncer().getHeader());
                            player.sendMessage(Lang.HOUSES.f("&7You are now editing your house, right click a block to begin!"));
                            player.sendMessage(Lang.HOUSES.f("&7SHIFT + Right click a block to automatically set it to your last used block" +
                                    " (for quick changing)"));
                            player.sendMessage(Lang.HOUSES.f("&7Once you're finished, leave your house to disable house editing." +
                                    " (You can also use your door GUI to disable it)"));
                            player.sendMessage(Core.getAnnouncer().getFooter());
                        }
                        user.setChangingBlocks(!user.isChangingBlocks());
                        return;
                    default:
                        return;
                }
            case "sellhouse":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                House house = Houses.getHousesManager().getHouse(user.getMenuHouseId());
                                if (house == null) {
                                    player.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                                    return;
                                }
                                house.sellHouse(player, GTM.getUserManager().getLoadedUser(uuid), user);
                            case 14:
                                MenuManager.openMenu(player, "house");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }

            case "buyhouse":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                House house = Houses.getHousesManager().getHouse(user.getMenuHouseId());
                                if (house == null) {
                                    player.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                                    return;
                                }
                                house.buyHouse(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid), user);
                            case 14:
                                MenuManager.openMenu(player, "house");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }

            case "sellpremiumhouse":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                PremiumHouse house = Houses.getHousesManager().getPremiumHouse(user.getMenuHouseId());
                                house.sell(player, GTM.getUserManager().getLoadedUser(uuid), user);
                            case 14:
                                MenuManager.openMenu(player, "premiumhouse");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "buypremiumhouse":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                PremiumHouse house = Houses.getHousesManager().getPremiumHouse(user.getMenuHouseId());
                                house.buy(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid), user);
                            case 14:
                                MenuManager.openMenu(player, "premiumhouse");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "guests":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
                        PremiumHouse house = Houses.getManager().getPremiumHouse(user.getMenuHouseId());
                        List<PremiumHouseGuest> guests = house.getGuests();
                        Iterator<PremiumHouseGuest> it = guests.iterator();
                        for (int i = 0; i < (page * 20); i++) {
                            if (!it.hasNext())
                                break;
                            PremiumHouseGuest guest = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2, "&3&l" + guest.getName()), guest.getName()));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the premium house menu!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&3&lPrevious Page", "&7Page " + (page - 1)));
                        if (house.getOwner().equals(player.getUniqueId())) {
                            inv.setItem(49, Utils.createItem(Material.SLIME_BALL, "&a&lAdd Guest"));
                            inv.setItem(51, Utils.createItem(Material.INK_SACK, 1, "&c&lRemove Guest"));
                        } else
                            inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.TRIPWIRE_HOOK, "&3&lPremium House Guests", "&7Players that can access this house!"),
                                    Color.fromRGB(102, 127, 51)));
                        if (guests.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));

                        return;
                    }
                    case SLIME_BALL: {
                        PremiumHouse house = Houses.getManager().getPremiumHouse(user.getMenuHouseId());
                        if (house.isOwner(uuid))
                            player.closeInventory();
                        user.addGuest(player, house);
                        return;
                    }
                    case INK_SACK:
                        PremiumHouse house = Houses.getManager().getPremiumHouse(user.getMenuHouseId());
                        if (house.getOwner().equals(user.getUUID()))
                            MenuManager.openMenu(player, "removeguests");
                        else
                            player.sendMessage(Utils.f(Lang.HOUSES + "&7You don't own this house!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "premiumhouse");
                        return;
                    default:
                        return;
                }
            case "removeguests":
                if (item == null)
                    return;
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
                        PremiumHouse house = Houses.getManager().getPremiumHouse(user.getMenuHouseId());
                        List<PremiumHouseGuest> guests = house.getGuests();
                        Iterator<PremiumHouseGuest> it = guests.iterator();
                        for (int i = 0; i < (page * 20); i++) {
                            if (!it.hasNext())
                                break;
                            PremiumHouseGuest guest = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            inv.setItem(slots[i - (page - 1) * 20],
                                    Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2, "&3&l" + guest.getName(), "&7Click to remove this guest!"), guest.getName()));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the premium house menu!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&3&lPrevious Page", "&7Page " + (page - 1)));
                        if (house.getOwner().equals(player.getUniqueId())) {
                            inv.setItem(49, Utils.createItem(Material.SLIME_BALL, "&a&lAdd Guest"));
                            inv.setItem(51, Utils.createItem(Material.INK_SACK, 1, "&c&lStop Removing Guests"));
                        } else
                            inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.TRIPWIRE_HOOK, "&3&lPremium House Guests", "&7Players that can access this house!"),
                                    Color.fromRGB(102, 127, 51)));
                        if (guests.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&3&lNext Page", "&7Page 2"));
                        return;
                    }
                    case SLIME_BALL: {
                        PremiumHouse house = Houses.getManager().getPremiumHouse(user.getMenuHouseId());
                        player.closeInventory();
                        user.addGuest(player, house);
                        return;
                    }
                    case SKULL_ITEM:
                        PremiumHouse house = Houses.getHousesManager().getPremiumHouse(user.getMenuHouseId());
                        String guest = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Player target = Bukkit.getPlayer(guest);
                        if (target == null) house.removeGuest(player, guest);
                        else
                            house.removeGuest(player, target, Houses.getUserManager().getLoadedUser(target.getUniqueId()));
                        MenuManager.openMenu(player, "removeguests");
                        return;
                    case INK_SACK:
                        MenuManager.openMenu(player, "guests");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "guests");
                        return;
                    default:
                        return;

                }
            case "houseshelp":
                if (item == null) return;
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "houses");
                        return;
                    default:
                        break;
                }
            case "buytrashcan":
                if (item == null) return;
                switch (item.getType()) {
                    case SLIME_BALL: {
                        MenuManager.openMenu(player, "confirmtrashcanbuy");
                        return;
                    }
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    default:
                        return;
                }
            case "confirmtrashcanbuy": {
                if (item == null) return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                if (user.getOpenTrashcan() == null) return;
                                GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
                                PremiumHouseTrashcan trashcan = user.getOpenTrashcan();
                                PremiumHouse premiumHouse = Houses.getHousesManager().getPremiumHouse(trashcan.getHouseId());
                                if (!premiumHouse.getOwnerName().equalsIgnoreCase(player.getName())) {
                                    player.sendMessage(Lang.HOUSES.f("&7Only the house owner may purchase house extensions!"));
                                    return;
                                }
                                if (gtmUser.hasPermits(5)) {
                                    gtmUser.takePermits(5);
                                    trashcan.setOwned(true);
                                    Houses.getHousesManager().save();
                                    Utils.insertLogLater(player.getUniqueId(), player.getName(), "confirmtrashcanbuyMenu", "BUY_TRASHCAN", "Premium House ID: " + trashcan.getHouseId(),1,5);
                                    player.sendMessage(Lang.HOUSES.f("&7Trash Can purchased for &3&l5 permits&7!"));
                                } else {
                                    player.sendMessage(Lang.HOUSES.f("&7You don't have the required 5 permits to purchase this!"));
                                }
                                player.closeInventory();
                                return;
                            case 14:
                                MenuManager.openMenu(player, "buytrashcan");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            }
        }
    }

}
