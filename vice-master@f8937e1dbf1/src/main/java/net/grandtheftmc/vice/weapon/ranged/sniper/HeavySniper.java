package net.grandtheftmc.vice.weapon.ranged.sniper;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.SniperWeapon;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class HeavySniper extends SniperWeapon implements RankedWeapon {

    /**
     * Construct a new RangedWeapon.
     */
    public HeavySniper() {
        super(
                (short) 29,
                "Heavy Sniper", //Name
                WeaponType.SNIPER, //Weapon Type
                AmmoType.SNIPER, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 281).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_IRONGOLEM_HURT,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.BLOCK_PISTON_EXTEND,
                },
                Effect.CLOUD //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_AXE).setName(getName()).build());
        setDescription("When you really like", "killing people without", "getting your hands dirty.");

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
        this.delay = 41;

        this.damage = 34.5; //RangedWeapon
        this.meleeDamage = 6.0; //RangedWeapon
        this.accuracy = 0.01265; //RangedWeapon
        this.magSize = 6; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 100; //RangedWeapon
        this.recoil = 0.3; //RangedWeapon
        this.zoom = 9; //RangedWeapon
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.ELITE;
    }
}
