package net.grandtheftmc.gtm.weapon.ranged.assault;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.AssultRifleWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class AdvancedRifle extends AssultRifleWeapon implements WeaponVisualStatue {
    /**
     * Construct a new RangedWeapon.
     */
    public AdvancedRifle() {
        super(
        		(short) 24,
                "Advanced Rifle", //Name
                WeaponType.ASSAULT, //Weapon Type
                AmmoType.ASSAULT_RIFLE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 231).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_ITEM_BREAK,
                        Sound.ITEM_ARMOR_EQUIP_IRON,
                        Sound.ITEM_ARMOR_EQUIP_IRON,
                        Sound.ENTITY_SKELETON_AMBIENT,
                },
                Effect.VOID_FOG //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.STONE_AXE).setName(getName()).build());
        setDescription("Bit egotistical to name", "your gun 'advanced', no?");

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

        this.walkSpeed = 0.18; //Weapon

        this.damage = 3.5; //RangedWeapon
        this.meleeDamage = 5.0; //RangedWeapon
        this.accuracy = 0.015; //RangedWeapon
        this.magSize = 30; //RangedWeapon
        this.reloadTime = 35; //RangedWeapon
        this.range = 45; //RangedWeapon
        this.recoil = 0.1; //RangedWeapon
        this.zoom = 3; //RangedWeapon
        this.rps = 8;
        this.rpm = 500; //AssultRifleWeapon
        this.multiShoot = true;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);
        ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), this, WeaponVisualStatue.VisualType.NAME);
        clickable.setSmall(true);

        ArmorStand weapon = spawnEntity(origin.clone().add(-0.2, 0, 0.4), this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(180), 0, AngleUtil.getRadianFromDegree(20)));
        weapon.setMarker(true);

//        Location hookLoc = origin.clone().add(0.045, 1.02, -0.68);
//        hookLoc.setPitch(0);
//        hookLoc.setYaw(0);
//        ArmorStand hook = spawnEntity(hookLoc, this, WeaponVisualStatue.VisualType.NONE);
//        hook.setHelmet(new ItemStack(Material.TRIPWIRE_HOOK));
//        hook.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(45), 0, 0));
//        hook.setSmall(true);
//        hook.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -371.5, 25.5, 244.5, 90.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.5));
    }
}
