package net.grandtheftmc.vice.machine.recipe.command;

import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.vice.machine.recipe.menu.RecipeMenuManager;
import org.bukkit.entity.Player;

public final class MachineRecipeCommand extends CoreCommand<Player> {

    private final RecipeMenuManager recipeMenuManager;

    public MachineRecipeCommand(RecipeMenuManager recipeMenuManager) {
        super("recipe", "Display visual recipes for Machines & Drugs");
        this.recipeMenuManager = recipeMenuManager;
    }

    @Override
    public void execute(Player sender, String[] strings) {
        this.recipeMenuManager.getRecipeMenuPortal().openInventory(sender);
    }
}
