package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.events.ArmorEquipEvent;
import net.grandtheftmc.vice.events.EquipArmorType;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryClick implements Listener {

    @EventHandler
    public final void equipEventRunner(final InventoryClickEvent e){
        boolean shift = false, numberkey = false;
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
        if(e.getCurrentItem() == null) return;
        EquipArmorType newEquipArmorType = EquipArmorType.matchType(shift ? e.getCurrentItem() : e.getCursor());
        if(!shift && newEquipArmorType != null && e.getRawSlot() != newEquipArmorType.getSlot()){
            // Used for drag and drop checking to make sure you aren't trying to place a helmet in the boots place.
            return;
        }
        if(shift){
            newEquipArmorType = EquipArmorType.matchType(e.getCurrentItem());
            if(newEquipArmorType != null){
                boolean equipping = true;
                if(e.getRawSlot() == newEquipArmorType.getSlot()){
                    equipping = false;
                }
                if(newEquipArmorType.equals(EquipArmorType.HELMET) && (equipping ? e.getWhoClicked().getInventory().getHelmet() == null : e.getWhoClicked().getInventory().getHelmet() != null) || newEquipArmorType.equals(EquipArmorType.CHESTPLATE) && (equipping ? e.getWhoClicked().getInventory().getChestplate() == null : e.getWhoClicked().getInventory().getChestplate() != null) || newEquipArmorType.equals(EquipArmorType.LEGGINGS) && (equipping ? e.getWhoClicked().getInventory().getLeggings() == null : e.getWhoClicked().getInventory().getLeggings() != null) || newEquipArmorType.equals(EquipArmorType.BOOTS) && (equipping ? e.getWhoClicked().getInventory().getBoots() == null : e.getWhoClicked().getInventory().getBoots() != null)){
                    ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), ArmorEquipEvent.EquipMethod.SHIFT_CLICK, newEquipArmorType, equipping ? null : e.getCurrentItem(), equipping ? e.getCurrentItem() : null);
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
            }else{
                // e.getCurrentItem() == Unequip
                // e.getCursor() == Equip
                newEquipArmorType = EquipArmorType.matchType(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR ? e.getCurrentItem() : e.getCursor());
            }
            if(newEquipArmorType != null && e.getRawSlot() == newEquipArmorType.getSlot()){
                ArmorEquipEvent.EquipMethod method = ArmorEquipEvent.EquipMethod.DRAG;
                if(e.getAction().equals(InventoryAction.HOTBAR_SWAP) || numberkey) method = ArmorEquipEvent.EquipMethod.HOTBAR_SWAP;
                ArmorEquipEvent armorEquipEvent = new ArmorEquipEvent((Player) e.getWhoClicked(), method, newEquipArmorType, oldArmorPiece, newArmorPiece);
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
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        ItemStack clicked = e.getCurrentItem();
        ClickType click = e.getClick();
        if (e.getClickedInventory() == null)
            return;

        if (user.getBooleanFromStorage(BooleanStorageType.BACKPACK_OPEN)) {
//            if(e.getClick()==ClickType.NUMBER_KEY){
//                e.setCancelled(true);
//                return;
//            }
//            ItemStack drug = click.isShiftClick() ? e.getCurrentItem() : e.getCursor();
//            if(drug!=null){
//                if(drug.getType().toString().endsWith("SHULKER_BOX")){
//                    e.setCancelled(true);
//                    return;
//                }
//                else{
//                    GameItem item = Vice.getItemManager().getItem(drug);
//                    if(item !=null && item.canSell() && (click.isShiftClick() || e.getClickedInventory().getType()==InventoryType.CHEST)){
//                        e.setCancelled(true);
//                        return;
//                    }
//                }
//            }
//            System.out.println("TEST");
        }

        if(inv.getType()==InventoryType.ENDER_CHEST) {
            if(e.getClick()==ClickType.NUMBER_KEY){
                e.setCancelled(true);
                return;
            }
            GameItem gi = Vice.getItemManager().getItem(click.isShiftClick() ? e.getCurrentItem() : e.getCursor());
            if(gi!=null && (gi.isScheduled() || gi.getItem().getType()==Material.WATCH || gi.getItem().getType()==Material.CHEST)) {
                if (click.isShiftClick() && e.getClickedInventory().getType() == InventoryType.PLAYER) {
                    e.setCancelled(true);
                } else if (!click.isShiftClick() && e.getClickedInventory().equals(inv))
                    e.setCancelled(true);
            }
        }
        if (e.getClickedInventory().getType() == InventoryType.PLAYER && (click==ClickType.LEFT || click==ClickType.RIGHT)) {
            if (e.getSlot() == 17) {
                Vice.getBackpackManager().openBackpack(player);
                e.setCancelled(true);
                return;
            } else if (e.getSlot() == 16) {
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

        if (ViceUtils.isDefaultPlayerItem(clicked) && e.getClick() == ClickType.DOUBLE_CLICK) {
            e.setCancelled(true);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onBackpackTransfer(InventoryClickEvent event) {

        ItemStack item = event.getCurrentItem();
        boolean shift = false;

        if(event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT) shift = true;

        if (event.getClick() == ClickType.NUMBER_KEY) {

            if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;

            switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                case "Phone":
                case "Ammo Pouch":
                case "Backpack":
                    event.setCancelled(true);
                    break;
            }
        }

        if (shift) {

        	if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;

            switch (ChatColor.stripColor(item.getItemMeta().getDisplayName())) {
                case "Phone":
				case "Ammo Pouch":
                case "Backpack":
                    event.setCancelled(true);
                    break;
            }
        } else {

            ItemStack cursor = event.getCursor();
            Inventory inventory = event.getClickedInventory();

            if (inventory == null) return;

            if (cursor == null) return;

            if (cursor.getItemMeta() != null && cursor.getItemMeta().getDisplayName() != null && ChatColor.stripColor(cursor.getItemMeta().getDisplayName()).equals("Phone") && cursor.getType() == Material.WATCH) {
                event.setResult(Event.Result.DENY);
            }
        }
    }

    @EventHandler
    public void onSwitchToOffhand(PlayerSwapHandItemsEvent e) {
        e.setCancelled(true);
    }
}
