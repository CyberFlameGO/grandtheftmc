package com.j0ach1mmall3.wastedvehicles.api.events;

import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

public final class VehicleDamageEvent extends VehicleEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final ArmorStand armorStand;
    private final EntityDamageEvent.DamageCause cause;
    private double damage;

    public VehicleDamageEvent(WastedVehicle vehicle, ArmorStand armorStand, EntityDamageEvent.DamageCause cause, double damage) {
        super(vehicle);
        this.armorStand = armorStand;
        this.cause = cause;
        this.damage = damage;
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public EntityDamageEvent.DamageCause getCause() {
        return this.cause;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
