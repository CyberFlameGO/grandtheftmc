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

public final class Helicopter extends WastedVehicle {
    public Helicopter(Main plugin, VehicleProperties vehicleProperties) {
        super(plugin, vehicleProperties);
    }

    @Override
    public void onSteer(ArmorStand armorStand, Player player, SteerDirection steerDirection) {
        double boost = this.speedBoosts.isEmpty() ? 1 : this.speedBoosts.get(0);

        VehicleUtils.setYaw(armorStand, player.getEyeLocation().getYaw());

        Vector velocity = new Vector(0, 0, 0);
        if(steerDirection.getForMotion() != 0.0) velocity.add(player.getEyeLocation().getDirection().setY(0).multiply(steerDirection.getForMotion()).multiply(this.speed * boost));
        if(steerDirection.getSideMotion() != 0.0) velocity.setY(-0.5 * steerDirection.getSideMotion());
        armorStand.setVelocity(velocity);
    }

    @Override
    public void onTick(ArmorStand armorStand) {
        if(armorStand.getLocation().getBlock().getType() == Material.WATER || armorStand.getLocation().getBlock().getType() == Material.STATIONARY_WATER) this.explode(armorStand);
    }

    @Override
    public Vector getWeaponDirection(ArmorStand armorStand, Player player) {
        return armorStand.getEyeLocation().getDirection().setY(-Math.PI / 4);
    }

    @Override
    public List<ArmorStand> getPassengers() {
        return this.passengers;
    }
}
