package net.grandtheftmc.Creative.listeners;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.Creative.users.CreativeUser;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;

import java.util.UUID;

public class Login implements Listener {

    @EventHandler
    public void onLogin(AsyncPlayerPreLoginEvent e) {
        CreativeUser user = Creative.getUserManager().getLoadedUser(e.getUniqueId());
        user.dataCheck(e.getName(), Core.getUserManager().getLoadedUser(e.getUniqueId()).getUserRank());
        if (!user.updateDataFromDb())
            e.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "&cAn error occured while trying to fetch your data from the database. Please try again in a few seconds!");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoginMonitor(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        CreativeUser user = Creative.getUserManager().getLoadedUser(uuid);
        if (!user.hasUpdated())
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.f("&cThe server is still restarting! Please try again in a few seconds!"));
        if (e.getResult() == Result.ALLOWED)
            return;
        Creative.getUserManager().unloadUser(uuid);

    }

}
