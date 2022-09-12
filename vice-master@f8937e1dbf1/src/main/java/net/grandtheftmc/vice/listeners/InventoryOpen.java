package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InventoryOpen implements Listener {

    private final static List<Material> BANNED_MATERIALS = new ArrayList<>(Arrays.asList(Material.WOOD_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLD_SWORD, Material.DIAMOND_SWORD, Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS, Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_LEGGINGS, Material.GOLD_BOOTS, Material.GOLD_LEGGINGS, Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.DIAMOND_BOOTS, Material.DIAMOND_LEGGINGS));
    @EventHandler
    public void onOpen(InventoryOpenEvent e) {
        Inventory inv = e.getInventory();
        Player player = (Player) e.getPlayer();

        String disp = ChatColor.stripColor(e.getInventory().getTitle());
        if(inv.getHolder()==null)
            return;
        for(ItemStack is : inv.getStorageContents()) {
            if(is!=null && is.getDurability()==0 && BANNED_MATERIALS.contains(is.getType())) {
                inv.removeItem(is);
            }
        }

        if (!"Loot Crate".equalsIgnoreCase(disp))
            return;
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item == null)
                continue;
            AmmoType type = AmmoType.getAmmoType(item.getType(),item.getDurability());
            if (type == null||type.isInInventory())
                continue;
            user.addAmmo(type, item.getAmount());
            player.sendMessage(Lang.AMMO_ADD.f(item.getAmount() + "&7 " + type.getGameItem().getDisplayName()));
            inv.setItem(i, null);
        }
    }

    @EventHandler
    protected final void onItemPickup(PlayerPickupItemEvent event) {
        Player player = (Player) event.getPlayer();
        ItemStack itemStack = event.getItem().getItemStack();

        if(itemStack != null && itemStack.getDurability()==0 && BANNED_MATERIALS.contains(itemStack.getType())) {
            event.getItem().remove();
        }
    }
}
