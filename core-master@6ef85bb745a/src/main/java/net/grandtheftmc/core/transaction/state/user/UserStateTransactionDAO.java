package net.grandtheftmc.core.transaction.state.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.grandtheftmc.core.util.debug.Log;

public class UserStateTransactionDAO {

	/**
	 * Get a mapping of (transaction id, transaction) for the specified user.
	 * <p>
	 * Wrapper around
	 * {@link #getUserStateTransactions(Connection, UUID, boolean)}, only
	 * getting the active transactions to process.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the UUID of the user
	 * 
	 * @return A mapping of (transaction id, transaction) where each is a state
	 *         change that needs to be handled.
	 *
	 * @see UserStateTransactionDAO#getUserStateTransactions(Connection, UUID,
	 *      boolean)
	 */
	public static Map<Integer, UserStateTransaction> getUserStateTransactions(Connection conn, UUID uuid) {
		return getUserStateTransactions(conn, uuid, false);
	}

	/**
	 * Get a mapping of (transaction id, transaction) for the specified user.
	 * <p>
	 * Wrapper around
	 * {@link #getUserStateTransactions(Connection, UUID, boolean, boolean)},
	 * only getting transactions that have not already been processed.
	 *
	 * @param conn - the database connection thread
	 * @param uuid - the UUID of the user
	 * @param includeDelayed - {@code true} if we should get all transactions,
	 *            {@code false} if we should get only the ones that have
	 *            exceeded the process time
	 *
	 * @return A mapping of (transaction id, transaction) where each is a state
	 *         change that needs to be handled.
	 *
	 * @see UserStateTransactionDAO#getUserStateTransactions(Connection, UUID,
	 *      boolean, boolean)
	 */
	public static Map<Integer, UserStateTransaction> getUserStateTransactions(Connection conn, UUID uuid, boolean includeDelayed) {
		return getUserStateTransactions(conn, uuid, includeDelayed, false);
	}

	/**
	 * Get a mapping of (transaction id, transaction) for the specified user.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the UUID of the user
	 * @param includeDelayed - {@code true} if we should get all transactions,
	 *            {@code false} if we should get only the ones that have
	 *            exceeded the process time
	 * @param alreadyProcessed - {@code true} if we should get already processed
	 *            transactions as well, otherwise {@code false}
	 * 
	 * @return A mapping of (transaction id, transaction) where each is a state
	 *         change that needs to be handled.
	 */
	public static Map<Integer, UserStateTransaction> getUserStateTransactions(Connection conn, UUID uuid, boolean includeDelayed, boolean alreadyProcessed) {
		Map<Integer, UserStateTransaction> transactions = new HashMap<>();

		String query = "SELECT id, payload, creation, process_at FROM user_state_transaction WHERE uuid = UNHEX(?)";
		if (!includeDelayed) {
			query += " AND process_at <= CURRENT_TIMESTAMP";
		}

		if (!alreadyProcessed) {
			query += " AND processed = 0";
		}

		query += " ORDER BY creation ASC, process_at ASC;";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, uuid.toString().replaceAll("-", ""));

			try (ResultSet resultSet = statement.executeQuery()) {
				while (resultSet.next()) {
					int id = resultSet.getInt("id");

					JSONObject payload;
					try {
						payload = (JSONObject) new JSONParser().parse(resultSet.getString("payload"));
					}
					catch (ParseException e) {
						Log.error("UserStateTransaction", "Failed to load transaction: " + id);
						e.printStackTrace();
						continue;
					}

					long creation = resultSet.getTimestamp("creation").getTime();
					long processAt = resultSet.getTimestamp("process_at").getTime();

					transactions.put(id, new UserStateTransaction(id, uuid, payload, creation, processAt));
				}
			}
		}
		catch (SQLException e) {
			Log.error("Core", "Unable to getUserStateTransactions() for uuid=" + uuid.toString() + ", includeDelayed=" + includeDelayed + ", alreadyProcessed=" + alreadyProcessed);
			e.printStackTrace();
		}

		return transactions;
	}

	/**
	 * Creates a user state transaction in the database.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the UUID of the user to create the transaction for
	 * @param payload - the payload information that should be parsed
	 * 
	 * @return {@code true} if the transaction was successfully added,
	 *         {@code false} otherwise.
	 */
	public static boolean addUserStateTransaction(Connection conn, UUID uuid, JSONObject payload) {

		String query = "INSERT INTO user_state_transaction (uuid, payload) VALUES (UNHEX(?), ?);";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, uuid.toString().replaceAll("-", ""));
			statement.setString(2, payload.toJSONString());

			statement.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			Log.error("Core", "Unable to addUserStateTransaction() for uuid=" + uuid.toString() + ", payload=" + payload.toJSONString());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Creates the required statement to put a user state transaction in the
	 * database
	 *
	 * @param conn - the database connection thread
	 * @param uuid - the UUID of the user to create the transaction for
	 * @param payload - the payload information that should be parsed
	 *
	 * @return An {@link ArrayList} containing the statements required to put a
	 *         user state transaction in the database
	 */
	public static List<PreparedStatement> collateSaveUserStateTransaction(Connection conn, UUID uuid, JSONObject payload) {

		String query = "INSERT INTO user_state_transaction (uuid, payload) VALUES (UNHEX(?), ?);";

		// result set
		List<PreparedStatement> statements = new ArrayList<>();

		try {
			PreparedStatement statement = conn.prepareStatement(query);

			statement.setString(1, uuid.toString().replaceAll("-", ""));
			statement.setString(2, payload.toJSONString());

			statements.add(statement);
		}
		catch (SQLException e) {
			Log.error("Core", "Unable to collateSaveUserStateTransaction() for uuid=" + uuid.toString() + ", payload=" + payload.toJSONString());
			e.printStackTrace();
		}

		return statements;
	}

	/**
	 * Removes the specified transaction ID from the user state transaction
	 * table.
	 * 
	 * @param conn - the database connection thread
	 * @param id - the ID of the transaction to remove
	 * 
	 * @return {@code true} if the transaction was removed, {@code false}
	 *         otherwise.
	 */
	public static boolean removeUserStateTransaction(Connection conn, int id) {

		String query = "DELETE FROM user_state_transaction WHERE id = ? LIMIT 1;";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setInt(1, id);

			statement.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			Log.error("Core", "Unable to removeUserStateTransaction() for id=" + id);
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Creates a log for the user state transaction.
	 * <p>
	 * This is so that we know which logs were parsed.
	 * 
	 * @param conn - the database connection thread
	 * @param id - the id of the transaction
	 * @param transaction - the transaction object
	 * 
	 * @return {@code true} if the query was created, {@code false} otherwise.
	 */
	public static boolean createLogUserStateTransaction(Connection conn, int id, UserStateTransaction transaction) {

		String query = "INSERT INTO log_user_state_transaction (id, uuid, payload, creation, process_at) VALUES (?, UNHEX(?), ?, ?, ?);";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setInt(1, id);
			statement.setString(2, transaction.getUUID().toString().replaceAll("-", ""));
			statement.setString(3, transaction.getPayload().toJSONString());
			statement.setTimestamp(4, new Timestamp(transaction.getCreation()));
			statement.setTimestamp(5, new Timestamp(transaction.getProcessAt()));

			statement.executeUpdate();
			return true;
		}
		catch (SQLException e) {
			Log.error("UserStateTransaction", "Unable to createLogUserStateTransaction() for uuid=" + transaction.getUUID().toString() + ", payload=" + transaction.getPayload().toJSONString());
			e.printStackTrace();
		}

		return false;
	}
}
