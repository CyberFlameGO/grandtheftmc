package net.grandtheftmc.gtm.drugs.categories.examples;

import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.categories.IDrugCategory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.TimeUnit;

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
