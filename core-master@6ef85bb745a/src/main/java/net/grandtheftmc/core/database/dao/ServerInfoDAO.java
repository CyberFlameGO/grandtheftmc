package net.grandtheftmc.core.database.dao;

import com.google.common.collect.Lists;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.servers.Server;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.UserRank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ServerInfoDAO {

    public static Optional<List<Server>> fetchAllServers() {
        List<Server> servers = Lists.newArrayList();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM `servers`;")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        servers.add(new Server(result.getString("name"), ServerType.getType(result.getString("type")),
                                result.getInt("number"), result.getString("ip"), result.getInt("port"),
                                UserRank.getUserRankOrNull(result.getString("rankToJoin"))));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[ServerInfoDAO:fetchAllServers()] SQLException occurred");
            e.printStackTrace();
            return Optional.empty();
        }

        return Optional.of(servers);
    }

    public static boolean updateServerInfo(String name, ServerType type, int number, int onlinePlayers, int maxPlayers, String map, String gameState, int round, UserRank rankToJoin) {
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("UPDATE `servers` SET " +
                    "`type`=?,`number`=?,`onlinePlayers`=?,`maxPlayers`=?,`map`=?,`gameState`=?,`round`=?,`rankToJoin`=?,`lastCheck`=? WHERE `name`=?;")) {
                statement.setString(1, type.name());
                statement.setInt(2, number);
                statement.setInt(3, onlinePlayers);
                statement.setInt(4, maxPlayers);
                statement.setString(5, map);
                statement.setString(6, gameState);
                statement.setInt(7, round);
                statement.setString(8, rankToJoin == null ? "DEFAULT" : rankToJoin.name());
                statement.setLong(9, System.currentTimeMillis());
                statement.setString(10, name);
                statement.execute();
            }
        } catch (SQLException e) {
            Core.error("[ServerInfoDAO:updateServerInfo(...)] SQLException occurred");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static Collection<String> getOnlineStaff() {
        Collection<String> collection = Lists.newArrayList();
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("SELECT `lastname` FROM `users` WHERE `userrank` IN ('HELPOP','MOD','ADMIN','DEV');")) {
                try (ResultSet result = statement.executeQuery()) {
                    while (result.next()) {
                        collection.add(result.getString("lastname"));
                    }
                }
            }
        } catch (SQLException e) {
            Core.error("[ServerInfoDAO:getOnlineStaff()] SQLException occurred");
            e.printStackTrace();
            return Lists.newArrayList();
        }

        return collection;
    }
}
