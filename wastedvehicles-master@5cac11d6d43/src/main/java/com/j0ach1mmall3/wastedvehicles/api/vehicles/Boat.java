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
 * @since 2/07/2016
 */
public final class Boat extends WastedVehicle {
    public Boat(Main plugin, VehicleProperties vehicleProperties) {
        super(plugin, vehicleProperties);
    }

    @Override
    public void onSteer(ArmorStand armorStand, Player player, SteerDirection steerDirection) {
        Material material = armorStand.getLocation().add(0, this.vehicleProperties.getBoundingBoxHeight(), 0).getBlock().getType();

        if(material == Material.WATER || material == Material.STATIONARY_WATER) {
            double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);

            if(steerDirection.isLeft()) VehicleUtils.setYaw(armorStand, armorStand.getLocation().getYaw() - (float) this.vehicleProperties.getRotationSpeed());
            if(steerDirection.isRight()) VehicleUtils.setYaw(armorStand, armorStand.getLocation().getYaw() + (float) this.vehicleProperties.getRotationSpeed());
            if(steerDirection.isForward() && this.speed < this.vehicleProperties.getMaxSpeed()) this.speed += this.vehicleProperties.getAcceleration() * boost;
            if(steerDirection.isBackward() && this.speed > -this.vehicleProperties.getMaxSpeed() / 2) this.speed -= this.vehicleProperties.getAcceleration() * boost / 2;

            VehicleUtils.makeJumping(armorStand);
            armorStand.setVelocity(armorStand.getEyeLocation().getDirection().multiply(this.speed * boost).setY(0.05));
        }
    }

    @Override
    public void onTick(ArmorStand armorStand) {
        Material material = armorStand.getLocation().add(0, this.vehicleProperties.getBoundingBoxHeight(), 0).getBlock().getType();
        if(material == Material.AIR) armorStand.setVelocity(new Vector(0, -0.5, 0));
        else {
            if(this.speed > 0 && this.speed - this.vehicleProperties.getDeceleration() > 0) this.speed -= this.vehicleProperties.getDeceleration();
            if(this.speed < 0 && this.speed + this.vehicleProperties.getDeceleration() < 0) this.speed += this.vehicleProperties.getDeceleration();

            double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);
            armorStand.setVelocity(armorStand.getEyeLocation().getDirection().multiply(this.speed * boost).setY(0));
        }
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
