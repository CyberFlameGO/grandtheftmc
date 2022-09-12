package net.grandtheftmc.vice.weapon.ranged.launcher;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class GrenadeLauncher extends LauncherWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public GrenadeLauncher() {
        super(
        		(short) 31,
                "Grenade Launcher", //Name
                WeaponType.LAUNCHER, //Weapon Type
                AmmoType.GRENADE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 301).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_CHICKEN_EGG,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_CONTRACT,
                },
                Effect.FIREWORKS_SPARK //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.SHEARS).setName(getName()).build());
        setDescription("For if your throwing", "game is weak " + (Core.getSettings().isSister() ? "." : "AF."));

        setSupportedAttachments(Attachment.GRIP);
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
        this.delay = 20;

        this.damage = 350.0; //RangedWeapon
        this.meleeDamage = 6.0; //RangedWeapon
        this.accuracy = 0.005; //RangedWeapon
        this.magSize = 6; //RangedWeapon
        this.reloadTime = 50; //RangedWeapon
        this.range = 50; //RangedWeapon
        this.recoil = 0.5; //RangedWeapon
        this.zoom = 1; //RangedWeapon

        this.blowOnHit = false; //LauncherWeapon
        this.blowDelay = 5;
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SPONSOR;
    }
}
