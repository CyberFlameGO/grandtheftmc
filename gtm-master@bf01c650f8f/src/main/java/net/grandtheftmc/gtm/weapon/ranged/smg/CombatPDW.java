package net.grandtheftmc.gtm.weapon.ranged.smg;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
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
import net.grandtheftmc.guns.weapon.ranged.guns.SMGWeapon;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public class CombatPDW extends SMGWeapon implements WeaponVisualStatue {

    /**
     * Construct a new RangedWeapon.
     */
    public CombatPDW() {
        super(
        		(short) 14,
                "Combat PDW", //Name
                WeaponType.SMG, //Weapon Type
                AmmoType.SMG, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 131).build(), //ItemStack
                new Sound[] { //Gun Sounds
                        Sound.ENTITY_BLAZE_HURT,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.ITEM_ARMOR_EQUIP_GOLD,
                        Sound.BLOCK_WOODEN_DOOR_OPEN,
                },
                Effect.FLYING_GLYPH //Particles
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.IRON_AXE).setName(getName()).build());
        setDescription("It's a defensive weapon.", "With a supressor.", "Thanks, Congress!");

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

        this.damage = 2.1; //RangedWeapon
        this.meleeDamage = 4.0; //RangedWeapon
        this.accuracy = 0.025; //RangedWeapon
        this.magSize = 30; //RangedWeapon
        this.reloadTime = 40; //RangedWeapon
        this.range = 40; //RangedWeapon
        this.recoil = 0.0; //RangedWeapon
        this.zoom = 4; //RangedWeapon

        this.rpm = 450; //AssultRifleWeapon
        this.rps = 7;
        this.multiShoot = true;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);
        ArmorStand clickable = spawnEntity(origin.clone().add(0, 1.5, 0), this, WeaponVisualStatue.VisualType.NAME);
        clickable.setSmall(true);

        ArmorStand weapon = spawnEntity(origin.clone().add(0.2, -0.15, -0.38), this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(180), 0, AngleUtil.getRadianFromDegree(20)));
        weapon.setMarker(true);

        Location hookLoc = origin.clone().add(0.045, 1.07, -0.7);
        hookLoc.setPitch(0);
        hookLoc.setYaw(0);
        ArmorStand hook = spawnEntity(hookLoc, this, WeaponVisualStatue.VisualType.NONE);
        hook.setHelmet(new ItemStack(Material.TRIPWIRE_HOOK));
        hook.setHeadPose(new EulerAngle(AngleUtil.getRadianFromDegree(45), 0, 0));
        hook.setSmall(true);
        hook.setMarker(true);

        return origin;
    }

    @Override
    public Location getOrigin(World world) {
        return new Location(world, -364.5, 25.5, 227.5, -90.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.5));
    }
}
