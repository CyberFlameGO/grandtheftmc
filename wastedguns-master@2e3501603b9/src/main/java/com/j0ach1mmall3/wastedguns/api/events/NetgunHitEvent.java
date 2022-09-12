package com.j0ach1mmall3.wastedguns.api.events;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NetgunHitEvent extends Event implements Cancellable {

	/** List of handlers for this event. */
	private static final HandlerList HANDLERS = new HandlerList();
	/** Whether or not this event is cancelled */
	private boolean cancelled;
	/** The entity shooting the netgun */
	private final Entity shooter;
	/** The entity getting hit by the netgun */
	private final Entity target;
	/** The location of the hit, where the netgun explodes */
	private final Location location;
	/** The number of ticks to keep the net alive for */
	private int duration;

	/**
	 * Construct a new NetgunHitEvent.
	 * <p>
	 * This should be constructed and fired whenever a netgun hits something.
	 * 
	 * @param shooter - the shooter of the netgun
	 * @param target - the target of the netgun, if one exists
	 * @param location - the location where the net appears
	 * @param duration - the time, in ticks, that the nets live for
	 */
	public NetgunHitEvent(Entity shooter, Entity target, Location location, int duration) {
		this.shooter = shooter;
		this.target = target;
		this.location = location;
		this.duration = duration;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}

	/**
	 * Get the list of handlers for this event.
	 * 
	 * @return The handlers for this event.
	 */
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}

	/**
	 * Get the entity that shot the netgun.
	 * 
	 * @return The entity that shot the netgun.
	 */
	public Entity getShooter() {
		return shooter;
	}

	/**
	 * Get the target of the netgun, if one exists.
	 * 
	 * @return The target of the netgun, if one exists, otherwise {@code empty}.
	 */
	public Optional<Entity> getTarget() {
		if (target != null) {
			return Optional.of(target);
		}

		return Optional.empty();
	}

	/**
	 * Get the epicenter of the location for where the netgun hit.
	 * <p>
	 * This is used as a reference point for where to build the "webs".
	 * </p>
	 * 
	 * @return The location for where the netgun hit.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Get the time, in ticks, that the webs from this netgun live for.
	 * <p>
	 * If the value is 20, that means the netgun's webs will stay on the server
	 * for 20 ticks or 1 second.
	 * </p>
	 * 
	 * @return The time, in ticks that the webs will stay alive for.
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Set the time, in ticks, that the webs from this netgun will live for.
	 * <p>
	 * If the value is 20, that means the netgun's webs will stay on the server
	 * for 20 ticks or 1 second.
	 * </p>
	 * 
	 * @param duration - the time in ticks to live for the web
	 */
	public void setDuration(int duration) {
		this.duration = duration;
	}
}
