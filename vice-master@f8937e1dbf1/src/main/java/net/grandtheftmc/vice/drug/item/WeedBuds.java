package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;

public final class WeedBuds extends BaseDrugItem {

    /**
     * Construct a new Drug Item
     */
    public WeedBuds() {
        super("Weed Buds", DrugType.MARIJUANA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));
    }
}
