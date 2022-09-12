package net.grandtheftmc.gtm.gangs;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.gang.GangDAO;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class Gang {

//    private boolean hasUpdated;
//
//    private int id;
//    protected String name;
//
//    protected String description = "Your default gang description!";
//    protected UUID leader;
//    protected String leaderName;
//    protected int maxMembers;
//    protected List<GangMember> gangMembers = new ArrayList<>();
//    protected Map<Integer, Integer> relations = new HashMap<>();
//    protected Map<Integer, Integer> relationRequests = new HashMap<>();
//
//    public Gang(int id) {
//        this.id = id;
//    }
//
//    public Gang(String name, UUID leader, String leaderName, int maxMembers) {
//        this.name = name;
//        this.leader = leader;
//        this.leaderName = leaderName;
//        this.maxMembers = maxMembers;
//    }
//
//    public String getName() {
//        return this.name;
//    }
//
//    public void setName(String newName) {
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set name='" + name + "' where name='" + this.name + "';");
////        Core.sql.updateAsyncLater("update " + Core.name() + " set gang='" + name + "' where gang='" + this.name + "';");
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs_relations set gang1='" + name + "' where gang1='" + this.name + "';");
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs_relations set gang2='" + name + "' where gang2='" + this.name + "';");
//
//        ServerUtil.runTaskAsync(() -> {
////            BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set name='" + name + "' where name='" + this.name + "';");
////            BaseDatabase.runCustomQuery("update " + Core.name() + " set gang='" + name + "' where gang='" + this.name + "';");
////            BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs_relations set gang1='" + name + "' where gang1='" + this.name + "';");
////            BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs_relations set gang2='" + name + "' where gang2='" + this.name + "';");
//
//            // -- NEW --
//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                GangDAO.setName(connection, newName, id);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//
//        //Remove from hashmap
////        GTM.getGangManager().unloadGang(newName);
//
//        for (Player p : Bukkit.getOnlinePlayers()) {
//            if (this.isMember(p.getUniqueId()) || this.isLeader(p.getUniqueId())) {
//                GTMUser user = GTM.getUserManager().getLoadedUser(p.getUniqueId());
//                user.setGang(newName);
//            }
//        }
//        this.name = newName;
//
//        //Associate new mapping
////        GTM.getGangManager().addLoadedGang(this);
//
//    }
//
//    public boolean updateDataFromDb() {
//        if (this.hasUpdated) return true;
//        boolean b = true;
//
//        GangDAO
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
//        this.gangMembers = new ArrayList<>();
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//            try (PreparedStatement statement = connection.prepareStatement("select uuid,name,gangRank from " + Core.name() + " where gang=?;")) {
//                statement.setString(1, this.name);
//                try (ResultSet result = statement.executeQuery()) {
//                    while (result.next()) {
//                        if (!Objects.equals("leader", result.getString("gangRank"))) {
//                            this.gangMembers.add(new GangMember(UUID.fromString(result.getString("uuid")),
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
//    }
//
//    public String getDescription() {
//        return this.description;
//    }
//
//    public void setDescription(String s) {
//        this.description = s;
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set description='" + s + "' where name='" + this.name + "';");
//
////        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set description='" + s + "' where name='" + this.name + "';"));
//        ServerUtil.runTaskAsync(() -> {
//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                GangDAO.setDescription(connection, s, id);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public UUID getLeader() {
//        return this.leader;
//    }
//
//    public void setLeader(UUID uuid) {
//        this.leader = uuid;
//        Player player = Bukkit.getPlayer(uuid);
//        if (player != null) {
//            GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
//            user.setGangRole("leader");
//            this.hasMaxMembers(player, Core.getUserManager().getLoadedUser(uuid), user);
//        }
//
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set leader='" + uuid + "',maxMembers=" + this.maxMembers + " where name='" + this.name + "';");
//
////        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set leader='" + uuid + "',maxMembers=" + this.maxMembers + " where name='" + this.name + "';"));
//        ServerUtil.runTaskAsync(() -> {
//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                GangDAO.setLeader(connection, uuid, id);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public boolean isLeader(UUID uuid) {
//        return Objects.equals(uuid, this.leader);
//    }
//
//    public void setLeader(UUID uuid, String name) {
//        this.leader = uuid;
//        this.leaderName = name;
//        Player player = Bukkit.getPlayer(uuid);
//        if (player != null) {
//            GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
//            user.setGangRole("leader");
//            this.hasMaxMembers(player, Core.getUserManager().getLoadedUser(uuid), user);
//        }
//
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set leader='" + uuid + "',leaderName='" + name + "',maxMembers=" + this.maxMembers + " where name='" + this.name + "';");
//
////        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set leader='" + uuid + "',leaderName='" + name + "',maxMembers=" + this.maxMembers + " where name='" + this.name + "';"));
//        ServerUtil.runTaskAsync(() -> {
//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                GangDAO.setLeader(connection, uuid, id);
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//        });
//    }
//
//    public String getLeaderName() {
//        return this.leaderName;
//    }
//
//    public void setLeaderName(String name) {
//        this.leaderName = name;
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set leaderName='" + name + "' where name='" + this.name + "';");
//        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set leaderName='" + name + "' where name='" + this.name + "';"));
//    }
//
//    public boolean isLeader(String name) {
//        return this.leaderName.equalsIgnoreCase(name);
//    }
//
//    public int getMaxMembers() {
//        Player player = Bukkit.getPlayer(this.leader);
//        if (player == null)
//            return this.maxMembers;
//        return GTM.getUserManager().getLoadedUser(this.leader).getMaxGangMembers(player,
//                Core.getUserManager().getLoadedUser(this.leader));
//    }
//
//    public boolean hasMaxMembers() {
//        Player player = Bukkit.getPlayer(this.leader);
//        if (player == null)
//            return this.maxMembers <= this.gangMembers.size();
//        return this.hasMaxMembers(player, Core.getUserManager().getLoadedUser(this.leader),
//                GTM.getUserManager().getLoadedUser(this.leader));
//    }
//
//    public boolean hasMaxMembers(Player leader, User user, GTMUser gtmUser) {
//        this.maxMembers = gtmUser.getMaxGangMembers(leader, user);
//        return this.maxMembers <= this.gangMembers.size();
//    }
//
//    public void updateMaxMembers(Player leader, User user, GTMUser gtmUser) {
//        this.maxMembers = gtmUser.getMaxGangMembers(leader, user);
////        Core.sql.updateAsyncLater("update " + Core.name() + "_gangs set maxMembers=" + this.maxMembers + " where name='" + this.name + "';");
//        ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + "_gangs set maxMembers=" + this.maxMembers + " where name='" + this.name + "';"));
//    }
//
//    public List<GangMember> getGangMembers() {
//        return this.gangMembers;
//    }
//
//    public int getOnlineMembers() {
//        return (int) Bukkit.getOnlinePlayers().stream().filter(player -> this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId())).count();
//    }
//
//    public GangMember getMember(UUID uuid) {
//        return this.gangMembers.stream().filter(m -> Objects.equals(m.getUUID(), uuid)).findFirst().orElse(null);
//    }
//
//    public GangMember getMember(String name) {
//        return this.gangMembers.stream().filter(m -> Objects.equals(m.getName(), name)).findFirst().orElse(null);
//    }
//
//    public boolean isMember(UUID uuid) {
//        return this.getMember(uuid) != null;
//    }
//
//    public boolean isMember(String name) {
//        return this.getMember(name) != null;
//    }
//
//    public boolean isColeader(UUID uuid) {
//        GangMember m = this.getMember(uuid);
//        return m != null && m.isColeader();
//    }
//
//    public Map<String, String> getRelations() {
//        return this.relations;
//    }
//
//    public String getRelation(String name) {
//        return this.relations.get(name);
//    }
//
//    public boolean hasRelation(String name) {
//        return this.getRelation(name) != null;
//    }
//
//    public boolean isAllied(String name) {
//        String rel = this.getRelation(name);
//        return Objects.equals("ally", rel);
//    }
//
//    public boolean isEnemy(String name) {
//        String rel = this.getRelation(name);
//        return Objects.equals("enemy", rel);
//    }
//
//    public boolean isNeutral(String name) {
//        String rel = this.getRelation(name);
//        return rel == null;
//    }
//
//    public void addRelation(String name, String relation) {
//        this.relations.put(name, relation);
//    }
//
//    public void removeRelation(String name) {
//        this.relations.remove(name);
//    }
//
//    public void addRelationRequest(String name, String relation) {
//        this.relationRequests.put(name, relation);
//    }
//
//    public void unload(UUID uuid) {
//        if (!Objects.equals(this.leader, uuid) && Bukkit.getPlayer(this.leader) != null) return;
//        for (GangMember member : this.gangMembers) {
//            if (!Objects.equals(member.getUUID(), uuid) && Bukkit.getPlayer(member.getUUID()) != null)
//                return;
//        }
//        GTM.getGangManager().unloadGang(this.name);
//
//    }
//
//    public void disband(Player sender) {
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        sender.sendMessage(Lang.GANGS.f(
//                "&7Are you sure you want to disband this gang? It will remove all members and delete it forever. Type \"&a/gang disband confirm\"&7 to disband your gang!"));
//    }
//
//    public void disbandConfirm(Player sender, User user, GTMUser gtmUser) {
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//
////        Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs where name='" + this.name + "';");
////        Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs_relations where gang1='" + this.name + "' or gang2='" + this.name + "';");
////        Core.sql.updateAsyncLater("update " + Core.name() + " set gang=null,gangRank='member' where gang='" + this.name + "';");
//        ServerUtil.runTaskAsync(() -> {
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs where name='" + this.name + "';");
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs_relations where gang1='" + this.name + "' or gang2='" + this.name + "';");
//            BaseDatabase.runCustomQuery("update " + Core.name() + " set gang=null,gangRank='member' where gang='" + this.name + "';");
//        });
//
//        sender.sendMessage(Lang.GANGS.f("&7You disbanded the gang &a" + this.name + "&7!"));
//        gtmUser.setGangName(null);
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (this.isMember(player.getUniqueId())) {
//                GTMUser u = GTM.getUserManager().getLoadedUser(player.getUniqueId());
//                u.setGangName(null);
//                player.sendMessage(
//                        Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 disbanded your gang &a" + this.name + "&7!"));
//            } else if (!this.isLeader(player.getUniqueId()))
//                player.sendMessage(
//                        Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 disbanded the gang &a" + this.name + "&7!"));
//        this.relations.keySet().stream().filter(gang -> GTM.getGangManager().isLoaded(gang)).forEach(gang -> GTM.getGangManager().getLoadedGang(gang).removeRelation(this.name));
//        GTM.getGangManager().unloadGang(this.name);
//    }
//
//    public void setLeader(Player sender, User user, GTMUser gtmUser, Player target) {
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//
//        if (target == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not online!"));
//            return;
//        }
//
//        if (!this.isMember(target.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
//            return;
//        }
//
//        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
//        if (!targetGtmUser.canLeadGang(targetUser)) {
//            sender.sendMessage(Lang.GANGS.f("&7You must be a &e&l" + GTMRank.GANGSTER.getColoredNameBold() + "&7 to create a gang! Check the &d&lMy Account&7 -> &a&lRanks&7 menu on your phone for more information."));
//            return;
//        }
//
//        targetGtmUser.setGang(this.name, "leader");
//        gtmUser.setGangRole("coleader");
//        this.setLeader(target.getUniqueId(), target.getName());
//        this.gangMembers.stream().filter(member -> Objects.equals(member.getUUID(), target.getUniqueId())).findFirst().ifPresent(member -> this.gangMembers.remove(member));
//        sender.sendMessage(Lang.GANGS.f("&7You promoted " + targetUser.getColoredName(target) + "&7 to the leader of your gang! You are now a co-leader!"));
//        target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted you to leader of your gang!"));
//        Bukkit.getOnlinePlayers().stream().filter(player -> this.isMember(player.getUniqueId())).forEach(player -> player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted "
//                + targetUser.getColoredName(target) + "&7 to the leader of your gang!")));
//        this.gangMembers.add(new GangMember(sender.getUniqueId(), sender.getName(), "coleader"));
//    }
//
//    public void promote(Player sender, User user, GTMUser gtmUser, String targetName) {
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        GangMember member = this.getMember(targetName);
//        if (member == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
//            return;
//        }
//        if (member.isColeader()) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is already a coleader of this gang!"));
//            return;
//        }
//        Player target = Bukkit.getPlayer(member.getUUID());
//        String n = member.getName();
//        if (target == null) {
////            Core.sql.updateAsyncLater("update " + Core.name() + " set gangRank='coleader' where gang='" + this.name + "' and uuid='" + member.getUUID() + "';");
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gangRank='coleader' where gang='" + this.name + "' and uuid='" + member.getUUID() + "';"));
//        }
//        else {
//            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
//            n = targetUser.getColoredName(target);
//            targetGtmUser.setGangRole("coleader");
//            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 promoted you to coleader of your gang!"));
//        }
//        member.setRank(GangRole.CO_LEADER);
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (this.isMember(player.getUniqueId()) && !Objects.equals(player, target) && !Objects.equals(player, sender))
//                player.sendMessage(Lang.GANGS
//                        .f("&7" + user.getColoredName(sender) + "&7 promoted " + n + "&7 to coleader of your gang!"));
//        sender.sendMessage(Lang.GANGS.f("&7You promoted " + n + "&7 to coleader of your gang!"));
//    }
//
//    public void demote(Player sender, User user, GTMUser gtmUser, String targetName) {
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        GangMember member = this.getMember(targetName);
//        if (member == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not a member of this gang!"));
//            return;
//        }
//        if (!member.isColeader()) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not a coleader of this gang!"));
//            return;
//        }
//        Player target = Bukkit.getPlayer(member.getUUID());
//        String n = member.getName();
//        if (target == null) {
////            Core.sql.updateAsyncLater("update " + Core.name() + " set gangRank='member' where gang='" + this.name + "' and uuid='" + member.getUUID() + "';");
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gangRank='member' where gang='" + this.name + "' and uuid='" + member.getUUID() + "';"));
//        }
//        else {
//            User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
//            n = targetUser.getColoredName(target);
//            targetGtmUser.setGangRole("member");
//            target.sendMessage(
//                    Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 demoted you to member of your gang!"));
//        }
//        member.setRank(GangRole.MEMBER);
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (this.isMember(player.getUniqueId()) && !Objects.equals(player, target) && !Objects.equals(player, sender))
//                player.sendMessage(Lang.GANGS
//                        .f("&7" + user.getColoredName(sender) + "&7 demoted " + n + "&7 to member of your gang!"));
//        sender.sendMessage(Lang.GANGS.f("&7You demoted " + n + "&7 to member of your gang!"));
//
//    }
//
//    public void ally(Player sender, User user, GTMUser gtmUser, String gang) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the (co-)leader of this gang!"));
//            return;
//        }
//        Gang g = GTM.getGangManager().getLoadedGang(gang);
//        if (g == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
//            return;
//        }
//        if (this.isAllied(g.name)) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang is allied to you already!"));
//            sender.sendMessage(Lang.GANGS.f("&7Want to unally this gang? Use /g neutral"));
//            return;
//        }
//        if (this.relationRequests.containsKey(g.name) && Objects.equals("ally", this.relationRequests.get(g.name))) {
//            this.relations.put(g.name, "ally");
//            g.addRelation(this.name, "ally");
//            this.relationRequests.remove(g.name);
//
////            Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');");
////            Core.sql.updateAsyncLater("insert into " + Core.name() + "_gangs_relations(gang1,gang2,relation) values('" + this.name + "','" + g.name + "','ally');");
//            ServerUtil.runTaskAsync(() -> {
//                BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');");
//                BaseDatabase.runCustomQuery("insert into " + Core.name() + "_gangs_relations(gang1,gang2,relation) values('" + this.name + "','" + g.name + "','ally');");
//            });
//
//            for (Player player : Bukkit.getOnlinePlayers())
//                if (g.isMember(player.getUniqueId()) || g.isLeader(player.getUniqueId()))
//                    player.sendMessage(Lang.GANGS.f("&7Your gang is now allied to &a" + this.name + "&7!"));
//                else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
//                    player.sendMessage(Lang.GANGS.f("&7Your gang is now allied to &a" + g.name + "&7!"));
//                }
//            sender.sendMessage(Lang.GANGS.f("&7You accepted the ally request from gang &a" + g.name + "&7!"));
//        }
//        g.addRelationRequest(this.name, "ally");
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (g.isMember(player.getUniqueId()) || g.isLeader(player.getUniqueId()))
//                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 from gang &a" + this.name
//                        + "&7 has requested to be allied to your gang!"));
//            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender)) {
//                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender)
//                        + "&7 has requested to be allied to &a" + g.name + "&7!"));
//            }
//        sender.sendMessage(Lang.GANGS.f("&7You sent an ally request to gang &a" + g.name + "&7!"));
//    }
//
//    public void neutral(Player sender, User user, GTMUser gtmUser, String gang) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        Gang g = GTM.getGangManager().getLoadedGang(gang);
//        if (g == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
//            return;
//        }
//        if (this.isNeutral(g.name)) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang is neutral to you already!"));
//            return;
//        }
//        if (this.relationRequests.containsKey(g.name) && Objects.equals("neutral", this.relationRequests.get(g.name))) {
//            this.relations.put(g.name, "neutral");
//            g.addRelation(this.name, "neutral");
//            this.relationRequests.remove(g.name);
//
////            Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');");
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');"));
//
//            for (Player player : Bukkit.getOnlinePlayers())
//                if (g.isMember(player.getUniqueId()) || g.isLeader(player.getUniqueId()))
//                    player.sendMessage(Lang.GANGS.f("&7Your gang is now neutral to &a" + this.name + "&7!"));
//                else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender))
//                    player.sendMessage(Lang.GANGS.f("&7Your gang is now neutral to &a" + g.name + "&7!"));
//            sender.sendMessage(Lang.GANGS.f("&7You accepted the neutral request from gang &a" + g.name + "&7!"));
//        }
//        g.addRelationRequest(this.name, "neutral");
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (g.isMember(player.getUniqueId()) || g.isLeader(player.getUniqueId()))
//                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 from gang &a" + this.name
//                        + "&7 has requested to be neutral to your gang!"));
//            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()) && !Objects.equals(player, sender))
//                player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender)
//                        + "&7 has requested to be neutral to &a" + g.name + "&7!"));
//        sender.sendMessage(Lang.GANGS.f("&7You sent a neutral request to gang &a" + g.name + "&7!"));
//    }
//
//    public void enemy(Player sender, User user, GTMUser gtmUser, String gang) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        Gang g = GTM.getGangManager().getLoadedGang(gang);
//        if (g == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang does not exist or no one in that gang is online!"));
//            return;
//        }
//        if (this.isEnemy(g.name)) {
//            sender.sendMessage(Lang.GANGS.f("&7That gang is an enemy to you already!"));
//            sender.sendMessage(Lang.GANGS.f("&7Want to unenemy this gang? Use /g neutral"));
//            return;
//        }
//        this.relations.put(g.name, "enemy");
//        g.addRelation(this.name, "enemy");
//        this.relationRequests.remove(g.name);
//
////        Core.sql.updateAsyncLater("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');");
////        Core.sql.updateAsyncLater("insert into " + Core.name() + "_gangs_relations(gang1,gang2,relation) values('" + this.name + "','" + g.name + "','enemy');");
//        ServerUtil.runTaskAsync(() -> {
//            BaseDatabase.runCustomQuery("delete from " + Core.name() + "_gangs_relations where (gang1='" + this.name + "' and gang2='" + g.name + "') or (gang1='" + g.name + "' and gang2='" + this.name + "');");
//            BaseDatabase.runCustomQuery("insert into " + Core.name() + "_gangs_relations(gang1,gang2,relation) values('" + this.name + "','" + g.name + "','enemy');");
//        });
//
//        for (Player player : Bukkit.getOnlinePlayers())
//            if (g.isMember(player.getUniqueId()) || g.isLeader(player.getUniqueId()))
//                player.sendMessage(Lang.GANGS.f("&7Your gang is now an enemy to gang &a" + this.name + "&7!"));
//            else if (this.isMember(player.getUniqueId()) || this.isLeader(player.getUniqueId()))
//                player.sendMessage(Lang.GANGS.f("&7Your gang is now an enemy to gang &a" + g.name + "&7!"));
//    }
//
//    public void invite(Player sender, User user, GTMUser gtmUser, Player target) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        if (target == null) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not online!"));
//            return;
//        }
//        if (Objects.equals(sender, target)) {
//            sender.sendMessage(Lang.GANGS.f("&7You can't invite yourself!"));
//            return;
//        }
//        if (this.isMember(target.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is already in your gang!"));
//            return;
//        }
//        if (this.hasMaxMembers()) {
//            sender.sendMessage(Lang.GANGS.f(
//                    "&7Your gang is full! Rank up or buy a rank at &astore.grandtheftmc.net&7 for more gang members!"));
//            return;
//        }
//        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
//        if (!targetGtmUser.canJoinGang(targetUser)) {
//            sender.sendMessage(Lang.GANGS.f("&7That player can not join a gang because he is not a &e&lHOMIE&7 yet!"));
//            target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 tried to invite you to gang &a" + this.name + "&7, but you need to be &a &e&lHOMIE&7 or &6&lVIP&7 to join a gang!"));
//            return;
//        }
//        targetGtmUser.addGangInvite(this.name);
//        sender.sendMessage(Lang.GANGS.f("&7You invited " + targetUser.getColoredName(target) + "&7 to your gang!"));
//        target.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 invited you to gang &a" + this.name
//                + "&7! Use &a\"/g join " + this.name + "\"&7 to join the gang!"));
//        this.sendToAllExcept(sender, Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 invited "
//                + targetUser.getColoredName(target) + "&7 to your gang!"));
//    }
//
//    public void accept(Player sender, User user, GTMUser gtmUser) {
//        if (!gtmUser.canJoinGang(user)) {
//            sender.sendMessage(Lang.GANGS.f("You must be " + GTMRank.HOMIE.getColoredNameBold() + "&7 or &6&lVIP&7 to join a gang! Check the &d&lMy Account&7 -> &a&lRanks&7 menu on your phone for more information."));
//            return;
//        }
//        if (this.isMember(sender.getUniqueId()) || this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are a member of this gang already!"));
//            return;
//        }
//        if (!gtmUser.isInvited(this.name)) {
//            sender.sendMessage(Lang.GANGS.f("&7You have not been invited to this gang!"));
//            return;
//        }
//        if (this.hasMaxMembers()) {
//            sender.sendMessage(Lang.GANGS.f("&7This gang is full!"));
//            return;
//        }
//        sender.sendMessage(Lang.GANGS.f("&7You joined the gang &a" + this.name + "&7!"));
//        gtmUser.setGang(this.name);
//        gtmUser.removeGangInvite(this.name);
//        this.sendToAll(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 joined your gang!"));
//        this.gangMembers.add(new GangMember(sender.getUniqueId(), sender.getName(), "member"));
//    }
//
//    public void kick(Player sender, User user, GTMUser gtmUser, String target) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        if (sender.getName().equalsIgnoreCase(target)) {
//            sender.sendMessage(Lang.GANGS.f("&7You can't kick yourself!"));
//            return;
//        }
//        if (!this.isMember(target)) {
//            sender.sendMessage(Lang.GANGS.f("&7That player is not in your gang!"));
//            return;
//        }
//        GangMember member = this.getMember(target);
//        this.gangMembers.remove(member);
//        Player player = Bukkit.getPlayer(target);
//        String name = target;
//        if (player == null) {
////            Core.sql.updateAsyncLater("update " + Core.name() + " set gang=null, gangRank='member' where name='" + name + "';");
//            String finalName = name;
//            ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gang=null, gangRank='member' where name='" + finalName + "';"));
//        }
//        else {
//            name = Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player);
//            GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
//            targetGtmUser.resetGang();
//            player.sendMessage(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 kicked you from the gang!"));
//        }
//        this.sendToAllExcept(sender, Lang.GANGS
//                .f("&7" + user.getColoredName(sender) + "&7 kicked &a" + name + "&7 from your gang!"));
//        sender.sendMessage(Lang.GANGS.f("&7You kicked &a" + name + "&7 from the gang!"));
//    }
//
//    public void leave(Player sender, User user, GTMUser gtmUser) {
//        if (this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f(
//                    "&7You are the leader of your gang! Please use &a\"/f leader <player\"&7 before leaving the gang!"));
//            return;
//        }
//        if (!this.isMember(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not a member of this gang!"));
//            return;
//        }
//        GangMember member = this.getMember(sender.getUniqueId());
//        this.gangMembers.remove(member);
//        gtmUser.resetGang();
//        sender.sendMessage(Lang.GANGS.f("&7You left the gang &a" + this.name + "&7!"));
//        this.sendToAll(Lang.GANGS.f("&7" + user.getColoredName(sender) + "&7 left the gang!"));
//    }
//
//    public void info(Player sender, User user, GTMUser gtmUser) {
//        sender.sendMessage(Utils.f(" &7&m---------------&7[&a&l " + this.name + " &7]&m---------------"));
//        sender.sendMessage(Utils.f("&a&lDescription: &7" + this.description));
//        int allies = 0;
//        int enemies = 0;
//        String a = "";
//        String e = "";
//        for (Map.Entry<String, String> entry : this.relations.entrySet()) {
//            String r = entry.getValue();
//            if (Objects.equals("ally", r)) {
//                a = a + (allies > 0 ? "&7, " : "") + "&a" + entry.getKey();
//                allies++;
//            } else if (Objects.equals("enemy", r)) {
//                e = e + (enemies > 0 ? "&c, " : "") + "&c" + entry.getKey();
//                enemies++;
//            }
//        }
//        int online = 0;
//        int offline = 0;
//        String on = "";
//        String off = "";
//        Player leader = Bukkit.getPlayer(this.leader);
//        if (leader != null) {
//            User u = Core.getUserManager().getLoadedUser(leader.getUniqueId());
//            on = on + (online > 0 ? "&7, " : "") + "&aLeader " + u.getColoredName(leader);
//            online++;
//        }
//        for (GangMember member : this.gangMembers) {
//            Player player = Bukkit.getPlayer(member.getUUID());
//            if (player == null) {
//                off = off + (offline > 0 ? "&7, " : "") + "&a" + (member.isColeader() ? "Coleader" : "") +
//                        member.getName();
//                offline++;
//            } else {
//                User u = Core.getUserManager().getLoadedUser(member.getUUID());
//                on = on + (online > 0 ? "&7, " : "") + "&a" + (member.isColeader() ? "Coleader" : "") +
//                        (u.isSpecial() ? u.getColoredName(player) : player.getName());
//                online++;
//            }
//        }
//        if (allies > 0)
//            sender.sendMessage(Utils.f("&a&lAllies&7(&a" + allies + "&7)&a: &7" + a));
//        if (enemies > 0)
//            sender.sendMessage(Utils.f("&c&lEnemies&7(&c" + enemies + "&7)&c: &7" + e));
//        sender.sendMessage(Utils.f("&a&lOnline Members&7(&a" + online + "&7)&a: &7" + on));
//        sender.sendMessage(Utils.f("&a&lOffline Members&7(&a" + offline + "&7)&a: &7" + off));
//    }
//
//    public String list(Player sender, Gang gang) {
//        int online = (int) this.gangMembers.stream().filter(member -> Bukkit.getPlayer(member.getUUID()) != null).count();
//        int total = this.gangMembers.size();
//        return (this.isAllied(gang.name) || this.equals(gang) ? "&a" : this.isEnemy(gang.name) ? "&c" : "&7") + "&l" + this.name + ": " + online + "&7/&a" + total + "&7 players online";
//    }
//
//    public boolean hasUpdated() {
//        return this.hasUpdated;
//    }
//
//    public void name(Player sender, User user, GTMUser gtmUser, String gangName) {
//        UUID uuid = sender.getUniqueId();
//        if (!this.isLeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the leader of this gang!"));
//            return;
//        }
//        if (!gangName.matches("^[a-zA-Z_0-9]+$")) {
//            sender.sendMessage(Lang.GANGS.f("&7Only a-z, A-Z, 0-9 and _ are allowed in a gang name!"));
//            return;
//        }
//        if (gangName.length() > 16 || gangName.length() < 3) {
//            sender.sendMessage(Lang.GANGS.f("&7The name of your gang needs to be 3-16 characters long!"));
//            return;
//        }
//        if (GTM.getGangManager().isLoaded(gangName)) {
//            sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
//            return;
//        }
//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                String s = null;
////                try {
////                    ResultSet rs = Core.sql
////                            .query("select name from " + Core.name() + "_gangs where name='" + gangName + "';");
////                    if (rs.next())
////                        s = rs.getString("name");
////                    rs.close();
////                } catch (SQLException e) {
////                    e.printStackTrace();
////                    return;
////                }
//                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                    try (PreparedStatement statement = connection.prepareStatement("select name from " + Core.name() + "_gangs where name='" + gangName + "';")) {
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
//                        if (sender == null)
//                            return;
//                        if (name != null) {
//                            sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
//                            return;
//                        }
//                        User user = Core.getUserManager().getLoadedUser(uuid);
//                        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
//                        Gang gang = gtmUser.getGang();
//                        gang.setName(gangName);
//                        sender.sendMessage(
//                                Lang.GANGS.f("&7You changed the name of your gang to &a" + gangName + "&7!"));
//                        Gang.this.sendToAllExcept(sender, Lang.GANGS.f("&7" + user.getColoredName(sender)
//                                + "&7 changed the name of your gang to &a" + gangName + "&7!"));
//                    }
//                }.runTask(GTM.getInstance());
//            }
//        }.runTaskAsynchronously(GTM.getInstance());
//    }
//
//    public void description(Player sender, User user, GTMUser gtmUser, String description) {
//        if (!this.isLeader(sender.getUniqueId()) && !this.isColeader(sender.getUniqueId())) {
//            sender.sendMessage(Lang.GANGS.f("&7You are not the (co-)leader of this gang!"));
//            return;
//        }
//        if (description.length() > 64 || description.length() < 3) {
//            sender.sendMessage(Lang.GANGS.f("&7The description must be between 3-64 characters long!"));
//            return;
//        }
//        this.setDescription(description);
//        sender.sendMessage(Lang.GANGS.f("&7You changed the description of your gang to &a" + description + "&7!"));
//        this.sendToAllExcept(sender, Lang.GANGS.f("&7" + user.getColoredName(sender)
//                + "&7 changed the description of your gang to &a" + description + "&7!"));
//    }
//
//    public void chat(Player player, User user, GTMUser gtmUser, String msg) {
//        Bukkit.getOnlinePlayers().stream().filter(p -> this.isMember(p.getUniqueId()) || this.isLeader(p.getUniqueId())).forEach(p -> p.sendMessage(Lang.GANGCHAT.f("&7[&a&l" + user.getColoredName(player) + "&7] &r") + Utils.fColor(msg)));
//        Core.getUserManager().getLoadedUsers().stream().filter(u -> (Boolean)u.getPref(Pref.SOCIALSPY)).forEach(u -> {
//            if(u != null && u.getUUID() != null) {
//                Player p = Bukkit.getPlayer(u.getUUID());
//                if(p != null) p.sendMessage(Lang.SS.f("&r" + player.getName() + ": /gc " + msg));
//            }
//        });
//    }
//
//    public void sendToAll(String msg) {
//        Bukkit.getOnlinePlayers().stream().filter(p -> this.isMember(p.getUniqueId()) || this.isLeader(p.getUniqueId())).forEach(p -> p.sendMessage(Utils.f(msg)));
//    }
//
//    public void sendToAllExcept(Player player, String msg) {
//        Bukkit.getOnlinePlayers().stream().filter(p -> (this.isMember(p.getUniqueId()) || this.isLeader(p.getUniqueId())) && !Objects.equals(p, player)).forEach(p -> p.sendMessage(Utils.f(msg)));
//    }
//
//    public int getUniqueId() {
//        return this.id;
//    }
//
//    protected void setUniqueId(int identifier) {
//        this.id = identifier;
//    }
}
