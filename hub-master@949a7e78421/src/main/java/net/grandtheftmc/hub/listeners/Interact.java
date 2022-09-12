package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.servers.ServerManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.hub.Hub;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Interact implements Listener {
    private Map<String, Long> recentClicks = new HashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        UserManager um = Core.getUserManager();
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        User user = Core.getUserManager().getLoadedUser(uuid);
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null) {
            switch (item.getType()) {
                case COMPASS:
                    if (!Core.getSettings().serverWarperEnabled())
                        return;
                    event.setCancelled(true);
//                    MenuManager.openMenu(player, "serverwarper");
                    Hub.getInstance().getTranzitMenu().openInventory(player);
                    return;
                case WATCH:
                    if (!Core.getSettings().serverWarperEnabled())
                        return;
                    event.setCancelled(true);
                    MenuManager.openMenu(player, "hubservers");
                    return;
                case BOOK:
                    return;
                case ENDER_CHEST:
                    MenuManager.openMenu(player, "cosmetics");
                    event.setCancelled(true);
                    return;
                case REDSTONE_COMPARATOR:
                    if (recentClicks.containsKey(player.getName())) {
                        if (recentClicks.get(player.getName()) + TimeUnit.SECONDS.toMillis(3) >= System.currentTimeMillis()) {
                            player.sendMessage(Lang.HUB.f("&7You must wait before toggling this again!"));
                            event.setCancelled(true);
                            return;
                        } else {
                            recentClicks.put(player.getName(), System.currentTimeMillis());
                        }
                    } else {
                        recentClicks.put(player.getName(), System.currentTimeMillis());
                    }
                    if(user.getPref(Pref.PLAYERS_SHOWN)) {
                        player.sendMessage(Lang.PREFS.f("&7Players are now hidden"));
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                            if(targetUser.getPref(Pref.PLAYERS_SHOWN)) target.hidePlayer(player);
                            player.hidePlayer(target);
                        }
                    } else {
                        player.sendMessage(Lang.PREFS.f("&7Players are now shown"));
                        for (Player target : Bukkit.getOnlinePlayers()) {
                            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                            if(targetUser.getPref(Pref.PLAYERS_SHOWN)) target.showPlayer(player);
                            player.showPlayer(target);
                        }
                    }
                    user.setPref(player, Pref.PLAYERS_SHOWN, !user.getPref(Pref.PLAYERS_SHOWN));
                    ItemMeta meta = item.getItemMeta();
                    String b = user.getPref(Pref.PLAYERS_SHOWN) ? "&c&lHide" : "&a&lShow";
                    meta.setDisplayName(Utils.f(b + " Players        &7&lRight Click"));
                    item.setItemMeta(meta);
                    event.setCancelled(true);
                    return;
                case EXP_BOTTLE:
                    event.setCancelled(true);
                    player.updateInventory();
                    MenuManager.openMenu(player, "rewards");
                    return;
                default:
                    break;
            }

        }
        Block block = event.getClickedBlock();
        if (block == null) return;
        if (block.getType() == Material.WALL_SIGN) {
            Sign sign = (Sign) block.getState();
            Server server = null;
            ServerManager sm = Core.getServerManager();
            for (Server s : sm.getServers()) {
                if (s.getJoinSigns().contains(sign.getLocation())) {
                    server = s;
                    break;
                }
            }
            if (server == null) return;
            if (server.needsRankToJoin() && server.getRankToJoin().isHigherThan(user.getUserRank())) {
                player.sendMessage(Utils.f("&cSorry, this game is for " + server.getRankToJoin().getColoredNameBold()
                        + "&c and up only! Go to " + Core.getSettings().getStoreLink() + " to purchase a rank!"));
                return;
            }

            if (Objects.equals(server.getGameState(), "INGAME") && !user.isSpecial()) {
                player.sendMessage(Utils.f(
                        "&cThis game is in progress! You must be &6&lVIP&c or above to spectate games! Go to " + Core.getSettings().getStoreLink() + " to purchase a rank!"));
                return;
            } else if (Objects.equals(server.getGameState(), "END")) {
                player.sendMessage(Utils.f("&cThis game is ending! There is no point in joining now!"));
                return;
            }

            if (server.isFull() && (!user.isSpecial() || user.getUserRank() == UserRank.VIP)) {
                player.sendMessage(
                        Utils.f("&cSorry, this server is full! You must be &a&lPREMIUM&c or up to join full games!"));
                return;
            }
            player.sendMessage(Utils.f("&7Joining server &a&l" + server.getName().toUpperCase() + "&7..."));
            sm.sendToServer(player, server.getName());
        }
    }

    @EventHandler(ignoreCancelled = true)
    protected final void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (user == null) {
            event.setCancelled(true);
            return;
        }

        if (!user.hasEditMode())
            event.setCancelled(true);
    }
}