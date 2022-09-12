package net.grandtheftmc.Bungee.users;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class User {
    private final UUID uuid;
    private String username;
    private UserRank ur;
    private int authyId = 0;
    private boolean authyVerified;
    private String lastIPAddress = "0";

    private boolean staffChat;
    private boolean socialSpy;
    private long lastJoin;
    private long lastQuit;
    private long playtime;

    public User(UUID uuid) {
        this.uuid = uuid;
        this.dataCheck();
    }

    public void dataCheck() {
        this.update();

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("INSERT INTO Authy (uuid,authyId) VALUES (?, ?) ON DUPLICATE KEY UPDATE uuid=?;")) {
                statement.setString(1, this.uuid.toString());
                statement.setInt(2, this.authyId);
                statement.setString(3, this.uuid.toString());

                statement.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRank getUserRank() {
        return this.ur;
    }

    public void setUserRank(UserRank ur) {
        this.ur = ur;
    }

    public boolean isRank(UserRank userRank) {
        return userRank == this.ur || this.ur.isHigherThan(userRank);
    }

    public boolean isSpecial() {
        return this.ur != UserRank.DEFAULT;
    }

    public int getAuthyId() {
        return this.authyId;
    }

    public void setAuthyId(int authyId) {
        this.authyId = authyId;

        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE Authy SET authyId=? WHERE uuid=?;")) {
                    statement.setInt(1, this.authyId);
                    statement.setString(2, this.uuid.toString());
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isAuthyVerified() {
        if (!this.isRank(UserRank.BUILDER)) return true;
        return authyVerified;
    }

    public void setAuthyVerified(boolean authyVerified) {
        this.authyVerified = authyVerified;
    }

    public String getLastIPAddress() {
        return this.lastIPAddress;
    }

    public void setLastIPAddress(String lastIPAddress) {
        this.lastIPAddress = lastIPAddress;

        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("UPDATE Authy SET lastIPAddress=? WHERE uuid=?;")) {
                    statement.setString(1, this.lastIPAddress);
                    statement.setString(2, this.uuid.toString());
                    statement.execute();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean getStaffChat() {
        return this.staffChat;
    }

    public void setStaffChat(boolean b) {
        this.staffChat = b;
    }

    public void toggleStaffChat() {
        this.staffChat = !this.staffChat;
    }

    public boolean getSocialSpy() {
        return this.socialSpy;
    }

    public void setSocialSpy(boolean b) {
        this.socialSpy = b;
    }

    public Long getLastJoin() {
        return this.lastJoin;
    }

    public void setLastJoin(Long lastJoin) {
        this.lastJoin = lastJoin;
    }

    public long getLastQuit() {
        return this.lastQuit;
    }

    public void setLastQuit(long lastQuit) {
        this.lastQuit = lastQuit;
    }

    public Long getPlaytime() {
        return this.playtime;
    }

    public void setPlaytime(Long playtime) {
        this.playtime = playtime;
    }

    public String getColoredName(ProxiedPlayer player) {
        return this.ur.getColor() + (this.ur == UserRank.DEFAULT ? "" : "&l") + player.getName();
    }

    public String getColoredName() {
        return this.ur.getColor() + (this.ur == UserRank.DEFAULT ? "" : "&l") + this.username;
    }

    public void update() {
        Bungee.getInstance().getProxy().getScheduler().runAsync(Bungee.getInstance(), () -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT UP.rank, U.name FROM user_profile UP, user U WHERE UP.uuid=UNHEX(?) AND UP.uuid=U.uuid;")) {
                    statement.setString(1, this.uuid.toString().replaceAll("-", ""));
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            UserRank ur = UserRank.getUserRank(result.getString("rank"));
                            if (!ur.isHigherThan(UserRank.YOUTUBER)) {
                                Bungee.getUserManager().getLoadedUsers().remove(this);
                                return;
                            }
                            this.ur = ur;
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                try (PreparedStatement statement = connection.prepareStatement("SELECT authyId,lastIPAddress FROM Authy WHERE uuid=? LIMIT 1;")) {
                    statement.setString(1, this.uuid.toString());
                    try (ResultSet result = statement.executeQuery()) {
                        if (result.next()) {
                            this.authyId = result.getInt("authyId");
                            this.lastIPAddress = result.getString("lastIPAddress");
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }
}
