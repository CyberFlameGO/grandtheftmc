package net.grandtheftmc.gtm.users;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.users.UserDAO;

public class GTMUserDAO {

    /**
     * @param uuid the player's uuid that is transfering. The player should be kicked before doing this, also should be done with a delay.
     * @param server the gtm server number that the player is transfering to.
     */
    public static boolean transferData(UUID uuid, int server){
        String query = "SELECT * FROM " + Core.name() + " WHERE uuid = UNHEX(?)";
        String targetServerQuery = "INSERT INTO gtm" + server + " (uuid,name,rank,backpackContents,cheatcodes,bank) VALUES(UNHEX(?),?,?,?,?,?) ON DUPLICATE KEY UPDATE `name`=?, `rank`=?, `backpackContents`=?, `cheatcodes`=?, `bank`=?";;
        String originServerQuery = "DELETE FROM " + Core.name() + " WHERE `uuid` = UNHEX('" + uuid.toString().replaceAll("-", "") +"')";

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            double bank;
            GTMRank rank;
            String backpack;
            Blob cheatcodes;
            String name;
            int money = CurrencyDAO.getCurrency(connection, Currency.MONEY.getServerKey(), uuid, Currency.MONEY);
            int permits = CurrencyDAO.getCurrency(connection, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT);
            try (PreparedStatement statement = connection.prepareStatement(query)) {
            	
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                try (ResultSet set = statement.executeQuery()){

                    set.next();

                    bank = set.getDouble("bank");
                    rank = GTMRank.fromString(set.getString("rank"));
                    backpack = set.getString("backpackContents");;
                    cheatcodes = set.getBlob("cheatcodes");
                    name = set.getString("name");
                    Core.log("[TransferCommand] [uuid=" + uuid.toString() + "] Got " + bank + " / " + rank + " / " + backpack + " / " + cheatcodes + " / " + name + ". Transfering data to GTM" + server);
                }
                catch (SQLException e){
                    e.printStackTrace();
                    return false;
                }
            }

            /**
             * delete the entire record of the current player.
             */
            connection.prepareStatement(originServerQuery).execute();
            Core.log("[TransferCommand] [uuid=" + uuid + "] Deleted player's record from original server.");
            CurrencyDAO.saveCurrency(connection, Core.name().toUpperCase(), uuid, Currency.MONEY, 0);
            CurrencyDAO.saveCurrency(connection, Core.name().toUpperCase(), uuid, Currency.PERMIT, 0);

            /**
             * Transfer values over to the new server.
             */
            try (PreparedStatement statement = connection.prepareStatement(targetServerQuery)){
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, name);
                statement.setString(3, rank.toString());
                statement.setString(4, backpack);
                statement.setBlob(5, cheatcodes);
                statement.setDouble(6, bank);

                statement.setString(7, name);
                statement.setString(8, rank.toString());
                statement.setString(9, backpack);
                statement.setBlob(10, cheatcodes);
                statement.setDouble(11, bank);

                Core.log("[TransferCommand] [uuid=" + uuid.toString() + "] Sent information to target server with following statement: " + statement.toString());

                statement.execute();
            }
            CurrencyDAO.saveCurrency(connection, "GTM"+server, uuid, Currency.MONEY, money);
            CurrencyDAO.saveCurrency(connection, "GTM"+server, uuid, Currency.PERMIT, permits);
            Core.log("[TransferCommand] [uuid=" + uuid + "] Updated player's currencies money=" + money + ", permits=" + permits);


        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


        return true;
    }

    public static boolean setRank(String name, GTMRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `rank`=? WHERE `name`=?;")) {
                statement.setString(1, rank.getName());
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setRank(UUID uuid, GTMRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `rank`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setString(1, rank.getName());
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKills(String name, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `kills`=? WHERE `name`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKills(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `kills`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setDeaths(String name, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `deaths`=? WHERE `name`=?;")) {
                statement.setInt(1, value);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setDeaths(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `deaths`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
	 * Hard sets the value of the money currency in the user_currency table,
	 * using another DAO to help.
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean setMoney(String name, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.saveCurrency(connection, Currency.MONEY.getServerKey(), uuid, Currency.MONEY, (int) value);
        	}
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
	 * Hard sets the value of the money currency in the user_currency table,
	 * using another DAO to help.
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean setMoney(UUID uuid, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.saveCurrency(connection, Currency.MONEY.getServerKey(), uuid, Currency.MONEY, (int) value);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean setBank(String name, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bank`=? WHERE `name`=?;")) {
                statement.setDouble(1, value);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setBank(UUID uuid, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `bank`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setDouble(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    public static boolean addBank(UUID uuid, double value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET bank = bank + ? WHERE uuid = UNHEX(?);")) {
                statement.setDouble(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setPlaytime(UUID uuid, long value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `playtime`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setLong(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKillCounter(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `killCounter`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean setKillstreak(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `killStreak`=? WHERE `uuid`=UNHEX(?);")) {
                statement.setInt(1, value);
                statement.setString(2, uuid.toString().replaceAll("-", ""));
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

	/**
	 * Hard sets the value of the permit currency in the user_currency table,
	 * using another DAO to help.
	 * 
	 * @deprecated - Please see CurrencyDAO for how to use this global currency.
	 */
    @Deprecated
	public static boolean setPermits(UUID uuid, int value) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.saveCurrency(connection, Currency.PERMIT.getServerKey(), uuid, Currency.PERMIT, value);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
