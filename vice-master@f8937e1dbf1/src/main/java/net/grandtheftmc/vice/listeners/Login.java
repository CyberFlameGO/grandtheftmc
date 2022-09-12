package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
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
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getUniqueId());
        user.dataCheck(e.getName(), Core.getUserManager().getLoadedUser(e.getUniqueId()).getUserRank());
        if (!user.updateDataFromDb())
            e.disallow(org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result.KICK_OTHER,
                    "&cAn error occured while trying to fetch your data from the database. Please try again in a few seconds!");
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onLoginMonitor(PlayerLoginEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
        if (!user.getBooleanFromStorage(BooleanStorageType.HAS_UPDATED))
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, Utils.f("&cThe server is still restarting! Please try again in a few seconds!"));
        if (e.getResult() == Result.ALLOWED)
            return;
        Vice.getUserManager().unloadUser(uuid);

    }

}
