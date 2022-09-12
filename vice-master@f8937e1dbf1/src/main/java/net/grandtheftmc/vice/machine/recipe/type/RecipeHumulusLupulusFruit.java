package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;

public final class RecipeHumulusLupulusFruit extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeHumulusLupulusFruit() {
        super(
                1,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("humuluslupulusfruit").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("hop").getItem(), 6)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("需", 1) + "  霚  " + _("霂", 6));
    }
}
