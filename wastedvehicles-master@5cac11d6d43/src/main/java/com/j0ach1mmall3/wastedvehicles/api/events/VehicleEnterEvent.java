package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class VehicleEnterEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final ArmorStand armorStand;

    public VehicleEnterEvent(WastedVehicle vehicle, Player player, ArmorStand armorStand) {
        super(vehicle);
        this.player = player;
        this.armorStand = armorStand;
    }

    public Player getPlayer() {
        return this.player;
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
