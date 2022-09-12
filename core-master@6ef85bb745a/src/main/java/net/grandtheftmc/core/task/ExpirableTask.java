package net.grandtheftmc.core.task;

import org.bukkit.plugin.Plugin;

/**
 * Representation of an expirable {@link Task}. It will expire after a set
 * length. Optionally can be dynamic or set-in-stone via locking the handler.
 */
public abstract class ExpirableTask extends RepeatingTask {

	/** Whether or not the task's time/length is locked. */
	private final boolean locked;
	/** Overall length of the task in seconds. */
	private double length;
	/** Current time in seconds left until the task expires. */
	private double timeLeft;
	/** Whether or not this task has expired. */
	private boolean expired;

	/**
	 * Constructs a new ExpirableTask. Defaults to making multiExecution 'false'
	 * and locked to 'true'.
	 * 
	 * @param plugin - Plugin handling the task.
	 * @param delay - Delay in seconds until the task begins execution.
	 * @param interval - Interval in seconds between executions.
	 * @param length - Overall length of the task in seconds (how long until
	 *            expiration).
	 */
	public ExpirableTask(Plugin plugin, double delay, double interval, double length) {
		this(plugin, false, delay, interval, length, true);
	}

	/**
	 * Constructs a new ExpirableTask. Defaults to making multiExecution
	 * 'false'.
	 * 
	 * @param plugin - Plugin handling the task.
	 * @param delay - Delay in seconds until the task begins execution.
	 * @param interval - Interval in seconds between executions.
	 * @param length - Overall length of the task in seconds (how long until
	 *            expiration).
	 * @param locked - Whether or not to lock the tasks' time and length.
	 */
	public ExpirableTask(Plugin plugin, double delay, double interval, double length, boolean locked) {
		this(plugin, false, delay, interval, length, locked);
	}

	/**
	 * Constructs a new ExpirableTask.
	 * 
	 * @param plugin - Plugin handling the task.
	 * @param multiExecution - Whether or not the task can be executed multiple
	 *            times.
	 * @param delay - Delay in seconds until the task begins execution.
	 * @param interval - Interval in seconds between executions.
	 * @param length - Overall length of the task in seconds (how long until
	 *            expiration).
	 * @param locked - Whether or not to lock the tasks' time and length.
	 */
	public ExpirableTask(Plugin plugin, boolean multiExecution, double delay, double interval, double length, boolean locked) {
		super(plugin, multiExecution, delay, interval);
		this.length = length;
		this.timeLeft = length;
		this.locked = locked;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void onInterval() {
		if (timeLeft > 0) {
			onInterval(timeLeft);
			timeLeft = timeLeft - interval;
			return;
		}

		cancel();
		expired = true;
		onExpire();
	}

	/**
	 * Check to see if the TaskHandler's time/length is currently locked.
	 * 
	 * @return {@code true} if the TaskHandler is locked, otherwise
	 *         {@code false}.
	 */
	public boolean isLocked() {
		return locked;
	}

	/**
	 * Get the overall length of the task.
	 * 
	 * @return {@code double} representing the task's overall length in seconds.
	 */
	public double getLength() {
		return length;
	}

	/**
	 * Set the length of the task.
	 * 
	 * @param length - {@code double} to be set as the task's new length.
	 */
	public void setLength(double length) {
		this.length = length;
	}

	/**
	 * Get the current time left until the task expires.
	 * 
	 * @return {@code double} representing the time left in seconds until
	 *         expiration.
	 */
	public double getTimeLeft() {
		return timeLeft;
	}

	/**
	 * Set the time left until the task expires.
	 * 
	 * @param timeLeft - {@code double}, in seconds, to be set as the task's new
	 *            time left.
	 */
	public void setTimeLeft(double timeLeft) {
		this.timeLeft = timeLeft;
	}

	/**
	 * Checks to see if the task has expired. This differs from
	 * {@link #isCancelled()} since this checks for whether or not the time has
	 * actually depleted- properly expiring instead of possibly being cancelled
	 * somewhere during the task's execution.
	 * 
	 * @return {@code true} if the time left for the task until expiration is 0,
	 *         marking it as expired. Otherwise, if it is not, {@code false}.
	 */
	public boolean hasExpired() {
		return expired;
	}

	/**
	 * Optional call-back for when the task ends execution.
	 */
	protected void onExpire() {
	}

	/**
	 * Call-back for when the task is run. This allows you to execute code
	 * according to its execution interval and it also supplies the current
	 * time-left. This is called by {@link #onInterval()}.
	 * 
	 * @param timeLeft - {@code double} representing the time left, in seconds,
	 *            until the task expires
	 */
	protected abstract void onInterval(double timeLeft);

}
