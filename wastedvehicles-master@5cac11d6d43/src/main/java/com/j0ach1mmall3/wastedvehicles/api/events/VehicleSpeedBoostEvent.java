package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.SpeedBoost;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public final class VehicleSpeedBoostEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final Player player;
    private final ArmorStand armorStand;
    private SpeedBoost speedBoost;

    public VehicleSpeedBoostEvent(WastedVehicle vehicle, Player player, ArmorStand armorStand, SpeedBoost speedBoost) {
        super(vehicle);
        this.player = player;
        this.armorStand = armorStand;
        this.speedBoost = speedBoost;
    }

    public Player getPlayer() {
        return this.player;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public SpeedBoost getSpeedBoost() {
        return this.speedBoost;
    }

    public void setSpeedBoost(SpeedBoost speedBoost) {
        this.speedBoost = speedBoost;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
