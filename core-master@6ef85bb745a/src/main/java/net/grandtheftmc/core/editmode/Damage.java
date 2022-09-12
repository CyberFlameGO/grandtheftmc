package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

public class Damage implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        //Faster to check a boolean first than comparing entities.
        if (Core.getWorldManager().usesEditMode(e.getEntity().getWorld().getName())
                && e.getEntity().getType() == EntityType.ARMOR_STAND
                && (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION
                || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            e.setCancelled(true);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!Core.getWorldManager().usesEditMode(e.getEntity().getWorld().getName()))
            return;

        Entity entity = e.getEntity();
        switch (entity.getType()) {
            case ITEM_FRAME:
                if (e.getDamager() instanceof Player
                        && Core.getUserManager().getLoadedUser(e.getDamager().getUniqueId()).hasEditMode())
                    break;
                e.setCancelled(true);
                return;
            case ARMOR_STAND:
                if (e.getDamager() instanceof Player
                        && Core.getUserManager().getLoadedUser(e.getDamager().getUniqueId()).hasEditMode()) {
                    ItemStack item = ((Player) e.getDamager()).getInventory().getItemInMainHand();
                    if (item != null && item.getType() == Material.DIAMOND_SWORD && entity.isInvulnerable())
                        entity.remove();
                    break;
                }

                e.setCancelled(true);
                return;
            default:
        }
    }

}
