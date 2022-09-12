package net.grandtheftmc.gtm.drugs.categories.examples;

import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.categories.IDrugCategory;

import java.util.concurrent.TimeUnit;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Opioids extends Drug implements IDrugCategory {

    protected Opioids(String name,  int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Opioids";
    }

    @Override
    public String description() {
        return "Can cause drowsiness, confusion, nausea, feeligns of euphoria, respiratory complications and relieve pain.";
    }
}
