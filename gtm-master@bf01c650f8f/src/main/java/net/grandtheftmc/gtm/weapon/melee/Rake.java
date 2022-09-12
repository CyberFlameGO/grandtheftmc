package net.grandtheftmc.gtm.weapon.melee;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class Rake extends MeleeWeapon implements WeaponVisualStatue {

    /**
     * Construct a new Weapon.
     */
    public Rake() {
        super(
        		(short) 1,
                "Rake", //Name
                WeaponType.MELEE, //Weapon Type
                AmmoType.MELEE, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 1).build(), //ItemStack
                new Sound[] { //Sounds
                        Sound.ENTITY_PLAYER_ATTACK_NODAMAGE,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.STICK).setName(getName()).build());
        setDescription("This...isn't even a", "proper weapon.");

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

        this.delay = 6;
        this.meleeDamage = 4;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);
        Location clickLoc = origin.clone().add(-0.4, 1.5, -0.4);
        clickLoc.setYaw(-45.0f);
        ArmorStand clickable = spawnEntity(clickLoc, this, WeaponVisualStatue.VisualType.NAME);
        clickable.setSmall(true);

        ArmorStand weapon = spawnEntity(origin.clone().add(-0.225, 1.1, -0.26), this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(260), 0, AngleUtil.getRadianFromDegree(180)));
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
        return new Location(world, -360.5, 25.5, 238.5, -90.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.5));
        entity.setMetadata("statue_X", new FixedMetadataValue(GTM.getInstance(), -0.4));
        entity.setMetadata("statue_Z", new FixedMetadataValue(GTM.getInstance(), -0.4));
    }
}
