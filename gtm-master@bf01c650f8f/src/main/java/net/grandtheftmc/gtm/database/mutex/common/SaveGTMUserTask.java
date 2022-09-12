package net.grandtheftmc.gtm.database.mutex.common;

import java.sql.Connection;

import org.bukkit.plugin.Plugin;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.mutex.task.SaveMutexTask;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.gtm.database.dao.MutexDAO;
import net.grandtheftmc.gtm.users.GTMUser;

public abstract class SaveGTMUserTask extends SaveMutexTask {

	/** The gtm user reference */
	private GTMUser user;

	/**
	 * Constructs a new SaveGTMUserTask, which attempts to save the gtm user,
	 * asynchronously.
	 * 
	 * @param plugin - the owning plugin
	 * @param user - the user being saved
	 */
	public SaveGTMUserTask(Plugin plugin, GTMUser user) {
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
		Log.info("TEST-SaveGTMUserTask", "Setting " + user.getUUID() + "'s mutex to " + user.isLocked());
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			MutexDAO.setGTMUserMutex(conn, user.getUUID(), user.isLocked());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
