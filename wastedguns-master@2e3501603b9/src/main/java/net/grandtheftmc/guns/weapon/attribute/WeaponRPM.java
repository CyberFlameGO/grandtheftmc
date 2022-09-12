package net.grandtheftmc.guns.weapon.attribute;

/**
 * Created by Luke Bingham on 24/07/2017.
 */
public interface WeaponRPM extends WeaponAttribute {

	/**
	 * Get the amount of revolutions this weapon uses per second.
	 * <p>
	 * Note: Minecraft runs 20 ticks per second, so if this value is 10, that
	 * means 10 bullets are fired per second.
	 * </p>
	 * 
	 * @return The number of revolutions this weapon uses per second.
	 */
	int getRPS();

	/**
	 * Get the amount of revolutions this weapon uses per minute.
	 * <p>
	 * Note: Only increments of 60 effect this value.
	 * </p>
	 * 
	 * @return The number of revolutions this weapon uses per minute.
	 * 
	 * @deprecated - Please use {@link #getRPS()} instead as it's hard to
	 *             calculate the rpm of a gun and assume it's true RPM value.
	 */
	@Deprecated
	int getRpm();

	/**
	 * Get the burst rate of the weapon.
	 * <p>
	 * Note: If the burst rate of the weapon is say 5, then that means it will
	 * attempt to fire it's clip through 5 ticks before stopping.
	 * 
	 * If the burst rate is 20, then that means it will attempt to fire through all 20 ticks before stopping.
	 * </p>
	 * 
	 * @return The burst rate of the weapon, in ticks, before this weapon stops trying to fire.
	 */
	default int getBurstRate(){
		return 5;
	}
}
