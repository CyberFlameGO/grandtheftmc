package net.grandtheftmc.gtm.weapon.ranged.special;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.SpecialWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class Minigun extends SpecialWeapon implements RankedWeapon, WeaponVisualStatue {

    /**
     * Construct a new RangedWeapon.
     */
    public Minigun() {
        super(
        		(short) 33,
                "Minigun", //Name
                WeaponType.MINIGUN, //Weapon Type
                AmmoType.MINIGUN, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 321).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_ARMOR_EQUIP_DIAMOND,
                        Sound.ITEM_FLINTANDSTEEL_USE,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_HOE).setName(getName()).build());
        setDescription("Say hello to my", "little friend.");

        setSupportedAttachments(Attachment.GRIP);
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

        this.walkSpeed = 0.1; //Weapon

        this.damage = 2.25; //RangedWeapon
        this.meleeDamage = 3.0; //RangedWeapon
        this.accuracy = 0.05; //RangedWeapon
        this.magSize = 600; //RangedWeapon
        this.reloadTime = 100; //RangedWeapon
        this.range = 55; //RangedWeapon
        this.recoil = 0.05; //RangedWeapon
        this.zoom = 3; //RangedWeapon
        this.reloadShoot = true;

        this.minigun = true; //SpecialWeapon
        this.rpm = 1200; //SpecialWeapon
        this.rps = 20;
        this.multiShoot = true;
    }

    @Override
    public UserRank requiredRank() {
        return UserRank.SUPREME;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);

        ArmorStand clickable = spawnEntity(origin.clone().add(-0.6, 0.6, 0), this, WeaponVisualStatue.VisualType.NAME);
//        clickable.setVisible(true);

        ArmorStand clickable2 = spawnEntity(origin.clone().add(-1.4, 0.6, 0), this, WeaponVisualStatue.VisualType.NAME);
//        clickable2.setVisible(true);

        Location weaponLoc = origin.clone().add(-0.3, 1.1, 0.4);
        weaponLoc.setYaw(90.0f);
        ArmorStand weapon = spawnEntity(weaponLoc, this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(315), 0f, 0f));
        weapon.setMarker(true);

        ArmorStand support = spawnEntity(origin.clone().add(-1.75, -0.34, 0.25), this, WeaponVisualStatue.VisualType.NONE);
        support.setHelmet(new ItemStack(Material.END_ROD));
        support.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(300), 0f, 0f));
        support.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -364.5, 25.5, 241.5, 0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
        entity.setMetadata("statue_X", new FixedMetadataValue(GTM.getInstance(), -1d));
    }
}
