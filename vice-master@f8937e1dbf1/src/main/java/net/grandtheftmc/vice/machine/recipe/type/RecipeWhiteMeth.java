package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeWhiteMeth extends MachineRecipe {

    /*
    White Meth
        Find Ephredra Sinica Seeds
        Plant -> Grow -> Ephredra Sinica
        Ephredra Sinica -> Process -> White Meth
     */

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeWhiteMeth() {
        super(
                13,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("ephedrasinica").getItem(), 5)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("whitemeth").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霠", 5) + "  霚  " + _("霐", 1));
    }
}
