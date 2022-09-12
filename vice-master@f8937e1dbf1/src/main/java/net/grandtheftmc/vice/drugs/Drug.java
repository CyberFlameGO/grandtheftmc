package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.vice.drugs.categories.DrugCategory;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Remco on 25-3-2017.
 */
public abstract class Drug {

    private final String name;
    private int duration;

    protected Drug(String name, int duration) {
        this.name = name;
        this.duration = duration;
    }

    /**
     * General startpoint for applying drugs.
     *
     * @param player
     * @return
     */
    public abstract boolean apply(Player player);

    /**
     * Name of the drug
     *
     * @return
     */
    public String getName() {
        return name;
    }


    /**
    * Duration of the particle effects
     *
     * @return
    */
    public int getDuration() {
        return duration;
    }

    public Optional<DrugCategory> getCategory() {
        return DrugCategory.byDrug(this);
    }

    protected Drug getInstance(){
        return this;
    }
}
