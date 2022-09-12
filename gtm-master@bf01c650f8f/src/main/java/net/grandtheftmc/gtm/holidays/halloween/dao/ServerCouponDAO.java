package net.grandtheftmc.gtm.holidays.halloween.dao;

import net.grandtheftmc.core.database.BaseDatabase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Timothy Lampen on 2017-10-15.
 */
public class ServerCouponDAO {

    public static boolean deleteServerCoupon(UUID uuid) {
        String query = "UPDATE server_coupons SET creationTime=0 WHERE uuid=?;";
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean deleteServerCoupon(int couponID) {
        String query = "UPDATE server_coupons SET creationTime=0 WHERE couponID=?;";
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, couponID);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static List<ServerCoupon> getAllServerCoupons() {
        String query = "SELECT * FROM server_coupons;";
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try(ResultSet set = connection.prepareStatement(query).executeQuery()) {
                List<ServerCoupon> coupons = new ArrayList<>();
                while (set.next()) {
                    int couponID = set.getInt("couponID");
                    long creationTime = set.getLong("creationTime");
                    String couponName = set.getString("couponName");
                    coupons.add(new ServerCoupon(couponID, couponName, creationTime));
                }
                return coupons;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }

    public static Optional<ServerCoupon> getServerCoupon(UUID uuid) {
        String query = "SELECT * FROM server_coupons WHERE UUID=?;";
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                try (ResultSet set = ps.executeQuery()) {
                    if (!set.next()) return Optional.empty();
                    int couponID = set.getInt("couponID");
                    long creationTime = set.getLong("creationTime");
                    String couponName = set.getString("couponName");
                    return Optional.of(new ServerCoupon(couponID, couponName, creationTime));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static boolean setServerCoupon(UUID uuid, int couponID, String couponName, long creationTime) {
        String query = "INSERT INTO server_coupons (uuid, couponID, couponName, creationTime) VALUES (?,?,?,?);";
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try(PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, couponID);
                ps.setString(3, couponName);
                ps.setLong(4, creationTime);
                ps.executeUpdate();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
