package net.grandtheftmc.Bungee;

import net.grandtheftmc.Bungee.redisbungee.data.DataType;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.grandtheftmc.Bungee.utils.DefaultFontInfo;
import net.grandtheftmc.Bungee.utils.ServerStatus;
import net.grandtheftmc.Bungee.utils.TimeFormatter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.*;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class Utils {
    public static final ConcurrentHashMap<ServerInfo, Boolean> SERVERS = new ConcurrentHashMap<>();
    private static final Random RANDOM = new Random();
    public static Collection<String> recentHelps = new ArrayList<>();

    private Utils() {
    }

    public static TextComponent ft(String s) {
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', s));
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

    public static void loop(int amount, Runnable runnable) {
        for (int i = 0; i < amount; i++) runnable.run();
    }

    public static String f(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void redisChatLog(String sender, String msg) {
        Map<String, Object> chatLogSerializd = new HashMap<>();
        chatLogSerializd.put("type", "staff");
        chatLogSerializd.put("sender", sender);
        chatLogSerializd.put("message", msg);
        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.LOG, chatLogSerializd));
    }

    public static void chatLog(String sender, String msg) {
        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("gtmlogs/gtmlog_staff_" + fileName + ".txt");
        try {
            if (!file.isFile() || !file.exists()) {
                file.createNewFile();
            }
            String date = new SimpleDateFormat("MM/dd/yy - h:mm a").format(new Date());
            String message = date + " - " + sender + ": " + msg + "\n";
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void log(String msg, String logName) {
        String fileName = new SimpleDateFormat("MM-dd-yy").format(new Date());
        File file = new File("gtmlogs/gtmlog_" + logName + "_" + fileName + ".txt");
        try {
            if (!file.isFile() || !file.exists()) {
                file.createNewFile();
            }
            String date = new SimpleDateFormat("MM/dd/yy - h:mm a").format(new Date());
            String message = date + " - " + msg + "\n";
            FileWriter fileWriter = new FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(message);
            bufferedWriter.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public static void redisStaffChat(String sender, String msg) {
        Map<String, Object> staffChatSerialized = new HashMap<>();
        staffChatSerialized.put("sender", sender);
        staffChatSerialized.put("message", msg);

        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.STAFFCHAT, staffChatSerialized));
    }

    public static void staffChat(String name, String msg) {
        String prefix = "&7[&a&l" + name + "&7] ";
        ComponentBuilder a = new ComponentBuilder(Lang.STAFF.f(prefix))
                .append(msg)
                .color(ChatColor.GREEN);
        for (String string : msg.split(" ")) {
            if (string.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")) {
                a.event(new ClickEvent(ClickEvent.Action.OPEN_URL, string));
                break;
            }
        }
        ProxyServer.getInstance().getPlayers().stream().filter(player -> player.hasPermission("staffchat.use")).forEach(player -> player.sendMessage(a.create()));
        Bungee.log(Lang.STAFF.f("[" + name + "] " + msg));
    }

    /**
     * Called when a help request has been invoked on the Redis listener. This forwards the request to all staff.
     *
     * @param playerName Player who requested help.
     * @param msg        What did they ask for help with?
     * @param server     What server is the player on.
     */
    public static void redisHelp(String playerName, String msg, String server) {
        //can't pass proxy player here as staff and help requester may be on different instances
        BaseComponent[] a = new ComponentBuilder(Lang.HELP.f("&7[&8" + playerName + "&7] "))
                .append(msg)
                .color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + playerName + " "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.f("&7User is on server &a" + server)).create()))
                .create();

        ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission("staffchat.use") || p.getName().equalsIgnoreCase(playerName)).forEach(p -> p.sendMessage(a));
        Bungee.log(Lang.HELP.f("&7[&8" + playerName + "&7] &r" + msg));
    }

    @Deprecated
    public static void help(ProxiedPlayer player, String msg) {
        if (msg.split(" ").length <= 1) {
            player.sendMessage(Lang.HELP.f("&7Only one word? Try to describe your problem more accurately."));
            return;
        }

        String name = getColoredName(player);
        BaseComponent[] a = new ComponentBuilder(Lang.HELP.f("&7[&a&l" + name + "&7] "))
                .append(msg).color(ChatColor.GREEN)
                .event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + player.getName() + " "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(Utils.f("&7User is on server &a" + player.getServer().getInfo().getName())).create()))
                .create();

        ProxyServer.getInstance().getPlayers().stream().filter(p -> p.hasPermission("staffchat.use") || p.equals(player)).forEach(p -> p.sendMessage(a));
        Bungee.log(Lang.HELP.f("&7[&a&l" + name + "&7] &r" + msg));
        player.sendMessage(Lang.HELP.f("&7Your message has been sent to all online staff. Use &a&l\"/gmsg <name>\"&7 to talk to them individually."));
        addRecentHelp(player.getName());
    }

    public static void msg(ProxiedPlayer player, String msg) {
        player.sendMessage(ft(msg));
    }

    public static void msg(CommandSender player, String msg) {
        player.sendMessage(ft(msg));
    }

    public static void redisGlobalMessage(ProxiedPlayer sender, String target, String msg) {
        if (!Bungee.getRedisManager().isPlayerOnline(target)) {
            msg(sender, "&7That player is not on the server!");
            return;
        }

        String to = Lang.GMSG.f("&7[" + getColoredName(sender) + "&7 -> me] &r" + msg);
        String from = Lang.GMSG.f("&7[me -> " + getColoredName(target) + "&7] &r" + msg);
        String ss = Lang.GSPY.f("&7[" + getColoredName(sender) + "&7 -> " + getColoredName(target) + "&7] &r" + msg);
        msg(sender, from);

        Map<String, Object> toSerialized = new HashMap<>();
        toSerialized.put("sender", sender.getName());
        toSerialized.put("target", target);
        toSerialized.put("message", to);
        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.GMSG, toSerialized));


        Map<String, Object> socialSpySerialized = new HashMap<>();
        socialSpySerialized.put("message", ss);
        Bungee.getRedisManager().sendMessage(Bungee.getRedisManager().serialize(DataType.SOCIALSPY, socialSpySerialized));
    }

    @Deprecated
    public static void globalMessage(ProxiedPlayer sender, ProxiedPlayer target, String msg) {
        if (target == null) {
            msg(sender, "&7That player is not on the server!");
            return;
        }

        String to = Lang.GMSG.f("&7[" + getColoredName(sender) + "&7 -> me] &r" + msg);
        String from = Lang.GMSG.f("&7[me -> " + getColoredName(target) + "&7] &r" + msg);
        String ss = Lang.GSPY.f("&7[" + getColoredName(sender) + "&7 -> " + getColoredName(target) + "&7] &r" + msg);
        String url = "";
        for (String string : msg.split(" ")) {
            if (string.matches("^((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?$")) {
                url = string;
                break;
            }
        }

        BaseComponent[] a = new ComponentBuilder(to)
                .event(url.isEmpty() ? new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/gmsg " + sender.getName() + " ")
                        : new ClickEvent(ClickEvent.Action.OPEN_URL, url))
                .create();
        target.sendMessage(a);
        msg(sender, from);
        Bungee.log(Lang.GMSG.f("&7[" + sender.getName() + " -> " + target.getName() + "&7] &r" + msg));
        log("[" + sender.getName() + " -> " + target.getName() + "] " + msg, "gmsglog");
        socialSpy(ss, sender, target);
    }

    public static void socialSpy(String msg, ProxiedPlayer sender, ProxiedPlayer target) {
        Bungee.getUserManager().getLoadedUsers().forEach(user -> {
            if (Bungee.getInstance().getProxy().getPlayer(user.getUUID()) == null) return;
            if (!user.isRank(UserRank.ADMIN)) return;
            ProxiedPlayer staff = Bungee.getInstance().getProxy().getPlayer(user.getUUID());
            if (user.getSocialSpy()) {
                if (!staff.getName().equals(sender.getName()) || !staff.getName().equals(target.getName())) {
                    staff.sendMessage(Utils.f(msg));
                }
            }
        });
    }

    public static Configuration loadConfigFile(File file) {
        Configuration c = null;
        try {
            if (!file.exists())
                file.createNewFile();
            c = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static Configuration loadConfig(String src) {
        return loadConfigFile(new File(src + ".yml"));
    }

    public static Configuration loadConfigFromMaps(String src) {
        return loadConfigFile(new File("/home/mcservers/development/master/maps/" + src + ".yml"));
    }

    public static Configuration loadConfigFromMaster(String src) {
        return loadConfigFile(new File("/home/mcservers/development/master/" + src + ".yml"));
    }

    public static void saveConfigFile(Configuration c, File file) {
        try {
            if (!file.exists())
                file.createNewFile();
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(c, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig(Configuration c, String src) {
        saveConfigFile(c, new File(src + ".yml"));
    }

    public static Random getRandom() {
        return RANDOM;
    }

    public static List<String> getPlayerNames() {
        return ProxyServer.getInstance().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
    }

    public static void addRecentHelp(String player) {
        recentHelps.add(player);
        Bungee.getInstance().getProxy().getScheduler().schedule(Bungee.getInstance(), new Runnable() {
            @Override
            public void run() {
                recentHelps.remove(player);
            }
        }, 30, TimeUnit.SECONDS);
    }

    public static ServerInfo getRandomHub() {
        List<ServerInfo> hubs = Bungee.getInstance().getProxy().getServers().entrySet().stream().filter(map -> map.getKey().startsWith("hub")).map(Map.Entry::getValue).collect(Collectors.toList());
        return getLeastPlayers(hubs).orElse(hubs.get(Utils.getRandom().nextInt(hubs.size())));
    }

    public static ServerInfo getRandomServer(String type) {
        List<ServerInfo> servs = Bungee.getInstance().getProxy().getServers().entrySet().stream().filter(map -> map.getKey().startsWith(type)).map(Map.Entry::getValue).collect(Collectors.toList());
        return getLeastPlayers(servs).orElse(servs.get(Utils.getRandom().nextInt(servs.size())));
    }

    public static Optional<ServerInfo> getLeastPlayers(Collection<ServerInfo> servers) {
        Optional<ServerInfo> leastPlayers = Optional.empty();
        for (ServerInfo server : servers) {
            if (!isOnline(server)) {
                Bungee.error(server.getName() + " is offline");
                continue;
            }

            if (server.getPlayers().isEmpty()) {
                leastPlayers = Optional.of(server);
                break;
            }

            if (!leastPlayers.isPresent()) {
                leastPlayers = Optional.of(server);
                continue;
            }

            if (server.getPlayers().size() < leastPlayers.get().getPlayers().size()) {
                leastPlayers = Optional.of(server);
            }
        }
        return leastPlayers;
    }

    public static boolean isOnline(ServerInfo serverInfo) {
        ServerStatus serverStatus = ServerStatus.getServerStatus(serverInfo);
        return serverStatus.isOnline();
    }

    public static String getColoredName(ProxiedPlayer player) {
        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        return Utils.f(userOptional.map(user -> Utils.f(user.getUserRank().getColor() + player.getName())).orElseGet(() -> "&8" + player.getName()));
    }

    public static String getColoredName(String player) {
        return Utils.f("&8" + player);
    }

    public static String formatPlaytime(Long playtime) {
        TimeFormatter tf = new TimeFormatter(TimeUnit.MILLISECONDS, playtime);
        return tf.getHours() + "h " + tf.getMinutes() + "m";
    }

    public static boolean isStaff(String name) {
        for (User user : Bungee.getUserManager().getLoadedUsers()) {
            if (user.getUsername().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
