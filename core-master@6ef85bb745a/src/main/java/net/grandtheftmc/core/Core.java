package net.grandtheftmc.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.j0ach1mmall3.ultimatecosmetics.Main;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import io.sentry.SentryClient;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.buycraft.plugin.internal.okhttp3.OkHttpClient;
import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.achivements.AchievementCommand;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.announcer.Announcer;
import net.grandtheftmc.core.announcer.AnnouncerCommand;
import net.grandtheftmc.core.anticheat.Anticheat;
import net.grandtheftmc.core.casino.coins.CoinManager;
import net.grandtheftmc.core.commands.BucksCommand;
import net.grandtheftmc.core.commands.BungeeCommand;
import net.grandtheftmc.core.commands.ChatFilterCommand;
import net.grandtheftmc.core.commands.ClearChatCommand;
import net.grandtheftmc.core.commands.ConfigCommand;
import net.grandtheftmc.core.commands.CooldownCommand;
import net.grandtheftmc.core.commands.CouponCreditsCommand;
import net.grandtheftmc.core.commands.CrateCommand;
import net.grandtheftmc.core.commands.CrowbarCommand;
import net.grandtheftmc.core.commands.DiscordCommand;
import net.grandtheftmc.core.commands.EditModeCommand;
import net.grandtheftmc.core.commands.EventTagCommand;
import net.grandtheftmc.core.commands.FacebookCommand;
import net.grandtheftmc.core.commands.ForumRankCommand;
import net.grandtheftmc.core.commands.GlobalMuteCommand;
import net.grandtheftmc.core.commands.IgnoreCommand;
import net.grandtheftmc.core.commands.InfoCommand;
import net.grandtheftmc.core.commands.ListCommand;
import net.grandtheftmc.core.commands.MaxPlayersCommand;
import net.grandtheftmc.core.commands.MessageCommand;
import net.grandtheftmc.core.commands.OpenMenuCommand;
import net.grandtheftmc.core.commands.PlaytimeCommand;
import net.grandtheftmc.core.commands.PrefsCommand;
import net.grandtheftmc.core.commands.RankCommand;
import net.grandtheftmc.core.commands.ReplyCommand;
import net.grandtheftmc.core.commands.RewardsCommand;
import net.grandtheftmc.core.commands.RulesCommand;
import net.grandtheftmc.core.commands.SaveCommand;
import net.grandtheftmc.core.commands.ServerCommand;
import net.grandtheftmc.core.commands.SocialSpyCommand;
import net.grandtheftmc.core.commands.SpankCommand;
import net.grandtheftmc.core.commands.StoreCommand;
import net.grandtheftmc.core.commands.TokensCommand;
import net.grandtheftmc.core.commands.TradeCommand;
import net.grandtheftmc.core.commands.TrialCommand;
import net.grandtheftmc.core.commands.TwitterCommand;
import net.grandtheftmc.core.commands.VotestreakCommand;
import net.grandtheftmc.core.commands.WhitelistCommand;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.MutexDAO;
import net.grandtheftmc.core.database.dao.ServerStatsDAO;
import net.grandtheftmc.core.editmode.BlockChange;
import net.grandtheftmc.core.editmode.BreakBlock;
import net.grandtheftmc.core.editmode.Craft;
import net.grandtheftmc.core.editmode.HangingBreak;
import net.grandtheftmc.core.editmode.Liquid;
import net.grandtheftmc.core.editmode.PlaceBlock;
import net.grandtheftmc.core.editmode.WorldManager;
import net.grandtheftmc.core.event.EventCommand;
import net.grandtheftmc.core.events.ServerSaveEvent;
import net.grandtheftmc.core.handlers.chat.ChatManager;
import net.grandtheftmc.core.inventory.CoreMenuHandler;
import net.grandtheftmc.core.leaderboards.LeaderBoardManager;
import net.grandtheftmc.core.listeners.Chat;
import net.grandtheftmc.core.listeners.ChunkLoad;
import net.grandtheftmc.core.listeners.CommandListener;
import net.grandtheftmc.core.listeners.Damage;
import net.grandtheftmc.core.listeners.HopperComponent;
import net.grandtheftmc.core.listeners.HungerChange;
import net.grandtheftmc.core.listeners.InventoryClick;
import net.grandtheftmc.core.listeners.Join;
import net.grandtheftmc.core.listeners.Leave;
import net.grandtheftmc.core.listeners.Login;
import net.grandtheftmc.core.listeners.Move;
import net.grandtheftmc.core.listeners.PetListener;
import net.grandtheftmc.core.listeners.PlaywireRecieve;
import net.grandtheftmc.core.listeners.Save;
import net.grandtheftmc.core.listeners.SignChange;
import net.grandtheftmc.core.listeners.SwapHandItems;
import net.grandtheftmc.core.listeners.Teleport;
import net.grandtheftmc.core.listeners.UserStateTransactionListener;
import net.grandtheftmc.core.listeners.WeatherChange;
import net.grandtheftmc.core.menus.MenuListener;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.nametags.NametagCommand;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.npc.NPCManager;
import net.grandtheftmc.core.perms.PermsManager;
import net.grandtheftmc.core.playwire.PlaywireManager;
import net.grandtheftmc.core.redis.RedisFactory;
import net.grandtheftmc.core.redis.RedisManager;
import net.grandtheftmc.core.redis.listener.QueueListener;
import net.grandtheftmc.core.redis.listener.UserStateTransactionCheckListener;
import net.grandtheftmc.core.redis.listener.VoteNotificationListener;
import net.grandtheftmc.core.resourcepack.ResourcePackManager;
import net.grandtheftmc.core.servers.ServerManager;
import net.grandtheftmc.core.servers.ServerPingListener;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.stat.StatFactory;
import net.grandtheftmc.core.task.common.AntiAFK;
import net.grandtheftmc.core.task.common.BossBarTask;
import net.grandtheftmc.core.trading.TradeManager;
import net.grandtheftmc.core.tutorials.Help;
import net.grandtheftmc.core.tutorials.NextCommand;
import net.grandtheftmc.core.tutorials.TutorialCommand;
import net.grandtheftmc.core.tutorials.TutorialManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.voting.VoteCommand;
import net.grandtheftmc.core.voting.VoteManager;
import net.grandtheftmc.core.voting.crates.CrateManager;
import net.grandtheftmc.core.voting.crates.listeners.CrateNearbyListener;
import net.grandtheftmc.core.voting.crates.listeners.CrateOpenListener;
import net.grandtheftmc.core.whitelist.WhitelistManager;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisManager;
import net.grandtheftmc.jedis.message.ServerQueueNotifyMessage;
import net.grandtheftmc.jedis.message.UserStateTransactionCheck;
import net.grandtheftmc.jedis.message.VoteNotificationMessage;

public class Core extends JavaPlugin {
	
	// test commit

    public static final String GTM_VERSION = "2.4";
    public static final String VICE_VERSION = "2";
    public static final String CREATIVE_VERSION = "1.0";

//    public static MySQL sql;
    private static Core instance;
    private WorldEditPlugin worldEdit;
    private Main uc;
    private ProtocolManager manager;

    private Settings settings;

    private ServerManager serverManager;
    private UserManager userManager;
    private PermsManager permsManager;
    private NPCManager npcManager;
    private WhitelistManager whitelistManager;
    private VoteManager voteManager;
    private Announcer announcer;
    private TutorialManager tutorialManager;
    private LeaderBoardManager leaderBoardManager;
    private NametagManager nametagManager;
    private CrateManager crateManager;
    private AntiAFK antiAFK;
    private WorldManager worldManager;
//    private MySQLAsyncQueue mySQLAsyncQueue;
    private BukkitTask mysqlAsyncTask;
    private MenuManager menuManager;
    private PlaywireManager playwireManager;
    private CoinManager coinManager;
    private static BuycraftPlugin bp;
    private ChatManager chatManager;
    public static ResourcePackManager resourcePackManager;
    private TradeManager tradeManager;
    private static JedisManager jedisManager;
    private SentryClient sentryClient;
    private AlertManager alertManager;
    private CoreMenuHandler coreMenuHandler;
    /** The boss bar task handler */
    private BossBarTask bossBarTask;
    private static boolean enabled;

    private boolean restarting = false;

    private static final OkHttpClient okHttpClient = new OkHttpClient();

    public static Core getInstance() {
        return instance;
    }

    public void setRestarting(boolean restarting) {
        this.restarting = restarting;
    }

    public boolean isRestarting(){
        return this.restarting;
    }

    //    public static MySQL getSQL() {
//        return sql;
//    }

    public static CoinManager getCoinManager() {
        return Core.getInstance().coinManager;
    }

    public static WorldEditPlugin getWorldEdit() {
        return Core.getInstance().worldEdit;
    }

    public static Main getUltimateCosmetics() {
        return Core.getInstance().uc;
    }

    public static ServerManager getServerManager() {
        return Core.getInstance().serverManager;
    }

    /**
     * @deprecated Please just reference the singleton, {@link UserManager#getInstance()}.
     */
    @Deprecated
	public static UserManager getUserManager() {
        return Core.getInstance().userManager;
    }

    public static PermsManager getPermsManager() {
        return Core.getInstance().permsManager;
    }

    public static WhitelistManager getWhitelistManager() {
        return Core.getInstance().whitelistManager;
    }

    public static VoteManager getVoteManager() {
        return Core.getInstance().voteManager;
    }

    public static Announcer getAnnouncer() {
        return Core.getInstance().announcer;
    }

    public static Settings getSettings() {
        return Core.getInstance().settings;
    }

    public static OkHttpClient getOkHttpClient() {
        return Core.getInstance().okHttpClient;
    }

    public static TutorialManager getTutorialManager() {
        return Core.getInstance().tutorialManager;
    }

    public static NametagManager getNametagManager() {
        return Core.getInstance().nametagManager;
    }

    public static CrateManager getCrateManager() {
        return Core.getInstance().crateManager;
    }

    public static ProtocolManager getProtocolLib() {
        return Core.getInstance().manager;
    }

    public static AntiAFK getAntiAFK() {
        return Core.getInstance().antiAFK;
    }

    public static WorldManager getWorldManager() {
        return Core.getInstance().worldManager;
    }

    public static NPCManager getNPCManager() {
        return Core.getInstance().npcManager;
    }

    public static String name() {
        return Core.getInstance().settings.name();
    }

//    public static MySQLAsyncQueue getMySQLAsyncQueue() {
//        return Core.getInstance().mySQLAsyncQueue;
//    }

    public static JedisManager getJedisManager() {
        return jedisManager;
    }

    public static void setJedisManager(JedisManager jmanager) {
        jedisManager = jmanager;
    }

    public SentryClient getSentryClient() {
        return sentryClient;
    }

    public static TradeManager getTradeManager() {
        return Core.getInstance().tradeManager;
    }

    public AlertManager getAlertManager() {
        return alertManager;
    }


    public static void log(String s) {
        Core.getInstance().getLogger().log(Level.INFO, s);
    }

    public static void error(String s) {
        Core.getInstance().getLogger().log(Level.SEVERE, s);
    }

    public static boolean isCoreEnabled() {
        return enabled;
    }

    @Override
    public void onEnable() {
    	
        Bukkit.setWhitelist(false);
        instance = this;
        PluginManager plm = Bukkit.getPluginManager();

        this.settings = new Settings();

        this.manager = ProtocolLibrary.getProtocolManager();
        this.worldEdit = plm.getPlugin("WorldEdit") == null ? null : (WorldEditPlugin) plm.getPlugin("WorldEdit");
        this.uc = plm.getPlugin("UltimateCosmetics") == null ? null : (Main) plm.getPlugin("UltimateCosmetics");
        if (this.uc == null) {
            Core.error("UltimateCosmetics not found. Disabling Cosmetics");
            this.settings.setLoadCosmetics(false);
        }
        this.load();
        bp = (BuycraftPlugin) Bukkit.getPluginManager().getPlugin("BuycraftX");
        this.worldManager = new WorldManager();
        this.serverManager = new ServerManager();
        this.userManager = UserManager.getInstance();
        this.permsManager = new PermsManager();
        this.whitelistManager = new WhitelistManager();
        this.voteManager = new VoteManager(this);
        this.announcer = new Announcer();
        //this.playwireManager = new PlaywireManager().onEnable(this);
        this.tutorialManager = new TutorialManager();
        this.leaderBoardManager = new LeaderBoardManager();
        this.nametagManager = new NametagManager();
        this.tradeManager = new TradeManager().onEnable(this);
        this.crateManager = new CrateManager().onEnable(this);
        this.coreMenuHandler = new CoreMenuHandler(this);
        this.npcManager = new NPCManager().onEnable(this);
        this.alertManager = new AlertManager().onEnable(this);
        this.coinManager = new CoinManager().onEnable(this);
        this.registerCommands();
        this.registerListeners();
        getServer().getMessenger().registerOutgoingPluginChannel(Core.getInstance(), "BungeeCord");

//        EnjinCore.init();

        ServerUtil.runTaskAsync(() -> {
            String server = this.settings.getRedisConfig().getString("server");
            String password = this.settings.getRedisConfig().getString("password");
            int port = this.settings.getRedisConfig().getInt("port");

            jedisManager = new JedisManager();
            
            // init the module for the SERVER_QUEUE channel
            jedisManager.initModule(new ServerTypeId(net.grandtheftmc.ServerType.valueOf(this.settings.getType().name().toUpperCase()), this.settings.getNumber()), JedisChannel.SERVER_QUEUE, server, port, password);
            // init the module for the GLOBAL channel
            jedisManager.initModule(new ServerTypeId(net.grandtheftmc.ServerType.valueOf(this.settings.getType().name().toUpperCase()), this.settings.getNumber()), JedisChannel.GLOBAL, server, port, password);

            // register listeners for redis payloads
            Core.getJedisManager().getModule(JedisChannel.SERVER_QUEUE).registerListener(ServerQueueNotifyMessage.class, new QueueListener());
            Core.getJedisManager().getModule(JedisChannel.GLOBAL).registerListener(UserStateTransactionCheck.class, new UserStateTransactionCheckListener());
            Core.getJedisManager().getModule(JedisChannel.GLOBAL).registerListener(VoteNotificationMessage.class, new VoteNotificationListener());
        });

        // register stat factory
        StatFactory.init(this);
        // TODO should probably only register this on hubs
        StatFactory.getInstance().registerClientConnectionStat(getProtocolLib());

//        this.sentryClient = Sentry.init("https://b92c7a1d7aa543d281784f682acd1679:297e39e8e563441498073d28479297be@sentry.io/210918");
//        this.sentryClient.setServerName(this.settings.getType().name() + "-" + this.settings.getNumber());
//        this.sentryClient
//        try {
//            Method setupUncaughtExc = this.sentryClient.getClass().getDeclaredMethod("setupUncaughtExceptionHandler");
//            setupUncaughtExc.setAccessible(true);
//            setupUncaughtExc.invoke(this.sentryClient);
//        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        System.out.println(this.sentryClient.toString());
//
//        Sentry.capture("This is the first capture test");

        //Removed temp
//        if(name().equalsIgnoreCase("gtm1")) {
//            ScheduledExecutorService daily = Executors.newScheduledThreadPool(1);
//            Long midnight = LocalDateTime.now().until(LocalDate.now().plusDays(1).atStartOfDay(), ChronoUnit.MINUTES);
//            daily.scheduleAtFixedRate(new SaveDailyStatsRunnable(), midnight, TimeUnit.DAYS.toMinutes(1), TimeUnit.MINUTES);
//        }

        new Anticheat(this);
        BossBarTask bossBarTask = new BossBarTask();
        
        // setup currencies
        for (Currency currency : Currency.values()){
        	
        	if (settings.isMainNetwork()){
        		
        		// main network has some global currencies
        		switch(currency){
            		case TOKEN:
    				case CROWBAR:
    				case COUPON_CREDIT:
    				case VOTE_TOKEN:
    					log("Setting " + currency.getId() + " as a GLOBAL currency.");
    					currency.setServerKey("GLOBAL");
    					break;
    				case MONEY:
    				case PERMIT:
    					log("Setting " + currency.getId() + " as a SERVER currency.");
    					currency.setServerKey(name().toUpperCase());
    					break;
                	}
        	}
        	else{
            	
            	// sister network all currencies are server only
            	switch(currency){
            		case TOKEN:
    				case CROWBAR:
    				case MONEY:
    				case PERMIT:
    					log("Setting " + currency.getId() + " as a SERVER currency.");
    					currency.setServerKey(name().toUpperCase());
    					break;
            	}
        	}
        }
        
        // setup ranks
        for (UserRank ur : UserRank.values()){
        	
        	if (settings.isMainNetwork()){
        		switch(ur){
        			default:
        				log("Setting " + ur.getName() + " as a GLOBAL rank.");
        				ur.setServerKey("GLOBAL");
        				break;
        		}
        	}
        	else{
        		switch(ur){
        			case ADMIN:
        			case BUILDER:
        			case DEV:
        			case HELPOP:
        			case MANAGER:
        			case MOD:
        			case OWNER:
        			case SRMOD:
        			case YOUTUBER:
                    case BUILDTEAM:
        				log("Setting " + ur.getName() + " as a GLOBAL rank.");
        				ur.setServerKey("GLOBAL");
        				break;
        			default:
        				log("Setting " + ur.getName() + " as a SERVER rank.");
        				ur.setServerKey(Core.name().toUpperCase());
        				break;
        		}
        	}
        }
        
        // remove all expired trial ranks
        try (Connection conn = BaseDatabase.getInstance().getConnection()){
        	UserDAO.removeAllExpiredTrialRanks(conn);
        }
        catch(Exception e){
        	e.printStackTrace();
        }

        //Enable core.
        enabled = true;
    }

    @Override
    public void onDisable() {
        //Disable core.
        enabled = false;

        Bukkit.setWhitelist(true);
        Bukkit.getScheduler().cancelTasks(this);
        this.save(true);

        for (JedisChannel channel : jedisManager.getJedisModules().keySet()) {
            jedisManager.getModule(channel).disconnect();
        }

        //Components
        if (this.announcer != null) this.announcer.onDisable(this);
        if (this.worldManager != null) this.worldManager.onDisable(this);
        if (this.chatManager != null) this.chatManager.onDisable(this);
        if (this.leaderBoardManager != null) this.leaderBoardManager.onDisable(this);
        if (this.menuManager != null) this.menuManager.onDisable(this);
        if (this.nametagManager != null) this.nametagManager.onDisable(this);
        if (this.permsManager != null) this.permsManager.onDisable(this);
        if (resourcePackManager != null) resourcePackManager.onDisable(this);
        if (this.antiAFK != null) this.antiAFK.onDisable(this);
        if (this.tutorialManager != null) this.tutorialManager.onDisable(this);
        if (this.crateManager != null) this.crateManager.onDisable(this);
        if (this.voteManager != null) this.voteManager.onDisable(this);
        if (this.whitelistManager != null) this.whitelistManager.onDisable(this);
        if (this.alertManager != null) this.alertManager.onDisable(this);
        if (this.npcManager !=null) this.npcManager.onDisable(this);

        for(Player player : Bukkit.getOnlinePlayers()) {
        	
        	// grab the user
            User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
            if (user != null && user.isLocked()){
            	try (Connection conn = BaseDatabase.getInstance().getConnection()){
            		
            		// save the user
            		user.onSave(conn);
            		
            		// update stats
                	ServerStatsDAO.updatePlaytimeAndFirstlogin(conn, player, user);
                	
                	// set mutex to false
                	MutexDAO.setUserMutex(conn, user.getUUID(), false);
                }
                catch(Exception e){
                	e.printStackTrace();
                }
            }
        }
        shutdownRedis();
        
        // TODO test remove
        System.out.println("[Core] Shutting down MySQL DB pool...");
        
        // shutdown mysql connection pool
        BaseDatabase.getInstance().close();
//        Sentry.close();
//        this.sentryClient.closeConnection();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Join(), this);
        pm.registerEvents(new Leave(), this);
        pm.registerEvents(new Login(this), this);
        pm.registerEvents(new Move(), this);
        pm.registerEvents(new PetListener(), this);
        pm.registerEvents(new SignChange(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new HungerChange(), this);
        pm.registerEvents(new WeatherChange(), this);
        if (!Core.getWorldManager().getEditModeWorlds().isEmpty()) {
            pm.registerEvents(new BlockChange(), this);
            pm.registerEvents(new BreakBlock(), this);
            pm.registerEvents(new PlaceBlock(), this);
            pm.registerEvents(new Liquid(), this);
        }
        if (!Core.getSettings().canCraft()) {
            pm.registerEvents(new Craft(), this);
        }
        if (!Core.getWorldManager().getEditModeWorlds().isEmpty() || !Core.getSettings().canInteractInventory()) {
            pm.registerEvents(new InventoryClick(), this);
        }
        pm.registerEvents(new ChunkLoad(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new Teleport(), this);
        pm.registerEvents(new CommandListener(), this);
        pm.registerEvents(new HangingBreak(), this);
        pm.registerEvents(new net.grandtheftmc.core.editmode.Damage(), this);
        pm.registerEvents(new net.grandtheftmc.core.editmode.Interact(), this);
        pm.registerEvents(new net.grandtheftmc.core.editmode.InventoryClick(), this);
        pm.registerEvents(new ServerPingListener(), this);
        pm.registerEvents(new SwapHandItems(), this);
        pm.registerEvents(new Save(), this);
        pm.registerEvents(new CrateOpenListener(), this);

        if (pm.getPlugin("NuVotifier") != null || pm.getPlugin("Votifier") != null) {
            if (!settings.isSister()) pm.registerEvents(this.voteManager, this);
        }

        pm.registerEvents(menuManager = new MenuManager(), this);
        pm.registerEvents(new Damage(), this);
        pm.registerEvents(new CrateNearbyListener(), this);
        pm.registerEvents(new HopperComponent(), this);
        pm.registerEvents(new PlaywireRecieve(), this);
        pm.registerEvents(new UserStateTransactionListener(this), this);
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerCommands() {
        this.getCommand("config").setExecutor(new ConfigCommand());
        new EditModeCommand();
        this.getCommand("rank").setExecutor(new RankCommand());
        this.getCommand("bucks").setExecutor(new BucksCommand());
        this.getCommand("tokens").setExecutor(new TokensCommand());
        this.getCommand("whitelist").setExecutor(new WhitelistCommand());
        this.getCommand("announcer").setExecutor(new AnnouncerCommand());
        this.getCommand("message").setExecutor(new MessageCommand());
        this.getCommand("reply").setExecutor(new ReplyCommand());

        if (!Core.getSettings().isSister()) {
            this.getCommand("vote").setExecutor(new VoteCommand());
            this.getCommand("votestreak").setExecutor(new VotestreakCommand());
        }

        this.getCommand("tutorial").setExecutor(new TutorialCommand());
        this.getCommand("next").setExecutor(new NextCommand());
        this.getCommand("socialspy").setExecutor(new SocialSpyCommand());
//        this.getCommand("cosmetic").setExecutor(new CosmeticCommand());
        this.getCommand("nametag").setExecutor(new NametagCommand());
//        this.getCommand("petdata").setExecutor(new PetDataCommand());
        this.getCommand("prefs").setExecutor(new PrefsCommand());
        this.getCommand("rewards").setExecutor(new RewardsCommand());
        this.getCommand("ignore").setExecutor(new IgnoreCommand());
        this.getCommand("list").setExecutor(new ListCommand());
        this.getCommand("clearchat").setExecutor(new ClearChatCommand());
        this.getCommand("globalmute").setExecutor(new GlobalMuteCommand());
        this.getCommand("rules").setExecutor(new RulesCommand());
        this.getCommand("save").setExecutor(new SaveCommand());
        this.getCommand("playtime").setExecutor(new PlaytimeCommand());
        this.getCommand("info").setExecutor(new InfoCommand());
        this.getCommand("achievement").setExecutor(new AchievementCommand());
        this.getCommand("crowbar").setExecutor(new CrowbarCommand());
        this.getCommand("crate").setExecutor(new CrateCommand());
        this.getCommand("maxplayers").setExecutor(new MaxPlayersCommand());
        this.getCommand("chatfilter").setExecutor(new ChatFilterCommand());
        this.getCommand("forumrank").setExecutor(new ForumRankCommand());
        this.getCommand("store").setExecutor(new StoreCommand());
        this.getCommand("event").setExecutor(new EventCommand(this));
        new DiscordCommand();
        new FacebookCommand();
        new TwitterCommand();
        new SpankCommand();
        new ServerCommand();
        new CooldownCommand();
        new CouponCreditsCommand();
        new BungeeCommand();
        new TradeCommand();
        new OpenMenuCommand();
        new TrialCommand();
        new EventTagCommand();
    }

    private void load() {
        this.getServer().getScheduler().scheduleSyncRepeatingTask(this, () -> {
            Bukkit.getPluginManager().callEvent(new ServerSaveEvent());
            this.getLogger().info("ServerSaveEvent check");
        }, 18000L, 18000L);
        this.settings.setCoreConfig(Utils.loadConfig("core"));
        this.settings.setMySQLConfig(Utils.loadConfigFromMaster("mysql"));
        this.settings.setServersConfig(Utils.loadConfig("servers"));
        this.settings.setJoinSignsConfig(Utils.loadConfig("joinSigns"));
        this.settings.setPermsConfig(Utils.loadConfig("perms"));
        this.settings.setWhitelistConfig(Utils.loadConfig("whitelist"));
        this.settings.setAnnouncerConfig(Utils.loadConfig("announcer"));
        this.settings.setVotingConfig(Utils.loadConfig("voting"));
        this.settings.setTokenShopConfig(Utils.loadConfig("tokenshop"));
        this.settings.setTutorialsConfig(Utils.loadConfig("tutorials"));
        this.settings.setLeaderBoardsConfig(Utils.loadConfig("leaderBoards"));
        this.settings.setSocialSpyConfig(Utils.loadConfig("socialSpy"));
        this.settings.setWorldsConfig(Utils.loadConfig("worlds"));
        this.settings.setRewardsConfig(Utils.loadConfig("rewards"));
        this.settings.setRulesConfig(Utils.loadConfig("rules"));
        this.settings.setNametagsConfig(Utils.loadConfigFromMaster("nametags"));
        this.settings.setHelpConfig(Utils.loadConfig("help"));
        this.settings.setCratesConfig(Utils.loadConfig("crates"));
        this.settings.setCrateRewardsConfig(Utils.loadConfig("craterewards"));
        this.settings.setRedisConfig(Utils.loadConfig("redis"));
        Utils.setMaxPlayers(this.settings.getCoreConfig().getInt("maxplayers"));
        YamlConfiguration c = this.settings.getCoreConfig();

        // New entries to support sister network.
        this.settings.setSister(c.getBoolean("sister"));
        this.settings.setNetworkName(c.getString("networkName"));
        this.settings.setNetworkShortName(c.getString("networkShortName"));
        this.settings.setNetworkIP(c.getString("networkIP"));
        this.settings.setWebsiteLink(c.getString("websiteLink"));
        this.settings.setStoreLink(c.getString("websiteStoreLink"));
        this.settings.setServer_GTM_name(c.getString("server_GTA_name"));
        this.settings.setServer_GTM_shortName(c.getString("server_GTA_shortName"));

        this.settings.setType(ServerType.getType(c.getString("serverType")));
        if (c.get("serverNumber") == null) this.settings.setNumber(0);
        else this.settings.setNumber(c.getInt("serverNumber"));
        if (c.getString("rankToJoin") != null)
            this.settings.setRankToJoin(UserRank.getUserRankOrNull(c.getString("rankToJoin")));

        this.loadMySQL();
        this.loadMenus();
        Help.loadHelpData();
        antiAFK = new AntiAFK();
        this.chatManager = new ChatManager(Utils.loadConfig("chatsettings"));

//        mySQLAsyncQueue = new MySQLAsyncQueue();
//        mysqlAsyncTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, mySQLAsyncQueue, 0L, 20L);

        loadRedis();
    }

    public void reload() {

//        mySQLAsyncQueue.run();
//        mysqlAsyncTask.cancel();

        // TODO remove legacy code
        BaseDatabase.getInstance().close();
        //sql.closeConnection();

        shutdownRedis();

        this.load();
        this.serverManager.loadJoinSigns();
        this.permsManager.loadPerms();
        this.whitelistManager.load();
        this.announcer.loadAnnouncements();
        this.announcer.startSchedule();

        if (!settings.isSister()) {
            this.voteManager.startSchedule();
            this.voteManager.loadLinksAndRewards();
            this.voteManager.loadTokenShop();
        }

        this.tutorialManager.load();
        this.leaderBoardManager.loadLeaderBoards();
        this.nametagManager.loadNametags();
        this.crateManager.load();
        this.crateManager.loadRewards();
    }

    public void save(boolean shutdown) {
        this.serverManager.saveJoinSigns(shutdown);
        this.permsManager.savePerms(shutdown);
        this.whitelistManager.save(shutdown);
        this.announcer.saveAnnouncements(shutdown);
        this.tutorialManager.save(shutdown);
        this.leaderBoardManager.saveLeaderBoards(shutdown);
        this.crateManager.save(shutdown);

        if (!settings.isSister()) {
            this.voteManager.save(shutdown);
        }
    }


    private void loadRedis() {
        String server = this.settings.getRedisConfig().getString("server");
        String password = this.settings.getRedisConfig().getString("password");
        int port = this.settings.getRedisConfig().getInt("port");
        Core.log("Attempting to connect to redis server (" + server + ":" + port + ")(pass=" + password + ")...");

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {

//            Core.log("Initialising Redis connection pool...");
//            new RedisFactory(server, password, port, () -> {
//
//                Core.log("Initialising Redis listener...");
//                new RedisListener();
//            });


        });

        /* //THIS WORKS

        try {
            jedis = new Jedis(server, port);
            jedis.auth(password);
            jedis.ping();
            getLogger().info("Connected to Redis server successfully.");

            redisManager = new RedisManager();

            Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
                new RedisListener();
            });

            getLogger().info("Redis listener registered.");
        } catch (Exception e) {
            getLogger().warning("Failed to connect to redis! Redis support is disabled.");
        }*/



        /*

FutureTask<JedisPool> task = new FutureTask(new Callable() {
            public JedisPool call()
                    throws Exception {
                //JedisPoolConfig config = new JedisPoolConfig();
                //config.setMaxTotal(8);
                return new JedisPool(server, port);
                //return new JedisPool(config, server, port, 0, password);
            }
        });

        Bukkit.getScheduler().runTaskAsynchronously(this, task);

        try {
            Core.log("Creating Jedis pool...");
            ClassLoader prev = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(Core.class.getClassLoader());
            this.pool = task.get();
            Thread.currentThread().setContextClassLoader(prev);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Unable to create Redis pool", e);
        }


        try (Jedis rsc = pool.getResource()) {
            Core.log("Pinging Redis...");
            rsc.auth(password);
            rsc.ping();
            // If that worked
            getLogger().log(Level.INFO, "Successfully connected to Redis.");

            redisManager = new RedisManager();
            new RedisListener();
        } catch (JedisConnectionException e) {
            pool.destroy();
            pool = null;
            athrow e;
        }*/
    }

    private void shutdownRedis() {
        if (RedisManager.redisEnabled()) {
            RedisFactory.getPool().close();
        }
    }

    private void loadMySQL() {
    	
    	// initialize sql with settings from config.yml (in mysql) column
        YamlConfiguration c = this.settings.getMySQLConfig();
 
    	// TODO remove, as it's the old way of starting the sql settings
        this.settings.setHost(c.getString("mysql.host"));
        this.settings.setPort(c.getString("mysql.port"));
        this.settings.setDatabase(c.getString("mysql.database"));
        this.settings.setUser(c.getString("mysql.user"));
        this.settings.setPassword(c.getString("mysql.password"));

//        sql = new MySQL(this.settings.getHost(), this.settings.getPort(), this.settings.getDatabase(), this.settings.getUser(),
//                this.settings.getPassword());
//        sql.openConnection();
        
        // two connection system for compatibility purposes
        BaseDatabase.getInstance().init(c, "mysql");
        // TEST
        try (Connection conn = BaseDatabase.getInstance().getConnection()){
        	System.out.println("[Core] BaseDatabase grabbing connection...");
        	try (ResultSet result = conn.prepareStatement("SELECT 1").executeQuery()){
        		// empty on purpose
        		System.out.println("[Core] BaseDatabase works!");
        	}
        }
        catch(Exception e){
        	e.printStackTrace();
        }

        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement("ALTER TABLE users " +
                    "ADD COLUMN `country` VARCHAR(32) NOT NULL DEFAULT 'NONE'," +
                    "ADD COLUMN `language` VARCHAR(8) NOT NULL DEFAULT 'NONE';")) {

                statement.execute();
            }
        } catch (SQLException e) {
//            e.printStackTrace(); Ignore
        }

        new BukkitRunnable() {
            @Override
            public void run() {
//                if (!Core.this.settings.loadCosmetics()) return;
//                try (ResultSet rs = Core.sql.query("select * from cosmetics LIMIT 1;")) {
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    List<String> columns = new ArrayList<>();
//                    for (int i = 3; i <= metaData.getColumnCount(); i++)
//                        columns.add(metaData.getColumnName(i).toLowerCase());
//                    for (CosmeticType type : CosmeticType.values()) {
//                        if (!columns.contains(type.toString().toLowerCase()))
//                            Core.sql.update("alter table cosmetics add column `" + type.toString().toLowerCase() + "` BOOLEAN not null default 0;");
//                        if (!columns.contains("last:" + type.toString().toLowerCase()))
//                            Core.sql.update("alter table cosmetics add column `last:" + type.toString().toLowerCase() + "` VARCHAR(255);");
//                    }
//                    for (Cosmetic c : Cosmetic.values()) {
//                        if (!columns.contains(c.getDBName())) {
//                            Core.sql.update("alter table cosmetics add column `" + c.getDBName() + "` BOOLEAN not null default 0;");
//                        }
//                    }
//                    rs.close();
//                } catch (SQLException e) {
//                    Core.error("Error while altering Cosmetics table: ");
//                    e.printStackTrace();
//                }
//                try (ResultSet rs = Core.sql.query("select * from nametags LIMIT 1;")) {
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    List<String> columns = new ArrayList<>();
//                    for (int i = 3; i <= metaData.getColumnCount(); i++)
//                        columns.add(metaData.getColumnName(i).toLowerCase());
//                    Core.getNametagManager().getNametags().stream().filter(tag -> !columns.contains(tag.getName().toLowerCase())).forEach(tag -> Core.sql.update("alter table nametags add column " + tag.getName() + " BOOLEAN not null default 0;"));
//                    rs.close();
//                } catch (SQLException e) {
//                    Core.error("Error while altering Nametags table:");
//                    e.printStackTrace();
//                }
            }
        }.runTaskAsynchronously(this);
    }

    private void loadMenus() {
        if (!Core.getSettings().isSister()) {
            MenuManager.addMenu("vote", 54, "&e&lVoting Menu");
        }
        MenuManager.addMenu("tokenshop", 54, "&e&lToken Shop");
        MenuManager.addMenu("buyshopitem", 54, "&e&lBuy Token Shop Item");
        MenuManager.addMenu("serverwarper", 54, "&e&lServer Warper");
        MenuManager.addMenu("gtmservers", 54, "&e&l" + Core.getSettings().getServer_GTM_shortName() + " Server Warper");
        MenuManager.addMenu("cosmetics", 54, "&6&lCosmetics");
        MenuManager.addMenu("buycosmetic", 54, "&6&lBuy Cosmetic");
        MenuManager.addMenu("nametags", 54, "&a&lNametags");
        MenuManager.addMenu("applynametag", 54, "&a&lApply Nametag");
        MenuManager.addMenu("buynametag", 54, "&a&lBuy Nametag");
        MenuManager.addMenu("chooseeventtag", 54, "&6&lChoose Event Tag");
        MenuManager.addMenu("prefs", 54, "&5&lPreferences");
        MenuManager.addMenu("rewards", 54, "&a&lRewards");
        MenuManager.addMenu("confirmcratereward", 54, "&e&lConfirm Accepting Reward");
        MenuManager.addMenu("confirmexpensivecrate", 54, "&e&lConfirm Opening Crate");
        MenuManager.addMenu("hubservers", 54, "&e&lHub Server Warper");
        MenuManager.addMenu("topvoters", 54, "&a&lTop Voters");
        MenuManager.addMenu("freecoupons", 54, "&a&lStore Coupons");

        if (!Core.getSettings().isSister()) {
            MenuManager.addMenu("topvoters", 54, "&a&lTop Voters");
        }

//        ServerType st = this.settings.getType();
//        for (CosmeticType type : CosmeticType.values())
//            if (type.isEnabled(st))
//                MenuManager.addMenu(type.toString().toLowerCase(), 54, type.getColoredDisplayName());
//        if (CosmeticType.BANNER.isEnabled(st))
//            MenuManager.addMenu("bannervariant", 54, CosmeticType.BANNER.getColoredDisplayName() + " Hat or Cape");
//        if (CosmeticType.BLOCK.isEnabled(st))
//            MenuManager.addMenu("blockvariant", 54, "&2&lBalloon or Block Pet");
//        if (CosmeticType.PARTICLE.isEnabled(st))
//            MenuManager.addMenu("particleshape", 54, CosmeticType.PARTICLE.getColoredDisplayName() + " Shape");
//        if (CosmeticType.PET.isEnabled(st)) {
//            MenuManager.addMenu("petdata", 54, CosmeticType.PET.getColoredDisplayName() + " Data");
//        }
    }

    public static BuycraftPlugin getBuycraftX() {
        return bp;
    }
    public String getBuycraftSecret() {
        return "74d7d741ff781080376cee2bd09635098a7b966e";
    }

    private static LinkedList<String> getPastDataFromExcel(String fileName) throws IOException{
        FileInputStream excelFile = null;
        try {
            excelFile = new FileInputStream(new File(fileName));
        }catch (FileNotFoundException e) {
            XSSFWorkbook workbook = new XSSFWorkbook();
            File f = new File(fileName);
            f.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(f);
            workbook.write(outputStream);
            workbook.close();
            return getPastDataFromExcel(fileName);
        }

        XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
        XSSFSheet sheet = workbook.getSheet("Stats")==null ? workbook.createSheet("Stats") : workbook.getSheet("Stats");
        Iterator<Row> iterator = sheet.iterator();
        LinkedList<String> pastData = new LinkedList<String>();

        while (iterator.hasNext()) {
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            StringBuilder sb = new StringBuilder();
            while (cellIterator.hasNext()) {
                Cell currentCell = cellIterator.next();
                if (currentCell.getCellTypeEnum() == CellType.STRING) {
                    sb.append(currentCell.getStringCellValue() + "-");
                } else if (currentCell.getCellTypeEnum() == CellType.NUMERIC) {
                    sb.append(currentCell.getNumericCellValue() + "-");
                }
            }
            sb.deleteCharAt(sb.length()-1);
            pastData.add(sb.toString());
        }
        return pastData;
    }

    public static void saveDataToExcel(String fileName, LinkedList<String> pastData) throws IOException{
        int rowNum = 0;
        FileInputStream excelFile = new FileInputStream(new File(fileName));
        XSSFWorkbook workbook = new XSSFWorkbook(excelFile);
        XSSFSheet sheet = workbook.getSheet("Stats")==null ? workbook.createSheet("Stats") : workbook.getSheet("Stats");
        for(String entry : pastData) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (String s : entry.split("-")) {
                Cell cell = row.createCell(colNum++);
                cell.setCellValue(s);
            }
        }

        try {
            FileOutputStream outputStream = new FileOutputStream(new File("stats.xlsx"));
            workbook.write(outputStream);
            workbook.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the boss bar task that belongs to this Core plugin.
     * 
     * @return The boss bar task handler.
     */
	public BossBarTask getBossBarTask() {
		return bossBarTask;
	}


//    public static class SaveDailyStatsRunnable implements Runnable {
//
//        @Override
//        public void run(){
//            new BukkitRunnable() {
//                @Override
//                public void run() {
//                    try {
//                        saveDailyStats();
//                        Calendar calendar = Calendar.getInstance();
//                        int day = calendar.get(Calendar.DAY_OF_WEEK);
//                        if(day == Calendar.MONDAY) {
//                            saveWeeklyStats();
//                        }
//                    } catch (IOException | SQLException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }.runTaskAsynchronously(Core.getInstance());
//
//        }
//
//        private void saveWeeklyStats() throws IOException, SQLException{
//
//            LinkedList<String> pastData = getPastDataFromExcel("stats-weekly.xlsx");
//
//            double totalLogins = 0, totalNewPlayers = 0, totalNewPlayersLoginAgain = 0, totalPlayersPlayedBoth = 0, totalPlayersPlayedVice = 0, totalPlayersPlayedGTM = 0;
//            ResultSet set = sql.prepareStatement("SELECT * FROM server_stats").executeQuery();
//            while(set.next()) {
//                totalLogins++;
//                long firstLogin = set.getLong("firstLogin");
//                long weeklyLogin = set.getLong("weeklyLoginTime");
//                if(System.currentTimeMillis() - firstLogin <= 1000*60*60*24*7) {
//                    totalNewPlayers++;
//                    if(weeklyLogin!=0 && weeklyLogin!=firstLogin)
//                        totalNewPlayersLoginAgain++;
//                }
//                String playedServers = set.getString("playedServers")!=null ? set.getString("playedServers").toLowerCase() : "";
//                if(playedServers.contains("vice") && playedServers.contains("gtm")) {
//                    totalPlayersPlayedBoth++;
//                    continue;
//                }
//                if(playedServers.contains("vice")) {
//                    totalPlayersPlayedVice++;
//                }
//                if(playedServers.contains("gtm")) {
//                    totalPlayersPlayedGTM++;
//                }
//
//            }
//            pastData.add(new Date().toGMTString() + "-" + totalNewPlayersLoginAgain + "-" + totalPlayersPlayedBoth + "-" + totalPlayersPlayedVice + "-" + totalPlayersPlayedGTM + "-" + totalNewPlayers + "-" + totalLogins);
//
//            saveDataToExcel("stats-weekly.xlsx", pastData);
//
//            double avgLogins = 0, avgNewPlayers = 0, avgNewPlayersLoginAgain = 0, avgPlayersPlayedBoth = 0, avgPlayersPlayedVice = 0, avgPlayersPlayedGTM = 0;
//            int maxSize = (pastData.size() > 4 ? 4 : pastData.size());
//            Collections.reverse(pastData);
//            for(int i = 0; i<maxSize; i++) {
//                String[] split = pastData.get(i).split("-");
//                avgNewPlayersLoginAgain += Double.valueOf(split[1]);
//                avgPlayersPlayedBoth += Double.valueOf(split[2]);
//                avgPlayersPlayedVice += Double.valueOf(split[3]);
//                avgPlayersPlayedGTM += Double.valueOf(split[4]);
//                avgNewPlayers += Double.valueOf(split[5]);
//                avgLogins += Double.valueOf(split[6]);
//            }
//            avgNewPlayersLoginAgain /= maxSize;
//            avgPlayersPlayedBoth /= maxSize;
//            avgPlayersPlayedGTM /= maxSize;
//            avgPlayersPlayedVice /= maxSize;
//            avgLogins /= maxSize;
//            avgNewPlayers /= maxSize;
//            DecimalFormat df = new DecimalFormat("#.##");
//
//
//            SlackApi api = new SlackApi("https://hooks.slack.com/services/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");
//            SlackMessage msg = new SlackMessage();
//            msg.setChannel("important");
//            SlackAttachment attachment = new SlackAttachment();
//            attachment.setFallback("Generated server stats");
//            attachment.setColor("#36a64f");
//            attachment.setTitle("User Report For Network (Date: " + new Date().toGMTString() + ")");
//            attachment.setTitleLink("http://grandtheftmc.net");
//            List<SlackField> fields = new ArrayList<>();
//
//            SlackField f1 = new SlackField();
//            f1.setTitle("New Players Who Logged In >1 Times (Last 7 days)");
//            f1.setValue(df.format(totalNewPlayersLoginAgain/totalNewPlayers*100) + "% (4 Week Average: " + df.format(avgNewPlayersLoginAgain/avgNewPlayers*100) + "%)");
//            f1.setShorten(false);
//            fields.add(f1);
//
//            SlackField f2 = new SlackField();
//            f2.setTitle("Players Who Logged Into JUST Vice (Last 7 days)");
//            f2.setValue(df.format(totalPlayersPlayedVice/totalLogins*100) + "% (4 Week Average: " + df.format(avgPlayersPlayedVice/avgLogins*100) + "%)");
//            f2.setShorten(false);
//            fields.add(f2);
//
//            SlackField f3 = new SlackField();
//            f3.setTitle("Players Who Logged Into JUST GTM (Last 7 days)");
//            f3.setValue(df.format(totalPlayersPlayedGTM/totalLogins*100) + "% (4 Week Average: " + df.format(avgPlayersPlayedGTM/avgLogins*100) + "%)");;
//            f3.setShorten(false);
//            fields.add(f3);
//
//            SlackField f4 = new SlackField();
//            f4.setTitle("Players Who Logged Into BOTH Vice and GTM (Last 7 days)");
//            f4.setValue(df.format(totalPlayersPlayedBoth/totalLogins*100) + "% (4 Week Average: " + df.format(avgPlayersPlayedBoth/avgLogins*100) + "%)");
//            f4.setShorten(false);
//            fields.add(f4);
//
//            attachment.setFields(fields);
//            msg.setAttachments(Arrays.asList(attachment));
//            msg.setText(" ");
//            api.call(msg);
//
//            sql.prepareStatement("update server_stats set playedServers = NULL;").executeUpdate();
//            sql.prepareStatement("update server_stats set weeklyLoginTime = " + System.currentTimeMillis() + ";").executeUpdate();
//
//        }
//
//        private void saveDailyStats() throws IOException, SQLException{
//
//            for(Player p: Bukkit.getOnlinePlayers()) {
//                User u = Core.getUserManager().getLoadedUser(p.getUniqueId());
//                long playTime = System.currentTimeMillis() - u.getLoginTime() + u.getDailyPlayTime();
//                PreparedStatement stmt = Core.sql.prepareStatement("UPDATE server_stats set dailyPlayTime=" + playTime + ", firstLogin=" + p.getFirstPlayed() + " WHERE uuid='" + p.getUniqueId() + "';");
//                stmt.execute();
//                u.setDailyPlayTime(0);
//                u.setLoginTime(System.currentTimeMillis());
//            }
//
//            LinkedList<String> pastData = getPastDataFromExcel("stats.xlsx");
//
//            double dailyTotalLogins = 0, dailyPlaytime = 0, dailyNewPlayers = 0, dailyPlaytimeRanked = 0, dailyLoginsRanked = 0, dailyLoginsDefault = 0, dailyPlaytimeDefault = 0;
//            ResultSet set = sql.prepareStatement("SELECT * FROM server_stats").executeQuery();
//            while(set.next()) {
//                String uuid = set.getString("uuid");
//                if((System.currentTimeMillis() - set.getLong("firstLogin")<=1000*60*60*24))
//                    dailyNewPlayers++;
//                if(System.currentTimeMillis() - set.getLong("dailyLoginTime")<=1000*60*60*24) {
//                    dailyTotalLogins++;
//                    long playtime = set.getLong("dailyPlayTime");
//                    dailyPlaytime += playtime;
//                    ResultSet rank = sql.prepareStatement("SELECT * FROM users where UUID='" + uuid + "';").executeQuery();
//                    if(rank.next()) {
//                        UserRank userRank = UserRank.getUserRank(rank.getString("userrank"));
//                        if(userRank==UserRank.DEFAULT) {
//                            dailyLoginsDefault++;
//                            dailyPlaytimeDefault += playtime;
//                        }
//                        else {
//                            dailyLoginsRanked++;
//                            dailyPlaytimeRanked += playtime;
//                        }
//                    }
//                }
//            }
//            set = sql.prepareStatement("SELECT * FROM users;").executeQuery();
//
//            double totalPlayers = 0;
//            if(set.last())
//                totalPlayers = set.getRow();
//
//            dailyPlaytime /= dailyTotalLogins;
//            dailyPlaytime /= 1000.0 * 60.0;//minutes
//
//            dailyPlaytimeDefault /= dailyLoginsDefault;
//            dailyPlaytimeDefault /= 1000.0 * 60.0;
//
//            dailyPlaytimeRanked /= dailyLoginsRanked;
//            dailyPlaytimeRanked /= 1000.0 * 60.0;
//
//            pastData.add(new Date().toGMTString() + "-" + dailyTotalLogins + "-" + dailyNewPlayers + "-" + dailyPlaytime + "-" + dailyPlaytimeRanked + "-" + dailyPlaytimeDefault);
//            //pastData.add(new Date().toGMTString() + "-" + cNewPlayers + "-" + cActivePlayers);
//
//            saveDataToExcel("stats.xlsx", pastData);
//
//            double avgNewPlayers = 0, avgActivePlayers = 0, avgPlayTime = 0, avgPlayTimeRanked = 0, avgPlayTimeDefault = 0;
//            int maxSize = (pastData.size() > 7 ? 7 : pastData.size());
//            Collections.reverse(pastData);
//            for(int i = 0; i<maxSize; i++) {
//                String[] split = pastData.get(i).split("-");
//                avgNewPlayers += Double.valueOf(split[2]);
//                avgActivePlayers += Double.valueOf(split[1]);
//                avgPlayTime += Double.valueOf(split[3]);
//                avgPlayTimeRanked += Double.valueOf(split[4]);
//                avgPlayTimeDefault += Double.valueOf(split[5]);
//            }
//            avgNewPlayers /= maxSize;
//            avgActivePlayers /= maxSize;
//
//            avgPlayTime /= maxSize;
//            avgPlayTimeRanked /= maxSize;
//            avgPlayTimeDefault /= maxSize;
//
//            DecimalFormat df = new DecimalFormat("#.##");
//
//
//            SlackApi api = new SlackApi("https://hooks.slack.com/services/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");
//            SlackMessage msg = new SlackMessage();
//            msg.setChannel("important");
//            SlackAttachment attachment = new SlackAttachment();
//            attachment.setFallback("Generated server stats");
//            attachment.setColor("#36a64f");
//            attachment.setTitle("User Report For Network (Date: " + new Date().toGMTString() + ")");
//            attachment.setTitleLink("http://grandtheftmc.net");
//            List<SlackField> fields = new ArrayList<>();
//
//            SlackField f1 = new SlackField();
//            f1.setTitle("Active Players (Last 24 hrs)");
//            f1.setValue(dailyTotalLogins + " (" + df.format((dailyTotalLogins-avgActivePlayers)/avgActivePlayers*100) + "% Change compared to 7 day average)");
//            f1.setShorten(false);
//            fields.add(f1);
//
//            SlackField f2 = new SlackField();
//            f2.setTitle("New Players (Last 24 hrs)");
//            f2.setValue(dailyNewPlayers + " (" + df.format((dailyNewPlayers-avgNewPlayers)/avgNewPlayers*100) + "% Change compared to 7 day average)");
//            f2.setShorten(false);
//            fields.add(f2);
//
//            SlackField f3 = new SlackField();
//            f3.setTitle("Average Playtime (Last 24 hrs)");
//            f3.setValue(df.format(dailyPlaytime) + "min (" + df.format((dailyPlaytime-avgPlayTime)/avgPlayTime*100) + "% Change compared to 7 day average)");
//            f3.setShorten(false);
//            fields.add(f3);
//
//            SlackField f4 = new SlackField();
//            f4.setTitle("Average Playtime for Ranked Users (Last 24 hrs)");
//            f4.setValue(df.format(dailyPlaytimeRanked) + "min (" + df.format((dailyPlaytimeRanked-avgPlayTimeRanked)/avgPlayTimeRanked*100) + "% Change compared to 7 day average)");
//            f4.setShorten(false);
//            fields.add(f4);
//
//            SlackField f5 = new SlackField();
//            f5.setTitle("Average Playtime for Default Users (Last 24 hrs)");
//            f5.setValue(df.format(dailyPlaytimeDefault) + "min (" + df.format((dailyPlaytimeDefault-avgPlayTimeDefault)/avgPlayTimeDefault*100) + "% Change compared to 7 day average)");
//            f5.setShorten(false);
//            fields.add(f5);
//
//
//            SlackField f6 = new SlackField();
//            f6.setTitle("Total Players");
//            f6.setValue(totalPlayers + "");
//            f6.setShorten(false);
//            fields.add(f6);
//
//            attachment.setFields(fields);
//            msg.setAttachments(Arrays.asList(attachment));
//            msg.setText(" ");
//            api.call(msg);
//
//            sql.prepareStatement("update server_stats set dailyPlayTime = 0;").executeUpdate();
//            sql.prepareStatement("update server_stats set dailyLoginTime = " + System.currentTimeMillis() + ";").executeUpdate();
//
//        }
//    }
}