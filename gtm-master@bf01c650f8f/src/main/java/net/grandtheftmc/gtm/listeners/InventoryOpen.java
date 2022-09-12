package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryOpen implements Listener {

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        String disp = ChatColor.stripColor(event.getInventory().getTitle());
        Player player = (Player) event.getPlayer();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        Inventory inv = event.getInventory();
        if (!user.isRank(UserRank.BUILDER)) {
            if (inv.getType() == InventoryType.ANVIL
                    || inv.getType() == InventoryType.WORKBENCH
                    || inv.getType() == InventoryType.FURNACE) {
                event.setCancelled(true);
                return;
            }
        }
        if ("Loot Crate".equalsIgnoreCase(disp)) {
            for (int i = 0; i < inv.getSize(); i++) {
                ItemStack item = inv.getItem(i);
                if (item == null)
                    continue;
                AmmoType type = AmmoType.getAmmoType(item.getType(), item.getDurability());
                if (type == null || type.isInInventory())
                    continue;
                gtmUser.addAmmo(type, item.getAmount());
                player.sendMessage(Lang.AMMO_ADD.f(item.getAmount() + "&7 " + type.getGameItem().getDisplayName()));
                inv.setItem(i, null);
            }
        }
    }
}
