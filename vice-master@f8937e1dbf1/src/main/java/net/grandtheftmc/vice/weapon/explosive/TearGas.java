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
public class TearGas extends ThrowableWeapon {

    /**
     * Construct a new Weapon.
     */
    public TearGas() {
        super(
        		(short) 35,
                "Tear Gas", //Name
                WeaponType.THROWABLE, //Weapon Type
                AmmoType.EXPLOSIVE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 341).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.BLOCK_LAVA_EXTINGUISH
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GHAST_TEAR).setName(getName()).build());
        setDescription("This will make them", "cry more than your ex.");

        this.particles = Effect.CLOUD;
        this.delay = 55;
        this.teargas = true;
        this.duration = 140;
        this.damage = 1.0;
        this.explosionSize = 5.0;
        this.explosionDelay = 20;
        this.explosionStrength = 0.0;
    }
}
