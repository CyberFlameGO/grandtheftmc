package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeCrack extends MachineRecipe {

    /*
    Crack
        Get Coca Seed
        Plant Coca Seed -> Coca Plant Grows
        Destroy Melon -> 2 Coca Leaf
        Coca Leaf -> Process -> Crack
     */

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeCrack() {
        super(
                9,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("cocaleaf").getItem(), 3)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("crack").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霅", 3) + "  霚  " + _("霓", 1));
    }
}
