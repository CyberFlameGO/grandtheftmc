package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugBrewable;
import org.bukkit.inventory.ItemStack;

public final class Vodka extends BaseDrugItem<DrugBrewable> {

    protected static final ItemStack OUTPUT = Vice.getItemManager().getItem("vodka").getItem();

    /**
     * Construct a new Drug Item
     */
    public Vodka() {
        super("Vodka", DrugType.VODKA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(null);
    }
}
