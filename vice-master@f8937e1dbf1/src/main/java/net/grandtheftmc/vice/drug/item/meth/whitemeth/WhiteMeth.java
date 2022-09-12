package net.grandtheftmc.vice.drug.item.meth.whitemeth;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;

public final class WhiteMeth extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public WhiteMeth() {
        super("White Meth", DrugType.METH);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
