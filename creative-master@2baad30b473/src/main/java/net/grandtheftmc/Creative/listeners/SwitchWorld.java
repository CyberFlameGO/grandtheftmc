package net.grandtheftmc.Creative.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.Creative.users.CreativeRank;
import net.grandtheftmc.core.editmode.WorldConfig;
import net.grandtheftmc.core.events.PlayerSwitchWorldEvent;

public class SwitchWorld implements Listener {

    @EventHandler
    public void onSwitchWorld(PlayerSwitchWorldEvent e) {
        if (e.getToWorldConfig().isRestricted() && e.getToWorldConfig().getType() == WorldConfig.RestrictedType.GAMERANK) {
            CreativeRank
                    rank = CreativeRank.getRankOrNull(e.getToWorldConfig().getRestricted());
            if (rank == null) rank = CreativeRank.CREATOR;
            if (!Creative.getUserManager().getLoadedUser(e.getPlayer().getUniqueId()).isRank(rank))
                e.setCancelled(true);
        }
    }
}
