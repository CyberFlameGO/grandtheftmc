package net.grandtheftmc.vice.drug.attribute;

import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public interface DrugPlaceable extends DrugAttribute< BlockPlaceEvent > {

    /**
     * Check if the plant is of type.
     *
     * @param item Material type
     * @return
     */
    boolean isPlant(ItemStack item);

    /**
     * Get the blocks that the plant can grow on.
     *
     * @return Material array
     */
    Material[] getPlantableBlocks();

    /**
     * Check if a Material accepts plant growth.
     *
     * @param material Given Block type
     * @return true = can be planted
     */
    boolean canPlantOn(Material material);
}
