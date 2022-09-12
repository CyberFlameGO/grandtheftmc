package net.grandtheftmc.core.inventory;

import net.grandtheftmc.core.inventory.button.MenuItem;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 05/07/2017.
 */
public interface IMenuButtonHandler {

    /**
     * Add an item to the inventory.
     */
    void addItem(MenuItem menuItem);

    /**
     * Delete an item from the inventory.
     */
    void deleteItem(int index);

    /**
     * Check if the inventory contains a specific item.
     *
     * @param itemStack Item to search for
     * @return true if item is found
     */
    boolean containsItem(ItemStack itemStack);

    /**
     * Check if the inventory slot is in use.
     *
     * @param index slot index
     * @return found status
     */
    boolean containsItem(int index);

    /**
     * Get the MenuItem from an input ItemStack.
     *
     * @param itemStack Item to search for
     * @return MenuItem version of the found ItemStack
     */
    MenuItem getMenuItem(ItemStack itemStack);

    /**
     * Get the MenuItem from an index input.
     *
     * @param index slot to search
     * @return MenuItem version of the found ItemStack
     */
    MenuItem getMenuItem(int index);


}
