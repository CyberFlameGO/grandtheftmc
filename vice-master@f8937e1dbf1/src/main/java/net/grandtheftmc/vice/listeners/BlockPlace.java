package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 2017-08-09.
 */
public class BlockPlace implements Listener {

    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Block b = event.getBlockPlaced();
        Player player = event.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        switch (b.getType()) {
            case MOB_SPAWNER: {
                ItemStack hand = event.getItemInHand();
                if(hand.hasItemMeta() && hand.getItemMeta().hasDisplayName()) {
                    EntityType type = EntityType.valueOf(ChatColor.stripColor(hand.getItemMeta().getDisplayName()).toUpperCase().replace(" SPAWNER", "").replace(" ", "_"));
                    CreatureSpawner spawner = (CreatureSpawner)b.getState();
                    spawner.setSpawnedType(type);
                    spawner.update();
                }
            }
        }

        ItemStack item = event.getItemInHand();

        if (item != null && item.getType() == Material.CHEST) {
            if (item.getItemMeta() != null && item.getItemMeta().getDisplayName() != null && item.getItemMeta().getDisplayName().equals(Utils.f("&6&lBackpack"))) {
                event.setCancelled(true);
            }
        }
    }
}
