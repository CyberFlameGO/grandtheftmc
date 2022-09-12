package net.grandtheftmc.vice.areas.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.AreaManager;
import net.grandtheftmc.vice.areas.dao.DiscoveryDAO;
import net.grandtheftmc.vice.areas.events.AreaEnterEvent;
import net.grandtheftmc.vice.areas.events.AreaLeaveEvent;
import net.grandtheftmc.vice.areas.obj.Area;
import net.grandtheftmc.vice.areas.obj.AreaUser;
import net.grandtheftmc.vice.utils.TitleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Set;
import java.util.UUID;

public class AreaListener implements Listener {

    private final Vice plugin;
    private final AreaManager areaManager;

    public AreaListener(Vice plugin, AreaManager areaManager) {
        this.plugin = plugin;
        this.areaManager = areaManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();

        Set<Integer> discoveries = DiscoveryDAO.getSeasonByUUID(uuid, plugin.getSeasonManager().getCurrentSeason().getNumber());
        AreaUser user = new AreaUser(uuid, discoveries);

        areaManager.getAreaUsers().add(user);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        AreaUser user = areaManager.getUserByUUID(event.getPlayer().getUniqueId());
        areaManager.getAreaUsers().remove(user);
    }

    @EventHandler
    public void onPlayerEnterArea(AreaEnterEvent event) {
        Player player = event.getPlayer();
        Area area = event.getArea();
        AreaUser user = areaManager.getUserByUUID(player.getUniqueId());

        user.setCurrent(area.getID());

        if (!user.hasVisited(area.getID())) {
            user.getVisited().add(area.getID());
            ServerUtil.runTaskAsync(() -> DiscoveryDAO.insert(player.getUniqueId(), plugin.getSeasonManager().getCurrentSeason().getNumber(), area.getID()));

            new TitleBuilder()
                    .setTitleText(ChatColor.LIGHT_PURPLE + area.getName().replace("_", " "))
                    .setSubTitleText(ChatColor.GOLD + "You found a hidden area!")
                    .setDuration(3)
                    .setFadeIn(1)
                    .setFadeOut(1)
                    .send(player);
        }

        player.sendMessage(Lang.VICE.f("&dEntering&f: &6" + area.getName().replace("_", " ")));
    }

    @EventHandler
    public void onPlayerLeaveArea(AreaLeaveEvent event) {
        Player player = event.getPlayer();
        Area area = event.getArea();
        AreaUser user = areaManager.getUserByUUID(player.getUniqueId());

        user.setCurrent(-1);

        player.sendMessage(Lang.VICE.f("&dLeaving&f: &6" + area.getName().replace("_", " ")));
    }

}
