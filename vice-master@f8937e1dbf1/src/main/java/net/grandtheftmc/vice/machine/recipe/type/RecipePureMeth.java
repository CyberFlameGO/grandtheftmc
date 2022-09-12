package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipePureMeth extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipePureMeth() {
        super(
                15,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("methylamine").getItem(), 1),
                        new RecipeInput(Vice.getItemManager().getItem("ephedrasinica").getItem(), 64)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("puremeth").getItem(), 4)
                },
                16,
                800,
                2560
        );

        super.setRecipeText(_("霑", 1) + _("霠", 64) + "  霚  " + _("霒", 4));
    }
}
