package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class VehicleCreateEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;

    public VehicleCreateEvent(WastedVehicle vehicle, Player player) {
        super(vehicle);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
