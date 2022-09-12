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
public class StickyBomb extends ThrowableWeapon {

    /**
     * Construct a new Weapon.
     */
    public StickyBomb() {
        super(
        		(short) 37,
                "Sticky Bomb", //Name
                WeaponType.THROWABLE, //Weapon Type
                AmmoType.EXPLOSIVE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 361).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_GENERIC_EXPLODE
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.FIREBALL).setName(getName()).build());
        setDescription("It's like the explosion", "is hugging you!");

        this.particles = Effect.EXPLOSION_HUGE;
        this.delay = 40;
        this.sticky = true;
//        this.duration = 100;
        this.damage = 3;
        this.explosionSize = 3.0;
        this.explosionDelay = 0;
        this.tntFuseDelay = 80;
        this.explosionStrength = 0.9;
    }
}
