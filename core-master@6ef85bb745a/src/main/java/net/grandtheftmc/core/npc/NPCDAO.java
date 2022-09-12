package net.grandtheftmc.core.npc;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.database.BaseDatabase;
import org.bukkit.Location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by Timothy Lampen on 1/14/2018.
 */
public class NPCDAO {

    public static boolean loadNPCs() {
        String query = "SELECT * FROM npc_record WHERE server_key = ?;";
        try (Connection c = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement s = c.prepareStatement(query)) {
                s.setString(1, Core.name());
                try (ResultSet set = s.executeQuery()) {
                    while (set.next()) {
                        String reference = set.getString("reference");
                        String serializedLoc = set.getString("location");
                        ServerUtil.runTask(() -> Core.getNPCManager().load(reference, Utils.teleportLocationFromString(serializedLoc)));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean clearRecords(){
        String delete = "DELETE FROM npc_record WHERE server_key = ?";//incase an npc was deleted from the list, you don't want to load it in again.
        try (Connection c = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement s = c.prepareStatement(delete)){
                s.setString(1, Core.name());
                s.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean saveNPC(String reference, Location loc) {

        String add = "INSERT INTO npc_record(server_key, reference, location) VALUES (?,?,?);";
        try (Connection c = BaseDatabase.getInstance().getConnection()){
            try(PreparedStatement s = c.prepareStatement(add)) {
                s.setString(1, Core.name());
                s.setString(2, reference);
                s.setString(3, Utils.teleportLocationToString(loc));
                s.execute();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
