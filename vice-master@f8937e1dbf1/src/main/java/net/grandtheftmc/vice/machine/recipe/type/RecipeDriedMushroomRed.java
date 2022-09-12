package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeDriedMushroomRed extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeDriedMushroomRed() {
        super(
                4,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("magicmushroomred").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("driedmagicmushroom").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霟", 1) + "  霚  " + _("霋", 1));
    }
}
