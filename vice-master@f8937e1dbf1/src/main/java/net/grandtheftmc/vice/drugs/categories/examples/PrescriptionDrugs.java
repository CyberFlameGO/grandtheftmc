package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class PrescriptionDrugs extends Drug implements IDrugCategory {

    protected PrescriptionDrugs(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Prescription Drugs";
    }

    @Override
    public String description() {
        return "Can be very helpful (if used wisely). Can be very dangerous.";
    }
}
