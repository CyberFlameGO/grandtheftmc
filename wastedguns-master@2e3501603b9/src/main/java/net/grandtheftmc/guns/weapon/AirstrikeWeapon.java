package net.grandtheftmc.guns.weapon;

import org.bukkit.Sound;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class AirstrikeWeapon extends Weapon<AirstrikeWeapon> {

//    protected final boolean useBeacon = false;
//    protected final Effect beaconParticle = null, explosionParticle = null;
//    protected int beaconRadius = 3, circleParticles = 60, beaconLineParticles = 13, updateTicks = 5, rotationDegrees = 1, targetLength = 5;
//    protected double damage = 0.0, explosionSize = 5.0, explosionStrength = 2.0;
//    protected ItemStack bombItemStack, hydraItemStack;

    /**
     * Construct a new Weapon.
     */
    public AirstrikeWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds);
    }

    @Override
    public String[] getStatsBar() {
        return new String[] {"null"};
    }

    @Override
    public AirstrikeWeapon clone() {
        return null;
    }

//    /**
//     * Get the particle used for the beacon visuals.
//     *
//     * @return particle type
//     */
//    public Effect getBeaconParticle() {
//        return beaconParticle;
//    }
//
//    /**
//     * Get the particles used to visualise the explosion.
//     *
//     * @return particle type
//     */
//    public Effect getExplosionParticle() {
//        return explosionParticle;
//    }
//
//    /**
//     * Check if the airstrike weapon is using beacon.
//     *
//     * @return beacon status
//     */
//    public boolean isUsingBeacon() {
//        return useBeacon;
//    }
//
//    public int getBeaconRadius() {
//        return beaconRadius;
//    }
//
//    public int getCircleParticles() {
//        return circleParticles;
//    }
//
//    public int getBeaconLineParticles() {
//        return beaconLineParticles;
//    }
//
//    public int getUpdateTicks() {
//        return updateTicks;
//    }
//
//    public int getRotationDegrees() {
//        return rotationDegrees;
//    }
//
//    public int getTargetLength() {
//        return targetLength;
//    }
//
//    public double getDamage() {
//        return damage;
//    }
//
//    public double getExplosionSize() {
//        return explosionSize;
//    }
//
//    public double getExplosionStrength() {
//        return explosionStrength;
//    }
//
//    /**
//     * Get the Bomb Itemstack.
//     *
//     * @return bomb item
//     */
//    public ItemStack getBombItemStack() {
//        return bombItemStack;
////        ItemStack itemStack = new ItemStack(Material.STONE);
////        ItemMeta itemMeta = itemStack.getItemMeta();
////        itemMeta.setDisplayName(Utils.f("&6Grenade"));
////        itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
////        itemStack.setItemMeta(itemMeta);
////        return itemStack;
//    }
//
//    /**
//     * Get the Hydra Itemstack.
//     *
//     * @return hydra item
//     */
//    public ItemStack getHydraItemStack() {
//        return hydraItemStack;
////        ItemStack itemStack = new ItemStack(Material.QUARTZ_ORE);
////        ItemMeta itemMeta = itemStack.getItemMeta();
////        itemMeta.setDisplayName(Utils.f("&4&lHydra"));
////        itemMeta.setLore(Collections.singletonList(Utils.f("&7Type: &a&lPlane")));
////        itemStack.setItemMeta(itemMeta);
////        return itemStack;
//    }
//
//    /**
//     * Get the fire delay for the airstrike.
//     *
//     * @return fire delay
//     */
//    public int getFireDelay() {
//        return 20;
//    }
}
