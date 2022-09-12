package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

/**
 * Created by Timothy Lampen on 1/4/2018.
 */
public class VehicleShootEvent extends VehicleEvent implements Cancellable{
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ArmorStand armorStand;
    private final Player shooter;


    public VehicleShootEvent(WastedVehicle vehicle, ArmorStand armorStand, Player shooter) {
        super(vehicle);
        this.armorStand = armorStand;
        this.shooter = shooter;
    }

    public Player getShooter() {
        return shooter;
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
