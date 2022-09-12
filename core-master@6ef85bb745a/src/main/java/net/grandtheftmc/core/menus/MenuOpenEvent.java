package net.grandtheftmc.core.menus;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;

public class MenuOpenEvent extends PlayerEvent implements Cancellable{
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Menu menu;
    private ItemStack[] contents;
    private boolean cancelled = false;

    public MenuOpenEvent(Player player, Menu menu) {
        super(player);
        this.menu = menu;
        this.contents = new ItemStack[menu.getSize()];
    }

    public Menu getMenu() {
        return this.menu;
    }

    public ItemStack[] getContents() {
        return this.contents;
    }

    public void setContents(ItemStack[] contents) {
        this.contents = contents;
    }

    public Menu setItem(int i, ItemStack item) {
        if (i >= 0 && i < this.contents.length)
            this.contents[i] = item;
        return this.menu;
    }
    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}