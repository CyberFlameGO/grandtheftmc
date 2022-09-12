package net.grandtheftmc.gtm.weapon.ranged.shotgun;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.guns.ShotgunWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class PumpShotgun extends ShotgunWeapon implements WeaponVisualStatue {

    /**
     * Construct a new RangedWeapon.
     */
    public PumpShotgun() {
        super(
        		(short) 17,
                "Pump Shotgun", //Name
                WeaponType.SHOTGUN, //Weapon Type
                AmmoType.SHOTGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 161).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ENTITY_IRONGOLEM_ATTACK,
                },
                Effect.SMALL_SMOKE //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GOLD_SPADE).setName(getName()).build());
        setDescription("Standard, solid shotgun.", "This'll keep you going.");

        setWeaponSkins(
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 2), "&6&lUrban Camo"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 5), "&e&lGreen"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), "&e&lSlate"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 6), "&6&lPurple")
                /*new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 1), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 2), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 3), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 4), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 5), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 6), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 8), null),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 9), null)*/
        );

        this.walkSpeed = 0.14; //Weapon
        this.delay = 13;

        this.damage = 2.5; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        //this.accuracy = 0.025; //RangedWeapon
        this.accuracy = 0.12; //RangedWeapon
        this.magSize = 8; //RangedWeapon
        this.reloadTime = 56; //RangedWeapon
        this.range = 10; //RangedWeapon
        this.recoil = 0.25; //RangedWeapon
        this.zoom = 6; //RangedWeapon
        this.reloadShoot = true;

        this.shellSize = 8; //AssultRifleWeapon
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);
        ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), this, WeaponVisualStatue.VisualType.NAME);
        clickable.setSmall(true);

        ArmorStand weapon = spawnEntity(origin.clone().add(-0.35, 0.08, -0.1), this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(180), 0, AngleUtil.getRadianFromDegree(20)));
        weapon.setMarker(true);

//        Location hookLoc = origin.clone().add(0.045, 1.02, -0.68);
//        hookLoc.setPitch(0);
//        hookLoc.setYaw(0);
//        ArmorStand hook = spawnEntity(hookLoc, this, WeaponVisualStatue.VisualType.NONE);
//        hook.setHelmet(new ItemStack(Material.TRIPWIRE_HOOK));
//        hook.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(45), 0, 0));
//        hook.setSmall(true);
//        hook.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -376.5, 25.5, 239.5, 180.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.5));
    }
}
