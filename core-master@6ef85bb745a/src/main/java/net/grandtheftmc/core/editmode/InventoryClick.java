package net.grandtheftmc.core.editmode;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;

public class InventoryClick implements Listener {

    //@EventHandler idk why this is here ~ Tim.
    public void onClick(InventoryClickEvent e) {
        Player player = (Player) e.getWhoClicked();
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (Core.getWorldManager().usesEditMode(player.getWorld().getName()) && !u.hasEditMode()) {
            e.setCancelled(false);
        }

    }

}
