package net.grandtheftmc.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;

public class Settings {

    private final List<String> stopHungerChange = new ArrayList<>();
    private final List<String> stopWeatherChange = new ArrayList<>();
    private final List<String> stopChunkLoad = new ArrayList<>();
    private boolean stopLoadDefaultWorld;
    private boolean deletePlayerDatFiles;
    private GameMode defaultGameMode = GameMode.ADVENTURE;
    private boolean joinLeaveMessagesEnabled;
    private boolean useTeleportFix = true;
    private boolean loadCosmetics;
    private boolean serverWarperEnabled = true;
    private boolean statsMenuEnabled = true;
    private boolean useAnnouncer;
    private boolean tokenShopEnabled;
    private boolean canCraft;
    private boolean canOpenChests;
    private boolean canInteractInventory;
    private boolean petsVulnerable;
    private int maxPlayers = 300;

    private int number;
    private ServerType type;
    private UserRank rankToJoin;

    /** Whether or not this is the main Grand Theft Minecart network */
    private boolean sister = false;

    private String networkName = "Unknown",
                   networkShortName = "Unknown",
                   networkIP = "unknown.com",
                   websiteLink = "unknown.com",
                   storeLink = "store.unknown.com",
                   server_GTM_name = "null",
                   server_GTM_shortName = "null";

    private String host = "error";
    private String port = "error";
    private String database = "error";
    private String user = "error";
    private String password = "error";

    private YamlConfiguration coreConfig;
    private YamlConfiguration mysqlConfig;
    private YamlConfiguration serversConfig;
    private YamlConfiguration joinSignsConfig;
    private YamlConfiguration permsConfig;
    private YamlConfiguration whitelistConfig;
    private YamlConfiguration announcerConfig;
    private YamlConfiguration votingConfig;
    private YamlConfiguration tokenShopConfig;
    private YamlConfiguration tutorialsConfig;
    private YamlConfiguration leaderBoardsConfig;
    private YamlConfiguration socialSpyConfig;
    private YamlConfiguration worldsConfig;
    private YamlConfiguration rewardsConfig;
    private YamlConfiguration rulesConfig;
    private YamlConfiguration nametagsConfig;
    private YamlConfiguration helpConfig;
    private YamlConfiguration cratesConfig;
    private YamlConfiguration crateRewardsConfig;
    private YamlConfiguration redisConfig;

    public boolean useEditMode() {
        return true;
    }

    public boolean isUseEditMode() {
        return true;
    }

    public void setUseEditMode(boolean useEditMode) {
    }

    public boolean stopHungerChange(String world) {
        return this.stopHungerChange.contains(world);
    }

    public void setStopHungerChange(String world) {
        if (!this.stopHungerChange.contains(world))
            this.stopHungerChange.add(world);
    }

    public void removeStopHungerChange(String world) {
        this.stopHungerChange.remove(world);
    }

    public boolean stopWeatherChange(String world) {
        return this.stopWeatherChange.contains(world);
    }

    public void setStopWeatherChange(String world) {
        if (!this.stopWeatherChange.contains(world))
            this.stopWeatherChange.add(world);
    }

    public void removeStopWeatherChange(String world) {
        this.stopWeatherChange.remove(world);
    }

    public boolean stopChunkLoad(String world) {
        return this.stopChunkLoad.contains(world);
    }

    public void setStopChunkLoad(String world) {
        if (!this.stopChunkLoad.contains(world))
            this.stopChunkLoad.add(world);
    }

    public void removeStopChunkLoad(String world) {
        this.stopChunkLoad.remove(world);
    }

    public GameMode getDefaultGameMode() {
        return this.defaultGameMode == null ? GameMode.ADVENTURE : this.defaultGameMode;
    }

    public void setDefaultGameMode(GameMode g) {
        this.defaultGameMode = g;
    }

    public boolean getJoinLeaveMessagesEnabled() {
        return this.joinLeaveMessagesEnabled;
    }

    public void setJoinLeaveMessagesEnabled(boolean b) {
        this.joinLeaveMessagesEnabled = b;
    }

    public boolean getUseTeleportFix() {
        return this.useTeleportFix;
    }

    public void setUseTeleportFix(boolean b) {
        this.useTeleportFix = b;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public ServerType getType() {
        return this.type;
    }

    public void setType(ServerType type) {
        this.type = type;
    }

    public String getDisplayName() {
        return Utils.f(this.type.getDisplayName() + " &a&l" + this.number);
    }

    public String name() {
        return this.type.getName() + this.number;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return this.port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabase() {
        return this.database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public YamlConfiguration getCoreConfig() {
        return this.coreConfig;
    }

    public void setCoreConfig(YamlConfiguration c) {
        this.coreConfig = c;
    }

    public YamlConfiguration getRedisConfig() {
        return this.redisConfig;
    }

    public void setRedisConfig(YamlConfiguration c) {
        this.redisConfig = c;
    }

    public YamlConfiguration getMySQLConfig() {
        return this.mysqlConfig;
    }

    public void setMySQLConfig(YamlConfiguration c) {
        this.mysqlConfig = c;
    }

    public YamlConfiguration getServersConfig() {
        return this.serversConfig;
    }

    public void setServersConfig(YamlConfiguration c) {
        this.serversConfig = c;
    }

    public YamlConfiguration getJoinSignsConfig() {
        return this.joinSignsConfig;
    }

    public void setJoinSignsConfig(YamlConfiguration c) {
        this.joinSignsConfig = c;
    }

    public YamlConfiguration getPermsConfig() {
        return this.permsConfig;
    }

    public void setPermsConfig(YamlConfiguration c) {
        this.permsConfig = c;
    }

    public YamlConfiguration getWhitelistConfig() {
        return this.whitelistConfig;
    }

    public void setWhitelistConfig(YamlConfiguration c) {
        this.whitelistConfig = c;
    }

    public boolean stopLoadDefaultWorld() {
        return this.stopLoadDefaultWorld;
    }

    public void setStopLoadDefaultWorld(boolean b) {
        this.stopLoadDefaultWorld = b;
    }

    public boolean deletePlayerDatFiles() {
        return this.deletePlayerDatFiles;
    }

    public void setDeletePlayerDatFiles(boolean b) {
        this.deletePlayerDatFiles = b;
    }

    /*
     * public boolean loadUserKits() { return loadUserKits; }
     *
     * public void setLoadUserKits(boolean loadUserKits) { this.loadUserKits =
     * loadUserKits; }
     *
     * public boolean loadUserStats() { return loadUserStats; }
     *
     * public void setLoadUserStats(boolean loadUserStats) { this.loadUserStats
     * = loadUserStats; }
     */

    public boolean serverWarperEnabled() {
        return this.serverWarperEnabled;
    }

    public void setServerWarperEnabled(boolean b) {
        this.serverWarperEnabled = b;
    }

    public YamlConfiguration getAnnouncerConfig() {
        return this.announcerConfig;
    }

    public void setAnnouncerConfig(YamlConfiguration announcerConfig) {
        this.announcerConfig = announcerConfig;
    }

    public boolean useAnnouncer() {
        return this.useAnnouncer;
    }

    public void setUseAnnouncer(boolean useAnnouncer) {
        this.useAnnouncer = useAnnouncer;
    }

    public YamlConfiguration getVotingConfig() {
        return this.votingConfig;
    }

    public void setVotingConfig(YamlConfiguration votingConfig) {
        this.votingConfig = votingConfig;
    }

    public boolean statsMenuEnabled() {
        return this.statsMenuEnabled;
    }

    public void setStatsMenuEnabled(boolean statsMenuEnabled) {
        this.statsMenuEnabled = statsMenuEnabled;
    }

    public boolean isTokenShopEnabled() {
        return this.tokenShopEnabled;
    }

    public void setTokenShopEnabled(boolean tokenShopEnabled) {
        this.tokenShopEnabled = tokenShopEnabled;
    }

    public YamlConfiguration getTokenShopConfig() {
        return this.tokenShopConfig;
    }

    public void setTokenShopConfig(YamlConfiguration tokenShopConfig) {
        this.tokenShopConfig = tokenShopConfig;
    }

    public YamlConfiguration getTutorialsConfig() {
        return this.tutorialsConfig;
    }

    public void setTutorialsConfig(YamlConfiguration tutorialsConfig) {
        this.tutorialsConfig = tutorialsConfig;
    }

    public boolean canCraft() {
        return this.canCraft;
    }

    public void setCanCraft(boolean canCraft) {
        this.canCraft = canCraft;
    }

    public boolean canOpenChests() {
        return this.canOpenChests;
    }

    public void setCanOpenChests(boolean canOpenChests) {
        this.canOpenChests = canOpenChests;
    }

    public boolean canInteractInventory() {
        return this.canInteractInventory;
    }

    public void setCanInteractInventory(boolean canInteractInventory) {
        this.canInteractInventory = canInteractInventory;
    }

    public YamlConfiguration getLeaderBoardsConfig() {
        return this.leaderBoardsConfig;
    }

    public void setLeaderBoardsConfig(YamlConfiguration leaderBoardsConfig) {
        this.leaderBoardsConfig = leaderBoardsConfig;
    }

    public boolean useHolographicDisplays() {
        return Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
    }

    public YamlConfiguration getSocialSpyConfig() {
        return this.socialSpyConfig;
    }

    public void setSocialSpyConfig(YamlConfiguration socialSpyConfig) {
        this.socialSpyConfig = socialSpyConfig;
    }

    public UserRank getRankToJoin() {
        return this.rankToJoin;
    }

    public void setRankToJoin(UserRank rankToJoin) {
        this.rankToJoin = rankToJoin;
    }

    public boolean isSister() {
        return sister;
    }

    public void setSister(boolean sister) {
        this.sister = sister;
    }

    public String getNetworkName() {
        return networkName;
    }

    public void setNetworkName(String networkName) {
        this.networkName = ChatColor.translateAlternateColorCodes('&', networkName);
    }

    public String getNetworkShortName() {
        return networkShortName;
    }

    public void setNetworkShortName(String networkShortName) {
        this.networkShortName = ChatColor.translateAlternateColorCodes('&', networkShortName);
    }

    public String getNetworkIP() {
        return networkIP;
    }

    public void setNetworkIP(String networkIP) {
        this.networkIP = networkIP;
    }

    public String getWebsiteLink() {
        return websiteLink;
    }

    public void setWebsiteLink(String websiteLink) {
        this.websiteLink = websiteLink;
    }

    public String getStoreLink() {
        return storeLink;
    }

    public void setStoreLink(String storeLink) {
        this.storeLink = storeLink;
    }

    public String getServer_GTM_name() {
        return server_GTM_name;
    }

    public void setServer_GTM_name(String server_GTM_name) {
        this.server_GTM_name = ChatColor.translateAlternateColorCodes('&', server_GTM_name);
    }

    public String getServer_GTM_shortName() {
        return server_GTM_shortName;
    }

    public void setServer_GTM_shortName(String server_GTM_shortName) {
        this.server_GTM_shortName = ChatColor.translateAlternateColorCodes('&', server_GTM_shortName);
    }

//    public void setServer_GTM_longName(String server_GTM_longName) {
//        this.server_GTM_longName = server_GTM_longName;
//    }
//
//    public String getServer_GTM_longName() {
//        return server_GTM_longName;
//    }

    public boolean needRankToJoin() {
        return this.rankToJoin != null;
    }

    public YamlConfiguration getWorldsConfig() {
        return this.worldsConfig;
    }

    public void setWorldsConfig(YamlConfiguration worldsConfig) {
        this.worldsConfig = worldsConfig;
    }

    public boolean loadCosmetics() {
        return this.loadCosmetics;
    }

    public void setLoadCosmetics(boolean b) {
        this.loadCosmetics = b;
    }

    public boolean isPetsVulnerable() {
        return this.petsVulnerable;
    }

    public void setPetsVulnerable(boolean petsVulnerable) {
        this.petsVulnerable = petsVulnerable;
    }

    public YamlConfiguration getRewardsConfig() {
        return this.rewardsConfig;
    }

    public void setRewardsConfig(YamlConfiguration rewardsConfig) {
        this.rewardsConfig = rewardsConfig;
    }

    public YamlConfiguration getRulesConfig() {
        return this.rulesConfig;
    }

    public void setRulesConfig(YamlConfiguration rulesConfig) {
        this.rulesConfig = rulesConfig;
    }

    public YamlConfiguration getNametagsConfig() {
        return this.nametagsConfig;
    }

    public void setNametagsConfig(YamlConfiguration nametagsConfig) {
        this.nametagsConfig = nametagsConfig;
    }

    public YamlConfiguration getHelpConfig() {
        return this.helpConfig;
    }

    public void setHelpConfig(YamlConfiguration c) {
        this.helpConfig = c;
    }

    public YamlConfiguration getCratesConfig() {
        return this.cratesConfig;
    }

    public void setCratesConfig(YamlConfiguration c) {
        this.cratesConfig = c;
    }

    public YamlConfiguration getCrateRewardsConfig() {
        return this.crateRewardsConfig;
    }

    public void setCrateRewardsConfig(YamlConfiguration c) {
        this.crateRewardsConfig = c;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    /**
     * Whether or not this is the main grand theft minecart network.
     * <p>
     * Note: This is really only used to flag this as the Main or Sister network.
     * </p>
     * 
     * @return {@code true} if this is the main network, {@code false} otherwise.
     */
	public boolean isMainNetwork() {
		return !sister;
	}

	/**
	 * Set whether or not this is the main grand theft minecart network.
	 * 
	 * @param isMainNetwork - {@code true} if this is the main network, {@code false} if it's the sister network.
	 */
	public void setMainNetwork(boolean isMainNetwork) {
		this.sister = !isMainNetwork;
	}
}
