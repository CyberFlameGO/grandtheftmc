package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeCocaine extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeCocaine() {
        super(
                12,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("crack").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("cocaine").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霓", 1) + "  霚  " + _("霍", 1));
    }
}
