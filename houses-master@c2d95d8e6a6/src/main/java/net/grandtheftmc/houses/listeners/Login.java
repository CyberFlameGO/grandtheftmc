package net.grandtheftmc.houses.listeners;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class Login implements Listener {

    public Login(JavaPlugin plugin) {
    }

    @EventHandler
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        HouseUser user = Houses.getUserManager().getLoadedUser(e.getUniqueId());
//        user.dataCheck(e.getName());
        if (!user.updateDataFromDb())
            e.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "&cAn error occured while trying to fetch your data from the database. Please try again in a few seconds!");
    }

    @EventHandler(priority=EventPriority.MONITOR)
    public void onLoginMonitor(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        HouseUser user = Houses.getUserManager().getLoadedUser(uuid);

        if (!user.hasUpdated())
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.f("&cThe server is still restarting! Please try again in a few seconds!"));

        if (e.getResult() != Result.ALLOWED)
            Houses.getUserManager().unloadUser(uuid);
    }
}
