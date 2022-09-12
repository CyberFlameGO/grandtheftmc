package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Pickup implements Listener {

    public void m(int i) {
        Bukkit.broadcastMessage(String.valueOf(i));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem().getItemStack();
        if (player.isInsideVehicle() || e.getItem().getItemStack().getType() == Material.ARROW) {
            e.setCancelled(true);
            return;
        }
        switch (item.getType()) {
            case PAPER:
                ItemMeta im = item.getItemMeta();
                if (im == null || !im.hasDisplayName())
                    return;
                String disp = ChatColor.stripColor(item.getItemMeta().getDisplayName()).replace("$", "");
                double amnt;
                try {
                    amnt = Double.parseDouble(disp);
                } catch (NumberFormatException ex) {
                    return;
                }
                amnt *= item.getAmount();

                ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
                user.addMoney(amnt);
                player.sendMessage(Utils.f(Lang.MONEY_ADD.toString() + amnt));
                e.getItem().remove();
                e.setCancelled(true);
                Utils.kaching(player);
                ViceUtils.updateBoard(player, user);
                return;
            default:
                break;
        }
        AmmoType type = AmmoType.getAmmoType(item);
        if (type != null) {
            ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            if (type.isInInventory()) {
                user.updateAmmoLater();
                return;
            }
            int i = item.getAmount() + e.getRemaining();
            user.addAmmo(type, i);
            e.setCancelled(true);
            e.getItem().remove();
            player.sendMessage(Lang.AMMO_ADD.f(i + "&7 " + type.getGameItem().getDisplayName()));
        }


        HashMap<ItemStack, ItemStack> replacedVanillaItems = Vice.getItemManager().getReplacedVanilla();
        if(replacedVanillaItems.keySet().stream().anyMatch(value -> value.isSimilar(item))) {
            Optional<Map.Entry<ItemStack, ItemStack>> optItemStack = replacedVanillaItems.entrySet().stream().filter(map -> map.getKey().isSimilar(item)).findFirst();
            if (optItemStack.isPresent()) {
                ItemStack newItem = optItemStack.get().getValue();
                newItem.setAmount(item.getAmount());
                Item newI = player.getWorld().dropItemNaturally(e.getItem().getLocation(), newItem);
                newI.setPickupDelay(0);
                e.getItem().remove();
                e.setCancelled(true);

            }
        }
    }
}
