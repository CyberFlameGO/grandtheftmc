package net.grandtheftmc.hub.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Chat implements Listener {
    private Map<String, Long> recentChats = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.getUserRank() == UserRank.DEFAULT) {
            if (recentChats.containsKey(player.getName())) {
                if (recentChats.get(player.getName()) + TimeUnit.SECONDS.toMillis(2) >= System.currentTimeMillis()) {
                    player.sendMessage(Lang.HUB.f("&7You must wait a second before speaking again!"));
                    event.setCancelled(true);
                } else {
                    recentChats.remove(player.getName());
                }
            } else {
                recentChats.put(player.getName(), System.currentTimeMillis());
            }
        }
    }
}