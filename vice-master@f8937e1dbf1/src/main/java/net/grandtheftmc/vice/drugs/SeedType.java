package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

public final class SeedType {
    protected static SeedType POTATO_SEED, ERGOT_FUNGI, COCA_SEED, MARIJUANA, HUMULUS_SEED;

    static {
        POTATO_SEED = new SeedType(
                new ItemStack(Material.POTATO),
                new SeedDrop(1, 20, 30, Biome.BIRCH_FOREST),
                new SeedDrop(2, 20, 40, Biome.FOREST)//Deciduous Forest
        );

        ERGOT_FUNGI = new SeedType(
                Vice.getItemManager().getItem("ergotfungi").getItem(),
                new SeedDrop(3, 20, 30, Biome.JUNGLE),
                new SeedDrop(4, 20, 40, Biome.ROOFED_FOREST),//Fantasy Forest
                new SeedDrop(5, 20, 40, Biome.MUSHROOM_ISLAND),//Farmland
                new SeedDrop(6, 20, 40, Biome.MUTATED_ROOFED_FOREST)//Forgotten Forest
        );

        COCA_SEED = new SeedType(
                Vice.getItemManager().getItem("cocaseed").getItem(),
                new SeedDrop(7, 20, 40, Biome.DEEP_OCEAN),
                new SeedDrop(8, 20, 40, Biome.DESERT),
                new SeedDrop(9, 20, 30, Biome.TAIGA),//Mega Spruce Taiga
                new SeedDrop(10, 20, 30, Biome.MUTATED_TAIGA),//Mega Taiga
                new SeedDrop(11, 20, 30, Biome.MUTATED_REDWOOD_TAIGA),//Mega Taiga
                new SeedDrop(12, 20, 30, Biome.MESA)
        );

        MARIJUANA = new SeedType(
                Vice.getItemManager().getItem("marijuanaseed").getItem(),
                new SeedDrop(13, 20, 30, Biome.JUNGLE),
                new SeedDrop(14, 20, 40, Biome.BIRCH_FOREST),//Snowy Pine
                new SeedDrop(15, 20, 40, Biome.MUTATED_BIRCH_FOREST),//Snowy Pine
                new SeedDrop(16, 20, 40, Biome.MUTATED_SWAMPLAND),//Swamp Edge
                new SeedDrop(17, 20, 40, Biome.SWAMPLAND)
        );

        HUMULUS_SEED = new SeedType(
                Vice.getItemManager().getItem("humulusseed").getItem(),
                new SeedDrop(18, 20, 30, Biome.OCEAN),
                new SeedDrop(19, 20, 30, Biome.BIRCH_FOREST),//Pine Forest
                new SeedDrop(20, 20, 30, Biome.PLAINS),
                new SeedDrop(21, 20, 40, Biome.RIVER),
                new SeedDrop(22, 20, 40, Biome.SAVANNA)
        );
    }

    private static SeedType[] values;

    private final ItemStack itemStack;
    private final SeedDrop[] drops;

    public SeedType(ItemStack itemStack, SeedDrop... drops) {
        this.itemStack = itemStack;
        this.drops = drops;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public SeedDrop[] getDrops() {
        return drops;
    }

    public static void init() {
        values = new SeedType[] {POTATO_SEED, ERGOT_FUNGI, COCA_SEED, MARIJUANA, HUMULUS_SEED};
    }

    public static SeedType[] values() {
        return values;
    }
}
