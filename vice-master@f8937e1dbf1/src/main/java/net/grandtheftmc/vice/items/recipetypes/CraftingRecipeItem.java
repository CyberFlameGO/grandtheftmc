package net.grandtheftmc.vice.items.recipetypes;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Timothy Lampen on 7/10/2017.
 */
public abstract class CraftingRecipeItem extends RecipeItem{

    protected Recipe recipe = null;

    public CraftingRecipeItem(RecipeType type){
        super(type);
        register();
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    protected abstract void register();

    public abstract boolean validate(ItemStack[] matrix);

}
