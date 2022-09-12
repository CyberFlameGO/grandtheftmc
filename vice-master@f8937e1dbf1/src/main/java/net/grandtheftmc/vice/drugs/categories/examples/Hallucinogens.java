package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Hallucinogens extends Drug implements IDrugCategory {

    protected Hallucinogens(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Hallucinogens";
    }

    @Override
    public String description() {
        return "Change the mind and cause the appearance of things that are not really there.";
    }
}
