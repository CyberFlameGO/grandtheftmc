package com.j0ach1mmall3.wastedvehicles.api;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 15/05/2016
 */
public final class VehicleProperties {
    private final String identifier;
    private final VehicleType vehicleType;
    private final double acceleration;
    private final double deceleration;
    private final double takeOffSpeed;
    private final double rotationSpeed;
    private final boolean invisible;
    private final ItemStack item;
    private final float boundingBoxWidth;
    private final float boundingBoxHeight;
    private final double launchLocationMultiplier;
    private final double launchLocationYOffset;
    private final double maxHealth;
    private final boolean explode;
    private final boolean damageVehicleOnPlayerHit;
    private final int knockOutChance;
    private final int passengers;
    private final Set<SpeedBoost> speedBoosts;
    private final double maxSpeed;
    private final double defaultSpeed;
    private final double collisionDamage;
    private final String wastedGunsWeapon;
    private final List<String> allowedWeapons;
    private final List<String> allowedBlocks;
    private final List<String> displayBlocks;

    public VehicleProperties(String identifier, VehicleType vehicleType, double acceleration, double deceleration, double takeOffSpeed, double rotationSpeed, boolean invisible, ItemStack item, float boundingBoxWidth, float boundingBoxHeight, double launchLocationMultiplier, double launchLocationYOffset, double maxHealth, boolean explode, boolean damageVehicleOnPlayerHit, int knockOutChance, int passengers, Set<SpeedBoost> speedBoosts, double maxSpeed, double defaultSpeed, double collisionDamage, String wastedGunsWeapon, List<String> allowedWeapons, List<String> allowedBlocks, List<String> displayBlocks) {
        this.identifier = identifier;
        this.vehicleType = vehicleType;
        this.acceleration = acceleration;
        this.deceleration = deceleration;
        this.takeOffSpeed = takeOffSpeed;
        this.rotationSpeed = rotationSpeed;
        this.invisible = invisible;
        this.item = item;
        this.boundingBoxWidth = boundingBoxWidth;
        this.boundingBoxHeight = boundingBoxHeight;
        this.launchLocationMultiplier = launchLocationMultiplier;
        this.launchLocationYOffset = launchLocationYOffset;
        this.maxHealth = maxHealth;
        this.explode = explode;
        this.damageVehicleOnPlayerHit = damageVehicleOnPlayerHit;
        this.knockOutChance = knockOutChance;
        this.passengers = passengers;
        this.speedBoosts = speedBoosts;
        this.maxSpeed = maxSpeed;
        this.defaultSpeed = defaultSpeed;
        this.collisionDamage = collisionDamage;
        this.wastedGunsWeapon = wastedGunsWeapon;
        this.allowedWeapons = allowedWeapons;
        this.allowedBlocks = allowedBlocks;
        this.displayBlocks = displayBlocks;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public VehicleType getVehicleType() {
        return this.vehicleType;
    }

    public double getAcceleration() {
        return this.acceleration;
    }

    public double getDeceleration() {
        return this.deceleration;
    }

    public double getTakeOffSpeed() {
        return this.takeOffSpeed;
    }

    public double getRotationSpeed() {
        return this.rotationSpeed;
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public ItemStack getItem() {
        return this.item;
    }

    public float getBoundingBoxWidth() {
        return this.boundingBoxWidth;
    }

    public float getBoundingBoxHeight() {
        return this.boundingBoxHeight;
    }

    public double getLaunchLocationMultiplier() {
        return this.launchLocationMultiplier;
    }

    public double getLaunchLocationYOffset() {
        return this.launchLocationYOffset;
    }

    public double getMaxHealth() {
        return this.maxHealth;
    }

    public boolean isExplode() {
        return this.explode;
    }

    public boolean isDamageVehicleOnPlayerHit() {
        return this.damageVehicleOnPlayerHit;
    }

    public int getKnockOutChance() {
        return this.knockOutChance;
    }

    public int getPassengers() {
        return this.passengers;
    }

    public Set<SpeedBoost> getSpeedBoosts() {
        return this.speedBoosts;
    }

    public double getMaxSpeed() {
        return this.maxSpeed;
    }

    public double getDefaultSpeed() {
        return this.defaultSpeed;
    }

    public double getCollisionDamage() {
        return this.collisionDamage;
    }

    public String getWastedGunsWeapon() {
        return this.wastedGunsWeapon;
    }

    public List<String> getAllowedWeapons() {
        return this.allowedWeapons;
    }

    public List<String> getAllowedBlocks() {
        return this.allowedBlocks;
    }

    public List<String> getDisplayBlocks() {
        return this.displayBlocks;
    }
}
