package net.grandtheftmc.Creative.users;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class CreativeUser {

    private final UUID uuid;
    private CreativeRank rank;
    private boolean hasUpdated;

    public CreativeUser(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void dataCheck(String name, UserRank rank) {
//        sql.update("insert into " + Core.name() + "(uuid,name) values('" + this.uuid + "','" + name
//                + "') on duplicate key update name='" + name + "';");
//        sql.update("update " + Core.name() + " set name='ERROR' where name='" + name + "' and uuid!='" + this.uuid
//                + "';");

        BaseDatabase.runCustomQuery("insert into " + Core.name() + "(uuid,name) values('" + this.uuid + "','" + name
                + "') on duplicate key update name='" + name + "';");
        BaseDatabase.runCustomQuery("update " + Core.name() + " set name='ERROR' where name='" + name + "' and uuid!='" + this.uuid + "';");
    }

    public boolean updateDataFromDb() {
        boolean b = true;
//        try(ResultSet rs = sql.query("select * from " + Core.name() + " where uuid='" + this.uuid + "' LIMIT 1;")) {
//            if (rs.next()) {
//                this.rank = CreativeRank.fromString(rs.getString("rank"));
//            } else
//                b = false;
//            rs.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            b = false;
//        }

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + " where uuid='" + this.uuid + "' LIMIT 1;")) {
                try (ResultSet result = statement.executeQuery()) {
                    if(result.next()) this.rank = CreativeRank.fromString(result.getString("rank"));
                    else b = false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            b = false;
        }

        this.hasUpdated = b;
        return b;
    }

    public CreativeRank getRank() {
        return this.rank;
    }

    public boolean isRank(CreativeRank rank) {
        return !(rank == null || this.rank == null) && (this.rank == rank || this.rank.isHigherThan(rank));
    }

    public void setRank(CreativeRank r, Player player, User u) {
        this.rank = r;

//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set rank='" + r.getName() + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set rank='" + r.getName() + "' where uuid='" + this.uuid + "';"));

        NametagManager.updateNametag(Bukkit.getPlayer(this.uuid));
        u.setPerms(player);
    }


    public boolean hasUpdated() {
        return this.hasUpdated;
    }
}