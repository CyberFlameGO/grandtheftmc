package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugMachine;
import org.bukkit.event.Event;

public final class DriedMagicMushroom extends BaseDrugItem<DrugMachine> {

    /**
     * Construct a new Drug Item
     */
    public DriedMagicMushroom() {
        super("Dried Magic Mushroom", DrugType.MAGIC_MUSHROOM);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(new DrugMachine() {
            @Override
            public void onEvent(Event event) {

            }
        });
    }
}
