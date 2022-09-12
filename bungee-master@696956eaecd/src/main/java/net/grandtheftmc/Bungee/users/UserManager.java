package net.grandtheftmc.Bungee.users;

import com.google.common.collect.Maps;
import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.grandtheftmc.Bungee.utils.Callback;
import net.grandtheftmc.Bungee.utils.UUIDUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class UserManager {

    private final Map<UUID, User> loadedUsers = new HashMap<>();

    /**
     * Default set of permissions, loaded from configuration
     *
     * @see #loadPerms()
     */
    private final Map<UserRank, List<String>> perms = new HashMap<>();

    public UserManager() {
        this.loadPerms();
        this.loadUsers();
    }

    /**
     * Load permissions ranks from configuration file.
     */
    public void loadPerms() {
        Configuration c = Bungee.getSettings().getPermsConfig();
        for (String s : c.getKeys()) {
            UserRank rank = UserRank.getUserRankOrNull(s);
            if (rank != null) this.perms.put(rank, c.getStringList(s));
        }
        this.setPerms();
    }

    /**
     * Load a default set of users into redis memory from MySQL storage.
     * <p>
     * The default set is of all staff members, additional users are loaded on demand.
     */
    public void loadUsers() {
        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                this.step1(connection, obj -> {
                    this.step2(connection, obj);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }

//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                try (PreparedStatement statement = connection.prepareStatement("SELECT HEX(uuid) AS uid,rank FROM `user_profile` WHERE rank IN ('builder', 'helpop', 'mod', 'srmod', 'admin', 'dev', 'manager', 'owner');")) {
//                    try (ResultSet result = statement.executeQuery()) {
//                        HashMap<UUID, UserRank> map = Maps.newHashMap();
//                        while (result.next()) {
//                            UUID u = UUIDUtil.createUUID(result.getString("uid")).orElse(null);
//                            if (u == null) continue;
//                            map.put(u, UserRank.getUserRank(result.getString("rank")));
//                        }
//
//                        UUID uuid = null;
//                        for (UUID uid : map.keySet()) {
//                            uuid = uid;
//                            try (PreparedStatement statement2 = connection.prepareStatement("SELECT lastname,socialSpy FROM `users` WHERE `uuid`=?;")) {
//                                statement2.setString(1, uid.toString());
//                                try (ResultSet result2 = statement2.executeQuery()) {
//                                    if (result2.next()) {
//                                        String username = result2.getString("lastname");
//                                        boolean socialSpy = result2.getBoolean("socialSpy");
//
//                                        //Add user key to hashmap.
//                                        User user = this.loadedUsers.computeIfAbsent(uid, k -> {
//                                            User u = new User(k);
//                                            Bungee.getInstance().getLogger().info("Successfully cached user " + u.getUUID());
//                                            return u;
//                                        });
//
//                                        //Set ranks and other miscellaneous data.
//                                        user.setUserRank(map.get(uid));
//                                        user.setSocialSpy(socialSpy);
//                                        user.setUsername(username);
//                                    }
//                                }
//                            } catch (SQLException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        ResultSet rs = Bungee.getSQL().query("SELECT authyId,lastIPAddress FROM Authy WHERE uuid='" + uuid.toString() + "' LIMIT 1;");
//                        Optional<User> user = getLoadedUser(uuid);
//                        if (!user.isPresent()) {
//                            rs.close();
//                            return;
//                        }
//
//                        int authyId = 0;
//                        String ipAddress = "0";
//                        if (rs.next()) {
//                            authyId = rs.getInt("authyId");
//                            ipAddress = rs.getString("lastIPAddress");
//                        }
//                        user.get().setAuthyId(authyId);
//                        user.get().setLastIPAddress(ipAddress);
//                        rs.close();
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }

//            try {
//                ResultSet resultSet = Bungee.getSQL().query("SELECT HEX(uuid) AS uid,rank FROM `user_profile` WHERE rank IN ('builder', 'helpop', 'mod', 'srmod', 'admin', 'dev', 'manager', 'owner');");
//                HashMap<UUID, UserRank> map = Maps.newHashMap();
//                while (resultSet.next()) {
//                    UUID u = UUIDUtil.createUUID(resultSet.getString("uid")).orElse(null);
//                    if (u == null) continue;
//                    map.put(u, UserRank.getUserRank(resultSet.getString("rank")));
//                }
//                resultSet.close();

//                UUID uuid = null;
//                for (UUID uid : map.keySet()) {
//                    uuid = uid;
//                    try (PreparedStatement statement = Bungee.getSQL().prepareStatement("SELECT lastname,socialSpy FROM `users` WHERE `uuid`=?;")) {
//                        statement.setString(1, uid.toString());
//                        try (ResultSet set = statement.executeQuery()) {
//                            if (set.next()) {
//                                String username = set.getString("lastname");
//                                boolean socialSpy = set.getBoolean("socialSpy");
//
//                                //Add user key to hashmap.
//                                User user = this.loadedUsers.computeIfAbsent(uid, k -> {
//                                    User u = new User(k);
//                                    Bungee.getInstance().getLogger().info("Successfully cached user " + u.getUUID());
//                                    return u;
//                                });
//
//                                //Set ranks and other miscellaneous data.
//                                user.setUserRank(map.get(uid));
//                                user.setSocialSpy(socialSpy);
//                                user.setUsername(username);
//                            }
//                        }
//                    } catch (SQLException e) {
//                        e.printStackTrace();
//                    }
//                }

//                ResultSet rs = Bungee.getSQL().query("SELECT authyId,lastIPAddress FROM Authy WHERE uuid='" + uuid.toString() + "' LIMIT 1;");
//                Optional<User> user = getLoadedUser(uuid);
//                if (!user.isPresent()) {
//                    rs.close();
//                    return;
//                }
//
//                int authyId = 0;
//                String ipAddress = "0";
//                if (rs.next()) {
//                    authyId = rs.getInt("authyId");
//                    ipAddress = rs.getString("lastIPAddress");
//                }
//                user.get().setAuthyId(authyId);
//                user.get().setLastIPAddress(ipAddress);
//                rs.close();
//            } catch (SQLException exception) {
//                exception.printStackTrace();
//            }
        });
    }

    private void step1(Connection connection, Callback<HashMap<UUID, UserRank>> callback) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT HEX(uuid) AS uid,rank FROM `user_profile` WHERE rank IN ('builder', 'helpop', 'mod', 'srmod', 'admin', 'dev', 'manager', 'owner');")) {
            try (ResultSet result = statement.executeQuery()) {
                HashMap<UUID, UserRank> map = Maps.newHashMap();
                while (result.next()) {
                    UUID u = UUIDUtil.createUUID(result.getString("uid")).orElse(null);
                    if (u == null) continue;
                    map.put(u, UserRank.getUserRank(result.getString("rank")));
                }

                callback.call(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void step2(Connection connection, HashMap<UUID, UserRank> map) {
        UUID uuid;
        for (UUID uid : map.keySet()) {
            uuid = uid;
            try (PreparedStatement statement2 = connection.prepareStatement("SELECT lastname,socialSpy FROM `users` WHERE `uuid`=?;")) {
                statement2.setString(1, uid.toString());
                try (ResultSet result2 = statement2.executeQuery()) {
                    if (result2.next()) {
                        String username = result2.getString("lastname");
                        boolean socialSpy = result2.getBoolean("socialSpy");

                        //Add user key to hashmap.
                        User user = this.loadedUsers.computeIfAbsent(uid, k -> {
                            User u = new User(k);
                            Bungee.getInstance().getLogger().info("Successfully cached user " + u.getUUID());
                            return u;
                        });

                        //Set ranks and other miscellaneous data.
                        user.setUserRank(map.get(uid));
                        user.setSocialSpy(socialSpy);
                        user.setUsername(username);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            this.step3(connection, uuid);
        }
    }

    private void step3(Connection connection, UUID uuid) {
        try (PreparedStatement statement2 = connection.prepareStatement("SELECT authyId,lastIPAddress FROM Authy WHERE uuid='" + uuid.toString() + "' LIMIT 1;")) {
            try (ResultSet result = statement2.executeQuery()) {
                Optional<User> user = getLoadedUser(uuid);
                if (!user.isPresent()) {
                    result.close();
                    return;
                }

                int authyId = 0;
                String ipAddress = "0";
                if (result.next()) {
                    authyId = result.getInt("authyId");
                    ipAddress = result.getString("lastIPAddress");
                }
                user.get().setAuthyId(authyId);
                user.get().setLastIPAddress(ipAddress);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setPerms() {
        for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
            this.getLoadedUser(player.getUniqueId()).ifPresent(user -> this.setPerms(player, user.getUserRank()));
        }
    }

    public void setPerms(ProxiedPlayer player, UserRank rank) {
        if (rank == null) return;
        for (String perm : new ArrayList<>(player.getPermissions()))
            player.setPermission(perm, false);

        for (String perm : this.getPermsAndLower(rank))
            player.setPermission(perm, true);
    }

    private List<String> getPermsAndLower(UserRank rank) {
        List<String> perms = new ArrayList<>();
        if (rank != null)
            for (UserRank r : UserRank.values()) {
                List<String> l = this.perms.get(r);
                if (l != null && !l.isEmpty())
                    perms.addAll(l);
                if (r == rank) break;
            }
        return perms;
    }

    public Map<UUID, User> getLoadedUsersMap() {
        return loadedUsers;
    }

    public Collection<User> getLoadedUsers() {
        return this.loadedUsers.values();
    }

    public List<User> getSortedUsers() {
        Collection<User> users = Bungee.getUserManager().getLoadedUsers();
        List<User> userList = new ArrayList<>();
        for (UserRank userRank : UserRank.values()) {
            userList.addAll(users.stream().filter(user -> user.getUserRank() == userRank).collect(Collectors.toList()));
        }
        return userList;
    }

    /**
     * Get the User object of the player with the specified UUID.
     *
     * @param uuid
     * @return
     */
    public Optional<User> getLoadedUser(UUID uuid) {
        if (uuid == null) return null;
        return Optional.ofNullable(this.loadedUsers.get(uuid));
    }

    /**
     * Get the User object of the player with the specified username.
     *
     * @param username
     * @return
     */
    public Optional<User> getLoadedUser(String username) {
        if (username == null) return null;
        List<User> users = new ArrayList<>();
        this.loadedUsers.forEach((uuid, user) -> {
            if (user.getUsername().equalsIgnoreCase(username)) {
                users.add(user);
                return;
            }
        });
        return users.isEmpty() ? Optional.empty() : Optional.of(users.get(0));
    }
}
