package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class VehicleImpactVehicleEvent  extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player driver;
    private final WastedVehicle collidedWith;
    private final Player collidedWithDriver;
    private final ArmorStand armorStand;
    private final SteerDirection steerDirection;
    private final ArmorStand collidedStand;

    public VehicleImpactVehicleEvent(WastedVehicle vehicle, Player driver, WastedVehicle collidedWith, Player collidedWithDriver, ArmorStand armorStand, ArmorStand collidedStand, SteerDirection steerDirection) {
        super(vehicle);
        this.driver = driver;
        this.collidedWith = collidedWith;
        this.collidedWithDriver = collidedWithDriver;
        this.armorStand = armorStand;
        this.steerDirection = steerDirection;
        this.collidedStand = collidedStand;
    }

    public Player getDriver() {
        return this.driver;
    }

    public WastedVehicle getCollidedWith() {
        return this.collidedWith;
    }

    public Player getCollidedWithDriver() {
        return this.collidedWithDriver;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public SteerDirection getSteerDirection() {
        return this.steerDirection;
    }

    public ArmorStand getCollidedStand() {
        return this.collidedStand;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
