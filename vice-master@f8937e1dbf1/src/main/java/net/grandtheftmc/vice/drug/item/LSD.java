package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class LSD extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public LSD() {
        super("LSD", DrugType.LSD);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
