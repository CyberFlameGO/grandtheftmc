package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugGrowable;

public final class Cocaine extends BaseDrugItem<DrugGrowable> {

    /**
     * Construct a new Drug Item
     */
    public Cocaine() {
        super("Cocaine", DrugType.COCAINE);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
