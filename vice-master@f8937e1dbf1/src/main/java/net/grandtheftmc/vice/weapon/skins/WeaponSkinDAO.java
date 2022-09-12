package net.grandtheftmc.vice.weapon.skins;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;

public class WeaponSkinDAO {
    public static boolean lockSkin(Connection connection, UUID uuid, Weapon<?> weapon, WeaponSkin skin) {
        String query = "DELETE FROM user_weapon_skin WHERE weapon_id=? AND skin_id=? AND uuid=UNHEX(?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setShort(1, weapon.getUniqueIdentifier());
            statement.setShort(2, (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
            statement.setString(3, uuid.toString().replaceAll("-", ""));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }
    
    public static boolean unlockSkin(Connection connection, UUID uuid, Weapon<?> weapon, WeaponSkin skin) {
        String query = "INSERT INTO user_weapon_skin (uuid, server_key, weapon_id, skin_id) VALUES (UNHEX(?), ?, ?, ?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, uuid.toString().replaceAll("-", ""));
            statement.setString(2, Core.name().toUpperCase());
            statement.setShort(3, weapon.getUniqueIdentifier());
            statement.setShort(4, (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static boolean enableSkin(Connection connection, UUID uuid, Weapon<?> weapon, short skinID) {
        String query = "UPDATE user_weapon_skin SET enabled=1 WHERE server_key=? AND weapon_id=? AND skin_id=? AND uuid=UNHEX(?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setShort(2, weapon.getUniqueIdentifier());
            statement.setShort(3, skinID);
            statement.setString(4, uuid.toString().replaceAll("-", ""));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static boolean disableSkin(Connection connection, UUID uuid, Weapon<?> weapon, short skinID) {
        String query = "UPDATE user_weapon_skin SET enabled=0 WHERE server_key=? AND weapon_id=? AND skin_id=? AND uuid=UNHEX(?);";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setShort(2, weapon.getUniqueIdentifier());
            statement.setShort(3, skinID);
            statement.setString(4, uuid.toString().replaceAll("-", ""));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public static Map<Short, List<Short>> getUnlockedSkins(Connection connection, UUID uuid) {
        Map<Short, List<Short>> skins = new HashMap<Short, List<Short>>();
        String query = "SELECT * FROM user_weapon_skin WHERE server_key=? AND uuid=UNHEX(?);";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            
            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    short weaponID = result.getShort("weapon_id");
                    
                    if (skins.containsKey(weaponID)) {
                        skins.get(weaponID).add(result.getShort("skin_id"));
                    } else {
                        skins.put(weaponID, new ArrayList<Short>());
                        skins.get(weaponID).add(result.getShort("skin_id"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skins;
    }

    public static Map<Short, Short> getEquippedSkins(Connection connection, UUID uuid) {
        Map<Short, Short> skins = new HashMap<Short, Short>();
        String query = "SELECT * FROM user_weapon_skin WHERE server_key=? AND uuid=UNHEX(?) AND enabled=?;";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            statement.setShort(3, (short) 1);

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                    skins.put(result.getShort("weapon_id"), result.getShort("skin_id"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return skins;
    }
}