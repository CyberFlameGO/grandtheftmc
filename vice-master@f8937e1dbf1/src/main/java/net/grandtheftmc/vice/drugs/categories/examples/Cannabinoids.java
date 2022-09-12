package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Cannabinoids extends Drug implements IDrugCategory {

    protected Cannabinoids(String name,  int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Cannabinoids";
    }

    @Override
    public String description() {
        return "Give you a feeling of euphoria. May cause confusion, memory problems, anxiety and a higher heart rate.";
    }
}
