package net.grandtheftmc.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.j0ach1mmall3.jlib.inventory.CustomEnchantment;
import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import com.j0ach1mmall3.jlib.player.JLibPlayer;

import net.grandtheftmc.core.boards.Board;
import net.grandtheftmc.core.boards.BoardType;
import net.grandtheftmc.core.database.dao.LogDAO;
import net.grandtheftmc.core.database.dao.ServerInfoDAO;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.DefaultFontInfo;
import net.grandtheftmc.core.util.TimeFormatter;
import net.grandtheftmc.core.util.Title;

public final class Utils {
    private static final Random RANDOM = new Random();
    private static final List<String> DEBUGGERS = Arrays.asList("Presidentx", "j0ach1mmall3", "Duci13", "KwonShiYun");
    private static final Class BLOCK_POSITION_CLASS = ReflectionAPI.getNmsClass("BlockPosition");
    private static final Class TILE_ENTITY_CHEST_CLASS = ReflectionAPI.getNmsClass("TileEntityChest");
    private static final Class PACKET_PLAY_OUT_BLOCK_ACTION_CLASS = ReflectionAPI.getNmsClass("PacketPlayOutBlockAction");
    private static final Class BLOCK_CLASS = ReflectionAPI.getNmsClass("Block");
    private static final Enchantment GLOW;

    static {
        CustomEnchantment customEnchantment = new CustomEnchantment("GTMCore_Glow", new ArrayList<>(), null, 0, 1);
        customEnchantment.register();
        GLOW = customEnchantment.getEnchantment();
    }

    private Utils() {
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean returnTrue() {
        return true;
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static void broadcast(String string) {
        Bukkit.broadcastMessage(f(string));
    }

    public static void broadcastExcept(Player player, String string) {
        Bukkit.getOnlinePlayers().stream().filter(p -> !p.equals(player)).forEach(p -> p.sendMessage(Utils.f(string)));
    }

    public static void m(int i) {
        b(String.valueOf(i));
    }

    public static void b(String string) {
        DEBUGGERS.stream().map(Bukkit::getPlayer).filter(p -> p != null).forEach(p -> p.sendMessage(string));
        Core.log(string);
    }

    /**
     * @param player         The player whose line of sight we will use
     * @param targetLocation The target location that is converted to a vector to form an angle with line of sight
     * @return angle between entity's line of sight and the target vector
     */
    public static double getAngleBetweenVectors(Player player, Location targetLocation) {
        Vector lineOfSight = player.getEyeLocation().toVector();
        Vector target = targetLocation.toVector();
        target.setY(0);
        lineOfSight.setY(0);
        return lineOfSight.angle(target);
    }

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
        if (loc1 == null || loc2 == null) return null;
        List<Block> blocks = new ArrayList<>();

        int topBlockX = loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();
        int bottomBlockX = loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX();

        int topBlockY = loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY();
        int bottomBlockY = loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY();

        int topBlockZ = loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();
        int bottomBlockZ = loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ();

        for (int x = bottomBlockX; x <= topBlockX; x++) {
            for (int z = bottomBlockZ; z <= topBlockZ; z++) {
                for (int y = bottomBlockY; y <= topBlockY; y++) {
                    blocks.add(loc1.getWorld().getBlockAt(x, y, z));
                }
            }
        }

        return blocks;
    }

    public static Location getCenterOfBlock(Location loc) {
        return loc.add(0.5, 0, 0.5);
    }

    public static Location getCenterOfTwoBlocks(Location loc1, Location loc2) {
        loc1 = getCenterOfBlock(loc1);
        loc2 = getCenterOfBlock(loc2);
        return new Location(loc1.getWorld(), (loc1.getX() + loc2.getX()) / 2, loc1.getY(), (loc1.getZ() + loc2.getZ()) / 2);
    }

    public static Block getSecondHalfChest(Block block) {
        Optional<Block> possibleChest = Stream.of(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST).map(block::getRelative).filter(b -> b.getType() == Material.CHEST || b.getType() == Material.TRAPPED_CHEST).findFirst();
        return possibleChest.isPresent() ? possibleChest.get() : null;
    }

    public static void playDoorAnimation(Location location, boolean open) {
        playDoorAnimation(location.getBlock().getState(), open);
    }

    public static void playDoorAnimation(Player player, Location location, boolean open) {
        playDoorAnimation(player, location.getBlock().getState(), open);
    }

    public static void playDoorAnimation(BlockState state, boolean open) {
        Bukkit.getOnlinePlayers().forEach(p -> playDoorAnimation(p, state, open));
    }

    @SuppressWarnings("deprecation")
    public static void playDoorAnimation(Player player, BlockState state, boolean open) {
        byte data = state.getRawData();
        byte b = data < 4 ? open ? (byte) (data + 4) : data : open ? data : (byte) (data - 4);
        player.sendBlockChange(state.getLocation(), Material.IRON_DOOR_BLOCK, b);
    }

    public static void playIronDoorAnimation(Player player, Location loc, boolean open) {
        // TODO
    }

    public static boolean putItemInInventoryRandomly(Inventory inv, ItemStack item) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            if (inv.getItem(i) == null) list.add(i);
        }

        if (list.isEmpty()) return false;
        int i = list.get(RANDOM.nextInt(list.size()));
        inv.setItem(i, item);
        return true;
    }

    public static String f(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String fColor(String string) {
        for (Character c : new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}) {
            if (string.contains("&" + c)) string = string.replace("&" + c, ChatColor.getByChar(c).toString());
        }

        return string;
    }

    public static net.md_5.bungee.api.ChatColor getLastColor(String string) {
        net.md_5.bungee.api.ChatColor chatColor = null;
        for (Character c : new Character[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'}) {
            if (string.contains("&" + c)) {
                chatColor = net.md_5.bungee.api.ChatColor.getByChar(c);
            }
        }
        return chatColor;
    }

    public static String deFormat(String string) {
        return string.replace(ChatColor.COLOR_CHAR, '&');
    }

    public static String[] f(String[] array) {
        if (array == null) return null;
        for (int i = 0; i < array.length; i++) {
            array[i] = Utils.f(array[i]);
        }

        return array;
    }

    public static String[] deFormat(String[] array) {
        if (array == null) return null;
        for (int i = 0; i < array.length; i++) {
            array[i] = Utils.deFormat(array[i]);
        }

        return array;
    }

    public static Location blockLocationFromString(String string) {
        if (string == null) return null;
        String[] coords = string.split(",");
        if (coords.length != 4) return null;
        World world = Bukkit.getWorld(coords[0]);
        if (world == null) return null;
        int x;
        int y;
        int z;
        try {
            x = Integer.parseInt(coords[1]);
            y = Integer.parseInt(coords[2]);
            z = Integer.parseInt(coords[3]);
        } catch (NumberFormatException e) {
            return null;
        }

        return new Location(world, x, y, z);

    }

    public static double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public static float round(float value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).floatValue();
    }

    public static String blockLocationToString(Location loc) {
        if (loc == null) return null;
        return loc.getWorld().getName() + ',' + loc.getBlockX() + ',' + loc.getBlockY() + ',' + loc.getBlockZ();
    }

    public static Location teleportLocationFromString(String string) {
        if (string == null) return null;
        String[] coords = string.split(",");
        if (coords.length != 6) return null;
        World world = Bukkit.getWorld(coords[0]);
        double x;
        double y;
        double z;
        float pitch;
        float yaw;
        try {
            x = round(Double.parseDouble(coords[1]));
            y = round(Double.parseDouble(coords[2]));
            z = round(Double.parseDouble(coords[3]));
            pitch = round(Float.parseFloat(coords[4]));
            yaw = round(Float.parseFloat(coords[5]));
        } catch (NumberFormatException e) {
            return null;
        }

        Location location = new Location(world, x, y, z);
        location.setPitch(pitch);
        location.setYaw(yaw);
        return location;
    }

    public static String teleportLocationToString(Location loc) {
        if (loc == null) return null;
        return loc.getWorld().getName() + ',' + round(loc.getX()) + ',' + round(loc.getY()) + ',' + round(loc.getZ()) + ',' + round(loc.getPitch()) + ',' + round(loc.getYaw());
    }

    public static void giveLobbyItems(Player player) {
        if (player == null) return;
        player.setHealth(20);
        player.setMaxHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        PlayerInventory inv = player.getInventory();
        inv.clear();
        player.getInventory().setHeldItemSlot(4);

        inv.setItem(0, createItem(Material.COMPASS, "&e&lServer Warper      &7&lRight Click"));
        inv.setItem(4, createItem(Material.ENDER_CHEST, "&6&lCosmetics            &7&lRight Click"));
        // inv.setItem(6, createItem(Material.NETHER_STAR, "&d&lStats                 &7&lRight Click"));
        inv.setItem(7, createItem(Material.EXP_BOTTLE, "&a&lRewards             &7&lRight Click"));
        inv.setItem(8, createItem(Material.REDSTONE_COMPARATOR, "&5&lPreferences        &7&lRight Click"));

        player.getActivePotionEffects().clear();
        inv.setArmorContents(null);
        player.updateInventory();

    }

    public static void sendLobbyJoinMessage(Player p, User user) {
        p.sendMessage(new String[]{"", "", "", "", "", "", "", "", ""});
        String[] header = Core.getAnnouncer().getHeader();
        if (header != null && header.length > 0) ;
        p.sendMessage(f(Core.getAnnouncer().getHeader()));
        p.sendMessage(new String[]{"",
                Utils.fc("Welcome, " + user.getColoredName(p) + "&r to the &7&l" + Core.getSettings().getNetworkShortName() + " &6&lHub&r!"),
                Utils.fc("&e&l&oGTA in Minecraft!"), "", Utils.fc("&e&lSTORE &r&n" + Core.getSettings().getStoreLink()),
                Utils.fc("&a&lSITE         &r&n" + Core.getSettings().getWebsiteLink()), "", Utils.fc("&7Use the &eserver warper&7 to play!")});
        String[] footer = Core.getAnnouncer().getFooter();
        if (footer != null && footer.length > 0) ;
        p.sendMessage(f(Core.getAnnouncer().getFooter()));
    }

    public static void setInvisible(Player player, boolean b) {
        Bukkit.getOnlinePlayers().stream().filter(p -> !Objects.equals(player, p)).forEach(p -> {
            if (b)
                p.hidePlayer(player);
            else
                p.showPlayer(player);
        });
    }

    public static List<String> f(List<String> lore) {
        return lore.stream().map(Utils::f).collect(Collectors.toList());
    }

    public static ItemStack createItem(Material material, String name, int amnt, String... lore) {
        return createItem(material, name, toList(lore), amnt);
    }

    public static ItemStack createItem(Material material, String name, List<String> lore, int amnt) {
        ItemStack item = new ItemStack(material);
        if (amnt > 0)
            item.setAmount(amnt);
        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            if (lore != null)
                meta.setLore(f(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, int durability, String name, String... lore) {
        return createItem(material, durability, name, toList(lore));
    }

    public static ItemStack createItem(Material material, int durability, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        if (durability > 0)
            item.setDurability((short) durability);
        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            if (lore != null)
                meta.setLore(f(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, int durability, String name, int amnt, String... lore) {
        return createItem(material, durability, name, toList(lore), amnt);
    }

    public static ItemStack createItem(Material material, int durability, String name, List<String> lore, int amnt) {
        ItemStack item = new ItemStack(material);
        if (amnt > 0)
            item.setAmount(amnt);
        if (durability > 0)
            item.setDurability((short) durability);
        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            if (lore != null)
                meta.setLore(f(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, String name, String... lore) {
        return createItem(material, name, toList(lore));
    }

    public static ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);

        if (name != null || lore != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            if (lore != null)
                meta.setLore(f(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        if (name != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack createItem(Material material, int durability, String name) {
        ItemStack item = new ItemStack(material);
        if (durability > 0)
            item.setDurability((short) durability);
        if (name != null) {
            ItemMeta meta = item.getItemMeta();
            if (name != null)
                meta.setDisplayName(Utils.f(name));
            item.setItemMeta(meta);
        }
        return item;
    }

    public static ItemStack setArmorColor(ItemStack item, int red, int green, int blue) {
        return setArmorColor(item, Color.fromRGB(red, green, blue));
    }

    public static ItemStack setArmorColor(ItemStack item, Color color) {
        if (!(item.getItemMeta() instanceof LeatherArmorMeta))
            return item;
        LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
        meta.setColor(color);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack setSkullOwner(ItemStack item, String owner) {
        item.setDurability((short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwner(owner);
        item.setItemMeta(meta);
        return item;
    }

    public static void hidePlayersTo(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(p -> !Objects.equals(p, player)).forEach(player::hidePlayer);
    }

    public static void showPlayersTo(Player player) {
        Bukkit.getOnlinePlayers().stream().filter(p -> !Objects.equals(p, player)).forEach(player::showPlayer);
    }

    public static void sendTitle(Player player, Title title) {
        sendTitle(player, title.getTitle(), title.getSubtitle(), title.getFadeIn(), title.getStay(),
                title.getFadeOut());
    }

    public static void sendTitle(Title title) {
        sendTitle(title.getTitle(), title.getSubtitle(), title.getFadeIn(), title.getStay(), title.getFadeOut());
    }

    public static void sendTitle(String title, String subTitle, int fadeIn, int stay, int fadeOut) {
        for (Player p : Bukkit.getOnlinePlayers())
            sendTitle(p, title, subTitle, fadeIn, stay, fadeOut);
    }

    public static void sendTitle(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        new JLibPlayer(player).sendTitle(fadeIn, fadeOut, stay, Utils.f(title));
        if (subtitle != null)
            new JLibPlayer(player).sendSubTitle(fadeIn, fadeOut, stay, Utils.f(subtitle));
    }

    public static void sendActionBar(Player pl, String msg) {
        //new JLibPlayer(pl).sendActionBar(msg);
        Utils.sendActionBar(pl, msg);
    }

    public static void sendActionBar(String msg) {
//        for (Player p : Bukkit.getOnlinePlayers())
//            sendActionBar(p, msg);
    	Utils.sendActionBar(msg);
    }

    public static void updateHubScoreboard(Player player, User user) {
        String rank = "No Rank";
        if (user.isSpecial())
            rank = user.getUserRank().getColoredNameBold();
        Board board = new Board("lobby", Core.getSettings().getType().getScoreboardHeader(), BoardType.KEY_VALUE);
        board.addValue("a", "Bucks", String.valueOf(user.getBucks()));
        board.addValue("e", "Tokens", String.valueOf(user.getTokens()));
        board.addValue("6", "Rank", rank);
        board.addValue("6", "Server IP", user.getServerJoinAddress() != null ? user.getServerJoinAddress() : Core.getSettings().getNetworkIP());
        board.updateFor(player, user);
    }

    public static List<String> toList(String[] array) {
        List<String> ls = new ArrayList<>();
        if (array == null)
            return ls;
        Collections.addAll(ls, array);
        return ls;
    }

    public static ItemStack[] toArray(List<ItemStack> list) {
        ItemStack[] items = new ItemStack[list.size()];
        for (int i = 0; i < list.size(); i++)
            items[i] = list.get(i);
        return items;
    }

    public static String[] stringsToArray(List<String> list) {
        if (list == null)
            return null;
        String[] l = new String[list.size()];
        l = list.toArray(l);
        return l;
    }

    public static boolean calculateChance(double i) {
        return ThreadLocalRandom.current().nextDouble(101) <= i;
    }

    public static void sendToServer(Player player, String server) {
        Core.getServerManager().sendToServer(player, server);
    }

    public static void stopEntityTracking(Player player) {
        player.getLocation().getWorld().getLivingEntities().stream().filter(e -> e instanceof Creature).forEach(e -> {
            Creature c = (Creature) e;
            if (c.getTarget() instanceof Player && c.getTarget().equals(player))
                c.setTarget(null);
        });
    }

    public static void setFlyMode(Player player, boolean b) {
        player.setAllowFlight(b);
        player.setFlying(b);
    }

    public static Location randomLocation(Location location, double d) {
        double x = location.getX() + (RANDOM.nextDouble() * d) - (d / 2);
        double z = location.getZ() + (RANDOM.nextDouble() * d) - (d / 2);
        return new Location(location.getWorld(), x, location.getY(), z, location.getYaw(), location.getPitch());
    }

    public static int randomNumber(int start, int end) {
        return RANDOM.nextInt(end - start + 1) + start;
    }

    public static int randomNumber(int end) {
        return RANDOM.nextInt(end + 1);
    }

    public static void copyWorld(String name) {
        try {
            Core.log("Copying world " + "/home/mcservers/development/master/maps/" + name + " to " + Bukkit.getWorldContainer().getCanonicalPath() + '/' + name);
            File source = new File("/home/mcservers/development/master/maps/" + name);
            File dest = new File(name);
            Files.delete(dest.toPath());
            Files.copy(source.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            Core.log("Finished copying world " + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static World loadWorld(String name) {
        copyWorld(name);
        World world = Bukkit.getWorld(name);
        if (world == null) {
            WorldCreator creator = new WorldCreator(name);
            creator.environment(World.Environment.NORMAL);
            world = creator.createWorld();
        }
        return world;
    }

    public static YamlConfiguration loadConfigFile(File file) {
        YamlConfiguration c = new YamlConfiguration();
        try {
            if (!file.exists())
                file.createNewFile();
            c.load(file);
        } catch (IOException | InvalidConfigurationException e1) {
            e1.printStackTrace();
        }
        return c;
    }

    public static YamlConfiguration loadConfig(String src) {
        return loadConfigFile(new File(src + ".yml"));
    }

    public static YamlConfiguration loadConfigFromMaps(String src) {
        return loadConfigFile(new File("/home/mcservers/development/master/maps/" + src + ".yml"));
    }

    public static YamlConfiguration loadConfigFromMaster(String src) {
        return loadConfigFile(new File("/home/mcservers/development/master/" + src + ".yml"));
    }

    public static YamlConfiguration loadConfigFromPlugin(String src, String plugin) {
        try {
            return loadConfigFile(new File(Bukkit.getWorldContainer().getCanonicalPath() + '/' + plugin, src + ".yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveConfigFile(YamlConfiguration c, File file) {
        try {
            if (!file.exists())
                file.createNewFile();
            c.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(YamlConfiguration c, String src) {
        saveConfigFile(c, new File(src + ".yml"));
    }

    public static void deletePlayerDatFiles(String world) {
        File file = new File(Bukkit.getWorldContainer() + "/" + world + "/playerdata");
        if (file.isDirectory())
            for (File f : file.listFiles())
                f.delete();
    }

    public static void kaching(Player player) {
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_DOOR_CLOSE, 2, 1);
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null)
                    player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 0.3F, 1);

            }
        }.runTaskLater(Core.getInstance(), 5);

    }

    public static String timeInMillisToText(Long millis) {
        return timeInSecondsToText(millis / 1000);
    }


    public static String formatMoney(double money) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(money > 1000000 ? (money / 1000000) : money) + (money > 1000000 ? " mil" : "");
    }

    public static String numberFormat(int num) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(num);
    }

    public static String numberFormat(double num) {
        return NumberFormat.getCurrencyInstance(Locale.US).format(num);
    }

    public static int getAmountInInv(Player player, ItemStack stack) {
        int amnt = 0;
        Inventory inventory = player.getInventory();
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.isSimilar(stack))
                amnt += item.getAmount();
        }
        return amnt;
    }

    public static boolean giveItems(Player player, ItemStack... items) {
        Map<Integer, ItemStack> hm = player.getInventory().addItem(items);
        if (hm.isEmpty())
            return false;
        World w = player.getWorld();
        Location loc = player.getLocation();
        for (ItemStack item : hm.values())
            w.dropItemNaturally(loc, item);
        return true;
    }

    public static void takeItems(Player player, int amount, ItemStack stack) {
        int toRemove = amount;
        Inventory inventory = player.getInventory();
        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.isSimilar(stack)) {
                if (toRemove >= item.getAmount()) {
                    toRemove -= item.getAmount();
                    inventory.setItem(i, null);
                } else {
                    item.setAmount(item.getAmount() - toRemove);
                    return;
                }

            }
        }
    }

    public static String fc(String s) {
        s = Utils.f(s);
        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : s.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = 154 - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(' ');
            compensated += spaceLength;
        }
        return sb + s;
    }

    public static String[] fc(String[] array) {
        if (array == null)
            return null;
        String[] a = new String[array.length];
        for (int i = 0; i < array.length; i++)
            a[i] = Utils.fc(array[i]);
        return a;
    }

    public static Map<Player, Double> sort(Map<Player, Double> unsortMap) {
        List<Map.Entry<Player, Double>> list = new LinkedList<>(unsortMap.entrySet());
        Collections.sort(list, (o1, o2) -> o1.getValue().compareTo(o2.getValue()));
        Map<Player, Double> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<Player, Double> entry : list)
            sortedMap.put(entry.getKey(), entry.getValue());
        return sortedMap;
    }

    public static void clearPotionEffects(Player player) {
        for (PotionEffect e : player.getActivePotionEffects())
            player.removePotionEffect(e.getType());
    }

    public static String getCardinalDirection(Location location) {
        double rotation = location.getYaw() % 360;
        if (rotation < 0)
            rotation += 360.0;
        if (rotation >= 0 && rotation < 22.5)
            return "S";
        else if (rotation >= 22.5 && rotation < 67.5)
            return "SW";
        else if (rotation >= 67.5 && rotation < 112.5)
            return "W";
        else if (rotation >= 112.5 && rotation < 157.5)
            return "NW";
        else if (rotation >= 157.5 && rotation < 202.5)
            return "N";
        else if (rotation >= 202.5 && rotation < 247.5)
            return "NE";
        else if (rotation >= 247.5 && rotation < 292.5)
            return "E";
        else if (rotation >= 292.5 && rotation < 337.5)
            return "SE";
        else if (rotation >= 337.5 && rotation < 360.0)
            return "S";
        return null;

    }

    public static Location getInFrontOf(Location location) {
        String s = getCardinalDirection(location);
        switch (s) {
            case "N":
                return location.add(0, 0, -2);
            case "NE":
                return location.add(2, 0, -2);
            case "E":
                return location.add(2, 0, 0);
            case "SE":
                return location.add(2, 0, 2);
            case "S":
                return location.add(0, 0, 2);
            case "SW":
                return location.add(-2, 0, 2);
            case "W":
                return location.add(-2, 0, 0);
            case "NW":
                return location.add(-2, 0, -2);
            default:
                return location;
        }
    }

    public static void playChestAnimation(Location loc, boolean open) {
        Bukkit.getOnlinePlayers().forEach(p -> playChestAnimation(p, loc, open));
    }

    public static void playChestAnimation(Player player, Location loc, boolean open) {
        try {
            Object world = ReflectionAPI.getHandle((Object) loc.getWorld());
            Object position = BLOCK_POSITION_CLASS.getConstructor(double.class, double.class, double.class).newInstance(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
            Object tileChest = world.getClass().getMethod("getTileEntity", BLOCK_POSITION_CLASS).invoke(world, position);
            Object packet = PACKET_PLAY_OUT_BLOCK_ACTION_CLASS.getConstructor(BLOCK_POSITION_CLASS, BLOCK_CLASS, int.class, int.class).newInstance(position, tileChest.getClass().getMethod("getBlock").invoke(tileChest), 1, open ? 1 : 0);
            ReflectionAPI.sendPacket(player, packet);
        } catch (Exception e) {
            Core.error("An error occured while playing a chest animation");
            e.printStackTrace();
        }
    }

    public static ItemStack addGlow(ItemStack item) {
        item.addUnsafeEnchantment(GLOW, 1);
        return item;
    }

    public static void insertLogLater(UUID uuid, String name, String action, String type, String reward, double amount, double price) {
        new BukkitRunnable() {
            @Override
            public void run() {
                Utils.insertLog(uuid, name, action, type, reward, amount, price);
            }
        }.runTaskAsynchronously(Core.getInstance());
    }

    public static void insertLog(UUID uuid, String name, String action, String type, String reward, double amount, double price) {
        boolean result = LogDAO.insertLog(uuid, name, action, type, reward, amount, price);
        if(!result) Core.error("Error while logging uuid " + uuid + " name " + name + " action " + action + " reward " + reward + " amount " + amount + " price " + price);

//        try (PreparedStatement st = Core.sql.prepareStatement("insert into logs(uuid, name, action, type, reward, amount, price, server) values (?,?,?,?,?,?,?,?);")) {
//            st.setString(1, uuid.toString());
//            st.setString(2, name);
//            st.setString(3, action);
//            st.setString(4, type);
//            st.setString(5, reward);
//            st.setDouble(6, amount);
//            st.setDouble(7, price);
//            st.setString(8, Core.name());
//            st.execute();
//            st.close();
//        } catch (SQLException e) {
//            Core.error("Error while logging uuid " + uuid + " name " + name + " action " + action + " reward " + reward + " amount " + amount + " price " + price);
//        }
    }

    public static Collection<String> getOfflineStaff() {
//        Collection<String> staff = new ArrayList<>();
//        try (ResultSet resultSet = Core.getSQL().query("SELECT lastname FROM users WHERE userrank IN ( 'HELPOP', 'MOD', 'ADMIN', 'DEV' );")) {
//            while (resultSet.next()) {
//                staff.add(resultSet.getString("lastname"));
//            }
//            resultSet.close();
//        } catch (Exception exception) {
//
//        }
//        return staff;
        return ServerInfoDAO.getOnlineStaff();
    }

    public static TimeFormatter timeFormatter(TimeUnit timeUnit, Long time) {
        return new TimeFormatter(timeUnit, time);
    }

    public static boolean isBanned(String uuid) {
//        try (ResultSet rs = Core.sql.query("select * from BAT_ban where UUID='" + uuid + "';")) {
//            if (rs.next()) {
//                if (rs.getBoolean("ban_state")) {
//                    rs.close();
//                    return true;
//                }
//            }
//            rs.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return false;
        return UserDAO.isUserBanned(uuid);
    }

    /**
     * Apply these item flags to ItemStack
     *
     * @param itemStack - the ItemStack to apply these flags to
     * @param flags     - the flags to apply
     **/
    public static void applyItemFlags(ItemStack itemStack, ItemFlag... flags) {
        ItemMeta itemMeta = itemStack.getItemMeta();

        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(flags);

        itemMeta.addItemFlags(flags);
        itemStack.setItemMeta(itemMeta);
    }

    /**
     * Clone ItemStack, apply flags and return new copy.
     *
     * @param itemStack the ItemStack to apply these flags to
     * @param flags     the flags to apply
     * @return cloned ItemStack with itemflags
     **/
    public static ItemStack addItemFlags(ItemStack itemStack, ItemFlag... flags) {
        ItemStack stack = itemStack.clone();
        ItemMeta itemMeta = stack.getItemMeta();

        itemMeta.setUnbreakable(true);
        itemMeta.addItemFlags(flags);

        itemMeta.addItemFlags(flags);
        stack.setItemMeta(itemMeta);
        return stack;
    }

    public static Month getMonth() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getMonth();
    }

    public static int getDay() {
        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.getDayOfMonth();
    }

    public static void setMaxPlayers(int maxPlayers) {
        String bukkitversion = Bukkit.getServer().getClass().getPackage()
                .getName().substring(23);
        Object playerlist = null;
        try {
            playerlist = Class.forName("org.bukkit.craftbukkit." + bukkitversion + ".CraftServer")
                    .getDeclaredMethod("getHandle", null).invoke(Bukkit.getServer(), null);
            Field maxplayers = playerlist.getClass().getSuperclass()
                    .getDeclaredField("maxPlayers");
            maxplayers.setAccessible(true);
            maxplayers.set(playerlist, maxPlayers);
        } catch (IllegalAccessException | InvocationTargetException |
                NoSuchMethodException |
                ClassNotFoundException |
                NoSuchFieldException exception) {
            exception.printStackTrace();
        }
        Core.getSettings().setMaxPlayers(maxPlayers);
        Core.getSettings().getCoreConfig().set("maxplayers", maxPlayers);
        Utils.saveConfig(Core.getSettings().getCoreConfig(), "core");
    }

    public static void startEnchantmentShineRemover(ProtocolManager lib, Plugin owner){
        lib.addPacketListener(new PacketAdapter(owner, ListenerPriority.NORMAL, PacketType.Play.Server.ENTITY_EQUIPMENT, PacketType.Play.Server.WINDOW_ITEMS){
            @Override
            public void onPacketSending(PacketEvent event){
                PacketContainer packet = event.getPacket();
                if(packet.getType()==PacketType.Play.Server.ENTITY_EQUIPMENT){
                    removeEnchantments(packet.getItemModifier().read(0));
                }
                else if(packet.getType()==PacketType.Play.Server.WINDOW_ITEMS){
                    for(ItemStack is : packet.getItemArrayModifier().read(0)){
                        if(is!=null)
                            removeEnchantments(is);
                    }
                }
            }
        });
    }

    private static void removeEnchantments(ItemStack stack) {
        if(stack==null)
            return;
        Object[] copy = stack.getEnchantments().keySet().toArray();

        for (Object enchantment : copy) {
            stack.removeEnchantment((Enchantment) enchantment);
        }
    }

    public static String timeInSecondsToText(long timer) {
        return timeInSecondsToText(timer, C.WHITE, C.WHITE, C.WHITE);
    }

    public static String timeInSecondsToText(long timer, String numberColor, String textColor, String splitterColor) {
        StringBuilder sb = new StringBuilder();
        List<TimeUnit> units = Arrays.asList(TimeUnit.values());
        Collections.reverse(units);
        int counter = 0;
        for(TimeUnit u : units) {
            if (counter >= 2)
                break;
            long time = u.convert(timer, TimeUnit.SECONDS);
            if (time >= 1) {
                sb.append(numberColor + time + textColor + " " + (time == 1 ? u.toString().toLowerCase().substring(0, u.toString().length()-1) : u.toString().toLowerCase()) + splitterColor + (counter == 1 ? "" : ", "));
                counter++;
                timer -= TimeUnit.SECONDS.convert(time, u);
            }
        }
        return sb.toString();
    }
}
