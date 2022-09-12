package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class Hop extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public Hop() {
        super("Hop", DrugType.BEER);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(null);
    }
}
