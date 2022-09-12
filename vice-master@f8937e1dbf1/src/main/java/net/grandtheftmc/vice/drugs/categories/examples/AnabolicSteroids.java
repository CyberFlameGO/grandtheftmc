package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class AnabolicSteroids extends Drug implements IDrugCategory {

    protected AnabolicSteroids(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Anabolic Steroids";
    }

    @Override
    public String description() {
        return "Improves physical performance, enlarges muscles and increases strength.";
    }
}
