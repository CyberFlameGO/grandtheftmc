package net.grandtheftmc.core.redis.listener;

import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.transaction.state.user.UserStateTransaction;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.jedis.JMessageListener;
import net.grandtheftmc.jedis.message.UserStateTransactionCheck;

public class UserStateTransactionCheckListener implements JMessageListener<UserStateTransactionCheck> {

	@Override
	public void onReceive(ServerTypeId serverTypeId, UserStateTransactionCheck userStateMessage) {

		// ignore if core is not setup
		if (!Core.isCoreEnabled())
			return;

		// run on sync
		ServerUtil.runTask(() -> {

			// grab the player
			Player player = Bukkit.getPlayer(userStateMessage.getUUID());

			// if no player online, skip, user state transactions will be
			// handled
			if (player == null) {
				return;
			}

			// grab the core user
			User coreUser = UserManager.getInstance().getUser(userStateMessage.getUUID()).orElse(null);
			if (coreUser != null) {

				// make sure they are mutex locked
				if (coreUser.isLocked()) {

					// async fetch
					Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), () -> {

						try (Connection conn = BaseDatabase.getInstance().getConnection()) {
							// check for user state transactions
							UserStateTransaction.process(conn, Core.getInstance(), player, Core.getSettings().getType().getName(), Core.getSettings().getNumber());
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					});
				}
			}
			
			// TODO test remove
			Core.log("[UserStateTransactionCheckListener][DEBUG] Received user state transaction check for " + userStateMessage.getUUID().toString());
		});
	}
}