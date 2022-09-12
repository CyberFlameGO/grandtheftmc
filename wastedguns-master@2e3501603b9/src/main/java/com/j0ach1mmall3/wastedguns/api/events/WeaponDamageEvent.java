package com.j0ach1mmall3.wastedguns.api.events;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.guns.weapon.Weapon;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 7/05/2016
 */
public final class WeaponDamageEvent extends WeaponEvent {
    private static final HandlerList HANDLER_LIST = new HandlerList();

    /** The damage involved in the event */
    private double damage;
    /** The entity being damaged in the event */
    private final Entity entity;
    /** The cause of the damage in the event */
    private final EntityDamageEvent.DamageCause cause;

    /**
     * Construct a new WeaponDamageEvent.
     * 
     * @param who - the living entity that owns the weapon
     * @param weapon - the weapon in the event
     * @param weaponItemStack - the weapon itemstack in the event
     * @param damage - the damage for the event
     * @param entity - the entity involved in the event that is getting damaged
     * @param cause - the damage cause for the event
     */
    public WeaponDamageEvent(LivingEntity who, Weapon weapon, ItemStack weaponItemStack, double damage, Entity entity, EntityDamageEvent.DamageCause cause) {
        super(who, weapon, weaponItemStack);
        this.damage = damage;
        this.entity = entity;
        this.cause = cause;
    }
    
    /**
     * Construct a new WeaponDamageEvent.
     * 
     * @param who - the living entity that owns the weapon
     * @param weapon - the weapon in the event
     * @param damage - the damage for the event
     * @param entity - the entity involved in the event that is getting damaged
     * @param cause - the damage cause for the event
     */
    public WeaponDamageEvent(LivingEntity who, Weapon weapon, double damage, Entity entity, EntityDamageEvent.DamageCause cause) {
    	this(who, weapon, null, damage, entity, cause);
    }

    /**
     * Get the amount of damage involved in the event.
     * 
     * @return The amount of damage involved in the event.
     */
    public double getDamage() {
        return this.damage;
    }

    /**
     * Set the amount of damage involved in the event.
     * 
     * @param damage - the amount of damage in the event
     */
    public void setDamage(double damage) {
        this.damage = damage;
    }

    /**
     * Get the entity involved in the event, that is being damaged.
     * 
     * @return The entity being damaged.
     */
    public Entity getEntity() {
        return this.entity;
    }

    /**
     * Get the damage cause for the event.
     * 
     * @return The damage cause for the event.
     */
    public EntityDamageEvent.DamageCause getCause() {
        return this.cause;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }
}
