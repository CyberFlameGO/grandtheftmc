package net.grandtheftmc.gtm.items;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;

public class BackpackManager implements Listener {

    private final Map<Integer, Inventory> corpses = new HashMap<>();

    public void openBackpack(Player player) {
        this.openBackpack(player, GTM.getUserManager().getLoadedUser(player.getUniqueId()), Core.getUserManager().getLoadedUser(player.getUniqueId()));
    }

    public Inventory getBackpack(Player player, boolean monitor) {
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        User coreUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        int size = 9 * GTMUtils.getBackpackRows(coreUser.getUserRank());
        Inventory inv = Bukkit.createInventory(null, size, monitor ? Utils.f(player.getName()) : Utils.f("&6&lBackpack"));
        ItemStack[] backpackContents = gtmUser.getBackpackContents();
        if (backpackContents != null)
            for (int i = 0; i < backpackContents.length && i < size; i++)
                inv.setItem(i, backpackContents[i]);
        return inv;
    }

    public void openBackpack(Player player, GTMUser user, User u) {
    	if (user == null){
    		player.sendMessage(Lang.GTM.f("&7Your backpack may not be opened at this time! Please relog!"));
    		return;
    	}
    	
        if (user.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't open your backpack in jail!"));
            return;
        }
        if (user.isInCombat()) {
            player.sendMessage(Lang.COMBATTAG.f("&7You can't open your backpack in combat!"));
            return;
        }
        if (user.getBackpackOpen()) {
            player.sendMessage(Lang.GTM.f("&7Your backpack may not be opened at this time!"));
            return;
        }
        if (player.getOpenInventory() != null
                && Objects.equals("Backpack", ChatColor.stripColor(player.getOpenInventory().getTitle())))
            return;
        Inventory inv = this.getBackpack(player, false);
        
        // remove stuck phones and GPS
        inv.remove(Material.COMPASS);
        inv.remove(Material.WATCH);
        
        player.openInventory(inv);
        user.setBackpackOpen(true);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event){
        Inventory inv = event.getInventory();
        
        // ignore inven events that aren't backback
        if (!"backpack".equalsIgnoreCase(ChatColor.stripColor(inv.getTitle())))
            return;
        
        // grab event variables
        Inventory clickedInven = event.getClickedInventory();
        InventoryAction action = event.getAction();
        ItemStack item = event.getCurrentItem();
        ItemStack cur = event.getCursor();
        
        // if they are swapping 
        if (action == InventoryAction.HOTBAR_SWAP || action == InventoryAction.HOTBAR_MOVE_AND_READD){
        	
        	// if they are using number key
        	if (event.getClick() == ClickType.NUMBER_KEY){
	        	
	        	int hotbar = event.getHotbarButton();
	        	
	        	// if there was a click on the inventory
	        	if (clickedInven != null){
	        		
	        		// get the item they are swapping with
	        		ItemStack swappingItem = event.getWhoClicked().getInventory().getItem(hotbar);

	        		if (swappingItem != null){
	        			if (swappingItem.getType() == Material.COMPASS || swappingItem.getType() == Material.WATCH){
	        				event.setCancelled(true);
	        			}
	        		}
	        	}
        	}
        }
        
        if (item != null){
        	
        	// stops moving of GPS and Phone
        	if (item.getType() == Material.COMPASS || item.getType() == Material.WATCH){
        		event.setCancelled(true);
        	}
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Inventory inv = e.getInventory();
        if (!Objects.equals("Backpack", ChatColor.stripColor(inv.getTitle())) && Bukkit.getPlayer(inv.getTitle()) != null) {
            Player target = Bukkit.getPlayer(inv.getTitle());
            if (target.getOpenInventory() != null && Objects.equals("Backpack", ChatColor.stripColor(target.getOpenInventory().getTitle())))
                target.getOpenInventory().close();
            GTMUser user = GTM.getUserManager().getLoadedUser(target.getUniqueId());
            user.setBackpackContents(inv.getContents());
            user.setBackpackOpen(false);
            return;
        }
        if (!"backpack".equalsIgnoreCase(ChatColor.stripColor(inv.getTitle())))
            return;
        Player player = (Player) e.getPlayer();
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        user.setBackpackContents(inv.getContents());
        user.setBackpackOpen(false);
    }

    private final int[] glassSlots = new int[]{0, 5, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17};
    private final ItemStack glass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");

    public void kitPreview(Player player, Kit kit) {
        String name = kit.getName();
        JobMode mode = JobMode.getModeOrNull(kit.getName());
        UserRank ur = UserRank.getUserRankOrNull(kit.getName());
        GTMRank rank = GTMRank.getRankOrNull(kit.getName());
        if (mode != null)
            name = mode.getColoredNameBold();
        else if (ur != null)
            name = ur.getColoredNameBold();
        else if (rank != null)
            name = rank.getColoredNameBold();
        Inventory inv = Bukkit.createInventory(null, 54, Utils.f("&b&lKit Preview: " + name));

        for (int i : this.glassSlots)
            inv.setItem(i, this.glass);
        inv.setItem(1, kit.getHelmet() == null ? this.glass : kit.getHelmet().getItem().getItem());
        inv.setItem(2, kit.getChestPlate() == null ? this.glass : kit.getChestPlate().getItem().getItem());
        inv.setItem(3, kit.getLeggings() == null ? this.glass : kit.getLeggings().getItem().getItem());
        inv.setItem(4, kit.getBoots() == null ? this.glass : kit.getBoots().getItem().getItem());
        inv.setItem(6, kit.getOffHand() == null ? this.glass : kit.getOffHand().getItem().getItem());
        for (int i = 0; i < kit.getItems().size(); i++) {
            if (i < 9)
                inv.setItem(45 + i, kit.getItems().get(i));
            else
                inv.setItem(9 + i, kit.getItems().get(i));
        }
        player.openInventory(inv);
    }

}
