package net.grandtheftmc.gtm.gang;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import com.google.common.collect.Sets;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.UUIDUtil;
import net.grandtheftmc.gtm.gang.member.GTMGangMember;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.gang.relation.GTMGangRelation;
import net.grandtheftmc.gtm.gang.relation.GangRelation;
import net.grandtheftmc.gtm.gang.relation.RelationType;

public class GangDAO {

    public static Gang createGang(Connection connection, UUID owner, String name, int maxMembers) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `gtm_gang` (`server_key`,`name`,`owner`,`description`,`max_members`) VALUES (?,?,UNHEX(?),?,?);", Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, Core.name().toUpperCase());
            statement.setString(2, name);
            statement.setString(3, owner.toString().replaceAll("-", ""));
            statement.setString(4, "Default gang description.");
            statement.setInt(5, maxMembers);

            statement.executeUpdate();

            try (ResultSet result = statement.getGeneratedKeys()) {
                if (result.next()) {
                    return new GTMGang(result.getInt(1), owner, name, null, maxMembers);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Gang deleteGang(Connection connection, int id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM gtm_gang WHERE id=?;")) {
            statement.setInt(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean setName(Connection connection, String name, int id) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `gtm_gang` SET `name`=? WHERE `id`=?;")) {
            statement.setString(1, name);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setDescription(Connection connection, String desc, int id) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `gtm_gang` SET `description`=? WHERE `id`=?;")) {
            statement.setString(1, desc);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setOwner(Connection connection, UUID owner, int id) {
    	
    	if (owner == null){
    		Core.log("[GangDAO] Cannot set owner for id=" + id + " as the owner is null.");
    		return false;
    	}
    	
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `gtm_gang` SET `owner`=UNHEX(?) WHERE `id`=?;")) {
            statement.setString(1, owner.toString().replaceAll("-", ""));
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setMemberRole(Connection connection, UUID uuid, GangRole role, int id) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `gtm_gang_member` SET `role`=? WHERE `gang_id`=? AND uuid=UNHEX(?);")) {
            statement.setInt(1, role.getRankId());
            statement.setInt(2, id);
            statement.setString(3, uuid.toString().replaceAll("-", ""));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean addMember(Connection connection, UUID uuid, @Nullable GangRole role, int id) {
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `gtm_gang_member` (`gang_id`,`uuid`,`role`) VALUES (?,UNHEX(?),?);")) {
            statement.setInt(1, id);
            statement.setString(2, uuid.toString().replaceAll("-", ""));
            statement.setInt(3, role == null ? 1 : role.getRankId());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeMember(Connection connection, UUID owner, int id) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `gtm_gang_member` WHERE gang_id=? AND uuid=UNHEX(?);")) {
            statement.setInt(1, id);
            statement.setString(2, owner.toString().replaceAll("-", ""));

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public static boolean addRelation(Connection connection, int id, int otherId, RelationType relationType) {
        if (relationType == RelationType.NEUTRAL) return false;

        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO `gtm_gang_relation` (`gang_id`,`other_id`,`relation`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE relation=?;")) {
            statement.setInt(1, id);
            statement.setInt(2, otherId);
            statement.setString(3, relationType.getKey());
            statement.setString(4, relationType.getKey());

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean removeRelation(Connection connection, int id, int otherId) {
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM `gtm_gang_relation` WHERE (gang_id=? AND other_id=?) OR (other_id=? AND gang_id=?);")) {
            statement.setInt(1, id);
            statement.setInt(2, otherId);
            statement.setInt(3, id);
            statement.setInt(4, otherId);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean setMaxMembers(Connection connection, int maxMembers, int id) {
        try (PreparedStatement statement = connection.prepareStatement("UPDATE `gtm_gang` SET `max_members`=? WHERE `gang_id`=?;")) {
            statement.setInt(1, maxMembers);
            statement.setInt(2, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static boolean isGangExisting(Connection connection, String name) {
        try (PreparedStatement statement = connection.prepareStatement("SELECT `name` FROM `gtm_gang` WHERE `server_key`=? AND (name REGEXP '.*" + name + ".*');")) {
            statement.setString(1, Core.name().toUpperCase());
//            statement.setString(2, name);

            try (ResultSet result = statement.executeQuery()) {
                return result.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static Gang getGang(Connection connection, int id) {
        String query = "SELECT id,HEX(owner) as leader,name,description,max_members FROM gtm_gang WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Gang gang = new GTMGang(result.getInt("id"));
                    gang.setName(result.getString("name"));
                    UUIDUtil.createUUID(result.getString("leader")).ifPresent(gang::setOwner);
                    gang.setDescription(result.getString("description"));
                    gang.setMaxMembers(result.getInt("max_members"));
                    return gang;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<Gang> getGangs(Connection connection) {
        Set<Gang> list = Sets.newHashSet();
        final String query = "SELECT id,HEX(owner) as leader,name,description,max_members FROM gtm_gang WHERE server_key=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, Core.name().toUpperCase());

            try (ResultSet result = statement.executeQuery()) {
                while (result.next()) {
                	int gangID = result.getInt("id");
                    Gang gang = new GTMGang(gangID);
                    gang.setName(result.getString("name"));
                    String leader = result.getString("leader");
                    if (leader != null){
                    	UUID leaderUUID = UUIDUtil.createUUID(leader).orElse(null);
                    	if (leaderUUID != null){
                    		gang.setOwner(leaderUUID);
                    	}
                    	else{
                    		Core.log("[GangDAO] Unable to set owner of gang due to leader uuid not being parsed, leader=" + leader + ", gang_id=" + gangID);
                    	}
                    }
                    gang.setDescription(result.getString("description"));
                    gang.setMaxMembers(result.getInt("max_members"));
                    list.add(gang);
                }
                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static Set<GangMember> getMembers(Connection connection, int id) {
        //Pull player name by uuid, no current way of doing this..
        String query = "SELECT HEX(GM.uuid) as uid,GM.role,U.name FROM gtm_gang_member GM, gtm_gang G, user U WHERE GM.gang_id=? AND GM.gang_id=G.id AND GM.uuid=U.uuid;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                Set<GangMember> list = Sets.newHashSet();

                while (result.next()) {
                    UUID uuid = UUIDUtil.createUUID(result.getString("uid")).orElse(null);
                    if (uuid == null) continue;
                    list.add(new GTMGangMember(uuid, result.getString("name"), GangRole.getById(result.getInt("role"))));
                }

                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Sets.newHashSet();
    }

    public static Set<GangRelation> getRelations(Connection connection, int id) {
        String query = "SELECT other_id, relation FROM gtm_gang_relation WHERE gang_id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                Set<GangRelation> list = Sets.newHashSet();

                while (result.next()) {
                    list.add(new GTMGangRelation(result.getInt("other_id"), RelationType.getByKey(result.getString("relation"))));
                }

                return list;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Sets.newHashSet();
    }

    public static Optional<Gang> getGangInfo(Connection connection, int id) {
        String query = "SELECT * FROM gtm_gang WHERE id=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Optional<UUID> optional = UUIDUtil.createUUID(result.getString("owner"));
                    if (!optional.isPresent()) return Optional.empty();

                    return Optional.of(new GTMGang(result.getInt("id"), optional.get(), result.getString("name"), result.getString("description"), result.getInt("max_members")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public static Optional<Gang> getGangInfo(Connection connection, String name) {
        String query = "SELECT id,name,description,max_members FROM gtm_gang WHERE name=? AND server_key=?;";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, name);
            statement.setString(2, Core.name().toUpperCase());

            try (ResultSet result = statement.executeQuery()) {
                if (result.next()) {
                    Optional<UUID> optional = UUIDUtil.createUUID(result.getString("owner"));
                    if (!optional.isPresent()) return Optional.empty();

                    return Optional.of(new GTMGang(result.getInt("id"), optional.get(), result.getString("name"), result.getString("description"), result.getInt("max_members")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
}
