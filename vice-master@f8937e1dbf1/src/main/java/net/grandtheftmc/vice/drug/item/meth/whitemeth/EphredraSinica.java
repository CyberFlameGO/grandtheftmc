package net.grandtheftmc.vice.drug.item.meth.whitemeth;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;

public final class EphredraSinica extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public EphredraSinica() {
        super("Ephredra Sinica", DrugType.METH);

        super.setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
