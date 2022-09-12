package net.grandtheftmc.gtm.weapon.ranged.pistol;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import org.bukkit.*;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.PistolWeapon;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class Pistol extends PistolWeapon implements WeaponVisualStatue {

    /**
     * Construct a new RangedWeapon.
     */
    public Pistol() {
        super(
        		(short) 6,
                "Pistol", //Name
                WeaponType.PISTOL, //Weapon Type
                AmmoType.PISTOL, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 51).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_FIREWORK_BLAST,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.ITEM_ARMOR_EQUIP_LEATHER,
                        Sound.BLOCK_NOTE_HAT,
                },
                Effect.CRIT //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.WOOD_SWORD).setName(getName()).build());
        setDescription((Core.getSettings().isSister() ? "." : "Bitch ") + "basic pistol,", "great for scaring the cat");

        setSupportedAttachments(Attachment.SUPPRESSOR, Attachment.EXTENDED_MAGS, Attachment.GRIP, Attachment.SCOPE);
        setWeaponSkins(
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 2), "&6&lUrban Camo"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 5), "&e&lGreen"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 7), "&e&lSlate"),
                new WeaponSkin(getWeaponType(), (short) (getWeaponIdentifier() + 6), "&6&lPurple")
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
        this.delay = 5; //Weapon

        this.damage = 3.5; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.02; //RangedWeapon
        this.magSize = 12; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 25; //RangedWeapon
        this.zoom = 4; //RangedWeapon
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = getOrigin(world);
        ArmorStand glass = spawnEntity(origin.clone(), this, VisualType.NAME);
        glass.setHelmet(new ItemStack(Material.GLASS));

        ArmorStand weapon = spawnEntity(origin.clone().add(-0.12, 0.2, -1.05), this, VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(0, AngleUtil.getRadianFromDegree(25), AngleUtil.getRadianFromDegree(90)));
        weapon.setMarker(true);



        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -359.5, 25.5, 231.5, -90.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
    }
}
