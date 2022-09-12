package net.grandtheftmc.gtm;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.boards.Board;
import net.grandtheftmc.core.boards.BoardType;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.utils.ReflectionUtil;
import net.grandtheftmc.gtm.warps.Warp;
import net.grandtheftmc.guns.weapon.Weapon;

public final class GTMUtils {

    public static final String HEADER = Utils.f(
            " &7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀&a&l▄&7&l▀");
    public static final String FOOTER = Utils.f(
            " &7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄&a&l▀&7&l▄");

    private GTMUtils() {
    }

    public static GTMUser getGTMUser(Player player) {
        return GTM.getUserManager().getLoadedUser(player.getUniqueId());
    }

    public static User getUser(Player player) {
        return Core.getUserManager().getLoadedUser(player.getUniqueId());
    }

    public static UserRank getRank(Player player) {
        return Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
    }

    public static void updateBoard(Player player, GTMUser gtmUser) {
        updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), gtmUser);
    }

    public static void arrestPlayer(Cancellable e, Weapon weapon, Player player, Player victim) {
        UUID victimUUID = victim.getUniqueId();
        GTMUser victimGtmUser = GTM.getUserManager().getLoadedUser(victimUUID);
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        User victimUser = Core.getUserManager().getLoadedUser(victimUUID);
        if (gtmUser.getJobMode() != JobMode.COP){
            player.sendMessage(Lang.COP.f("&cYou cannot use this as you are not a cop!"));
            e.setCancelled(true);
            return;
        }
        if (victimGtmUser.getJobMode() == JobMode.COP) {
            e.setCancelled(true);
            player.sendMessage(Utils.f(Lang.HEY + "&cYou can't hit cops!"));
            return;
        }
        if (victimGtmUser.getJobMode() == JobMode.CRIMINAL && victimGtmUser.getWantedLevel() == 0) {
            player.sendMessage(Lang.HEY.f("&7You can't use this on citizens that are not wanted!"));
            e.setCancelled(true);
            return;
        }
        if (gtmUser.getJobMode() == JobMode.COP) {
            if (weapon.getCompactName().equalsIgnoreCase("nightstick")) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
            }
        }
        if (!"stungun".equalsIgnoreCase(weapon.getCompactName()) ||
                victimGtmUser.getJobMode() != JobMode.CRIMINAL || victimGtmUser.getWantedLevel() == 0) return;
        if (victim.getLastDamageCause() == null || victim.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.DRAGON_BREATH)
            return;
        ItemStack chestPlate = player.getInventory().getChestplate();
        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
            player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals during flight!"));
            return;
        }
        if(player.getVehicle() != null) {
            player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals while in a Vehicle!"));
            return;
        }
        int wantedLevel = victimGtmUser.getWantedLevel();
        int timeInJail = GTMUtils.getTimeInJail(wantedLevel);
        victimGtmUser.jail(timeInJail, player);
        player.sendMessage(Lang.COP_MODE.f("&7You arrested &a" + victimUser.getColoredName(victim)
                + "&7! He will go to jail for &a" + Utils.timeInSecondsToText(timeInJail) + "&7!"));
        Utils.broadcastExcept(player, Lang.COP_MODE.f("&a" + victimUser.getColoredName(victim) + "&7 was arrested by &a"
                + user.getColoredName(player) + "&7!"));
        victimGtmUser.addDeaths(1);
        victimGtmUser.setLastTag(-1);
        victimGtmUser.setKillCounter(0);
        victimGtmUser.setKillStreak(0);
        victimGtmUser.unsetCompassTarget(victim, victimUser);
        if (GTM.getWarpManager().cancelTaxi(victim, victimGtmUser))
            victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));
        victim.setHealth(victim.getMaxHealth());
        victim.spigot().respawn();
        victim.setFireTicks(0);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.setFlying(true);
        victim.getActivePotionEffects().clear();
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
        victim.setFoodLevel(20);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
        victim.setFlySpeed(0);
        GTMUtils.removeBoard(victim);
        double lostMoney = Utils.round(victimGtmUser.getMoney() / 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null)
                    return;
                User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                GTMUser victimGameUser = GTM.getUserManager().getLoadedUser(victim.getUniqueId());
                victim.sendMessage(Lang.JAIL.f("&7You were arrested and have to stay in jail for &a"
                        + Utils.timeInSecondsToText(timeInJail) + "&7!"));
                if (lostMoney > 0)
                    victim.sendMessage(Lang.MONEY_TAKE.f(String.valueOf(lostMoney)));
                victim.teleport(GTM.getWarpManager().getJail().getLocation());
                victim.setGameMode(GameMode.ADVENTURE);
                victim.getActivePotionEffects().clear();
                victim.setFoodLevel(20);
                victim.setFlying(false);
                victim.setFlySpeed(0.1F);
                GTMUtils.giveGameItems(victim);
                GTMUtils.updateBoard(victim, victimGameUser);
            }
        }.runTaskLater(GTM.getInstance(), 150);
        ItemStack[] contents = victim.getInventory().getContents();
        victim.getInventory().clear();
        Location loc = victim.getLocation();
        for (ItemStack item : contents)
            if (item != null && item.getType() != Material.WATCH && item.getType() != Material.COMPASS
                    && item.getType() != Material.CHEST)
                loc.getWorld().dropItemNaturally(loc, item);
        if (lostMoney > 0) {
            victimGtmUser.takeMoney(lostMoney);
            gtmUser.addMoney(lostMoney);
            player.sendMessage(Lang.MONEY.f("&7You confiscated &a$&l" + lostMoney + "&7 of &a"
                    + victimUser.getColoredName(victim) + "&7's money!"));
        }
        int copMoney = GTMUtils.getCopMoney(wantedLevel);
        gtmUser.addMoney(copMoney);
        player.sendMessage(Lang.COP_MODE.f("&7You were rewarded &a$&l" + copMoney + "&7 for arresting a player with &e"
                + GTMUtils.getWantedLevelStars(wantedLevel) + " (" + wantedLevel + ")&7!"));
        Utils.sendTitle(victim, "&c&lBUSTED", "&7Arrested by " + player.getName(), 80, 50, 20);
        GTMUtils.updateBoard(player, user, gtmUser);
    }

    public static void updateBoard(Player player, User user, GTMUser gtmUser) {
        if(!player.isOnline()) {
            return;
        }
        if (user == null || !user.getPref(Pref.USE_SCOREBOARD) || user.isInTutorial()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }
        Board board = new Board("gtm", "&7&l" + Core.getSettings().getServer_GTM_name(), BoardType.KEY_VALUE);
        board.addValue("a", "Money", Utils.formatMoney(gtmUser.getMoney()));
        board.addValue("a", "Bank", Utils.formatMoney(gtmUser.getBank()));
        board.addValue("e", Core.getSettings().getServer_GTM_shortName() + " Rank", gtmUser.getRank().getName());
        if (gtmUser.getJobMode() == JobMode.CRIMINAL) {
            int wantedLevel = gtmUser.getWantedLevel();
            board.addValue("c", "Wanted Level", getWantedLevelStars(wantedLevel) + " (" + wantedLevel + ')');
        } else
            board.addValue(gtmUser.getJobMode().getColorChar(), "Job Mode", gtmUser.getJobMode().getName());
        board.addValue("6", "Server IP", user.getServerJoinAddress() != null ? user.getServerJoinAddress() : Core.getSettings().getNetworkIP());
        board.updateFor(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
    }

    public static void removeBoard(Player player) {
        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
    }

    public static String getWantedLevelStars(int i) {
        String[] wantedLevels = new String[]{"✩✩✩✩✩", "✮✩✩✩✩", "✮✮✩✩✩", "✮✮✮✩✩", "✮✮✮✮✩", "✮✮✮✮✮"};
        return wantedLevels[i];
    }

    public static void giveGameItems(Player player) {
        ItemStack phone = Utils.createItem(Material.WATCH, "&7&lPhone");
        ItemStack compass = Utils.createItem(Material.COMPASS, "&7&lGPS");
        ItemStack bp = Utils.createItem(Material.CHEST, "&6&lBackpack");
        ItemStack ammoPouch = Utils.createItem(Material.CHEST, "&c&lAmmo Pouch");
        Inventory inv = player.getInventory();
        if (!inv.contains(Material.WATCH))
            if (inv.getItem(8) == null)
                inv.setItem(8, phone);
            else
                inv.addItem(phone);
        if (!inv.contains(Material.COMPASS))
            if(inv.getItem(7)==null)
                inv.setItem(7, compass);
            else
                inv.addItem(compass);
        inv.setItem(17, bp);
        inv.setItem(16, ammoPouch);

    }

    public static boolean isPhoneOrGPS(ItemStack item) {
        if (item == null)
            return false;
        ItemStack gps = Utils.createItem(Material.COMPASS, "&7&lGPS");
        ItemStack phone = Utils.createItem(Material.WATCH, "&7&lPhone");
        return item.equals(gps) || item.equals(phone);
    }

    public static String getMessageKilledBy(String name) {
        String[] msges = new String[]{"Shanked by " + name, "Rekt by " + name, "Killed by " + name,
                name + " killed you", name + " clapped yo ass", name + " killed you"};
        return msges[Utils.getRandom().nextInt(msges.length)];
    }

    public static void sendJoinMessage(Player p, User u) {
        p.sendMessage(new String[]{"", "", "", "", "", "", "", "", "", "", Utils.f(HEADER), "",
                Utils.fc("Welcome, " + u.getColoredName(p) + "&r to &7&l" + Core.getSettings().getServer_GTM_name() + "&r!"),
                Utils.fc("&e&l&oGTA in Minecraft!"), "", Utils.fc("&e&lSTORE &r&n" + Core.getSettings().getStoreLink()),
                Utils.fc("&a&lSITE          &r&n" + Core.getSettings().getWebsiteLink()), "", Utils.fc("&7Use &a/tutorial&7 to get started!"),
                "", Utils.f(FOOTER), ""});

    }

    public static List<GTMUser> getCops() {
        return GTMUserManager.getInstance().getUsers().stream().filter(user -> user.getJobMode() == JobMode.COP).collect(Collectors.toList());
    }

    public static Set<GTMUser> getCriminalsByWantedLevel(int minimumWantedLevel) {
        HashMap<GTMUser, Integer> unsortMap = new HashMap<>();
        GTMUserManager.getInstance().getUsers().stream().filter(u -> u.getJobMode() == JobMode.CRIMINAL && u.getWantedLevel() >= minimumWantedLevel
                && Objects.equals(Bukkit.getPlayer(u.getUUID()).getWorld().getName(), GTM.getSettings().getMap())).forEach(u -> unsortMap.put(u, u.getWantedLevel()));
        return sort(unsortMap).keySet();
    }

    public static Map<GTMUser, Integer> sort(Map<GTMUser, Integer> unsortMap) {
        List<Map.Entry<GTMUser, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Comparator.comparing(Map.Entry::getValue));
        Map<GTMUser, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<GTMUser, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static List<Player> getJailedPlayers() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            GTMUser gtmUser = GTM.getUserManager().getLoadedUser(p.getUniqueId());
            if (gtmUser.isArrested()) {
                players.add(p);
            }
        }
        return players;
    }

    public static String toBase64(ItemStack[] array) {
        if (array == null)
            return null;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream)) {

                dataOutput.writeInt(array.length);
                for (ItemStack stack : array)
                    dataOutput.writeObject(stack);
                dataOutput.close();
            }
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            GTM.getInstance().getLogger().log(Level.ALL, "Error while serializing items!");
            e.printStackTrace();
            return null;
        }
    }

    public static ItemStack[] fromBase64(String data) {
        if (data == null)
            return new ItemStack[0];
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            ItemStack[] array;
            try (BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream)) {
                array = new ItemStack[dataInput.readInt()];
                // Read the serialized inventory
                for (int i = 0; i < array.length; i++)
                    array[i] = (ItemStack) dataInput.readObject();

                dataInput.close();
            }
            return array;
        } catch (Exception e) {
            GTM.getInstance().getLogger().log(Level.ALL, "Error while deserializing items: " + data);
            e.printStackTrace();
            return new ItemStack[0];
        }
    }

    public static int getGangMembers(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 0;
            case VIP:
                return 2;
            case PREMIUM:
                return 5;
            case ELITE:
                return 10;
            case SPONSOR:
                return 15;
            default:
                return 20;
        }
    }

    public static String upperCaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
    }

    public static void chooseJobMode(Player player, User user, GTMUser gtmUser, JobMode mode) {
        if (mode == JobMode.CRIMINAL) {
            if (gtmUser.getJobMode() == JobMode.CRIMINAL) {
                player.sendMessage(Utils.f(Lang.JOBS + "&7You are already on the "
                        + JobMode.CRIMINAL.getColoredNameBold() + " Job&7!"));
                return;
            }
            player.sendMessage(Lang.JOBS.f("&7You have quit the " + gtmUser.getJobMode().getColoredNameBold() + "&7 Job!"));
            gtmUser.setJobMode(JobMode.CRIMINAL);
            gtmUser.unsetCompassTarget(player, user);
            updateBoard(player, user, gtmUser);
            NametagManager.updateNametag(player);
            GTMUtils.giveGameItems(player);
            return;
        }
        if (gtmUser.getJobMode() == JobMode.COP || gtmUser.getJobMode() == JobMode.HITMAN) {
            player.sendMessage(Utils.f(Lang.JOBS + "&7You are already on the "
                    + gtmUser.getJobMode().getColoredNameBold() + " Job&7!"));
            return;
        }
        if (!gtmUser.canSwitchJobMode(user.getUserRank())) {
            player.sendMessage(Utils.f(Lang.JOBS
                    + "&7Sorry, but you need to wait &a" + Utils.timeInMillisToText(gtmUser.getTimeUntilJobModeSwitch(user.getUserRank()))
                    + "&7 before switching Job Mode again!" + (user.getUserRank().isHigherThan(UserRank.SPONSOR) ? "" : " Buy a rank a &a&l" + Core.getSettings().getStoreLink() + "&7 to be able to switch faster!")));
            return;
        }
        if (!mode.canUse(gtmUser.getRank(), user.getUserRank())) {
            player.sendMessage(Lang.JOBS.f("&7You don't have access to " + mode.getColoredNameBold() + " Mode&7! Rank up to " + mode.getRank().getColoredNameBold() + "&7 or buy the " + mode.getUserRank().getColoredNameBold() + "&7 rank!"));
            return;
        }
        gtmUser.setJobMode(mode);
        if (mode == JobMode.COP)
            player.sendMessage(Utils.f(Lang.COP_MODE + "&7You are now in " + mode.getColoredNameBold()
                    + " Mode&7! You can earn money by killing wanted criminals."));
        else if (mode == JobMode.HITMAN) {
            player.sendMessage(Utils.f(Lang.HITMAN_MODE + "&7You are now in " + mode.getColoredNameBold() + " Mode&7! You can earn money by killing players that have a bounty on their head."));
        }
        GTM.getItemManager().giveKit(player, user, gtmUser, mode.getName());
        gtmUser.unsetCompassTarget(player, user);
        gtmUser.setKillCounter(0);
        updateBoard(player, user, gtmUser);
        NametagManager.updateNametag(player);
        GTMUtils.giveGameItems(player);
    }

    public static int getTimeInJail(int wl) { // IN SECONDS
        return new int[]{0, 60, 120, 180, 300, 400}[wl];
    }

    public static int getCopMoney(int wl) {
        return new int[]{0, 2000, 5000, 10000, 20000, 50000}[wl];
    }

    public static int getJobModeDelay(UserRank rank) { // IN SECONDS
        switch (rank) {
            case DEFAULT:
                return 9000;
            case VIP:
                return 7200;
            case PREMIUM:
                return 5400;
            case ELITE:
                return 3600;
            case SPONSOR:
                return 2700;
            default:
                return 1800;
        }
    }

    public static boolean isArmor(Material material) {
        String s = material.toString();
        return s.contains("BOOTS") || s.contains("LEGGINGS") || s.contains("CHESTPLATE") || s.contains("HELMET");
    }

    public static int getFeedDelay(UserRank rank) {// IN SECONDS
        switch (rank) {
            case DEFAULT:
                return 1800;
            case VIP:
                return 900;
            case PREMIUM:
                return 600;
            case ELITE:
                return 300;
            case SPONSOR:
                return 180;
            default:
                return 120;
        }

    }//testrtasdfadfasdfes
    private final static List<String> DEFAULT_ITEMS = new ArrayList<>(Arrays.asList("Phone", "Backpack", "Ammo Pouch"));
    public static boolean isDefaultPlayerItem(ItemStack is) {
        return is!=null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && DEFAULT_ITEMS.contains(ChatColor.stripColor(is.getItemMeta().getDisplayName()));
    }

    public static int getFixHandDelay(UserRank rank) {// IN SECONDS
        switch (rank) {
            case DEFAULT:
                return 60*60;
            case VIP:
                return 60*30;
            case PREMIUM:
                return 60*20;
            case ELITE:
                return 60*15;
            case SPONSOR:
                return 60*10;
            case SUPREME:
                return 60*5;
            default:
                return 60*5;
        }

    }
    public static int getFixAllDelay(UserRank rank) {// IN SECONDS
        switch (rank) {
            default:
                return 60*60*3;
        }

    }

    public static int getBackpackRows(UserRank rank) {
        switch (rank) {
            case DEFAULT:
                return 2;
            case VIP:
                return 3;
            case PREMIUM:
                return 5;
            case ELITE:
                return 7;
            case SPONSOR:
                return 9;
            default:
                return 11;
        }
    }

    public static int getCompassRadius(UserRank rank) {
        switch (rank) {
            case DEFAULT:
            case VIP:
                return 30;
            case PREMIUM:
                return 50;
            case ELITE:
                return 75;
            case SPONSOR:
                return 100;
            default:
                return 10;
        }
    }

    public static int getExtraCompassAccuracy(UserRank rank) {
        switch (rank) {
            case DEFAULT:
            case VIP:
                return 0;
            case PREMIUM:
                return 15;
            case ELITE:
                return 33;
            case SPONSOR:
                return 50;
            default:
                return 67;
        }
    }

    public static int getStartingMoney(UserRank rank) {
        switch (rank) {
            case DEFAULT:
                return 5000;
            case VIP:
                return 100000;
            case PREMIUM:
                return 250000;
            case ELITE:
                return 500000;
            case SPONSOR:
                return 1000000;
            default:
                return 2000000;
        }
    }

    public static int getStartingPermits(UserRank rank) {
        switch (rank) {
            case DEFAULT:
            case VIP:
            case PREMIUM:
            case ELITE:
                return 0;
            case SPONSOR:
                return 5;
            default:
                return 10;
        }
    }


    public static int getHouses(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 0;
            case VIP:
                return 1;
            case PREMIUM:
                return 2;
            case ELITE:
                return 3;
            case SPONSOR:
                return 5;
            default:
                return 10;
        }
    }

    public static int getFreeLotteryTickets(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 0;
            case VIP:
                return 1;
            case PREMIUM:
                return 2;
            case ELITE:
                return 3;
            case SPONSOR:
                return 5;
            default:
                return 10;
        }
    }

    public static int getWarpDelay(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 15;
            case VIP:
                return 12;
            case PREMIUM:
                return 10;
            case ELITE:
                return 8;
            case SPONSOR:
                return 6;
            default:
                return 5;
        }
    }

    public static int getNearRange(UserRank rank) {
        switch (rank) {
            case VIP:
                return 50;
            case PREMIUM:
                return 75;
            case ELITE:
                return 100;
            case SPONSOR:
                return 125;
            case SUPREME:
                return 150;
            default:
                return 100;
        }
    }

    public static int getStackDelay(UserRank rank) {
        switch (rank) {
            case SUPREME:
                return 3600;
            case HELPOP:
            case MOD:
            case SRMOD:
            case ADMIN:
                return 300;
            case DEV:
            case MANAGER:
            case OWNER:
                return 1;
            default:
                return 3600;
        }
    }

    /*
     * public static String serialize(ItemStack[] a) { StringBuilder b = new
     * StringBuilder(); for (int i = 0; i < a.length; i++) { if (i > 0)
     * b.append(","); ItemStack item = a[i]; try {
     * b.append(StreamSerializer.getDefault().serializeItemStack(item)); } catch
     * (Exception e) { b.append("null"); System.out.println(
     * "Error while serializing an item (" + i + "): " +
     * e.getCause().getMessage()); } } return b.toString(); }
     *
     * public static ItemStack[] deserialize(String s) { if (s == null ||
     * s.length() == 0) return new ItemStack[0]; String[] a = s.split(",");
     * ItemStack[] array = new ItemStack[a.length]; for (int i = 0; i <
     * a.length; i++) { try { array[i] =
     * StreamSerializer.getDefault().deserializeItemStack(a[i]); } catch
     * (Exception e) { array[i] = null; System.out.println(
     * "Error while deserializing an item (" + i + "): " + e.getMessage()); } }
     * return array; }
     */

    public static boolean isValidURL(String string) {
        String urlRegex = "^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$";
        Pattern p = Pattern.compile(urlRegex);
        Matcher m = p.matcher(string);
        return m.find();
    }

    public static double getCrossProduct(LivingEntity livingEntity, Location target) {
        if (livingEntity.getWorld() != target.getWorld()) return 10000;
        Location head = livingEntity.getLocation();
        org.bukkit.util.Vector look = livingEntity.getLocation().getDirection().normalize();
        org.bukkit.util.Vector direction = head.subtract(target).toVector().normalize();
        org.bukkit.util.Vector cp = direction.crossProduct(look);
        return cp.length();
    }

    public static String serializeLocation(Location location) {
        String world = location.getWorld().getName();
        String x = String.valueOf(location.getX());
        String y = String.valueOf(location.getY());
        String z = String.valueOf(location.getZ());
        String yaw = String.valueOf(location.getYaw());
        String pitch = String.valueOf(location.getPitch());
        return world + '@' + x + '@' + y + '@' + z + '@' + yaw + '@' + pitch;
    }

    public static Optional<Location> deserializeLocation(String loc) {
        String[] args = loc.split("@");
        World world = Bukkit.getWorld(args[0]);
        double x = Double.valueOf(args[1]);
        double y = Double.valueOf(args[2]);
        double z = Double.valueOf(args[3]);
        float yaw = Float.valueOf(args[4]);
        float pitch = Float.valueOf(args[5]);
        Location location = new Location(world, x, y, z, yaw, pitch);
        return Optional.ofNullable(location);
    }

    public static String convertItemStackToJson(ItemStack itemStack) {
        Class<?> craftItemStackClazz = ReflectionUtil.getOBCClass("inventory.CraftItemStack");
        Method asNMSCopyMethod = ReflectionUtil.getMethod(craftItemStackClazz, "asNMSCopy", ItemStack.class);
        Class<?> nmsItemStackClazz = ReflectionUtil.getNMSClass("ItemStack");
        Class<?> nbtTagCompoundClazz = ReflectionUtil.getNMSClass("NBTTagCompound");
        Method saveNmsItemStackMethod = ReflectionUtil.getMethod(nmsItemStackClazz, "save", nbtTagCompoundClazz);

        Object nmsNbtTagCompoundObj;
        Object nmsItemStackObj;
        Object itemAsJsonObject;

        try {
            nmsNbtTagCompoundObj = nbtTagCompoundClazz.newInstance();
            nmsItemStackObj = asNMSCopyMethod.invoke(null, itemStack);
            itemAsJsonObject = saveNmsItemStackMethod.invoke(nmsItemStackObj, nmsNbtTagCompoundObj);
        } catch (Throwable t) {
            return null;
        }

        return itemAsJsonObject.toString();
    }


    public static void moneylog(Player sender, Player target, double amount) {
        if (Core.getSettings().isSister()) return; //Throwing errors..

        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("gtmlogs/moneylog_" + fileName + ".txt");
        try {
            if (!file.isFile() || !file.exists())
                file.createNewFile();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        String date = new SimpleDateFormat("MM/dd/yy - h:mm a").format(new Date());
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file, true))) {
            bufferedWriter.write(date + " - " + sender.getName() + " sent " + formatter.format(amount) + " to " + target.getName() + '\n');
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void log(String logName, String msg) {
        if (Core.getSettings().isSister()) return; //Throwing errors..

        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("gtmlogs/" + logName + '_' + fileName + ".txt");
        try {
            if (!file.isFile() || !file.exists()) {
                file.createNewFile();
            }
            String date = new SimpleDateFormat("MM/dd/yy - h:mm a").format(new Date());
            FileWriter fileWriter = new FileWriter(file, true);
            try (BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
                bufferedWriter.write(date + " - " + msg + '\n');
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static GameItem getRandomGameItem() {
        List<GameItem> gameItems = GTM.getItemManager().getItems().stream().filter(gameItem -> gameItem.getType() == GameItem.ItemType.ITEMSTACK || gameItem.getType() == GameItem.ItemType.WEAPON).collect(Collectors.toList());
        return gameItems.get(ThreadLocalRandom.current().nextInt(gameItems.size()));
    }

    public static Warp getNearestWarp(Location location) {
        Warp nearestWarp = null;
        for (Warp warp : GTM.getWarpManager().getWarps()) {
            if (warp.getLocation().getWorld() != location.getWorld()) continue;
            if (nearestWarp == null ||
                    warp.getLocation().distance(location) < nearestWarp.getLocation().distance(location)) {
                nearestWarp = warp;
            }
        }
        return nearestWarp;
    }

    public static Map<String, Integer> getTopKillers(int count) {
        Map<String, Integer> topKillers = new HashMap<>();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " WHERE name != 'ERROR' ORDER BY kills DESC LIMIT " + count + ';')) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        topKillers.put(result.getString("name"), result.getInt("kills"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topKillers;
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        return map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static void spawnTinyArmorStand(Location location, String username, String title) {
        ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        armorStand.setSmall(true);
        armorStand.setBasePlate(false);
        ItemStack skull = Utils.createItem(Material.SKULL_ITEM, 3, Utils.f(username));
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        meta.setOwner(username);
        skull.setItemMeta(meta);
        armorStand.setHelmet(skull);
        armorStand.setCustomNameVisible(true);
        armorStand.setCustomName(Utils.f(title));
    }

    public static void sendGlow(Player player, Player target, long time) {
        PacketContainer packet = GTM.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        watcher.setObject(0, serializer, (byte) 0x40);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            GTM.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                removeGlow(player, target);
            }
        }.runTaskLaterAsynchronously(GTM.getInstance(), time);
    }

    public static void removeGlow(Player player, Player target) {
        PacketContainer packet = GTM.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        watcher.setObject(0, serializer, (byte) 0x0);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            GTM.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static Month getMonth() {
        return LocalDate.now(ZoneId.systemDefault()).getMonth();

    }

    public static int getDay() {
        return LocalDate.now(ZoneId.systemDefault()).getDayOfMonth();
    }

    public static ChatColor randomColor() {
        int a = ThreadLocalRandom.current().nextInt(16);
        switch (a) {
            case 1:
                return ChatColor.GREEN;
            case 2:
                return ChatColor.DARK_GREEN;
            case 3:
                return ChatColor.BLUE;
            case 4:
                return ChatColor.DARK_RED;
            case 5:
                return ChatColor.DARK_PURPLE;
            case 6:
                return ChatColor.GOLD;
            case 7:
                return ChatColor.GRAY;
            case 8:
                return ChatColor.DARK_GRAY;
            case 9:
                return ChatColor.DARK_BLUE;
            case 10:
                return ChatColor.GREEN;
            case 11:
                return ChatColor.AQUA;
            case 12:
                return ChatColor.RED;
            case 13:
                return ChatColor.LIGHT_PURPLE;
            case 14:
                return ChatColor.YELLOW;
            default:
                return ChatColor.AQUA;
        }
    }
}
