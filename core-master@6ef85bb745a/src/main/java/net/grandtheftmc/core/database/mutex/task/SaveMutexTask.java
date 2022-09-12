package net.grandtheftmc.core.database.mutex.task;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.database.mutex.event.AsyncMutexSaveEvent;
import net.grandtheftmc.core.database.mutex.event.MutexSaveCompleteEvent;
import net.grandtheftmc.core.task.ExpirableTask;
import net.grandtheftmc.core.util.debug.Log;

public abstract class SaveMutexTask extends ExpirableTask {

	/** The Mutexable object being manipulated */
	private Mutexable mutexable;
	/** Whether or not we've requested to save */
	private boolean request;

	/**
	 * Construct a new SaveMutexTask, which saves a mutexable.
	 * <p>
	 * This is a wrapper around
	 * {@link #SavePlayerTask(Plugin, Mutexable, double, double, double)}, with
	 * a delay of 0.05 secs, an interval of 0.25 secs, and a length of 1 sec.
	 * <p>
	 * Because there is a delay, after the delay is over, the task will execute
	 * as a 'starter' onInterval.
	 * 
	 * 
	 * @param plugin - the owning plugin
	 * @param mutexable - the mutexable to save
	 */
	public SaveMutexTask(Plugin plugin, Mutexable mutexable) {
		this(plugin, mutexable, 0.05, 0.25, 1);
	}

	/**
	 * Construct a new SaveMutexTask, which saves a mutexable.
	 * 
	 * @param plugin - the owning plugin
	 * @param mutexable - the mutexable to load
	 * @param delay - the delay, in seconds, before trying to execute the task
	 * @param interval - the interval, in seconds, of repeating tries
	 * @param length - the length, in seconds, of the task
	 */
	public SaveMutexTask(Plugin plugin, Mutexable mutexable, double delay, double interval, double length) {
		super(plugin, delay, interval, length);
		Validate.notNull(mutexable);
		this.mutexable = mutexable;
	}

	/**
	 * Call-back for when the mutex of this task is available.
	 * <p>
	 * Note: If the savings was successful, this method will automatically
	 * update the mutex state in the database.
	 * 
	 * @return {@code true} if the save was successful, {@code false} otherwise.
	 */
	protected abstract boolean onSave();

	/**
	 * Optional Call-back for when saving is complete.
	 */
	protected void onSaveComplete() {
	};

	/**
	 * Call-back for when the saving fails.
	 */
	protected abstract void onSaveFailure();

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
			Log.info("Core", "Exception in onInterval() of SaveMutexTask().");
			e.printStackTrace();
		}
	}

	@Override
	protected void onDelayComplete() {

		// when delay is over, handle mutex
		onInterval(getTimeLeft());
	}

	@Override
	protected void onExpire() {
		onSaveFailure();
	}

	/**
	 * Handles the mutex interaction.
	 */
	private void handleMutex() {

		// TODO remove
		long start = System.currentTimeMillis();

		// if mutex is locked
		if (mutexable.isLocked()) {

			// if not sent a save request
			if (!request) {
				request = true;

				// cancel this task
				cancel();

				// TODO remove
				Log.info("TEST-SaveMutexTask", "Mutex is unique(" + start + ") for " + mutexable.toString());

				boolean result = onSave();
				// TODO remove
				Log.info("TEST-SaveMutexTask", "Did onSave complete for " + mutexable.toString() + "? " + result + " (" + (System.currentTimeMillis() - start) + " msecs)");
				if (result) {

					// unlock the mutex and sync it to db
					mutexable.unlock();
					syncMutex();

					// call bukkit event to say it was saved
					Bukkit.getPluginManager().callEvent(new AsyncMutexSaveEvent(mutexable));

					// optional call-back
					onSaveComplete();
					
					// schedule a sync save complete event
					Bukkit.getScheduler().runTask(getPlugin(), () -> {
						Bukkit.getPluginManager().callEvent(new MutexSaveCompleteEvent(mutexable));
					});
				}
				else {
					onSaveFailure();
				}
			}
		}
		else {
			// TODO remove
			Log.info("TEST-SaveMutexTask", "Mutex not locked(" + start + "), will retry for " + mutexable.toString() + " (" + (System.currentTimeMillis() - start) + " msecs)");
		}
	}
}

