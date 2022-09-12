package net.grandtheftmc.vice.machine.recipe.menu;

import net.grandtheftmc.core.inventory.CoreMenu;
import net.grandtheftmc.core.inventory.CoreMenuFlag;
import net.grandtheftmc.core.inventory.button.ClickableItem;
import net.grandtheftmc.core.inventory.button.MenuItem;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.factory.ItemFactory;
import org.bukkit.Material;

import java.util.Set;

public final class RecipeMenuPortal extends CoreMenu {

    public RecipeMenuPortal(Set<RecipeMenu> recipeMenus) {
        super(4, "Choose a Drug Recipe", CoreMenuFlag.RESET_CURSOR_ON_OPEN, CoreMenuFlag.CLOSE_ON_NULL_CLICK);

        //# # # # # # # # #
        //# o o o o o o o #
        //# o o o o o o o #
        //# # # # # # # # #

        int[] slots = new int[] {10,11,12,13,14,15,16, 19,20,21,22,23,24,25}, empty = new int[] {0,1,2,3,4,5,6,7,8,9,17,18,26,27,28,29,30,31,32,33,34,35};
        int x = 0;
        for (RecipeMenu menu : recipeMenus) {
            super.addItem(new ClickableItem(slots[x], menu.getRecipe().getOutput()[0].getItemStack().clone(), (player, clickType) -> menu.openInventory(player)));
            x++;
        }

        for (int i : empty) {
            super.addItem(new MenuItem(i, new ItemFactory(Material.STAINED_GLASS_PANE, (byte) 8).setName(C.RED).build(), false));
        }
    }
}
