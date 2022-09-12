package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;

public abstract class VehicleEvent extends Event implements Cancellable {
    private final WastedVehicle vehicle;
    private boolean cancelled;

    protected VehicleEvent(WastedVehicle vehicle) {
        this.vehicle = vehicle;
    }

    public WastedVehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }
}
