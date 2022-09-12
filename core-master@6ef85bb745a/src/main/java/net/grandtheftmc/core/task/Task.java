package net.grandtheftmc.core.task;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.util.PluginAssociated;

/**
 * Representation of a basic once off task.
 */
public abstract class Task implements PluginAssociated, Runnable {

	/** Plugin handling the task. */
	protected final Plugin plugin;
	/** Can this task be executed multiple times */
	protected final boolean multiExecution;
	/** Whether or not the task is asynchronous. */
	protected boolean async;
	/** The task's id. */
	protected int id = -1;
	/** Whether this task has {@link #execute()} */
	private boolean executed;

	/**
	 * Constructs a new Task.
	 *
	 * @param plugin - Plugin handling the task.
	 */
	public Task(Plugin plugin) {
		this(plugin, false);
	}

	/**
	 * Constructs a new Task.
	 *
	 * @param plugin - Plugin handling the task
	 * @param multiExecution - ability for it to be executed multiple times
	 */
	public Task(Plugin plugin, boolean multiExecution) {
		Validate.notNull(plugin, "Plugin cannot be null!");
		this.plugin = plugin;
		this.multiExecution = multiExecution;
	}

	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Check to see whether or not this task is asynchronous. This will
	 * <b>always</b> return {@code false} if the task has not been executed.
	 *
	 * @return {@code true} if the task is running asynchronously, otherwise
	 *         {@code false}.
	 */
	public boolean isAsynchronous() {
		return async;
	}

	/** Starts the synchronous execution of the task. */
	public void execute() {
		execute(false);
	}

	/**
	 * Starts the specified (sync/async) execution of the task.
	 *
	 * @param runAsynchronously - whether or not to run this task asynchronously
	 */
	public void execute(boolean runAsynchronously) {
		if (!executed || multiExecution) {
			if (runAsynchronously) {
				async = true;
				id = Bukkit.getScheduler().runTaskAsynchronously(plugin, this).getTaskId();
			}
			else {
				id = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, this);
			}

			executed = true;
		}
	}

	/**
	 * Get the task's id. This is by default initialized to -1, so if it returns
	 * -1 it means the task has not been executed yet.
	 *
	 * @return Task id in the form of a {@code int}.
	 */
	protected int getTaskId() {
		return id;
	}

}
