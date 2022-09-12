package net.grandtheftmc.gtm.weapon.explosive;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import org.bukkit.*;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.ThrowableWeapon;
import net.grandtheftmc.guns.weapon.WeaponType;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class TearGas extends ThrowableWeapon implements WeaponVisualStatue {

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
        this.delay = 60;
        this.teargas = true;
        this.duration = 140;
        this.damage = 1.0;
        this.meleeDamage = 1.0;
        this.explosionSize = 5.0;
        this.explosionDelay = 40;
        this.explosionStrength = 0.0;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = getOrigin(world);
        ArmorStand glass = spawnEntity(origin.clone(), this, VisualType.NAME);
        glass.setHelmet(new ItemStack(Material.GLASS));

        Location weaponLoc = origin.clone().add(0.3, 0.85, -0.5);
        weaponLoc.setYaw(-30.0f);
        ArmorStand weapon = spawnEntity(weaponLoc, this, VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setMarker(true);



        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -365.5, 25.5, 234.5, -0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
    }
}
