package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.MoneyEvent;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuClickEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.menus.MenuOpenEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.*;
import net.grandtheftmc.vice.tasks.LotteryPlayer;
import net.grandtheftmc.vice.users.*;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.grandtheftmc.vice.utils.StringUtils;
import net.grandtheftmc.vice.world.warps.Warp;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;


public class MenuListener implements Listener {

    private static final int[] DEFAULT_PHONE_OPEN_SLOTS = new int[]{11,13,15,29,31,33,47,49,51};

    @EventHandler(priority = EventPriority.HIGH)
    public void onMenuOpen(MenuOpenEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        switch (menu.getName()) {
            case "choose_villager_type": {
                this.setPhoneDefaults(e);
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(uuid);
                if(viceUser.getChangingJob()==null) {
                    player.sendMessage(Lang.CHEAT_CODES.f("&7Cannot open this menu because you do not have a villager selected!"));
                    return;
                }
                int counter = 0;
                for(Villager.Profession p : Villager.Profession.values()) {
                    ItemStack is = new ItemStack(Material.DIRT);
                    switch (p) {
                        case HUSK:
                        case NORMAL:
                            continue;
                        case BLACKSMITH:
                            is.setType(Material.ANVIL);
                            break;
                        case BUTCHER:
                            is.setType(Material.PORK);
                            break;
                        case LIBRARIAN:
                            is.setType(Material.BOOK);
                            break;
                        case FARMER:
                            is.setType(Material.WOOD_HOE);
                            break;
                        case PRIEST:
                            is.setType(Material.GOLDEN_APPLE);
                            break;
                        case NITWIT:
                            is.setType(Material.CARROT_STICK);
                            break;
                    }
                    ItemMeta im = is.getItemMeta();
                    im.setDisplayName(Utils.f("&6&lJob: &b&l" + StringUtils.getCapitalized(p.toString().toLowerCase())));
                    is.setItemMeta(im);
                    e.setItem(DEFAULT_PHONE_OPEN_SLOTS[counter], is);
                    counter++;
                }
                return;
            }
            case "cheatcodes": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 14, 15, 20, 21, 23, 24, 29, 30, 32, 33, 39, 40, 41};
                for (int i = 0; i < CheatCode.values().length; i++) {//will only work with a max of 9 codes
                    int slot = slots[i];
                    CheatCode code = CheatCode.values()[i];
                    State state = Vice.getUserManager().getLoadedUser(uuid).getCheatCodeState(code).getState();
                    e.setItem(slot, code.getDisplayItem(Core.getUserManager().getLoadedUser(uuid), state));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the contacts page!"));
                e.setItem(51, Utils.createItem(Material.BOOK, "&2&lActivating Cheatcodes", "&7You can activate cheatcodes by clicking the item in this menu,", "&7by using &a&l/<cheatcode>&7, and &a&l/cheatcode <cheatcode>&7!"));
                return;
            }
            case "phone":
                this.setPhoneDefaults(e);
                e.setItem(11,
                        Utils.createItem(Material.ENDER_CHEST, "&e&lCosmetics", "&7Stand out from the crowd!"));
                e.setItem(15, Utils.createItem(Material.MINECART, "&c&lVehicles", "&7Ride in style!"));
                e.setItem(29, Utils.createItem(Material.EMPTY_MAP, "&a&lRanks", "&7Working my way to the top!"));
                e.setItem(31, Utils.createItem(Material.NETHER_STAR, "&d&lMy Account", "&7Stats, Ranks and Prefs!"));
                e.setItem(33, Utils.createItem(Material.CHEST, "&b&lKits", "&7Gear up!"));
                e.setItem(47, Utils.createItem(Material.BOOK, "&6&lContacts", "&7Call your associates!"));
                e.setItem(49, Utils.createItem(Material.EMERALD, "&a&lStore", "&7Support the server!"));
                e.setItem(51, Utils.createItem(Material.EXP_BOTTLE, "&a&lRewards", "&7Voting, daily and donor bonuses!"));
                return;
            case "cosmetics":
            case "rewards":
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                return;
            case "vote":
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the rewards page!"));
                return;
            case "account":
                this.setPhoneDefaults(e);
                e.setItem(22, Utils.createItem(Material.NAME_TAG, "&6&lUnlocked Tags", "&7Change your name prefix!"));
                e.setItem(11, Utils.createItem(Material.BOOK, "&d&lStats", "&7You got skills!"));
                e.setItem(15, Utils.createItem(Material.REDSTONE_COMPARATOR, "&5&lPreferences", "&7Toggle your shit!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.NETHER_STAR, "&d&lMy Account", "&7Take care of your biz!"));
                return;
            case "prefs":
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the account page!"));
                return;
            case "ranks": {
                this.setPhoneDefaults(e);
                int[] viceRankSlots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
                int[] donorSlots = new int[]{38, 39, 40, 41, 42};
                int i = 0;
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                for (ViceRank rank : ViceRank.values()) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    if (rank.getPrice() > 0) {
                        lore.add("&7Price: &a$&l" + rank.getPrice());
                        lore.add("");
                    }
                    lore.add("&aPerks:");
                    lore.add("");
                    lore.add("&bKit " + rank.getColoredNameBold() + "&7!");
                    switch (rank) {
                        case JUNKIE:
                            lore.add("&7Drivers License! (&a&lCars&7)");
                            lore.add("&a&l1x&7 /home set!");
                            break;
                        case FALCON:
                            lore.add("&7Team up! (Join a &a&lCartel&7)");
                            lore.add("&7Unlock &e&lMarksman Pistol&7!");
                            lore.add("&7Auction &a&l1&7 item! (&aComing soon&7!)");
                            lore.add("&7Call a &6&lCab&7! (&a/tpa&7)");
                            break;
                        case THUG:
                            lore.add("&7Unlock &e&lHeavy Shotgun&7!");
                            lore.add("&7Auction &a&l2&7 items! (&aComing soon&7!)");
                            lore.add("&7Craft items on the go! (&a/workbench&7)");
                            break;
                        case DEALER:
                            lore.add("&7Apply for &3&lCop&7!");
                            lore.add("&7Unlock &e&lChainsaw&7!");
                            lore.add("&7Unlock &e&lGusenberg Sweeper&7!");
                            break;
                        case GROWER:
                            lore.add("&7Create your own &e&lCartel&7!");
                            lore.add("&a&l2x&7 /home set!");
                            lore.add("&7Unlock &e&lRPG&7!");
                            break;
                        case SMUGGLER:
                            lore.add("&7Unlock &e&lHeavy Sniper&7!");
                            break;
                        case CHEMIST:
                            lore.add("&7Unlock &e&lSpecial Carbine&7!");
                            lore.add("&7Auction &a&l3&7 items! (&aComing soon&7!)");
                            break;
                        case DRUGLORD:
                            lore.add("&7Unlock &e&lGrenade Launcher&7!");
                            lore.add("&7Auction &a&l4&7 items! (&aComing soon&7!)");
                            lore.add("&7Up, up and away! (&4&lJetpack&7)");
                            break;
                        case KINGPIN:
                            lore.add("&7Unlock &e&lCombat MG&7!");
                            lore.add("&a&l3x&7 /home set!");
                            break;
                        case GODFATHER:
                            lore.add("&7Unlock &e&lHoming Launcher&7!");
                            lore.add("&7Unlock &e&lMinigun&7!");
                            lore.add("&7Auction &a&l5&7 items! (&aComing soon&7!)");
                            lore.add("&7Enderchest anywhere! (&a/echest&7)");
                            break;
                        default:
                            lore.add("&7Rank up for cool perks!");
                    }
                    e.setItem(viceRankSlots[i],
                            Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), Utils.f(lore)));
                    i++;
                }
                i = 0;
                for (UserRank rank : new UserRank[]{UserRank.VIP, UserRank.PREMIUM, UserRank.ELITE, UserRank.SPONSOR, UserRank.SUPREME}) {
                    List<String> lore = new ArrayList<>();
                    // TODO
                    lore.add("");
                    lore.add("&7Price: &a$&l" + rank.getPrice());
                    lore.add("");
                    lore.add("&aPerks:");
                    lore.add("");
                    lore.add("&a+ &e&l" + rank.getMonthlyTokens() + " Tokens &a&lmonthly&7!");
                    lore.add("&bKit " + rank.getColoredNameBold() + "&7!");
                    lore.add("&a&l" + ViceUtils.getBackpackRows(rank) + "&6&l Backpack &7rows!");
                    lore.add("&a+ $&l" + ViceUtils.getStartingMoney(rank) + "&7 in-game money!");
                    lore.add("&a+ " + ViceUtils.getWarpDelay(rank) + "&7s delay &6&lTaxi Service&7!");
                    lore.add("&a+ " + ViceUtils.getSetHomes(rank) + "x&7 /home set!");
                    lore.add("&a+ &l"+ Math.round(ViceUtils.getDrugSellModifier(rank) *100) + "&a%&7 sell multiplier!");
                    if (rank.isHigherThan(UserRank.VIP)) {
                        lore.add("&a+ Instantly&7 teleport anywhere from spawn!");
                        lore.add("&a+ &7Join &c&lFull&7 servers!");
                    }
                    if (rank.isHigherThan(UserRank.PREMIUM)) {
                        lore.add("&a+ &7Pick up your friend! (&a&l/tpahere&7)");
                        lore.add("&a+ &7Apply for &3&lCop&7!");
                    }
                    if (rank.isHigherThan(UserRank.ELITE)) {
                        lore.add("&a+ &7Quick sell Cheatcode");
                        lore.add("&a+ &7Satisfy yourself! (&a&l/feed&7)");
                        lore.add("&a+ &7Up, up and away! (&4&lJetpack&7)");
                    }
                    for (LockedWeapon w : LockedWeapon.values()) {
                        if (w.getUserRank() == rank || rank.isHigherThan(w.getUserRank())) {
                            GameItem g = Vice.getItemManager().getItemFromWeapon(w.toString());
                            if (g != null)
                                lore.add("&a+ &7Unlock " + g.getDisplayName() + "&7 instantly!");
                        }
                    }
                    e.setItem(donorSlots[i], Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore));
                    i++;

                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to my account!"));
                ViceRank next = user.getRank().getNext();
                if (next != null)
                    e.setItem(31, Utils.createItem(Material.PAPER, "&a&lRankup to " + next.getColoredNameBold() + "&a&l!",
                            "&7Price: &" + (user.hasMoney(next.getPrice()) ? "a" : "c") + "$&l" + next.getPrice()));
                return;
            }
            case "vicestats": {
                this.setPhoneDefaults(e);
                ViceUser u = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(11, Utils.createItem(Material.PAPER, "&a&lMoney: &f" + Utils.round(u.getMoney())));
                e.setItem(15, Utils.createItem(Material.EMPTY_MAP, "&3&lBonds: &f" + u.getBonds()));
                e.setItem(29, Utils.createItem(Material.IRON_SWORD, "&e&lKills: &f" + u.getKills()));
                e.setItem(31, Utils.createItem(Material.SKULL_ITEM, "&c&lDeaths: &f" + u.getDeaths()));
                e.setItem(33, Utils.createItem(Material.IRON_SWORD, "&a&lK/D Ratio: &f" + u.getKDR()));
                e.setItem(49, Utils.createItem(Material.BOOK_AND_QUILL, "&6&lKillstreak: &f" + u.getKillStreak()));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to my account!"));
                return;
            }

            case "kits": {
                this.setPhoneDefaults(e);
                ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                int[] grSlots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};
                int[] otherSlots = new int[]{29, 30, 31, 32, 33};
                int[] urSlots = new int[]{38, 39, 40, 41, 42};
                int i = 0;
                ViceRank viceRank = viceUser.getRank();
                for (ViceRank rank : ViceRank.values()) {
                    Kit kit = Vice.getItemManager().getKit(rank.getName().toLowerCase());
                    if (kit == null) {
                        continue;
                    }
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    if (rank != viceRank)
                        lore.add("&cRequires " + rank.getName());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(viceUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInSecondsToText(kit.getDelay())
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                viceUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));
                    lore.add("");
                    lore.addAll(kit.getContents().stream().map(KitItem::getDescription).collect(Collectors.toList()));
                    if (kit.getHelmet() != null)
                        lore.add("&7Helmet: " + kit.getHelmet().getDescription());
                    if (kit.getChestPlate() != null)
                        lore.add("&7Chestplate: " + kit.getChestPlate().getDescription());
                    if (kit.getLeggings() != null)
                        lore.add("&7Leggings: " + kit.getLeggings().getDescription());
                    if (kit.getBoots() != null)
                        lore.add("&7Boots: " + kit.getBoots().getDescription());
                    if (kit.getOffHand() != null)
                        lore.add("&7Offhand: " + kit.getOffHand().getDescription());
                    ItemStack item = Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore);
                    e.setItem(grSlots[i], item);
                    i++;
                }
                i = 0;
                for (UserRank rank : UserRank.getDonorRanks()) {
                    Kit kit = Vice.getItemManager().getKit(rank.getName().toLowerCase());
                    if (kit == null) {
                        i++;
                        continue;
                    }
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    if (!(rank == user.getUserRank()
                            || (rank == UserRank.SUPREME && user.getUserRank().isHigherThan(UserRank.SUPREME))))
                        lore.add("&cRequires " + rank.getColoredNameBold());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(viceUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInSecondsToText(kit.getDelay())
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                viceUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));
                    lore.add("");
                    lore.addAll(kit.getContents().stream().map(KitItem::getDescription).collect(Collectors.toList()));
                    if (kit.getHelmet() != null)
                        lore.add("&7Helmet: " + kit.getHelmet().getDescription());
                    if (kit.getChestPlate() != null)
                        lore.add("&7Chestplate: " + kit.getChestPlate().getDescription());
                    if (kit.getLeggings() != null)
                        lore.add("&7Leggings: " + kit.getLeggings().getDescription());
                    if (kit.getBoots() != null)
                        lore.add("&7Boots: " + kit.getBoots().getDescription());
                    if (kit.getOffHand() != null)
                        lore.add("&7Offhand: " + kit.getOffHand().getDescription());
                    ItemStack item = Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore);
                    e.setItem(urSlots[i], item);
                    i++;
                }
                i = 0;
                for (Kit kit : Vice.getItemManager().getKits()) {
                    if (net.grandtheftmc.vice.users.ViceRank.getRankOrNull(kit.getName()) != null || UserRank.getUserRankOrNull(kit.getName()) != null
                            || CopRank.getRankOrNull(kit.getName()) != null || i>=5)
                        continue;
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    if (kit.getPermission() != null && !player.hasPermission(kit.getPermission()))
                        lore.add("&cRequires permission " + kit.getPermission());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(viceUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInMillisToText((long) kit.getDelay() * 1000)
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                viceUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));
                    lore.add("");
                    lore.addAll(kit.getContents().stream().map(KitItem::getDescription).collect(Collectors.toList()));

                    ItemStack item = Utils.createItem(kit.getMaterial(), "&b&l" + kit.getName(), lore);
                    e.setItem(otherSlots[i], item);
                    i++;
                    if (i > otherSlots.length)
                        break;
                }
                return;

            }
            case "contacts": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(11, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service", "&7Click to select a destination!"));
                e.setItem(13, Utils.createItem(Material.SKULL_ITEM, 2, user.isCop() ? "&3&lBackup" : "&3&lPolice", user.isCop() ? "&7Click to request help from fellow officers!" : "&7Click to call the cops to your location!"));
                e.setItem(29, Utils.createItem(Material.ANVIL, "&2&lCheat Codes", "&7Become a cheater and rule the game!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.WATCH, "&c&l911 Emergency", "&7Click to teleport out of here!"));
                return;
            }
            case "taxi":
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.SKULL_ITEM, 3, "&e&lPlayer", "&7Click to select a player!"));
                e.setItem(13, Utils.createItem(Material.ENDER_PEARL, "&e&lWarp", "&7Click to select a warp!"));
                e.setItem(15, Utils.createItem(Material.IRON_DOOR, "&3&lHouse", "&7Click to select a house!"));
                e.setItem(29, Utils.createItem(Material.ANVIL, "&2&lCheat Codes", "&7Become a cheater and rule the game!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the contacts page!"));
                e.setItem(49, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service",
                        "&7Click a button to select your destination!"));
                return;
            case "taxiplayers": {
                this.setPhoneDefaults(e);
                List<Player> players = Bukkit.getOnlinePlayers().stream().filter(bp -> !Objects.equals(player.getUniqueId(), bp.getUniqueId())).collect(Collectors.toList());
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<Player> it = players.iterator();
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    Player p = it.next();
                    e.setItem(
                            slots[i], Utils
                                    .setSkullOwner(
                                            Utils.createItem(Material.SKULL_ITEM, 3,
                                                    "&e&l" + p.getName(), u.isPremium()
                                                            ? "&7Click to send teleport request!" : "&cRequires PREMIUM!"),
                                            p.getName()));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                e.setItem(49, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: Players", "&7Page 1"));
                if (players.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                return;
            }

            case "taxiwarps": {
                this.setPhoneDefaults(e);
                User u = Core.getUserManager().getLoadedUser(uuid);
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                List<Warp> warps = Vice.getWorldManager().getWarpManager().getWarps();
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<Warp> it = warps.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    Warp warp = it.next();
                    e.setItem(slots[i], Utils.createItem(Material.ENDER_PEARL, "&e&l" + warp.getName(),
                            u.isRank(UserRank.ELITE) ? "&7Click to teleport!" : "&7Click to teleport for &a$&l200&7!"));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                e.setItem(49, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: Warps", "&7Page 1"));
                if (warps.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                return;
            }

            case "ammopouch": {
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                int i = 0;
                for (AmmoType type : AmmoType.getTypes()) {
                    if (type.isInInventory())
                        continue;
                    ItemStack item = type.getGameItem().getItem();
                    int a = user.getAmmo(type);
                    ItemMeta meta = item.getItemMeta();
                    meta.setLore(Collections.singletonList(Utils.f("&7Amount: &a&l" + a)));
                    item.setItemMeta(meta);
                    if (a > 1) item.setAmount(a >= 127 ? 127 : a);
                    e.setItem(i, item);
                    e.setItem(i + 9, Utils.createItem(Material.REDSTONE, "&c&lDrop " + 50, 50));
                    e.setItem(i + 18, Utils.createItem(Material.REDSTONE, "&c&lDrop " + 10, 10));
                    e.setItem(i + 27, Utils.createItem(Material.REDSTONE, "&c&lDrop " + 1, 1));
                    i++;
                }
                ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
                for (int n : new int[]{8, 17, 26, 35})
                    e.setItem(n, grayGlass);
                return;

            }
            case "jail": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                List<Player> jailedPlayers = ViceUtils.getJailedPlayers();
                Iterator<Player> it = jailedPlayers.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    Player p = it.next();
                    ViceUser u = Vice.getUserManager().getLoadedUser(p.getUniqueId());
                    if (!u.isArrested())
                        continue;
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Time Left: &a&l" + Utils.timeInSecondsToText(u.getJailTimer()));
                    if (user.isCop())
                        lore.add("&7Click to release!");
                    e.setItem(slots[i], Utils.setSkullOwner(
                            Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + p.getName(), lore), p.getName()));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, Utils.createItem(Material.IRON_FENCE, "&c&lPrisoner List", "&7Page 1"));
                if (jailedPlayers.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&c&lNext Page", "&7Page 2"));
                return;
            }
            case "vehicles": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                List<PersonalVehicle> vehicles = new ArrayList<>(user.getVehicles());
                if (user.hasPersonalVehicle())
                    vehicles.remove(user.getPersonalVehicle());
                Iterator<PersonalVehicle> it = vehicles.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    PersonalVehicle vehicle = it.next();
                    ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                    ItemMeta meta = stack.getItemMeta();
                    meta.spigot().setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                    List<String> lore = new ArrayList<>(meta.getLore());
                    lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                    lore.add(Utils.f("&aClick to get this vehicle!"));
                    lore.add(Utils.f("&7Price: &a$&l200"));
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                    e.setItem(slots[i], stack);
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                if (user.hasPersonalVehicle()) {
                    PersonalVehicle vehicle = user.getPersonalVehicle();
                    ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                    ItemMeta meta = stack.getItemMeta();
                    List<String> lore = new ArrayList<>(meta.getLore());
                    lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                    lore.add(Utils.f("&aClick to view your personal vehicle!"));
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                    e.setItem(49, Utils.addGlow(stack));
                } else {
                    e.setItem(49, Utils.createItem(Material.MINECART, "&4&lVehicles", "&7Please select your personal vehicle!"));
                }
                if (vehicles.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&4&lNext Page", "&7Page 2"));
                return;
            }
            case "vehicleshop": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                Optional<VehicleProperties> opt = Vice.getWastedVehicles().getVehicle(user.getActionVehicle());
                GameItem item = Vice.getItemManager().getItemFromVehicle(user.getActionVehicle());
                if (opt == null || !opt.isPresent() || item == null) {
                    player.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                    return;
                }
                if (item.getSellPrice() <= 0) {
                    player.sendMessage(Lang.VEHICLES.f("&7You can't buy this vehicle!"));
                    player.closeInventory();
                    return;
                }
                VehicleProperties vehicle = opt.get();
                String buyPrice = NumberFormat.getNumberInstance(Locale.US).format(Utils.round(item.getSellPrice() * 2));
                String sellPrice = NumberFormat.getNumberInstance(Locale.US).format(Utils.round(item.getSellPrice()));
                e.setItem(11, Utils.createItem(Material.MINECART, "&4&lSpeed: &a&l" + vehicle.getMaxSpeed()));
                e.setItem(13, Utils.createItem(Material.PAPER, "&4&lPrice: &a$&l" + buyPrice));
                if (vehicle.getWastedGunsWeapon() != null) {
                    Optional<Weapon<?>> o = Vice.getWastedGuns().getWeaponManager().getWeapon(vehicle.getWastedGunsWeapon());
                    o.ifPresent(weapon -> e.setItem(29, weapon.createItemStack()));
                }
                if (!vehicle.getAllowedWeapons().isEmpty()) {
                    List<String> lore = vehicle.getAllowedWeapons().stream().map(s -> Vice.getItemManager().getItemFromWeapon(s)).filter(Objects::nonNull).map(GameItem::getDisplayName).collect(Collectors.toList());
                    e.setItem(33, Utils.createItem(Material.WOOD_SWORD, "&4&lAllowed Weapons", lore));
                }
                if (user.hasVehicle(vehicle.getIdentifier())) {
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lSell Vehicle", "&7Reward: &a$&l" + sellPrice));
                } else
                    e.setItem(31, Utils.createItem(Material.SLIME_BALL, "&a&lBuy Vehicle", "&7Price: &a$&l" + buyPrice));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, vehicle.getItem());
                return;
            }
            case "buyvehicle": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                Optional<VehicleProperties> opt = Vice.getWastedVehicles().getVehicle(user.getActionVehicle());
                GameItem item = Vice.getItemManager().getItemFromVehicle(user.getActionVehicle());
                if (opt == null || !opt.isPresent() || item == null) {
                    player.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                    return;
                }
                VehicleProperties vehicle = opt.get();
                if (item.getSellPrice() <= 0) {
                    player.sendMessage(Lang.VEHICLES.f("&7You can't buy this vehicle!"));
                    player.closeInventory();
                    return;
                }
                if (user.hasVehicle(vehicle.getIdentifier())) {
                    player.sendMessage(Lang.VEHICLES.f("&7You already own this vehicle!"));
                    MenuManager.openMenu(player, "vehicles");
                    return;
                }
                this.setConfirmDefaults(e, "&a&lBuy " + vehicle.getItem().getItemMeta().getDisplayName() + "&a&l for &a$&l" + Utils.round(item.getSellPrice() * 2), "&c&lCancel");
                return;
            }
            case "sellvehicle": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                PersonalVehicle vehicle = user.getPersonalVehicle(user.getActionVehicle());
                if (vehicle == null) return;
                double price = vehicle.getSellPrice();
                if (price <= 0) {
                    player.sendMessage(Lang.VEHICLES.f("&7You can't sell this vehicle!"));
                    player.closeInventory();
                    return;
                }
                this.setConfirmDefaults(e, "&a&lSell " + vehicle.getDisplayName() + "&a&l for &a$&l" + Utils.round(price), "&c&lCancel");
                return;
            }
            case "repairvehicle": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                if (user.getActionVehicle() == null) return;
                PersonalVehicle vehicle = user.getPersonalVehicle(user.getActionVehicle());
                if (vehicle == null) return;
                double price = vehicle.getRepairPrice();
                if (price <= 0) {
                    player.sendMessage(Lang.VEHICLES.f("&7You can't repair this vehicle!"));
                    player.closeInventory();
                    return;
                }
                this.setConfirmDefaults(e, "&a&lRepair " + vehicle.getDisplayName() + "&a&l for &a$&l" + Utils.round(price), "&c&lCancel");
                return;
            }
            case "personalvehicle": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                PersonalVehicle vehicle = user.getPersonalVehicle();
                if (vehicle == null) {
                    MenuManager.openMenu(player, "vehicles");
                    return;
                }
                VehicleProperties vehicleProperties = vehicle.getVehicleProperties();
                if (vehicleProperties == null) return;
                e.setItem(11, Utils.createItem(Material.MINECART, "&4&lStats", "&7Speed: &a&l" + vehicleProperties.getMaxSpeed(), "&7Health: " + vehicle.getFormattedHealth()));
                e.setItem(13, Utils.createItem(Material.PAPER, "&4&lPrice: &a$&l" + Utils.round(vehicle.getPrice())));
                if (vehicle.getRepairPrice() > 0 && !vehicle.onMap())
                    e.setItem(15, Utils.createItem(Material.WORKBENCH, "&4&lRepair", "&7Call the mechanic!", "&7Price: &a$&l" + Utils.round(vehicle.getRepairPrice())));
                else if (vehicle.onMap())
                    e.setItem(15, Utils.createItem(Material.ENDER_PEARL, "&4&lSend Away", "&7Click to send away your vehicle!", "&7Price: &a$&l200"));
                else
                    e.setItem(15, Utils.createItem(Material.ENDER_PEARL, "&4&lCall Vehicle", "&7Click to send your vehicle to yourself!", "&7Price: &a$&l200"));
                if (vehicleProperties.getWastedGunsWeapon() != null) {
                    Optional<Weapon<?>> o = Vice.getWastedGuns().getWeaponManager().getWeapon(vehicleProperties.getWastedGunsWeapon());
                    o.ifPresent(weapon -> e.setItem(29, weapon.createItemStack()));
                }
                if (!vehicleProperties.getAllowedWeapons().isEmpty()) {
                    List<String> lore = vehicleProperties.getAllowedWeapons().stream().map(s -> Vice.getItemManager().getItemFromWeapon(s)).filter(Objects::nonNull).map(GameItem::getDisplayName).collect(Collectors.toList());
                    e.setItem(33, Utils.createItem(Material.WOOD_SWORD, "&4&lAllowed Weapons", lore));
                }
                e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lSell Vehicle", "&7Reward: &a$&l" + Utils.round(vehicle.getSellPrice())));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the vehicles page!"));
                ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                ItemMeta meta = stack.getItemMeta();
                meta.spigot().setUnbreakable(true);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
                List<String> lore = new ArrayList<>(meta.getLore());
                lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                lore.add(Utils.f("&aClick to send your vehicle to yourself!"));
                lore.add(Utils.f("&7Price: &a$&l200"));
                stack.setItemMeta(meta);
                e.setItem(49, Utils.addGlow(stack));
                break;
            }
            case "mechanic": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                List<PersonalVehicle> vehicles = new ArrayList<>(user.getVehicles());
                new ArrayList<>(vehicles).stream().filter(vehicle -> vehicle.getRepairPrice() <= 0).forEach(vehicles::remove);
                Iterator<PersonalVehicle> it = vehicles.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    PersonalVehicle vehicle = it.next();
                    if (!vehicle.isDestroyed())
                        continue;
                    ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                    ItemMeta meta = stack.getItemMeta();
                    meta.spigot().setUnbreakable(true);
                    meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES);
                    List<String> lore = new ArrayList<>(meta.getLore());
                    lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                    lore.add(Utils.f("&7Repair Price: &a&l$" + NumberFormat.getNumberInstance(Locale.US).format(vehicle.getRepairPrice())));
                    lore.add(Utils.f("&7Click to repair this vehicle!"));
                    meta.setLore(lore);
                    stack.setItemMeta(meta);
                    e.setItem(slots[i], Objects.equals(vehicle, user.getPersonalVehicle()) ? Utils.addGlow(stack) : stack);
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, Utils.createItem(Material.WORKBENCH, "&4&lMechanic", "&7Repair your vehicles!"));
                if (vehicles.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&4&lNext Page", "&7Page 2"));
                return;
            }

            case "armorupgrade": {
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                ArmorUpgrade upgrade = user.getBuyingArmorUpgrade();
                if (upgrade == null) {
                    player.closeInventory();
                    return;
                }
                if (!upgrade.canUseUpgrade(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
                    player.closeInventory();
                    player.sendMessage(Lang.HEY.f("&7You need to rank up to " + upgrade.getViceRank().getColoredNameBold() + "&7 or donate for " + upgrade.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    return;
                }
                ItemStack item = player.getInventory().getItemInMainHand();
                GameItem gameItem = item == null ? null : Vice.getItemManager().getItem(item.getType());
                if (item == null || gameItem == null || !upgrade.canBeUsedOn(gameItem.getName())) {
                    player.closeInventory();
                    player.sendMessage(Lang.HEY.f("&7The &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7 can only be applied to the following types of items: " + upgrade.getTypesString() + "&7!"));
                    return;
                }
                HashSet<ArmorUpgrade> upgradesOnItem = ArmorUpgrade.getArmorUpgrades(item);
                if (upgradesOnItem.contains(upgrade)) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7That piece of armor already has the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    return;
                }
                if ((upgradesOnItem.contains(ArmorUpgrade.LIGHT) && upgrade == ArmorUpgrade.ULTRA_LIGHT) || (upgradesOnItem.contains(ArmorUpgrade.ULTRA_LIGHT) && upgrade == ArmorUpgrade.LIGHT)) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7This upgrade cannot be added to the armor piece due to conflicting upgrades."));
                    return;
                }
                double price = upgrade.getPrice();

                if (!user.hasMoney(upgrade.getPrice())) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7You can't afford the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    return;
                }
                this.setConfirmDefaults(e, "&a&lBuy &b&l" + upgrade.getDisplayName() + " Armor Upgrade", "&c&lCancel",
                        Collections.singletonList("&7Price: &a$&l" + price), Collections.singletonList("&7Item: &a&l" + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name())));
                return;
            }
            case "lottery": {
                this.setPhoneDefaults(e);
                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                LotteryPlayer p = Vice.getLottery().getLotteryPlayer(player.getUniqueId());
                int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32, 39, 40};
                int[] amnts = new int[]{1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000};
                for (int i = 0; i < 11; i++)
                    e.setItem(slots[i], Utils.createItem(Material.EMPTY_MAP,
                            (user.hasMoney(amnts[i] * 500) ? "&e" : "&c") + "&l" + amnts[i] + " Tickets", "&7Price: &a$&l" + (amnts[i] * 500), "&7Click to buy tickets!"));
                e.setItem(41,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&e&lCustom Amount", "&7Click to choose an amount!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, Utils.createItem(Material.GOLD_INGOT, "&e&lLottery", "&7Your tickets: &e&l" + (p == null ? 0 : p.getTickets()), "&7Go big or go home!"));
                LotteryPlayer winner1 = Vice.getLottery().getWinner(0);
                LotteryPlayer winner2 = Vice.getLottery().getWinner(1);
                LotteryPlayer winner3 = Vice.getLottery().getWinner(2);
                e.setItem(51, Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3,
                        "&e&lLast week's winners",
                        winner1 == null ? "" : "&a#&l1&7: &r" + winner1.getName() + " &a" + Utils.formatMoney(winner1.getAmount()) + "&7 (&a70%&7 of the pot)",
                        winner2 == null ? "" : "&a#&l2&7: &r" + winner2.getName() + " &a" + Utils.formatMoney(winner2.getAmount()) + "&7 (&a20%&7 of the pot",
                        winner3 == null ? "" : "&a#&l2&7: &r" + winner3.getName() + " &a" + Utils.formatMoney(winner3.getAmount()) + "&7 (&a10%&7 of the pot)"), winner1 == null ? "Presidentx" : winner1.getName()));
                return;
            }
            default:
                break;
        }


    }

    public void setPhoneDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
    }

    public void setPhoneDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) inv.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) inv.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            inv.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e) {
        this.setConfirmDefaults(e, "&a&lConfirm", "&c&lCancel");
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage, List<String> confirmLore, List<String> cancelLore) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage, confirmLore == null ? new ArrayList<>() : confirmLore);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage, cancelLore == null ? new ArrayList<>() : cancelLore);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onMenuClick(MenuClickEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
        ItemStack item = e.getItem();
        User coreUser = Core.getUserManager().getLoadedUser(uuid);
        Inventory inv = e.getInv();
        if (item == null || item.getType() == Material.AIR)
            return;
        switch (menu.getName()) {
            case "choose_villager_type": {
                if(user.getChangingJob()==null) {
                    player.sendMessage(Lang.CHEAT_CODES.f("&7Cannot open this menu because you do not have a villager selected!"));
                    return;
                }
                if(item.getType()==Material.STAINED_GLASS_PANE)
                    return;
                Villager.Profession p = Villager.Profession.valueOf(ChatColor.stripColor(item.getItemMeta().getDisplayName()).split(" ")[1].toUpperCase());
                Villager v = user.getChangingJob();
                v.setProfession(p);
                player.closeInventory();
                player.sendMessage(Utils.f("&7You have changed the profession of the villager to a &e" + p.toString()));
                user.setChangingJob(null);
                coreUser.addCooldown("villager_job_cc", 60*60, false, true);
                return;
            }
            case "cheatcodes": {
                if(item.getType()==Material.REDSTONE) {
                    MenuManager.openMenu(player, "contacts");
                    return;
                }
                Optional<CheatCode> optCode = CheatCode.getCheatCodeFromItemStack(item);
                if(!optCode.isPresent())
                    return;
                CheatCodeState cState = user.getCheatCodeState(optCode.get());
                if(cState.getState()==State.LOCKED) {
                    player.sendMessage(Lang.CHEAT_CODES.f(optCode.get().getLockedLore()));
                    return;
                }
                optCode.get().activate(coreUser, user, player, cState);
                MenuManager.openMenu(player, "cheatcodes");//refresh
                return;
            }
            case "phone":
                switch (item.getType()) {
                    case ENDER_CHEST:
                        MenuManager.openMenu(player, "cosmetics");
                        return;
                    case MINECART:
                        if (Vice.getUserManager().getLoadedUser(uuid).hasPersonalVehicle()) {
                            MenuManager.openMenu(player, "personalvehicle");
                            return;
                        }
                        MenuManager.openMenu(player, "vehicles");
                        return;
                    case NETHER_STAR:
                        MenuManager.openMenu(player, "account");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "contacts");
                        return;
                    case EMERALD:
                        player.closeInventory();
                        player.sendMessage(Lang.VICE.f("&7Go to &a&lstore.grandtheftmc.net&7 to buy Ranks, Bonds, Money and other packages!"));
                        return;
                    case EXP_BOTTLE:
                        MenuManager.openMenu(player, "rewards");
                        return;
                    case CHEST:
                        MenuManager.openMenu(player, "kits");
                        return;
                    case EMPTY_MAP:
                        MenuManager.openMenu(player, "ranks");
                        return;
                    case ANVIL:
                        MenuManager.openMenu(player, "cheatcodes");
                    default:
                        return;
                }
            case "cosmetics":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        return;
                }
            case "rewards":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        return;
                }
            case "vote":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "reward");
                        return;
                    default:
                        return;
                }
            case "account":
                switch (item.getType()) {
                    case NAME_TAG:
                        MenuManager.openMenu(player, "chooseeventtag");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "vicestats");
                        return;
                    case REDSTONE_COMPARATOR:
                        MenuManager.openMenu(player, "prefs");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                    default:
                        return;
                }
            case "ranks":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "account");
                        return;
                    case PAPER:
                        User u = Core.getUserManager().getLoadedUser(uuid);
                        user.rankup(player, u);
                        return;
                    default:
                        return;
                }
            case "vicestats":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "account");
                        return;
                    default:
                        return;
                }

            case "kits":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                && item.getType() != Material.STAINED_GLASS_PANE) {
                            Vice.getItemManager().giveKit(player, Core.getUserManager().getLoadedUser(uuid),
                                    Vice.getUserManager().getLoadedUser(uuid),
                                    ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                            MenuManager.updateMenu(player, "kits");
                        }
                        return;
                }
            case "contacts":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case ANVIL:
                        MenuManager.openMenu(player, "cheatcodes");
                        return;
                    case STORAGE_MINECART:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    case SKULL_ITEM:
                        if (user.isCop())
                            player.sendMessage(Lang.COP_MODE.f("&7You have called for backup! All officers have been notified, and they can teleport to you for 1 minute!"));
                        else {
                            player.sendMessage(Lang.VICE.f("&7You have called the police! All officers have been notified, and they can teleport to you for 1 minute!"));
                        }
                        user.setLastBackupRequest(System.currentTimeMillis());
                        for (ViceUser u : ViceUtils.getCops()) {
                            Player p = Bukkit.getPlayer(u.getUUID());
                            if (!Objects.equals(player, p))
                                p.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.f((user.isCop() ? "&3&lCop " : "&7Citizen") + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p))).append(" is requesting " + (user.isCop() ? "backup" : "police assistance") + "! Teleport: ").color(net.md_5.bungee.api.ChatColor.GRAY).
                                        append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup " + player.getName())).create());

                        }
                        return;
                    case WATCH:
                        Vice.getWorldManager().getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid), Vice.getUserManager().getLoadedUser(uuid), new TaxiTarget(Vice.getWorldManager().getWarpManager().getSpawn()), 0, -1);
                        return;
                    default:
                        return;
                }
            case "taxi":
                switch (e.getItem().getType()) {
                    case SKULL_ITEM:
                        MenuManager.openMenu(player, "taxiplayers");
                        return;
                    case BED:
                        Vice.getWorldManager().getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                Vice.getUserManager().getLoadedUser(uuid), new TaxiTarget(Vice.getWorldManager().getWarpManager().getSpawn()), 0,
                                -1);
                        player.closeInventory();
                        return;
                    case IRON_DOOR:
                        MenuManager.openMenu(player, "taxihouses");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "contacts");
                        return;
                    case ENDER_PEARL:
                        MenuManager.openMenu(player, "taxiwarps");
                        return;
                    default:
                        return;
                }
            case "taxiplayers":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        User u = Core.getUserManager().getLoadedUser(uuid);
                        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
                        players.remove(player);
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41,
                                42};
                        Iterator<? extends Player> it = players.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Player p = it.next();
                            if (i < (page - 1) * 20)
                                continue;

                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(
                                    Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + p.getName(),
                                            u.isPremium() ? "&7Click to send teleport request!" : "&cRequires PREMIUM!"),
                                    p.getName()));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49,
                                Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: Players", "&7Page " + page));
                        if (players.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case SKULL_ITEM:
                        Player target = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        player.closeInventory();
                        Vice.getWorldManager().getWarpManager().tpa(player, Core.getUserManager().getLoadedUser(uuid),
                                Vice.getUserManager().getLoadedUser(uuid), target);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    default:
                        return;
                }

            case "taxiwarps":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                        List<Warp> warps = Vice.getWorldManager().getWarpManager().getWarps();
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41,
                                42};
                        Iterator<Warp> it = warps.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Warp warp = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            inv.setItem(slots[i - (page - 1) * 20], Utils.createItem(Material.ENDER_PEARL,
                                    "&e&l" + warp.getName(), "&7Click to teleport for &a$&l200&7!"));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49,
                                Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: Warps", "&7Page " + page));
                        if (warps.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case ENDER_PEARL:
                        Warp warp = Vice.getWorldManager().getWarpManager().getWarp(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (warp == null) {
                            player.sendMessage(Lang.TAXI.f("&7That warp does not exist!"));
                            return;
                        }
                        player.closeInventory();
                        Vice.getWorldManager().getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                Vice.getUserManager().getLoadedUser(uuid), new TaxiTarget(warp), 200, -1);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    default:
                        return;
                }

            case "ammopouch":
                switch (item.getType()) {
                    case REDSTONE:
                        int slot = e.getSlot();
                        int i = 0;
                        while (slot > 8) {
                            slot -= 9;
                            i++;
                        }
                        int toDrop = i == 1 ? 50 : i == 2 ? 10 : i == 3 ? 1 : 0;
                        AmmoType type = AmmoType.getTypes()[slot];
                        int ammo = user.getAmmo(type);
                        if (ammo <= 0) {
                            player.sendMessage(Lang.AMMO.f("&7You have none of this type of ammo left!"));
                            return;
                        }
                        if (ammo < toDrop)
                            toDrop = ammo;
                        ItemStack stack = type.getGameItem().getItem();
                        stack.setAmount(toDrop);
                        user.removeAmmo(type, toDrop);
                        player.getWorld().dropItemNaturally(Utils.getInFrontOf(player.getLocation()), stack);
                        player.sendMessage(Lang.AMMO_TAKE.f(toDrop + "&7 " + type.getGameItem().getDisplayName() + "&7!"));
                        MenuManager.updateMenu(player, "ammopouch");
                        return;
                    default:
                        return;
                }
            case "jail":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41,
                                42};
                        List<Player> jailedPlayers = ViceUtils.getJailedPlayers();
                        Iterator<Player> it = jailedPlayers.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Player p = it.next();
                            if (i < (page - 1) * 20)
                                continue;

                            ViceUser u = Vice.getUserManager().getLoadedUser(p.getUniqueId());
                            if (!u.isArrested())
                                continue;
                            List<String> lore = new ArrayList<>();
                            lore.add("&7Time Left: &a&l" + Utils.timeInSecondsToText(u.getJailTimer()));
                            if (user.isCop() && Objects.equals(u.getJailCop(), player.getUniqueId()))
                                lore.add("&7Click to release!");
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(
                                    Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + p.getName(), lore), p.getName()));
                        }
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&c&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                        inv.setItem(49, Utils.createItem(Material.IRON_FENCE, "&c&lPrisoner List", "&7Page " + page));
                        if (jailedPlayers.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&c&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case SKULL_ITEM:
                        Player p = Bukkit.getPlayer(ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName()));
                        if (p == null) {
                            player.sendMessage(Lang.JAIL.f("&7That player is not online!"));
                            MenuManager.updateMenu(player, "jail");
                            return;
                        }
                        if (!Vice.getUserManager().getLoadedUser(uuid).isCop()) return;
                        ViceUser u = Vice.getUserManager().getLoadedUser(p.getUniqueId());
                        if (!u.isArrested()) {
                            player.sendMessage(Lang.JAIL.f("&7That player is not in jail!"));
                            MenuManager.updateMenu(player, "jail");
                            return;
                        }
                        if (u.getJailTimer() <= 5) {
                            player.sendMessage(Lang.JAIL.f("&7That prisoners is already being released!"));
                            return;
                        }
                        if (!Vice.getUserManager().getLoadedUser(uuid).isCop()) {
                            player.sendMessage(Lang.JAIL.f("&7You must be a cop to release prisoners!"));
                            return;
                        }
                        if (!Objects.equals(u.getJailCop(), player.getUniqueId())) {
                            player.sendMessage(Lang.JAIL.f("&7You can only release prisoners that you put in jail!"));
                            return;
                        }
                        u.setJailTimer(5);
                        player.sendMessage(Lang.JAIL.f("&7You released prisoners &e&l"
                                + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p) + "&7!"));
                        p.sendMessage(Lang.JAIL.f("&7You are being released by &a"
                                + Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player) + "&7!"));
                        MenuManager.updateMenu(player, "jail");
                        return;
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    default:
                        return;
                }
            case "vehicles":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        List<PersonalVehicle> vehicles = new ArrayList<>(user.getVehicles());
                        if (user.hasPersonalVehicle())
                            vehicles.remove(user.getPersonalVehicle());
                        Iterator<PersonalVehicle> it = vehicles.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            PersonalVehicle vehicle = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                            ItemMeta meta = stack.getItemMeta();
                            List<String> lore = new ArrayList<>(meta.getLore());
                            lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                            lore.add(Utils.f("&aClick to get this vehicle!"));
                            lore.add(Utils.f("&7Price: &a$&l200"));
                            meta.setLore(lore);
                            stack.setItemMeta(meta);
                            inv.setItem(slots[i - (page - 1) * 20], stack);
                        }
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&4&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the property page!"));
                        if (user.hasPersonalVehicle()) {
                            PersonalVehicle vehicle = user.getPersonalVehicle();
                            ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                            ItemMeta meta = stack.getItemMeta();
                            List<String> lore = new ArrayList<>(meta.getLore());
                            lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                            lore.add(Utils.f("&aClick to view your personal vehicle!"));
                            meta.setLore(lore);
                            stack.setItemMeta(meta);
                            inv.setItem(49, Utils.addGlow(stack));

                        } else
                            inv.setItem(49, Utils.createItem(Material.MINECART, "&4&lVehicles", "&7Please select your personal vehicle!"));
                        if (vehicles.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&4&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
                        GameItem gameItem = Vice.getItemManager().getItemFromDisplayName(item.getItemMeta().getDisplayName());
                        if (gameItem == null) return;
                        PersonalVehicle vehicle = user.getPersonalVehicle(gameItem.getWeaponOrVehicleOrDrug());
                        if (vehicle == null) return;
                        if (Objects.equals(vehicle, user.getPersonalVehicle())) {
                            MenuManager.openMenu(player, "personalvehicle");
                            return;
                        }
                        user.setPersonalVehicle(player, Core.getUserManager().getLoadedUser(uuid), vehicle);
                        player.closeInventory();
                        return;
                }

            case "vehicleshop":
                switch (item.getType()) {
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    case SLIME_BALL:
                        if (user.hasVehicle(user.getActionVehicle())) {
                            player.sendMessage(Lang.VEHICLES.f("&7You already own this vehicle!"));
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        MenuManager.openMenu(player, "buyvehicle");
                        return;
                    case INK_SACK:
                        if (!user.hasVehicle(user.getActionVehicle())) {
                            player.sendMessage(Lang.VEHICLES.f("&7You don't own this vehicle!"));
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        MenuManager.openMenu(player, "sellvehicle");
                        return;
                    default:
                        return;
                }
            case "buyvehicle":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                Optional<VehicleProperties> opt = Vice.getWastedVehicles().getVehicle(user.getActionVehicle());
                                GameItem gameItem = Vice.getItemManager().getItemFromVehicle(user.getActionVehicle());
                                if (opt == null || !opt.isPresent() || gameItem == null) {
                                    if (gameItem == null)
                                        Utils.b("GameItem for " + user.getActionVehicle() + " is null");
                                    player.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                                    return;
                                }
                                VehicleProperties vehicle = opt.get();
                                double price = gameItem.getSellPrice() * 2;
                                if (price <= 0) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.VEHICLES.f("&7You can't buy this vehicle!"));
                                    return;
                                }
                                if (user.hasVehicle(vehicle.getIdentifier())) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.VEHICLES.f("&7You already own this vehicle!"));
                                    return;
                                }
                                if (!user.hasMoney(price)) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.VEHICLES.f("&7You don't have the &c$&l" + Utils.round(price) + "&7 to pay for this vehicle!"));
                                    return;
                                }
                                user.setActionVehicle(null);
                                user.takeMoney(price);
                                ViceUtils.updateBoard(player, user);
                                user.giveVehiclePerm(player, vehicle);
                                player.sendMessage(Lang.VEHICLES.f("&7You bought vehicle " + vehicle.getItem().getItemMeta().getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                                MenuManager.openMenu(player, "vehicles");
                                return;
                            case 14:
                                MenuManager.openMenu(player, "vehicleshop");
                                return;
                            default:
                                return;
                        }

                    default:
                        return;
                }
            case "sellvehicle":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                PersonalVehicle vehicle = user.getPersonalVehicle(user.getActionVehicle());
                                if (vehicle == null) return;
                                if (vehicle.onMap()) {
                                    player.sendMessage(Lang.VEHICLES.f("&7Please send the driver to pick up your vehicle first!"));
                                    return;
                                }
                                double price = vehicle.getSellPrice();
                                if (price <= 0) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.VEHICLES.f("&7You can't sell this vehicle!"));
                                    return;
                                }
                                user.setActionVehicle(null);
                                user.removeVehiclePerm(player, vehicle.getVehicleProperties());
                                user.addMoney(price);
                                ViceUtils.updateBoard(player, user);
                                player.sendMessage(Lang.VEHICLES.f("&7You sold vehicle " + vehicle.getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                                MenuManager.openMenu(player, "vehicles");
                                return;
                            case 14:
                                MenuManager.openMenu(player, "personalvehicle");
                                return;
                            default:
                                return;
                        }

                    default:
                        return;
                }
            case "repairvehicle":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                PersonalVehicle vehicle = user.getPersonalVehicle(user.getActionVehicle());
                                if (vehicle == null) return;
                                if (vehicle.onMap()) {
                                    player.sendMessage(Lang.VEHICLES.f("&7Please send the driver to pick up your vehicle first!"));
                                    return;
                                }
                                double price = vehicle.getRepairPrice();
                                if (price <= 0) {
                                    player.closeInventory();
                                    player.sendMessage(Lang.VEHICLES.f("&7You can't repair this vehicle!"));
                                    return;
                                }
                                if (!user.hasMoney(price)) {
                                    player.sendMessage(Lang.VEHICLES.f("&7You can't afford to pay &c$&l" + price + "&7 to repair this vehicle!"));
                                    return;
                                }
                                user.setActionVehicle(null);
                                user.takeMoney(price);
                                ViceUtils.updateBoard(player, user);
                                double health = vehicle.getVehicleProperties().getMaxHealth();
                                vehicle.setHealth(health);
                                vehicle.updateVehicleInDatabase(player, health);
                                player.sendMessage(Lang.VEHICLES.f("&7You repaired vehicle " + vehicle.getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                                MenuManager.openMenu(player, "personalvehicle");
                                return;
                            case 14:
                                MenuManager.openMenu(player, "personalvehicle");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "personalvehicle":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "vehicles");
                        return;
                    case INK_SACK: {
                        PersonalVehicle vehicle = user.getPersonalVehicle();
                        if (vehicle == null) {
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        user.setActionVehicle(vehicle.getVehicle());
                        MenuManager.openMenu(player, "sellvehicle");
                        return;
                    }
                    case ENDER_PEARL: {
                        PersonalVehicle vehicle = user.getPersonalVehicle();
                        if (vehicle == null) {
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        if (vehicle.onMap()) {
                            vehicle.sendAway(player, Core.getUserManager().getLoadedUser(uuid), user);
                        } else {
                            vehicle.call(player, Core.getUserManager().getLoadedUser(uuid), user);
                        }
                        return;
                    }
                    case WORKBENCH: {
                        PersonalVehicle vehicle = user.getPersonalVehicle();
                        if (vehicle == null) {
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        if (vehicle.getRepairPrice() <= 0) {
                            player.sendMessage(Lang.VEHICLES.f("&7You can't repair this vehicle!"));
                            return;
                        }
                        user.setActionVehicle(vehicle.getVehicle());
                        MenuManager.openMenu(player, "repairvehicle");
                        return;
                    }
                    default:
                        PersonalVehicle vehicle = user.getPersonalVehicle();
                        if (vehicle == null) {
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !Objects.equals(item.getItemMeta().getDisplayName(), vehicle.getDisplayName()))
                            return;
                        vehicle.call(player, Core.getUserManager().getLoadedUser(uuid), user);
                        return;
                }
            case "mechanic":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        List<PersonalVehicle> vehicles = new ArrayList<>(user.getVehicles());
                        new ArrayList<>(vehicles).stream().filter(vehicle -> vehicle.getRepairPrice() <= 0).forEach(vehicles::remove);
                        Iterator<PersonalVehicle> it = vehicles.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            PersonalVehicle vehicle = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                            ItemMeta meta = stack.getItemMeta();
                            List<String> lore = new ArrayList<>(meta.getLore());
                            lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                            lore.add(Utils.f("&7Repair Price: &a&l$" + NumberFormat.getNumberInstance(Locale.US).format(vehicle.getRepairPrice())));
                            lore.add(Utils.f("&aClick to repair this vehicle!"));
                            meta.setLore(lore);
                            stack.setItemMeta(meta);
                            inv.setItem(slots[i - (page - 1) * 20], Objects.equals(vehicle, user.getPersonalVehicle()) ? Utils.addGlow(stack) : stack);
                        }
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&4&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the property page!"));
                        inv.setItem(49, Utils.createItem(Material.WORKBENCH, "&4&lMechanic", "&7Repair your vehicles!"));
                        if (vehicles.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&4&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    default:
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
                        GameItem gameItem = Vice.getItemManager().getItemFromDisplayName(item.getItemMeta().getDisplayName());
                        if (gameItem == null) return;
                        PersonalVehicle vehicle = user.getPersonalVehicle(gameItem.getWeaponOrVehicleOrDrug());
                        if (vehicle == null) return;
                        if (vehicle.onMap()) {
                            player.sendMessage(Lang.VEHICLES.f("&7Please send the driver to pick up your vehicle first!"));
                            return;
                        }
                        double price = vehicle.getRepairPrice();
                        if (price <= 0) {
                            player.closeInventory();
                            player.sendMessage(Lang.VEHICLES.f("&7You can't repair this vehicle!"));
                            return;
                        }
                        if (!user.hasMoney(price)) {
                            player.sendMessage(Lang.VEHICLES.f("&7You can't afford to pay &c$&l" + price + "&7 to repair this vehicle!"));
                            return;
                        }
                        user.setActionVehicle(null);
                        user.takeMoney(price);
                        ViceUtils.updateBoard(player, user);
                        double health = vehicle.getVehicleProperties().getMaxHealth();
                        vehicle.setHealth(health);
                        vehicle.updateVehicleInDatabase(player, health);
                        player.sendMessage(Lang.VEHICLES.f("&7The Mechanic repaired vehicle " + vehicle.getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                        player.closeInventory();
                        return;
                }

            case "armorupgrade":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                player.closeInventory();
                                ArmorUpgrade upgrade = user.getBuyingArmorUpgrade();
                                if (upgrade == null) {
                                    return;
                                }
                                if (!upgrade.canUseUpgrade(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
                                    player.sendMessage(Lang.HEY.f("&7You need to rank up to " + upgrade.getViceRank().getColoredNameBold() + "&7 or donate for " + upgrade.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                                    return;
                                }
                                ItemStack i = player.getInventory().getItemInMainHand();
                                GameItem gameItem = item == null ? null : Vice.getItemManager().getItem(i.getType());
                                if (i == null || gameItem == null || !upgrade.canBeUsedOn(gameItem.getName())) {
                                    player.sendMessage(Lang.HEY.f("&7The &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7 can be applied to the following types of items: " + upgrade.getTypesString() + "&7!"));
                                    return;
                                }
                                if (ArmorUpgrade.getArmorUpgrades(item).contains(upgrade)) {
                                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7That piece of armor already has the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                                    return;
                                }
                                double price = upgrade.getPrice();
                                if (!user.hasMoney(upgrade.getPrice())) {
                                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7You can't afford the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                                    return;
                                }
                                Bukkit.getPluginManager().callEvent(new MoneyEvent(player.getUniqueId(), MoneyEvent.MoneyEventType.TAKE, price));
                                player.getInventory().setItemInMainHand(upgrade.getUpgradedItem(gameItem, i));
                                player.sendMessage(Lang.ARMOR_UPGRADE.f("&7You applied the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7 to your " + (i.getItemMeta().hasDisplayName() ? i.getItemMeta().getDisplayName() : i.getType().name()) + "&7 for &a$&l" + price + "&7!"));
                                return;
                            case 14:
                                player.closeInventory();
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "lottery":
                switch (item.getType()) {
                    case EMPTY_MAP:
                        int amnt = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName())
                                .replace(" Tickets", ""));
                        if (!user.hasMoney(amnt * 500)) {
                            player.sendMessage(Lang.BANK.f("&7You don't have &c$&l" + (amnt * 500) + " &con you!"));
                            return;
                        }
                        user.takeMoney(amnt * 500);
                        LotteryPlayer p = Vice.getLottery().getLotteryPlayer(uuid);
                        if (p == null) {
                            p = new LotteryPlayer(uuid, player.getName());
                            Vice.getLottery().addLotteryPlayer(p);
                        }
                        p.addTickets(amnt);
                        ViceUtils.updateBoard(player, user);
                        player.sendMessage(Lang.LOTTERY.f("&7You bought &e&l" + amnt + " Tickets&7 for &a$&l" + (amnt * 500) + "&7!"));
                        player.closeInventory();
                        return;
                    case BOOK_AND_QUILL:
                        user.setBooleanToStorage(BooleanStorageType.BUYING_LOTTERY_TICKETS, true);
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.BANK
                                + "&7Please type (in chat) the amount of tickets you would like to buy for &a$&l500&7 each, or type&a \"quit\"&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bank");
                        return;
                    default:
                        return;
                }
        }
    }
}





