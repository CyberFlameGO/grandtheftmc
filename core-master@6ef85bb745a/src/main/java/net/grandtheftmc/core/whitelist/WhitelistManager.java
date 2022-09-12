package net.grandtheftmc.core.whitelist;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Component;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

public class WhitelistManager implements Component<WhitelistManager, Core> {

    private final List<WhitelistedUser> whitelistedUsers = new ArrayList<>();
    private boolean enabled;
    private UserRank bypassRank;

    public WhitelistManager() {
        this.load();
        this.startSchedule();
    }

    @Override
    public WhitelistManager onDisable(Core plugin) {
        if(!this.whitelistedUsers.isEmpty())
            this.whitelistedUsers.clear();
        return this;
    }

    public void whitelist(String name) {
        if (this.isWhitelisted(name))
            return;
        WhitelistedUser wu = new WhitelistedUser(null, name);
        this.whitelistedUsers.add(wu);
    }

    public void unwhitelist(String name) {
        WhitelistedUser wu = this.getWhitelistedUser(name);
        if (wu != null)
            this.whitelistedUsers.remove(wu);

    }

    private boolean isWhitelisted(String name) {
        return this.getWhitelistedUser(name) != null;
    }

    public void updateUUID(String name, UUID uuid) {
        WhitelistedUser wu = this.getWhitelistedUser(name);
        wu.setUuid(uuid);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean en) {
        this.enabled = en;
    }

    public List<WhitelistedUser> getWhitelistedUsers() {
        return this.whitelistedUsers;
    }

    public WhitelistedUser getWhitelistedUser(String name) {
        return this.whitelistedUsers.stream().filter(wu -> wu.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public WhitelistedUser getWhitelistedUser(UUID uuid) {
        return this.whitelistedUsers.stream().filter(wu -> Objects.equals(uuid, wu.getUuid())).findFirst().orElse(null);
    }

    public void save(boolean shutdown) {
        YamlConfiguration c = Core.getSettings().getWhitelistConfig();
        for (String key : c.getKeys(false))
            c.set(key, null);
        List<String> whitelistedNames = new ArrayList<>();
        c.set("enabled", this.enabled);
        c.set("bypassRank", this.bypassRank == null ? null : this.bypassRank.getName());
        for (WhitelistedUser wu : this.whitelistedUsers) {
            if (wu.getUuid() == null) {
                whitelistedNames.add(wu.getName());
                continue;
            }
            c.set("whitelist." + wu.getUuid(), wu.getName());
        }
        c.set("whitelistedNames", whitelistedNames);
        Utils.saveConfig(c, "whitelist");

    }

    public void load() {
        YamlConfiguration c = Core.getSettings().getWhitelistConfig();
        if (c.get("enabled") != null)
            this.enabled = c.getBoolean("enabled");
        if (c.get("bypassRank") != null) this.bypassRank = UserRank.getUserRankOrNull(c.getString("bypassRank"));
        if (c.get("whitelistedNames") != null)
            this.whitelistedUsers.addAll(c.getStringList("whitelistedNames").stream().map(s -> new WhitelistedUser(null, s)).collect(Collectors.toList()));
        if (c.get("whitelist") != null)
            this.whitelistedUsers.addAll(c.getConfigurationSection("whitelist").getKeys(false).stream().map(uuidString -> new WhitelistedUser(UUID.fromString(uuidString), c.getString("whitelist." + uuidString))).collect(Collectors.toList()));
        Utils.saveConfig(c, "whitelist");

    }

    public void startSchedule() {
        BukkitScheduler sched = Bukkit.getScheduler();
        sched.scheduleSyncRepeatingTask(Core.getInstance(), () -> Core.getWhitelistManager().save(false), 72000, 72000);
    }

    public UserRank getBypassRank() {
        return this.bypassRank;
    }

    public void setBypassRank(UserRank bypassRank) {
        this.bypassRank = bypassRank;
    }
}
