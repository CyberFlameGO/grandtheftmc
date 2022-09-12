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
public class PumpShotgun extends ShotgunWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public PumpShotgun() {
        super(
        		(short) 17,
                "Pump Shotgun", //Name
                WeaponType.SHOTGUN, //Weapon Type
                AmmoType.SHOTGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 161).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_DOOR_WOOD,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ITEM_ARMOR_EQUIP_CHAIN,
                        Sound.ENTITY_IRONGOLEM_ATTACK,
                },
                Effect.SMOKE //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.GOLD_SPADE).setName(getName()).build());
        setDescription("Standard, solid shotgun.", "This'll keep you going.");

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

        this.walkSpeed = 0.14; //Weapon
        this.delay = 70;

        this.damage = 4.75; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.05; //RangedWeapon
        this.magSize = 8; //RangedWeapon
        this.reloadTime = 56; //RangedWeapon
        this.range = 20; //RangedWeapon
        this.recoil = 0.7; //RangedWeapon
        this.zoom = 6; //RangedWeapon
        this.reloadShoot = true;

        this.shellSize = 4; //AssultRifleWeapon
    }
}
