package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class RecipeCraftBeer extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeCraftBeer() {
        super(
                7,
                new RecipeInput[] {
                        new RecipeInput(new ItemStack(Material.INK_SACK), 5),
                        new RecipeInput(Vice.getItemManager().getItem("alcohol").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("craftbeer").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霕", 5) + _("霃", 1) + "  霚  " + _("霈", 1));
    }
}
