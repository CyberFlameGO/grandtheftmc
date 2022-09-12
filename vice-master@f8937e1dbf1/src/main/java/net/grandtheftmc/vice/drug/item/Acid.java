package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class Acid extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public Acid() {
        super("Acid", DrugType.MARIJUANA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
