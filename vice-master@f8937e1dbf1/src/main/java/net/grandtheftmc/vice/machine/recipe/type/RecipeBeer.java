package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class RecipeBeer extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeBeer() {
        super(
                2,
                new RecipeInput[] {
                        new RecipeInput(Vice.getItemManager().getItem("hop").getItem(), 6),
                        new RecipeInput(new ItemStack(Material.GLASS_BOTTLE), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("alcohol").getItem(), 1)
                },
                2,
                100,
                15
        );

        super.setRecipeText(_("霂", 6) + " " + _("霔", 1) + "  霚  " + _("霃", 1));
    }
}
