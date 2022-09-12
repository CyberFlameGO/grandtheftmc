package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugPlaceable;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public final class MarijuanaSeed extends BaseDrugItem< DrugPlaceable > {

    private static final Material[] ACCEPTED_BLOCKS = new Material[]{ Material.SAND, Material.DIRT, Material.GRASS, Material.SOIL };

    /**
     * Construct a new Drug Item
     */
    public MarijuanaSeed() {
        super("Marijuana Seed", DrugType.MARIJUANA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(new DrugPlaceable() {
            @Override
            public boolean isPlant(ItemStack item) {
                return item.isSimilar(MarijuanaSeed.super.getGameItem().getItem());
            }

            @Override
            public Material[] getPlantableBlocks() {
                return ACCEPTED_BLOCKS;
            }

            @Override
            public boolean canPlantOn(Material material) {
                for (Material m : ACCEPTED_BLOCKS) {
                    if (material == m) return true;
                }
                return false;
            }

            @Override
            public void onEvent(BlockPlaceEvent event) {
                if (!this.isPlant(event.getItemInHand())) return;
                if (!this.canPlantOn(event.getBlockAgainst().getType())) {
                    event.setCancelled(true);
                    return;
                }

                System.out.println("Placed plant: " + MarijuanaSeed.super.getName());
            }
        });
    }
}
