package net.grandtheftmc.hub;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.boards.Board;
import net.grandtheftmc.core.boards.BoardType;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;

public final class HubUtils {

    private HubUtils() {}

    public static void giveItems(Player player) {
        if (player == null) return;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        player.setHealth(20);
        player.setMaxHealth(20);
        player.setFoodLevel(20);
        player.setGameMode(GameMode.SURVIVAL);
        PlayerInventory inv = player.getInventory();
        inv.clear();
        player.getInventory().setHeldItemSlot(4);
       // inv.setItem(0, Utils.createItem(Material.ENDER_CHEST, "&6&lCosmetics            &7&lRight Click"));
        inv.setItem(3, Utils.createItem(Material.COMPASS, "&e&lServer Warper      &7&lRight Click"));
        inv.setItem(5, Utils.createItem(Material.WATCH, "&d&lHub Warper      &7&lRight Click"));
        String b = user.getPref(Pref.PLAYERS_SHOWN) ? "&c&lHide" : "&a&lShow";
        inv.setItem(8, Utils.createItem(Material.REDSTONE_COMPARATOR, b + " Players        &7&lRight Click"));
        player.getActivePotionEffects().clear();
        inv.setArmorContents(null);
        inv.setChestplate(new ItemStack(Material.ELYTRA));
        player.updateInventory();
    }

    public static void sendJoinMessage(Player p, User user) {
        p.sendMessage(new String[]{"", "", "", "", "", "", "", "", ""});
        String[] header = Core.getAnnouncer().getHeader();
        if (header != null && header.length > 0) ;
        p.sendMessage(Utils.f(Core.getAnnouncer().getHeader()));
        p.sendMessage(new String[]{
                Utils.fc("Welcome, " + user.getColoredName(p) + "&r to the &7&l" + Core.getSettings().getNetworkShortName() + " &6&lHub&r!"),
                Utils.fc("&e&l&oGTA in Minecraft!"), "", Utils.fc("&e&lSTORE &r&n" + Core.getSettings().getStoreLink()),
                Utils.fc("&a&lSITE         &r&n" + Core.getSettings().getWebsiteLink() + " "), "", Utils.fc("&7Use the &eserver warper&7 to play!")});
        String[] footer = Core.getAnnouncer().getFooter();
        if (footer != null && footer.length > 0) ;
        p.sendMessage(Utils.f(Core.getAnnouncer().getFooter()));
    }

    public static void updateBoard(Player player, User user) {
        String rank = "No Rank";
        if (user.isSpecial())
            rank = user.getUserRank().getColoredNameBold();
        Board board = new Board("lobby", Core.getSettings().getType().getScoreboardHeader(), BoardType.KEY_VALUE);
        if (Core.getSettings().isSister()) {

            int online = 0;
            for (Server server : Core.getServerManager().getServers())
                online += server.getOnlinePlayers();
            board.addValue("d", "Players Online", online + "");

            board.addValue("6", "Rank", rank);
            board.addValue("6", "Server IP", Core.getSettings().getNetworkIP());
        }
        else {
            board.addValue("a", "Tokens", String.valueOf(user.getTokens()));
            board.addValue("e", "Crowbars", String.valueOf(Math.max(user.getCrowbars(), 0)));
            board.addValue("6", "Rank", rank);
            board.addValue("6", "Server IP", Core.getSettings().getNetworkIP());
            //board.addValue("6", "Server IP", user.getjoin)
        }
        board.updateFor(player, user);
    }

    public static String serializeLocation(Location location) {
        String world = location.getWorld().getName();
        String x = String.valueOf(location.getX());
        String y = String.valueOf(location.getY());
        String z = String.valueOf(location.getZ());
        String yaw = String.valueOf(location.getYaw());
        String pitch = String.valueOf(location.getPitch());
        return world + ':' + x + ':' + y + ':' + z + ':' + yaw + ':' + pitch;
    }

    public static Location deserializeLocation(String loc) {
        String[] args = loc.split(":");
        World world = Bukkit.getWorld(args[0]);
        double x = Double.valueOf(args[1]);
        double y = Double.valueOf(args[2]);
        double z = Double.valueOf(args[3]);
        float yaw = Float.valueOf(args[4]);
        float pitch = Float.valueOf(args[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }
}
