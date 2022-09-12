package net.grandtheftmc.vice.drug.attribute;

import org.bukkit.inventory.ItemStack;

public interface DrugBrewable extends DrugAttribute {

    /**
     * Brewing time in milliseconds.
     *
     * @return time
     */
    int getBrewTime();

    /**
     * Cab the given item be brewed.
     *
     * @param item brewable item
     * @return
     */
    boolean isBrewable(ItemStack item);

    /**
     * Get the Item that is returned from a finished brew.
     *
     * @return item
     */
    ItemStack getBrewResult();
}
