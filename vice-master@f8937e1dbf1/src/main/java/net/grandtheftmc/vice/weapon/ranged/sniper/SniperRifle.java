package net.grandtheftmc.vice.weapon.ranged.sniper;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.SniperWeapon;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class SniperRifle extends SniperWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public SniperRifle() {
        super(
                (short) 28,
                "Sniper Rifle", //Name
                WeaponType.SNIPER, //Weapon Type
                AmmoType.SNIPER, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 271).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_IRONGOLEM_HURT,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_EXTEND,
                },
                Effect.CLOUD //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_PICKAXE).setName(getName()).build());
        setDescription("When you like killing", "people without getting", "your hands dirty.");

        setSupportedAttachments(Attachment.SUPPRESSOR, Attachment.GRIP, Attachment.ADVANCED_SCOPE);
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
        this.delay = 32;

        this.damage = 23.0; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.0175; //RangedWeapon
        this.magSize = 10; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 95; //RangedWeapon
        this.recoil = 0.3; //RangedWeapon
        this.zoom = 8; //RangedWeapon
    }
}
