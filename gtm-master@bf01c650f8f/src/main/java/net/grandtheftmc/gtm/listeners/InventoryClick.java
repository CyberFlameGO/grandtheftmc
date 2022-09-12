package net.grandtheftmc.gtm.listeners;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.ItemStackEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.util.ItemStackManager;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.md_5.bungee.api.ChatColor;

public class InventoryClick implements Listener {

    @EventHandler
    public void onPickup(InventoryPickupItemEvent event) {
        if (event.getInventory() == null) return;
//        if (event.getItem() == null) return;
//        if (event.getItem().getLocation().getWorld().getName().equalsIgnoreCase("minesantos")) {
//            if (event.getInventory().getType() == InventoryType.HOPPER) {
//                event.setCancelled(true);
//            }
//        }

        if (event.getInventory().getType() == InventoryType.HOPPER) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void equipEventRunner(InventoryClickEvent e){
        boolean shift = false, numberkey = false;
        Player player = (Player)e.getWhoClicked();
        if(e.isCancelled()) return;
        if(e.getClick().equals(ClickType.SHIFT_LEFT) || e.getClick().equals(ClickType.SHIFT_RIGHT)){
            shift = true;
        }
        if(e.getClick().equals(ClickType.NUMBER_KEY)){
            numberkey = true;
        }
        if(e.getSlotType() != InventoryType.SlotType.ARMOR && e.getSlotType() != InventoryType.SlotType.QUICKBAR && e.getSlotType() != InventoryType.SlotType.CONTAINER) return;
        if(e.getClickedInventory() != null && !e.getClickedInventory().getType().equals(InventoryType.PLAYER)) return;
        if (!e.getInventory().getType().equals(InventoryType.CRAFTING) && !e.getInventory().getType().equals(InventoryType.PLAYER)) return;
        if(!(e.getWhoClicked() instanceof Player)) return;
        EquipArmorType newEquipArmorType = EquipArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
        if(!shift && newEquipArmorType != null && e.getSlot() != newEquipArmorType.getSlot() && newEquipArmorType!=EquipArmorType.CUSTOM){
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
            return;
        }
        if(shift){
            newEquipArmorType = EquipArmorType.matchType(e.getCurrentItem());
            if(newEquipArmorType != null){
                boolean equipping = true;
                if(e.getSlot()== newEquipArmorType.getSlot()){
                    equipping = false;
                }
                if(newEquipArmorType==EquipArmorType.CUSTOM) {
                    if(e.getCurrentItem()!=null && e.getCurrentItem().getType().toString().contains("LEATHER_"))
                        e.setCancelled(true);
                    return;
                }
                if(equipping && e.getCurrentItem() !=null && e.getCurrentItem().getAmount()>1) {
                    e.setCancelled(true);
                    player.sendMessage(Lang.GTM.f("&cYou cannot equip stacked armor!"));
                    return;
                }
                if(newEquipArmorType.equals(EquipArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newEquipArmorType.equals(EquipArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newEquipArmorType.equals(EquipArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newEquipArmorType.equals(EquipArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newEquipArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null,EquipArmorType.fromSlot(e.getSlot()));
                    Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                    if(armorEquipEvent.isCancelled()){
                        e.setCancelled(true);
                    }
                }
            }
        }else{
            ItemStack newArmorPiece = e.getCursor();
            ItemStack oldArmorPiece = e.getCurrentItem();
            if(numberkey){
                if(e.getClickedInventory().getType().equals(InventoryType.PLAYER)){// Prevents shit in the 2by2 crafting
                    // e.getClickedInventory() == The players inventory
                    // e.getHotBarButton() == key people are pressing to equip or unequip the item to or from.
                    // e.getRawSlot() == The slot the item is going to.
                    // e.getSlot() == Armor slot, can't use e.getRawSlot() as that gives a hotbar slot ;-;
                    ItemStack hotbarItem = e.getClickedInventory().getItem(e.getHotbarButton());
                    if(hotbarItem != null){// Equipping
                        newEquipArmorType = EquipArmorType.matchType(hotbarItem);
                        newArmorPiece = hotbarItem;
                        oldArmorPiece = e.getClickedInventory().getItem(e.getSlot());
                    }else{// Unequipping
                        newEquipArmorType = EquipArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
                    }
                }
            }
            else{
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                newEquipArmorType = EquipArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
            }

            if(newEquipArmorType == EquipArmorType.CUSTOM && e.getSlotType()!= InventoryType.SlotType.ARMOR)
                return;
            if(newArmorPiece!=null && newArmorPiece.getAmount()>1 && e.getSlotType()== InventoryType.SlotType.ARMOR) {
                e.setCancelled(true);
                player.sendMessage(Lang.GTM.f("&cYou cannot equip stacked armor!"));
                return;
            }

            if(newEquipArmorType != null && (e.getSlot() == newEquipArmorType.getSlot() || newEquipArmorType == EquipArmorType.CUSTOM)){
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.DRAG;
                if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newEquipArmorType, oldArmorPiece, newArmorPiece, EquipArmorType.fromSlot(e.getSlot()));
                Bukkit.getServer().getPluginManager().callEvent(armorEquipEvent);
                if(armorEquipEvent.isCancelled()){
                    e.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
    	
        Player player = (Player) e.getWhoClicked();
        Inventory inv = e.getInventory();
        GTMUser user = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        if (e.getClickedInventory() == null || e.getClickedInventory().getType() == InventoryType.CRAFTING)
            return;
        if (e.getClickedInventory().getType() == InventoryType.PLAYER) {
            if (e.getSlot() == 17) {
                e.getWhoClicked().closeInventory();
                GTM.getBackpackManager().openBackpack(player);
                e.setCancelled(true);
                return;
            } else if (e.getSlot() == 16) {
                e.getWhoClicked().closeInventory();
                MenuManager.openMenu(player, "ammopouch");
                e.setCancelled(true);
                return;
            }
        }
        String title = ChatColor.stripColor(e.getClickedInventory().getTitle());
        if (title.startsWith("Kit Preview: ")) {
            e.setCancelled(true);
            return;
        }
        if (title.endsWith("Corpse") && e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
            e.setCancelled(true);
            return;
        }
        
        if (user != null){
	        if(user.getBackpackOpen()){
	            ItemStack selected = e.isShiftClick() ? e.getCurrentItem() : e.getCursor();
	            if(selected.getType().toString().contains("SHULKER_BOX")){
	                e.setCancelled(true);
	                return;
	            }
	        }
        }

        /*
         * m("--------------------------------------------------"); m("Inv = " +
         * inv.getTitle() + " - " + inv.getType()); m("ClickedInv = " +
         * e.getClickedInventory().getTitle() + " - " +
         * e.getClickedInventory().getType()); m("Slot = " + e.getSlot() + " ("
         * + e.getRawSlot() + ") - " + e.getSlotType()); m("Item in Slot = " +
         * (inv.getItem(e.getSlot()) == null ? null :
         * inv.getItem(e.getSlot()).getType().toString())); m("CurrentItem = " +
         * (e.getCurrentItem() == null ? null :
         * e.getCurrentItem().getType().toString())); m("Cursor Item = " +
         * (e.getCursor() == null ? null : e.getCursor().getType().toString()));
         * m("Action = " + e.getAction() + " - " + e.getClick()); m("Hotbar = "
         * + e.getHotbarButton());
         */

        ItemStack current = e.getCurrentItem();
        ItemStack cursor = e.getCursor();
        if (GTMUtils.isPhoneOrGPS(cursor) && e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            player.updateInventory();
            return;
        }
        switch (e.getAction()) {
            case MOVE_TO_OTHER_INVENTORY:
                if (!GTMUtils.isPhoneOrGPS(current) || inv.getType() == InventoryType.CRAFTING
                        || Objects.equals(inv, e.getClickedInventory()))
                    break;
                e.setCancelled(true);
                player.updateInventory();
                break;
            case PLACE_ALL:
            case PLACE_ONE:
            case PLACE_SOME:
                if (!GTMUtils.isPhoneOrGPS(cursor) || e.getClickedInventory().getType() == InventoryType.PLAYER)
                    break;
                e.setCancelled(true);
                player.updateInventory();
                break;
            case SWAP_WITH_CURSOR:
                if (!GTMUtils.isPhoneOrGPS(cursor) || e.getClickedInventory().getType() == InventoryType.PLAYER)
                    break;
                e.setCancelled(true);
                player.updateInventory();
                break;
            case HOTBAR_SWAP:
                ItemStack i = player.getInventory().getItem(e.getHotbarButton());
                if (!GTMUtils.isPhoneOrGPS(i) || e.getClickedInventory().getType() == InventoryType.PLAYER)
                    return;
                e.setCancelled(true);
                player.updateInventory();
                break;
            default:
                break;
        }

    }

    @EventHandler(ignoreCancelled = true)
    protected final void onItemClick(InventoryClickEvent event) {
        if(event.getInventory() == null) return;
        if(event.getInventory().getType() != InventoryType.CRAFTING) return;

        if(event.getRawSlot() > 0 && event.getRawSlot() < 5) {
            event.setCancelled(true);
            ((Player)event.getWhoClicked()).updateInventory();
        }
    }

    @EventHandler
    public void onSwitchToOffhand(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void onItemStack(ItemStackEvent event){
    	
    	Weapon weapon = GTMGuns.getInstance().getWeaponManager().getWeapon(event.getItemStack()).orElse(null);
    	if (weapon != null){
    		switch (weapon.getWeaponType()){
				case THROWABLE:
					// throwable weapons are stackable
					event.setCancelled(false);
					break;
				default:
					event.setCancelled(true);
					break;
    		}
    	}
    	else{
    		// if the itemstack manager says this type cannot be stacked
    		if(!ItemStackManager.STACKABLES.containsKey(event.getItemStack())){
    			event.setCancelled(true);
    			return;
    		}
    	}
    	
    	
    }
}
