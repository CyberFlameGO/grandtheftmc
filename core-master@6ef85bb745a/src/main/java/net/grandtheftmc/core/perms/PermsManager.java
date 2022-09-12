package net.grandtheftmc.core.perms;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.events.GetPermsEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermsManager implements Component<PermsManager, Core> {

    private final List<UserPerms> userPerms = new ArrayList<>();
    private List<RankPerms> rankPerms = new ArrayList<>();

    public PermsManager() {
        this.loadPerms();
    }

    @Override
    public PermsManager onDisable(Core plugin) {
        this.userPerms.forEach(e -> e.onDisable(plugin));
        this.rankPerms.forEach(e -> e.onDisable(plugin));

        this.userPerms.clear();
        this.rankPerms.clear();
        return this;
    }

    public RankPerms getRankPerms(UserRank rank) {
        for (RankPerms p : this.rankPerms)
            if (p.getRank() == rank)
                return p;
        return null;
    }

    public UserPerms getUserPerms(UUID uuid) {
        for (UserPerms u : this.userPerms)
            if (u.getUUID().equals(uuid))
                return u;
        return null;
    }

    public List<String> getPerms(UUID uuid) {
        List<String> perms = new ArrayList<>();
        UserPerms u = this.getUserPerms(uuid);
        if (u != null)
            perms.addAll(u.getPerms());
        GetPermsEvent e = new GetPermsEvent(uuid);
        Bukkit.getPluginManager().callEvent(e);
        perms.addAll(e.getPerms());
        return perms;
    }

    public List<String> getAllPerms(UserRank ur, UUID uuid) {
        List<String> perms = new ArrayList<>();
        for (UserRank r : UserRank.getUserRanks()) {
            perms.addAll(this.getRankPerms(r).getPerms());
            if (r == ur)
                break;
        }
        perms.addAll(this.getPerms(uuid));
        return perms;

    }

    public void loadPerms() {
        this.rankPerms = new ArrayList<>();
        FileConfiguration c = Core.getSettings().getPermsConfig();
        if (c == null)
            return;
        for (UserRank rank : UserRank.getUserRanks()) {
            List<String> ls = c.getStringList("ranks." + rank.getName().toLowerCase());
            RankPerms perms = new RankPerms(rank, ls == null ? new ArrayList<>() : ls);
            this.rankPerms.add(perms);

        }
        if (c.getConfigurationSection("players") != null)
            for (String s : c.getConfigurationSection("players").getKeys(false)) {
                UUID uuid = UUID.fromString(s);
                if (uuid == null)
                    break;
                List<String> ls = c.getStringList("players." + s);
                UserPerms perms = new UserPerms(uuid, ls);
                this.userPerms.add(perms);
            }
        for (Player p : Bukkit.getOnlinePlayers())
            this.updatePerms(p.getUniqueId());

    }

    public void savePerms(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getPermsConfig();
        for (String s : c.getKeys(false)) c.set(s, null);
        for (RankPerms rank : this.rankPerms)
            c.set("ranks." + rank.getRank().getName().toLowerCase(), rank.getPerms());
        for (UserPerms user : this.userPerms)
            c.set("players." + user.getUUID(), this.getPerms(user.getUUID()));
        Utils.saveConfig(c, "perms");
    }

    public boolean hasPerm(UserRank ur, String perm) {
        for (String p : this.getRankPerms(ur).getPerms())
            if (p.equalsIgnoreCase(perm))
                return true;
        return false;

    }

    public boolean hasPerm(UUID uuid, String perm) {
        return this.getPerms(uuid).stream().anyMatch(p -> p.equalsIgnoreCase(perm));
    }

    public void addPerm(UUID uuid, String perm) {
        UserPerms user = this.getUserPerms(uuid);
        if (user == null) {
            user = new UserPerms(uuid);
            this.userPerms.add(user);
        }
        user.addPerm(perm);
        this.savePerms(false);
    }

    public void removePerm(UUID uuid, String perm) {
        UserPerms user = this.getUserPerms(uuid);
        if (user != null)
            user.removePerm(perm);
        this.savePerms(false);
    }

    public void updatePerms(UserRank ur) {
        UserManager um = Core.getUserManager();
        for (Player p : Bukkit.getOnlinePlayers()) {
            User u = um.getLoadedUser(p.getUniqueId());
            if (u != null)
                if (ur == u.getUserRank())
                    u.setPerms(p);
        }
        this.savePerms(false);
    }

    public void updatePerms(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null)
            return;
        User u = Core.getUserManager().getLoadedUser(uuid);
        u.setPerms(player);
        this.savePerms(false);
    }
}
