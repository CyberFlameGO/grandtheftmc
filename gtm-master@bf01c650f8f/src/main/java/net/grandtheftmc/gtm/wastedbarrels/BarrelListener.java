package net.grandtheftmc.gtm.wastedbarrels;

import net.grandtheftmc.gtm.GTM;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class BarrelListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerMoveEvent(PlayerMoveEvent event) {
        if (GTM.getBarrelManager().getUnloadedBarrels().isEmpty()) return;
        GTM.getBarrelManager().getUnloadedBarrels().forEach(location -> {
            if (event.getTo().getWorld() != location.getWorld()) return;
            if (location.distance(event.getTo()) < 10) {
                GTM.getBarrelManager().spawnWastedBarrel(location);
                GTM.getBarrelManager().getUnloadedBarrels().remove(location);
            }
        });
    }

    @EventHandler
    public void entityDamageEntityEvent(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof ArmorStand)) return;
        Player player = (Player) event.getDamager();
        ArmorStand armorStand = (ArmorStand) event.getEntity();
        if(armorStand.getHelmet().getType() != Material.TNT) return;
        WastedBarrel wastedBarrel;
        wastedBarrel = armorStand.hasMetadata("WastedBarrel") ? (WastedBarrel) armorStand.getMetadata("WastedBarrel").get(0).value() : new WastedBarrel(armorStand);
        wastedBarrel.onDamage(event.getDamage(), player);
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_METAL_HIT, 5.0F, 5.0F);
        event.setCancelled(true);
    }
}
