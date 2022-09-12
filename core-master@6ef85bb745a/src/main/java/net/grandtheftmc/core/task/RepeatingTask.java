package net.grandtheftmc.core.task;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

/**
 * Representation of a repeating {@link Task}. A task that repeats over
 * and over infinitely at a set interval and optional initial delay.
 */
public abstract class RepeatingTask extends Task {

	/** The interval in seconds at which the task executes. */
	protected final double interval;
	/** The initial delay in seconds until the task begins execution. */
	protected final double delay;
	/** Whether or not the task is cancelled. */
	protected boolean cancelled;
	/** Whether the task has begun execution */
	private boolean started;
	/** Whether or not the delay has been completed if there was one. */
	private boolean delayCompleted;
	/** Whether or not the task is currently paused. */
	private boolean paused;
	/** The task's system epoch time in milliseconds. */
	private long epoch;

	/**
	 * Construct a new RepeatingTask, which is used to repeatedly run code.
	 * 
	 * @param plugin - the owning plugin
	 * @param delay - the delay, in seconds, until the task executes
	 * @param interval - the interval, in seconds, between running the task
	 */
	public RepeatingTask(Plugin plugin, double delay, double interval) {
		this(plugin, false, delay, interval);
	}

	/**
	 * Construct a new RepeatingTask, which is used to repeatedly run code.
	 * 
	 * @param plugin - the owning plugin
	 * @param multiExecution - whether this task can be stopped/ran multiple times
	 * @param delay - the delay, in seconds, until the task executes
	 * @param interval - the interval, in seconds, between running the task
	 */
	public RepeatingTask(Plugin plugin, boolean multiExecution, double delay, double interval) {
		super(plugin, multiExecution);
		this.delay = delay;
		this.interval = interval;
		this.epoch = System.currentTimeMillis();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute() {
		execute(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void execute(boolean runAsynchronously) {
		if(!started || multiExecution) {
			if(runAsynchronously) {
				async = true;
				id = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this, 0L, 1L).getTaskId();
			} else {
				id = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, 0L, 1L);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		if (cancelled) {
			Bukkit.getScheduler().cancelTask(id);
			onCancel();
			return;
		}

		if (paused) {
			return;
		}

		if (!started) {
			onStart();
			started = true;
		}

		// The task's current system time in milliseconds.
		long current = System.currentTimeMillis();

		if (delay > 0) {
			if (!delayCompleted) {
				if (current >= epoch + (delay * 1000)) {
					delayCompleted = true;
					onDelayComplete();
				}

				return;
			}
		}

		if (current >= epoch + (interval * 1000)) {
			epoch = current;
			onInterval();
		}
	}

	/**
	 * Check to see whether or not the task has started execution.
	 * 
	 * @return {@code true} if it has started execution, otherwise {@code false}     .
	 */
	public boolean hasStarted() {
		return started;
	}

	/**
	 * Check to see if the task's execution is currently paused.
	 * 
	 * @return {@code true} if the task is paused, otherwise {@code false}.
	 */
	public boolean isPaused() {
		return paused;
	}

	/**
	 * Pause the task's execution if it already isn't.
	 */
	public void pause() {
		if (!paused) {
			paused = true;
			onPause();
		}
	}

	/**
	 * Resume the task if it is currently paused.
	 */
	public void resume() {
		if (paused) {
			paused = false;
			onResume();
		}
	}

	/**
	 * Check to see whether or not the task is cancelled.
	 * 
	 * @return {@code true} if the task has been cancelled, otherwise
	 *         {@code false}.
	 */
	public boolean isCancelled() {
		return cancelled;
	}

	/**
	 * Cancel the task's execution.
	 */
	public void cancel() {
		cancelled = true;
	}

	/**
	 * Get the interval in seconds between executions.
	 * 
	 * @return {@code double} representing the interval between executions.
	 */
	public double getInterval() {
		return interval;
	}

	/**
	 * Optional call-back for when the task starts executing.
	 */
	protected void onStart() {}

	/**
	 * Optional call-back for when the task's execution has been delayed and the
	 * first interval begins.
	 */
	protected void onDelayComplete() {}

	/**
	 * Optional call-back for when the task's execution is paused.
	 */
	protected void onPause() {}

	/**
	 * Optional call-back for when the task's execution is resumed.
	 */
	protected void onResume() {}

	/**
	 * Optional call-back for when the task's execution is cancelled.
	 */
	protected void onCancel() {}

	/**
	 * Call-back for when the task is run at the appropriate interval.
	 */
	protected abstract void onInterval();

}


