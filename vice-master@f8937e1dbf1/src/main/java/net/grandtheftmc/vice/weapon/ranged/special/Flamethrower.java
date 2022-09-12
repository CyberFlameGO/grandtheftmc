package net.grandtheftmc.vice.weapon.ranged.special;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.ShotgunWeapon;

/**
 * Created by Luke Bingham on 03/08/2017.
 */
public class Flamethrower extends ShotgunWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public Flamethrower() {
        super(
        		(short) 40,
                "Flamethrower", //Name
                WeaponType.FLAMETHROWER, //TODO Weapon Type
                AmmoType.NONE, //TODO AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 391).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ITEM_FIRECHARGE_USE,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.UI_BUTTON_CLICK,
                },
                Effect.MOBSPAWNER_FLAMES //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.FLINT_AND_STEEL).setDurability((short) 10).setName(getName()).build());
        setDescription("Is it me, or is it", "getting hot in here?");

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
        this.delay = 2;

        this.damage = 6.0; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.05; //RangedWeapon
        this.magSize = 25; //RangedWeapon
        this.reloadTime = 100; //RangedWeapon
        this.range = 25; //RangedWeapon
        this.recoil = 0.001; //RangedWeapon
        this.zoom = 3; //RangedWeapon
        this.shellSize = 5;

//        this.flamethrower = true; //SpecialWeapon
//        this.rpm = 200; //SpecialWeapon
    }

    @Override
    public int getRpm() {
        return 200;
    }

    @Override
    public boolean isAutomatic() {
        return true;
    }
}
