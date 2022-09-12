package net.grandtheftmc.core.database.mutex;

public interface Lockable {

	/**
	 * Get the lock state of this object.
	 * 
	 * @return {@code true} if this object is locked, {@code false} otherwise.
	 */
	boolean isLocked();

	/**
	 * Sets the lock state of this object.
	 * 
	 * @param locked - {@code true} if the object is locked, {@code false}
	 *            otherwise.
	 */
	void setLocked(boolean locked);

	/**
	 * Attempt to lock this object.
	 * 
	 * @return {@code true} if the object was successfully locked. {@code false}
	 *         if the object was already locked.
	 */
	boolean lock();

	/**
	 * Attempt to unlock this object.
	 * 
	 * @return {@code true} if the object was successfully unlocked.
	 *         {@code false} if the object was already unlocked.
	 */
	boolean unlock();

}
