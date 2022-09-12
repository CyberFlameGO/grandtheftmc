package net.grandtheftmc.gtm.weapon.ranged.launcher;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class HomingLauncher extends LauncherWeapon implements RankedWeapon, WeaponVisualStatue {

    /**
     * Construct a new RangedWeapon.
     */
    public HomingLauncher() {
        super(
        		(short) 32,
                "Homing Launcher", //Name
                WeaponType.LAUNCHER, //Weapon Type
                AmmoType.ROCKET, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 311).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_FIREWORK_LAUNCH,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_CONTRACT,
                },
                Effect.FIREWORKS_SPARK //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.SULPHUR).setName(getName()).build());
        setDescription("Imagine a homing pidgeon.", "But it explodes.");

        setSupportedAttachments(Attachment.GRIP);
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

        this.walkSpeed = 0.12; //Weapon
        this.delay = 60;

        this.damage = 16; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.003; //RangedWeapon
        this.magSize = 1; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 100; //RangedWeapon
        this.recoil = 1.0; //RangedWeapon
        this.zoom = 1; //RangedWeapon

        this.homingLauncher = true; //LauncherWeapon
        this.rocketSpeed = 1.0; //LauncherWeapon
        this.explosionSize = 2.5; //LauncherWeapon
        this.explosionStrength = 1.7; //LauncherWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SUPREME;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);

        ArmorStand clickable = spawnEntity(origin.clone().add(0.2, 0.5, 0), this, WeaponVisualStatue.VisualType.NAME);
//        clickable.setVisible(true);

        ArmorStand clickable2 = spawnEntity(origin.clone().add(-0.6, 0.5, 0), this, WeaponVisualStatue.VisualType.NAME);
//        clickable2.setVisible(true);

        Location weaponLoc = origin.clone().add(0.3, 0.02, 0.4);
        weaponLoc.setYaw(90.0f);
        ArmorStand weapon = spawnEntity(weaponLoc, this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(255), 0f, 0f));
        weapon.setMarker(true);

        ArmorStand support = spawnEntity(origin.clone().add(-0.92, -0.4, 0.25), this, WeaponVisualStatue.VisualType.NONE);
        support.setHelmet(new ItemStack(Material.END_ROD));
        support.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(300), 0f, 0f));
        support.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -367.5, 25.5, 241.5, 0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
        entity.setMetadata("statue_X", new FixedMetadataValue(GTM.getInstance(), -0.2d));
    }
}
