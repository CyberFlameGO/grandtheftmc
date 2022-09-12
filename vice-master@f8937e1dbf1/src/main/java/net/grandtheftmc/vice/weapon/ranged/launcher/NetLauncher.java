package net.grandtheftmc.vice.weapon.ranged.launcher;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.LauncherWeapon;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class NetLauncher extends LauncherWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public NetLauncher() {
        super(
        		(short) 39,
                "Net Launcher", //Name
                WeaponType.NETGUN, //TODO Weapon Type
                AmmoType.ROCKET, //TODO AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 381).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_SHULKER_SHOOT,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_CONTRACT,
                },
                Effect.BLAZE_SHOOT //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.SAPLING).setName(getName()).build());
        setDescription("It's like discount,", (Core.getSettings().isSister() ? "weak" : "shitty") + " spiderman.");

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
        this.delay = 50;

        this.damage = 2.5; //RangedWeapon
        this.meleeDamage = 8.0; //RangedWeapon
        this.accuracy = 0.005; //RangedWeapon
        this.magSize = 4; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 35; //RangedWeapon
        this.recoil = 1.0; //RangedWeapon
        this.zoom = 1; //RangedWeapon

        //this.netgun = true; //Launcher weapon todo: disabled because it was updating the actual class, now it just uses getName().equalsignorecase("net launcher")
    }
}
