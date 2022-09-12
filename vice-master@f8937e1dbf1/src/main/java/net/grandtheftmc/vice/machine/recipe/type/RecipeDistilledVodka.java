package net.grandtheftmc.vice.machine.recipe.type;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.MachineRecipe;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeInput;
import net.grandtheftmc.vice.machine.recipe.misc.RecipeOutput;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public final class RecipeDistilledVodka extends MachineRecipe {

    /**
     * Construct a new Machine Recipe.
     */
    public RecipeDistilledVodka() {
        super(
                8,
                new RecipeInput[] {
                        new RecipeInput(new ItemStack(Material.GOLD_NUGGET), 3),
                        new RecipeInput(Vice.getItemManager().getItem("vodka").getItem(), 1)
                },
                new RecipeOutput[] {
                        new RecipeOutput(Vice.getItemManager().getItem("distilledvodka").getItem(), 1)
                },
                2,
                100,
                10
        );

        super.setRecipeText(_("霛", 3) + _("霉", 1) + "  霚  " + _("霝", 1));
    }
}
