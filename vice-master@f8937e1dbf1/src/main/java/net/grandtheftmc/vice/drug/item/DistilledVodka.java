package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugCraftable;
import net.grandtheftmc.vice.items.ItemManager;
import net.grandtheftmc.vice.utils.recipe.ShapelessRegister;
import org.bukkit.Material;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;

public final class DistilledVodka extends BaseDrugItem< DrugCraftable > {

    /**
     * Construct a new Drug Item
     */
    public DistilledVodka() {
        super("Distilled Vodka", DrugType.VODKA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        ShapelessRegister recipe = new ShapelessRegister(ItemManager.NAMESPACED_KEY, super.getGameItem().getItem().clone());

        ItemStack vodka = Vodka.OUTPUT.clone();
        recipe.addIngredient(2, vodka);

        ItemStack arrow = new ItemStack(Material.ARROW);
        recipe.addIngredient(1, arrow);

        recipe.register();

        setAttribute(new DrugCraftable() {
            @Override
            public boolean isShapeless() {
                return false;
            }

            @Override
            public void onEvent(PrepareItemCraftEvent event) {

            }
        });
    }
}
