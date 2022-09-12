package net.grandtheftmc.gtm.drugs;

import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Created by Remco on 25-3-2017.
 */
@FunctionalInterface
public interface DrugEffect {

    void apply(Drug drug, int duration, Player player);

}
