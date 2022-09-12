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
public class StickyBomb extends ThrowableWeapon implements WeaponVisualStatue {

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
        this.delay = 60;
        this.sticky = true;
//        this.duration = 100;
        this.damage = 16;
        this.explosionSize = 5.0;
        //this.explosionDelay = 100;
        this.meleeDamage = 1;
        //this.tntFuseDelay = 80;
        this.explosionStrength = 0.9;
        this.scaledDamage = true;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = getOrigin(world);
        ArmorStand glass = spawnEntity(origin.clone(), this, VisualType.NAME);
        glass.setHelmet(new ItemStack(Material.GLASS));

        Location weaponLoc = origin.clone().add(0.85, 0.3, -0.5);
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
        return new Location(world, -366.5, 25.5, 233.5, -0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
    }
}
