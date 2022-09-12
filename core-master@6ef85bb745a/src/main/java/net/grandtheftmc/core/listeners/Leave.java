package net.grandtheftmc.core.listeners;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.ServerStatsDAO;
import net.grandtheftmc.core.database.mutex.common.SaveUserTask;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Playtime;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.debug.Log;

public class Leave implements Listener {

	/**
	 * Listens in on player quit events.
	 * <p>
	 * Note: This is HIGH so that events can listen in BEFORE or AFTER this
	 * event is called since the player will be removed from the container.
	 * </p>
	 * 
	 * @param event - the event
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void onLeave(PlayerQuitEvent event) {

		// grab event variables
		Player p = event.getPlayer();

		// update Move listener
		Move.logout(p.getUniqueId());

		// grab the user
		User coreUser = UserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
		if (coreUser != null) {

			// if they are help op
			if (coreUser.isRank(UserRank.HELPOP)) {
				coreUser.setLeaveTime(System.currentTimeMillis());
				Long playtime = coreUser.getLeaveTime() - coreUser.getJoinTime();
				if (Playtime.playtime.containsKey(p.getName())) {
					playtime = Playtime.playtime.get(p.getName()) + coreUser.getLeaveTime() - coreUser.getJoinTime();
				}
				Playtime.playtime.put(p.getName(), playtime);
			}

			// if quit messages are enabled
			event.setQuitMessage(Core.getSettings().getJoinLeaveMessagesEnabled() ? Utils.f(p.getDisplayName() + " &eleft the game!") : null);
			coreUser.removePerms(p);

			ServerUtil.runTaskAsync(() -> {
				try (Connection connection = BaseDatabase.getInstance().getConnection()) {
					ServerStatsDAO.updatePlaytimeAndFirstlogin(connection, p, coreUser);
				}
				catch (SQLException ex) {
					ex.printStackTrace();
				}
			});
		}

		// REMOVE from local container and save
		User removedUser = UserManager.getInstance().removeUser(p.getUniqueId()).orElse(null);
		if (removedUser != null) {

			new SaveUserTask(Core.getInstance(), removedUser) {
				@Override
				protected boolean onSave() {
					try (Connection conn = BaseDatabase.getInstance().getConnection()) {
						removedUser.onSave(conn);
					}
					catch (SQLException e) {
						e.printStackTrace();
					}

					return true;
				}

				@Override
				protected void onSaveFailure() {
					Log.error("Core", "Unhandled exception while saving " + removedUser.getName());
				}
			};
		}
	}
}