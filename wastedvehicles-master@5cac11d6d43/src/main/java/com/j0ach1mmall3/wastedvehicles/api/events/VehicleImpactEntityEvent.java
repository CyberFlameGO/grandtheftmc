package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class VehicleImpactEntityEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player driver;
    private final LivingEntity impactedEntity;
    private final ArmorStand armorStand;
    private final SteerDirection steerDirection;

    public VehicleImpactEntityEvent(WastedVehicle vehicle, Player driver, LivingEntity impactedEntity, ArmorStand armorStand, SteerDirection steerDirection) {
        super(vehicle);
        this.impactedEntity = impactedEntity;
        this.driver = driver;
        this.armorStand = armorStand;
        this.steerDirection = steerDirection;
    }

    public Player getDriver() {
        return this.driver;
    }

    public LivingEntity getImpactedEntity() {
        return this.impactedEntity;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public SteerDirection getSteerDirection() {
        return this.steerDirection;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
