package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Inhalants extends Drug implements IDrugCategory{

    protected Inhalants(String name, int duration) {
        super(name, duration);
    }
    @Override
    public String name() {
        return "Inhalants";
    }

    @Override
    public String description() {
        return "Give you immediate results. These can have sudden mental damage.";
    }
}
