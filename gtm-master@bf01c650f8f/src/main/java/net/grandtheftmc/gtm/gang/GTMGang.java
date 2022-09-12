package net.grandtheftmc.gtm.gang;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.gang.member.GTMGangMember;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.gang.relation.GTMGangRelation;
import net.grandtheftmc.gtm.gang.relation.GangRelation;
import net.grandtheftmc.gtm.gang.relation.RelationType;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;

public class GTMGang implements Gang {

    private Set<GangMember> members;
    private Set<GangRelation> relations;
    private final Map<Integer, RelationType> relationRequests;

    private final int id;

    private UUID owner;
    private String name, description;
    private int maxMembers;

    /** Don't ask why this is needed.. */
    private boolean hasUpdated = false;

    public GTMGang(int id) {
        this.id = id;
        this.members = Sets.newHashSet();
        this.relations = Sets.newHashSet();
        this.relationRequests = Maps.newHashMap();
    }

    public GTMGang(int id, UUID owner, String name, String description, int maxMembers) {
        this(id);
        this.owner = owner;
        this.name = name;
        this.description = description;
        this.maxMembers = maxMembers;
    }

    @Override
    public int getUniqueId() {
        return id;
    }

    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(UUID owner) {
        this.owner = owner;

        if (!GangManager.ENABLED) return;

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            GangDAO.setOwner(connection, owner, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;

        if (!GangManager.ENABLED) return;

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            GangDAO.setName(connection, name, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.setDescription(connection, description, id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getMaxMembers() {
        return maxMembers;
    }

    @Override
    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    @Override
    public String getOwnerName() {
        Optional<GangMember> optional = this.members.stream().filter(member -> member.getUniqueId().equals(this.owner)).findFirst();
        return optional.isPresent() ? optional.get().getName() : "Unknown";
    }

    @Override
    public void toggleChat(UUID uuid) {
        this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().ifPresent(GangMember::toggleChat);
    }

    @Override
    public boolean isGangChat(UUID uuid) {
        Optional<GangMember> optional = this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst();
        return optional.isPresent() && optional.get().getChatToggle();
    }

    @Override
    public void setGangChat(UUID uuid, boolean on) {
        this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().ifPresent(member -> member.setChat(on));
    }

    @Override
    public Set<GangMember> getMembers() {
        return members;
    }

    @Override
    public void setMembers(Set<GangMember> members) {
        this.members = members;
    }

    @Override
    public Set<GangMember> getOnlineMembers() {
        return this.members.stream().filter(GangMember::isOnline).collect(Collectors.toSet());
    }

    @Override
    public Optional<GangMember> getMember(UUID uuid) {
        return this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst();
    }

    @Override
    public Optional<GangMember> getMember(String playerName) {
        return this.members.stream().filter(member -> member.getName().equalsIgnoreCase(playerName)).findFirst();
    }

    @Override
    public void addMember(GangMember member) {
        if (this.hasMember(member.getUniqueId())) return;
        this.members.add(member);

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.addMember(connection, member.getUniqueId(), member.getRole(), this.id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void removeMember(UUID uuid) {
        if (!this.hasMember(uuid)) return;
        this.members.stream().filter(member -> member.getUniqueId().equals(uuid)).findFirst().ifPresent(this.members::remove);

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.removeMember(connection, uuid, this.id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public boolean hasMember(UUID uuid) {
        return this.members.stream().anyMatch(member -> member.getUniqueId().equals(uuid));
    }

    @Override
    public void setMemberRole(UUID uuid, GangRole role) {
        Optional<GangMember> optional = this.getMember(uuid);
        if (!optional.isPresent()) return;
        optional.get().setRole(this.id, role);
    }

    @Override
    public Set<GangRelation> getRelations() {
        return relations;
    }

    @Override
    public void setRelation(Set<GangRelation> relations) {
        this.relations = relations;
    }

    @Override
    public void addRelation(GangRelation relation) {
        if (hasRelation(relation.getRelativeId())) return;
        this.relations.add(relation);

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.addRelation(connection, this.id, relation.getRelativeId(), relation.getRelationType());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
	public void addRelationRequest(int id, RelationType type) {
        this.relationRequests.put(id, type);
    }

    @Override
    public void removeRelation(int id) {
        Optional<GangRelation> rel = this.relations.stream().filter(relation -> relation.getRelativeId() == id).findFirst();
        if (!rel.isPresent()) return;
        this.relations.remove(rel.get());
    }

    @Override
    public boolean hasRelation(int id) {
        return this.relations.stream().anyMatch(relation -> relation.getRelativeId() == id);
    }

    @Override
    public String list(Gang gang) {
        int online = (int) this.members.stream().filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null).count();
        int total = this.members.size();
        return (this.isAllied(gang) || this.equals(gang) ? "&a" : this.isEnemy(gang) ? "&c" : "&7") + "&l" + this.name + ": " + online + "&7/&a" + total + "&7 players online";
    }

    @Override
    public void sendToAll(String str) {
        this.members.stream().filter(member -> Bukkit.getPlayer(member.getUniqueId()) != null).forEach(member -> Bukkit.getPlayer(member.getUniqueId()).sendMessage(str));
    }

    @Override
    public void sendToAllExcept(String str, UUID uuid) {
        this.members.stream().filter(member -> !member.getUniqueId().equals(uuid) && Bukkit.getPlayer(member.getUniqueId()) != null).forEach(member -> Bukkit.getPlayer(member.getUniqueId()).sendMessage(str));
    }

    @Override
    public RelationType getRelation(int id) {
        Optional<GangRelation> relation = this.relations.stream().filter(r -> r.getRelativeId() == id).findFirst();
        return relation.isPresent() ? relation.get().getRelationType() : RelationType.NEUTRAL;
    }

    @Override
    public boolean isNeutral(Gang gang) {
        RelationType rel = this.getRelation(gang.getUniqueId());
        return rel == RelationType.NEUTRAL;
    }

    @Override
    public boolean isAllied(Gang gang) {
        return this.relations.stream().anyMatch(relation -> relation.getRelativeId() == gang.getUniqueId() && relation.getRelationType() == RelationType.ALLY);
    }

    @Override
    public boolean isEnemy(Gang gang) {
        return this.relations.stream().anyMatch(relation -> relation.getRelativeId() == gang.getUniqueId() && relation.getRelationType() == RelationType.ENEMY);
    }

    @Override
    public boolean isLeader(UUID uuid) {
        return this.owner.equals(uuid);
    }

    @Override
    public boolean isCoLeader(UUID uuid) {
        return this.members.stream().anyMatch(member -> member.isCoLeader() && member.getUniqueId().equals(uuid));
    }

    @Override
    public boolean isMember(UUID uuid) {
        return this.members.stream().anyMatch(member -> member.getUniqueId().equals(uuid));
    }

    @Override
    public Optional<Gang> getViewingGang(UUID uuid) {
        Optional<GangMember> member = this.getMember(uuid);
        return member.isPresent() ? member.get().getViewingGang() : Optional.empty();
    }

    @Override
    public void setViewingGang(UUID uuid, Gang gang) {
        this.getMember(uuid).ifPresent(member -> member.setViewingGang(gang));
    }

    @Override
    public boolean isViewingGang(UUID uuid, Gang gang) {
        Optional<GangMember> member = this.getMember(uuid);
        if (!member.isPresent()) return false;

        Optional<Gang> g = member.get().getViewingGang();
        return g.isPresent() && g.get().getUniqueId() == gang.getUniqueId();
    }

    @Override
    public boolean isViewingGang(UUID uuid) {
        Optional<GangMember> member = this.getMember(uuid);
        return member.map(gangMember -> gangMember.getViewingGang().isPresent()).orElse(false);
    }

    @Override
    public Optional<GangMember> getViewingGangMember(UUID uuid) {
        Optional<GangMember> member = this.getMember(uuid);
        return member.isPresent() ? member.get().getViewingGangMember() : Optional.empty();
    }

    @Override
    public void setViewingGangMember(UUID uuid, GangMember member) {
        this.getMember(uuid).ifPresent(m -> m.setViewingGangMember(member));
    }

    @Override
    public boolean isViewingGangMember(UUID uuid, UUID member) {
        Optional<GangMember> m = this.getMember(uuid);
        if (!m.isPresent()) return false;

        Optional<GangMember> g = m.get().getViewingGangMember();
        return g.isPresent() && g.get().getUniqueId().equals(member);
    }

    public boolean canJoinGang(User user, GTMUser gtmUser) {
        return user.isSpecial() || gtmUser.isRank(GTMRank.HOMIE);
    }

    public boolean canLeadGang(User user, GTMUser gtmUser) {
        return user.getUserRank().isHigherThan(UserRank.VIP) || gtmUser.isRank(GTMRank.GANGSTER);
    }

    @Override
    public void invite(Player sender, User user, Player target) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        if (target == null) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not online!"));
            return;
        }

        if (Objects.equals(sender, target)) {
            sender.sendMessage(Lang.GANGS.f("&7You can't invite yourself!"));
            return;
        }

        if (this.isMember(target.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7That player is already in your gang!"));
            return;
        }

        if (this.maxMembers <= this.members.size()) {
            sender.sendMessage(Lang.GANGS.f("&7Your gang is full! Rank up or buy a rank at &a" + Core.getSettings().getStoreLink() + "&7 for more gang members!"));
            return;
        }

        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (!canJoinGang(targetUser, targetGtmUser)) {
            sender.sendMessage(Lang.GANGS.f("&7That player can not join a gang because he is not a &e&lHOMIE&7 yet!"));
            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 tried to invite you to gang &a" + this.name + "&7, but you need to be &a &e&lHOMIE&7 or &6&lVIP&7 to join a gang!"));
            return;
        }

//        targetGtmUser.addGangInvite(this.name);
        GangManager.getInstance().addGangInvite(target.getUniqueId(), this.id);

        sender.sendMessage(Lang.GANGS.f("&7You invited " + targetUser.getColoredName(target) + "&7 to your gang!"));
        target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 invited you to gang &a" + this.name + "&7! Use &a\"/g join " + this.name + "\"&7 to join the gang!"));
        this.sendToAllExcept(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 invited " + targetUser.getColoredName(target) + "&7 to your gang!"), sender.getUniqueId());
    }

    @Override
    public void accept(Player sender, User user, GTMUser gtmUser) {
        if (!canJoinGang(user, gtmUser)) {
            sender.sendMessage(Lang.GANGS.f("You must be " + GTMRank.HOMIE.getColoredNameBold() + "&7 or &6&lVIP&7 to join a gang! Check the &d&lMy Account&7 -> &a&lRanks&7 menu on your phone for more information."));
            return;
        }

        if (this.isMember(sender.getUniqueId()) || this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are a member of this gang already!"));
            return;
        }

        if (!GangManager.getInstance().isInvited(user.getUUID(), this.id)) {
            sender.sendMessage(Lang.GANGS.f("&7You have not been invited to this gang!"));
            return;
        }

        if (this.maxMembers <= this.members.size()) {
            sender.sendMessage(Lang.GANGS.f("&7This gang is full!"));
            return;
        }

        Gang current = GangManager.getInstance().getGangByMember(sender.getUniqueId()).orElse(null);
        if (current != null) {
            sender.sendMessage(Lang.GANGS.f("&7Leave your current gang before joining another."));
            return;
        }

        sender.sendMessage(Lang.GANGS.f("&7You joined the gang &a" + this.name + "&7!"));

        if (!GangManager.ENABLED) return;

//        gtmUser.setGang(this.name);
//        gtmUser.removeGangInvite(this.name);
        GangManager.getInstance().removeGangInvite(user.getUUID());

        this.addMember(new GTMGangMember(sender.getUniqueId(), sender.getName(), GangRole.MEMBER));
        this.sendToAll(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 joined your gang!"));
    }

    @Override
    public void leave(Player sender, User user, GTMUser gtmUser) {
        if (this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are the leader of your gang! Please use &a\"/gang leader <player\"&7 before leaving the gang!"));
            return;
        }

        if (!this.isMember(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not a member of this gang!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        this.removeMember(sender.getUniqueId());
//        gtmUser.resetGang();

        sender.sendMessage(Lang.GANGS.f("&7You left the gang &a" + this.name + "&7!"));
        this.sendToAll(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 left the gang!"));
    }

    @Override
    public void setOwner(Player sender, User user, GTMUser gtmUser, Player target) {
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        if (target == null) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not online!"));
            return;
        }

        if (!this.isMember(target.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
            return;
        }

        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (!canLeadGang(targetUser, targetGtmUser)) {
            sender.sendMessage(Lang.GANGS.f("&7You must be a &e&l" + GTMRank.GANGSTER.getColoredNameBold() + "&7 to create a gang! Check the &d&lMy Account&7 -> &a&lRanks&7 menu on your phone for more information."));
            return;
        }

//        targetGtmUser.setGang(this.name, GangRole.LEADER);
        this.setOwner(target.getUniqueId());
        this.setMemberRole(target.getUniqueId(), GangRole.LEADER);

//        gtmUser.setGangRole(GangRole.CO_LEADER);
        this.setMemberRole(sender.getUniqueId(), GangRole.CO_LEADER);

//        Player player = Bukkit.getPlayer(this.owner);
//        if (player != null)
//            GTM.getUserManager().getLoadedUser(this.owner).setGangRole(GangRole.LEADER);

//        this.members.stream().filter(member -> Objects.equals(member.getUniqueId(), target.getUniqueId())).findFirst().ifPresent(this.members::remove);
//        this.removeMember(target.getUniqueId());

        sender.sendMessage(Lang.GANGS.f("&7You promoted " + targetUser.getColoredName(target) + "&7 to the leader of your gang! You are now a co-leader!"));
        target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted you to leader of your gang!"));
        this.sendToAll(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted " + targetUser.getColoredName(target) + "&7 to the leader of your gang!"));

//        this.members.add(new GTMGangMember(sender.getUniqueId(), sender.getName(), "coleader"));
//        this.addMember(new GTMGangMember(sender.getUniqueId(), sender.getName(), GangRole.CO_LEADER));

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.setOwner(connection, owner, id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void promote(Player sender, User user, GTMUser gtmUser, String targetName) {
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        Optional<GangMember> optional = this.members.stream().filter(member -> member.getName().equalsIgnoreCase(targetName)).findFirst();
        if (!optional.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
            return;
        }

        if (optional.get().isCoLeader()) {
            sender.sendMessage(Lang.GANGS.f("&7That player is already a coleader of this gang!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        Player target = Bukkit.getPlayer(optional.get().getUniqueId());
        String n = optional.get().getName();

        if (target == null) {
//            Core.sql.updateAsyncLater("update " + Core.name() + " set gangRank='coleader' where gang='" + this.name + "' and uuid='" + member.getUUID() + "';");
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gangRank='coleader' where gang='" + this.name + "' and uuid='" + member.getUniqueId() + "';"));

//            ServerUtil.runTaskAsync(() -> {
//                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    GangDAO.setMemberRole(connection, optional.get().getUniqueId(), GangRole.CO_LEADER, id);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
            this.setMemberRole(optional.get().getUniqueId(), GangRole.CO_LEADER);
        } else {
            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
            n = targetUser.getColoredName(target);

//            targetGtmUser.setGangRole(GangRole.CO_LEADER);
            this.setMemberRole(target.getUniqueId(), GangRole.CO_LEADER);

            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted you to coleader of your gang!"));
        }

        this.setMemberRole(optional.get().getUniqueId(), GangRole.CO_LEADER);
//        optional.get().setRole(this.id, GangRole.CO_LEADER);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isMember(player.getUniqueId()) && !Objects.equals(player, target) && !Objects.equals(player, sender)) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted " + n + "&7 to coleader of your gang!"));
            }
        }

        sender.sendMessage(Lang.GANGS.f("&7You promoted " + n + "&7 to coleader of your gang!"));
    }

    @Override
    public void demote(Player sender, User user, GTMUser gtmUser, String targetName) {
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        Optional<GangMember> optional = this.members.stream().filter(member -> member.getName().equalsIgnoreCase(targetName)).findFirst();
        if (!optional.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
            return;
        }

        if (!optional.get().isCoLeader()) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not a coleader of this gang!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        Player target = Bukkit.getPlayer(optional.get().getUniqueId());
        String n = optional.get().getName();
        if (target == null) {
            this.setMemberRole(optional.get().getUniqueId(), GangRole.MEMBER);
        } else {
            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
            n = targetUser.getColoredName(target);

            this.setMemberRole(target.getUniqueId(), GangRole.MEMBER);
            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 demoted you to member of your gang!"));
        }

        optional.get().setRole(this.id, GangRole.MEMBER);
        for (Player player : Bukkit.getOnlinePlayers())
            if (this.isMember(player.getUniqueId()) && !Objects.equals(player, target) && !Objects.equals(player, sender))
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 demoted " + n + "&7 to member of your gang!"));

        sender.sendMessage(Lang.GANGS.f("&7You demoted " + n + "&7 to member of your gang!"));
    }

    @Override
    public void kick(Player sender, User user, GTMUser gtmUser, String targetName) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        if (sender.getName().equalsIgnoreCase(targetName)) {
            sender.sendMessage(Lang.GANGS.f("&7You can't kick yourself!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        Optional<GangMember> optional;
        Player target = Bukkit.getPlayer(targetName);
        if (target != null) optional = this.members.stream().filter(member -> member.getUniqueId().equals(target.getUniqueId())).findFirst();
        else optional = this.members.stream().filter(member -> member.getName().equalsIgnoreCase(targetName)).findFirst();

        if (!optional.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That player is not in your gang!"));
            return;
        }

//        this.gangMembers.remove(member);

        if (optional.get().getRole() == GangRole.LEADER) {
            sender.sendMessage(Lang.GANGS.f("&cYou cannot kick the leader!"));
            return;
        }

        this.removeMember(optional.get().getUniqueId());

        String name = targetName;
        if (target == null) {
//            Core.sql.updateAsyncLater("update " + Core.name() + " set gang=null, gangRank='member' where name='" + name + "';");
//            String finalName = name;
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gang=null, gangRank='member' where name='" + finalName + "';"));

//            ServerUtil.runTaskAsync(() -> {
//                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    GangDAO.removeMember(connection, optional.get().getUniqueId(), this.id);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });
        } else {
            name = Core.getUserManager().getLoadedUser(target.getUniqueId()).getColoredName(target);
//            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
//            targetGtmUser.resetGang();
            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 kicked you from the gang!"));
        }

        this.sendToAllExcept(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 kicked &a" + name + "&7 from your gang!"), sender.getUniqueId());
        sender.sendMessage(Lang.GANGS.f("&7You kicked &a" + name + "&7 from the gang!"));
    }

    @Override
    public void disbandConfirm(Player sender, User user, GTMUser gtmUser) {
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        if (!GangManager.ENABLED) return;

//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs where name='" + this.name + "';");
//        Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs_relations where gang1='" + this.name + "' or gang2='" + this.name + "';");
//        Core.sql.updateAsyncLater("update " + Core.name() + " set gang=null,gangRank='member' where gang='" + this.name + "';");
        ServerUtil.runTaskAsync(() -> {
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs where name='" + this.name + "';");
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs_relations where gang1='" + this.name + "' or gang2='" + this.name + "';");
//            BaseDatabase.runCustomQuery("update " + Core.name() + " set gang=null,gangRank='member' where gang='" + this.name + "';");

            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                GangDAO.deleteGang(connection, this.id);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        sender.sendMessage(Lang.GANGS.f("&7You disbanded the gang &a" + this.name + "&7!"));
//        gtmUser.setGangName(null);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (this.isMember(player.getUniqueId())) {
//                GTMUser u = GTM.getUserManager().getLoadedUser(player.getUniqueId());
//                u.setGangName(null);
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 disbanded your gang &a" + this.name + "&7!"));
            }
            else if (!this.isLeader(player.getUniqueId())) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 disbanded the gang &a" + this.name + "&7!"));
            }
        }

        for (GangRelation r : this.relations) {
            Gang g = GangManager.getInstance().getGang(r.getRelativeId()).orElse(null);
            if (g == null) continue;
            g.removeRelation(this.id);
        }

        //TODO, Should auto remove in database.
//        this.relations.stream().filter(relation -> GangManager.getInstance().getGang(relation.getRelativeId()).isPresent())
//                .forEach(relation -> GangManager.getInstance().getGang(relation.getRelativeId()).get().removeRelation(this.id));

//        this.relations.keySet().stream().filter(gang -> GTM.getGangManager().isLoaded(gang)).forEach(gang -> GTM.getGangManager().getLoadedGang(gang).removeRelation(this.name));

//        GTM.getGangManager().unloadGang(this.name);
        GangManager.getInstance().removeGang(this);
    }

    @Override
    public void disband(Player sender) {
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        sender.sendMessage(Lang.GANGS.f("&7Are you sure you want to disband this gang? It will remove all members and delete it forever. Type \"&a/gang disband confirm\"&7 to disband your gang!"));
    }

    @Override
    public void rename(Player sender, User user, GTMUser gtmUser, String gangName) {
        UUID uuid = sender.getUniqueId();
        if (!this.isLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        if (!gangName.matches("^[a-zA-Z_0-9]+$")) {
            sender.sendMessage(Lang.GANGS.f("&7Only a-z, A-Z, 0-9 and _ are allowed in a gang name!"));
            return;
        }

        if (gangName.length() > 16 || gangName.length() < 3) {
            sender.sendMessage(Lang.GANGS.f("&7The name of your gang needs to be 3-16 characters long!"));
            return;
        }

        if (GangManager.getInstance().getGang(gangName).isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        ServerUtil.runTaskAsync(() -> {
            boolean[] exists = {false};
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                exists[0] = GangDAO.isGangExisting(connection, gangName);
            } catch (SQLException e) {
                e.printStackTrace();
                return;
            }

            ServerUtil.runTask(() -> {
                Player sender2 = Bukkit.getPlayer(uuid);
                if (sender2 == null) return;
                if (exists[0]) {
                    sender2.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
                    return;
                }

                this.setName(gangName);

                sender.sendMessage(Lang.GANGS.f("&7You changed the name of your gang to &a" + gangName + "&7!"));
                sendToAllExcept(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 changed the name of your gang to &a" + gangName + "&7!"), sender.getUniqueId());
            });
        });

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                String s = null;
//                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    try (PreparedStatement statement = connection.prepareStatement("SELECT name FROM gtm_gang WHERE server_key=? AND name=?;")) {
//                        statement.setString(1, Core.name().toUpperCase());
//                        statement.setString(2, gangName);
//                        try (ResultSet result = statement.executeQuery()) {
//                            if (result.next()) s = result.getString("name");
//                        }
//                    }
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                    return;
//                }
//
//                String name = s;
//                new BukkitRunnable() {
//                    @Override
//                    public void run() {
//                        Player sender = Bukkit.getPlayer(uuid);
//                        if (sender == null) return;
//                        if (name != null) {
//                            sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
//                            return;
//                        }
//
//                        User user = Core.getUserManager().getLoadedUser(uuid);
//                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
//                        Gang gang = gtmUser.getGang();
//                        gang.setName(gangName);
//
//                        sender.sendMessage(Lang.GANGS.f("&7You changed the name of your gang to &a" + gangName + "&7!"));
//                        sendToAllExcept(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 changed the name of your gang to &a" + gangName + "&7!"), sender.getUniqueId());
//                    }
//                }.runTask(GTM.getInstance());
//            }
//        }.runTaskAsynchronously(GTM.getInstance());
    }

    @Override
    public void description(Player sender, User user, GTMUser gtmUser, String description) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the (co-)leader of this gang!"));
            return;
        }

        if (description.length() > 64 || description.length() < 3) {
            sender.sendMessage(Lang.GANGS.f("&7The description must be between 3-64 characters long!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        this.setDescription(description);
        sender.sendMessage(Lang.GANGS.f("&7You changed the description of your gang to &a" + description + "&7!"));
        this.sendToAllExcept(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 changed the description of your gang to &a" + description + "&7!"), sender.getUniqueId());
    }

    @Override
    public void ally(Player sender, User user, GTMUser gtmUser, String gang) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the (co-)leader of this gang!"));
            return;
        }

        Optional<Gang> g = GangManager.getInstance().getGang(gang);
        if (!g.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
            return;
        }

        if (this.isAllied(g.get())) {
            sender.sendMessage(Lang.GANGS.f("&7That gang is allied to you already!"));
            sender.sendMessage(Lang.GANGS.f("&7Want to unally this gang? Use /g neutral"));
            return;
        }

        if (!GangManager.ENABLED) return;

        if (this.relationRequests.containsKey(g.get().getUniqueId()) && Objects.equals(RelationType.ALLY, this.relationRequests.get(g.get().getUniqueId()))) {
            this.addRelation(new GTMGangRelation(g.get().getUniqueId(), g.get().getName(), RelationType.ALLY));
            g.get().addRelation(new GTMGangRelation(this.id, this.name, RelationType.ALLY));
            this.relationRequests.remove(g.get().getUniqueId());

//            ServerUtil.runTaskAsync(() -> {
//                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    GangDAO.removeRelation(connection, g.get().getUniqueId(), this.id);
//                    GangDAO.addRelation(connection, this.id, g.get().getUniqueId(), RelationType.ALLY);
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//            });

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (g.get().isMember(player.getUniqueId()) || g.get().isLeader(player.getUniqueId())) {
                    player.sendMessage(Lang.GANGS.f("&7Your gang is now allied to &a" + this.name + "&7!"));
                }
                else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
                    player.sendMessage(Lang.GANGS.f("&7Your gang is now allied to &a" + g.get().getName() + "&7!"));
                }
            }
            sender.sendMessage(Lang.GANGS.f("&7You accepted the ally request from gang &a" + g.get().getName() + "&7!"));
        }

        g.get().addRelationRequest(this.id, RelationType.ALLY);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (g.get().isMember(player.getUniqueId()) || g.get().isLeader(player.getUniqueId())) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 from gang &a" + this.name + "&7 has requested to be allied to your gang!"));
            }
            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 has requested to be allied to &a" + g.get().getName() + "&7!"));
            }
        }

        sender.sendMessage(Lang.GANGS.f("&7You sent an ally request to gang &a" + g.get().getName() + "&7!"));
    }

    @Override
    public void neutral(Player sender, User user, GTMUser gtmUser, String gang) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        Optional<Gang> g = GangManager.getInstance().getGang(gang);
        if (!g.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
            return;
        }

        if (this.isNeutral(g.get())) {
            sender.sendMessage(Lang.GANGS.f("&7That gang is neutral to you already!"));
            return;
        }

        if (!GangManager.ENABLED) return;

        if (this.relationRequests.containsKey(g.get().getUniqueId()) && Objects.equals(RelationType.NEUTRAL, this.relationRequests.get(g.get().getUniqueId()))) {
//            this.relations.add(new GTMGangRelation(g.get().getUniqueId(), g.get().getName(), RelationType.NEUTRAL));
//            g.get().addRelation(new GTMGangRelation(this.id, this.name, RelationType.NEUTRAL));
            this.relationRequests.remove(g.get().getUniqueId());

            ServerUtil.runTaskAsync(() -> {
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    GangDAO.removeRelation(connection, g.get().getUniqueId(), this.id);
                    GangDAO.removeRelation(connection, this.id, g.get().getUniqueId());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (g.get().isMember(player.getUniqueId()) || g.get().isLeader(player.getUniqueId())) {
                    player.sendMessage(Lang.GANGS.f("&7Your gang is now neutral to &a" + this.name + "&7!"));
                }
                else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
                    player.sendMessage(Lang.GANGS.f("&7Your gang is now neutral to &a" + g.get().getName() + "&7!"));
                }
            }
            sender.sendMessage(Lang.GANGS.f("&7You accepted the neutral request from gang &a" + g.get().getName() + "&7!"));
        }

        g.get().addRelationRequest(this.id, RelationType.NEUTRAL);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (g.get().isMember(player.getUniqueId()) || g.get().isLeader(player.getUniqueId())) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 from gang &a" + this.name + "&7 has requested to be neutral to your gang!"));
            }
            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 has requested to be neutral to &a" + g.get().getName() + "&7!"));
            }
        }

        sender.sendMessage(Lang.GANGS.f("&7You sent a neutral request to gang &a" + g.get().getName() + "&7!"));
    }

    @Override
    public void enemy(Player sender, User user, GTMUser gtmUser, String gang) {
        if (!this.isLeader(sender.getUniqueId()) && !this.isCoLeader(sender.getUniqueId())) {
            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
            return;
        }

        Optional<Gang> g = GangManager.getInstance().getGang(gang);
        if (!g.isPresent()) {
            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
            return;
        }

        if (this.isEnemy(g.get())) {
            sender.sendMessage(Lang.GANGS.f("&7That gang is an enemy to you already!"));
            sender.sendMessage(Lang.GANGS.f("&7Want to unenemy this gang? Use /g neutral"));
            return;
        }

        if (!GangManager.ENABLED) return;

        this.relations.add(new GTMGangRelation(g.get().getUniqueId(), g.get().getName(), RelationType.ENEMY));
        this.addRelation(new GTMGangRelation(g.get().getUniqueId(), g.get().getName(), RelationType.ENEMY));
        g.get().addRelation(new GTMGangRelation(this.id, this.name, RelationType.ENEMY));
        this.relationRequests.remove(g.get().getUniqueId());

//        ServerUtil.runTaskAsync(() -> {
//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                GangDAO.removeRelation(connection, g.get().getUniqueId(), this.id);
//                GangDAO.addRelation(connection, this.id, g.get().getUniqueId(), RelationType.ALLY);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });

        for (Player player : Bukkit.getOnlinePlayers())
            if (g.get().isMember(player.getUniqueId()) || g.get().isLeader(player.getUniqueId()))
                player.sendMessage(Lang.GANGS.f("&7Your gang is now an enemy to gang &a" + this.name + "&7!"));
            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()))
                player.sendMessage(Lang.GANGS.f("&7Your gang is now an enemy to gang &a" + g.get().getName() + "&7!"));
    }

    @Override
    public void chat(Player player, User user, GTMUser gtmUser, String msg) {
        Bukkit.getOnlinePlayers().stream().filter(p -> this.isMember(p.getUniqueId()) || this.isLeader(p.getUniqueId())).forEach(p -> p.sendMessage(Lang.GANGCHAT.f("&7[&a&l" + user.getColoredName(player) + "&7] &r") + Utils.fColor(msg)));

        UserManager.getInstance().getUsers().stream().filter(u -> u.getPref(Pref.SOCIALSPY)).forEach(u -> {
            if (u != null && u.getUUID() != null) {
                Player p = Bukkit.getPlayer(u.getUUID());
                if (p != null) p.sendMessage(Lang.SS.f("&r" + player.getName() + ": /gc " + msg));
            }
        });
    }

    @Override
    public boolean updateDataFromDb() {
//        if (this.hasUpdated) return true;
//        boolean b = true;
//
////        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
////            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_gangs where name=?;")) {
////                statement.setString(1, this.name);
////                try (ResultSet result = statement.executeQuery()) {
////                    if (result.next()) {
////                        this.description = result.getString("description");
////                        this.leader = UUID.fromString(result.getString("leader"));
////                        this.leaderName = result.getString("leaderName");
////                        this.maxMembers = result.getInt("maxMembers");
////                    }
////                    else {
////                        b = false;
////                    }
////                }
////            }
////        } catch (SQLException e) {
////            e.printStackTrace();
////        }
//
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            Gang temp = GangDAO.getGang()
//            this.members.addAll(GangDAO.getMembers(connection, this.id));
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            try (PreparedStatement statement = connection.prepareStatement("select uuid,name,gangRank from " + Core.name() + " where gang=?;")) {
//                statement.setString(1, this.name);
//                try (ResultSet result = statement.executeQuery()) {
//                    while (result.next()) {
//                        if (!Objects.equals("leader", result.getString("gangRank"))) {
//                            this.gangMembers.add(new net.grandtheftmc.gtm.gangs.GangMember(UUID.fromString(result.getString("uuid")),
//                                    result.getString("name"), result.getString("gangRank")));
//                        }
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//        this.relations = new HashMap<>();
//        this.relationRequests = new HashMap<>();
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            try (PreparedStatement statement = connection.prepareStatement("select * from " + Core.name() + "_gangs_relations where gang1=? or gang2=?;")) {
//                statement.setString(1, this.name);
//                statement.setString(2, this.name);
//                try (ResultSet result = statement.executeQuery()) {
//                    while (result.next()) {
//                        String gang1 = result.getString("gang1");
//                        String gang2 = result.getString("gang2");
//                        this.relations.put(gang1.equalsIgnoreCase(this.name) ? gang2 : gang1, result.getString("relation"));
//                    }
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//            b = false;
//        }
//
//        this.hasUpdated = b;
//        return b;
        return true;
    }
}
