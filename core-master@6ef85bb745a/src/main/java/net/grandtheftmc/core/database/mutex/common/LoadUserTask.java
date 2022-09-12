package net.grandtheftmc.core.database.mutex.common;

import java.sql.Connection;

import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.MutexDAO;
import net.grandtheftmc.core.database.mutex.task.LoadMutexTask;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.debug.Log;

public abstract class LoadUserTask extends LoadMutexTask {

	/** The user reference */
	private User user;

	/**
	 * Constructs a new LoadUserTask, which attempts to load a user,
	 * asynchronously.
	 * 
	 * @param plugin - the owning plugin
	 * @param user - the user being loaded
	 */
	public LoadUserTask(Plugin plugin, User user) {
		super(plugin, user);
		this.user = user;

		// run async
		execute(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean fetchMutex() {
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			return MutexDAO.getUserMutex(conn, user.getUUID());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// else not free
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void syncMutex() {
		// TODO remove
		Log.info("TEST-LoadUserTask", "Setting " + user.getUUID() + "'s mutex to " + user.isLocked());
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			MutexDAO.setUserMutex(conn, user.getUUID(), user.isLocked());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
