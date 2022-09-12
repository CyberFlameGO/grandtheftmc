package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.Menu;
import net.grandtheftmc.core.menus.MenuClickEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.menus.MenuOpenEvent;
import net.grandtheftmc.core.util.Utils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Timothy Lampen on 2017-09-22.
 */
public class MenuListener implements Listener{

    @EventHandler
    public void onOpen(MenuOpenEvent e) {
        Player player = e.getPlayer();
        FPlayer fme = FPlayers.getInstance().getByPlayer(player);
        Menu menu = e.getMenu();

        switch (menu.getName()) {
            case "carteltop" : {
                player.sendMessage(Lang.VICE.f("&7Organizing &6" + Factions.getInstance().getAllFactions().size() + "&7 factions..."));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        // Get all Factions and remove non player ones.
                        ArrayList<Faction> factionList = Factions.getInstance().getAllFactions();
                        factionList.remove(Factions.getInstance().getWilderness());
                        factionList.remove(Factions.getInstance().getSafeZone());
                        factionList.remove(Factions.getInstance().getWarZone());
                        Collections.sort(factionList, new Comparator<Faction>() {
                            @Override
                            public int compare(Faction o1, Faction o2) {

                                double f1Worth = o1.getStash();
                                for(int i =0; i < o1.getAllClaims().size(); i++ )
                                    f1Worth += Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * i);

                                double f2Worth = o2.getStash();
                                for(int i =0; i < o2.getAllClaims().size(); i++ )
                                    f2Worth += Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * i);
                                //takes 0.08ms to complete the entire sort.

                                if(f1Worth < f2Worth)
                                    return 1;
                                else if(f1Worth > f2Worth)
                                    return -1;
                                return 0;
                            }
                        });
                        boolean containsSender = false;
                        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
                        for(int i = 0; i<(factionList.size()>=20 ? 20 : factionList.size()); i++) {
                            Faction f = factionList.get(i);
                            if(f.getFPlayers().contains(fme))
                                containsSender = true;
                            items.add(generateRankingItem(fme, f, i+1));
                        }
                        final boolean finalContainsSender = containsSender;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if(!player.isOnline())
                                    return;
                                Inventory inv = Bukkit.createInventory(player, 54, Utils.f("&cTop Cartels"));
                                setPhoneDefaults(inv);
                                int[] slots = new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42};
                                int counter = 0;
                                for (ItemStack is : items) {
                                    int slot = slots[counter];
                                    inv.setItem(slot, is);
                                    counter++;
                                }
                                if (!finalContainsSender && !Factions.getInstance().getWilderness().getFPlayers().contains(fme)) {
                                    inv.setItem(49, generateRankingItem(fme, fme.getFaction(), -1));
                                }
                                inv.setItem(47, Utils.createItem(Material.REDSTONE, "&c&lClose Menu"));
                                player.openInventory(inv);
                                player.updateInventory();
                            }
                        }.runTaskLater(P.p, 5);
                    }
                }.runTaskAsynchronously(P.p);
                break;
            }
        }
    }

    @EventHandler
    public void onClick(MenuClickEvent e){
        Player player = e.getPlayer();
        FPlayer fme = FPlayers.getInstance().getByPlayer(player);
        Menu menu = e.getMenu();
        ItemStack clicked = e.getItem();
        if(clicked==null || clicked.getType()==Material.STAINED_GLASS_PANE || clicked.getType()==Material.AIR)
            return;

        switch (menu.getName()) {
            case "carteltop" : {
                switch (clicked.getType()) {
                    case REDSTONE:
                        player.closeInventory();
                        break;
                }
                break;
            }
        }
    }

    private ItemStack generateRankingItem(FPlayer fme, Faction f, int rank){
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
        SkullMeta im = (SkullMeta)item.getItemMeta();
        im.setDisplayName((rank==-1 ? ChatColor.GREEN + "Your Faction" : ChatColor.GREEN +"#" + ChatColor.GOLD + rank + ChatColor.GREEN + " Rank"));
        ArrayList<String> lore = new ArrayList<String>();
        lore.add(ChatColor.GOLD + "Name: " + ChatColor.GRAY + f.getTag());
        lore.add(ChatColor.GOLD + "Owner: " + ChatColor.GRAY + (f.getFPlayerAdmin()==null ? "Wilderness" : f.getFPlayerAdmin().getName()));
        im.setOwner(f.getFPlayerAdmin().getName());

        double worth = f.getStash();
        for(int i =0; i < f.getAllClaims().size(); i++ )
            worth += Conf.econCostClaimWilderness + (Conf.econCostClaimWilderness * Conf.econClaimAdditionalMultiplier * i);
        lore.add(ChatColor.GOLD + "Worth: " + ChatColor.GRAY + Utils.formatMoney(worth));

        lore.add(ChatColor.GOLD + "Members: " + ChatColor.GRAY + f.getFPlayers().size());
        lore.add(ChatColor.GOLD + "Relation: " + f.getRelationTo(fme).getColor() + StringUtils.capitalize(f.getRelationTo(fme).nicename));

        im.setLore(lore);
        item.setItemMeta(im);
        return item;
    }


    public void setPhoneDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
    }

    public void setPhoneDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) inv.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) inv.setItem(i, blackGlass);
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48,
                49, 50, 51})
            inv.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) inv.setItem(i, lightGlass);
    }

    public void setGPSDefaults(MenuOpenEvent e) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53}) e.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7}) e.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            e.setItem(i, grayGlass);
    }

    public void setGPSDefaults(Inventory inv) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        for (int i : new int[]{0, 9, 18, 27, 36, 45, 8, 17, 26, 35, 44, 53})
            inv.setItem(i, whiteGlass);
        for (int i : new int[]{1, 2, 3, 4, 5, 6, 7})
            inv.setItem(i, blackGlass);
        for (int i : new int[]{10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34, 37,
                38, 39, 40, 41, 42, 43, 46, 47, 48, 49, 50, 51, 52})
            inv.setItem(i, grayGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e) {
        this.setConfirmDefaults(e, "&a&lConfirm", "&c&lCancel");
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }

    private void setConfirmDefaults(MenuOpenEvent e, String confirmMessage, String cancelMessage, List<String> confirmLore, List<String> cancelLore) {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack greenGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 5, confirmMessage, confirmLore == null ? new ArrayList<>() : confirmLore);
        ItemStack redGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 14, cancelMessage, cancelLore == null ? new ArrayList<>() : cancelLore);
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) e.setItem(i, whiteGlass);
        for (int i : new int[]{2, 3, 4, 5, 6}) e.setItem(i, blackGlass);
        for (int i : new int[]{13, 22, 31, 40, 49,}) e.setItem(i, grayGlass);
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) e.setItem(i, lightGlass);
        for (int i : new int[]{11, 12, 20, 21, 29, 30, 38, 39, 47, 48}) e.setItem(i, greenGlass);
        for (int i : new int[]{14, 15, 23, 24, 32, 33, 41, 42, 50, 51}) e.setItem(i, redGlass);
    }
}
