package net.grandtheftmc.Creative.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class Join implements Listener {

	@EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE) player.setGameMode(GameMode.CREATIVE);
        player.sendMessage(Core.getAnnouncer().getHeader());
        player.sendMessage(Utils.f("&7Welcome to &aCreative!"));
        player.sendMessage(Utils.f("&7Use &a/plot auto &7to get started with a plot."));
        player.sendMessage(Core.getAnnouncer().getFooter());
    }

}
