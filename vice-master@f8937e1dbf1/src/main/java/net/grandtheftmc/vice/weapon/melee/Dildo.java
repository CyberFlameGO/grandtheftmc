package net.grandtheftmc.vice.weapon.melee;

import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class Dildo extends MeleeWeapon {

    /**
     * Construct a new Weapon.
     */
    public Dildo() {
        super(
        		(short) 41,
                "Dildo", //Name
                WeaponType.MELEE, //Weapon Type
                AmmoType.MELEE, //AmmoType

                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 401).build(), //ItemStack
                new Sound[] { //Sounds
                        Sound.ENTITY_PLAYER_ATTACK_STRONG,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.SAPLING, (byte) 1).setName(getName()).build());
        setDescription("Certified 'me time'.");

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

        this.delay = 7;
        this.meleeDamage = 8.0;
    }
}
