package net.grandtheftmc.core.database.mutex.task;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.database.mutex.event.AsyncMutexLoadEvent;
import net.grandtheftmc.core.database.mutex.event.MutexLoadCompleteEvent;
import net.grandtheftmc.core.task.ExpirableTask;
import net.grandtheftmc.core.util.debug.Log;

public abstract class LoadMutexTask extends ExpirableTask {

	/** The Mutexable object being manipulated */
	private Mutexable mutexable;
	/** Whether or not we've requested to load */
	private boolean request;

	/**
	 * Construct a new LoadMutexTask, which loads a mutexable after grabbing the
	 * lock.
	 * <p>
	 * This is a wrapper around
	 * {@link #LoadPlayerTask(Plugin, Mutexable, double, double, double)}, with
	 * a delay of 0.2 secs, an interval of 0.25 secs, and a length of 1.5 secs.
	 * <p>
	 * Because there is a delay, after the delay is over, the task will execute
	 * as a 'starter' onInterval.
	 * 
	 * @param plugin - the owning plugin
	 * @param mutexable - the mutexable to load
	 */
	public LoadMutexTask(Plugin plugin, Mutexable mutexable) {
		this(plugin, mutexable, 0.2, 0.25, 1.5);
	}

	/**
	 * Construct a new LoadMutexTask, which loads a mutexable after grabbing the
	 * lock.
	 * 
	 * @param plugin - the owning plugin
	 * @param mutexable - the mutexable to load
	 * @param delay - the delay, in seconds, before trying to execute the task
	 * @param interval - the interval, in seconds, of repeating tries
	 * @param length - the length, in seconds, of the task
	 */
	public LoadMutexTask(Plugin plugin, Mutexable mutexable, double delay, double interval, double length) {
		super(plugin, delay, interval, length);
		Validate.notNull(mutexable);
		this.mutexable = mutexable;
	}

	/**
	 * Call-back for when the mutex of this task is available.
	 * <p>
	 * Note: If the loading was successful, this method will automatically
	 * update the mutex state in the database.
	 * 
	 * @return {@code true} if the load was successful, {@code false} otherwise.
	 */
	protected abstract boolean onLoad();

	/**
	 * Optional call-back for when loading is complete.
	 */
	protected void onLoadComplete() {
	};

	/**
	 * Call-back for when the loading fails.
	 */
	protected abstract void onLoadFailure();

	/**
	 * Fetches the mutex from the database.
	 * 
	 * @return {@code true} if this mutex is taken by something else,
	 *         {@code false} if the mutex is free.
	 */
	protected abstract boolean fetchMutex();

	/**
	 * Syncs the Mutex with the database.
	 */
	public abstract void syncMutex();

	@Override
	protected void onInterval(double timeLeft) {

		try {
			handleMutex();
		}
		catch (Exception e) {
			Log.info("Core", "Exception in onInterval() of LoadMutexTask().");
			e.printStackTrace();
		}
	}

	@Override
	protected void onDelayComplete() {

		// when delay is over, handle mutex
		onInterval(getTimeLeft());
	}

	/**
	 * Call to {@link #onLoadFailure()}.
	 */
	@Override
	protected void onExpire() {
		onLoadFailure();
	}

	/**
	 * Handles the mutex interaction.
	 */
	private void handleMutex() {

		// TODO remove
		long start = System.currentTimeMillis();

		// fetch mutex from database
		boolean mutex = fetchMutex();

		// if not locked
		if (!mutex) {

			// if we haven't sent a load request
			if (!request) {
				request = true;

				// cancel this task
				cancel();

				// TODO remove
				Log.info("TEST-LoadMutexTask", "Mutex is available (" + start + ") for " + mutexable.toString() + " (" + (System.currentTimeMillis() - start) + " msecs)");

				boolean result = onLoad();
				// TODO remove
				Log.info("TEST-LoadMutexTask", "Did onLoad complete for " + mutexable.toString() + "? " + result + " (" + (System.currentTimeMillis() - start) + " msecs)");
				if (result) {

					// lock and sync to db
					mutexable.lock();
					syncMutex();
					
					// call bukkit event to say it was loaded
					Bukkit.getPluginManager().callEvent(new AsyncMutexLoadEvent(mutexable));

					// optional call-back
					onLoadComplete();
					
					// schedule a sync load complete event
					Bukkit.getScheduler().runTask(getPlugin(), () -> {
						Bukkit.getPluginManager().callEvent(new MutexLoadCompleteEvent(mutexable));
					});
				}
				else {
					onLoadFailure();
				}
			}
		}
		else {
			// TODO remove
			Log.info("TEST-LoadMutexTask", "Mutex is locked (" + start + "), will retry for " + mutexable.toString() + " (" + (System.currentTimeMillis() - start) + " msecs)");
		}
	}
}

