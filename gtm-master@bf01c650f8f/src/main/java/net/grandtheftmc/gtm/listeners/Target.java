package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;

import java.util.Objects;
import java.util.UUID;

public class Target implements Listener {

    @EventHandler
    public void onTarget(EntityTargetEvent e) {
        if (!(e.getEntity() instanceof Tameable) || !(e.getTarget() instanceof Player))
            return;
        Tameable t = (Tameable) e.getEntity();
        if (!(t.getOwner() instanceof Player))
            return;
        if ("spawn".equalsIgnoreCase(e.getTarget().getWorld().getName())) {
            e.setCancelled(true);
            return;
        }
        Player player = (Player) t.getOwner();
        UUID uuid = player.getUniqueId();
        GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
        Player target = (Player) e.getTarget();
        UUID targetUuid = target.getUniqueId();
        GTMUser targetUser = GTM.getUserManager().getLoadedUser(targetUuid);

//        Gang gang = user.getGang();
        Gang gang = GangManager.getInstance().getGangByMember(uuid).orElse(null);

//        Gang targetGang = targetUser.getGang();
        Gang targetGang = GangManager.getInstance().getGangByMember(targetUuid).orElse(null);

        if (targetGang != null && gang != null) {
            if (Objects.equals(targetGang, gang)) {
                e.setCancelled(true);
                return;
            }

            if (targetGang.isAllied(gang)) {
                e.setCancelled(true);
                return;
            }
        }

        switch (user.getJobMode()) {
        case COP:
            JobMode mode = targetUser.getJobMode();
            if (user.getJobMode() == JobMode.COP) {
                e.setCancelled(true);
                break;
            }
            if (mode == JobMode.CRIMINAL && targetUser.getWantedLevel() == 0) {
                e.setCancelled(true);
                break;
            }
            default:
        break;
        }
    }

}
