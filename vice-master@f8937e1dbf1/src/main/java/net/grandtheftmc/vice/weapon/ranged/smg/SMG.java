package net.grandtheftmc.vice.weapon.ranged.smg;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.SMGWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class SMG extends SMGWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public SMG() {
        super(
        		(short) 12,
                "SMG", //Name
                WeaponType.SMG, //Weapon Type
                AmmoType.SMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 111).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_BLAZE_HURT,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.BLOCK_WOODEN_DOOR_OPEN,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.IRON_SPADE).setName(getName()).build());
        setDescription("The intro to proper", "gang warfare.");

        setSupportedAttachments(Attachment.SUPPRESSOR, Attachment.EXTENDED_MAGS, Attachment.GRIP, Attachment.SCOPE);
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

        this.walkSpeed = 0.2; //Weapon

        this.damage = 4.0; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.025; //RangedWeapon
        this.magSize = 30; //RangedWeapon
        this.reloadTime = 50; //RangedWeapon
        this.range = 35; //RangedWeapon
        this.recoil = 0.0; //RangedWeapon
        this.zoom = 4; //RangedWeapon

        this.rpm = 510; //AssultRifleWeapon
    }
}
