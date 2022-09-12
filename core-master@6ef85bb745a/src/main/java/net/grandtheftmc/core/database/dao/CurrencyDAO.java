package net.grandtheftmc.core.database.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.currency.trans.CurrencyTransaction;

public class CurrencyDAO {

	/**
	 * Get the amount of the specified currency for the specified uuid on the
	 * given serverKey.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the key of server to lookup the currency for
	 * @param uuid - the uuid of the of the user
	 * @param currency - the currency to retrieve
	 * 
	 * @return The amount of the specified currency the user has, if found,
	 *         otherwise 0.
	 */
	public static Integer getCurrency(Connection conn, String serverKey, UUID uuid, Currency currency) {

		String query = "SELECT amount FROM user_currency WHERE uuid=UNHEX(?) AND server_key=? AND currency=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);
			ps.setString(3, currency.getId());

			try (ResultSet result = ps.executeQuery()) {
				if (result.next()) {
					return result.getInt("amount");
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[CurrencyDAO] Error attempting to getCurrency() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId());
			exc.printStackTrace();
		}

		return 0;
	}
	
	/**
	 * Save the specified currency/amount for the specified user.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key
	 * @param uuid - the uuid of the user
	 * @param currency - the currency to save
	 * @param balance - the balance of the currency
	 * 
	 * @return {@code true} if the currency was saved, {@code false} otherwise.
	 */
	public static boolean saveCurrency(Connection conn, String serverKey, UUID uuid, Currency currency, int balance){
		
		String query = "INSERT IGNORE INTO user_currency (uuid, server_key, currency, amount) VALUES (UNHEX(?), ?, ?, ?) ON DUPLICATE KEY UPDATE amount = VALUES(amount);";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);
			ps.setString(3, currency.getId());
			ps.setInt(4, balance);
			
			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[CurrencyDAO] Error attempting to saveCurrency() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId() + ", amount=" + balance);
			exc.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Increments the specified currency/amount for the specified user.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key
	 * @param uuid - the uuid of the user
	 * @param currency - the currency to save
	 * @param numToAdd - the number to add
	 * 
	 * @return {@code true} if the currency was incremented, {@code false} otherwise.
	 * 
	 * @deprecated - This only exists for compatibility purposes, and is not safe without mutex contention.
	 */
	@Deprecated
	public static boolean addCurrency(Connection conn, String serverKey, UUID uuid, Currency currency, int numToAdd){
		
		String query = "UPDATE user_currency SET amount=amount + ? WHERE uuid=UNHEX(?) AND server_key=? AND currency=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setInt(1, numToAdd);
			ps.setString(2, uuid.toString().replaceAll("-", ""));
			ps.setString(3, serverKey);
			ps.setString(4, currency.getId());
			
			ps.executeUpdate();
			return true;
		}
		catch (SQLException exc) {
			Core.log("[CurrencyDAO] Error attempting to addCurrency() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId() + ", numToAdd=" + numToAdd);
			exc.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Creates the specified currency entry for the specified user.
	 * <p>
	 * This is so they have a value to save to, and should be checked when a user logs in.
	 * </p>
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key
	 * @param uuid - the uuid of the user
	 * @param currency - the currency to save
	 * 
	 * @return {@code true} if the currency was created, {@code false} otherwise.
	 */
	public static boolean createCurrency(Connection conn, String serverKey, UUID uuid, Currency currency){
		
		String existQuery = "SELECT currency FROM user_currency WHERE uuid=UNHEX(?) AND server_key=? AND currency=?";
		
		boolean exists = false;
		try (PreparedStatement ps = conn.prepareStatement(existQuery)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);
			ps.setString(3, currency.getId());
			
			try (ResultSet result = ps.executeQuery()){
				if (result.next()){
					String c = result.getString("currency");
					if (c != null){
						exists = true;
					}
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[CurrencyDAO] Error attempting to createCurrency(EXIST) for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId());
			exc.printStackTrace();
			return false;
		}
		
		if (!exists){
			String query = "INSERT IGNORE INTO user_currency (uuid, server_key, currency, amount) VALUES (UNHEX(?), ?, ?, ?);";

			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setString(1, uuid.toString().replaceAll("-", ""));
				ps.setString(2, serverKey);
				ps.setString(3, currency.getId());
				ps.setInt(4, 0);
				
				ps.executeUpdate();
				
				// currency was created, so return true
				return true;
			}
			catch (SQLException exc) {
				Core.log("[CurrencyDAO] Error attempting to createCurrency(CREATE) for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId());
				exc.printStackTrace();
				return false;
			}
		}
		
		return false;
	}

	/**
	 * Get all the currencies for the specified user on the specified serverKey.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the key of the server to lookup
	 * @param uuid - the uuid of the user to lookup
	 * 
	 * @return A mapping of Currency to Integer where the currency is the unique
	 *         lookup and the Integer is how much of that currency the player
	 *         has.
	 */
	public static Map<Currency, Integer> getAllCurrencies(Connection conn, String serverKey, UUID uuid) {

		Map<Currency, Integer> currencies = new HashMap<>();

		String query = "SELECT currency, amount FROM user_currency WHERE uuid=UNHEX(?) AND server_key=?;";

		try (PreparedStatement ps = conn.prepareStatement(query)) {
			ps.setString(1, uuid.toString().replaceAll("-", ""));
			ps.setString(2, serverKey);

			try (ResultSet result = ps.executeQuery()) {
				while (result.next()) {

					Currency curr = Currency.fromID(result.getString("currency")).orElse(null);
					if (curr != null) {
						int amount = result.getInt("amount");

						currencies.put(curr, amount);
					}
				}
			}
		}
		catch (SQLException exc) {
			Core.log("[CurrencyDAO] Error attempting to getAllCurrencies() for serverKey=" + serverKey + ", uuid=" + uuid.toString());
			exc.printStackTrace();
		}

		return currencies;
	}

	/**
	 * Saves all the currencies specified to the database for the given user.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key to save for
	 * @param uuid - the uuid of the user
	 * @param currencies - the currencies to save
	 */
	public static void saveCurrencies(Connection conn, String serverKey, UUID uuid, Map<Currency, Integer> currencies) {
		
		for (Currency currency : currencies.keySet()){
			
			int balance = currencies.get(currency);
			
			String query = "INSERT IGNORE INTO user_currency (uuid, server_key, currency, amount) VALUES (UNHEX(?), ?, ?, ?) ON DUPLICATE KEY UPDATE amount = VALUES(amount);";

			try (PreparedStatement ps = conn.prepareStatement(query)) {
				ps.setString(1, uuid.toString().replaceAll("-", ""));
				ps.setString(2, serverKey);
				ps.setString(3, currency.getId());
				ps.setInt(4, balance);
				
				ps.executeUpdate();
			}
			catch (SQLException exc) {
				Core.log("[CurrencyDAO] Error attempting to saveCurrencies() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", currency=" + currency.getId() + ", amount=" + balance);
				exc.printStackTrace();
			}
		}
	}
	
	/**
	 * Logs the specified currency transactions to the database.
	 * 
	 * @param conn - the database connection thread
	 * @param serverKey - the server key
	 * @param uuid - the uuid of the player saving their transaction
	 * @param trans - the list of transactions to save
	 */
	public static void logCurrencyTransaction(Connection conn, String serverKey, UUID uuid, List<CurrencyTransaction> trans){
		
		for (CurrencyTransaction t : trans){
			String query = "INSERT INTO log_currency_transaction (uuid, server_key, currency, amount, source, reason) VALUES (UNHEX(?), ?, ?, ?, ?, ?);";
			
			try (PreparedStatement ps = conn.prepareStatement(query)){
				ps.setString(1, uuid.toString().replaceAll("-", ""));
				ps.setString(2, serverKey);
				ps.setString(3, t.getCurrency().getId());
				ps.setInt(4, t.getAmount());
				ps.setString(5, t.getSource().getSource().getId());
				ps.setString(6, t.getSource().getReason().isPresent() ? t.getSource().getReason().get() : "");
				
				ps.executeUpdate();
			}
			catch(SQLException exc){
				Core.log("[CurrencyDAO] Error logging currency transaction for server_key=" + serverKey + ", uuid=" + uuid.toString() + ", transaction=" + t.toString());
				exc.printStackTrace();
			}
		}
	}
	
}
