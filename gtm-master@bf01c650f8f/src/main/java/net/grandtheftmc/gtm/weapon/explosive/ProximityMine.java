package net.grandtheftmc.gtm.weapon.explosive;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.ThrowableWeapon;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class ProximityMine extends ThrowableWeapon implements WeaponVisualStatue {

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
        this.damage = 10;
        this.meleeDamage = 1.0;
        this.explosionSize = 5.0;
        this.explosionDelay = 0;
        this.explosionStrength = 3.0;
        this.scaledDamage = false;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = getOrigin(world);
        ArmorStand glass = spawnEntity(origin.clone(), this, VisualType.NAME);
        glass.setHelmet(new ItemStack(Material.GLASS));

        Location weaponLoc = origin.clone().add(0.9, 0.3, -0.5);
        weaponLoc.setYaw(-30.0f);
        ArmorStand weapon = spawnEntity(weaponLoc, this, VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(0, AngleUtil.getRadianFromDegree(180), AngleUtil.getRadianFromDegree(90)));
        weapon.setMarker(true);



        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -367.5, 25.5, 233.5, -0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
    }
}
