package net.grandtheftmc.vice.drugs.categories.examples;

import net.grandtheftmc.vice.drugs.Drug;
import net.grandtheftmc.vice.drugs.categories.IDrugCategory;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Stimulants extends Drug implements IDrugCategory {

    protected Stimulants(String name, int duration) {
        super(name, duration);
    }

    @Override
    public String name() {
        return "Stimulants";
    }

    @Override
    public String description() {
        return "Speeds up your nevous system and make you feel very alive. Also known as \"uppers\" because of their ability to make you feel very awake.";
    }

}
