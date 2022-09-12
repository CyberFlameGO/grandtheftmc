package net.grandtheftmc.vice.weapon.ranged.smg;

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
import net.grandtheftmc.guns.weapon.ranged.guns.SMGWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class GusenbergSweeper extends SMGWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public GusenbergSweeper() {
        super(
        		(short) 15,
                "Gusenberg Sweeper", //Name
                WeaponType.SMG, //Weapon Type
                AmmoType.SMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 141).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_BLAZE_HURT,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.BLOCK_WOODEN_DOOR_OPEN,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.IRON_HOE).setName(getName()).build());
        setDescription("With this gun, you can", "make them an offer they", "can't refuse.");

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

        this.damage = 6.0; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.016; //RangedWeapon
        this.magSize = 50; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 47; //RangedWeapon
        this.recoil = 0.0; //RangedWeapon
        this.zoom = 2; //RangedWeapon

        this.rpm = 555; //AssultRifleWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.PREMIUM;
    }
}
