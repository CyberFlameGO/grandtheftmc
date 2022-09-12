package net.grandtheftmc.vice.drug.attribute;

import net.grandtheftmc.vice.utils.recipe.ShapedRegister;
import net.grandtheftmc.vice.utils.recipe.ShapelessRegister;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

public interface DrugCraftable extends DrugAttribute< PrepareItemCraftEvent > {

    default ShapelessRegister getShapelessRecipe() { return null; }
    default ShapedRegister getShapedRecipe() { return null; }

    boolean isShapeless();
}
