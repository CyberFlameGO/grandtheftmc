package net.grandtheftmc.vice.weapon.ranged.launcher;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class HomingLauncher extends LauncherWeapon implements RankedWeapon {

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
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), "&e&lSlate")
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

        this.damage = 350.0; //RangedWeapon
        this.meleeDamage = 7.0; //RangedWeapon
        this.accuracy = 0.005; //RangedWeapon
        this.magSize = 1; //RangedWeapon
        this.reloadTime = 60; //RangedWeapon
        this.range = 70; //RangedWeapon
        this.recoil = 1.0; //RangedWeapon
        this.zoom = 1; //RangedWeapon

        this.homingLauncher = true; //LauncherWeapon
        this.rocketSpeed = 1.0; //LauncherWeapon
        this.explosionSize = 5.0; //LauncherWeapon
        this.explosionStrength = 1.7; //LauncherWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SUPREME;
    }
}
