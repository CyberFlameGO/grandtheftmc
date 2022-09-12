package net.grandtheftmc.guns.weapon.attribute;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/**
 * Created by Luke Bingham on 07/08/2017.
 */
public interface WeaponExplosive extends WeaponAttribute {

    void onExplode(Entity explosive, Player shooter);

    default void onLand(Entity explosive) {}
}
