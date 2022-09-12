package net.grandtheftmc.core.task.common;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;

public class BossBarTask {

	/** Whether or not the task is enabled */
	private boolean enabled;
	/** The task object held */
	private BukkitTask task;

	/**
	 * Construct a new BossBar task that will update boss bar info for all
	 * users.
	 */
	public BossBarTask() {
		this.enabled = true;
		constructTask();
	}

	/**
	 * Construct the runnable task and assign it.
	 */
	public void constructTask() {
		this.task = new BukkitRunnable() {
			@Override
			public void run() {
				for (User user : UserManager.getInstance().getUsers()) {
					user.refreshBossBar();
				}
			}
		}.runTaskTimerAsynchronously(Core.getInstance(), 0, 1);
	}

	/**
	 * Get whether or not the boss bar task is enabled.
	 * 
	 * @return {@code true} if the boss bar task is enabled.
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * Set whether or not the boss bar task is enabled.
	 * 
	 * @param enabled - {@code true} if the task is enabled.
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;

		// if disabling, cancel the task
		if (!enabled) {
			if (task != null) {
				task.cancel();
			}
		}
		else {

			// if enabling and task exists, cancel
			if (task != null) {
				task.cancel();
			}

			// construct new task
			constructTask();
		}
	}
}
