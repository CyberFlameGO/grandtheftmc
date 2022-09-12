package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeDriedMushroomBrown extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeDriedMushroomBrown() {
        super(
                5,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("magicmushroombrown").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("driedmagicmushroom").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霞", 1) + "  霚  " + _("霋", 1));
    }
}
