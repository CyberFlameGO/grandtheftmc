package net.grandtheftmc.core.menus;


import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class MenuClickEvent extends PlayerEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Menu menu;
    private final Inventory inv;
    private final int slot;
    private final ItemStack item;
    private final InventoryClickEvent event;

    public MenuClickEvent(Player player, Menu menu, Inventory inv, int slot, ItemStack item, InventoryClickEvent event) {
        super(player);
        this.menu = menu;
        this.inv = inv;
        this.slot = slot;
        this.item = item;
        this.event = event;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public int getSlot() {
        return this.slot;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public Inventory getInv() {
        return this.inv;
    }

    public InventoryClickEvent getInventoryClickEvent() {
        return this.event;
    }

    public ClickType getClickType() {
        return this.event.getClick();
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
