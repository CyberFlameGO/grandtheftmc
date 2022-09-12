package net.grandtheftmc.core.transaction.state.user;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.json.simple.JSONObject;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.transaction.state.StateTransaction;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.core.util.json.JSONParser;

public class UserStateTransaction extends StateTransaction {

	/** The owner of the transaction */
	private final UUID uuid;

	/**
	 * Construct a new UserStateTransaction.
	 * <p>
	 * This is a way of scheduling new state changes for a user at a different
	 * time. Specific examples would be allow us to give a currency/rank in the
	 * future for the specified player.
	 * 
	 * @param id - the ID of the transaction
	 * @param uuid - the UUID of the user for this transaction
	 * @param payload - the payload contents for this transaction
	 * @param creation - the time that this transaction was created
	 * @param processAt - the time that this transaction should be processed
	 */
	public UserStateTransaction(int id, UUID uuid, JSONObject payload, long creation, long processAt) {
		super(id, payload, creation, processAt);
		this.uuid = uuid;
	}

	/**
	 * Get the UUID of player that owns this transaction.
	 * 
	 * @return The UUID of the player that owns this transaction.
	 */
	public UUID getUUID() {
		return uuid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;

		if (o == null || getClass() != o.getClass())
			return false;

		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(13, 37).append(getId()).toHashCode();
	}

	/**
	 * Process the specified transactions for the given player.
	 * <p>
	 * This will collect all the transactions for that player, regardless of
	 * server type/id, and filter them to call only the correct ones.
	 * <p>
	 * This will remove transactions if they were successfully processed.
	 * <p>
	 * Note: Please call this method on an async thread! For more details see
	 * {@link #process(Connection, Plugin, Player, ServerType, int)};
	 * 
	 * @param plugin - the owning plugin
	 * @param player - the player to check transactions for
	 * @param serverType - the type of server we are checking the transactions
	 *            for
	 * @param serverNum - the ID of the server we are checking the transactions
	 *            for
	 */
	public static void process(Plugin plugin, Player player, String serverType, int serverNum) {
		try (Connection conn = BaseDatabase.getInstance().getConnection()) {
			process(conn, plugin, player, serverType, serverNum);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process the specified transactions for the given player.
	 * <p>
	 * This will collect all the transactions for that player, regardless of
	 * server type/id, and filter them to call only the correct ones.
	 * <p>
	 * This will remove transactions if they were successfully processed.
	 * <p>
	 * Note: Please call this method on an async thread!
	 * 
	 * @param conn - the database connection thread
	 * @param plugin - the owning plugin
	 * @param player - the player we are processing the transactions for
	 * @param serverType - the type of server we are checking the transactions
	 *            for
	 * @param serverNum - the ID of the server we are checking the transactions
	 *            for
	 */
	public static void process(Connection conn, Plugin plugin, Player player, String serverType, int serverNum) {
		
		UserStateTransactionDAO.getUserStateTransactions(conn, player.getUniqueId()).forEach((id, transaction) -> {
			JSONParser payload = new JSONParser(transaction.getPayload());

			// Only run on correct server
			JSONParser server = payload.parseObject("server");
			if (server.hasKey("type")) {
				
				// Validate correct server type
				if (!serverType.equalsIgnoreCase(server.getString("type"))) {
					return;
				}

				// Validate correct server number
				if (server.hasKey("id")) {
					if (serverNum != server.getInt("id")) {
						return;
					}
				}
			}

			// make sure we're on sync thread
			Bukkit.getScheduler().runTaskLater(plugin, () -> {

				// call the processing of the event
				UserStateTransactionEvent event = new UserStateTransactionEvent(player, transaction);
				Bukkit.getPluginManager().callEvent(event);

				// Remove from transactions as processed
				if (event.isProcessed()) {

					// async update
					Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
						try (Connection deleteConn = BaseDatabase.getInstance().getConnection()) {
							boolean removed = UserStateTransactionDAO.removeUserStateTransaction(deleteConn, id);
							if (removed){
								UserStateTransactionDAO.createLogUserStateTransaction(deleteConn, id, transaction);
							}
						}
						catch (Exception e) {
							Log.error("Core", "We were unable to delete a user state transaction id=" + id);
							e.printStackTrace();
						}
					});

					Log.info("Core", "Successfully processed UserStateTransaction for name=" + player.getName() + ", payload=" + transaction.getPayload().toJSONString());
				}
			}, 20L);
		});
	}

}
