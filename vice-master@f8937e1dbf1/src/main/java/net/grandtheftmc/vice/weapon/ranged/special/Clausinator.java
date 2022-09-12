package net.grandtheftmc.vice.weapon.ranged.special;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.guns.PistolWeapon;

/**
 * Created by Timothy Lampen on 2017-12-12.
 */
public class Clausinator extends PistolWeapon {
    public Clausinator() {
        super(
        		(short) 50,
        		"Clausinator", //Name
                WeaponType.CLAUSINATOR, //Weapon Type
                AmmoType.NONE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 1011).build(),
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_FLINTANDSTEEL_USE,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setDescription("Snowball fight!");

        setOldItemStack(new ItemStack(Material.ACACIA_DOOR_ITEM));



        this.walkSpeed = 0.1; //Weapon
        this.delay = 40; //Weapon

        this.damage = 0.001; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.025; //RangedWeapon
        this.magSize = 600; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 25; //RangedWeapon
        this.zoom = 4; //RangedWeapon
    }
}
