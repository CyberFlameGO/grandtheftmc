package net.grandtheftmc.vice.items.recipetypes;

import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.*;

import java.util.*;

/**
 * Created by Timothy Lampen on 2017-08-06.
 */
public abstract class OtherRecipeItem extends CraftingRecipeItem{
    private HashMap<ItemStack, Integer> ingredients;
    public OtherRecipeItem(RecipeType type) {
        super(type);
    }
    //This integrated validate method should only be used for shapeless and furnace recipes, NOT for shaped.
    //this also assumes that the correct amount of each items is present.
    public boolean validate(ItemStack[] matrix){
        if(ingredients.size()==0)
            return false;
        switch (this.getType()){
            case FURNACE:
                return matrix.length==1 && ingredients.keySet().stream().anyMatch(itemStack -> itemStack.isSimilar(matrix[0]));
            case SHAPELESS_CRAFTING: {
                HashMap<ItemStack, Integer> ingredientsCopy = (HashMap<ItemStack, Integer>) this.ingredients.clone();
                for (ItemStack is : matrix) {
                    if(is==null || is.getType()== Material.AIR){
                        continue;
                    }
                    ItemStack compareable = is.clone();
                    compareable.setAmount(1);
                    if(ingredientsCopy.containsKey(compareable)){
                        ingredientsCopy.put(compareable, ingredientsCopy.get(compareable)-1);
                    }
                }
                return ingredientsCopy.values().stream().allMatch(integer -> integer==0);
            }
        }
        return false;
    }

    protected void setRecipe(Recipe r){
        this.recipe = r;
        if(getType()==RecipeType.FURNACE){
            Bukkit.addRecipe(r);
        }
    }
    protected void addIngredient(ItemStack is, int amount){
        if(this.ingredients==null)
            this.ingredients = new HashMap<>();
        switch (this.getType()){
            case SHAPELESS_CRAFTING:
                ingredients.put(is, amount);
                ((ShapelessRecipe)this.recipe).addIngredient(amount, is.getData());
            case FURNACE: {
                ingredients.put(is, amount);
            }
        }
    }

    public HashMap<ItemStack, Integer> getIngredients() {
        return ingredients;
    }
}
