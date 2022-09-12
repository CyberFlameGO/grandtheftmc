package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Depressants extends Drug implements IDrugCategory {

    protected Depressants(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Depressants";
    }

    @Override
    public String description() {
        return "Slows down activity in the central nervous system. They slow down the body and give you the feeling of relaxation.";
    }
}
