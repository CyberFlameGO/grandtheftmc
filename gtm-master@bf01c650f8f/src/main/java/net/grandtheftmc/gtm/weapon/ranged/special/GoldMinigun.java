package net.grandtheftmc.gtm.weapon.ranged.special;

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
import net.grandtheftmc.guns.weapon.ranged.guns.SpecialWeapon;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class GoldMinigun extends SpecialWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public GoldMinigun() {
        super(
        		(short) 42,
                "Gold Minigun", //Name
                WeaponType.MINIGUN, //Weapon Type
                AmmoType.MINIGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 411).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_FLINTANDSTEEL_USE,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_HOE).setDurability((short) -1/* IDK?.. */).setName(getName()).build());//TODO Unknown at the moment.
        setDescription("Is it me, or is it", "getting hot in here?");

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

        this.walkSpeed = 0.1; //Weapon

        this.damage = 2.25; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.05; //RangedWeapon
        this.magSize = 600; //RangedWeapon
        this.reloadTime = 100; //RangedWeapon
        this.range = 55; //RangedWeapon
        this.recoil = 0.05; //RangedWeapon
        this.zoom = 3; //RangedWeapon
        this.reloadShoot = true;

        this.minigun = true; //SpecialWeapon
        this.rpm = 1200; //SpecialWeapon
        this.rps = 20;
        this.multiShoot = true;
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SUPREME;
    }
}
