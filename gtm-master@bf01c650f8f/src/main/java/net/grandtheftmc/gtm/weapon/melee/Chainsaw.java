package net.grandtheftmc.gtm.weapon.melee;

import net.grandtheftmc.core.util.AngleUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.weapon.WeaponVisualStatue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;

import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.factory.ItemFactory;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.EulerAngle;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class Chainsaw extends MeleeWeapon implements RankedWeapon, WeaponVisualStatue {

    /**
     * Construct a new Weapon.
     */
    public Chainsaw() {
        super(
        		(short) 5,
                "Chainsaw", //Name
                WeaponType.MELEE, //Weapon Type
                AmmoType.ENERGY, //AmmoType
                new ItemFactory(Material.DIAMOND_SWORD).setDurability((short) 41).build(), //ItemStack
                new Sound[] { //Sounds
                        Sound.ENTITY_WOLF_GROWL,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC,
                        Sound.ITEM_ARMOR_EQUIP_GENERIC
                }
        );

        //This is the OLD itemstack, this isn't needed when creating a new weapon.
        setOldItemStack(new ItemFactory(Material.DIAMOND_BARDING).setName(getName()).build());
        setDescription("A chainsaw?", "You absolute monster!");

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

        this.delay = 5;
        this.meleeDamage = 6.5;
        this.range = 2.5;
    }

    /*@Override
    public void onRightClick(Player player) {
        ServerUtil.debug("20");
        MathUtil.getNearbyEntities(player, this.range).forEach(e -> {
            ServerUtil.debug("21");
            if (player.hasLineOfSight(e)) {
                ServerUtil.debug("they have line of sight.2");
                Vector toEntity = e.getLocation().toVector().subtract(player.getLocation().toVector());
                double dot = toEntity.normalize().dot(player.getLocation().getDirection());
                ServerUtil.debug(dot + " / dot2");
                if (dot <= 1 && dot >= 0) {
                    ServerUtil.debug("yup.2");
                    e.setNoDamageTicks(0);
                    e.damage(getMeleeDamage(), player);
                }
            }
        });
        Sounds.broadcastSound(getSounds()[0], player.getEyeLocation());
    }//todo: moved to the meleeweapon class, don't know why this isn't working.*/

    @Override
    public UserRank requiredRank() {
        return UserRank.PREMIUM;
    }

    @Override
    public Location spawnVisual(World world) {
        Location origin = this.getOrigin(world);
        Location clickLoc = origin.clone().add(0.4, 1.5, -0.4);
        clickLoc.setYaw(-135.0f);
        ArmorStand clickable = spawnEntity(clickLoc, this, WeaponVisualStatue.VisualType.NAME);
        clickable.setSmall(true);

        ArmorStand weapon = spawnEntity(origin.clone().add(0.25, 0.45, -0.65), this, WeaponVisualStatue.VisualType.NONE);
        weapon.setArms(true);
        weapon.setItemInHand(this.createItemStack().clone());
        weapon.setRightArmPose(new EulerAngle(AngleUtil.getRadianFromDegree(280), 0, 0));
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
        return new Location(world, -375.5, 25.5, 243.5, 0.0f, 0.0f);
    }

    @Override
    public void extras(ArmorStand entity) {
        entity.setMetadata("statue_Y", new FixedMetadataValue(GTM.getInstance(), 2.5));
        entity.setMetadata("statue_X", new FixedMetadataValue(GTM.getInstance(), 0.4));
        entity.setMetadata("statue_Z", new FixedMetadataValue(GTM.getInstance(), -0.4));
    }
}
