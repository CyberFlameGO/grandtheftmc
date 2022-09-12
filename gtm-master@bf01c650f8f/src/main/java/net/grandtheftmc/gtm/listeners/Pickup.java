package net.grandtheftmc.gtm.listeners;

import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.items.GameItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.users.GTMUser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Pickup implements Listener {

    public void m(int i) {
        Bukkit.broadcastMessage(String.valueOf(i));
    }

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();
        if (player.getGameMode() == GameMode.SPECTATOR) {
            e.setCancelled(true);
            return;
        }

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

                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.addMoney(amnt);
                player.sendMessage(Utils.f(Lang.MONEY_ADD.toString() + amnt));
                e.getItem().remove();
                e.setCancelled(true);
                Utils.kaching(player);
                GTMUtils.updateBoard(player, user);
                return;
            default:
                break;
        }
        AmmoType type = AmmoType.getAmmoType(item.getType(), item.getDurability());
        if (type != null) {
            GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
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
    }
}
