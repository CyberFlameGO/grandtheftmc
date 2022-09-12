package net.grandtheftmc.gtm.gang;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.gang.command.GangCommand;
import net.grandtheftmc.gtm.gang.command.GangDisableCommand;
import net.grandtheftmc.gtm.gang.member.GTMGangMember;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.gang.relation.GangRelation;
import net.grandtheftmc.gtm.gang.relation.RelationType;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class GangManager {

    public static boolean ENABLED = true;
    private static GangManager instance;

    private Set<Gang> gangs;
    private final HashMap<UUID, Integer> gangInvites;

    public GangManager(JavaPlugin plugin) {
        instance = this;
        this.gangs = Sets.newHashSet();
        this.gangInvites = Maps.newHashMap();

        new GangCommand(this);
        new GangDisableCommand();
        plugin.getCommand("gangchat").setExecutor(new GangChatCommand());

        ServerUtil.runTaskAsync(() -> {
            long start = System.currentTimeMillis();
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                this.gangs = GangDAO.getGangs(connection);
                System.out.println("Loaded gangs(" + gangs.size() + "), took " + (System.currentTimeMillis() - start) + "ms.");

                for (Gang gang : this.getGangs()) {
                    gang.setMembers(GangDAO.getMembers(connection, gang.getUniqueId()));
                    gang.setRelation(GangDAO.getRelations(connection, gang.getUniqueId()));

                    boolean found = false;
                    GangMember coOwner = null;
                    for (GangMember member : gang.getMembers()) {
                        if (member.getRole() == GangRole.LEADER)
                            found = true;
                        if (member.getRole() == GangRole.CO_LEADER && !found)
                            coOwner = member;
                    }

                    if (!found && coOwner != null) {
                        gang.setOwner(coOwner.getUniqueId());
                        System.out.println("Owner set for gang (" + gang.getUniqueId() + ")");
                    }
                }

                System.out.println("Loaded gangs(" + gangs.size() + ")");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public static GangManager getInstance() {
        return instance;
    }

    public Set<Gang> getGangs() {
        return gangs;
    }

    public void addGang(Gang gang) {
        this.gangs.add(gang);
    }

    public void removeGang(Gang gang) {
        this.gangs.remove(gang);
    }

    public Optional<Gang> getGang(int id) {
        return this.gangs.stream().filter(gang -> gang.getUniqueId() == id).findFirst();
    }

    public Optional<Gang> getGang(String name) {
        return this.gangs.stream().filter(gang -> gang.getName().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Gang> getGangByMember(UUID uuid) {
        return this.gangs.stream().filter(gang -> gang.getMembers().stream().anyMatch(mem -> mem.getUniqueId().equals(uuid))).findFirst();
    }

    public Integer getGangInvites(UUID uuid) {
        return this.gangInvites.getOrDefault(uuid, -1);
    }

    public void addGangInvite(UUID uuid, int id) {
        this.gangInvites.put(uuid, id);
    }

    public void removeGangInvite(UUID uuid) {
        this.gangInvites.remove(uuid);
    }

    public boolean isInvited(UUID uuid, int id) {
        return this.gangInvites.containsKey(uuid) && this.gangInvites.get(uuid) == id;
    }

    public void createGang(Player sender, String name) {
        UUID uuid = sender.getUniqueId();
        String gangName = name;

        User user = Core.getUserManager().getLoadedUser(uuid);
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);

        if (!gtmUser.isRank(GTMRank.GANGSTER) && user.getUserRank().isHigherThan(UserRank.VIP)) {
            sender.sendMessage(Lang.GANGS.f("&7You must be a &e&l" + GTMRank.GANGSTER.getColoredNameBold() + "&7 or &a&lPREMIUM&7 to create a gang! Check the &d&lMy Account&7 -> &a&lRanks&7 menu on your phone for more information."));
            return;
        }

        if (this.getGangByMember(uuid).orElse(null) != null) {
            sender.sendMessage(Lang.GANGS.f("&7You are in a gang already!"));
            return;
        }

        if (!name.matches("^[a-zA-Z_0-9]+$")) {
            sender.sendMessage(Lang.GANGS.f("&7Only a-z, A-Z, 0-9 and _ are allowed in a gang name!"));
            return;
        }

        if (name.length() > 16 || name.length() < 3) {
            sender.sendMessage(Lang.GANGS.f("&7The name of your gang needs to be 3-16 characters long!"));
            return;
        }

        if (this.getGang(gangName).orElse(null) != null) {
            sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
            return;
        }

        if (!gtmUser.hasMoney(500000)) {
            sender.sendMessage(Lang.GANGS.f("&7You don't have &a$&l500,000&7 to create this gang!"));
            return;
        }

        ServerUtil.runTaskAsync(() -> {
            boolean b = false;
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                b = GangDAO.isGangExisting(connection, gangName);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            boolean exists = b;

            ServerUtil.runTask(() -> {
                Player sender2 = Bukkit.getPlayer(uuid);
                if (sender2 == null) return;
                if (exists) {
                    sender.sendMessage(Lang.GANGS.f("&7A gang with that name already exists!"));
                    return;
                }

                if (!gtmUser.hasMoney(500000)) {
                    sender.sendMessage(Lang.GANGS.f("&7You don't have &a$&l500,000&7 to create this gang!"));
                    return;
                }

                gtmUser.takeMoney(500000);
                GTMUtils.updateBoard(sender2, user, gtmUser);
                int maxMembers = this.getMaxGangMembers(sender2, user);

                ServerUtil.runTaskAsync(() -> {
                    Gang gang = null;
                    try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                        gang = GangDAO.createGang(connection, sender2.getUniqueId(), gangName, maxMembers);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                    if(gang == null) return;
                    this.addGang(gang);
                    gang.addMember(new GTMGangMember(sender2.getUniqueId(), sender2.getName(), GangRole.LEADER));

                    ServerUtil.runTask(() -> {
                        Player sender3 = Bukkit.getPlayer(uuid);
                        if (sender3 == null) return;

                        sender3.sendMessage(Lang.GANGS.f("&7You created a gang with the name &a" + gangName + "&7!"));
                    });
                });
            });
        });
    }

    public int getMaxGangMembers(Player player, User user) {
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());

        int amnt = gtmUser.getRank().getGangMembers() + GTMUtils.getGangMembers(user.getUserRank());
        for (int i = 20; i > 0; i--)
            if (player.hasPermission("gangs.members." + i)) {
                amnt = i;
                break;
            }
        for (int i = 20; i > 0; i--)
            if (player.hasPermission("gangs.extramembers." + i)) {
                amnt += i;
                break;
            }
        return amnt;
    }

    public void info(Gang gang, Player sender, User user, GTMUser gtmUser) {
        sender.sendMessage(Utils.f(" &7&m---------------&7[&a&l " + gang.getName() + " &7]&m---------------"));
        sender.sendMessage(Utils.f("&a&lDescription: &7" + gang.getDescription()));

        StringBuilder ally = new StringBuilder(), enemy = new StringBuilder();
        int iAlly = 0, iEnemy = 0;
        for (GangRelation relation : gang.getRelations()) {
            if (relation.getRelationType() == RelationType.ALLY) {
                ally.append(iAlly > 0 ? "&7, " : "").append("&a").append(relation.getRelativeName());
                iAlly++;
            }
            else if (relation.getRelationType() == RelationType.ENEMY) {
                enemy.append(iEnemy > 0 ? "&c, " : "").append("&c").append(relation.getRelativeName());
                iEnemy++;
            }
        }

        int online = 0, offline = 0;
        StringBuilder on = new StringBuilder(), off = new StringBuilder();
        Player leader = Bukkit.getPlayer(gang.getOwner());
        if (leader != null) {
            User u = Core.getUserManager().getLoadedUser(leader.getUniqueId());
            on.append(online > 0 ? "&7, " : "").append("&aLeader ").append(u.getColoredName(leader));
//            members[0].append("&aLeader ").append(u.getColoredName(leader)).append("&7, ");
            online++;
        }

        for (GangMember member : gang.getMembers()) {
            if (member.getRole() == GangRole.LEADER) continue;
            Player player = Bukkit.getPlayer(member.getUniqueId());
            if (player == null) {
                off.append(offline > 0 ? "&7, " : "").append("&a").append(member.isCoLeader() ? "Coleader " : "").append(member.getName());
                offline++;
                continue;
            }

            User u = Core.getUserManager().getLoadedUser(member.getUniqueId());
            on.append(online > 0 ? "&7, " : "").append("&a").append(member.isCoLeader() ? "Coleader " : "").append(u.isSpecial() ? u.getColoredName(player) : player.getName());
            online++;
        }

        if (iAlly > 0) sender.sendMessage(Utils.f("&a&lAllies&7(&a" + iAlly + "&7)&a: &7" + ally.toString()));
        if (iEnemy > 0) sender.sendMessage(Utils.f("&c&lEnemies&7(&c" + iEnemy + "&7)&c: &7" + enemy.toString()));
        sender.sendMessage(Utils.f("&a&lOnline Members&7(&a" + online + "&7)&a: &7" + on));
        sender.sendMessage(Utils.f("&a&lOffline Members&7(&a" + offline + "&7)&a: &7" + off));
    }
}
