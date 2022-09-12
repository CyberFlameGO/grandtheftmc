package net.grandtheftmc.vice.weapon.explosive;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.ThrowableWeapon;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class ProximityMine extends ThrowableWeapon {

    /**
     * Construct a new Weapon.
     */
    public ProximityMine() {
        super(
        		(short) 38,
                "Proximity Mine", //Name
                WeaponType.THROWABLE, //Weapon Type
                AmmoType.EXPLOSIVE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 371).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_GENERIC_EXPLODE
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GLOWSTONE_DUST).setName(getName()).build());
        setDescription("Enjoy your spawn camping...");

        this.particles = Effect.EXPLOSION_HUGE;
        this.delay = 40;
        this.proximity = true;
//        this.duration = 100;
        this.damage = 3.0;
        this.explosionSize = 3.0;
        this.explosionDelay = 0;
        this.explosionStrength = 3.0;
    }
}
