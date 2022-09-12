package net.grandtheftmc.vice.drug.item.beer;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugMachine;
import net.grandtheftmc.vice.drug.item.BaseDrugItem;
import org.bukkit.event.Event;

public final class HumulusLupulusFruit extends BaseDrugItem<DrugMachine> {

    /**
     * Construct a new Drug Item
     */
    public HumulusLupulusFruit() {
        super("Humulus Lupulus Fruit", DrugType.BEER);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(new DrugMachine() {
            @Override
            public void onEvent(Event event) {

            }
        });
    }
}
