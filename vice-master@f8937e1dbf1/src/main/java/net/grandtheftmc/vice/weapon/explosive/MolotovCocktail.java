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
public class MolotovCocktail extends ThrowableWeapon {

    /**
     * Construct a new Weapon.
     */
    public MolotovCocktail() {
        super(
        		(short) 36,
                "Molotov Cocktail", //Name
                WeaponType.THROWABLE, //Weapon Type
                AmmoType.EXPLOSIVE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 351).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SKELETON_SHOOT,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_BAT_TAKEOFF,
                        Sound.ENTITY_GENERIC_EXPLODE
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.MAGMA_CREAM).setName(getName()).build());
        setDescription("Nothing says angst quite", "as much as a bottle of", "liquid fire.");

        this.particles = Effect.FLAME;
        this.delay = 55;
        this.flammable = true;
        this.duration = 140;
        this.damage = 1.0;
        this.explosionSize = 5.0;
        this.explosionDelay = 20;
        this.explosionStrength = 0.3;
    }
}
