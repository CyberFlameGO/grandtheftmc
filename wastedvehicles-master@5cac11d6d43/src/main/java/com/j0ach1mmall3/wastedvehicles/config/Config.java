package com.j0ach1mmall3.wastedvehicles.config;

import com.j0ach1mmall3.jlib.storage.file.yaml.ConfigLoader;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.SpeedBoost;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.api.VehicleType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class Config extends ConfigLoader<Main> {
    private final ItemStack jetpackItem;
    private final boolean jetpackFuelEnabled;
    private final ItemStack jetpackFuelItem;
    private final int jetpackFuelInterval;
    private final List<String> jetpackAllowedWeapons;
    private final List<VehicleProperties> vehicleProperties;

    public Config(Main plugin) {
        super("config.yml", plugin);
        this.jetpackItem = this.customConfig.getItemNew(this.config, "Jetpack.Item");
        this.jetpackFuelEnabled = this.config.getBoolean("Jetpack.Fuel.Enabled");
//        this.jetpackFuelItem = this.customConfig.getItemNew(this.config, "Jetpack.Fuel.Item");
        this.jetpackFuelItem = this.customConfig.getItemNew(this.config, "Jetpack.Fuel.Item");
        this.jetpackFuelInterval = this.config.getInt("Jetpack.Fuel.Interval");
        this.jetpackAllowedWeapons = this.config.getStringList("Jetpack.AllowedWeapons");
        this.vehicleProperties = this.loadVehicleProperties();
    }

    private List<VehicleProperties> loadVehicleProperties() {
        List<VehicleProperties> vehicleProperties = new ArrayList<>();
        this.customConfig.getKeys("Vehicles").forEach(s -> vehicleProperties.add(new VehicleProperties(
                s,
                VehicleType.valueOf(this.config.getString("Vehicles." + s + ".VehicleType").toUpperCase()),
                this.config.getDouble("Vehicles." + s + ".Acceleration"),
                this.config.getDouble("Vehicles." + s + ".Deceleration"),
                this.config.getDouble("Vehicles." + s + ".TakeOffSpeed"),
                this.config.getDouble("Vehicles." + s + ".RotationSpeed"),
                this.config.getBoolean("Vehicles." + s + ".Invisible"),
                this.customConfig.getItemNew(this.config, "Vehicles." + s + ".Item"),
                (float) this.config.getDouble("Vehicles." + s + ".BoundingBoxWidth"),
                (float) this.config.getDouble("Vehicles." + s + ".BoundingBoxHeight"),
                this.config.getDouble("Vehicles." + s + ".LaunchLocationMultiplier"),
                this.config.getDouble("Vehicles." + s + ".LaunchLocationYOffset"),
                this.config.getDouble("Vehicles." + s + ".MaxHealth"),
                this.config.getBoolean("Vehicles." + s + ".Explode"),
                this.config.getBoolean("Vehicles." + s + ".DamageVehicleOnPlayerHit"),
                this.config.getInt("Vehicles." + s + ".KnockOutChance"),
                this.config.getInt("Vehicles." + s + ".Passengers"),
                this.loadSpeedBoosts("Vehicles." + s + ".SpeedBoosts"),
                this.config.getDouble("Vehicles." + s + ".MaxSpeed"),
                this.config.getDouble("Vehicles." + s + ".DefaultSpeed"),
                this.config.getDouble("Vehicles." + s + ".CollisionDamage"),
                this.config.getString("Vehicles." + s + ".WastedGunsWeapon"),
                this.config.getStringList("Vehicles." + s + ".AllowedWeapons"),
                this.config.getStringList("Vehicles." + s + ".AllowedBlocks"),
                this.config.getStringList("Vehicles." + s + ".DisplayBlocks")
                )
        ));
        return vehicleProperties;
    }

    private Set<SpeedBoost> loadSpeedBoosts(String path) {
        Set<SpeedBoost> speedBoosts = new HashSet<>();
        this.customConfig.getKeys(path).forEach(s -> speedBoosts.add(new SpeedBoost(this.customConfig.getItemNew(this.config, path + ".Item"), this.config.getDouble(path + ".SpeedBoost"), this.config.getInt(path + ".Duration"))));
        return speedBoosts;
    }

    public ItemStack getJetpackItem() {
        return this.jetpackItem;
    }

    public boolean isJetpackFuelEnabled() {
        return this.jetpackFuelEnabled;
    }

    public ItemStack getJetpackFuelItem() {
        return this.jetpackFuelItem;
    }

    public int getJetpackFuelInterval() {
        return this.jetpackFuelInterval;
    }

    public List<String> getJetpackAllowedWeapons() {
        return this.jetpackAllowedWeapons;
    }

    public List<VehicleProperties> getVehicleProperties() {
        return this.vehicleProperties;
    }
}
