package net.grandtheftmc.vice.drug.item.beer;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugMachine;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;
//import net.grandtheftmc.vice.machine.event.MachineRecipeCompleteEvent;

public final class CraftBeer extends BaseDrugItem<DrugMachine> {

    /**
     * Construct a new Drug Item
     */
    public CraftBeer() {
        super("Craft Beer", DrugType.CRAFT_BEER);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(null);
    }
}
