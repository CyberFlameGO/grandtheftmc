package net.grandtheftmc.core.util;

/**
 * This interface is especially useful for storing serialized objects in a map,
 * and acts as a reminder to engineers to mark an object with an id.
 */
public interface Identifiable<T> {

	/**
	 * Get the identification for this object.
	 * 
	 * @return The identification for this object.
	 */
	T getId();
}
