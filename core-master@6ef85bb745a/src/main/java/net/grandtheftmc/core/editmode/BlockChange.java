package net.grandtheftmc.core.editmode;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;

public class BlockChange implements Listener {

    @EventHandler
    public void blockChange(BlockFromToEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBurn(BlockBurnEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);

    }

    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getLocation().getWorld().getName())) {
            e.blockList().clear();
        }
    }

    @EventHandler
    public void onIgnite(BlockIgniteEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName())) {
            if (e.getCause() == IgniteCause.FLINT_AND_STEEL) {
                User u = Core.getUserManager().getLoadedUser(e.getPlayer().getUniqueId());
                if (!u.hasEditMode())
                    e.setCancelled(true);
            } else
                e.setCancelled(true);
        }
    }

    // @EventHandler This event disables redstone and stuff
    public void onBlockPhysics(BlockPhysicsEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onLeavesDecay(LeavesDecayEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void noUproot(PlayerInteractEvent event) {
        if (Core.getWorldManager().usesEditMode(event.getPlayer().getWorld().getName()) && event.getAction() == Action.PHYSICAL && event.getClickedBlock().getType() == Material.SOIL)
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockForm(BlockFormEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockSpread(BlockSpreadEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName()))
            e.setCancelled(true);
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (Core.getWorldManager().usesEditMode(e.getBlock().getWorld().getName())) e.setCancelled(true);
    }

}
