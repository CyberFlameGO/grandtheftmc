package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.UUID;

public class Target implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (!(e.getTarget() instanceof Player))
            return;

        if (e.getTarget().getWorld().getName().equals("spawn")) {
            e.setCancelled(true);
            return;
        }

        if (e.getEntity() instanceof Tameable) {
            Tameable t = (Tameable) e.getEntity();

            if (!(t.getOwner() instanceof Player))
                return;
           
            Player player = (Player) t.getOwner();
            UUID uuid = player.getUniqueId();
            ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
            Player target = (Player) e.getTarget();
            UUID targetUuid = target.getUniqueId();
            ViceUser targetUser = Vice.getUserManager().getLoadedUser(targetUuid);
        }
    }
}
