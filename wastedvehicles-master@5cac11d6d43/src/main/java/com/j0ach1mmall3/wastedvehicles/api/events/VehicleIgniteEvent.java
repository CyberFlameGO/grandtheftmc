package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.HandlerList;

public final class VehicleIgniteEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ArmorStand armorStand;

    public VehicleIgniteEvent(WastedVehicle vehicle, ArmorStand armorStand) {
        super(vehicle);
        this.armorStand = armorStand;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
