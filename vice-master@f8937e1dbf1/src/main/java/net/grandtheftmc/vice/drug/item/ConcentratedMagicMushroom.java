package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class ConcentratedMagicMushroom extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public ConcentratedMagicMushroom() {
        super("Concentrated Magic Mushroom", DrugType.MAGIC_MUSHROOM);

        super.setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
