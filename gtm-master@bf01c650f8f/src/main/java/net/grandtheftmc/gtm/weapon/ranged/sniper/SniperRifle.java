package net.grandtheftmc.gtm.weapon.ranged.sniper;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.SniperWeapon;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class SniperRifle extends SniperWeapon implements WeaponVisualStatue {

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

        this.walkSpeed = 0.14; //Weapon
        this.delay = 50;

        this.damage = 18; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.005; //RangedWeapon
        this.magSize = 10; //RangedWeapon
        this.reloadTime = 80; //RangedWeapon
        this.range = 95; //RangedWeapon
        this.recoil = 0.3; //RangedWeapon
        this.zoom = 8; //RangedWeapon
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);

        Location clickLoc = origin.clone().add(0, 0.2, -0.6);
        clickLoc.setYaw(90);
        ArmorStand clickable = spawnEntity(clickLoc, this, WeaponVisualStatue.VisualType.NAME);
//        clickable.setVisible(true);

        Location clickLoc2 = origin.clone().add(0, 0.2, 0);
        clickLoc2.setYaw(90);
        ArmorStand clickable2 = spawnEntity(clickLoc2, this, WeaponVisualStatue.VisualType.NAME);
//        clickable2.setVisible(true);

        Location weaponLoc = origin.clone().add(0.3, 0.28, -1.2);
        ArmorStand weapon = spawnEntity(weaponLoc, this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(280), 0f, 0f));
        weapon.setMarker(true);

//        ArmorStand support = spawnEntity(origin.clone().add(-1.75, -0.34, 0.25), this, WeaponVisualStatue.VisualType.NONE);
//        support.setHelmet(new ItemStack(Material.END_ROD));
//        support.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(300), 0f, 0f));
//        support.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -373.5, 25.5, 233.5, 0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.25));
    }
}
