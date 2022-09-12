package net.grandtheftmc.core.editmode;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Interact implements Listener {

    @EventHandler
    public void onInteractEntity(PlayerInteractEntityEvent e) {
        Player player = e.getPlayer();
        Entity en = e.getRightClicked();
        if (Core.getWorldManager().usesEditMode(en.getWorld().getName()))
            checkTypes(e, player.getUniqueId(), en.getType());
    }

    @EventHandler
    public void onInteractAtEntity(PlayerInteractAtEntityEvent e) {
        Player player = e.getPlayer();
        Entity en = e.getRightClicked();
        if (Core.getWorldManager().usesEditMode(en.getWorld().getName()))
            checkTypes(e, player.getUniqueId(), en.getType());
    }

    private void checkTypes(Cancellable e, UUID u, EntityType et) {
        switch (et) {
            case PAINTING:
            case ITEM_FRAME:
            case ARMOR_STAND:
                if (!Core.getUserManager().getLoadedUser(u).hasEditMode())
                    e.setCancelled(true);
                return;
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        if (!Core.getWorldManager().usesEditMode(player.getWorld().getName()) || e.getAction() != Action.RIGHT_CLICK_BLOCK)
            return;
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item != null) {
            switch (item.getType()) {
                case ITEM_FRAME:
                case WATER_BUCKET:
                case LAVA_BUCKET:

                    if (!u.hasEditMode())
                        e.setCancelled(true);
                    break;
                case BUCKET:
                    switch (e.getClickedBlock().getType()) {
                        case WATER:
                        case STATIONARY_WATER:
                        case LAVA:
                        case STATIONARY_LAVA:
                            if (!u.hasEditMode())
                                e.setCancelled(true);
                        default:
                            break;
                    }
                default:
                    break;
            }
        }

        switch (e.getClickedBlock().getType()) {
            case CHEST:
            case TRAPPED_CHEST:
            case DROPPER:
            case HOPPER:
            case FURNACE:
            case BURNING_FURNACE:
            case BREWING_STAND:
                if (!Core.getSettings().canOpenChests() && !u.hasEditMode())
                    e.setCancelled(true);
                break;

            case FLOWER_POT:
                if (!u.hasEditMode()) {
                    e.setCancelled(true);
                    e.getClickedBlock().getState().update();
                }
                break;

            default:
                break;
        }
    }

}
