package net.grandtheftmc.vice.weapon.ranged.shotgun;

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
import net.grandtheftmc.guns.weapon.ranged.guns.ShotgunWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class HeavyShotgun extends ShotgunWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public HeavyShotgun() {
        super(
        		(short) 20,
                "Heavy Shotgun", //Name
                WeaponType.SHOTGUN, //Weapon Type
                AmmoType.SHOTGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 191).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ENTITY_IRONGOLEM_ATTACK,
                },
                Effect.SMOKE //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GOLD_HOE).setName(getName()).build());
        setDescription("Like your normal shotgun,", "but even heftier.");

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

        this.walkSpeed = 0.12; //Weapon
        this.delay = 6;

        this.damage = 6.25; //RangedWeapon
        this.meleeDamage = 6.0; //RangedWeapon
        this.accuracy = 0.022; //RangedWeapon
        this.magSize = 6; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 20; //RangedWeapon
        this.recoil = 0.6; //RangedWeapon
        this.zoom = 3; //RangedWeapon

        this.shellSize = 3; //AssultRifleWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.VIP;
    }
}
