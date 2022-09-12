package net.grandtheftmc.vice.drug.item;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drug.DrugType;
import net.grandtheftmc.vice.drug.attribute.DrugMachine;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

public final class MarijuanaLeaf extends BaseDrugItem<DrugMachine> {

    private static final ItemStack OUTPUT = Vice.getItemManager().getItem("").getItem();

    /**
     * Construct a new Drug Item
     */
    public MarijuanaLeaf() {
        super("Marijuana Leaf", DrugType.MARIJUANA);

        setGameItem(Vice.getItemManager().getItem(super.getShortName()));

        setAttribute(new DrugMachine() {

            //OUTPUT

            @Override
            public void onEvent(Event event) {

            }
        });
    }

    protected static ItemStack getOutput() {
        return OUTPUT.clone();
    }
}
