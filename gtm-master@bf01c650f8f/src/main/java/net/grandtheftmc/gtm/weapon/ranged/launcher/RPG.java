package net.grandtheftmc.gtm.weapon.ranged.launcher;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class RPG extends LauncherWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public RPG() {
        super(
        		(short) 30,
                "RPG", //Name
                WeaponType.LAUNCHER, //Weapon Type
                AmmoType.ROCKET, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 291).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_FIREWORK_LAUNCH,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_CONTRACT,
                },
                Effect.FIREWORKS_SPARK //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.FEATHER).setName(getName()).build());
        setDescription("Want to take out", "the side of a building?", "Use this.");

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
        this.delay = 80;

        this.damage = 16; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.005; //RangedWeapon
        this.magSize = 1; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 60; //RangedWeapon
        this.recoil = 1.0; //RangedWeapon
        this.zoom = 1; //RangedWeapon

        this.rocketSpeed = 2.0; //LauncherWeapon
        this.explosionSize = 6.0; //LauncherWeapon
        this.explosionStrength = 1.7; //LauncherWeapon
        this.scaledDamage = true;
    }
}














