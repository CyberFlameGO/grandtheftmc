package net.grandtheftmc.vice.drugs;

import org.bukkit.entity.Player;

/**
 * Created by Remco on 25-3-2017.
 */
@FunctionalInterface
public interface DrugEffect {

    void apply(Drug drug, int duration, Player player);

}
