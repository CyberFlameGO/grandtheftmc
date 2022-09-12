package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class RecipeVodka extends MachineRecipe {

    /*
     Vodka
      - Get potato seeds
      - Plant potatoes in tilled earth
      - Destroy potato plant to get a potato
      - Place potato with glass bottle in Vodka Distillery to get Vodka
     */

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeVodka() {
        super(
                6,
                new RecipeInput[] {
                        new RecipeInput(new ItemStack(Material.POTATO_ITEM), 6),
                        new RecipeInput(new ItemStack(Material.GLASS_BOTTLE), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("vodka").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霜", 6) + _("霔", 1) + "  霚  " + _("霉", 1));
    }
}
