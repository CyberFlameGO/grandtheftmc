package net.grandtheftmc.core.users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.stat.StatDAO;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.users.eventtag.EventTagDAO;
import net.grandtheftmc.core.users.eventtag.TagVisibility;
import net.grandtheftmc.core.util.CoreLocation;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.UUIDUtil;

public class UserDAO {

    /**
     * This method will fetch all of the general user stats,
     * - User Rank
     * - Trial Rank
     * - Trial Rank Expiry
     * - Bucks
     * - Tokens
     * - Crowbars
     * - Prefs
     * - Votes
     * - Vote Streak
     * - Last Vote Streak
     * - Daily Streak
     * - Last Daily Reward
     * - Last Donor Reward
     * - Shown Achievements
     * - Unlocked Achievements
     * - Ignored
     *
     * @param user - user profile
     * @return Whether the stats were fetched
     */
    public static boolean fetchGeneralUser(User user) {
    	
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `users` WHERE `uuid`=?;")) {
                statement.setString(1, user.getUUID().toString());
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        //user.ur = UserRank.getUserRank(result.getString("userrank"));
                        //user.trialRank = UserRank.getUserRankOrNull(result.getString("trialRank"));
                        //user.trialRankExpiry = result.getLong("trialRankExpiry");
                    	// TODO perhaps remove bucks, as its in users table but might be unused
                        user.bucks = result.getInt("bucks");
                        // removed due to CurrencyDAO
                        //user.tokens = result.getInt("tokens");
                        //user.crowbars = result.getInt("crowbars");
                        user.prefs.clear();
                        for (Pref pref : Pref.values())
                            if (result.getBoolean(pref.getDbName()))
                                user.prefs.add(pref);
                        //user.couponCredits = result.getInt("couponCredits");
                        user.dailyStreak = result.getInt("dailyStreak");
                        user.lastDailyReward = result.getLong("lastDailyReward");
                        user.lastDonorReward = result.getString("lastDonorReward");
                        user.shownAchievement = Achievement.getAchivementExact(result.getString("shownAchievement")).orElse(Achievement.Hobo);
                        if (result.getString("unlockedAchievements") == null || result.getString("unlockedAchievements").equalsIgnoreCase("NULL")) {
                            user.addAchievement(Achievement.Hobo);
                            user.setShownAchievement(Achievement.Hobo);
                        }
                        String a = result.getString("unlockedAchievements");
                        if (a != null) {
                            for (String string : a.split(",")) {
                                Achievement.getAchivementExact(string).ifPresent(user.unlockedAchievements::add);
                            }
                        }
                        String ignored = result.getString("ignored");
                        user.ignored = new ArrayList<>();
                        if (ignored != null) {
                            Collections.addAll(user.ignored, ignored.split(","));
                        }
                    }
                }
            }
            
            // get global rank if there is one
            UserRank globalRank = getRank(connection, "GLOBAL", user.getUUID());
            if (globalRank != null) {
            	user.globalRank = globalRank;
            }
            
            // get server rank if there is one
            UserRank serverRank = getRank(connection, Core.name().toUpperCase(), user.getUUID());
            if (serverRank != null) {
            	user.ur = serverRank;
            }

            // get trial rank if there is one
            // Note: We use the default's rank serverKey, as most settings will have per server
            // or global ranks based off this rank.
            Pair<UserRank, Timestamp> trialInfo = getTrialRank(connection, UserRank.DEFAULT.getServerKey(), user.getUUID());
            if (trialInfo != null) {
            	user.trialRank = trialInfo.getLeft();
            	user.trialRankExpiry = trialInfo.getRight().getTime();
            }
            
            user.setServerJoinAddress(StatDAO.getUserJoinAddress(connection, user.getUUID()).orElse("mc.gtm.net"));
            
            // per request of prez, perhaps change down the road
            if (Core.getInstance().getSettings().getType() == ServerType.VICE){
            	user.setServerJoinAddress("play.vicemc.net");
            }
            
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchGeneralUser(user)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will insert the user to the 'users' database.
     *
     * @param uuid - Unique Identifier of the user
     * @param name - Name of the user
     *
     * @return Whether the data was inserted
     */
    public static boolean insertUser(UUID uuid, String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `users`(`uuid`,`lastname`) VALUES(?,?) ON DUPLICATE KEY UPDATE `lastname`=?;")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setString(3, name);
                statement.execute();
            }
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `user`(`uuid`,`name`) VALUES(UNHEX(?),?) ON DUPLICATE KEY UPDATE `name`=?;")) {
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, name);
                statement.setString(3, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:insertUser(uuid,name)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will insert the user to the 'voters' database.
     *
     * @param uuid - Unique Identifier of the user
     * @param name - Name of the user
     * @return Whether the data was inserted
     */
    public static boolean insertVoter(UUID uuid, String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `votes`(`uuid`,`name`) VALUES (?,?) ON DUPLICATE KEY UPDATE `name`=?;")) {
                statement.setString(1, uuid.toString());
                statement.setString(2, name);
                statement.setString(3, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:insertVoter(uuid,name)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Adds the player tag to the specified user.
     * 
     * @param uuid - the uuid of the user to add the tag to
     * @param tag - the tag that you are adding to the player
     * 
     * @return true if the update was successful
     */
    public static boolean addPlayerTag(UUID uuid, EventTag tag) {
    	
        try (Connection connect = BaseDatabase.getInstance().getConnection()) {
            
        	String query = "INSERT IGNORE INTO user_tag (uuid, server_key, tag, enabled) VALUES (UNHEX(?), ?, ?, ?);";
        	
        	try (PreparedStatement statement = connect.prepareStatement(query)){
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, tag.isGlobal() ? "GLOBAL" : Core.name().toUpperCase());
                statement.setString(3, tag.toString());
                statement.setBoolean(4, false);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:addPlayerTag(uuid, tag)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
     * Adds the player tag to the specified user for the specified server key.
     * 
     * @param uuid - the uuid of the user to add the tag to
     * @param serverKey - the server key to add the tag for
     * @param tag - the tag that you are adding to the player
     * 
     * @return {@code true} if the update was successful, {@code false} otherwise.
     */
    public static boolean addPlayerTag(UUID uuid, String serverKey, EventTag tag) {
    	
        try (Connection connect = BaseDatabase.getInstance().getConnection()) {
            
        	String query = "INSERT IGNORE INTO user_tag (uuid, server_key, tag, enabled) VALUES (UNHEX(?), ?, ?, ?);";
        	
        	try (PreparedStatement statement = connect.prepareStatement(query)){
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, serverKey);
                statement.setString(3, tag.toString());
                statement.setBoolean(4, false);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:addPlayerTag(uuid, serverKey, tag)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Updates the player tags for the given user.
     * 
     * @param uuid -  the uuid of the user
     * @param newEquipped the newly equipped tag
     * 
     * @return true if the update was successful
     */
    public static boolean updatePlayerTags(UUID uuid, EventTag newEquipped){
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {

        	String disableQuery = "UPDATE user_tag SET enabled=0 WHERE uuid=UNHEX(?) AND (server_key=? OR server_key=?);";
        	
            try (PreparedStatement statement = connection.prepareStatement(disableQuery)) {
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, "GLOBAL");
                statement.setString(3, Core.name().toUpperCase());
                statement.executeUpdate();
            }

            if(newEquipped!=null) {
            	
            	String equipQuery = "UPDATE user_tag SET enabled=1 WHERE uuid=UNHEX(?) AND tag=?;";
            	
                try (PreparedStatement statement = connection.prepareStatement(equipQuery)) {
                    statement.setString(1, uuid.toString().replaceAll("-", ""));
                    statement.setString(2, newEquipped.toString());
                    statement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            Core.error("[UserDAO:updatePlayerTags(user, equippedTag)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * @param uuid - the uuid of the player to remove from
     * @param tag - the tag that must be removed.
     *
     * @return true if the method was successful.
     */
    public static boolean removePlayerTag(UUID uuid, EventTag tag) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()){
            String removeQuery = "DELETE FROM user_tag WHERE uuid=UNHEX(?) AND tag=?";

            try (PreparedStatement statement = connection.prepareStatement(removeQuery)) {
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, tag.toString());
                statement.execute();
            }

        } catch (SQLException e) {
            Core.error("[UserDAO:removePlayerTag(user, tag)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @param uuid - the uuid of the player
     * @return a readable copy of the player's event tags.
     */
    public static Set<String> fetchReadablePlayerTags(UUID uuid) {
        Set<String> tags = new HashSet<>();
        try (Connection connect = BaseDatabase.getInstance().getConnection()) {

            String query = "SELECT * FROM user_tag WHERE uuid=UNHEX(?);";

            try (PreparedStatement statement = connect.prepareStatement(query)){
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                try(ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        EventTag tag = EventTag.valueOf(result.getString("tag"));
                        String serverKey = result.getString("server_key");
                        tags.add("&b" + tag.toString() + " &7From Server: &6&l" + serverKey.toUpperCase());
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchPlayerTags(uuid)] SQLException occurred");
            e.printStackTrace();
            return null;
        }
        return tags;
    }

    /**
     * Get all the player tags for the given user.
     * 
     * @param user - the user to get the tags of
     * 
     * @return a set of all the unlocked tags a player has for their current server and globally. Also places the enabled tag into the enabled tag slot for the user
     */
    public static Set<EventTag> fetchAndEquipServerPlayerTags(User user){
        Set<EventTag> tags = new HashSet<>();
        try (Connection connect = BaseDatabase.getInstance().getConnection()) {
        	
        	String query = "SELECT * FROM user_tag WHERE uuid=UNHEX(?) AND (server_key=? OR server_key=?);";
        	
            try (PreparedStatement statement = connect.prepareStatement(query)){
                statement.setString(1, user.getUUID().toString().replaceAll("-", ""));
                statement.setString(2, "GLOBAL");
                statement.setString(3, Core.name().toUpperCase());
                try(ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        EventTag tag = EventTag.valueOf(result.getString("tag"));
                        tags.add(tag);
                        if(result.getBoolean("enabled") && EventTagDAO.getTagVisibility(tag)!= TagVisibility.NO_ONE)
                            ServerUtil.runTask(() -> user.setEquipedTag(tag));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchPlayerTags(uuid)] SQLException occurred");
            e.printStackTrace();
            return null;
        }
        return tags;
    }

    /**
     * @param uuid the uuid of the player
     * @param credits the amount of credits to insert
     */
    public static boolean updateCouponCredits(UUID uuid, int credits) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	CurrencyDAO.saveCurrency(connection, Currency.COUPON_CREDIT.getServerKey(), uuid, Currency.COUPON_CREDIT, credits);
//            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `couponCredits`=? WHERE `uuid`=?;")) {
//                statement.setInt(1, credits);
//                statement.setString(2, uuid.toString());
//                statement.execute();
//            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateCouponCredits(uuid,rank)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }


    /**
     * This method will fetch the server_stats related data.<br>
     * (DailyPlayTime, PlayedServers, Etc..)
     *
     * @param user - user profile
     * @return Whether the data were fetched
     */
    public static boolean fetchServerStats(User user) {
        long time = System.currentTimeMillis();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `server_stats` WHERE `uuid`=?;")) {
                statement.setString(1, user.getUUID().toString());
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        user.dailyPlayTime = result.getLong("dailyPlayTime");

                        try (PreparedStatement statement2 = connection.prepareStatement("UPDATE `server_stats` SET `dailyLoginTime`=?,`playedServers`=?,`weeklyLoginTime`=? WHERE `uuid`=?;")) {
                            statement2.setLong(1, time);

                            String playedServers = result.getString("playedServers");
                            if (playedServers == null)
                                playedServers = Core.name();
                            if (!playedServers.toLowerCase().contains(Core.name().toLowerCase()))
                                playedServers += Core.name();
                            statement2.setString(2, playedServers);

                            long weeklyLoginTime = result.getLong("weeklyLoginTime");
                            weeklyLoginTime = weeklyLoginTime == 0 ? time : weeklyLoginTime;
                            statement2.setLong(3, weeklyLoginTime);

                            statement2.setString(4, user.getUUID().toString());
                            statement2.execute();
                        }
                    } else {
                        try (PreparedStatement statement2 = connection.prepareStatement("INSERT INTO `server_stats`(`uuid`,`firstLogin`,`dailyLoginTime`,`dailyPlayTime`,`playedServers`,`weeklyLoginTime`) VALUES(?,?,?,?,?,?);")) {
                            statement2.setString(1, user.getUUID().toString());
                            statement2.setLong(2, 0);
                            statement2.setLong(3, time);
                            statement2.setLong(4, 0);
                            statement2.setString(5, Core.name());
                            statement2.setLong(6, time);

                            user.dailyPlayTime = 0;
                            statement2.execute();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchServerStats(user)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the users rank in the database only.
     *
     * @param uuid - Unique Identifier of the user
     * @param rank - Rank to update to
     * @return Whether the data updated
     *
     * @deprecated - Please see {@link #saveRank(Connection, String, UUID, UserRank)} for how to update a user rank properly. This exists for compatibility purposes only.
     */
    @Deprecated
	public static boolean updateUserRank(UUID uuid, UserRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return saveRank(connection, rank.getServerKey(), uuid, rank);
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserRank(uuid,rank)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This will update the trial rank data in 'user_trial_rank' table only.
     * <p>
     * Note: If a player has a trial rank, this will delete their old one and add the new one.
     * </p>
     *
     * @param uuid - Unique Identifier of the user
     * @param rank - Rank of the trial (can be null)
     * @param expiry - Time in milliseconds of the expiry
     * @return Whether the data updated
     * 
     * @deprecated - Please see {@link #createTrialRank(Connection, String, UUID, UserRank, Timestamp)} for how to update a trial rank properly. This exists for compatibility purposes only.
     */
    @Deprecated
	public static boolean updateUserTrialRank(UUID uuid, @Nullable UserRank rank, long expiry) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	
        	// grab the trial data
            Pair<UserRank, Timestamp> trialData = getTrialRank(connection, rank.getServerKey(), uuid);
            if (trialData != null){
            	// remove old data
            	removeTrialRank(connection, rank.getServerKey(), uuid);
            }
            
        	return createTrialRank(connection, rank.getServerKey(), uuid, rank, new Timestamp(expiry));
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserTrialRank(uuid,rank,expiry)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This fetch the amount of bucks from 'users' only.
     *
     * @param uuid - Unique Identifier of the user
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static int fetchUserBucks(UUID uuid) {
        int bucks = 0;
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `bucks` FROM `users` WHERE `uuid`=?;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if(result.next()) {
                        bucks = result.getInt("bucks");
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchUserBucks(uuid,bucks)] SQLException occurred");
            e.printStackTrace();
            return -1;
        }

        return bucks;
    }

    /**
     * This fetch the amount of bucks from 'users' only.
     *
     * @param name - Name of the user
     * @return Whether the data updated
     * 
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static int fetchUserBucksByName(String name) {
        int bucks = 0;
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `bucks` FROM `users` WHERE `lastname`=?;")) {
                statement.setString(1, name);
                try (ResultSet result = statement.executeQuery()) {
                    if(result.next()) {
                        bucks = result.getInt("bucks");
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchUserBucksByName(name,bucks)] SQLException occurred");
            e.printStackTrace();
            return -1;
        }

        return bucks;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param uuid - Unique Identifier of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean updateUserBucks(UUID uuid, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=? WHERE `uuid`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserBucks(uuid,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param name - Name of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean updateUserBucksByName(String name, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=? WHERE `lastname`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserBucksByName(name,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param name - Name of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean addUserBucksByName(String name, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=bucks+? WHERE `lastname`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserBucksByName(name,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param uuid - Unique Identifier of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean addUserBucks(UUID uuid, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=bucks+? WHERE `uuid`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserBucks(uuid,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param name - Name of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean subtractUserBucksByName(String name, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=bucks-? WHERE `lastname`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserBucksByName(name,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of bucks in 'users' only.
     *
     * @param uuid - Unique Identifier of the user
     * @param bucks - Amount of bucks
     * @return Whether the data updated
     * 
     * @deprecated - possibly unused as money is per server and this isn't a global currency.
     */
    @Deprecated
	public static boolean subtractUserBucks(UUID uuid, int bucks) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `bucks`=bucks-? WHERE `uuid`=?;")) {
                statement.setInt(1, bucks);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserBucks(uuid,bucks)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of tokens in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean updateUserTokens(UUID uuid, int tokens) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	CurrencyDAO.saveCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, tokens);
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserTokens(uuid,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will update the amount of tokens in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean updateUserTokensByName(String name, int tokens) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		CurrencyDAO.saveCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, tokens);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserTokensByName(name,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will add to the amount of tokens in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean addUserTokens(UUID uuid, int tokens) {
    	
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.addCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, tokens);
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserTokens(uuid,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This will add to the amount of tokens in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean addUserTokensByName(String name, int tokens) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.addCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, tokens);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserTokens(name,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will subtract to the amount of tokens in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean subtractUserTokens(UUID uuid, int tokens) {
    	
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.addCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, -1 * tokens);
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserTokens(uuid,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This will subtract to the amount of tokens in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param tokens - Amount of tokens
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean subtractUserTokensByName(String name, int tokens) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.addCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN, -1 * tokens);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserTokensByName(name,tokens)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will subtract to the amount of tokens in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static int getUserTokens(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.getCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN);
        } catch (SQLException e) {
            Core.error("[UserDAO:getUserTokens(uuid)] SQLException occurred");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This will subtract to the amount of tokens in 'user_currency' table.
     *
     * @param name - Name of the user
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static int getUserTokensByName(String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.getCurrency(connection, Currency.TOKEN.getServerKey(), uuid, Currency.TOKEN);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:getUserTokensByName(name)] SQLException occurred");
            e.printStackTrace();
        }
        
        return -1;
    }

    /**
     * This will update the amount of crowbars in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean updateUserCrowbars(UUID uuid, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.saveCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, crowbars);
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserCrowbars(uuid,crowbars)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * This will update the amount of crowbars in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean updateUserCrowbarsByName(String name, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.saveCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, crowbars);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserCrowbarsByName(name,crowbars)] SQLException occurred");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This will add to the amount of crowbars in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean addUserCrowbars(UUID uuid, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.addCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, crowbars);
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserCrowbars(uuid,crowbars)] SQLException occurred");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This will add to the amount of crowbars in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean addUserCrowbarsByName(String name, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.addCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, crowbars);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:addUserCrowbarsByName(name,crowbars)] SQLException occurred");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This will subtract to the amount of crowbars in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean subtractUserCrowbars(UUID uuid, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.addCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, -1 * crowbars);
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserCrowbars(uuid,crowbars)] SQLException occurred");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * This will subtract to the amount of crowbars in 'user_currency' table.
     *
     * @param name - Name of the user
     * @param crowbars - Amount of crowbars
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static boolean subtractUserCrowbarsByName(String name, int crowbars) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.addCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR, -1 * crowbars);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:subtractUserCrowbarsByName(name,crowbars)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * This will get the amount of crowbars in 'user_currency' table.
     *
     * @param uuid - Unique Identifier of the user
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static int getUserCrowbars(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return CurrencyDAO.getCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR);
        } catch (SQLException e) {
            Core.error("[UserDAO:getUserCrowbars(uuid,crowbars)] SQLException occurred");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * This will get the amount of crowbars in 'user_currency' table.
     *
     * @param name - Name of the user
     * @return Whether the data updated
     * 
     * @deprecated - Please see CurrencyDAO for how to use this global currency.
     */
    @Deprecated
	public static int getUserCrowbarsByName(String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	UUID uuid = UserDAO.getUuidByName(name);
        	if (uuid != null){
        		return CurrencyDAO.getCurrency(connection, Currency.CROWBAR.getServerKey(), uuid, Currency.CROWBAR);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:getUserCrowbarsByName(name,crowbars)] SQLException occurred");
            e.printStackTrace();
        }
        
        return -1;
    }

    public static boolean updateUserPref(UUID uuid, Pref pref, boolean status) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `" + pref.getDbName() + "`=? WHERE `uuid`=?;")) {
                statement.setBoolean(1, status);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserPref(uuid,pref,status)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserNametag(UUID uuid, String nametag, boolean add) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `nametags` SET `" + nametag + "`=? WHERE `uuid`=?;")) {
                statement.setInt(1, add ? 1 : 0);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserNametag(uuid,nametag,add)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastDonorReward(UUID uuid, String lastDonorReward) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastDonorReward`=? WHERE `uuid`=?;")) {
                statement.setString(1, lastDonorReward);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastDonorReward(uuid,lastDonorReward)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLastDonorRewardByName(String name, long lastDonorReward) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `lastDonorReward`=? WHERE `lastname`=?;")) {
                statement.setLong(1, lastDonorReward);
                statement.setString(2, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserLastDonorReward(uuid,lastDonorReward)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserIgnore(UUID uuid, String ignore) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `ignored`=? WHERE `uuid`=?;")) {
                statement.setString(1, ignore);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUserIgnore(uuid,ignore)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUnlockedAchievements(UUID uuid, String unlockedAchievements) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `unlockedAchievements`=? WHERE `uuid`=?;")) {
                statement.setString(1, unlockedAchievements);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateUnlockedAchievements(uuid,unlockedAchievements)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateShownAchievement(UUID uuid, String achievement) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `shownAchievement`=? WHERE `uuid`=?;")) {
                statement.setString(1, achievement);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:updateShownAchievement(uuid,achievement)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Note: This defaults to lookup the serverKey based on the serverKey of the "DEFAULT" rank.
     * 
     * @deprecated - Please see {@link #getRank(Connection, String, UUID)} for how to get a user rank properly. This exists for compatibility purposes only.
     */
    @Deprecated
	public static UserRank fetchUserRank(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	return getRank(connection, UserRank.DEFAULT.getServerKey(), uuid);
        } catch (SQLException e) {
            Core.error("[UserDAO:fetchUserRank(uuid)] SQLException occurred");
            e.printStackTrace();
            return UserRank.DEFAULT;
        }
    }

    public static int countUsers() {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT COUNT(1) AS rowcount FROM `users`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    if (result.next())
                        return result.getInt("rowcount");
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:countUsers()] SQLException occurred");
            e.printStackTrace();
            return 0;
        }

        return 0;
    }

    public static boolean isLastMonthsVoteWinner(String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `last_months_voters` WHERE `name`=?;")) {
                statement.setString(1, name);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next();
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:isLastMonthsVoteWinner(name)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @deprecated - Please see {@link #saveRank(Connection, String, UUID, UserRank)} for how to save a user rank properly. This exists for compatibility purposes only.
     */
    @Deprecated
	public static boolean updateRankByName(String name, UserRank rank) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
        	
        	UUID uuid = getUuidByName(name);
        	if (uuid != null){
        		return saveRank(connection, rank.getServerKey(), uuid, rank);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:updateRankByName(name,rank)] SQLException occurred");
            e.printStackTrace();
        }

        return false;
    }

    /**
     * @deprecated - Please see {@link #saveRank(Connection, String, UUID, UserRank)} for how to save a user rank properly. This exists for compatibility purposes only.
     */
    @Deprecated
	public static boolean updateRankByNameAndRank(String name, UserRank from, UserRank to) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            
        	UUID uuid = getUuidByName(name);
        	if (uuid != null){
        		// Note: This might not be the correct way of implementation for new system
        		return saveRank(connection, to.getServerKey(), uuid, to);
        	}
        } catch (SQLException e) {
            Core.error("[UserDAO:updateRankByNameAndRank(name,from,to)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isUserBanned(String uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `BAT_ban` WHERE `UUID`=?;")) {
                statement.setString(1, uuid);
                try (ResultSet result = statement.executeQuery()) {
                    return result.next() && result.getBoolean("ban_state");
                }
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:isUserBanned(uuid)] SQLException occurred");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * @deprecated due to database table changes, and therefore this method may not work. This method may not even be used.
     */
    @Deprecated
	public static boolean reset(String name) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `users` SET `tokens`=0,`bucks`=0,`votes`=0,`voteStreak`=0,`lastVoteStreak`=0,`dailyStreak`=0,`lastDailyReward`=0,`lastDonorReward`=0 WHERE `lastname`=?;")) {
                statement.setString(1, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:reset(name)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }
    
    /**
     * Get the UUID of a user based off their name.
     * 
     * @param conn - the database connection thread
     * @param name - the name of the user
     * 
     * @return The UUID that was found for the given name, 
     *     if more than one record exists, we return the first instance, 
     *     and log an error message. If none are found, we return null.
     */
	public static UUID getUUID(Connection conn, String name) {
    	
    	String query = "SELECT HEX(uuid) AS uuid FROM user WHERE name=?";
    	UUID uuid = null;
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, name);
            
            try (ResultSet result = ps.executeQuery()) {
            	if (result.next()) {
            		
            		// change from HEX into UUID object
					uuid = UUIDUtil.createUUID(result.getString("uuid")).orElse(null);

					// safe check to see if there's more than one result
					if (result.next()) {
						Core.error("[UserDAO:getUuidByName(name)] There exists more than one user with the name: " + name);
					}
				}
            }
        }
        catch (SQLException e) {
            Core.error("[UserDAO:getUuidByName(name)] SQLException occurred");
            e.printStackTrace();
        }
    	
    	return uuid;
    }

    /**
     * Get the UUID of a user based off their name.
     * 
     * @param name - the name of the user
     * 
     * @return The UUID that was found for the given name, 
     *     if more than one record exists, we return the first instance, 
     *     and log an error message. If none are found, we return null.
     * @deprecated - Please use {@link #getUUID(Connection, String)} instead.
     */
	@Deprecated
	public static UUID getUuidByName(String name) {
    	
    	try (Connection conn = BaseDatabase.getInstance().getConnection()) {
            return getUUID(conn, name);
        } 
        catch (SQLException e) {
            Core.error("[UserDAO:getUuidByName(name)] SQLException occurred");
            e.printStackTrace();
        }
    	
    	return null;
    }
	
	/**
	 * Get the name of the user with the specified uuid.
	 * 
	 * @param conn - the database connection thread
	 * @param uuid - the uuid of the user to get the name of
	 * 
	 * @return The string representation of the name for the user with the given uuid.
	 */
	public static String getName(Connection conn, UUID uuid) {
		
    	String query = "SELECT name FROM user WHERE uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "')";
    	
    	try (ResultSet result = conn.createStatement().executeQuery(query)) {
			if (result.next()) {
				String username = result.getString(1);
				return username;
			}
		}
        catch (SQLException e) {
            Core.error("[UserDAO:getNameByUuid(uuid)] SQLException occurred");
            e.printStackTrace();
        }
    	
        return null;
    }

	/**
	 * Get the name of the user with the specified uuid.
	 * 
	 * @param uuid - the uuid of the user to get the name of
	 * 
	 * @return The string representation of the name for the user with the given uuid.
	 * @deprecated - Please use {@link #getName(Connection, UUID)} instead.
	 */
	@Deprecated
	public static String getNameByUuid(UUID uuid) {
		
    	String query = "SELECT name FROM user WHERE uuid=UNHEX('" + uuid.toString().replaceAll("-", "") + "')";
    	
    	try (Connection conn = BaseDatabase.getInstance().getConnection()) {
    		return getName(conn, uuid);
    	}
        catch (SQLException e) {
            Core.error("[UserDAO:getNameByUuid(uuid)] SQLException occurred");
            e.printStackTrace();
        }
    	
        return null;
    }

    public static boolean deleteFromByName(String name, String table) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `" + table + "` WHERE `name`=?;")) {
                statement.setString(1, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[UserDAO:deleteFromByName(name,table)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateCountry(UUID uuid, String country) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET `country`=? WHERE `uuid`=?;")) {
                statement.setString(1, country);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean updateUserLocation(UUID uuid, CoreLocation location) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO user_location " +
                    "(uuid, country, city, country_code, isp, latitude, longitude, region, region_name, timezone, zip) " +
                    "VALUES " +
                    "(UNHEX(?), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE " +
                    "country = ?, " +
                    "city = ?, " +
                    "country_code = ?, " +
                    "isp = ?, " +
                    "latitude = ?, " +
                    "longitude = ?, " +
                    "region = ?, " +
                    "region_name = ?, " +
                    "timezone = ?, " +
                    "zip = ?" +
                    ";")
            ) {
                statement.setString(1, uuid.toString().replaceAll("-", ""));
                statement.setString(2, trimToSize(location.getCountry(), 16));
                statement.setString(3, trimToSize(location.getCity(), 16));
                statement.setString(4, trimToSize(location.getCountryCode(), 6));
                statement.setString(5, trimToSize(location.getIsp(), 16));
                statement.setString(6, trimToSize(location.getLat(), 10));
                statement.setString(7, trimToSize(location.getLon(), 10));
                statement.setString(8, trimToSize(location.getRegion(), 8));
                statement.setString(9, trimToSize(location.getRegionName(), 16));
                statement.setString(10, trimToSize(location.getTimezone(), 16));
                statement.setString(11, trimToSize(location.getZip(), 10));
                statement.setString(12, trimToSize(location.getCountry(), 16));
                statement.setString(13, trimToSize(location.getCity(), 16));
                statement.setString(14, trimToSize(location.getCountryCode(), 5));
                statement.setString(15, trimToSize(location.getIsp(), 16));
                statement.setString(16, trimToSize(location.getLat(), 10));
                statement.setString(17, trimToSize(location.getLon(), 10));
                statement.setString(18, trimToSize(location.getRegion(), 8));
                statement.setString(19, trimToSize(location.getRegionName(), 16));
                statement.setString(20, trimToSize(location.getTimezone(), 16));
                statement.setString(21, trimToSize(location.getZip(), 10));

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static CoreLocation getUserLocation(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM user_location WHERE uuid=UNHEX(?);")) {
                statement.setString(1, uuid.toString().replaceAll("-", ""));

                try (ResultSet result = statement.executeQuery()) {
                    if (result.next()) {
                        CoreLocation location = new CoreLocation();
                        location.setCountry(result.getString("country"));
                        location.setCountry(result.getString("city"));
                        location.setCountry(result.getString("country_code"));
                        location.setCountry(result.getString("isp"));
                        location.setCountry(result.getString("latitude"));
                        location.setCountry(result.getString("longitude"));
                        location.setCountry(result.getString("region"));
                        location.setCountry(result.getString("region_name"));
                        location.setCountry(result.getString("timezone"));
                        location.setCountry(result.getString("zip"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String trimToSize(String input, int size) {
        if (input.length() <= size) return input;
        return input.substring(0, size);
    }

    public static boolean updateLanguage(UUID uuid, String language) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE users SET `language`=? WHERE `uuid`=?;")) {
                statement.setString(1, language);
                statement.setString(2, uuid.toString());
                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static String[] getCountryAndLanguage(UUID uuid) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `country`,`language` FROM users WHERE `uuid`=?;")) {
                statement.setString(1, uuid.toString());
                try (ResultSet result = statement.executeQuery()) {
                    if(result.next()) {
                        return new String[]{result.getString("country"), result.getString("language")};
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     * Get the rank of the specified user on the given server_key.
     * 
     * @param conn - the database connection thread
     * @param serverKey - the server key
     * @param uuid - the uuid of the user to get the rank for
     * 
     * @return The rank of the user, if they have a rank, otherwise {@code null}.
     */
    public static UserRank getRank(Connection conn, String serverKey, UUID uuid){
    	
    	String query = "SELECT rank FROM user_profile WHERE uuid=UNHEX(?) AND server_key=?;";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		
    		try (ResultSet result = ps.executeQuery()){
    			if (result.next()){
    				return UserRank.getUserRankOrNull(result.getString("rank"));
    			}
    		}
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to getRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString());
			exc.printStackTrace();
		}
    	
    	return null;
    }
    
    /**
     * Get the highest rank we know about for this user on the global or server specific scale.
     * <p>
     * Note: This exists purely as a helper method and only should 
     * be suitable for if you read the method implementation. This
     * makes two calls to getRank() with "GLOBAL" and the SERVERKEY 
     * of this plugin to determine which is higher.
     * </p>
     * 
     * @param conn - the database connection thread
     * @param uuid - the uuid of the user
     * 
     * @return The highest rank, if any were found, for the specified user uuid.
     */
    public static UserRank getHighestRank(Connection conn, UUID uuid){
    	
    	UserRank globalRank = getRank(conn, "GLOBAL", uuid);
    	UserRank localRank = getRank(conn, Core.name().toUpperCase(), uuid);
    	
    	UserRank highest = null;
    	
    	// init highest if we can
    	if (globalRank != null){
    		highest = globalRank;
    	}
    	
    	// if local rank found
    	if (localRank != null){
    		// if no highest yet, this is default highest
    		if (highest == null || localRank.isHigherThan(highest)){
    			highest = localRank;
    		}
    	}
    	
    	return highest;
    }
    
    /**
     * Updates the rank of the user uuid in the database for the specified serverKey.
     * <p>
     * Note: The user rank must already exist for this to happen.
     * </p>
     * 
     * @param conn - the database connection thread
     * @param serverKey - the server key
     * @param uuid - the uuid of the user to save the rank for
     * @param rank - the rank to save
     * 
     * @return {@code true} if the query ran, {@code false} otherwise.
     */
    public static boolean saveRank(Connection conn, String serverKey, UUID uuid, UserRank rank){
    	
    	//String query = "UPDATE user_profile SET rank=? WHERE uuid=UNHEX(?) AND server_key=?;";
    	String query = "INSERT IGNORE INTO user_profile (uuid, server_key, rank) VALUES (UNHEX(?), ?, ?) ON DUPLICATE KEY UPDATE rank=VALUES(rank);";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		ps.setString(3, rank.getName());
    		
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to saveRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", rank=" + rank.getName());
			exc.printStackTrace();
			return false;
		}
    }
    
    /**
     * Creates a new entry in the user_profile table for a user with the specified rank on the specified server key.
     * 
     * @param conn - the database connection thread
     * @param serverKey - the key of the server
     * @param uuid - the uuid of the user
     * @param rank - the rank for that user
     * 
     * @return {@code true} if the create query ran, {@code false} otherwise.
     */
    public static boolean createRank(Connection conn, String serverKey, UUID uuid, UserRank rank){
    	
    	// if ANY rank exists already
    	boolean rankExists = false;
    	
    	String existQuery = "SELECT rank FROM user_profile WHERE uuid=UNHEX(?) AND server_key=?";
    	try (PreparedStatement ps = conn.prepareStatement(existQuery)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		
    		try (ResultSet result = ps.executeQuery()){
    			if (result.next()){
    				UserRank ur = UserRank.getUserRankOrNull(result.getString("rank"));
    				if (ur != null){
    					rankExists = true;
    				}
    			}
    		}
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to createRank(EXISTS) for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", rank=" + rank.getName());
			exc.printStackTrace();
			return false;
		}
    	
    	// if it doesn't exist in db yet
    	if (!rankExists){
    		
    		String query = "INSERT INTO user_profile (uuid, server_key, rank) VALUES (UNHEX(?), ?, ?);";
        	
        	try (PreparedStatement ps = conn.prepareStatement(query)){
        		ps.setString(1, uuid.toString().replaceAll("-", ""));
        		ps.setString(2, serverKey);
        		ps.setString(3, rank.getName());
        		
        		ps.executeUpdate();
        		return true;
        	}
        	catch (SQLException exc) {
    			Core.log("[UserDAO] Error attempting to createRank(CREATE) for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", rank=" + rank.getName());
    			exc.printStackTrace();
    			return false;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * Deletes a rank entry for the specified user, on the specified server key, with the specified rank.
     * <p>
     * If they do not have the given rank on the specific server key, nothing will happen.
     * </p>
     * 
     * @param conn - the database connection thread
     * @param serverKey - the key of the server
     * @param uuid - the uuid of the user
     * @param rank - the rank for that user
     * 
     * @return {@code true} if the create query ran, {@code false} otherwise.
     */
    public static boolean deleteRank(Connection conn, String serverKey, UUID uuid, UserRank rank){
    	
    	String query = "DELETE FROM user_profile WHERE uuid=UNHEX(?) AND server_key=? AND rank=?;";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		ps.setString(3, rank.getName());
    		
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to deleteRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", rank=" + rank.getName());
			exc.printStackTrace();
			return false;
		}
    }
    
    /**
     * Get the trial rank for the specified user.
     * 
     * @param conn - the database connection thread
     * @param serverKey - the server key lookup
     * @param uuid - the uuid of the user to lookup
     * 
     * @return The pair that represents their trial rank, and when it expires, if one exists.
     */
    public static Pair<UserRank, Timestamp> getTrialRank(Connection conn, String serverKey, UUID uuid){
    	
    	String query = "SELECT rank, expire_at FROM user_trial_rank WHERE uuid=UNHEX(?) AND server_key=?;";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		
    		try (ResultSet result = ps.executeQuery()){
    			if (result.next()){
    				UserRank trialRank = UserRank.getUserRankOrNull(result.getString("rank"));
    				if (trialRank != null){
    					Timestamp expireAt = result.getTimestamp("expire_at");	
    					return Pair.of(trialRank, expireAt);
    				}
    			}
    		}
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to getTrialRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString());
			exc.printStackTrace();
		}
    	
    	return null;
    }
    
    public static void saveTrialRank(Connection conn, String serverKey, UUID uuid, UserRank trialRank, Timestamp expireAt){
    	
    	// TODO is this even needed?
    }
    
    /**
     * Creates a trial rank record in the database for the specified user/serverKey combo.
     * <p>
     * Note: This query will fail if the user already has a trial rank in the database for that serverKey.
     * </p>
     * 
     * @param conn - the database connection thread
     * @param serverKey - the server key to update
     * @param uuid - the uuid of the user
     * @param trialRank - the trial rank they have
     * @param expireAt - when the rank expires
     * 
     * @return {@code true} if the trial rank query ran, {@code false} otherwise.
     */
    public static boolean createTrialRank(Connection conn, String serverKey, UUID uuid, UserRank trialRank, Timestamp expireAt){
    	
    	String query = "INSERT INTO user_trial_rank (uuid, server_key, rank, expire_at) VALUES (UNHEX(?), ?, ?, ?);";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		ps.setString(3, trialRank.getName());
    		ps.setTimestamp(4, expireAt);
    		
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to createTrialRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString() + ", trialRank=" + trialRank.getName() + ", expireAt=" + expireAt);
			exc.printStackTrace();
			return false;
		}
    }
    
    /**
     * Removes the trial rank for the specified uuid on the server key.
     * 
     * @param conn - the database connection thread 
     * @param serverKey - the server key to delete it for
     * @param uuid - the uuid of the user to remove
     * 
     * @return {@code true} if the query ran, {@code false} otherwise.
     */
    public static boolean removeTrialRank(Connection conn, String serverKey, UUID uuid){
    	
    	String query = "DELETE FROM user_trial_rank WHERE uuid=UNHEX(?) AND server_key=?;";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.setString(1, uuid.toString().replaceAll("-", ""));
    		ps.setString(2, serverKey);
    		
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to removeTrialRank() for serverKey=" + serverKey + ", uuid=" + uuid.toString());
			exc.printStackTrace();
			return false;
		}
    }
    
    /**
     * Remove all expired trial ranks from the "user_trial_rank" table.
     * <p>
     * Note: This is a costly query if there are a lot of trial ranks, so we should run this periodically.
     * </p>
     * 
     * @param conn - the database connection thread
     * 
     * @return {@code true} if the query ran, {@code false} otherwise.
     */
	public static boolean removeAllExpiredTrialRanks(Connection conn){
    	
    	String query = "DELETE FROM user_trial_rank WHERE expire_at < NOW();";
    	
    	try (PreparedStatement ps = conn.prepareStatement(query)){
    		ps.executeUpdate();
    		return true;
    	}
    	catch (SQLException exc) {
			Core.log("[UserDAO] Error attempting to removeAllExpiredTrialRanks()");
			exc.printStackTrace();
			return false;
		}
    }
}
