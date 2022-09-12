package net.grandtheftmc.gtm.gang;

import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.gang.relation.GangRelation;
import net.grandtheftmc.gtm.gang.relation.RelationType;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public interface Gang {

    int getUniqueId();

    UUID getOwner();
    void setOwner(UUID owner);

    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    int getMaxMembers();
    void setMaxMembers(int maxMembers);

    String getOwnerName();

    void toggleChat(UUID uuid);
    boolean isGangChat(UUID uuid);
    void setGangChat(UUID uuid, boolean on);

    Set<GangMember> getMembers();
    void setMembers(Set<GangMember> members);
    Set<GangMember> getOnlineMembers();
    Optional<GangMember> getMember(UUID uuid);
    Optional<GangMember> getMember(String playerName);
    void addMember(GangMember member);
    void removeMember(UUID uuid);
    boolean hasMember(UUID uuid);
    void setMemberRole(UUID uuid, GangRole role);

    Set<GangRelation> getRelations();
    void setRelation(Set<GangRelation> relations);
    void addRelation(GangRelation relation);
    void removeRelation(int id);
    boolean hasRelation(int id);

    RelationType getRelation(int id);
    void addRelationRequest(int id, RelationType type);

    String list(Gang gang);
    void sendToAll(String str);
    void sendToAllExcept(String str, UUID uuid);

    boolean isNeutral(Gang gang);
    boolean isAllied(Gang gang);
    boolean isEnemy(Gang gang);
    boolean isLeader(UUID uuid);
    boolean isCoLeader(UUID uuid);
    boolean isMember(UUID uuid);

    Optional<Gang> getViewingGang(UUID uuid);
    void setViewingGang(UUID uuid, Gang gang);
    boolean isViewingGang(UUID uuid, Gang gang);
    boolean isViewingGang(UUID uuid);

    Optional<GangMember> getViewingGangMember(UUID uuid);
    void setViewingGangMember(UUID uuid, GangMember member);
    boolean isViewingGangMember(UUID uuid, UUID member);

    void invite(Player sender, User user, Player target);
    void accept(Player sender, User user, GTMUser gtmUser);
    void leave(Player sender, User user, GTMUser gtmUser);
    void setOwner(Player sender, User user, GTMUser gtmUser, Player target);
    void promote(Player sender, User user, GTMUser gtmUser, String targetName);
    void demote(Player sender, User user, GTMUser gtmUser, String targetName);
    void kick(Player sender, User user, GTMUser gtmUser, String targetName);
    void disbandConfirm(Player sender, User user, GTMUser gtmUser);
    void disband(Player sender);
    void rename(Player sender, User user, GTMUser gtmUser, String gangName);
    void description(Player sender, User user, GTMUser gtmUser, String description);

    void ally(Player sender, User user, GTMUser gtmUser, String gang);
    void neutral(Player sender, User user, GTMUser gtmUser, String gang);
    void enemy(Player sender, User user, GTMUser gtmUser, String gang);
    void chat(Player player, User user, GTMUser gtmUser, String msg);

    boolean updateDataFromDb();
}
