package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeConcentratedMagicMushroom extends MachineRecipe {

    /*
    Concentrated Magic Mushroom
        Dried Magic Mushroom -> Process -> Concentrated Magic Mushroom
     */

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeConcentratedMagicMushroom() {
        super(
                10,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("driedmagicmushroom").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("concentratedmagicmushroom").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霋", 1) + "  霚  " + _("霌", 1));
    }
}
