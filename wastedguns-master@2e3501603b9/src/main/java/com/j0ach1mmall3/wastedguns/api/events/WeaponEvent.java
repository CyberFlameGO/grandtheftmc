package com.j0ach1mmall3.wastedguns.api.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.guns.weapon.Weapon;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public abstract class WeaponEvent extends Event implements Cancellable {
	/** The weapon object involved in the event */
    protected final Weapon<?> weapon;
    /** ItemStack representation for the weapon */
    protected final ItemStack weaponItemStack;
    /** The living entity involved in the event */
    protected final LivingEntity livingEntity;
    protected boolean cancelled;

    /**
     * Construct a new WeaponEvent.
     * 
     * @param who - the owner of the weapon
     * @param weapon - the weapon involved
     * @param weaponItemStack - the weapon itemstack involved
     */
    protected WeaponEvent(LivingEntity who, Weapon<?> weapon, ItemStack weaponItemStack) {
        this.livingEntity = who;
        this.weapon = weapon;
        this.weaponItemStack = weaponItemStack;
    }
    
    /**
     * Construct a new WeaponEvent.
     * 
     * @param who - the owner of the weapon
     * @param weapon - the weapon involved
     */
    protected WeaponEvent(LivingEntity who, Weapon<?> weapon) {
        this(who, weapon, null);
    }

    /**
     * Get the living entity involved in the event.
     * <p>
     * This is owner of the weapon.
     * </p>
     * 
     * @return The living entity involved in this event.
     */
    public final LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    /**
     * Get the weapon involved in this event.
     * 
     * @return The weapon involved in this event.
     */
    public final Weapon<?> getWeapon() {
        return this.weapon;
    }
    
    /**
     * Get the weapon itemstack involved in this event.
     * 
     * @return The weapon itemstack involved in this event.
     */
    public final ItemStack getWeaponItemStack(){
    	return this.weaponItemStack;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }
}
