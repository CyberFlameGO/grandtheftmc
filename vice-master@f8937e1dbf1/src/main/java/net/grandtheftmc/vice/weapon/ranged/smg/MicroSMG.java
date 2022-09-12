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
public class MicroSMG extends SMGWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public MicroSMG() {
        super(
        		(short) 11,
                "Micro SMG", //Name
                WeaponType.SMG, //Weapon Type
                AmmoType.SMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 101).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_BLAZE_HURT,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.BLOCK_WOODEN_DOOR_OPEN,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.IRON_SWORD).setName(getName()).build());
        setDescription("It's like a peashooter,", "on steriods.");

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
        this.accuracy = 0.035; //RangedWeapon
        this.magSize = 16; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 25; //RangedWeapon
        this.recoil = 0.0; //RangedWeapon
        this.zoom = 4; //RangedWeapon

        this.rpm = 600; //AssultRifleWeapon
    }
}
