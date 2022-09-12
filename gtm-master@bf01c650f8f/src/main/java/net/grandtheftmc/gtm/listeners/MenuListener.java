package net.grandtheftmc.gtm.listeners;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.j0ach1mmall3.jlib.inventory.JLibItem;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.MoneyEvent;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuClickEvent;
import net.grandtheftmc.core.menus.MenuCloseEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.menus.MenuOpenEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.bounties.Bounty;
import net.grandtheftmc.gtm.bounties.BountyManager;
import net.grandtheftmc.gtm.bounties.BountyPlacer;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.item.DrugDealerItem;
import net.grandtheftmc.gtm.drugs.item.DrugItem;
import net.grandtheftmc.gtm.event.christmas.ChristmasEvent;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.relation.GangRelation;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.Head;
import net.grandtheftmc.gtm.items.Kit;
import net.grandtheftmc.gtm.items.KitItem;
import net.grandtheftmc.gtm.tasks.LotteryPlayer;
import net.grandtheftmc.gtm.users.ChatAction;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.CheatCodeState;
import net.grandtheftmc.gtm.users.CompassTarget;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.LockedWeapon;
import net.grandtheftmc.gtm.users.PersonalVehicle;
import net.grandtheftmc.gtm.users.TaxiTarget;
import net.grandtheftmc.gtm.warps.Warp;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.House;
import net.grandtheftmc.houses.houses.HouseDoor;
import net.grandtheftmc.houses.houses.PremiumHouse;
import net.grandtheftmc.houses.houses.PremiumHouseDoor;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.UserHouse;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.minecraft.server.v1_12_R1.NBTTagCompound;


public class MenuListener implements Listener {
    private final static List<Integer> SLOTS = Arrays.asList(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42);

    @EventHandler(priority = EventPriority.HIGH)
    public void onMenuOpen(MenuOpenEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        switch (menu.getName()) {
            case "transferconfirm": {
                this.setConfirmDefaults(e, "&a&lConfirm Transfer", "&c&lCancel Transfer", "&c- This transfer is irreversible", "&c- All of your items will be deleted off", "&cyour current server and put onto", "&cyour target server.", "&c- Staff are unable to undo this decision.");
                return;
            }
            case "christmasshop": {
                this.setPhoneDefaults(e);
                String p = "&7Price: [&6@&7]";
                e.setItem(2, Utils.setLore(GTM.getItemManager().getItem("santahat").getItem(), p.replace("@", "20")));
                e.setItem(3, Utils.setLore(GTM.getItemManager().getItem("santatunic").getItem(), p.replace("@", "32")));
                e.setItem(5, Utils.setLore(GTM.getItemManager().getItem("santapants").getItem(), p.replace("@", "24")));
                e.setItem(6, Utils.setLore(GTM.getItemManager().getItem("santaboots").getItem(), p.replace("@", "16")));

                e.setItem(11, Utils.setLore(GTM.getItemManager().getItem("elfhat").getItem(), p.replace("@", "5")));
                e.setItem(12, Utils.setLore(GTM.getItemManager().getItem("elftunic").getItem(), p.replace("@", "8")));
                e.setItem(14, Utils.setLore(GTM.getItemManager().getItem("elfpants").getItem(), p.replace("@", "6")));
                e.setItem(15, Utils.setLore(GTM.getItemManager().getItem("elfboots").getItem(), p.replace("@", "4")));

                e.setItem(20, Utils.setLore(GTM.getItemManager().getItem("rudolfhat").getItem(), p.replace("@", "10")));
                e.setItem(21, Utils.setLore(GTM.getItemManager().getItem("rudolftunic").getItem(), p.replace("@", "16")));
                e.setItem(23, Utils.setLore(GTM.getItemManager().getItem("rudolfpants").getItem(), p.replace("@", "12")));
                e.setItem(24, Utils.setLore(GTM.getItemManager().getItem("rudolfboots").getItem(), p.replace("@", "8")));


                e.setItem(4, Utils.setLore(Utils.createItem(Material.NAME_TAG, "&b&lFestive Tag"), p.replace("@", "35")));
                e.setItem(13, Utils.setLore(Utils.createItem(Material.NAME_TAG, "&2&lXMAS Tag"), p.replace("@", "35")));
                e.setItem(22, Utils.setLore(Utils.createItem(Material.NAME_TAG, "&r&lSnowman Tag"), p.replace("@", "50")));
                e.setItem(31, Utils.setLore(Utils.createItem(Material.NAME_TAG, "&c&lSanta Tag"), p.replace("@", "50")));
                e.setItem(40, Utils.setLore(Utils.createItem(Material.NAME_TAG, "&c&lH&2&lo&c&lH&2&lo&c&lH&2&lo Tag"), p.replace("@", "50")));


                e.setItem(48, Utils.setLore(GTM.getItemManager().getItem("christmascake").getItem(), p.replace("@", "4")));
                e.setItem(49, Utils.setLore(GTMGuns.getInstance().getWeaponManager().getWeapon("clausinator").get().createItemStack(), p.replace("@", "250"),"&7Shooting at players stack a", "&7slowness effect on them", "&7for a short duration"));
                e.setItem(50, Utils.setLore(Utils.createItem(Material.MAGMA_CREAM, "&c&lDevil's Snowballs x16", 16), p.replace("@", "3")));
                return;
            }
            case "sellinvconfirm": {
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                this.setConfirmDefaults(e, "&aSell inventory for " + Utils.formatMoney(user.getSellInvConfirmAmt()), "&cCancel the transaction");
                return;
            }
            case "cheatcodes": {
                this.setPhoneDefaults(e);
                int[] slots = new int[] { 11, 13, 15, 20, 22, 24, 29, 31, 33, 40 };
                for (int i = 0; i < CheatCode.getCodes().length; i++) {//will only work with a max of 9 codes
                    int slot = slots[i];
                    CheatCode code = CheatCode.getCodes()[i];
                    State state = GTM.getUserManager().getLoadedUser(uuid).getCheatCodeState(code).getState();
                    e.setItem(slot, code.getDisplayItem(Core.getUserManager().getLoadedUser(uuid), state));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the contacts page!"));
                e.setItem(51, Utils.createItem(Material.BOOK, "&2&lActivating Cheatcodes", "&7You can activate cheatcodes by clicking the item in this menu,", "&7by using &a&l/<cheatcode>&7, and &a&l/cheatcode <cheatcode>&7!"));
                return;
            }
            case "drugdealer": {
                this.setPhoneDefaults(e);
                Iterator<Integer> slots = Arrays.asList(11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42).iterator();
                Collection<DrugDealerItem> drugDealerItems =
                        GTM.getDrugManager().getDrugDealer().getItems()
                                .stream()
                                .filter(drugDealerItem -> !drugDealerItem.isShouldDisplay())
                                .collect(Collectors.toList());
                drugDealerItems.forEach(drugDealerItem -> {
                    if (!slots.hasNext()) return;
                    e.setItem(slots.next(), drugDealerItem.getItemStack());
                });
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu."));
                e.setItem(49, Utils.createItem(Material.SAPLING, 2, "&3&lDrug Dealer", "&7Score some dope!"));
                return;
            }
            case "phone":
                this.setPhoneDefaults(e);
                e.setItem(11,
                        Utils.createItem(Material.ENDER_CHEST, "&e&lCosmetics", "&7Stand out from the crowd!"));
                e.setItem(13,
                        Utils.setArmorColor(
                                Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang", "&7Play with friends,", "&7and dominate the city!"),
                                Color.fromRGB(102, 127, 51)));
                e.setItem(15, Utils.createItem(Material.POWERED_MINECART, "&2&lProperty", "&7Houses, Online ATM and Vehicles!"));
                e.setItem(29,
                        Utils.setSkullOwner(
                                Utils.createItem(Material.SKULL_ITEM, 1, "&5&lBounties", "&7Place hits on other players!"),
                                player.getName()));
                e.setItem(31, Utils.createItem(Material.NETHER_STAR, "&d&lMy Account", "&7Stats, Ranks and Prefs!"));
                e.setItem(33, Utils.createItem(Material.CHEST, "&b&lKits", "&7Gear up!"));
                e.setItem(47, Utils.createItem(Material.BOOK, "&6&lContacts", "&7Call your associates!"));
                e.setItem(49, Utils.createItem(Material.EMERALD, "&a&l" + Core.getSettings().getServer_GTM_shortName() + " Store", "&7Support the server!"));

                e.setItem(51, Utils.createItem(Material.EXP_BOTTLE, "&a&lRewards", "&7" + (Core.getSettings().isSister() ? "" : "Voting, ") + "daily and donor bonuses!"));
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
                e.setItem(11, Utils.createItem(Material.EMPTY_MAP, "&a&lRanks", "&7Working my way to the top!"));
                e.setItem(13, Utils.createItem(Material.BOOK, "&d&lStats", "&7You got skills!"));
                e.setItem(15, Utils.createItem(Material.REDSTONE_COMPARATOR, "&5&lPreferences", "&7Toggle your stuff!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.NETHER_STAR, "&d&lMy Account", "&7Take care of your biz!"));
                return;
            case "prefs":
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the account page!"));
                return;
            case "ranks": {
                this.setPhoneDefaults(e);
                int[] gtmRankSlots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24};

                int[] donorSlots = new int[]{38, 39, 40, 41, 42};
                int i = 0;
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                for (GTMRank rank : GTMRank.getGTMRanks()) {
                    if (rank == GTMRank.HOBO)
                        continue;
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
                        case CRIMINAL:
                            lore.add("&7Drivers License! (&a&lCars&7)");
                            break;
                        case HOMIE:
                            lore.add("&7Team up! (Join a &a&lGang&7)");
                            lore.add("&7Unlock &e&lMarksman Pistol&7!");
                            break;
                        case THUG:
                            lore.add("&7A place to crash! (1 &3&lHouse&7)");
                            lore.add("&7Unlock &e&lHeavy Shotgun&7!");
                            lore.add("&7Unlock &a&lLight &bArmor Upgrade&7!");
                            break;
                        case GANGSTER:
                            lore.add("&7Create your own &a&lGang&7!");
                            lore.add("&7Use &b&lCOP Mode&7!");
                            lore.add("&7Unlock &e&lChainsaw&7!");
                            lore.add("&7Unlock &e&lGusenberg Sweeper&7!");
                            lore.add("&7Unlock &a&lDurable &bArmor Upgrade&7!");
                            break;
                        case MUGGER:
                            lore.add("&7Growing! (3 &a&lGang Members&7)");
                            lore.add("&7Another hideout! (2 &3&lHouses&7)");
                            lore.add("&7Unlock &e&lRPG&7!");
                            lore.add("&7Unlock &a&lUltra Light &bArmor Upgrade&7!");
                            break;
                        case HUNTER:
                            lore.add("&7Use &8&lHITMAN Mode&7!");
                            lore.add("&7Connecting! (&a&l5 Gang Members&7)");
                            lore.add("&7Unlock &e&lHeavy Sniper&7!");
                            lore.add("&7Unlock &a&lTank &bArmor Upgrade&7!");
                            break;
                        case DEALER:
                            lore.add("&7Fly &3&lPlanes&7!");
                            lore.add("&7Unlock &e&lSpecial Carbine&7!");
                            lore.add("&7Unlock &a&lReinforced &bArmor Upgrade&7!");
                            break;
                        case PIMP:
                            lore.add("&7Spreading! (&a&l10 Gang Members&7)");
                            lore.add("&7Unlock &e&lGrenade Launcher&7!");
                            lore.add("&7Unlock &a&lBomb Squad &bArmor Upgrade&7!");
                            break;
                        case MOBSTER:
                            lore.add("&7Buy a crib! (3 &3&lHouses&7)");
                            lore.add("&7Unlock &e&lCombat MG&7!");
                            lore.add("&7Unlock &a&lExoskeleton &bArmor Upgrade&7!");
                            lore.add("&7Up, up and away! (&4&lJetpack&7)");
                            break;
                        case GODFATHER:
                            lore.add("&7Broadening! (&a&l20 Gang Members&7)");
                            lore.add("&7Unlock &e&lHoming Launcher&7!");
                            lore.add("&7Unlock &e&lMinigun&7!");
                            lore.add("&7Unlock &a&lEnhanced &bArmor Upgrade&7!");
                            break;
                        default:
                            lore.add("&7Rank up for cool perks!");
                    }
                    e.setItem(gtmRankSlots[i],
                            Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), Utils.f(lore)));
                    i++;
                }
                i = 0;
                for (UserRank rank : new UserRank[]{UserRank.VIP, UserRank.PREMIUM, UserRank.ELITE, UserRank.SPONSOR, UserRank.SUPREME}) {
                    List<String> lore = new ArrayList<>();
                    lore.add("");
                    lore.add("&7Price: &a$&l" + rank.getPrice());
                    lore.add("");
                    lore.add("&aPerks:");
                    lore.add("");
                    lore.add("&a+ &e&l" + rank.getMonthlyTokens() + " Tokens &a&lmonthly&7!");
                    lore.add("&bKit " + rank.getColoredNameBold() + "&7!");
                    lore.add("&a&l" + GTMUtils.getBackpackRows(rank) + "&6&l Backpack &7rows!");
                    lore.add("&a+ $&l" + GTMUtils.getStartingMoney(rank) + "&7 in-game money!");
                    lore.add("&a+ &l" + GTMUtils.getHouses(rank) + "&7 extra &3&lHouses&7");
                    lore.add("&a+ &7Expanding! (&a&l" + GTMUtils.getGangMembers(rank) + "&7 Gang Members)");
                    lore.add("&a+ &7Call a &6&lCab&7! (&a&l/tpa&7)");
                    lore.add("&a+ " + GTMUtils.getWarpDelay(rank) + "&7s delay &6&lTaxi Service&7!");
                    if (rank.isHigherThan(UserRank.VIP)) {
                        lore.add("&a+ Instantly&7 teleport to the map!");
                        lore.add("&a+ &7Upgraded GPS! (&a+~&l" + GTMUtils.getExtraCompassAccuracy(rank) + "&a%&7)");
                        lore.add("&a+ &7Join &c&lFull&7 servers!");
                        lore.add("&a+ &7Unlock &3&lCOP Mode&7!");
                    }
                    lore.add("&a+ &7Switch &3&lJob Mode&7 faster! (&a" + GTMUtils.getJobModeDelay(rank) + "&7)");
                    if (rank.isHigherThan(UserRank.PREMIUM)) {
                        lore.add("&a+ &7Unlock &8&lHITMAN Mode&7!");
                        lore.add("&a+ &7Pick up your friend! (&a&l/tpahere&7)");
                    }
                    if (rank.isHigherThan(UserRank.ELITE)) {
                        lore.add("&a+ &7Satisfy yourself! (&a&l/feed&7)");
                        lore.add("&a+ &7Up, up and away! (&4&lJetpack&7)");
                    }
                    if (rank.isHigherThan(UserRank.SUPREME)) {
                        lore.add("&a+ &7Quick sell Cheatcode");
                    }
                    for (LockedWeapon w : LockedWeapon.values()) {
                        if (w.getUserRank() == rank || rank.isHigherThan(w.getUserRank())) {
                            GameItem g = GTM.getItemManager().getItemFromWeapon(w.toString());
                            if (g != null)
                                lore.add("&a+ &7Unlock " + g.getDisplayName() + "&7 instantly!");
                        }
                    }
                    for (ArmorUpgrade u : ArmorUpgrade.values())
                        if (u.getUserRank() == rank || rank.isHigherThan(u.getUserRank()))
                            lore.add("&a+ &7Unlock " + u.getDisplayName() + " &b&lArmor Upgrade&7 instantly!");
                    e.setItem(donorSlots[i], Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore));
                    i++;

                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to my account!"));
                GTMRank next = user.getRank().getNext();
                if (next != null)
                    e.setItem(31, Utils.createItem(Material.PAPER, "&a&lRankup to " + next.getColoredNameBold() + "&a&l!",
                            "&7Price: &" + (user.hasMoney(next.getPrice()) ? "a" : "c") + "$&l" + next.getPrice()));
                return;
            }
            case "gtmstats": {
                this.setPhoneDefaults(e);
                GTMUser u = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(11, Utils.createItem(Material.PAPER, "&a&lMoney: &f" + Utils.round(u.getMoney())));
                e.setItem(13, Utils.createItem(Material.ITEM_FRAME, "&a&lBank: &f" + Utils.round(u.getBank())));
                e.setItem(15, Utils.createItem(Material.EMPTY_MAP, "&3&lPermits: &f" + u.getPermits()));
                e.setItem(29, Utils.createItem(Material.IRON_SWORD, "&e&lKills: &f" + u.getKills()));
                e.setItem(31, Utils.createItem(Material.SKULL_ITEM, "&c&lDeaths: &f" + u.getDeaths()));
                e.setItem(33, Utils.createItem(Material.IRON_SWORD, "&a&lK/D Ratio: &f" + u.getKDR()));
                e.setItem(49, Utils.createItem(Material.BOOK_AND_QUILL, "&6&lKillstreak: &f" + u.getKillStreak()));
                e.setItem(51, Utils.createItem(Material.NETHER_STAR, "&c&lWanted Level: &f"
                        + GTMUtils.getWantedLevelStars(u.getWantedLevel()) + " (" + u.getWantedLevel() + ')'));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to my account!"));
                return;
            }
            case "bounties":
                this.setPhoneDefaults(e);
                e.setItem(11,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&5&lBounties List", "&7View all active bounties!"));
                e.setItem(15, Utils.createItem(Material.COMPASS, "&5&lFind Player",
                        GTM.getUserManager().getLoadedUser(player.getUniqueId()).getJobMode()
                                == JobMode.HITMAN ? "&7Track wanted players down with your GPS Tracker!" : "&cRequires HITMAN Mode!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.PAPER, "&5&lPlace Bounty", "&7Place a hit on a player!"));
                e.setItem(51, Utils.createItem(Material.BOOK, "&5&lBounties Help", "&7Some helpful information!"));
                return;
            case "bountieslist": {
                BountyManager bm = GTM.getBountyManager();
                this.setPhoneDefaults(e);
                Set<Bounty> bounties = bm.getBountiesByAmount();
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<Bounty> it = bounties.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    Bounty b = it.next();
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Bounty Total: &a$&l" + b.getAmount());
                    lore.add("");
                    double anon = 0;
                    for (BountyPlacer p : b.getPlacers())
                        if (p.isAnonymous())
                            anon += p.getAmount();
                        else
                            lore.add("&7" + p.getName() + ": &a$&l" + p.getAmount());
                    if (anon > 0) {
                        lore.add("&7Anonymous: &a$&l" + anon);
                    }
                    lore.add("");
                    lore.add("&7Expires: &a&l" + Utils.timeInMillisToText(b.getTimeUntilExpiryInMillis()));
                    e.setItem(slots[i], Utils.setSkullOwner(
                            Utils.createItem(Material.SKULL_ITEM, 3, "&5&l" + b.getName(), lore), b.getName()));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the bounties page!"));
                e.setItem(49, Utils.createItem(Material.BOOK_AND_QUILL, "&5&lBounty List", "&7Page 1"));
                if (bounties.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&5&lNext Page", "&7Page 2"));
                return;
            }
            case "bountieshelp": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 30, 31, 32, 33, 34, 38, 39, 40, 41, 42};
                List<ItemStack> items = new ArrayList<>();
                items.add(Utils.createItem(Material.PAPER, "&5&lWho can claim bounties?",
                        "&7Only players that are on the Hitman Job", "&7can claim the money from bounties."));
                items.add(Utils.createItem(Material.PAPER, "&5&lWho can place bounties?", "&7Anyone with at least $2.000",
                        "&7can place a bounty on a player."));
                items.add(Utils.createItem(Material.PAPER, "&5&lWhat are Anonymous bounties?",
                        "&7Your name will not be shown in the List", "&7for placing an Anonymous Bounty."));
                items.add(Utils.createItem(Material.PAPER, "&5&lHow long do bounties last?",
                        "&7If a bounty hasn't been claimed within", "&724 hours, all money will be lost."));
                items.add(Utils.createItem(Material.PAPER, "&5&lCan 2 people pay for the same bounty?",
                        "&7Multiple people can place a bounty", "&7on the same player.", "&7The hitman will receive the total amount."));
                for (int i = 0; i < items.size(); i++)
                    e.setItem(slots[i], items.get(i));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the bounties page!"));
                e.setItem(49, Utils.createItem(Material.BOOK, "&5&lBounties Help", "&7Some helpful information!"));
                return;
            }
            case "bountiesplace": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                if (user.getBountyName() == null)
                    e.setItem(11, Utils.createItem(Material.SKULL_ITEM, 3, "&5&lChoose Player", "&7Pick your target!"));
                else
                    e.setItem(11, Utils.setSkullOwner(
                            Utils.createItem(Material.SKULL_ITEM, 3, "&5&lChoose Player", "&7" + user.getBountyName()),
                            user.getBountyName()));
                if (user.getBountyAmount() <= 0)
                    e.setItem(15, Utils.createItem(Material.PAPER, 1, "&5&lChoose Amount", "&7Name your price!"));
                else
                    e.setItem(15, Utils.createItem(Material.PAPER, "&5&lChoose Amount", "&a$&l" + user.getBountyAmount()));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the bounties page!"));
                e.setItem(49, Utils.createItem(Material.SLIME_BALL, "&5&lConfirm Bounty", "&7Click to place the hit!"));
                e.setItem(51, Utils.createItem(Material.SKULL_ITEM, 1, "&5&lPlace Anonymously", "&7No one will know!"));
                return;
            }
            case "kits": {
                this.setPhoneDefaults(e);
                GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                int[] grSlots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29};
                int[] jobSlots = new int[]{31, 33};
                int[] otherSlots = new int[]{30, 32,};
                int[] urSlots = new int[]{38, 39, 40, 41, 42};
                int i = 0;
                GTMRank gtmRank = gtmUser.getRank();
                for (GTMRank rank : GTMRank.getGTMRanks()) {
                    Kit kit = GTM.getItemManager().getKit(rank.getName().toLowerCase());
                    if (kit == null) {
                        continue;
                    }
                    List<String> lore = new ArrayList<>();

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

                    lore.add("");
                    if (gtmUser.getJobMode() != JobMode.CRIMINAL)
                        lore.add("&cRequires " + JobMode.CRIMINAL.getColoredNameBold() + " Mode");
                    if (!(rank == GTMRank.HOBO || rank == gtmRank))
                        lore.add("&7Requires exact rank " + rank.getColoredNameBold());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(gtmUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInSecondsToText(kit.getDelay())
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                gtmUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));

                    ItemStack item = Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(meta);
                    e.setItem(grSlots[i], item);
                    i++;
                }
                i = 0;
                for (JobMode job : new JobMode[]{JobMode.COP, JobMode.HITMAN}) {
                    Kit kit = GTM.getItemManager().getKit(job.getName().toLowerCase());
                    if (kit == null) {
                        continue;
                    }

                    List<String> lore = new ArrayList<>();
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

                    lore.add("");
                    if (gtmUser.getJobMode() != job)
                        lore.add("&cRequires " + job.getColoredNameBold() + " Mode");
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(gtmUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInSecondsToText(kit.getDelay())
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                gtmUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));

                    ItemStack item = Utils.createItem(job.getMaterial(), job.getColoredNameBold(), lore);
                    if (job.getMaterial() == Material.LEATHER_CHESTPLATE)
                        item = Utils.setArmorColor(item, Color.BLUE);
                    else if (job.getMaterial() == Material.SKULL_ITEM)
                        item.setDurability((short) 1);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(meta);
                    e.setItem(jobSlots[i], item);
                    i++;
                }
                i = 0;
                for (UserRank rank : UserRank.getDonorRanks()) {
                    Kit kit = GTM.getItemManager().getKit(rank.getName().toLowerCase());
                    if (kit == null) {
                        i++;
                        continue;
                    }
                    List<String> lore = new ArrayList<>();

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

                    lore.add("");
                    if (!(rank == user.getUserRank()
                            || (rank == UserRank.SUPREME && user.getUserRank().isHigherThan(UserRank.SUPREME))))
                        lore.add("&cRequires " + rank.getColoredNameBold());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(gtmUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInSecondsToText(kit.getDelay())
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                gtmUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));

                    ItemStack item = Utils.createItem(rank.getMaterial(), rank.getColoredNameBold(), lore);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(meta);
                    e.setItem(urSlots[i], item);
                    i++;
                }
                i = 0;
                for (Kit kit : GTM.getItemManager().getKits()) {
                    if (GTMRank.getRankOrNull(kit.getName()) != null || UserRank.getUserRankOrNull(kit.getName()) != null
                            || JobMode.getModeOrNull(kit.getName()) != null)
                        continue;
                    List<String> lore = new ArrayList<>();

                    lore.add("");
                    lore.addAll(kit.getContents().stream().map(KitItem::getDescription).collect(Collectors.toList()));

                    lore.add("");
                    if (kit.getPermission() != null && !player.hasPermission(kit.getPermission()))
                        lore.add("&cRequires permission " + kit.getPermission());
                    if (kit.getCost() > 0)
                        lore.add("&7Cost: &a$&l" + kit.getCost());
                    if (kit.getDelay() > 0)
                        lore.add(gtmUser.canUseKit(kit.getName())
                                ? "&7Delay: &a&l" + Utils.timeInMillisToText((long) kit.getDelay() * 1000)
                                : "&cTime Left: &l" + Utils.timeInMillisToText(
                                gtmUser.getKitExpiry(kit.getName()) - System.currentTimeMillis()));

                    ItemStack item = Utils.createItem(kit.getMaterial(), "&b&l" + kit.getName().toUpperCase(), lore);
                    ItemMeta meta = item.getItemMeta();
                    meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
                            ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_POTION_EFFECTS);
                    item.setItemMeta(meta);

                    try {
                        e.setItem(otherSlots[i], item);
                        i++;
                        if (i > otherSlots.length) break;
                    } catch (ArrayIndexOutOfBoundsException ex) {}
                }

                return;

            }
            case "contacts": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                e.setItem(11, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service", "&7Click to select a destination!"));
                e.setItem(13, Utils.createItem(Material.SKULL_ITEM, 2, user.getJobMode() == JobMode.COP ? "&3&lBackup" : "&3&lPolice", user.getJobMode() == JobMode.COP ? "&7Click to request help from fellow officers!" : "&7Click to call the cops to your location!"));
                e.setItem(15, Utils.createItem(Material.WOOD_SWORD, "&c&lSuicide Hotline", "&7Call the Suicide Hotline!"));

                e.setItem(29, Utils.createItem(Material.ANVIL, "&2&lCheat Codes", "&7Become a cheater and rule the game!"));

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.WATCH, "&c&l911 Emergency", "&7Click to teleport out of here!"));


                return;
            }
            case "taxi":
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.SKULL_ITEM, 3, "&e&lPlayer", "&7Click to select a player!"));
                e.setItem(13, Utils.createItem(Material.EMERALD, "&a&lQuick Play", "&7Click to teleport to a random location!"));
                e.setItem(22, Utils.createItem(Material.ENDER_PEARL, "&e&lWarp", "&7Click to select a warp!"));
                e.setItem(15, Utils.createItem(Material.IRON_DOOR, "&3&lHouse", "&7Click to select a house!"));
                e.setItem(31, Utils.createItem(Material.BED, "&e&lSpawn", "&7Click to teleport to spawn!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the contacts page!"));
                e.setItem(49, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service",
                        "&7Click a button to select your destination!"));
                return;
            case "taxiplayers": {
                this.setPhoneDefaults(e);
                List<Player> players = new ArrayList<>();
                for (Player bp : Bukkit.getOnlinePlayers()) {
                    if (!player.getUniqueId().equals(bp.getUniqueId())) {
                        players.add(bp);
                    }
                }
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
            case "taxihouses": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42,
                        47, 48, 49, 50, 51};
                User u = Core.getUserManager().getLoadedUser(uuid);
                GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                List<UserHouse> houses = user.getHouses();
                List<PremiumHouse> premiumHouses = user.getPremiumHousesAsGuest();
                Iterator<UserHouse> it = houses.iterator();
                Iterator<PremiumHouse> it2 = premiumHouses.iterator();
                for (int i = 0; i < 20; i++) {
                    PremiumHouse premiumHouse = it.hasNext() ? null : it2.hasNext() ? it2.next() : null;
                    UserHouse userHouse = it.hasNext() ? it.next() : null;
                    if (premiumHouse == null && userHouse == null)
                        break;
                    if (premiumHouse != null) {
                        e.setItem(slots[i],
                                Utils.addGlow(Utils.createItem(
                                        Material.IRON_DOOR, "&3&lPremium House: &a&l" + premiumHouse.getId(), Arrays.asList(
                                                "Permits: &a&l" + premiumHouse.getPermits(),
                                                "&7Chests: &a&l" + premiumHouse.getChests().size(),
                                                "&7Owned by &a" + (Objects.equals(player.getUniqueId(), premiumHouse.getOwner())
                                                        ? "me" : premiumHouse.getOwnerName()) + '.',
                                                u.isRank(UserRank.ELITE) ? "&7Click to teleport!" :
                                                        gtmUser.hasMoney(500) ? "&7Click to teleport for &a$&l500&7!" : "&cYou can't afford &c$&l500&c to pay for the ride!"))))
                        ;
                        continue;
                    }

                    House house = Houses.getHousesManager().getHouse(userHouse.getId());
                    e.setItem(slots[i],
                            Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(),
                                    Arrays.asList("&7Price: &a$&l" + house.getPrice(),
                                            "&7Chests: &a&l" + house.getChests().size(),
                                            u.isRank(UserRank.ELITE) ? "&7Click to teleport!" : gtmUser.hasMoney(500) ? "&7Click to teleport for &a$&l500&7!" : "&cYou can't afford &c$&l500&c to pay for the ride!")));
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                e.setItem(49, Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: &3&lHouses", "&7Page 1"));
                if (houses.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                return;
            }
            case "taxiwarps": {
                this.setPhoneDefaults(e);
                User u = Core.getUserManager().getLoadedUser(uuid);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                List<Warp> warps = GTM.getWarpManager().getWarps();
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
            case "bank": {
                this.setPhoneDefaults(e);
                GTMUser user = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
                if (user != null){
                    e.setItem(13,
                            Utils.createItem(Material.SKULL_ITEM, 3, "&a&lTransfer Money", "&7Send money to another player!"));
                    e.setItem(29, Utils.createItem(Material.INK_SACK, 1, "&c&lWithdraw Money", "&7Take it all!"));
                    e.setItem(31,
                            Utils.createItem(Material.BOOK, "&3&lBank Balance: &a$&l" + Utils.formatMoney(user.getBank()), "&7You a rich fool!"));
                    e.setItem(33,
                            Utils.createItem(Material.SLIME_BALL, "&a&lDeposit Money", "&7Trust me! Your money is safe here!"));
                    e.setItem(49, Utils.createItem(Material.PAPER, "&3&lBanking", "&7Your financial buddy!"));
                    e.setItem(47, Utils.createItem(Material.REDSTONE, "&7Return to the property page!"));
                }
                return;
            }
            case "bankwithdraw": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
                int[] amnts = new int[]{100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000};
                for (int i = 0; i < 9; i++)
                    e.setItem(slots[i], Utils.createItem(Material.PAPER,
                            (user.hasBank(amnts[i]) ? "&a" : "&c") + "$&l" + amnts[i], "&7Click to withdraw!"));
                e.setItem(39, Utils.createItem(Material.PAPER, "&3&lHalf: &a$&l" + Utils.round(user.getBank() / 2),
                        "&7Click to withdraw!"));
                e.setItem(40, Utils.createItem(Material.PAPER, "&3&lAll: &a$&l" + user.getBank(), "&7Click to withdraw"));
                e.setItem(41,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&a&lCustom Amount", "&7Click to choose an amount!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the banking page!"));
                return;
            }
            case "bankdeposit": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
                int[] amnts = new int[]{100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000};
                for (int i = 0; i < 9; i++)
                    e.setItem(slots[i], Utils.createItem(Material.PAPER,
                            (user.hasMoney(amnts[i]) ? "&a" : "&c") + "$&l" + amnts[i], "&7Click to deposit!"));
                e.setItem(39, Utils.createItem(Material.PAPER, "&3&lHalf: " + (user.hasMoney(200) ? "&a" : "&c") + "$&l" + Utils.round(user.getMoney() / 2),
                        "&7Click to deposit!"));
                e.setItem(40, Utils.createItem(Material.PAPER, "&3&lAll: " + (user.hasMoney(200) ? "&a" : "&c") + "$&l" + user.getMoney(), "&7Click to deposit"));
                e.setItem(41,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&a&lCustom Amount", "&7Click to choose an amount!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the banking page!"));
                return;
            }
            case "banktransfer": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32};
                int[] amnts = new int[]{100, 200, 500, 1000, 2000, 5000, 10000, 20000, 50000};
                for (int i = 0; i < 9; i++)
                    e.setItem(slots[i], Utils.createItem(Material.PAPER,
                            (user.hasBank(amnts[i]) ? "&a" : "&c") + "$&l" + amnts[i], "&7Click to transfer!"));
                e.setItem(39, Utils.createItem(Material.PAPER, "&3&lHalf: &a$&l" + Utils.round(user.getBank() / 2),
                        "&7Click to transfer!"));
                e.setItem(40, Utils.createItem(Material.PAPER, "&3&lAll: &a$&l" + user.getBank(), "&7Click to transfer"));
                e.setItem(41,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&a&lCustom Amount", "&7Click to choose an amount!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the banking page!"));
                return;
            }
            case "gps": {
                this.setGPSDefaults(e);
                GTMUser u = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                Optional<Gang> optional = GangManager.getInstance().getGangByMember(player.getUniqueId());

                e.setItem(10, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lGang Member", !optional.isPresent() ? "&cYou don't have a gang!" : "&7Click to select a homie!"), Color.fromRGB(102, 127, 51)));
                HouseUser user = Houses.getUserManager().getLoadedUser(uuid);
                e.setItem(13, Utils.createItem(Material.IRON_DOOR, "&3&lHouses", user.getHouses().isEmpty() && user.getPremiumHousesAsGuest().isEmpty() ? "&cYou don't own any houses!" : "&7Click to select a house!"));
                e.setItem(16, Utils.setArmorColor(Utils.createItem(Material.LEATHER_HELMET, "&b&lAssist Cop", u.getJobMode() == JobMode.COP ? "&7Click to select a cop!" : "&cRequires COP Mode!"),
                        Color.BLUE));
                if (u.hasPersonalVehicle()) {
                    PersonalVehicle vehicle = u.getPersonalVehicle();
                    ItemStack stack = vehicle.getVehicleProperties().getItem().clone();
                    ItemMeta meta = stack.getItemMeta();
                    List<String> lore = new ArrayList<>(meta.getLore());
                    lore.add(Utils.f("&7Health: " + vehicle.getFormattedHealth()));
                    lore.add(Utils.f("&aClick to track your personal vehicle!"));
                    stack.setItemMeta(meta);
                    e.setItem(29, Utils.addGlow(stack));
                }
                e.setItem(31, Utils.createItem(Material.REDSTONE, "&c&lReset Tracker", "&7Click to reset!"));
                e.setItem(46, Utils.createItem(Material.SKULL_ITEM, 3, "&e&lWanted Criminal",
                        u.getJobMode() == JobMode.COP ? "&7Click to select a criminal!" : "&cRequires COP Mode!"));
                e.setItem(49, Utils.createItem(Material.COMPASS, "&c&lGPS Tracker",
                        u.hasCompassTarget() ? "&7Click to refresh!" : "&7Click a button to set your destination!"));
                e.setItem(52,
                        Utils.createItem(Material.SKULL_ITEM, 1, "&5&lBounty Tracker",
                                u.getJobMode() == JobMode.HITMAN ? "&7Click to select a wanted player!"
                                        : "&cRequires HITMAN Mode!"));
                return;
            }
            case "gpsgangs": {
                this.setGPSDefaults(e);
                int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};

                Optional<Gang> optional = GangManager.getInstance().getGangByMember(uuid);
                if (!optional.isPresent()) {
                    MenuManager.openMenu(player, "gps");
                    return;
                }

                List<String> members = new ArrayList<>(optional.get().getMembers().stream().filter(member -> !Objects.equals(member.getUniqueId(), player.getUniqueId()) && Bukkit.getPlayer(member.getUniqueId()) != null).map(GangMember::getName).collect(Collectors.toList()));

                members.remove(player.getName());
                Iterator<String> it = members.iterator();
                for (int i = 0; i < 28; i++) {
                    if (!it.hasNext())
                        break;
                    String member = it.next();
                    e.setItem(slots[i], Utils.setSkullOwner(
                            Utils.createItem(Material.SKULL_ITEM, 3, "&a&l" + member, "&7Click to track your homie!"),
                            member));
                }
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lTrack Gang Member",
                        "&7Click on a gang member to select them!"), Color.fromRGB(102, 127, 51)));
                e.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                if (members.size() > 28)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
                return;
            }
            case "gpshouses": {
                int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33,
                        34, 37, 38, 39, 40, 41, 42, 43};
                this.setGPSDefaults(e);
                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                List<UserHouse> houses = user.getHouses();
                List<PremiumHouse> premiumHouses = user.getPremiumHousesAsGuest();
                int counter = 0;
                for(PremiumHouse house : premiumHouses) {
                    if(counter>=28)
                        break;
                    e.setItem(slots[counter],
                            Utils.addGlow(
                                    Utils.createItem(Material.IRON_DOOR, "&3&lPremium House: &a&l" + house.getId(),
                                            Arrays.asList("&7Permits: " + house.getPermits(),
                                                    "&7Chests: " + house.getChests().size(),
                                                    "&7Owned by " + (Objects.equals(player.getUniqueId(), house.getOwner())
                                                            ? "me" : house.getOwnerName()) + '.',
                                                    "&7Click to track!"))));
                    counter++;
                }
                for(UserHouse userHouse : houses) {
                    if(counter>=28)
                        break;
                    House house = Houses.getHousesManager().getHouse(userHouse.getId());
                    e.setItem(slots[counter], Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(), Arrays.asList("&7Price: " + house.getPrice(), "&7Chests: " + house.getChests().size(), "&7Click to track!")));
                    counter++;
                }
                e.setItem(49,
                        Utils.setArmorColor(
                                Utils.createItem(Material.IRON_DOOR, "&3&lFind House", "&7Click on a house to select it!"),
                                Color.fromRGB(102, 127, 51)));
                e.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                if ((houses.size() + premiumHouses.size()) > 28)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
                return;
            }
            case "gpscops": {
                this.setGPSDefaults(e);
                int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33,
                        34, 37, 38, 39, 40, 41, 42, 43};
                List<GTMUser> cops = GTMUtils.getCops();
                cops.remove(GTM.getUserManager().getLoadedUser(player.getUniqueId()));
                Iterator<GTMUser> it = cops.iterator();
                for (int i = 0; i < 28; i++) {
                    if (!it.hasNext())
                        break;
                    GTMUser u = it.next();
                    Player p = Bukkit.getPlayer(u.getUUID());
                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&b&l" + p.getName(),
                            "&7Click to track your colleague!"), p.getName()));
                }
                e.setItem(49, Utils.setArmorColor(
                        Utils.createItem(Material.LEATHER_CHESTPLATE, "&b&lAssist Cop", "&7Click on a cop to select them!"),
                        Color.fromRGB(102, 127, 51)));
                e.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                if (cops.size() > 28)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&b&lNext Page", "&7Page 2"));
                return;
            }
            case "gpscriminals": {
                this.setGPSDefaults(e);
                int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33,
                        34, 37, 38, 39, 40, 41, 42, 43};
                Set<GTMUser> criminals = GTMUtils.getCriminalsByWantedLevel(2);
                criminals.remove(GTM.getUserManager().getLoadedUser(player.getUniqueId()));
                Iterator<GTMUser> it = criminals.iterator();
                for (int i = 0; i < 28; i++) {
                    if (!it.hasNext())
                        break;
                    GTMUser u = it.next();
                    Player p = Bukkit.getPlayer(u.getUUID());
                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + p.getName(),
                            "&7Click to track this criminal!"), p.getName()));
                }
                e.setItem(49, Utils.createItem(Material.COMPASS, "&e&lTrack Wanted Criminal",
                        "&7Click on a criminal to select them!"));
                e.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                if (criminals.size() > 28)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                return;
            }
            case "gpsbounties": {
                BountyManager bm = GTM.getBountyManager();
                this.setGPSDefaults(e);
                Set<Bounty> bounties = bm.getBountiesByAmount();
                int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33,
                        34, 37, 38, 39, 40, 41, 42, 43};
                Iterator<Bounty> it = bounties.iterator();
                for (int i = 0; i < 28; i++) {
                    if (!it.hasNext())
                        break;
                    Bounty b = it.next();
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Bounty Total: &a$&l" + b.getAmount());
                    lore.add("");
                    double anon = 0;
                    for (BountyPlacer p : b.getPlacers())
                        if (p.isAnonymous())
                            anon += p.getAmount();
                        else
                            lore.add("&7" + p.getName() + ": &a$&l" + p.getAmount());
                    if (anon > 0) {
                        lore.add("&7Anonymous: &a$&l" + anon);
                    }
                    e.setItem(slots[i], Utils.setSkullOwner(
                            Utils.createItem(Material.SKULL_ITEM, 3, "&5&l" + b.getName(), lore), b.getName()));
                }
                e.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.COMPASS, "&5&lTrack Bounties", "&7Page 1"));
                if (bounties.size() > 28)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&5&lNext Page", "&7Page 2"));
                return;
            }
            case "mygang": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);

                Optional<Gang> optional = GangManager.getInstance().getGangByMember(uuid);

                if (!optional.isPresent()) {
                    e.setItem(31, Utils.createItem(Material.SLIME_BALL, "&a&lCreate Gang", "&7Price: " + (user.hasMoney(500000) ? "&a" : "&c") + "$&l500,000"));
                    return;
                }

                e.setItem(13, Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2,
                                "&a&lLeader: " + optional.get().getOwnerName(), optional.get().isLeader(uuid)
                                        ? Collections.singletonList("&7Click to appoint a new leader!") : Collections.emptyList()),
                                        optional.get().getOwnerName()));

                if (optional.get().isLeader(uuid)) e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lDisband Gang", "&7Click to abandon your homies!"));
                else e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lLeave Gang", "&7Click to abandon your homies!"));

                e.setItem(29, Utils.createItem(Material.BOOK_AND_QUILL, "&a&lGang Relations", "&7Click to view your allies and enemies!"));
                e.setItem(33, Utils.createItem(Material.TRIPWIRE_HOOK, "&a&lView Members", "&7Click to view your homies!"));
                e.setItem(15, Utils.createItem(Material.EMPTY_MAP, "&a&lDescription", optional.get().isLeader(uuid) || optional.get().isCoLeader(uuid)
                                ? Arrays.asList("&7" + optional.get().getDescription(), "&7Click to change!")
                                : Collections.singletonList("&7" + optional.get().getDescription())));

                e.setItem(11, Utils.createItem(Material.FEATHER, "&a&lName: " + optional.get().getName(), optional.get().isLeader(uuid) ? Collections.singletonList("&7Click to change the name!") : Collections.emptyList()));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang: " + optional.get().getName()), Color.fromRGB(102, 127, 51)));
                e.setItem(51, Utils.createItem(Material.BOOK, "&a&lGang List", "&7Click to view the most powerful gangs!"));
                return;
            }
            case "gang": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);

                Optional<Gang> ownGang = GangManager.getInstance().getGangByMember(uuid);
                if(!ownGang.isPresent()) return;
                Gang gang = ownGang.get().getViewingGang(uuid).orElse(null);

                GTM.log("1");
                boolean isEnemy = ownGang.get().isEnemy(gang);
                if (gang == null) {
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lGang: Unknown", "&7This gang does not exist!"));
                    return;
                }

                GTM.log("2");
                if (Objects.equals(gang, ownGang.get())) {
                    ServerUtil.runTaskLater(() -> MenuManager.openMenu(player, "mygang"), 1);
//                    new BukkitRunnable() {
//                        @Override
//                        public void run() {
//                            MenuManager.openMenu(player, "mygang");
//                        }
//                    }.runTaskLater(GTM.getInstance(), 1);
                    return;
                }

                GTM.log("3");
                e.setItem(13, Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 2, "&a&lLeader: " + gang.getOwnerName()), gang.getOwnerName()));
                e.setItem(29, Utils.createItem(Material.BOOK_AND_QUILL, "&a&lGang Relations", "&7Click to view this gang's allies and enemies!"));
                e.setItem(33, Utils.createItem(Material.TRIPWIRE_HOOK, "&a&lView Members", "&7Click to view this gang's homies!"));
                e.setItem(15, Utils.createItem(Material.EMPTY_MAP, "&a&lDescription", "&7" + gang.getDescription()));
                e.setItem(11, Utils.createItem(Material.FEATHER, "&a&lName: &7&l" + gang.getName()));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + "&lGang: " + gang.getName()), isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                e.setItem(51, Utils.createItem(Material.BOOK, "&a&lGang List", "&7Click to view the most powerful gangs!"));
                return;
            }

            case "disbandgang":
                this.setConfirmDefaults(e, "&a&lClick to disband your gang!", "&c&lCancel");
                return;

            case "leavegang":
                this.setConfirmDefaults(e, "&a&lClick to leave your gang!", "&c&lCancel");
                return;

            case "mygangmembers": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if(gang == null) return;

                List<GangMember> members = new ArrayList<>(gang.getMembers());
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<GangMember> it = members.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext()) break;
                    GangMember m = it.next();

                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&a&l" + m.getName(), "&7Rank: &a&l" + m.getRole().getFormattedTag(),
                            "&7Online: &a&l" + m.isOnline(), "&7Click to view this gang member!"), m.getName()));
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang Members", "&7Page 1"), Color.fromRGB(102, 127, 51)));
                if (members.size() > 20) e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
                int size = members.size();
                int max = gang.getMaxMembers();

                List<String> lore = new ArrayList<>();
                lore.add("&7Your gang can have &a" + (max - size) + "&7 more members!");
                if (gang.isLeader(uuid)) {
                    if (size >= max) lore.add("&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get more gang members!");
                    lore.add("&7Click to invite a player!");
                }

                if (gang.isCoLeader(uuid)) lore.add("&7Click to invite a player!");
                e.setItem(51, Utils.createItem(Material.BOOK, "&a&lMax Gang Members: &a&l" + size + "&7/&a&l" + max, lore));
                return;
            }

            case "gangmember": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);

                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (gang == null) return;

                GangMember member = gang.getViewingGangMember(uuid).orElse(null);
                if (member == null) return;

                e.setItem(31, Utils.createItem(Material.SKULL_ITEM, 2, "&a&l" + member.getName(), "&7Online: &a&l" + (Bukkit.getPlayer(member.getUniqueId()) != null)));
                e.setItem(15, Utils.createItem(Material.PAPER, "&a&lRank: " + member.getRole().getFormattedTag()));
                if (gang.isLeader(uuid) || gang.isCoLeader(uuid)) {
                    if (member.isCoLeader()) e.setItem(13, Utils.createItem(Material.BOOK_AND_QUILL, 1, "&a&lDemote", "&7Make this player a Member!"));
                    else e.setItem(13, Utils.createItem(Material.SLIME_BALL, "&a&lPromote", "&7Make this player a Coleader!"));
                    e.setItem(11, Utils.createItem(Material.INK_SACK, 1, "&a&lKick", "&7Force this player to leave the gang!"));
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lGang Member: " + member.getName()), Color.fromRGB(102, 127, 51)));
                List<String> lore = new ArrayList<>();
                int size = gang.getMembers().size();
                int max = gang.getMaxMembers();
                lore.add("&7Your gang can have &a" + (max - size) + "&7 more members!");
                if (gang.isLeader(uuid) && size >= max)
                    lore.add("&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get more gang members!");

                e.setItem(51, Utils.createItem(Material.BOOK, "&a&lMax Gang Members: &a&l" + size + "&7/&a&l" + max, lore));
                return;

            }
            case "gangmembers": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Gang ownGang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (ownGang == null) return;

                Gang gang = ownGang.getViewingGang(uuid).orElse(null);
                if (gang == null) {
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lGang: &7&lUnknown", "&7This gang does not exist!"));
                    return;
                }

                boolean isEnemy = ownGang.isEnemy(gang);
                if (Objects.equals(gang, ownGang)) {
                    ServerUtil.runTaskLater(() -> MenuManager.openMenu(player, "mygangmembers"), 1);
                    return;
                }

                List<GangMember> members = new ArrayList<>(gang.getMembers());
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Iterator<GangMember> it = members.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext()) break;
                    GangMember m = it.next();
                    e.setItem(slots[i], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, (isEnemy ? "&c" : "&a") + "&l" + m.getName(), "&7Rank: &a&l" + m.getRole().getFormattedTag(), "&7Online: &a&l" + m.isOnline(), "&7Click to view this gang member!"), m.getName()));
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + "&lGang Members: &7&l" + gang.getName(), "&7Page 1"), isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                if (members.size() > 20) e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));

                int size = members.size();
                int max = gang.getMaxMembers();
                e.setItem(51, Utils.createItem(Material.BOOK, "&a&lMax Gang Members: &a&l" + size + "&7/&a&l" + max, size >= max ? "&7Go to &a" + Core.getSettings().getStoreLink() + "&7 to get more gang members!" : "&7Your gang can have &a" + (max - size) + "&7 more members!"));
                return;
            }
            case "mygangrelations": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (gang == null) return;

                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Set<GangRelation> relations = gang.getRelations();
                int i = 0;
                for (GangRelation r : relations) {
                    int online = 0;
                    int amnt = 0;

                    Optional<Gang> optional = GangManager.getInstance().getGang(r.getRelativeId());
                    if (optional.isPresent()) {
                        Gang ga = optional.get();
                        online = ga.getOnlineMembers().size();
                        amnt = ga.getMembers().size() + 1;
                    }
                    boolean isEnemy = gang.isEnemy(optional.get());
                    boolean isAlly = gang.isAllied(optional.get());
                    e.setItem(slots[i], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + r.getRelativeName(),
                                                    "&7Relation: " + (isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral"),
                                                    amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                                    amnt == 0 ? "This gang is not online!" : "&7Click to view this gang!"),
                                                    isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                    i++;
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang Relations", "&7Page 1"), Color.fromRGB(102, 127, 51)));
                if (relations.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));

                if (gang.isLeader(uuid) || gang.isCoLeader(uuid))
                    e.setItem(51, Utils.createItem(Material.BOOK, "&a&lSet Relation", "&7Click to set a relation towards an other gang!"));
                return;
            }
            case "gangrelations": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);

                Gang ownGang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (ownGang == null) return;

                Gang gang = ownGang.getViewingGang(uuid).orElse(null);
                if (gang == null) {
                    e.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lGang: &7&lUnknown", "&7This gang does not exist!"));
                    return;
                }

                boolean isOwnEnemy = ownGang.isEnemy(gang);
                if (Objects.equals(gang, ownGang)) {
                    ServerUtil.runTaskLater(() -> MenuManager.openMenu(player, "mygangrelations"), 1);
                    return;
                }

                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Set<GangRelation> relations = gang.getRelations();
                int i = 0;
                for (GangRelation r : relations) {
                    int online = 0;
                    int amnt = 0;

                    Optional<Gang> optional = GangManager.getInstance().getGang(r.getRelativeId());
                    if (optional.isPresent()) {
                        Gang ga = optional.get();
                        online = ga.getOnlineMembers().size();
                        amnt = ga.getMembers().size() + 1;
                    }
                    boolean isEnemy = gang.isEnemy(optional.get());
                    boolean isAlly = gang.isAllied(optional.get());
                    e.setItem(slots[i], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + optional.get().getName(),
                                                    "&7Relation: " + (isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral"),
                                                    amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                                    "&7Click to view this gang!"),
                                                    isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                    i++;
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isOwnEnemy ? "&c" : "&a") + "&lGang Relations: &7&l" + gang.getName(), "&7Page 1"), isOwnEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                if (relations.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));

                if (ownGang.isLeader(uuid) || ownGang.isCoLeader(uuid))
                    e.setItem(51, Utils.createItem(Material.BOOK, "&a&lSet Relation", "&7Click to set a relation towards this gang!"));
                return;
            }
            case "gangs": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (gang == null) return;

                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Set<Gang> gangs = GangManager.getInstance().getGangs();
                Iterator<Gang> it = gangs.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext()) break;
                    Gang g = it.next();
                    int online = g.getOnlineMembers().size();
                    int amnt = g.getMembers().size() + 1;
                    boolean isEnemy = gang.isEnemy(g);
                    boolean isAlly = gang.isAllied(g);

                    GangMember member = gang.getMember(uuid).orElse(null);
                    if (member != null) {
                        String relation = Objects.equals(gang, g) ? "&a&l" + member.getRole().getTag() : isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral";
                        e.setItem(slots[i], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + g.getName(),
                                "&7Relation: " + relation, amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                amnt == 0 ? "This gang is not online!" : "&7Click to view this gang!"),
                                isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                    }
                }

                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                e.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lGang List", "&7Page 1"), Color.fromRGB(102, 127, 51)));
                if (gangs.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
                return;
            }

            case "ammopouch": {
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                int i = 0;
                for (AmmoType type : AmmoType.getTypes()) {
                    if (type.isInInventory())
                        continue;
                    ItemStack item = type.getGameItem().getItem();
                    int a = user.getAmmo(type);
                    e.setItem(i, Utils.createItem(item.getType(), item.getItemMeta().getDisplayName(), a >= 127 ? 127 : a,
                            "&7Amount: &a&l" + a));
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                List<Player> jailedPlayers = GTMUtils.getJailedPlayers();
                Iterator<Player> it = jailedPlayers.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext())
                        break;
                    Player p = it.next();
                    GTMUser u = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                    if (!u.isArrested())
                        continue;
                    List<String> lore = new ArrayList<>();
                    lore.add("&7Time Left: &a&l" + Utils.timeInSecondsToText(u.getJailTimer()));
                    if (user.getJobMode() == JobMode.COP)
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
            case "property":
                this.setPhoneDefaults(e);
                e.setItem(11, Utils.createItem(Material.IRON_DOOR, "&3&lHouses", "&7My place to crash!"));
                e.setItem(13, Utils.createItem(Material.PAPER, "&3&lOnline Banking", "&7Keeping your money safe!"));
                e.setItem(15, Utils.createItem(Material.MINECART, "&c&lVehicles", "&7Ride in style!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                e.setItem(49, Utils.createItem(Material.POWERED_MINECART, "&2&lProperty", "&7Please select a property!"));
                return;
            case "vehicles": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
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
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the property page!"));
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(user.getActionVehicle());
                GameItem item = GTM.getItemManager().getItemFromVehicle(user.getActionVehicle());
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
                    Optional<Weapon<?>> o = GTM.getWastedGuns().getWeaponManager().getWeapon(vehicle.getWastedGunsWeapon());
                    o.ifPresent(weapon -> e.setItem(29, weapon.createItemStack()));
                }
                if (!vehicle.getAllowedWeapons().isEmpty()) {
                    List<String> lore = vehicle.getAllowedWeapons().stream().map(s -> GTM.getItemManager().getItemFromWeapon(s)).filter(Objects::nonNull).map(GameItem::getDisplayName).collect(Collectors.toList());
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(user.getActionVehicle());
                GameItem item = GTM.getItemManager().getItemFromVehicle(user.getActionVehicle());
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
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
                    Optional<Weapon<?>> o = GTM.getWastedGuns().getWeaponManager().getWeapon(vehicleProperties.getWastedGunsWeapon());
                    o.ifPresent(weapon -> e.setItem(29, weapon.createItemStack()));
                }
                if (!vehicleProperties.getAllowedWeapons().isEmpty()) {
                    List<String> lore = vehicleProperties.getAllowedWeapons().stream().map(s -> GTM.getItemManager().getItemFromWeapon(s)).filter(Objects::nonNull).map(GameItem::getDisplayName).collect(Collectors.toList());
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
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
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
            case "heads": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                Set<Head> heads = GTM.getShopManager().getNonExpiredHeadsByBid();
                Iterator<Head> it = heads.iterator();
                for (int i = 0; i < 20; i++) {
                    if (!it.hasNext()) break;

                    Head head = it.next();
                    List<String> lore = new ArrayList<>();
                    lore.add(Utils.f("&7Seller: &a&l" + head.getSellerName()));

                    if (head.hasBid()) {
                        lore.add(Utils.f("&7Bidder: &a&l" + head.getBidderName()));
                        lore.add(Utils.f("&7Bid: &a$&l" + head.getBid()));
                    }
                    else {
                        lore.add(Utils.f("&7Starting Bid: &a$&l10,000"));
                    }

                    lore.add(Utils.f("&7Click to bid!"));
                    lore.add(Utils.f("&7Time Left: &a&l" + Utils.timeInMillisToText(head.getTimeUntilExpiry())));
                    lore.add(Utils.f("&0" + head.getExpiry()));
                    e.setItem(slots[i], new JLibItem.Builder().withType(Material.SKULL_ITEM).withDurability((short) 3).withName(Utils.f("&e&l" + head.getHead())).withLore(lore).withOwner(head.getHead()).build().getItemStack());
                }
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, Utils.createItem(Material.SKULL_ITEM, "&e&lHead Auction", "&7Buy and sell your souvenirs!"));
                if (heads.size() > 20)
                    e.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page 2"));
                e.setItem(51, Utils.createItem(Material.PAPER, "&e&lAuction Head", "&7Click to auction a player head!"));
                return;
            }
            case "auctionhead": {
                this.setPhoneDefaults(e);
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item == null || item.getType() != Material.SKULL_ITEM || item.getDurability() != 3) {
                    player.sendMessage(Lang.HEAD_AUCTION.f("&7That's not a player head!"));
                    player.closeInventory();
                    return;
                }
                this.setConfirmDefaults(e, "&a&lAuction " + item.getItemMeta().getDisplayName() + "&a&l!", "&c&lCancel");
                return;
            }
            case "armorupgrade": {
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                ArmorUpgrade upgrade = user.getBuyingArmorUpgrade();
                if (upgrade == null) {
                    player.closeInventory();
                    return;
                }
                if (!upgrade.canUseUpgrade(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
                    player.closeInventory();
                    player.sendMessage(Lang.HEY.f("&7You need to rank up to " + upgrade.getGTMRank().getColoredNameBold() + "&7 or donate for " + upgrade.getUserRank().getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    return;
                }
                ItemStack item = player.getInventory().getItemInMainHand();
                GameItem gameItem = item == null ? null : GTM.getItemManager().getItem(item.getType());
                if (item == null || gameItem == null || !upgrade.canBeUsedOn(gameItem.getName())) {
                    player.closeInventory();
                    player.sendMessage(Lang.HEY.f("&7The &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7 can only be applied to the following types of items: " + upgrade.getTypesString() + "&7!"));
                    return;
                }
                HashSet<ArmorUpgrade> upgradesOnItem = ArmorUpgrade.getArmorUpgrades(item);

                if (upgradesOnItem.contains(upgrade)) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7That piece of armor already has the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    e.setCancelled(true);
                    return;
                }
                if ((upgradesOnItem.contains(ArmorUpgrade.LIGHT) && upgrade == ArmorUpgrade.ULTRA_LIGHT) || (upgradesOnItem.contains(ArmorUpgrade.ULTRA_LIGHT) && upgrade == ArmorUpgrade.LIGHT)) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7This upgrade cannot be added to the armor piece due to conflicting upgrades."));
                    e.setCancelled(true);
                    return;
                }
                double price = upgrade.getPrice();

                if (!user.hasMoney(upgrade.getPrice())) {
                    player.closeInventory();
                    player.sendMessage(Lang.ARMOR_UPGRADE.f("&7You can't afford the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                    return;
                }
                this.setConfirmDefaults(e, "&a&lBuy &b&l" + upgrade.getDisplayName() + " Armor Upgrade", "&c&lCancel",
                        "&7Price: &a$&l" + price, "&7Item: &a&l" + (item.getItemMeta().hasDisplayName() ? item.getItemMeta().getDisplayName() : item.getType().name()));
                return;
            }
            case "lottery": {
                this.setPhoneDefaults(e);
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                LotteryPlayer p = GTM.getLottery().getLotteryPlayer(player.getUniqueId());
                int[] slots = new int[]{12, 13, 14, 21, 22, 23, 30, 31, 32, 39, 40};
                int[] amnts = new int[]{1, 2, 5, 10, 20, 50, 100, 200, 500, 1000, 2000};
                for (int i = 0; i < 11; i++)
                    e.setItem(slots[i], Utils.createItem(Material.EMPTY_MAP,
                            (user.hasMoney(amnts[i] * 500) ? "&e" : "&c") + "&l" + amnts[i] + " Tickets", "&7Price: &a$&l" + (amnts[i] * 500), "&7Click to buy tickets!"));
                e.setItem(41,
                        Utils.createItem(Material.BOOK_AND_QUILL, "&e&lCustom Amount", "&7Click to choose an amount!"));
                e.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                e.setItem(49, Utils.createItem(Material.GOLD_INGOT, "&e&lLottery", "&7Your tickets: &e&l" + (p == null ? 0 : p.getTickets()), "&7Go big or go home!"));
                LotteryPlayer winner1 = GTM.getLottery().getWinner(0);
                LotteryPlayer winner2 = GTM.getLottery().getWinner(1);
                LotteryPlayer winner3 = GTM.getLottery().getWinner(2);
                e.setItem(51, Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3,
                        "&e&lLast week's winners",
                        winner1 == null ? "" : "&a#&l1&7: &r" + winner1.getName() + " &a" + Utils.formatMoney(winner1.getAmount()) + "&7 (&a70%&7 of the pot)",
                        winner2 == null ? "" : "&a#&l2&7: &r" + winner2.getName() + " &a" + Utils.formatMoney(winner2.getAmount()) + "&7 (&a20%&7 of the pot",
                        winner3 == null ? "" : "&a#&l2&7: &r" + winner3.getName() + " &a" + Utils.formatMoney(winner3.getAmount()) + "&7 (&a10%&7 of the pot)"), winner1 == null ? "Presidentx" : winner1.getName()));
                return;
            }
            case "realestateagent": {
                this.setPhoneDefaults(e);
                e.setItem(12, Utils.createItem(Material.BOOK_AND_QUILL, "&a&lPremium Houses", ""));
                e.setItem(14, Utils.createItem(Material.BOOK_AND_QUILL, "&b&lNon-Premium Houses", ""));
                return;
            }
            case "realestate-premium": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                        49, 50, 51};
                for (PremiumHouse premiumHouse : Houses.getHousesManager().getPremiumHouses()) {
                    if (premiumHouse.getDoors().size() < 1 || premiumHouse.getChests().size() < 1) continue;
                    if (premiumHouse.isOwned()) continue;
                }
                // TODO
                return;
            }
            case "realestate-nonpremium": {
                this.setPhoneDefaults(e);
                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                        49, 50, 51};
                // TODO
                return;
            }
            default:
                break;
        }

    }

    private void setPhoneDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52})
            e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6})
            e.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            e.setItem(i, grayGlass);
    }

    private void setPhoneDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52})
            inv.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6})
            inv.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            inv.setItem(i, grayGlass);
    }

    private void setGPSDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53})
            e.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7})
            e.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            e.setItem(i, grayGlass);
    }

    private void setGPSDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");

        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53})
            inv.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7})
            inv.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            inv.setItem(i, grayGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e) {
        this.setConfirmDefaults(e, "&a&lConfirm", "&c&lCancel");
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage, String... lore) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage, lore);
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

    @EventHandler(priority = EventPriority.HIGH)
    public void onMenuClick(MenuClickEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
        ItemStack item = e.getItem();
        Inventory inv = e.getInv();
        if (item == null || item.getType() == Material.AIR || (item.getType() == Material.STAINED_GLASS_PANE && item.getDurability() != 14 && item.getDurability() != 5))
            return;
        //TODO: EACH SERVER TYPE needs one of these, because of the different GameItem systems that will be present.
        /*if(menu instanceof SubCategoryMenu) {
            SubCategoryMenu category = (SubCategoryMenu)menu;
            if(category.getType()!=ServerType.GTM)
                //some other server will handle the purchase.;
                return;

            if(item.getType()==Material.REDSTONE){
                if(category.getPreviousSubCategory()==null){
                    category.getShopMenu().openFor(player);
                }
                else{
                    category.getPreviousSubCategory().openFor(player);
                }
                return;
            }

            SubCategoryMenu subCategory = category.getSubCategory(item.getItemMeta().getDisplayName());
            if(subCategory==null){
                //at lowest level category, only displaying items currently to purchase
                if(!item.getItemMeta().hasLore())
                    return;
                List<String> lore = item.getItemMeta().getLore();
                double price = Double.parseDouble(ChatColor.stripColor(lore.get(lore.size()-1)).replace("Price: $", ""));

                //lore.remove(lore.get(lore.size()-1));//remove price so it is back to gameitem
                ItemStack gameItemStack = item.clone();
                ItemMeta im = gameItemStack.getItemMeta();
                im.setLore(null);
                gameItemStack.setItemMeta(im);
                GameItem gameItem = GTM.getItemManager().getItem(gameItemStack);
                if(gameItem==null){
                    Core.error("Problem sanitizing SubCategoryItem -> GameItem with item name: " + item.getItemMeta().getDisplayName());
                    player.sendMessage(Utils.f(Lang.SHOP + "&7Error: Unable to locate gameItem from SubCategoryItem"));
                    return;
                }

                if(user.getMoney()<price){
                    player.sendMessage(Utils.f(Lang.SHOP + "&7Error: You do not have enough money to purchase this item!"));
                    return;
                }
                user.setMoney(user.getMoney()-price);
                player.getInventory().addItem(gameItem.getItem());
                player.sendMessage(Utils.f(Lang.SHOP + "&7You have successfully purchased the desired item!"));
                player.closeInventory();
            }
            else{
                //not at lowest level category, still more categories to sort through
                subCategory.openFor(player);
            }
        }
        else {*/

        switch (menu.getName()) {
            case "transferconfirm": {
                switch (item.getType()) {
                    case STAINED_GLASS_PANE: {
                        switch (item.getDurability()) {
                            case 5: {
                                user.setCurrentChatAction(ChatAction.CONFIRM_TRANSFER, true);
                                player.closeInventory();

                                player.sendMessage(Lang.GTM.f("&cPlease read the following points before you select your transfer target:"));

                                player.sendMessage(Utils.f(" &e&lHOUSES&8&l>"));
                                player.sendMessage(Utils.f("&6- Your premium houses will be &c&lSOLD &6and transformed into permits."));
                                player.sendMessage(Utils.f("&6- Purchased trashcans will be &c&LSOLD &6and transformed into permits."));
                                player.sendMessage(Utils.f("&6- Purchased houses will be &c&LSOLD and &6transformed into cash."));
                                player.sendMessage(Utils.f("&6- Any items in houses will be &c&LLOST &6so put them in your backpack."));
                                
                                player.sendMessage(Utils.f(" &e&lCOSMETICS&8&l>"));
                                player.sendMessage(Utils.f("&6- Your player event tags will be &c&lTRANSFERRED &6to the target server."));

                                player.sendMessage(Utils.f(" &a&lMONEY AND ITEMS&8&l>"));
                                player.sendMessage(Utils.f("&6- Your Permits on this server will &c&lREPLACE &6those on the target server."));
                                player.sendMessage(Utils.f("&6- Your Money on this server will &c&lREPLACE &6those on the target server."));
                                player.sendMessage(Utils.f("&6- Your Bank balance on this server &c&lREPLACE &6replace those on the target server."));
                                player.sendMessage(Utils.f("&6- Your GTMRank on this server will &c&lREPLACE &6those on the target server."));
                                player.sendMessage(Utils.f("&6- Your backpack on this server will &c&lREPLACE &6those on the target server."));
                                player.sendMessage(Utils.f("&6- Your cheatcodes on this server will &c&lREPLACE &6those on the target server."));
                                player.sendMessage(Utils.f("&6- Your vehicles on this server will be &c&lSOLD &6and transformed into cash."));
                                player.sendMessage(Utils.f(""));
                                player.sendMessage(Utils.f("&6- Your player inventory on this server will &c&lBE DELETED &6and only &c&lBACKPACK &6contents will transfer."));

                                player.sendMessage(Lang.GTM.f("&6If you agree to THESE TERMS, enter the server that you would like to transfer to out of the following options: "));
                                
                                Chat.TRANSFER_SERVER_ALLOWED.forEach(id -> {
                                	player.sendMessage(Lang.GTM.f("&aGTM" + id));
                                });
                                break;
                            }

                            case 14: {
                                player.closeInventory();
                                player.sendMessage(Lang.GTM.f("&cYou have cancelled the transfer process."));
                                break;
                            }
                        }
                    }
                }
            }
            case "christmasshop": {
                if(!item.getItemMeta().hasLore())
                    return;
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        return;
                    default:
                        int cost = 0;
                        try{
                            cost = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Price: [", "").replace("]", ""));
                        }catch (NumberFormatException nfe){
                            nfe.printStackTrace();
                            player.sendMessage(Lang.CHRISTMAS.f("&cSorry, an internal error occured while trying to purchase your item."));
                            return;
                        }
                        if(!ChristmasEvent.hasCandyCanes(player, cost)) {
                            player.sendMessage(Lang.CHRISTMAS.f("&cYou do not have enough candy canes to purchase this item!"));
                            return;
                        }

                        switch (item.getType()) {
                            case NAME_TAG: {
                                String name = ChatColor.stripColor( item.getItemMeta().getDisplayName().replace(" Tag", "").replace(" ", "_").toUpperCase());
                                EventTag tag = EventTag.valueOf(name);
                                User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
                                if(coreUser.getUnlockedTags().contains(tag)) {
                                    player.sendMessage(Lang.CHRISTMAS.f("&cYou cannot buy tags that you have already unlocked!"));
                                    return;
                                }
                                coreUser.giveEventTag(tag);
                                player.sendMessage(Lang.CHRISTMAS.f("&aYou have been given the " + tag.getBoldName() + " tag. &7Select your active tag by going into Phone -> Account -> Unlocked Tags.\n&7Make sure that the '&6Show Game Rank&7' preference is toggled off."));
                                break;
                            }
                            case MAGMA_CREAM: {
                                Utils.giveItems(player, Utils.createItem(Material.MAGMA_CREAM, "&c&lDevil's Snowball", 16,"&7Throw at players to cause slowness"));
                                break;
                            }
                            default: {
                                if(item.getItemMeta().getDisplayName().contains("Clausinator")) {//if it is the clausinator: because some of the lore is generated post
                                    Utils.giveItems(player, GTM.getItemManager().getItem("clausinator").getItem());
                                    break;
                                }
                                ItemMeta im = item.getItemMeta();
                                im.setLore(Collections.emptyList());
                                item.setItemMeta(im);
                                Utils.giveItems(player, item);
                                break;
                            }
                        }
                        ChristmasEvent.removeCandyCanes(player, cost);
                        player.closeInventory();
                        player.sendMessage(Lang.CHRISTMAS.f("&aThe item has been added to your inventory!"));
                        break;
                }
                break;
            }

            /*
            case "sellinvconfirm": {
                switch (item.getDurability()) {
                    case 5:
                        double cPrice = TrashCanManager.getTotalInvPrice(player);
                       double price = user.getSellInvConfirmAmt();

                        for(int i = 9; i<36; i++){
                            ItemStack is = player.getInventory().getItem(i);
                            if(is == null || is.getType() == Material.AIR) continue;
                            GameItem gameItem = GTM.getItemManager().getItem(is);
							if(gameItem == null || gameItem.getType() == GameItem.ItemType.DRUG) continue;
                            if(!gameItem.canSell()) continue;                            player.getInventory().setItem(i, new ItemStack(Material.AIR));
                        }

                        player.updateInventory();
                        user.addMoney(cPrice);
                        GTMUtils.updateBoard(player, user);
                        player.sendMessage(Utils.f(Lang.MONEY_ADD.toString() + cPrice));

                        if(cPrice!=price) {
                            player.sendMessage(Lang.TRASH_CAN.f("&7The final sale price was different as your inventory changed!"));
                        }

                        user.setSellInvConfirmAmt(0);
                        player.closeInventory();
                        break;
                    case 14:
                        user.setSellInvConfirmAmt(0);
                        player.closeInventory();
                        break;
                }
                return;
            }*/
            case "cheatcodes": {
                User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
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

                if (optCode.get() != CheatCode.QUICKSELL)
                    MenuManager.openMenu(player, "cheatcodes");//refresh
                return;
            }
            case "drugdealer": {
                Optional<DrugItem> drug = ((DrugService) GTM.getDrugManager().getService()).getDrugItem(item.getItemMeta().getDisplayName());
                if (drug.isPresent()) {
                    List<String> itemLore = item.getItemMeta().getLore();
                    int drugStock = Integer.parseInt(ChatColor.stripColor(itemLore.get(0).split(" ")[2]));
                    int drugPrice = Integer.parseInt(ChatColor.stripColor(itemLore.get(1).split(" ")[1]).replace("$", ""));
                    if (drugStock > 0) {
                        net.minecraft.server.v1_12_R1.ItemStack nmsCopy = CraftItemStack.asNMSCopy(item);
                        NBTTagCompound tag = nmsCopy.hasTag() ? nmsCopy.getTag() : new NBTTagCompound();
                        if ((tag != null ? tag.get("drugName") : null) != null) {
                            DrugItem drugItem = drug.get();
                            if (DrugDealerItem.byDrugItem(drugItem).isPresent()) {
                                if (user.getMoney() >= drugPrice) {
                                    user.setMoney(user.getMoney() - drugPrice);
                                    player.getInventory().addItem(drugItem.getItemStack());
                                    DrugDealerItem drugDealerItem = DrugDealerItem.byDrugItem(drugItem).get();
                                    drugDealerItem.setStockRemaining(drugDealerItem.getStockRemaining() - 1);
                                    e.getInv().setItem(e.getSlot(), drugDealerItem.getItemStack());
                                    player.updateInventory();
                                    GTMUtils.updateBoard(player, GTM.getUserManager().getLoadedUser(player.getUniqueId()));
                                } else {
                                    player.sendMessage(Lang.DRUGS.f("&cYou do not have enough money for this drug!"));
                                }
                            } else {
                                player.sendMessage(Lang.DRUGS.f("&cCannot find DrugItem for this drug!"));
                            }
                        } else {
                            player.sendMessage(Lang.DRUGS.f("&cUnable to validate NMS tag on ItemStack"));
                        }
                    }
                } else {
                    switch (item.getType()) {
                        case REDSTONE:
                            player.closeInventory();
                            return;
                        default:
                            return;
                    }
                }
                return;
            }
            case "phone":
                switch (item.getType()) {
                    case ENDER_CHEST:
                        MenuManager.openMenu(player, "cosmetics");
                        return;
                    case LEATHER_CHESTPLATE:
                        MenuManager.openMenu(player, "mygang");
                        return;
                    case POWERED_MINECART:
                        MenuManager.openMenu(player, "property");
                        return;
                    case SKULL_ITEM:
                        MenuManager.openMenu(player, "bounties");
                        return;
                    case NETHER_STAR:
                        MenuManager.openMenu(player, "account");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "contacts");
                        return;
                    case EMERALD:
                        player.closeInventory();
                        player.sendMessage(Lang.GTM.f("&7Go to &a&l" + Core.getSettings().getStoreLink() + "&7 to buy Ranks, Permits, Money and other packages!"));
                        return;
                    case EXP_BOTTLE:
                        MenuManager.openMenu(player, "rewards");
                        return;
                    case CHEST:
                        MenuManager.openMenu(player, "kits");
                        return;
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
                    case EMPTY_MAP:
                        MenuManager.openMenu(player, "ranks");
                        return;
                    case NAME_TAG:
                        MenuManager.openMenu(player, "chooseeventtag");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "gtmstats");
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
            case "gtmstats":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "account");
                        return;
                    default:
                        return;
                }
            case "bounties":
            	
            	if (!GTM.getSettings().isBountySystem()){
            		player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Bounty System is currently disabled!"));
                    return;
            	}
            	
                switch (item.getType()) {
                	
                    case BOOK_AND_QUILL:
                        MenuManager.openMenu(player, "bountieslist");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "bountieshelp");
                        return;
                    case COMPASS:
                        if (GTM.getUserManager().getLoadedUser(player.getUniqueId()).getJobMode() != JobMode.HITMAN) {
                            player.sendMessage(Lang.GPS.f("&7You are not a hitman!"));
                            return;
                        }
                        MenuManager.openMenu(player, "gpsbounties");
                        return;
                    case PAPER:
                        MenuManager.openMenu(player, "bountiesplace");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        return;
                }
            case "bountieslist":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        BountyManager bm = GTM.getBountyManager();
                        this.setPhoneDefaults(inv);
                        Set<Bounty> bounties = bm.getBountiesByAmount();
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41,
                                42};
                        Iterator<Bounty> it = bounties.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Bounty b = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            List<String> lore = new ArrayList<>();
                            lore.add("&7Bounty Total: &a$&l" + b.getAmount());
                            lore.add("");
                            double anon = 0;
                            for (BountyPlacer p : b.getPlacers())
                                if (p.isAnonymous())
                                    anon += p.getAmount();
                                else
                                    lore.add("&7" + p.getName() + ": &a$&l" + p.getAmount());
                            if (anon > 0)
                                lore.add("&7Anonymous: &a$&l" + anon);
                            lore.add("");
                            lore.add("&7Expires: &a&l" + Utils.timeInMillisToText(b.getTimeUntilExpiryInMillis()));
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(
                                    Utils.createItem(Material.SKULL_ITEM, 3, "&5&l" + b.getName(), lore), b.getName()));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the bounties page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&5&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.createItem(Material.BOOK, "&5&lBounty List", "&7Page " + page));
                        if (bounties.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&5&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bounties");
                        return;
                    default:
                        return;
                }
            case "bountiesplace": {
                BountyManager bm = GTM.getBountyManager();
                switch (item.getType()) {
                    case SKULL_ITEM:
                        if (item.getDurability() == 1) {
                        	
                        	if (!GTM.getSettings().isBountySystem()){
                        		player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Bounty System is currently disabled!"));
                                return;
                        	}
                        	
                            if (user.getBountyName() == null) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Please choose a target first!"));
                                return;
                            }
                            if (user.getBountyAmount() <= 0) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Please choose an amount first!"));
                                return;
                            }
                            Player target = Bukkit.getPlayer(user.getBountyUUID());
                            if (target == null) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Your target is not online!"));
                                return;
                            }
                            
                            // balance check
                            if (!user.hasMoney(user.getBountyAmount())) {
                                player.sendMessage(Utils.f(Lang.MONEY + "&7You don't have &c$&l" + user.getBountyAmount()
                                        + " to place this bounty!"));
                                return;
                            }
                            
                            // the amount of money placed for the bounty
                            int bountyAmount = user.getBountyAmount();
                            
                            // take the money from the user that they are placing
                            user.takeMoney(bountyAmount);
                            GTMUtils.updateBoard(player, user);
                            
                            // determine tax on placing new bounties
                            double bountyTax = 0;
                            if (GTM.getSettings().isBountyTax()){
                            	bountyTax = GTM.getSettings().getBountyTaxPercent();
                            	
                            	// clamp bounds
                            	if (bountyTax >= 100.0){
                            		bountyTax = 100.0;
                            	}
                            	if (bountyTax <= 0){
                            		bountyTax = 0;
                            	}
                            }
                            
                            // how much we subtract from balance
                            int taxSubtractAmount = (int) ((bountyTax / 100.0) * bountyAmount);
                            // what's the final placed amount
                            final int finalBountyAmount = bountyAmount - taxSubtractAmount;
                            
                            // TODO test remove
                            Core.log("[Bounty][DEBUG] Bounty placed on " + target.getName() + " with initial amount of " + bountyAmount + ", tax=" + taxSubtractAmount + ", final=" + finalBountyAmount);
                            
                            // if bounty already exists, raise it
                            if (bm.placeBounty(target, finalBountyAmount, player, true)) {
                                player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You raised the bounty on &a" + target.getName()
                                        + "&7 by &a$&l" + finalBountyAmount + "&7, as &c$&l" + taxSubtractAmount + "&7 was for tax purposes."));
                                Utils.broadcastExcept(player, Lang.BOUNTIES + "&7An anonymous player raised the bounty on &a"
                                        + target.getName() + "&7 by &a$&l" + finalBountyAmount + "&7.");
                                user.setBountyAmount(0);
                                user.setBountyName(null);
                                user.setBountyUUID(null);
                                MenuManager.openMenu(player, "bounties");
                                return;
                            }

                            // otherwise add a new one
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You put a bounty of &a&l$" + finalBountyAmount
                                    + "&7 on &a" + target.getName() + "&7's head!"));
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Tax for placing the new bounty was &c&l$" + taxSubtractAmount + "&7."));
                            Utils.broadcastExcept(player, Lang.BOUNTIES + "&7An anonymous player put a bounty of &a$&l"
                                    + finalBountyAmount + "&7 on &a" + target.getName() + "&7.");
                            user.setBountyAmount(0);
                            user.setBountyName(null);
                            user.setBountyUUID(null);
                            MenuManager.openMenu(player, "bounties");
                            return;
                        } else {
                            user.setCurrentChatAction(ChatAction.PICKING_BOUNTY_TARGET, 0);
                            player.closeInventory();
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Type the target in chat, or \"quit\" to cancel."));
                            return;
                        }
                    case PAPER:
                        user.setCurrentChatAction(ChatAction.PICKING_BOUNTY, 0);
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.BOUNTIES
                                + "&7Please type the amount in chat, or type \"quit\" to cancel. The minimum bounty is &a$&l2.000&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case SLIME_BALL:
                    	
                    	if (!GTM.getSettings().isBountySystem()){
                    		player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Bounty System is currently disabled!"));
                            return;
                    	}
                    	
                        if (user.getBountyName() == null) {
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Please choose a target first!"));
                            return;
                        }
                        if (user.getBountyAmount() <= 0) {
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Please choose an amount first!"));
                            return;
                        }
                        Player target = Bukkit.getPlayer(user.getBountyUUID());
                        if (target == null) {
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Your target is not online!"));
                            return;
                        }
                        
                        // balance check
                        if (!user.hasMoney(user.getBountyAmount())) {
                            player.sendMessage(Utils.f(Lang.MONEY + "&7You don't have &c$&l" + user.getBountyAmount() + " to place this bounty!"));
                            return;
                        }
                        
                        // the amount of money placed for the bounty
                        int bountyAmount = user.getBountyAmount();
                        
                        // take the money from the user that they are placing
                        user.takeMoney(bountyAmount);
                        GTMUtils.updateBoard(player, user);
                        
                        // determine tax on placing new bounties
                        double bountyTax = 0;
                        if (GTM.getSettings().isBountyTax()){
                        	bountyTax = GTM.getSettings().getBountyTaxPercent();
                        	
                        	// clamp bounds
                        	if (bountyTax >= 100.0){
                        		bountyTax = 100.0;
                        	}
                        	if (bountyTax <= 0){
                        		bountyTax = 0;
                        	}
                        }
                        
                        // how much we subtract from balance
                        int taxSubtractAmount = (int) ((bountyTax / 100.0) * bountyAmount);
                        // what's the final placed amount
                        final int finalBountyAmount = bountyAmount - taxSubtractAmount;
                        
                        // TODO test remove
                        Core.log("[Bounty][DEBUG] Bounty placed on " + target.getName() + " with initial amount of " + bountyAmount + ", tax=" + taxSubtractAmount + ", final=" + finalBountyAmount);

                        // if bounty already exists, raise it
                        if (bm.placeBounty(target, finalBountyAmount, player, true)) {
                            player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You raised the bounty on &a" + target.getName()
                                    + "&7 by &a$&l" + finalBountyAmount + "&7, as &c$&l" + taxSubtractAmount + "&7 was for tax purposes."));
                            Utils.broadcastExcept(player, Lang.BOUNTIES + "&7An anonymous player raised the bounty on &a"
                                    + target.getName() + "&7 by &a$&l" + finalBountyAmount + "&7.");
                            user.setBountyAmount(0);
                            user.setBountyName(null);
                            user.setBountyUUID(null);
                            MenuManager.openMenu(player, "bounties");
                            return;
                        }

                        // otherwise add a new one
                        player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You put a bounty of &a&l$" + finalBountyAmount
                                + "&7 on &a" + target.getName() + "&7's head!"));
                        player.sendMessage(Utils.f(Lang.BOUNTIES + "&7Tax for placing the new bounty was &c&l$" + taxSubtractAmount + "&7."));
                        Utils.broadcastExcept(player, Lang.BOUNTIES + "&7An anonymous player put a bounty of &a$&l"
                                + finalBountyAmount + "&7 on &a" + target.getName() + "&7.");
                        user.setBountyAmount(0);
                        user.setBountyName(null);
                        user.setBountyUUID(null);
                        MenuManager.openMenu(player, "bounties");
                        return;
                    default:
                        return;
                }
            }
            case "bountieshelp":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "bounties");
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
                    	
                    	if (!GTM.getSettings().isKitSystem()){
                    		player.sendMessage(ChatColor.RED + "Kits are currently disabled!");
                    		return;
                    	}
                    	
                        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()
                                && item.getType() != Material.STAINED_GLASS_PANE) {
                            GTM.getItemManager().giveKit(player, Core.getUserManager().getLoadedUser(uuid),
                                    GTM.getUserManager().getLoadedUser(uuid),
                                    ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                            MenuManager.updateMenu(player, "kits");
                        }
                        return;
                }
            case "contacts": {
                switch (item.getType()) {
                    case STORAGE_MINECART:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    case ANVIL:
                        MenuManager.openMenu(player, "cheatcodes");
                        return;
                    case SKULL_ITEM:
                        if (user.hasRequestedBackup()) {
                            player.sendMessage(Lang.COP_MODE.f("&7You have already called " + (user.getJobMode() == JobMode.COP ? "for backup" : "the police") + "! Please wait &c&l" + Utils.timeInSecondsToText(Math.round(user.getTimeUntilBackupRequestExpires()/1000.0), "&3", "&7", "&7")+ "&7 to request backup again!"));
                            return;
                        }
                        if (user.getJobMode() == JobMode.COP)
                            player.sendMessage(Lang.COP_MODE.f("&7You have called for backup! All officers have been notified, and they can teleport to you for 1 minute!"));
                        else {
                            if (user.getWantedLevel() > 0) {
                                player.sendMessage(Lang.WANTED.f("&7You are currently wanted by the police! It would be unwise to tip off your location."));
                                return;
                            }
                            player.sendMessage(Lang.GTM.f("&7You have called the police! All officers have been notified, and they can teleport to you for 1 minute!"));
                        }
                        user.setLastBackupRequest(System.currentTimeMillis());
                        for (GTMUser u : GTMUtils.getCops()) {
                            Player p = Bukkit.getPlayer(u.getUUID());
                            if (!Objects.equals(player, p))
                                p.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.f((user.getJobMode() == JobMode.COP ? "&3&lCop " : "&7Citizen") + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p))).append(" is requesting " + (user.getJobMode() == JobMode.COP ? "backup" : "police assistance") + "! Teleport: ").color(net.md_5.bungee.api.ChatColor.GRAY).
                                        append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup " + player.getName())).create());

                        }
                        return;
                    case WOOD_SWORD:
                        player.closeInventory();
                        BaseComponent[] baseComponents =
                                new ComponentBuilder(Lang.GTM.f("&7Do you wish to commit suicide?"))
                                        //.color(net.md_5.bungee.api.ChatColor.GRAY)
                                        .append(" Click here for Yes")
                                        .color(net.md_5.bungee.api.ChatColor.GREEN)
                                        .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/suicide"))
                                        .create();
                        player.spigot().sendMessage(baseComponents);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case WATCH:
                        GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid), new TaxiTarget(GTM.getWarpManager().getSpawn()), 0, -1);
                        return;
                    default:
                        return;
                }
            }
            case "taxi":
                switch (e.getItem().getType()) {
                    case SKULL_ITEM:
                        MenuManager.openMenu(player, "taxiplayers");
                        return;
                    case BED:
                        GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                GTM.getUserManager().getLoadedUser(uuid), new TaxiTarget(GTM.getWarpManager().getSpawn()), 0,
                                -1);
                        player.closeInventory();
                        return;
                    case EMERALD:
                        GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user, new TaxiTarget(GTM.getWarpManager().getRandomWarp()), 0, Core.getUserManager().getLoadedUser(player.getUniqueId()).isPremium() ? 1 : 10);
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
                        GTM.getWarpManager().tpa(player, Core.getUserManager().getLoadedUser(uuid),
                                GTM.getUserManager().getLoadedUser(uuid), target);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    default:
                        return;
                }
            case "taxihouses":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    case IRON_DOOR:
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (Objects.equals("Find House", s))
                            return;
                        if (s.startsWith("Premium House: ")) {
                            int id;
                            try {
                                id = Integer.parseInt(s.replace("Premium House: ", ""));
                            } catch (NumberFormatException ex) {
                                player.sendMessage(Utils.f(Lang.HOUSES + "&7That House ID is invalid!"));
                                return;
                            }
                            PremiumHouse premiumHouse = Houses.getHousesManager().getPremiumHouse(id);
                            if (premiumHouse == null) {
                                player.sendMessage(Lang.HOUSES.f("&7That house does not exist!"));
                                return;
                            }
                            PremiumHouseDoor door = premiumHouse.getDoor();
                            if (door == null || door.getOutsideLocation() == null) {
                                player.sendMessage(Lang.HOUSES.f("&7That house does not have any doors!"));
                                return;
                            }
                            Location tpLocation = door.getOutsideLocation();
                            player.closeInventory();
                            GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                    GTM.getUserManager().getLoadedUser(uuid), new TaxiTarget(tpLocation), 500, -1);
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
                            HouseDoor door = house.getDoor();
                            if (door == null || door.getOutsideLocation() == null) {
                                player.sendMessage(Lang.HOUSES.f("&7That house does not have any doors!"));
                                return;
                            }
                            Location tpLocation = door.getOutsideLocation();
                            player.closeInventory();
                            GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                    GTM.getUserManager().getLoadedUser(uuid), new TaxiTarget(tpLocation), 500, -1);
                            return;
                        }
                        return;
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
                                33, 34, 37, 38, 39, 40, 41, 42, 43};
                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                        List<UserHouse> houses = houseUser.getHouses();
                        List<PremiumHouse> premiumHouses = houseUser.getPremiumHousesAsGuest();
                        Iterator<UserHouse> it = houses.iterator();
                        Iterator<PremiumHouse> it2 = premiumHouses.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            PremiumHouse premiumHouse = it.hasNext() ? null : it2.hasNext() ? it2.next() : null;
                            UserHouse userHouse = it.hasNext() ? it.next() : null;
                            if (i < (page - 1) * 20)
                                continue;
                            if (premiumHouse == null && userHouse == null)
                                break;
                            if (premiumHouse != null) {
                                inv.setItem(slots[i - (page - 1) * 20], Utils.addGlow(Utils.createItem(Material.IRON_DOOR,
                                        "&3&lPremium House: &a&l" + premiumHouse.getId(),
                                        Arrays.asList("Permits: &a&l" + premiumHouse.getPermits(),
                                                "&7Chests: &a&l" + premiumHouse.getChests().size(),
                                                "&7Owned by &a" + (Objects.equals(player.getUniqueId(), premiumHouse.getOwner()) ? "me"
                                                        : premiumHouse.getOwnerName()) + '.',
                                                gtmUser.hasMoney(500) ? "&7Click to teleport for &a$&l500&7!" : "&cYou can't afford &c$&l500&c to pay for the ride!"))));
                                continue;
                            }

                            House house = Houses.getHousesManager().getHouse(userHouse.getId());
                            inv.setItem(slots[i - (page - 1) * 20],
                                    Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(),
                                            Arrays.asList("&7Price: &$a&l" + house.getPrice(),
                                                    "&7Chests: &a&l" + house.getChests().size(),
                                                    "&7Click to teleport for &a$&l500&7!")));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the taxi page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49,
                                Utils.createItem(Material.STORAGE_MINECART, "&e&lTaxi Service: &3&lHouses", "&7Page " + page));
                        if (houses.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
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
                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                        List<Warp> warps = GTM.getWarpManager().getWarps();
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
                        Warp warp = GTM.getWarpManager().getWarp(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (warp == null) {
                            player.sendMessage(Lang.TAXI.f("&7That warp does not exist!"));
                            return;
                        }
                        player.closeInventory();
                        GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(uuid),
                                GTM.getUserManager().getLoadedUser(uuid), new TaxiTarget(warp), 200, -1);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "taxi");
                        return;
                    default:
                        return;
                }
            case "bank":
                switch (e.getItem().getType()) {
                    case INK_SACK:
                        MenuManager.openMenu(player, menu.getName() + "withdraw");
                        return;
                    case SLIME_BALL:
                        MenuManager.openMenu(player, menu.getName() + "deposit");
                        return;
                    case SKULL_ITEM:
                        MenuManager.openMenu(player, menu.getName() + "transfer");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "property");
                        return;
                    default:
                        return;
                }
            case "bankwithdraw": {
                switch (item.getType()) {
                    case PAPER:
                        double amnt = Double.parseDouble(ChatColor.stripColor(item.getItemMeta().getDisplayName())
                                .replace("$", "").replace("All: ", "").replace("Half: ", ""));
                        if (!user.hasBank(amnt)) {
                            player.sendMessage(Lang.BANK.f("&7You don't have &c$&l" + amnt + " &cin your bank account!"));
                            return;
                        }
                        user.withdrawFromBank(amnt);
                        GTMUtils.updateBoard(player, user);
                        player.sendMessage(Lang.BANK.f("&7You withdrew &a$&l" + amnt + "&7 from your bank account!"));
                        player.closeInventory();
                        return;
                    case BOOK_AND_QUILL:
                        user.setCurrentChatAction(ChatAction.BANK_WITHDRAWING, 0);
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.BANK
                                + "&7Please type the amount you would like to withdraw in chat, or type&a \"quit\"&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bank");
                        return;
                    default:
                        return;

                }
            }
            case "bankdeposit": {
                switch (item.getType()) {
                    case PAPER:
                        double amnt = Double.parseDouble(ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("$", "").replace("All: ", "").replace("Half: ", ""));
                        if (!user.hasMoney(amnt)) {
                            player.sendMessage(Lang.BANK.f("&7You don't have &c$&l" + amnt + " &con you!"));
                            return;
                        }
                        if(amnt < 100) {
                            player.sendMessage(Lang.BANK.f("&c&lYou must deposit at least $100!"));
                            return;
                        }
                        user.depositToBank(amnt);
                        GTMUtils.updateBoard(player, user);
                        player.sendMessage(Lang.BANK.f("&7You deposited &a$&l" + amnt + "&7 into your bank account!"));
                        player.closeInventory();
                        return;
                    case BOOK_AND_QUILL:
                        user.setCurrentChatAction(ChatAction.BANK_DEPOSITING, 0);
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.BANK
                                + "&7Please type the amount you would like to deposit in chat, or type&a \"quit\"&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bank");
                        return;
                    default:
                        return;

                }
            }
            case "banktransfer": {
                switch (item.getType()) {
                    case PAPER:
                        double amnt = Double.parseDouble(ChatColor.stripColor(item.getItemMeta().getDisplayName())
                                .replace("$", "").replace("All: ", "").replace("Half: ", ""));
                        if (!user.hasBank(amnt)) {
                            player.sendMessage(Lang.BANK.f("&7You don't have &c$&l" + amnt + " &con you!"));
                            return;
                        }
                        
                        if (!GTM.getSettings().isBankToBankTransfer()){
                        	player.sendMessage(Lang.BANK.f("&cBank to bank transferring is currently disabled!"));
                        	return;
                        }
                        
                        user.setCurrentChatAction(ChatAction.BANK_TRANSFERRING, amnt);
                        player.closeInventory();
                        player.sendMessage(Lang.BANK.f("&7Please type the name of the player you would like to transfer &a$&l"
                                + amnt + "&7 to, or type&a \"quit\"&7!"));
                        return;
                    case BOOK_AND_QUILL:
                        user.setCurrentChatAction(ChatAction.BANK_TRANSFERRING, 0);
                        player.closeInventory();
                        player.sendMessage(Lang.BANK
                                .f("&7Please type the amount you would like to deposit in chat, or type &a\"quit\"&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bank");
                        return;
                    default:
                        return;

                }
            }
            case "gps":
                switch (item.getType()) {
                    case LEATHER_CHESTPLATE: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7You are not in any gang!"));
                            return;
                        }

                        MenuManager.openMenu(player, "gpsgangs");
                        return;
                    }
                    case IRON_DOOR: {
                        HouseUser houseUser = Houses.getUserManager().getLoadedUser(uuid);
                        if (houseUser.getHouses().isEmpty() && houseUser.getPremiumHousesAsGuest().isEmpty()) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7You do not own any houses!"));
                            return;
                        }
                        MenuManager.openMenu(player, "gpshouses");

                        return;
                    }
                    case LEATHER_HELMET: {
                        if (user.getJobMode() != JobMode.COP) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7You are not a cop!"));
                            return;
                        }
                        MenuManager.openMenu(player, "gpscops");
                        return;
                    }
                    case REDSTONE: {
                        user.unsetCompassTarget(player, Core.getUserManager().getLoadedUser(uuid));
                        player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS target was unset."));
                        return;
                    }
                    case SKULL_ITEM: {
                        if (item.getDurability() == 3) {
                            if (user.getJobMode() != JobMode.COP) {
                                player.sendMessage(Utils.f(Lang.GPS + "&7You are not a cop!"));
                                return;
                            }
                            MenuManager.openMenu(player, "gpscriminals");
                            return;
                        }
                        if (user.getJobMode() != JobMode.HITMAN) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7You are not a hitman!"));
                            return;
                        }
                        MenuManager.openMenu(player, "gpsbounties");
                        return;
                    }
                    case COMPASS: {
                        if (!user.hasCompassTarget())
                            return;
                        if (user.getLastCompassRefresh() > System.currentTimeMillis() - 5000) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7Please wait 5 seconds before refreshing the tracker!"));
                            return;
                        }
                        if (user.refreshCompassTarget(player, Core.getUserManager().getLoadedUser(uuid))){
                            player.sendMessage(Utils.f(Lang.GPS + "&7You refreshed your GPS tracker! It will refresh every 60 seconds!"));
                        }
                        return;
                    }
                    default:
                        PersonalVehicle vehicle = user.getPersonalVehicle();
                        if (vehicle == null) return;
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName() || !Objects.equals(item.getItemMeta().getDisplayName(), vehicle.getDisplayName()))
                            return;
                        if (!vehicle.onMap()) {
                            player.sendMessage(Lang.GPS.f("&7Your vehicle is not on the map!"));
                            return;
                        }
                        if (vehicle.isDestroyed()) {
                            player.sendMessage(Lang.GPS.f("&7Your vehicle is destroyed!"));
                            return;
                        }
                        user.setCompassTarget(player, Core.getUserManager().getLoadedUser(uuid), new CompassTarget(vehicle));
                        player.sendMessage(Lang.GPS.f("&7Your GPS is now tracking your " + (vehicle.isStolen() ? "stolen " : "")
                                + vehicle.getDisplayName() + "&7!"));
                        return;
                }
            case "gpsgangs":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "gps");
                        return;
                    case SKULL_ITEM: {
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Player p = Bukkit.getPlayer(s);
                        if (p == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not online!"));
                            return;
                        }

                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        Gang targetGang = GangManager.getInstance().getGangByMember(p.getUniqueId()).orElse(null);

                        if (gang == null || targetGang == null || !Objects.equals(gang, targetGang)) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not in your gang!"));
                            return;
                        }

                        player.closeInventory();
                        user.setCompassTarget(player, Core.getUserManager().getLoadedUser(uuid), new CompassTarget(p));
                        player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS is now tracking " + Core.getUserManager().getLoadedUser(uuid).getColoredName(p) + "&7!"));
                        return;
                    }
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setGPSDefaults(inv);
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        List<String> members = new ArrayList<>();
                        if (gang != null) {
                            members.addAll(gang.getMembers().stream().filter(member -> !member.getUniqueId().equals(player.getUniqueId()) && Bukkit.getPlayer(member.getUniqueId()) != null).map(GangMember::getName).collect(Collectors.toList()));
                        }

                        members.remove(player.getName());
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37, 38, 39, 40, 41, 42, 43};
                        Iterator<String> it = members.iterator();
                        for (int i = 0; i < (page * 28); i++) {
                            if (!it.hasNext()) break;
                            String member = it.next();
                            if (i < (page - 1) * 28) continue;

                            inv.setItem(slots[i - (page - 1) * 28], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&a&l" + member, "&7Click to track your homie!"), member));
                        }
                        inv.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&b&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lTrack Gang Member", "&7Click on a gang member to select them!"), Color.fromRGB(102, 127, 51)));
                        if (members.size() > (28 * page)) inv.setItem(50, Utils.createItem(Material.ARROW, "&b&lNext Page", "&7Page " + (page + 1)));
                        return;
                    default:
                        return;
                }
            case "gpshouses":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "gps");
                        return;
                    case IRON_DOOR:
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        if (Objects.equals("Find House", s))
                            return;
                        if (s.startsWith("Premium House: ")) {
                            int id;
                            try {
                                id = Integer.parseInt(s.replace("Premium House: ", ""));
                            } catch (NumberFormatException e1) {
                                player.sendMessage(Utils.f(Lang.GPS + "&7That House ID is invalid!"));
                                return;
                            }
                            PremiumHouse house = Houses.getHousesManager().getPremiumHouse(id);
                            user.setCompassTarget(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), new CompassTarget(house.getDoor().getLocation()));
                            player.sendMessage(
                                    Utils.f(Lang.GPS + "&7You set your GPS target to &3&lPremium House &a&l" + id + "&7!"));
                        } else if (s.startsWith("House: ")) {
                            int id;
                            try {
                                id = Integer.parseInt(s.replace("House: ", ""));
                            } catch (NumberFormatException ex) {
                                player.sendMessage(Utils.f(Lang.GPS + "&7That House ID is invalid!"));
                                return;
                            }
                            House house = Houses.getHousesManager().getHouse(id);
                            user.setCompassTarget(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), new CompassTarget(house.getDoor().getLocation()));
                            player.sendMessage(Utils.f(Lang.GPS + "&7You set your GPS target to &3&lHouse &a&l" + id + "&7!"));
                            return;
                        }
                        return;
                    case ARROW:
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setGPSDefaults(inv);
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
                                33, 34, 37, 38, 39, 40, 41, 42, 43};
                        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                        List<UserHouse> houses = houseUser.getHouses();
                        List<PremiumHouse> premiumHouses = houseUser.getPremiumHousesAsGuest();
                        Iterator<UserHouse> it = houses.iterator();
                        Iterator<PremiumHouse> it2 = premiumHouses.iterator();
                        for (int i = 0; i < (page * 28); i++) {
                            PremiumHouse premiumHouse = it.hasNext() ? null : it2.hasNext() ? it2.next() : null;
                            UserHouse userHouse = it.hasNext() ? it.next() : null;
                            if (i < (page - 1) * 28)
                                continue;
                            if (premiumHouse == null && userHouse == null)
                                break;
                            if (premiumHouse != null) {
                                inv.setItem(slots[i - (page - 1) * 20], Utils.addGlow(Utils.createItem(Material.IRON_DOOR,
                                        "&3&lPremium House: &a&l" + premiumHouse.getId(),
                                        Arrays.asList("Permits: " + premiumHouse.getPermits(),
                                                "&7Chests: " + premiumHouse.getChests().size(),
                                                "&7Owned by " + (Objects.equals(player.getUniqueId(), premiumHouse.getOwner()) ? "me"
                                                        : premiumHouse.getOwnerName()) + '.',
                                                "&7Click to track!"))));
                                continue;
                            }
                            House house = Houses.getHousesManager().getHouse(userHouse.getId());
                            inv.setItem(slots[i - (page - 1) * 28],
                                    Utils.createItem(Material.IRON_DOOR, "&3&lHouse: &a&l" + house.getId(),
                                            Arrays.asList("&7Price: " + house.getPrice(),
                                                    "&7Chests: " + house.getChests().size(), "&7Click to track!")));
                        }
                        inv.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&b&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(
                                Utils.createItem(Material.IRON_DOOR, "&3&lFind House", "&7Click on a house to select it!"),
                                Color.fromRGB(102, 127, 51)));
                        if ((houses.size() + premiumHouses.size()) > (28 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page 2"));
                        return;
                    default:
                        return;

                }
            case "gpscops":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "gps");
                        return;
                    case SKULL_ITEM: {
                        if (user.getJobMode() != JobMode.COP) {
                            player.sendMessage(Lang.GPS.f("&7You are not a cop!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        Player p = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (p == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not online!"));
                            return;
                        }
                        GTMUser u = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                        if (u.getJobMode() != JobMode.COP) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not a cop!"));
                            return;
                        }

                        user.setCompassTarget(player, Core.getUserManager().getLoadedUser(uuid), new CompassTarget(player));
                        player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS is now tracking "
                                + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p) + "&7!"));
                        return;
                    }
                    case ARROW:
                        if (user.getJobMode() != JobMode.COP) {
                            player.sendMessage(Lang.GPS.f("&7You are not a cop!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setGPSDefaults(inv);
                        List<GTMUser> cops = GTMUtils.getCops();
                        cops.remove(GTM.getUserManager().getLoadedUser(uuid));
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
                                33, 34, 37, 38, 39, 40, 41, 42, 43};
                        Iterator<GTMUser> it = cops.iterator();
                        for (int i = 0; i < (page * 28); i++) {
                            if (!it.hasNext())
                                break;
                            GTMUser u = it.next();
                            if (i < (page - 1) * 28)
                                continue;
                            Player p = Bukkit.getPlayer(u.getUUID());
                            inv.setItem(slots[i - (page - 1) * 28], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3,
                                    "&b&l" + p.getName(), "&7Click to track your colleague!"), p.getName()));
                        }
                        inv.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&b&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(
                                Utils.createItem(Material.LEATHER_HELMET, "&b&lAssist Cop", "&7Click on a cop to select them!"),
                                Color.fromRGB(102, 127, 51)));
                        if (cops.size() > (28 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&b&lNext Page", "&7Page " + (page + 1)));
                        return;
                    default:
                        return;
                }
            case "gpscriminals":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "gps");
                        return;
                    case SKULL_ITEM: {
                        if (user.getJobMode() != JobMode.COP) {
                            player.sendMessage(Lang.GPS.f("&7You are not a cop!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        Player p = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (p == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not online!"));
                            return;
                        }
                        GTMUser u = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                        if (u.getJobMode() != JobMode.CRIMINAL) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not a criminal!"));
                            return;
                        }
                        user.setCompassTarget(player, Core.getUserManager().getLoadedUser(uuid), new CompassTarget(player));
                        player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS is now tracking "
                                + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p) + "&7!"));
                        return;
                    }
                    case ARROW:
                        if (user.getJobMode() != JobMode.COP) {
                            player.sendMessage(Lang.GPS.f("&7You are not a cop!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setGPSDefaults(inv);
                        Set<GTMUser> criminals = GTMUtils.getCriminalsByWantedLevel(2);
                        criminals.remove(GTM.getUserManager().getLoadedUser(uuid));
                        Iterator<GTMUser> it = criminals.iterator();
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
                                33, 34, 37, 38, 39, 40, 41, 42, 43};
                        for (int i = 0; i < (page * 28); i++) {
                            if (!it.hasNext())
                                break;
                            GTMUser u = it.next();
                            if (i < (page - 1) * 28)
                                continue;
                            Player p = Bukkit.getPlayer(u.getUUID());
                            inv.setItem(slots[i - (page - 1) * 28], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3,
                                    "&b&l" + p.getName(), "&7Click to track this criminal!"), p.getName()));
                        }
                        inv.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the home page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.createItem(Material.COMPASS, "&e&lTrack Wanted Criminal",
                                "&7Click on a criminal to select them!"));
                        if (criminals.size() > (28 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
                        return;
                    default:
                        return;
                }
            case "gpsbounties":
                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "gps");
                        return;
                    case SKULL_ITEM: {
                        if (user.getJobMode() != JobMode.HITMAN) {
                            player.sendMessage(Lang.GPS.f("&7You are not a hitman!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        Player p = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        if (p == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player is not online!"));
                            return;
                        }
                        if (GTM.getBountyManager().getBounty(p.getUniqueId()) == null) {
                            player.sendMessage(Utils.f(Lang.GPS + "&7That player does not have a bounty on his head!"));
                            return;
                        }
                        user.setCompassTarget(player, Core.getUserManager().getLoadedUser(uuid), new CompassTarget(p));
                        player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS is now tracking " + Core.getUserManager().getLoadedUser(p.getUniqueId()).getColoredName(p) + "&7!"));
                        return;
                    }
                    case ARROW:
                        if (user.getJobMode() != JobMode.HITMAN) {
                            player.sendMessage(Lang.GPS.f("&7You are not a hitman!"));
                            MenuManager.openMenu(player, "gps");
                            return;
                        }
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        BountyManager bm = GTM.getBountyManager();
                        this.setPhoneDefaults(inv);
                        Set<Bounty> bounties = bm.getBountiesByAmount();
                        int[] slots = new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
                                33, 34, 37, 38, 39, 40, 41, 42, 43};
                        Iterator<Bounty> it = bounties.iterator();
                        for (int i = 0; i < (page * 28); i++) {
                            if (!it.hasNext())
                                break;
                            Bounty b = it.next();
                            if (i < (page - 1) * 28)
                                continue;
                            List<String> lore = Arrays.asList("&7Bounty Total: &a$&l" + b.getAmount(), "");
                            double anon = 0;
                            for (BountyPlacer p : b.getPlacers())
                                if (p.isAnonymous())
                                    anon += p.getAmount();
                                else
                                    lore.add("&7" + p.getName() + ": &a$&l" + p.getAmount());
                            if (anon > 0)
                                lore.add("&7Anonymous: &a$&l" + anon);
                            inv.setItem(slots[i - (page - 1) * 28], Utils.setSkullOwner(
                                    Utils.createItem(Material.SKULL_ITEM, 3, "&5&l" + b.getName(), lore), b.getName()));
                        }
                        inv.setItem(46, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the bounties page!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&5&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.createItem(Material.COMPASS, "&5&lBounty List", "&7Page " + page));
                        if (bounties.size() > (28 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&5&lNext Page", "&7Page " + (page + 1)));
                        return;
                    default:
                        return;
                }
            case "mygang":
                switch (item.getType()) {
                    case SLIME_BALL: {
                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "create");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the name of the gang you would like to create, or type &a\"quit\"&7 to quit!"));
                        return;
                    }
                    case SKULL_ITEM: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || !gang.isLeader(uuid)) return;
                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "leader");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the name of the player you would like to appoint as leader of your gang, or type &a\"quit\"&7 to quit!"));
                        return;
                    }
                    case INK_SACK: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || !gang.isLeader(uuid)) return;
                        MenuManager.openMenu(player, gang.isLeader(uuid) ? "disbandgang" : "leavegang");
                        return;
                    }
                    case BOOK_AND_QUILL:
                        MenuManager.openMenu(player, "mygangrelations");
                        return;
                    case TRIPWIRE_HOOK:
                        MenuManager.openMenu(player, "mygangmembers");
                        return;
                    case EMPTY_MAP: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || !(gang.isLeader(uuid) || gang.isCoLeader(uuid))) return;
                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "description");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the description you would like to set for your gang, or type &a\"quit\"&7 to quit!"));
                        return;
                    }
                    case FEATHER:
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || !gang.isLeader(uuid)) return;
                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "name");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the name you would like to set for your gang, or type &a\"quit\"&7 to quit!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "gangs");
                        return;
                    default:
                        return;
                }
            case "gang": {
                Gang own = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                if (own == null) return;

                Gang gang = own.getViewingGang(uuid).orElse(null);
                if (gang == null) return;

                switch (item.getType()) {
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    case BOOK:
                        MenuManager.openMenu(player, "gangs");
                        return;
                    case BOOK_AND_QUILL:
                        MenuManager.openMenu(player, "gangrelations");
                        return;
                    case TRIPWIRE_HOOK:
                        MenuManager.openMenu(player, "gangmembers");
                        return;
                    default:
                        return;
                }
            }
            case "disbandgang":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                                if (gang == null) {
                                    player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                    return;
                                }

                                player.closeInventory();
                                gang.disbandConfirm(player, Core.getUserManager().getLoadedUser(uuid), user);
                                return;
                            case 14:
                                MenuManager.openMenu(player, "mygang");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "leavegang":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                                if (gang == null) {
                                    player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                                    return;
                                }

                                player.closeInventory();
                                gang.leave(player, Core.getUserManager().getLoadedUser(uuid), user);
                                return;
                            case 14:
                                MenuManager.openMenu(player, "mygang");
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "mygangmembers":
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        List<GangMember> members = new ArrayList<>();
                        if (gang != null) members.addAll(gang.getMembers());

                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        Iterator<GangMember> it = members.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext()) break;
                            GangMember m = it.next();
                            if (i < (page - 1) * 20) continue;

                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&a&l" + m.getName(), "&7Rank: &a&l" + m.getRole().getFormattedTag(), "&7Online: &a&l" + m.isOnline(), "&7Click to view this gang member!"), m.getName()));
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang Members", "&7Page " + page), Color.fromRGB(102, 127, 51)));
                        if (members.size() > (20 * page)) inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));
                        return;
                    }
                    case SKULL_ITEM: {
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) {
                            player.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                            return;
                        }

                        Optional<GangMember> optional = gang.getMember(s);
                        if (!optional.isPresent()) {
                            player.sendMessage(Lang.GANGS.f("&7That player is not a member of your gang!"));
                            return;
                        }

                        gang.setViewingGangMember(uuid, optional.get());
                        MenuManager.openMenu(player, "gangmember");
                        return;
                    }
                    case BOOK:
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || (!gang.isLeader(uuid) && !gang.isCoLeader(uuid))) return;
                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "invite");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the name of the player you would like to invite to your gang, or type &a\"quit\"&7 to quit!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "mygang");
                        return;
                    default:
                        return;
                }
            case "gangmember":
                switch (item.getType()) {
                    case BOOK_AND_QUILL: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) return;

                        GangMember member = gang.getViewingGangMember(uuid).orElse(null);
                        if (member == null) return;

                        gang.demote(player, Core.getUserManager().getLoadedUser(uuid), user, member.getName());
                        MenuManager.updateMenu(player, "gangmember");
                        return;
                    }
                    case SLIME_BALL: {
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) return;

                        GangMember member = gang.getViewingGangMember(uuid).orElse(null);
                        if (member == null) return;

                        gang.promote(player, Core.getUserManager().getLoadedUser(uuid), user, member.getName());
                        MenuManager.updateMenu(player, "gangmember");
                        return;
                    }
                    case INK_SACK:
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) return;

                        GangMember member = gang.getViewingGangMember(uuid).orElse(null);
                        if (member == null) return;

                        gang.kick(player, Core.getUserManager().getLoadedUser(uuid), user, member.getName());
                        MenuManager.openMenu(player, "mygangmembers");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "mygangmembers");
                        return;
                    default:
                        return;
                }
            case "gangmembers":
                switch (item.getType()) {
                    case ARROW:
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        Gang ownGang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (ownGang == null) {
                            MenuManager.openMenu(player, "mygangmembers");
                            return;
                        }

                        Gang gang = ownGang.getViewingGang(uuid).orElse(null);
                        if (gang == null) {
                            inv.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lGang: &7&lUnknown", "&7This gang does not exist!"));
                            return;
                        }

                        if (Objects.equals(gang, ownGang)) {
                            MenuManager.openMenu(player, "mygangmembers");
                            return;
                        }

                        boolean isEnemy = ownGang.isEnemy(gang);
                        List<GangMember> members = new ArrayList<>(gang.getMembers());

                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        Iterator<GangMember> it = members.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext()) break;
                            GangMember m = it.next();
                            if (i < (page - 1) * 20) continue;
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, (isEnemy ? "&c" : "&a") + "&l" + m.getName(), "&7Rank: &a&l" + m.getRole().getFormattedTag(),
                                            "&7Online: &a&l" + m.isOnline(), "&7Click to view this gang member!"), m.getName()));
                        }

                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + "&lGang Members: &7&l" + gang.getName(), "&7Page " + page), Color.fromRGB(102, 127, 51)));
                        if (members.size() > (20 * page)) inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));
                        return;
                    case SKULL_ITEM:
                        Player target = Bukkit.getPlayer(ChatColor.stripColor(item.getItemMeta().getDisplayName()));
                        player.closeInventory();
                        GTM.getWarpManager().tpa(player, Core.getUserManager().getLoadedUser(uuid), GTM.getUserManager().getLoadedUser(uuid), target);
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "gang");
                        return;
                    default:
                        return;
                }

            case "mygangrelations":
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        Set<GangRelation> relations = gang.getRelations();
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        int i = 0;
                        for (GangRelation r : relations) {
                            if (i < (page - 1) * 20) continue;
                            int online = 0;
                            int amnt = 0;

                            Optional<Gang> optional = GangManager.getInstance().getGang(r.getRelativeId());
                            if (optional.isPresent()) {
                                Gang ga = optional.get();
                                online = ga.getOnlineMembers().size();
                                amnt = ga.getMembers().size() + 1;
                            }

                            boolean isEnemy = gang.isEnemy(optional.get());
                            boolean isAlly = gang.isAllied(optional.get());
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + optional.get().getName(),
                                                            "&7Relation: " + (isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral"),
                                                            amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                                            "&7Click to view this gang!"),
                                                            isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                            i++;
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));

                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lMy Gang Relations", "&7Page " + page), Color.fromRGB(102, 127, 51)));
                        if (relations.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));

                        if (gang.isLeader(uuid) || gang.isCoLeader(uuid))
                            inv.setItem(51, Utils.createItem(Material.BOOK, "&a&lSet Relation", "&7Click to set a relation towards an other gang!"));
                        return;
                    }
                    case LEATHER_CHESTPLATE: {
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Optional<Gang> viewing = GangManager.getInstance().getGang(s);
                        if (Objects.equals("My Gang Relations", s) || !viewing.isPresent())
                            return;
                        Gang gang = viewing.get();

                        Gang self = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (self == null) return;
                        self.setViewingGang(uuid, gang);

                        MenuManager.openMenu(player, "gang");
                        return;
                    }
                    case BOOK:
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null || !(gang.isLeader(uuid) || gang.isCoLeader(uuid)))
                            return;

                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "relation");
                        gang.setViewingGang(uuid, null);
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the name of the gang you would like to change your relation to, or type &a\"quit\"&7 to quit."));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "mygang");
                        return;
                    default:
                        return;
                }
            case "gangrelations":
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        Gang ownGang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (ownGang == null) return;

                        Gang gang = ownGang.getViewingGang(uuid).orElse(null);
                        if (gang == null) {
                            inv.setItem(31, Utils.createItem(Material.INK_SACK, 1, "&c&lGang: &7&lUnknown", "&7This gang does not exist!"));
                            return;
                        }

                        if (Objects.equals(gang, ownGang)) {
                            MenuManager.openMenu(player, "mygangrelations");
                            return;
                        }

                        boolean isOwnEnemy = gang.isEnemy(ownGang);
                        Set<GangRelation> relations = gang.getRelations();
                        int i = 0;
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        for (GangRelation r : relations) {
                            if (i < (page - 1) * 20) continue;
                            int online = 0;
                            int amnt = 0;

                            Optional<Gang> optional = GangManager.getInstance().getGang(r.getRelativeId());
                            if (optional.isPresent()) {
                                Gang ga = optional.get();
                                online = ga.getOnlineMembers().size();
                                amnt = ga.getMembers().size() + 1;
                            }
                            boolean isEnemy = gang.isEnemy(optional.get());
                            boolean isAlly = gang.isAllied(optional.get());
                            inv.setItem(slots[i - (page - 1) * 20], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + optional.get().getName(),
                                                            "&7Relation: " + (isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral"),
                                                            amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                                            "&7Click to view this gang!"),
                                                            isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                            i++;
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));

                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isOwnEnemy ? "&c" : "&a") + "&lGang Relations: &7&l" + gang.getName(),
                                "&7Page " + page), isOwnEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));

                        if (relations.size() > (20 * page))
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));

                        if (ownGang.isLeader(uuid) || ownGang.isCoLeader(uuid))
                            inv.setItem(51, Utils.createItem(Material.BOOK, "&a&lSet Relation", "&7Click to set a relation towards this gang!"));
                        return;
                    }
                    case LEATHER_CHESTPLATE: {
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Optional<Gang> optional = GangManager.getInstance().getGang(s);
                        if (s.startsWith("Gang Relations: ") || !optional.isPresent())
                            return;

                        Gang self = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (self == null) return;

                        self.setViewingGang(uuid, optional.get());
                        MenuManager.openMenu(player, "gang");
                        return;
                    }
                    case BOOK:
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (gang == null) return;

                        Gang viewing = gang.getViewingGang(uuid).orElse(null);
                        if(viewing == null) return;

                        if (!(gang.isLeader(uuid) || gang.isCoLeader(uuid)) || !GangManager.getInstance().getGang(viewing.getUniqueId()).isPresent())
                            return;


                        user.setCurrentChatAction(ChatAction.GANG_CHAT_ACTION, "relation");
                        player.closeInventory();
                        player.sendMessage(Lang.GANGS.f("&7Please type in the relation (ally, neutral or enemy) you would like to set towards gang &a" + viewing.getName() + "&7, or type &a\"quit\" to quit!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "gang");
                        return;
                    default:
                        return;
                }

            case "gangs":
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        Set<Gang> gangs = GangManager.getInstance().getGangs();
                        Iterator<Gang> it = gangs.iterator();
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Gang g = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            int online = g.getOnlineMembers().size();
                            int amnt = g.getMembers().size() + 1;
                            boolean isEnemy = gang.isEnemy(g);
                            boolean isAlly = gang.isAllied(g);

                            Optional<GangMember> optional = g.getMember(uuid);
                            if(optional.isPresent()) {
                                String relation = Objects.equals(gang, g) ? "&a&l" + optional.get().getRole().getTag() : isEnemy ? "&c&lEnemy" : isAlly ? "&a&lAlly" : "&a&lNeutral";
                                inv.setItem(slots[i - (page - 1) * 20], Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, (isEnemy ? "&c" : "&a") + g,
                                                                "&7Relation: " + relation, amnt == 0 ? "&7Online Members: &c&l0" : "&7Online Members: &a&l" + online + "&7/&a&l" + amnt,
                                                                amnt == 0 ? "This gang is not online!" : "&7Click to view this gang!"),
                                                                isEnemy ? Color.RED : Color.fromRGB(102, 127, 51)));
                            }
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lBack", "&7Return to the gang page!"));
                        if (page > 1) inv.setItem(48, Utils.createItem(Material.ARROW, "&a&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.setArmorColor(Utils.createItem(Material.LEATHER_CHESTPLATE, "&a&lGang List", "&7Page " + page), Color.fromRGB(102, 127, 51)));
                        if (gangs.size() > (20 * page)) inv.setItem(50, Utils.createItem(Material.ARROW, "&a&lNext Page", "&7Page " + (page + 1)));
                        return;
                    }
                    case LEATHER_CHESTPLATE:
                        String s = ChatColor.stripColor(item.getItemMeta().getDisplayName());
                        Optional<Gang> optional = GangManager.getInstance().getGang(s);
                        if (Objects.equals("Gang List", s) || !optional.isPresent())
                            return;

                        Gang self = GangManager.getInstance().getGangByMember(uuid).orElse(null);
                        if (self == null) return;

                        self.setViewingGang(uuid, optional.get());
                        MenuManager.openMenu(player, "gang");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "mygang");
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
                        List<Player> jailedPlayers = GTMUtils.getJailedPlayers();
                        Iterator<Player> it = jailedPlayers.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Player p = it.next();
                            if (i < (page - 1) * 20)
                                continue;

                            GTMUser u = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                            if (!u.isArrested())
                                continue;
                            List<String> lore = new ArrayList<>();
                            lore.add("&7Time Left: &a&l" + Utils.timeInSecondsToText(u.getJailTimer()));
                            if (user.getJobMode() == JobMode.COP && Objects.equals(u.getJailCop(), player.getUniqueId()))
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
                        if (GTM.getUserManager().getLoadedUser(uuid).getJobMode() != JobMode.COP) return;
                        GTMUser u = GTM.getUserManager().getLoadedUser(p.getUniqueId());
                        if (!u.isArrested()) {
                            player.sendMessage(Lang.JAIL.f("&7That player is not in jail!"));
                            MenuManager.updateMenu(player, "jail");
                            return;
                        }
                        if (u.getJailTimer() <= 5) {
                            player.sendMessage(Lang.JAIL.f("&7That prisoners is already being released!"));
                            return;
                        }
                        if (GTM.getUserManager().getLoadedUser(uuid).getJobMode() != JobMode.COP) {
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
            case "property":
                switch (item.getType()) {
                    case IRON_DOOR:
                        MenuManager.openMenu(player, "houses");
                        return;
                    case MINECART:
                        if (GTM.getUserManager().getLoadedUser(uuid).hasPersonalVehicle()) {
                            MenuManager.openMenu(player, "personalvehicle");
                            return;
                        }
                        MenuManager.openMenu(player, "vehicles");
                        return;
                    case PAPER:
                        MenuManager.openMenu(player, "bank");
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "phone");
                        return;
                    default:
                        return;
                }
            case "vehicles":
                switch (item.getType()) {
                    case ARROW: {
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
                    }
                    case REDSTONE:
                        MenuManager.openMenu(player, "property");
                        return;
                    default:
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
                        GameItem gameItem = GTM.getItemManager().getItemFromDisplayName(item.getItemMeta().getDisplayName());
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
                    case SLIME_BALL: {
                        if (user.hasVehicle(user.getActionVehicle())) {
                            player.sendMessage(Lang.VEHICLES.f("&7You already own this vehicle!"));
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        MenuManager.openMenu(player, "buyvehicle");
                        return;
                    }
                    case INK_SACK: {
                        if (!user.hasVehicle(user.getActionVehicle())) {
                            player.sendMessage(Lang.VEHICLES.f("&7You don't own this vehicle!"));
                            MenuManager.openMenu(player, "vehicles");
                            return;
                        }
                        MenuManager.openMenu(player, "sellvehicle");
                        return;
                    }
                    default:
                        return;
                }
            case "buyvehicle":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5:
                                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(user.getActionVehicle());
                                GameItem gameItem = GTM.getItemManager().getItemFromVehicle(user.getActionVehicle());
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
                                GTMUtils.updateBoard(player, user);
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
                                GTMUtils.updateBoard(player, user);
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
                            case 5: {
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
                                GTMUtils.updateBoard(player, user);
                                double health = vehicle.getVehicleProperties().getMaxHealth();
                                vehicle.setHealth(health);
                                vehicle.updateVehicleInDatabase(player, health);
                                player.sendMessage(Lang.VEHICLES.f("&7You repaired vehicle " + vehicle.getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                                MenuManager.openMenu(player, "personalvehicle");
                                return;
                            }
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
                    case ARROW: {
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
                    }
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    default:
                        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;
                        GameItem gameItem = GTM.getItemManager().getItemFromDisplayName(item.getItemMeta().getDisplayName());
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
                        GTMUtils.updateBoard(player, user);
                        double health = vehicle.getVehicleProperties().getMaxHealth();
                        vehicle.setHealth(health);
                        vehicle.updateVehicleInDatabase(player, health);
                        player.sendMessage(Lang.VEHICLES.f("&7The Mechanic repaired vehicle " + vehicle.getDisplayName() + "&7 for &a$&l" + Utils.round(price) + "&7!"));
                        player.closeInventory();
                        return;
                }
            case "heads": {
                switch (item.getType()) {
                    case ARROW: {
                        int page = Integer
                                .parseInt(ChatColor.stripColor(item.getItemMeta().getLore().get(0)).replace("Page ", ""));
                        this.setPhoneDefaults(inv);
                        int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                        Set<Head> heads = GTM.getShopManager().getNonExpiredHeadsByBid();
                        Iterator<Head> it = heads.iterator();
                        for (int i = 0; i < page * 20; i++) {
                            if (!it.hasNext())
                                break;
                            Head head = it.next();
                            if (i < (page - 1) * 20)
                                continue;
                            List<String> lore = new ArrayList<>();
                            lore.add(Utils.f("&7Seller: &a&l" + head.getSellerName()));
                            if (head.hasBid()) {
                                lore.add(Utils.f("&7Bidder: &a&l" + head.getBidderName()));
                                lore.add(Utils.f("&7Bid: &a$&l" + head.getBid()));
                            } else lore.add(Utils.f("&7Starting Bid: &a$&l10,000"));
                            lore.add(Utils.f("&7Click to bid!"));
                            lore.add(Utils.f("&7Time Left: &a&l" + Utils.timeInMillisToText(head.getTimeUntilExpiry())));
                            lore.add(Utils.f("&0" + head.getExpiry()));
                            inv.setItem(slots[i - (page - 1) * 20], new JLibItem.Builder().withType(Material.SKULL_ITEM).withDurability((short) 3).withName(Utils.f("&e&l" + head.getHead())).withLore(lore).withOwner(head.getHead()).build().getItemStack());
                        }
                        inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose", "&7Click to close this menu!"));
                        if (page > 1)
                            inv.setItem(48, Utils.createItem(Material.ARROW, "&e&lPrevious Page", "&7Page " + (page - 1)));
                        inv.setItem(49, Utils.createItem(Material.SKULL_ITEM, "&e&lHead Auction", "&7Buy and sell your souvenirs!"));
                        if (heads.size() > 20 * page)
                            inv.setItem(50, Utils.createItem(Material.ARROW, "&e&lNext Page", "&7Page " + (page + 1)));
                        inv.setItem(51, Utils.createItem(Material.PAPER, "&e&lAuction Head", "&7Click to auction a player head!"));
                        return;
                    }
                    case REDSTONE:
                        player.closeInventory();
                        return;
                    case PAPER:
                        player.closeInventory();
                        player.sendMessage(Lang.HEAD_AUCTION.f("&7Click me with the head in your hand to put it up for sale!"));
                        return;
                    case SKULL_ITEM:
                        try {
                            String owner = ((SkullMeta) item.getItemMeta()).getOwner();
                            String s = item.getItemMeta().hasLore() ? ChatColor.stripColor(item.getItemMeta().getLore().get(item.getItemMeta().getLore().size() - 1)) : null;
                            if (s == null) return;
                            Long expiry = Long.parseLong(s);
                            Head head = GTM.getShopManager().getHead(owner, expiry);
                            if (head == null) return;
                            user.setBiddingHead(head.getHead());
                            user.setCurrentChatAction(ChatAction.BIDDING_HEAD, 0);
                            user.setBiddingExpiry(head.getExpiry());
                            player.sendMessage(Lang.HEAD_AUCTION.f("&7The current bid for &e&l" + head.getHead() + "'s Head&7 is &a$&l" + (head.hasBid() ? head.getBid() : "10,000") + "&7! Type your bid or type &a\"quit\"&7 to cancel bidding."));
                            player.closeInventory();
                            return;
                        } catch (NumberFormatException ignored) {
                            return;
                        } catch (Exception e1) {
                            e1.printStackTrace();
                            return;
                        }
                    default:
                        return;
                }
            }
            case "auctionhead":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5: {
                                player.closeInventory();
                                GTM.getShopManager().auctionHead(player, GTM.getUserManager().getLoadedUser(uuid));
                                return;
                            }
                            case 14:
                                player.closeInventory();
                                return;
                            default:
                                return;
                        }

                    default:
                        return;
                }
            case "armorupgrade":
                switch (item.getType()) {
                    case STAINED_GLASS_PANE:
                        switch (item.getDurability()) {
                            case 5: {
                                player.closeInventory();
                                ArmorUpgrade upgrade = user.getBuyingArmorUpgrade();
                                if (upgrade == null) {
                                    return;
                                }
                                if (!upgrade.canUseUpgrade(user.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
                                    player.sendMessage(Lang.HEY.f("&7You need to rank up to " + upgrade.getGTMRank().getColoredNameBold() + "&7 or donate for " + upgrade.getUserRank().getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use the &b&l" + upgrade.getDisplayName() + " Armor Upgrade&7!"));
                                    return;
                                }
                                ItemStack i = player.getInventory().getItemInMainHand();
                                GameItem gameItem = item == null ? null : GTM.getItemManager().getItem(i.getType());
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
                            }
                            case 14:
                                player.closeInventory();
                                return;
                            default:
                                return;
                        }
                    default:
                        return;
                }
            case "lottery": {
                switch (item.getType()) {
                    case EMPTY_MAP:
                        int amnt = Integer.parseInt(ChatColor.stripColor(item.getItemMeta().getDisplayName())
                                .replace(" Tickets", ""));
                        if (!user.hasMoney(amnt * 500)) {
                            player.sendMessage(Lang.BANK.f("&7You don't have &c$&l" + (amnt * 500) + " &con you!"));
                            return;
                        }
                        user.takeMoney(amnt * 500);
                        LotteryPlayer p = GTM.getLottery().getLotteryPlayer(uuid);
                        if (p == null) {
                            p = new LotteryPlayer(uuid, player.getName());
                            GTM.getLottery().addLotteryPlayer(p);
                        }
                        p.addTickets(amnt);
                        GTMUtils.updateBoard(player, user);
                        player.sendMessage(Lang.LOTTERY.f("&7You bought &e&l" + amnt + " Tickets&7 for &a$&l" + (amnt * 500) + "&7!"));
                        player.closeInventory();
                        return;
                    case BOOK_AND_QUILL:
                        user.setCurrentChatAction(ChatAction.BUYING_LOTTERY_TICKETS, 0);
                        player.closeInventory();
                        player.sendMessage(Utils.f(Lang.BANK
                                + "&7Please type (in chat) the amount of tickets you would like to buy for &a$&l500&7 each, or type&a \"quit\"&7!"));
                        return;
                    case REDSTONE:
                        MenuManager.openMenu(player, "bank");
                        return;
                    default:
                        break;
                }
            }
        }

    }


    @EventHandler
    public void onMenuClose(MenuCloseEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        Menu menu = e.getMenu();
        switch (menu.getName()) {
            case "bountiesplace":
                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                if (user.getCurrentChatAction()==ChatAction.PICKING_BOUNTY || user.getCurrentChatAction()==ChatAction.PICKING_BOUNTY_TARGET)
                    return;
                if (user.getBountyAmount() > 0 || user.getBountyName() != null)
                    player.sendMessage(Utils.f(Lang.BOUNTIES + "&7You cancelled the bounty."));
                user.setBountyAmount(-1);
                user.setBountyName(null);
                user.setBountyUUID(null);
                return;
            default:
                break;
        }
    }
}
