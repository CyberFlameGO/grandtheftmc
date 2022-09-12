package net.grandtheftmc.vice.drug.item.meth.whitemeth;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugPlantable;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class EphredraSinicaSeeds extends BaseDrugItem<DrugPlantable> {

    private static final Material[] ACCEPTED_BLOCKS = new Material[]{ Material.SAND };

    /**
     * Construct a new Drug Item
     */
    public EphredraSinicaSeeds() {
        super("Ephredra Sinica Seed", DrugType.METH);

        super.setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        super.setAttribute(new DrugPlantable() {
            @Override
            public void onEvent(PlayerInteractEvent event) {
                if (!this.isPlant(event.getItem())) return;
                if (event.getBlockFace() != BlockFace.UP) return;
                if (!this.canPlantOn(event.getClickedBlock().getType())) return;

                //TODO Plant seed.
            }

            @Override
            public boolean isPlant(ItemStack item) {
                return item.isSimilar(EphredraSinicaSeeds.super.getGameItem().getItem());
            }

            @Override
            public Material[] getPlantableBlocks() {
                return ACCEPTED_BLOCKS;
            }

            @Override
            public boolean canPlantOn(Material material) {
                return material == ACCEPTED_BLOCKS[0];
            }
        });
    }
}
