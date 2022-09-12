package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class Crack extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public Crack() {
        super("Crack", DrugType.CRACK);

        super.setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
