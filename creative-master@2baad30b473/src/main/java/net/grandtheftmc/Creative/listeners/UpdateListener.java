package net.grandtheftmc.Creative.listeners;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.Creative.users.CreativeRank;
import net.grandtheftmc.Creative.users.CreativeUser;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.events.DisplayNameUpdateEvent;
import net.grandtheftmc.core.events.GetPermsEvent;
import net.grandtheftmc.core.events.NametagUpdateEvent;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpdateListener implements Listener {


    @EventHandler
    public void onDisplayNameUpdate(DisplayNameUpdateEvent e) {
        Player player = e.getPlayer();
        CreativeUser u = Creative.getUserManager().getLoadedUser(player.getUniqueId());
        if (u.isRank(CreativeRank.TRAINEE))
            e.setPrefix(u.getRank().getColoredNameBold());
    }

    @EventHandler
    public void onGetPerms(GetPermsEvent e) {
        CreativeUser user = Creative.getUserManager().getLoadedUser(e.getUUID());
        if (user != null && user.getRank() != null)
            user.getRank().getAllPerms().forEach(e::addPerm);
    }

    @EventHandler
    public void onNametagChange(NametagUpdateEvent e) {
        Player player = e.getPlayer();
        if (player == null)
            return;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        CreativeUser creativeUser = Creative.getUserManager().getLoadedUser(player.getUniqueId());
        if (creativeUser.isRank(CreativeRank.TRAINEE))
            e.setSuffix(Utils.f(creativeUser.getRank().getColoredNameBold()));
    }

}
