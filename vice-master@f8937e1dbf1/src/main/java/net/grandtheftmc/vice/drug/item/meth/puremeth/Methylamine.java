package net.grandtheftmc.vice.drug.item.meth.puremeth;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;

public final class Methylamine extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public Methylamine() {
        super("Methylamine", DrugType.METH);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
