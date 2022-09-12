package net.grandtheftmc.core.inventory;

import com.google.common.collect.Maps;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public abstract class CoreMenu implements InventoryHolder, IMenuButtonHandler {

    private final HashMap<Integer, MenuItem> mapItems;
//    private final List<MenuItem> items;
    private Inventory inventory;

    private final int rows;
    private final String title;
    private CoreMenuFlag[] menuFlags;
    protected boolean selfHandle = false;

    /**
     * Construct a new Menu.
     */
    public CoreMenu(int rows, String title, CoreMenuFlag... menuFlags) {
        this(rows, title);
        this.menuFlags = menuFlags;
        if(hasFlag(CoreMenuFlag.PHONE_LAYOUT)) {
            this.inventory = Bukkit.createInventory(this, 54, title);
            setPhoneDefaults();
        }
    }

    /**
     * Construct a new Menu.
     */
    public CoreMenu(int rows, String title) {
        if(rows <= 0 || rows >= 7) {
            throw new IndexOutOfBoundsException("Menu rows out of bounds, choose value between 1 - 6");
        }

        this.mapItems = Maps.newHashMap();

        this.rows = rows;
        this.title = Utils.f(title);
        this.inventory = Bukkit.createInventory(this, rows * 9, title);

    }

    /**
     * Construct a new Menu.
     */
    public CoreMenu(String title, int rows, InventoryType type, CoreMenuFlag... menuFlags) {
        this.mapItems = Maps.newHashMap();

        this.rows = rows;
        this.title = title;
        this.inventory = Bukkit.createInventory(this, type, title);
        this.menuFlags = menuFlags;
    }

    public void setSelfHandle(boolean b) {
        this.selfHandle = b;
    }

    @Override
    public final Inventory getInventory() {
        return this.inventory;
    }

    public boolean isSelfHandle() {
        return selfHandle;
    }

    public void selfHandle(InventoryClickEvent event) {
        CoreMenu menu = (CoreMenu) event.getInventory().getHolder();
        Player player = (Player) event.getWhoClicked();

        MenuItem item = menu.getMenuItem(event.getRawSlot());
        if(item == null) return;

        if(!item.isAllowingPickup())
            event.setCancelled(true);

        if(item instanceof ClickableItem) {
            event.setCancelled(true);
            ((ClickableItem) item).getClickAction().onClick(player, event.getClick());
        }
    }

    private void setPhoneDefaults() {
        ItemStack whiteGlass = Utils.createItem(Material.STAINED_GLASS_PANE, "&a");
        ItemStack grayGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 7, "&a");
        ItemStack blackGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 15, "&a");
        ItemStack lightGlass = Utils.createItem(Material.STAINED_GLASS_PANE, 8, "&a");
        for (int i : new int[]{1, 10, 19, 28, 37, 46, 7, 16, 25, 34, 43, 52}) addItem(new MenuItem(i, whiteGlass, false));
        for (int i : new int[]{2, 3, 4, 5, 6}) addItem(new MenuItem(i, blackGlass, false));
        for (int i : new int[]{11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33, 38, 39, 40, 41, 42, 47, 48, 49, 50, 51}) addItem(new MenuItem(i, grayGlass, false));
        for (int i : new int[]{0, 8, 9, 17, 18, 26, 27, 35, 36, 44, 45, 53}) addItem(new MenuItem(i, lightGlass, false));
    }

    /**
     * Open the inventory to the specified player.
     *
     * @param player Specified Player
     */
    public void openInventory(Player player) {
        if(hasFlag(CoreMenuFlag.RESET_CURSOR_ON_OPEN)) player.closeInventory();
        player.openInventory(this.inventory);
    }

    public final boolean hasFlag(CoreMenuFlag menuFlag) {
        if(this.menuFlags == null || this.menuFlags.length == 0) return false;
        boolean result = false;
        for(int i = 0; i < this.menuFlags.length; i++) {
            if(this.menuFlags[i] == menuFlag) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * This should be overwritten for menus which require the usage of this.
     */
    public void onClose(InventoryCloseEvent event){

    }

    /**
     * This should be overwritten for menu which require the usage of this. If selfHandle = true then this is not triggered.
     * This is done before the handling of the Clickable / MenuItems.
     * Cancelling this event WILLNOT cancel the handling of the Clickable / Menuitems.
     */
    public void onClick(InventoryClickEvent event){

    }

    /**
     * This is called on every interaction.
     *
     * @param menu
     */
    public void onInteract(CoreMenu menu) {

    }

    /**
     * Add an item to the inventory.
     *
     * @param menuItem
     */
    @Override
    public void addItem(MenuItem menuItem) {
        this.inventory.setItem(menuItem.getIndex(), menuItem.getItemStack());
//        this.items.add(menuItem);
        this.mapItems.put(menuItem.getIndex(), menuItem);
    }

    @Override
    public void deleteItem(int index) {
        this.inventory.setItem(index, null);
        this.mapItems.remove(index);
    }

    /**
     * Check if the inventory contains a specific item.
     *
     * @param itemStack Item to search for
     * @return true if item is found
     */
    @Override
    public boolean containsItem(ItemStack itemStack) {
//        return this.items.stream().anyMatch(item -> item.getItemStack().equals(itemStack));
        return this.mapItems.values().stream().anyMatch(item -> item.getItemStack().equals(itemStack));
    }

    /**
     * Check if the inventory slot is in use.
     *
     * @param index slot index
     * @return found status
     */
    @Override
    public boolean containsItem(int index) {
        return this.inventory.getItem(index) != null;
    }

    /**
     * Get the MenuItem from an input ItemStack.
     *
     * @param itemStack Item to search for
     * @return MenuItem version of the found ItemStack
     */
    @Override
    public MenuItem getMenuItem(ItemStack itemStack) {
//        return this.items.stream().filter(item -> item.getItemStack().equals(itemStack)).findFirst().orElse(null);
        return this.mapItems.values().stream().filter(item -> item.getItemStack().equals(itemStack)).findFirst().orElse(null);
    }

    /**
     * Get the MenuItem from an index input.
     *
     * @param index slot to search
     * @return MenuItem version of the found ItemStack
     */
    @Override
    public MenuItem getMenuItem(int index) {
        return this.mapItems.getOrDefault(index, null);
//        return this.mapItems.values().stream().filter(item -> item.getIndex() == index).findFirst().orElse(null);
    }

    /**
     * Get the amount of rows in the inventory.
     *
     * @return amount of rows
     */
    public final int getRows() {
        return rows;
    }

    /**
     * Get the display title of the inventory ui.
     *
     * @return display title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Get the flags assigned to said inventory.
     *
     * @return array of menu flags
     */
    public final CoreMenuFlag[] getMenuFlags() {
        return menuFlags;
    }

    protected int[] getEdgeSlots(int rows) {
        switch (rows) {
            case 1:
                return new int[] {};
            case 2:
                return new int[] {};
            case 3:
                return new int[] {0,1,2,3,4,5,6,7,8,  9,17,  18,19,20,21,22,23,24,25,26};
            case 4:
                return new int[] {0,1,2,3,4,5,6,7,8,  9,17,  18,26,  27,28,29,30,31,32,33,34,35};
            case 5:
                return new int[] {0,1,2,3,4,5,6,7,8,  9,17,  18,26,  27,35,  36,37,38,39,40,41,42,43,44};
            case 6:
                return new int[] {0,1,2,3,4,5,6,7,8,  9,17,  18,26,  27,35,  36,44,  45,46,47,48,49,50,51,52,53};
        }

        return new int[]{};
    }

    public boolean isSlotBlocked(int slot) {
        for (int i : this.mapItems.keySet()) {
            if (i != slot) continue;
            MenuItem item = this.mapItems.get(i);
            if (!item.isAllowingPickup()) {
                return true;
            }
        }

        return false;
    }
}
