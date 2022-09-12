package net.grandtheftmc.vice.items.recipetypes;

import net.grandtheftmc.vice.items.recipetypes.RecipeItem;
import net.grandtheftmc.vice.items.recipetypes.RecipeType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;

/**
 * Created by Timothy Lampen on 7/11/2017.
 */
public abstract class BrewingRecipeItem extends RecipeItem {

    private ItemStack ingredient;
    private ItemStack result;
    private ItemStack child;
    private String name;


    public BrewingRecipeItem() {
        super(RecipeType.BREWING);
    }


    public RecipeType getType() {
        return RecipeType.BREWING;
    }

    /**
     * @param isActualPotion if the item is an actual Material potion
     * @apiNote finalizeSetup() MUST be done after super() call in the item classes
     */
    public void finalizeSetup(ItemStack result, String baseName, ItemStack ingredient, ItemStack child, boolean isActualPotion) {
        this.result = result;
        this.name = isActualPotion ? constructPotionName(result.getType(), baseName, ((PotionMeta)result.getItemMeta()).getBasePotionData()) : baseName;
        this.ingredient = ingredient;
        this.child = child;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public ItemStack getChild() {
        return child;
    }



    public ItemStack getResult() {
        return result;
    }

    private String constructPotionName(Material mat, String baseName, PotionData data){
        String name = data.isExtended() ? "Extended " : " ";
        name += mat==Material.SPLASH_POTION ? "Splash" : "";
        name += " Potion of  " + baseName;
        name += data.isUpgraded() ? " II" : "";
        return name;
    }

    public String getName() {
        return name;
    }
}
