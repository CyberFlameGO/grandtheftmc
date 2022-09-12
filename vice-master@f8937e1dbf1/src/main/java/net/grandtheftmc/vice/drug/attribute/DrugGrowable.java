package net.grandtheftmc.vice.drug.attribute;

import org.bukkit.inventory.ItemStack;

public interface DrugGrowable extends DrugAttribute {

    /**
     * Growth time in milliseconds.
     *
     * @return time
     */
    int getGrowthTime();

    /**
     * Get the Item that is returned from a fully grown plant.
     *
     * @return item
     */
    ItemStack getPlantGrowthResult();
}
