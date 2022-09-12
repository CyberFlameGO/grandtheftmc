package net.grandtheftmc.core.database.mutex;

public abstract class Mutexable implements Lockable {

	/** The lock of this Mutex */
	private boolean lock;

	/**
	 * Construct a new Mutexable which holds a lock.
	 */
	public Mutexable() {
		this.lock = false;
	}

	/**
	 * Get the lock state of this object.
	 * 
	 * @return {@code true} if this object is locked, {@code false} otherwise.
	 */
	@Override
	public synchronized boolean isLocked() {
		return lock;
	}

	/**
	 * Sets the lock state of this object.
	 * 
	 * @param locked - {@code true} if the object is locked, {@code false}
	 *            otherwise.
	 */
	@Override
	public synchronized void setLocked(boolean locked) {
		this.lock = locked;
	}

	/**
	 * Attempt to lock this object.
	 * 
	 * @return {@code true} if the object was successfully locked. {@code false}
	 *         if the object was already locked.
	 */
	@Override
	public synchronized boolean lock() {

		boolean result = false;

		if (!lock) {
			lock = true;
			result = true;
		}

		return result;
	}

	/**
	 * Attempt to unlock this object.
	 * 
	 * @return {@code true} if the object was successfully unlocked.
	 *         {@code false} if the object was already unlocked.
	 */
	@Override
	public synchronized boolean unlock() {
		boolean result = false;

		if (lock) {
			lock = false;
			result = true;
		}

		return result;
	}
}
