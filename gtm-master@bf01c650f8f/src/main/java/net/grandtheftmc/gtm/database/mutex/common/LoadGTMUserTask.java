package net.grandtheftmc.gtm.database.mutex.common;

import java.sql.Connection;

import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.mutex.task.LoadMutexTask;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.gtm.database.dao.MutexDAO;
import net.grandtheftmc.gtm.users.GTMUser;

public abstract class LoadGTMUserTask extends LoadMutexTask {

	/** The gtm user reference */
	private GTMUser user;

	/**
	 * Constructs a new LoadGTMUserTask, which attempts to load a gtm user,
	 * asynchronously.
	 * 
	 * @param plugin - the owning plugin
	 * @param user - the gtm user being loaded
	 */
	public LoadGTMUserTask(Plugin plugin, GTMUser user) {
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
			return MutexDAO.getGTMUserMutex(conn, user.getUUID());
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
		Log.info("TEST-LoadGTMUserTask", "Setting " + user.getUUID() + "'s mutex to " + user.isLocked());
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			MutexDAO.setGTMUserMutex(conn, user.getUUID(), user.isLocked());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
