package net.grandtheftmc.vice.dropship;

import net.grandtheftmc.core.gui.ConfirmationMenu;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.dropship.event.DropShipStartEvent;
import net.grandtheftmc.vice.items.ItemManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class DropShipHandler<T extends JavaPlugin> implements Component<DropShipManager, T> {

    private final DropShipManager dropShipManager;
    private final ItemStack dropitem;
    private final ItemStack majorDropitem;
    private final T plugin;

    public DropShipHandler(DropShipManager dropShipManager, ItemManager itemManager, T plugin) {
        this.dropShipManager = dropShipManager;
        this.plugin = plugin;
        this.dropitem = itemManager.getItem("dropship").getItem();
        this.majorDropitem = itemManager.getItem("majordropship").getItem();

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public DropShipManager onEnable(T plugin) {
        return this.dropShipManager;
    }

    @Override
    public DropShipManager onDisable(T plugin) {
        return this.dropShipManager;
    }

    @EventHandler
    protected final void onItemInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) return;

        ItemStack item = event.getItem();
        boolean small = false, major = false;

        if (item == null)
            return;

        if (item.isSimilar(this.dropitem))
            small = true;

        if (item.isSimilar(this.majorDropitem))
            major = true;

        if (!small && !major) return;

        //Check if a player can start a drop ship.
        if (!this.dropShipManager.canStartDrop()) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(C.RED + "You cannot activate a dropship until the current event has finished.");//TODO, FORMAT.
            return;
        }

        boolean finalMajor = major;
        ConfirmationMenu menu = new ConfirmationMenu(this.plugin, item.clone()) {
            @Override
            protected void onConfirm(InventoryClickEvent e, Player p) {
                dropShipManager.getClosestArea(finalMajor, event.getPlayer(), area -> {
                    if (area == null) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(C.RED + "Couldn't find a nearby settlement.");
                        return;
                    }

                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                    }

                    event.getPlayer().updateInventory();

                    dropShipManager.startDropShop(finalMajor, event.getPlayer(), area);
                });
            }

            @Override
            protected void onDeny(InventoryClickEvent e, Player p) {
                event.setCancelled(true);
            }
        };

        menu.open(event.getPlayer());
    }

    @EventHandler
    protected final void onDropshipStart(DropShipStartEvent event) {
        event.getDropShip().restock();
    }

    @EventHandler
    protected final void onFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (!this.dropShipManager.userDamageMap.containsKey(player.getUniqueId())) return;

        event.setCancelled(true);
        this.dropShipManager.userDamageMap.remove(player.getUniqueId());
    }
}
