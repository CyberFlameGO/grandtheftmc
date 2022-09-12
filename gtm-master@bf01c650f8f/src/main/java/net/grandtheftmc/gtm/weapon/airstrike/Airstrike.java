package net.grandtheftmc.gtm.weapon.airstrike;

import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AirstrikeWeapon;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class Airstrike extends AirstrikeWeapon {

    /**
     * Construct a new Weapon.
     */
    public Airstrike() {
        super(
        		(short) 51,
        		"Airstrke", //Name
                WeaponType.DROPPABLE, //Weapon Type
                AmmoType.NONE, //AmmoType
                new ItemFactory(Material.WOOD_BUTTON).build(), //ItemStack
                new Sound[] {
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_GENERIC_EXPLODE
                }
        );
    }
}
