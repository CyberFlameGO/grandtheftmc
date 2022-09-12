package net.grandtheftmc.vice.drug.item.beer;

import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugPlantable;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public final class HumulusLupulusSeed extends BaseDrugItem< DrugPlantable > {

    private static final Material[] ACCEPTED_BLOCKS = new Material[]{ Material.SAND };

    /**
     * Construct a new Drug Item
     */
    public HumulusLupulusSeed() {
        super("Humulus Lupulus Seed", DrugType.BEER);

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
                return item.isSimilar(HumulusLupulusSeed.super.getGameItem().getItem());
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
