package net.grandtheftmc.vice.weapon.ranged.assault;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.AssultRifleWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class AdvancedRifle extends AssultRifleWeapon {
    /**
     * Construct a new RangedWeapon.
     */
    public AdvancedRifle() {
        super(
        		(short) 24,
                "Advanced Rifle", //Name
                WeaponType.ASSAULT, //Weapon Type
                AmmoType.ASSAULT_RIFLE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 231).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ITEM_BREAK,
                        Sound.ITEM_ARMOR_EQUIP_IRON,
                        Sound.ITEM_ARMOR_EQUIP_IRON,
                        Sound.ENTITY_SKELETON_AMBIENT,
                },
                Effect.VOID_FOG //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.STONE_AXE).setName(getName()).build());
        setDescription("Bit egotistical to name", "your gun 'advanced', no?");

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

        this.walkSpeed = 0.18; //Weapon

        this.damage = 8.0; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.02; //RangedWeapon
        this.magSize = 30; //RangedWeapon
        this.reloadTime = 35; //RangedWeapon
        this.range = 45; //RangedWeapon
        this.recoil = 0.2; //RangedWeapon
        this.zoom = 3; //RangedWeapon

        this.rpm = 500; //AssultRifleWeapon
    }
}
