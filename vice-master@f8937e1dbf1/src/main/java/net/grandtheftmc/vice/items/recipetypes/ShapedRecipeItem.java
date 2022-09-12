package net.grandtheftmc.vice.items.recipetypes;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import java.util.HashMap;

/**
 * Created by Timothy Lampen on 2017-08-06.
 */
public abstract class ShapedRecipeItem extends CraftingRecipeItem {

    private HashMap<Character, ItemStack> ingredients = new HashMap<>();

    public ShapedRecipeItem() {
        super(RecipeType.SHAPED_CRAFTING);
        register();
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public boolean validate(ItemStack[] matrix){
        ShapedRecipe sRecipe = (ShapedRecipe)recipe;
        int pos;
        for(int i = 0; i<sRecipe.getShape().length; i++){
            String line = sRecipe.getShape()[i];
            pos = i*3;
            for(char id : line.toCharArray()){
                ItemStack compare = this.ingredients.get(id);
                if(!compare.isSimilar(matrix[pos])) {
                    return false;
                }
                pos++;
            }
        }
        return true;
    }

    protected void setRecipe(ShapedRecipe r, SlotContainer... ingredients){
        this.ingredients = new HashMap<>();
        this.recipe = r;
        for(SlotContainer container : ingredients){
            this.ingredients.put(container.getId(), container.getItemStack());
        }
    }

    public HashMap<Character, ItemStack> getIngredients() {
        return ingredients;
    }
}
