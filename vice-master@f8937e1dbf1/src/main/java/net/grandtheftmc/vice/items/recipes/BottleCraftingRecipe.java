package net.grandtheftmc.vice.items.recipes;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.items.recipetypes.OtherRecipeItem;
import net.grandtheftmc.vice.items.recipetypes.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Timothy Lampen on 2017-08-09.
 */
public class BottleCraftingRecipe extends OtherRecipeItem {
    public BottleCraftingRecipe() {
        super(RecipeType.FURNACE);
    }

    @Override
    protected void register() {
        FurnaceRecipe hopPlantToHop = new FurnaceRecipe(new ItemStack(Material.GLASS_BOTTLE), Material.GLASS);
        setRecipe(hopPlantToHop);
        addIngredient(new ItemStack(Material.GLASS), 1);
    }
}
