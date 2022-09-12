package net.grandtheftmc.vice.weapon.ranged.pistol;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.PistolWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class StunGun extends PistolWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public StunGun() {
        super(
        		(short) 7,
                "Stun Gun", //Name
                WeaponType.PISTOL, //Weapon Type
                AmmoType.NONE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 61).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.BLOCK_DISPENSER_DISPENSE,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.BLOCK_NOTE_HAT,
                },
                Effect.MAGIC_CRIT //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.WOOD_SPADE).setName(getName()).build());
        setDescription("Nothing quite like", "50,000 volts straight", "to the " + (Core.getSettings().isSister() ? "head." : "nipples."));

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

        this.walkSpeed = 0.2; //Weapon
        this.delay = 57; //Weapon

        this.damage = 1.0; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.008; //RangedWeapon
        this.magSize = 1; //RangedWeapon
        this.reloadTime = 60; //RangedWeapon
        this.range = 10; //RangedWeapon
        this.zoom = 0; //RangedWeapon

        this.stun = true; //PistolWeapon
        this.duration = 80; //PistolWeapon
    }
}
