package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeAcid extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeAcid() {
        super(
                11,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("ergotfungi").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("acid").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霁", 1) + "  霚  " + _("霎", 1));
    }
}
