package net.grandtheftmc.vice;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedDataWatcher;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.boards.Board;
import net.grandtheftmc.core.boards.BoardType;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.items.Schedule;
import net.grandtheftmc.vice.users.ViceRank;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.ReflectionUtil;
import net.grandtheftmc.vice.world.warps.Warp;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.*;
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
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class ViceUtils {

    public static final String HEADER = Utils.f(
            " &7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀&d&l▄&7&l▀");
    public static final String FOOTER = Utils.f(
            " &7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄&d&l▀&7&l▄");

    private ViceUtils() {
    }

    public static Material getSeedVersionOfMaterial(Material material) {
        String sMat = material + "_SEEDS";
        return Material.getMaterial(sMat) == null ? material : Material.getMaterial(sMat);
    }

    public static ViceUser getViceUser(Player player) {
        return Vice.getUserManager().getLoadedUser(player.getUniqueId());
    }

    public static User getUser(Player player) {
        return Core.getUserManager().getLoadedUser(player.getUniqueId());
    }

    public static UserRank getRank(Player player) {
        return Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
    }

    public static void updateBoard(Player player, ViceUser viceUser) {
        updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), viceUser);
    }

    public static boolean canPlantOn(Material plant, Material block){
        switch (plant){
            case CARROT_ITEM:
            case POTATO_ITEM:
            case SEEDS:
            case BEETROOT_SEEDS:
            case MELON_SEEDS:
            case PUMPKIN_SEEDS:
                return block==Material.SOIL;
            case NETHER_STALK:
                return block== Material.SOUL_SAND;
            case SUGAR_CANE:
                return block== Material.DIRT || block== Material.SAND;
            case CACTUS:
                return block== Material.SAND;
        }
        return true;
    }

    public static void updateBoard(Player player, User user, ViceUser viceUser) {
        if (!user.getPref(Pref.USE_SCOREBOARD) || user.isInTutorial()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
            return;
        }
        Board board = new Board("vice", "&d&lVice&7&lMC", BoardType.LIST);
        board.addLine("&a&lMoney");
        board.addLine(' ' + Utils.formatMoney(viceUser.getMoney()));
        board.addLine("&a");
        if (viceUser.isCop()) {
            board.addLine("&3&lCop Rank");
            board.addLine(' ' + viceUser.getCopRank().getName());
        } else {
            ViceRank rank = viceUser.getRank();
            ViceRank next = rank.getNext();
            int cost = next == null ? 0 : next.getPrice();
            board.addLine(Utils.f("&e&lRank Info"));
            board.addLine(Utils.f(" &7Current: &r" + viceUser.getRank()));
            if (next != null) {
                board.addLine(Utils.f(" &7Next: &r" + next.getColoredName()));
                board.addLine(Utils.f(" &7Cost: &r" + Utils.formatMoney(next.getPrice())));
                board.addLine(Utils.f(viceUser.hasMoney(cost) ? " &7Use &a/rankup&7 to progress!" : " &7Progress: &r" + (100 * Utils.round(viceUser.getMoney() / cost)) + "%"));
            }
        }
        board.addLine(Utils.f("&b"));
        board.addLine(Utils.f("&6&lServer IP"));
        board.addLine(Utils.f(" mc-gtm.net"));
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
        ItemStack bp = Utils.createItem(Material.CHEST, "&6&lBackpack");
        ItemStack ammoPouch = Utils.createItem(Material.CHEST, "&c&lAmmo Pouch");
        Inventory inv = player.getInventory();
        if (!inv.contains(phone))
            if (inv.getItem(8) == null)
                inv.setItem(8, phone);
            else
                inv.addItem(phone);
        if(!inv.contains(bp))
            inv.setItem(17, bp);
        if(!inv.contains(ammoPouch))
            inv.setItem(16, ammoPouch);
    }



    public static boolean isPhone(ItemStack item) {
        return item != null && Objects.equals(item, Utils.createItem(Material.WATCH, "&7&lPhone"));
    }

    public static String getMessageKilledBy(String name) {
        String[] msges = new String[]{"Shanked by " + name, "Rekt by " + name, "Killed by " + name,
                name + " killed you", name + " clapped yo ass", name + " killed you"};
        return msges[Utils.getRandom().nextInt(msges.length)];
    }

    public static void sendJoinMessage(Player p, User u) {
        p.sendMessage(new String[]{"", "", "", "", "", "", "", "", "", "", Utils.f(HEADER), "",
                Utils.fc("Welcome, " + u.getColoredName(p) + "&r to &d&lVice&7&lMC&r!  "),
                Utils.fc("&e&l&oDrug Cartels in Minecraft!      "), "", Utils.fc("&e&lSTORE &r&nstore.grandtheftmc.net"),
                Utils.fc("&a&lSITE        &r&ngrandtheftmc.net  "), "", Utils.fc("&7Use &a/tutorial&7 to get started!"),
                "", Utils.f(FOOTER), ""});

    }

    public static List<ViceUser> getCops() {
        return Vice.getUserManager().getLoadedUsers().stream().filter(ViceUser::isCop).collect(Collectors.toList());
    }

    public static Map<ViceUser, Integer> sort(Map<ViceUser, Integer> unsortMap) {
        List<Map.Entry<ViceUser, Integer>> list = new LinkedList<>(unsortMap.entrySet());
        list.sort(Comparator.comparing(obj -> obj.getValue()));
        Map<ViceUser, Integer> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<ViceUser, Integer> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static List<Player> getJailedPlayers() {
        List<Player> players = new ArrayList<>();
        for (Player p : Bukkit.getOnlinePlayers()) {
            ViceUser viceUser = Vice.getUserManager().getLoadedUser(p.getUniqueId());
            if (viceUser.isArrested()) {
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
            Vice.getInstance().getLogger().log(Level.ALL, "Error while serializing items!");
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
            Vice.getInstance().getLogger().log(Level.ALL, "Error while deserializing items: " + data);
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

    private final static List<String> DEFAULT_ITEMS = new ArrayList<>(Arrays.asList("Phone", "Backpack", "Ammo Pouch"));
    public static boolean isDefaultPlayerItem(ItemStack is) {
        return is!=null && is.hasItemMeta() && is.getItemMeta().hasDisplayName() && DEFAULT_ITEMS.contains(ChatColor.stripColor(is.getItemMeta().getDisplayName()));
    }

    public static String upperCaseFirst(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length()).toLowerCase();
    }

    public static int getTimeInJailForDrugs(Player player) { // IN SECONDS
        Map<Schedule, Integer> drugs = new HashMap<>();
        for (ItemStack item : player.getInventory().getContents()) {
            GameItem gameItem = Vice.getItemManager().getItem(item);
            if (gameItem != null && gameItem.isScheduled()) {
                Schedule s = gameItem.getSchedule();
                drugs.put(s, drugs.getOrDefault(s, 0) + item.getAmount());
            }
        }
        int i = drugs.entrySet().stream().mapToInt(entry -> (int) (getTimeInJail(entry.getValue()) * entry.getKey().getJailMultiplier())).sum();
        return i > 900 ? 900 : i;
    }

    private static int getTimeInJail(int drugs) { // IN SECONDS
        if (drugs <= 64)
            return 60;
        if (drugs <= 256)
            return 120;
        if (drugs <= 512)
            return 180;
        return 300;
    }

    public static int getCopMoney(int wl) {
        return new int[]{0, 2000, 5000, 10000, 20000, 50000}[wl];
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

    public static boolean isTool(Material mat) {
        String s = mat.toString();
        return s.contains("SPADE") || s.contains("PICKAXE") || s.contains("AXE") || s.contains("HOE");
    }

    public static boolean isArmorPiece(Material mat) {
        String s = mat.toString();
        return s.contains("LEGGINGS") || s.contains("BOOTS") || s.contains("HELMET") || s.contains("CHESTPLATE");
    }

    public static int getFixAllDelay(UserRank rank) {// IN SECONDS
        switch (rank) {
            default:
                return 60*60*24;
        }

    }

    public static int getSetHomes(ViceRank rank) {
        switch (rank) {
            case JUNKIE:
            case FALCON:
            case THUG:
            case DEALER:
                return 0;
            case GROWER:
            case SMUGGLER:
            case CHEMIST:
            case DRUGLORD:
                return 2;
            default:
                return 3;
        }
    }

    public static int getSetHomes(UserRank rank) {
        switch (rank) {
            case DEFAULT:
                return 1;
            case VIP:
                return 2;
            case PREMIUM:
                return 4;
            case ELITE:
                return 7;
            case SPONSOR:
                return 12;
            default:
                return 20;
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

    public static int getStartingBonds(UserRank rank) {
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


    public static int getFreeLotteryTickets(UserRank userRank) {
        switch (userRank) {
            case DEFAULT:
                return 0;
            case VIP:
                return 1;
            case PREMIUM:
                return 2;
            case ELITE:
                return 5;
            case SPONSOR:
                return 10;
            default:
                return 20;
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
            case HELPOP:
            case MOD:
            case SRMOD:
            case ADMIN:
                return 30;
            case DEV:
            case MANAGER:
            case OWNER:
                return 1;
            default:
                return 60;
        }
    }

    public static double getDrugSellModifier(UserRank rank){
        switch (rank) {
            case DEFAULT:
                return 1;
            case VIP:
                return 1.05;
            case PREMIUM:
                return 1.1;
            case ELITE:
                return 1.2;
            case SPONSOR:
                return 1.3;
            case SUPREME:
                return 1.5;
            default:
                return 1.5;
        }
    }

    /*
     * public static String serialize(ItemStack[] a) { StringBuilder b = new
     * StringBuilder(); for (int i = 0; i < a.length; i++) { if (i > 0)
     * b.append(","); ItemStack items = a[i]; try {
     * b.append(StreamSerializer.getDefault().serializeItemStack(items)); } catch
     * (Exception e) { b.append("null"); System.out.println(
     * "Error while serializing an items (" + i + "): " +
     * e.getCause().getMessage()); } } return b.toString(); }
     *
     * public static ItemStack[] deserialize(String s) { if (s == null ||
     * s.length() == 0) return new ItemStack[0]; String[] a = s.split(",");
     * ItemStack[] array = new ItemStack[a.length]; for (int i = 0; i <
     * a.length; i++) { try { array[i] =
     * StreamSerializer.getDefault().deserializeItemStack(a[i]); } catch
     * (Exception e) { array[i] = null; System.out.println(
     * "Error while deserializing an items (" + i + "): " + e.getMessage()); } }
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
        if (location == null)
            return null;
        String world = location.getWorld() == null ? null : location.getWorld().getName();
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

    public static double getRecentTPS() {
        return 20;
    }

    public static void moneylog(Player sender, Player target, double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("vicelogs/moneylog_" + fileName + ".txt");
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
        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("Vicelogs/" + logName + '_' + fileName + ".txt");
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
        List<GameItem> gameItems = Vice.getItemManager().getItems().stream().filter(gameItem -> gameItem.getType() == GameItem.ItemType.ITEMSTACK || gameItem.getType() == GameItem.ItemType.WEAPON).collect(Collectors.toList());
        return gameItems.get(ThreadLocalRandom.current().nextInt(gameItems.size()));
    }

    public static Warp getNearestWarp(Location location) {
        Warp nearestWarp = null;
        for (Warp warp : Vice.getWorldManager().getWarpManager().getWarps()) {
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
//        try {
//            ResultSet resultSet = Core.getSQL().query("SELECT * FROM " + Core.name() + " ORDER BY kills DESC LIMIT " + count + ';');
//            while (resultSet.next()) {
//                topKillers.put(resultSet.getString("name"), resultSet.getInt("kills"));
//            }
//            resultSet.close();
//        } catch (Exception ignored) {
//        }

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " ORDER BY kills DESC LIMIT " + count + ';')) {
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
        PacketContainer packet = Vice.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        watcher.setObject(0, serializer, (byte) 0x40);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            Vice.getProtocolManager().sendServerPacket(player, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                removeGlow(player, target);
            }
        }.runTaskLaterAsynchronously(Vice.getInstance(), time);
    }

    public static void removeGlow(Player player, Player target) {
        PacketContainer packet = Vice.getProtocolManager().createPacket(PacketType.Play.Server.ENTITY_METADATA);
        packet.getIntegers().write(0, target.getEntityId());
        WrappedDataWatcher watcher = new WrappedDataWatcher();
        WrappedDataWatcher.Serializer serializer = WrappedDataWatcher.Registry.get(Byte.class);
        watcher.setEntity(target);
        watcher.setObject(0, serializer, (byte) 0x0);
        packet.getWatchableCollectionModifier().write(0, watcher.getWatchableObjects());
        try {
            Vice.getProtocolManager().sendServerPacket(player, packet);
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

//    public static CoreNPC createNPC(Location location, String uuid, String displayName) {
//        GameProfile prof;
//        try {
//            prof = new ProfileBuilder(displayName).applySkin(UUID.fromString(uuid)).build();
//        } catch (SkinLookupException e) {
//            e.printStackTrace();
//            return null;
//        }
//
//        try {
//            CoreNPC npc = new CoreNPC_1_12_R1(location, UUID.randomUUID().toString(), prof);
//            npc.setArmour();
//            npc.spawn();
//            return npc;
//        } catch (DuplicateIdentifierException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    /* Property example:
            skin18488.getProperties().put("textures", new Property(
                    "textures",
                    "eyJ0aW1lc3RhbXAiOjE0OTk2OTMzMDMzODEsInByb2ZpbGVJZCI6IjQzYTgzNzNkNjQyOTQ1MTBhOWFhYjMwZjViM2NlYmIzIiwicHJvZmlsZU5hbWUiOiJTa3VsbENsaWVudFNraW42Iiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS82ZDg2NmFmYmZiNGFkMDE4YWYzZDUzY2M3ZjMyMmFlNzUwYTUyZjQ3OTNkNzg5YTcxNTI1YmJlZmRlOTI4M2MifX19",
                    "HLaxBLtNoquUFZNH4OTD/C1lGgTgYsde5qVp3Qm6cIAUZISG3KPIG/Rd53NupJgbYVe/lgA/QlnxjJ8QvpWhZOT9qPCVicAUHKGk5lVZGx3zTgf0DYHgNwd++lYFIHzFzke5VGtrd4DRtev/NplmO3KJ7orRbNFWwL1HqI9OQQNH/6cE6WKz3ecfohdwhA9E2LsL3Tljr4b3Q5heK5Q5WdSsTa7GCX/sxSFp8XHqedQEMWMc2Q72dXlGuiJ3+9HVu8XTzfE30sCYUdXk4CkZ5lWaL2M521lsve+c+gbfDXakZzmcAXd1E11sGEzrGnsr/78KMSie6u/noRFFb5lgGN3scQogRClHgHNql25S0o5QW6NWnwHEd5+fOfcpRZq45XW57aj0qTZLxZpWrn6mWddZHs5OWJ99wChsUj6Tj3Z+rtptHBqEBdj4Cd33zFT6B9Os3GveEpDx1MOV+dG6l6zBVdWy5Sg1oGJxQlYbLy5/FesYIXLXpkK9sU1mPQFmNg2YlF9j87bo8PHU6GngN6fqA+xwlEaLYVkaRkbaQMwD8eotTWKJRFyAEkCnjOBeOG/bQKpIbRfJ+k/N9/K18oUoazd6dnEtDVOogQTVvQPxlV8iZO08GwCfbBJBZzIijw7CPc7RkreFeye0hhfUjuFpSPDkqarccINA0C0d+VE="));
     */
    public static void createNPC(Location location, String uuid, String displayName, com.mojang.authlib.properties.Property texture) {
//        GameProfile prof;
//        try {
//            prof = new ProfileBuilder(displayName).applySkin(UUID.fromString(uuid)).build();
//        } catch (SkinLookupException e) {
//            e.printStackTrace();
//            return;
//        }
//
//        try {
//            GameProfile skin18488 = new GameProfile(UUID.fromString(uuid), displayName);
//            skin18488.getProperties().put("textures", texture);
//
//            CoreNPC npc = new CoreNPC(location, UUID.randomUUID().toString(), skin18488);
//            npc.setArmour();
//            npc.setHeadRotation(0F);
//            npc.spawn();
//        } catch (DuplicateIdentifierException e) {
//            e.printStackTrace();
//        }
    }
    public static boolean isInSpawnRange(Player player, double range) {
        return Vice.getWorldManager().getWarpManager().getSpawn() != null && Vice.getWorldManager().getWarpManager().getSpawn().getLocation().distance(player.getLocation()) < range;
}

    public static boolean ascendLevel(Player player) {
        Location pos = player.getLocation();
        int x = pos.getBlockX();
        int y = Math.max(0, pos.getBlockY());
        int z = pos.getBlockZ();
        World world = pos.getWorld();

        byte free = 0;
        byte spots = 0;
        while (y <= 256 + 2) {
            if (world.getBlockAt(x, y, z).isEmpty()) {
                free += 1;
            } else {
                free = 0;
            }
            if (free == 2) {
                spots += 1;
                if (spots == 2) {
                    Block block = world.getBlockAt(x, y - 2, z);
                    int type = block.getTypeId();
                    if ((type == 10) || (type == 11)) {
                        return false;
                    }

                    player.teleport(new Location(world, pos.getX(), y - 1, pos.getZ(), pos.getYaw(), pos.getPitch()));
                    return true;
                }
            }
            y++;
        }
        return false;
    }

    public static boolean descendLevel(Player player) {
        Location pos = player.getLocation();
        int x = pos.getBlockX();
        int y = Math.max(0, pos.getBlockY() - 1);
        int z = pos.getBlockZ();
        World world = pos.getWorld();

        byte free = 0;
        while (y >= 1) {
            if (world.getBlockAt(x, y, z).isEmpty()) {
                free += 1;
            } else {
                free = 0;
            }
            if (free == 2) {
                while (y >= 0) {
                    Block block = world.getBlockAt(x, y, z);
                    int type = block.getTypeId();
                    if ((type != 0) && (type != 10) && (type != 11)) {
                        player.teleport(new Location(world, pos.getX(), y + 1, pos.getZ(), pos.getYaw(), pos.getPitch()));
                        return true;
                    }
                    y--;
                }
                return false;
            }
            y--;
        }
        return false;
    }
}
