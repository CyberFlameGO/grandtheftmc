package net.grandtheftmc.gtm.drugs.categories.examples;

import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.categories.IDrugCategory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class AAlcohol extends Drug implements IDrugCategory {

    protected AAlcohol(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Alcohol";
    }

    @Override
    public String description() {
        return "Can make you feel very special. You won't be walking straight if you drink too much though.";
    }
}
