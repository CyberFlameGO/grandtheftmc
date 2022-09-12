package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeWeed extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeWeed() {
        super(
                3,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("marijuanaleaf").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("weed").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霢", 1) + "  霚  " + _("霆", 1));
    }
}
