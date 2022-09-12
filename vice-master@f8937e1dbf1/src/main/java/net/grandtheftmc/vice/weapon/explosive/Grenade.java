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
public class Grenade extends ThrowableWeapon {

    /**
     * Construct a new Weapon.
     */
    public Grenade() {
        super(
        		(short) 34,
                "Grenade", //Name
                WeaponType.THROWABLE, //Weapon Type
                AmmoType.EXPLOSIVE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 331).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_GENERIC_EXPLODE
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.FIREWORK_CHARGE).setName(getName()).build());
        setDescription("How to clear a room", "in 3..2..1.");

        this.particles = Effect.EXPLOSION_HUGE;
        this.delay = 55;
        this.damage = 3;
        this.explosionSize = 5.0;
        this.explosionDelay = 60;
        this.explosionStrength = 2.0;
    }
}
