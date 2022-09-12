package net.grandtheftmc.vice.areas.listeners;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.areas.AreaManager;
import net.grandtheftmc.vice.areas.builder.AreaBuilder;
import net.grandtheftmc.vice.areas.dao.AreaDAO;
import net.grandtheftmc.vice.areas.obj.Area;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public class AreaBuilderListener implements Listener {

    private final AreaManager areaManager;

    public AreaBuilderListener(Vice plugin, AreaManager areaManager) {
        this.areaManager = areaManager;

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    /**
     * Removes player from area builders upon disconnecting
     * @param event
     */
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AreaBuilder builder = areaManager.getBuilderByUserUUID(player.getUniqueId());

        if (builder == null) return;

        areaManager.getBuilders().remove(builder);

        for (ItemStack contents : player.getInventory()) {
            if (contents == null || !contents.getType().equals(Material.STICK)) continue;

            if (!contents.hasItemMeta() || !contents.getItemMeta().getDisplayName().equals(areaManager.getAreaClaimStick().getItemMeta().getDisplayName())) continue;

            player.getInventory().remove(contents);
        }
    }

    /**
     * Handles the claiming interactions
     * @param event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Action action = event.getAction();
        Block clickedBlock = event.getClickedBlock();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand == null || !hand.getType().equals(Material.STICK)) return;

        if (!areaManager.isBuilding(player.getUniqueId())) return;

        AreaBuilder builder = areaManager.getBuilderByUserUUID(player.getUniqueId());

        event.setCancelled(true);

        if (action.equals(Action.LEFT_CLICK_AIR) && player.isSneaking()) {
            if (builder.getCorner1() == null || builder.getCorner2() == null) {
                player.sendMessage(Lang.VICE.f("&cYou have not set both corners yet"));
                return;
            }

            Area area = areaManager.convertBuilderToArea(builder);

            areaManager.getAreas().add(area);
            areaManager.getBuilders().remove(builder);

            ServerUtil.runTaskAsync(() -> AreaDAO.insert(area));

            player.sendMessage(Lang.VICE.f("&aArea has been created"));
            player.getInventory().setItemInMainHand(null);

            boolean drawNearby = false;

            for(Area areas : areaManager.getAreas()) {
                if (!areas.isOverlapping(area.getMinX(), area.getMaxX(), area.getMinZ(), area.getMaxZ(), area.getWorld().getName()) || areas.getID() == area.getID()) continue;

                player.sendMessage(Lang.VICE.f("&cWarning! This area is overlapping &6" + areas.getName()));
                drawNearby = true;
            }

            if(drawNearby) {
                areaManager.drawNearby(player);

                player.sendMessage(Lang.VICE.f("&eNearby areas have been temporarily drawn on your screen"));
            }
        }

        if (action.equals(Action.LEFT_CLICK_BLOCK)) {
            builder.setCorner1(clickedBlock.getLocation());
            player.sendMessage(Lang.VICE.f("&aCorner #1 has been updated"));
            return;
        }

        if (action.equals(Action.RIGHT_CLICK_BLOCK)) {
            builder.setCorner2(clickedBlock.getLocation());
            player.sendMessage(Lang.VICE.f("&aCorner #2 has been updated"));
            return;
        }

        if (action.equals(Action.RIGHT_CLICK_AIR)) {
            builder.setCorner1(null);
            builder.setCorner2(null);
            player.sendMessage(Lang.VICE.f("&eArea claims have been reset"));
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemDrop().getItemStack();

        if (!item.getType().equals(Material.STICK)) return;

        if (!item.hasItemMeta() || !item.getItemMeta().getDisplayName().equals(areaManager.getAreaClaimStick().getItemMeta().getDisplayName())) return;

        event.getItemDrop().setItemStack(null);

        if (areaManager.isBuilding(player.getUniqueId())) {
            areaManager.getBuilders().remove(areaManager.getBuilderByUserUUID(player.getUniqueId()));
            player.sendMessage(Lang.VICE.f("&eYou are no longer creating this area"));
        }
    }

}
