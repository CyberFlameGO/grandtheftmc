package com.j0ach1mmall3.wastedvehicles.api.vehicles;

import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;
import com.j0ach1mmall3.wastedvehicles.util.VehicleUtils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 17/05/2016
 */
public final class Car extends WastedVehicle {
    public Car(Main plugin, VehicleProperties vehicleProperties) {
        super(plugin, vehicleProperties);
    }

    @Override
    public void onSteer(ArmorStand armorStand, Player player, SteerDirection steerDirection) {
        double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);
        if(steerDirection.isLeft()) VehicleUtils.setYaw(armorStand, armorStand.getEyeLocation().getYaw() - (float) this.vehicleProperties.getRotationSpeed());
        if(steerDirection.isRight()) VehicleUtils.setYaw(armorStand, armorStand.getEyeLocation().getYaw() + (float) this.vehicleProperties.getRotationSpeed());
        if(steerDirection.isForward() && this.speed < this.vehicleProperties.getMaxSpeed()) this.speed += this.vehicleProperties.getAcceleration() * boost;
        if(steerDirection.isBackward() && this.speed > -this.vehicleProperties.getMaxSpeed() / 2) this.speed -= this.vehicleProperties.getAcceleration() * boost / 2;

        VehicleUtils.makeJumping(armorStand);

        for(ArmorStand passenger : this.passengers) {
            VehicleUtils.makeJumping(passenger);
        }
    }

    @Override
    public void onTick(ArmorStand armorStand) {
        if(armorStand.hasMetadata("WastedVehiclePassenger")) return;
        armorStand.setFireTicks(0);
        if(this.speed > 0 && this.speed - this.vehicleProperties.getDeceleration() > 0) this.speed -= this.vehicleProperties.getDeceleration();
        if(this.speed < 0 && this.speed + this.vehicleProperties.getDeceleration() < 0) this.speed += this.vehicleProperties.getDeceleration();

        double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);
        armorStand.setVelocity(armorStand.getEyeLocation().getDirection().multiply(this.speed * boost).setY(MINECRAFT_GRAVITY));
        this.passengers.forEach(passenger -> {
            VehicleUtils.teleport(passenger, armorStand.getLocation().add(0.75, -0.20, 0));
            passenger.setFireTicks(0);
            VehicleUtils.setYaw(passenger, armorStand.getEyeLocation().getYaw());
        });
        if(armorStand.getLocation().getBlock().getType() == Material.WATER || armorStand.getLocation().getBlock().getType() == Material.STATIONARY_WATER) this.explode(armorStand);
    }

    @Override
    public Vector getWeaponDirection(ArmorStand armorStand, Player player) {
        return armorStand.getEyeLocation().getDirection().setY(0);
    }

    @Override
    public List<ArmorStand> getPassengers() {
        return this.passengers;
    }
}