package net.grandtheftmc.core.events;

import org.bukkit.event.Cancellable;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 06/08/2017.
 */
public class ItemStackEvent extends CoreEvent implements Cancellable {

    private boolean b;
    private ItemStack itemStack;
    private boolean clickOnly;

    /**
     * Construct a new Event
     */
    public ItemStackEvent(ItemStack itemStack) {
        super(false);
        this.itemStack = itemStack;
        this.clickOnly = false;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public boolean isClickOnly() {
        return clickOnly;
    }

    public void setClickOnly(boolean clickOnly) {
        this.clickOnly = clickOnly;
    }

    @Override
    public boolean isCancelled() {
        return b;
    }

    @Override
    public void setCancelled(boolean b) {
        this.b = b;
    }
}
