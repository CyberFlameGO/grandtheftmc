package net.grandtheftmc.vice.weapon.ranged.shotgun;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.guns.ShotgunWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class SawedoffShotgun extends ShotgunWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public SawedoffShotgun() {
        super(
        		(short) 16,
                "Sawed-off Shotgun", //Name
                WeaponType.SHOTGUN, //Weapon Type
                AmmoType.SHOTGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 151).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ENTITY_IRONGOLEM_ATTACK,
                },
                Effect.SMOKE //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GOLD_SWORD).setName(getName()).build());
        setDescription("Yehaw, time t'shoot me", "some uppities!");

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

        this.walkSpeed = 0.16; //Weapon
        this.delay = 70;

        this.damage = 3.75; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.075; //RangedWeapon
        this.magSize = 8; //RangedWeapon
        this.reloadTime = 56; //RangedWeapon
        this.range = 15; //RangedWeapon
        this.recoil = 1.0; //RangedWeapon
        this.zoom = 6; //RangedWeapon
        this.reloadShoot = true;

        this.shellSize = 5; //AssultRifleWeapon
    }
}
