package com.j0ach1mmall3.wastedvehicles.api.vehicles;

import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;
import com.j0ach1mmall3.wastedvehicles.util.VehicleUtils;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 17/05/2016
 */
public final class Plane extends WastedVehicle {
    public Plane(Main plugin, VehicleProperties vehicleProperties) {
        super(plugin, vehicleProperties);
    }

    @Override
    public void onSteer(ArmorStand armorStand, Player player, SteerDirection steerDirection) {
        double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);

        VehicleUtils.setYaw(armorStand, player.getEyeLocation().getYaw());
        VehicleUtils.setPitch(armorStand, player.getEyeLocation().getPitch());
        armorStand.setHeadPose(new EulerAngle(Math.toRadians(player.getEyeLocation().getPitch()), 0, 0));

        if(steerDirection.isForward() && this.speed < this.vehicleProperties.getMaxSpeed()) this.speed += this.vehicleProperties.getAcceleration() * boost;
        if(steerDirection.isBackward() && this.speed > -this.vehicleProperties.getMaxSpeed() / 2) this.speed -= this.vehicleProperties.getAcceleration() * boost / 2;

        VehicleUtils.makeJumping(armorStand);
    }

    @Override
    public void onTick(ArmorStand armorStand) {
        if(armorStand.getPassenger() == null) {
            if(this.speed > 0 && this.speed - this.vehicleProperties.getDeceleration() > 0) this.speed -= this.vehicleProperties.getDeceleration();
            if(this.speed < 0 && this.speed + this.vehicleProperties.getDeceleration() < 0) this.speed += this.vehicleProperties.getDeceleration();
        }

        double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);
        Vector direction = armorStand.getEyeLocation().getDirection();
        if(this.speed < this.vehicleProperties.getTakeOffSpeed() && this.speed > -this.vehicleProperties.getTakeOffSpeed()) armorStand.setVelocity(direction.multiply(this.speed * boost).setY(4 * MINECRAFT_GRAVITY));
        else armorStand.setVelocity(direction.multiply(this.speed * boost));
        armorStand.setVelocity(direction.multiply(this.speed * boost));
        if(armorStand.getLocation().getBlock().getType() == Material.WATER || armorStand.getLocation().getBlock().getType() == Material.STATIONARY_WATER) this.explode(armorStand);
    }

    @Override
    public Vector getWeaponDirection(ArmorStand armorStand, Player player) {
        return player.getEyeLocation().getDirection();
    }

    @Override
    public List<ArmorStand> getPassengers() {
        return this.passengers;
    }
}