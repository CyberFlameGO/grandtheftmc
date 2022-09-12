package net.grandtheftmc.vice.weapon.ranged.pistol;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.guns.PistolWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class MarksmanPistol extends PistolWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public MarksmanPistol() {
        super(
        		(short) 10,
                "Marksman Pistol", //Name
                WeaponType.PISTOL, //Weapon Type
                AmmoType.PISTOL, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 91).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_FIREWORK_BLAST,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.BLOCK_NOTE_HAT,
                },
                Effect.CRIT //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.WOOD_HOE).setName(getName()).build());
        setDescription("For when you can't afford", "a real sniper rifle.");

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
        this.delay = 57; //Weapon

        this.damage = 20.0; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.035; //RangedWeapon
        this.magSize = 1; //RangedWeapon
        this.reloadTime = 100; //RangedWeapon
        this.range = 10; //RangedWeapon
        this.zoom = 4; //RangedWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.VIP;
    }
}
