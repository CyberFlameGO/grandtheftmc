package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public class MagicMushroom extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public MagicMushroom() {
        super("Magic Mushroom", DrugType.MAGIC_MUSHROOM);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
