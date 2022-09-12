package net.grandtheftmc.core.inventory.button;

import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public class MenuItem {

    private final int index;
    private final ItemStack itemStack;

    private final boolean allowPickup;

    /**
     * Construct a new MenuItem
     *
     * @param index        menu slot index
     * @param itemStack    menu itemstack
     * @param allowPickup  movable status
     */
    public MenuItem(int index, ItemStack itemStack, boolean allowPickup) {
        this.index = index;
        this.itemStack = itemStack;
        this.allowPickup = allowPickup;
    }

    /**
     * Get the index of the set item.
     *
     * @return index
     */
    public final int getIndex() {
        return index;
    }

    /**
     * Get the ItemStack of the set item.
     *
     * @return itemstack
     */
    public final ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * If true, the item can be moved.
     * If false, the item is stationary.
     *
     * @return movable status
     */
    public final boolean isAllowingPickup() {
        return allowPickup;
    }
}
