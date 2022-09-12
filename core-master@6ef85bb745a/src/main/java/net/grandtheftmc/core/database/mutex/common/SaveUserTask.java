package net.grandtheftmc.core.database.mutex.common;

import java.sql.Connection;

import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.MutexDAO;
import net.grandtheftmc.core.database.mutex.task.SaveMutexTask;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.debug.Log;

public abstract class SaveUserTask extends SaveMutexTask {

	/** The user reference */
	private User user;

	/**
	 * Constructs a new SaveUserTask, which attempts to save the user,
	 * asynchronously.
	 * 
	 * @param plugin - the owning plugin
	 * @param user - the user being saved
	 */
	public SaveUserTask(Plugin plugin, User user) {
		super(plugin, user);
		this.user = user;

		// run async
		execute(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void syncMutex() {
		// TODO remove
		Log.info("TEST-SaveUserTask", "Setting " + user.getUUID() + "'s mutex to " + user.isLocked());
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			MutexDAO.setUserMutex(conn, user.getUUID(), user.isLocked());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
