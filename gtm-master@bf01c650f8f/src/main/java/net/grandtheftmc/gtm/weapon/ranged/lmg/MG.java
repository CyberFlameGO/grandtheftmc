package net.grandtheftmc.gtm.weapon.ranged.lmg;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LMGWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class MG extends LMGWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public MG() {
        super(
        		(short) 26,
                "MG", //Name
                WeaponType.LMG, //Weapon Type
                AmmoType.LMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 251).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.BLOCK_NOTE_SNARE,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ENTITY_SKELETON_STEP,
                },
                Effect.VOID_FOG //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_SWORD).setName(getName()).build());
        setDescription("Cover me,", "I'm going in!");

        setSupportedAttachments(Attachment.SUPPRESSOR, Attachment.EXTENDED_MAGS, Attachment.GRIP, Attachment.SCOPE);
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
        //this.delay = 5;

        this.damage = 2.5; //RangedWeapon
        this.meleeDamage = 6.0; //RangedWeapon
        this.accuracy = 0.055; //RangedWeapon
        this.magSize = 55; //RangedWeapon
        this.reloadTime = 50; //RangedWeapon
        this.range = 60; //RangedWeapon
        this.recoil = 0.1; //RangedWeapon
        this.zoom = 3; //RangedWeapon
        this.rpm = 760; //LMGWeapon
        this.rps = 7;
        this.multiShoot = true;
    }
}
