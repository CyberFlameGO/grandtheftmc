package net.grandtheftmc.vice.machine.recipe.menu;

import com.google.common.collect.Sets;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.machine.recipe.command.MachineRecipeCommand;
import net.grandtheftmc.vice.machine.recipe.menu.type.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public class RecipeMenuManager implements Component<RecipeMenuManager, Vice> {

    private final Set<RecipeMenu> recipeMenus;
    private final RecipeMenuPortal recipeMenuPortal;

    public RecipeMenuManager(JavaPlugin plugin) {
        this.recipeMenus = Sets.newHashSet(
                new RecipeMenuAcid(),
                new RecipeMenuBeer(),
                new RecipeMenuCocaine(),
                new RecipeMenuConcentratedMagicMushroom(),
                new RecipeMenuCrack(),
                new RecipeMenuCraftBeer(),
                new RecipeMenuDistilledVodka(),
                new RecipeMenuDriedMushroom(),
                new RecipeMenuHumulusLapulusFruit(),
                new RecipeMenuLSD(),
                new RecipeMenuPureMeth(),
                new RecipeMenuVodka(),
                new RecipeMenuWeed(),
                new RecipeMenuWhiteMeth()
        );

        this.recipeMenuPortal = new RecipeMenuPortal(this.recipeMenus);

        new MachineRecipeCommand(this);
    }

    public RecipeMenuPortal getRecipeMenuPortal() {
        return recipeMenuPortal;
    }

    @EventHandler
    protected final void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getTitle().toLowerCase().contains("recipe :")) {
            event.setCancelled(true);
        }
    }
}
