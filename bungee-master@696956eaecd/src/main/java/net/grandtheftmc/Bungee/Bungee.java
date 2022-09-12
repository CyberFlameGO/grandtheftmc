package net.grandtheftmc.Bungee;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.imaginarycode.minecraft.redisbungee.RedisBungee;

import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.Bungee.authy.AuthyManager;
import net.grandtheftmc.Bungee.commands.AuthyCommand;
import net.grandtheftmc.Bungee.commands.FindCommand;
import net.grandtheftmc.Bungee.commands.GlobalMessageCommand;
import net.grandtheftmc.Bungee.commands.HelpCommand;
import net.grandtheftmc.Bungee.commands.HubCommand;
import net.grandtheftmc.Bungee.commands.MotdCommand;
import net.grandtheftmc.Bungee.commands.PermsCommand;
import net.grandtheftmc.Bungee.commands.PlaytimeCommand;
import net.grandtheftmc.Bungee.commands.ServerCommand;
import net.grandtheftmc.Bungee.commands.StaffChatCommand;
import net.grandtheftmc.Bungee.database.BaseDatabase;
import net.grandtheftmc.Bungee.help.HelpCore;
import net.grandtheftmc.Bungee.listeners.Chat;
import net.grandtheftmc.Bungee.listeners.Connect;
import net.grandtheftmc.Bungee.listeners.Disconnect;
import net.grandtheftmc.Bungee.listeners.Kick;
import net.grandtheftmc.Bungee.listeners.Login;
import net.grandtheftmc.Bungee.listeners.Ping;
import net.grandtheftmc.Bungee.redisbungee.RedisListener;
import net.grandtheftmc.Bungee.redisbungee.RedisManager;
import net.grandtheftmc.Bungee.tasks.AnnouncerTask;
import net.grandtheftmc.Bungee.tasks.AuthyTask;
import net.grandtheftmc.Bungee.tasks.PlaytimePurgeTask;
import net.grandtheftmc.Bungee.tasks.ServerStatusTask;
import net.grandtheftmc.Bungee.users.UserManager;
import net.grandtheftmc.jedis.JMessageListener;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisManager;
import net.grandtheftmc.jedis.message.ServerJoinRequestMessage;
import net.grandtheftmc.slack.Slack;
import net.grandtheftmc.slack.SlackChannel;
import net.grandtheftmc.slack.SlackField;
import net.grandtheftmc.slack.SlackHook;
import net.grandtheftmc.slack.attachment.SlackAttachment;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.config.Configuration;

public class Bungee extends Plugin implements Listener {
	
	// TODO remove
	// test commit
	
    private static Bungee instance;
    private RedisManager redisManager;
    private AuthyManager authyManager;
    private Settings settings;
    private UserManager um;
    private JedisManager jedisManager;

    public boolean enabled = false;

    public static final boolean GTM = true;

    private HelpCore helpCore;

    private Long startTime;
    private ScheduledTask task69;

    public static Bungee getInstance() {
        return instance;
    }

    public static Settings getSettings() {
        return instance.settings;
    }

    public static UserManager getUserManager() {
        return instance.um;
    }

    public static RedisManager getRedisManager() {
        return instance.redisManager;
    }

    public static AuthyManager getAuthyManager() {
        return instance.authyManager;
    }

    public static void log(String msg) {
        Bungee.getInstance().getLogger().log(Level.INFO, msg);
    }

    public static void error(String msg) {
        Bungee.getInstance().getLogger().log(Level.SEVERE, msg);
    }

    public static void consoleLog(String log) {
    }

    public HelpCore getHelpCore() {
        return helpCore;
    }

    public Long getStartTime() {
        return this.startTime;
    }

    @Override
    public void onEnable() {
        instance = this;

        getProxy().getScheduler().runAsync(this, () -> {
            this.jedisManager = new JedisManager();
            this.jedisManager.initModule(new ServerTypeId(ServerType.PROXY, -1), JedisChannel.SERVER_QUEUE, GTM ? "172.16.0.1" : "databasesql", 5555, GTM ? "gtmredispass" : "redispass");
            this.jedisManager.getModule(JedisChannel.SERVER_QUEUE).registerListener(ServerJoinRequestMessage.class, new QueueListener());
        });

        this.settings = new Settings();
        this.load();
        this.um = new UserManager();
        this.registerCommands();
        this.registerListeners();
        new AnnouncerTask();
        new ServerStatusTask();
        new PlaytimePurgeTask();
        new AuthyTask();
        this.startTime = System.currentTimeMillis();

        enabled = true;
    }

    @Override
    public void onDisable() {
        if(this.task69 != null && this.task69.getTask() != null)
            this.task69.cancel();

        for(JedisChannel channel : this.jedisManager.getJedisModules().keySet()) {
            this.jedisManager.getModule(channel).disconnect();
        }
    }

    public void registerCommands() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        for (String st : this.settings.getServers().keySet())
            pm.registerCommand(this, new ServerCommand(st, this.jedisManager));
//        pm.registerCommand(this, new AltCommand());
        pm.registerCommand(this, new GlobalMessageCommand());
        pm.registerCommand(this, new HelpCommand());
        pm.registerCommand(this, new HubCommand(this.jedisManager, "hub"));
        pm.registerCommand(this, new HubCommand(this.jedisManager, "lobby"));
        pm.registerCommand(this, new HubCommand(this.jedisManager, "gtm"));
        pm.registerCommand(this, new HubCommand(this.jedisManager, "vice"));
        pm.registerCommand(this, new MotdCommand());
        pm.registerCommand(this, new PermsCommand());
//        pm.registerCommand(this, new SeenCommand());
        pm.registerCommand(this, new StaffChatCommand());
        pm.registerCommand(this, new PlaytimeCommand());
        pm.registerCommand(this, new AuthyCommand());
        pm.registerCommand(this, new FindCommand());
    }

    private void registerListeners() {
        PluginManager pm = ProxyServer.getInstance().getPluginManager();
        pm.registerListener(this, new Chat());
        pm.registerListener(this, new Disconnect());
        pm.registerListener(this, new Login());
        pm.registerListener(this, new Connect());
        pm.registerListener(this, new Kick());
        pm.registerListener(this, new Ping());
        pm.registerListener(this, new RedisListener());

        getRedisManager().getRedisAPI().registerPubSubChannels(getRedisManager().getMessageChannel());
    }

    private void load() {
        this.settings.setMySQLConfig(Utils.loadConfigFromMaster("mysql"));
//        this.settings.setBATConfig(BAT.getInstance().getConfiguration());
        this.settings.setPermsConfig(Utils.loadConfig("perms"));
        this.settings.setMotdConfig(Utils.loadConfig("motd"));
        this.settings.setGtmConfig(Utils.loadConfig("gtmconfig"));

        this.settings.setHelpConfig(Utils.loadConfig("help"));
        //Init help core.
        this.helpCore = new HelpCore(this.settings.getHelpConfiguration());

        this.settings.setMotd(this.settings.getMotdConfig().getString("motd"));
        this.loadMySQL();
        if (this.getProxy().getPluginManager().getPlugin("RedisBungee") != null) {
            this.redisManager = new RedisManager(RedisBungee.getApi());
        } else {
            error("RedisBungee not found!");
        }

        this.authyManager = new AuthyManager();
    }

    private void loadMySQL() {
        Configuration c = this.settings.getMySQLConfig();
        this.settings.setHost(c.getString("mysql.host"));
        this.settings.setPort(c.getString("mysql.port"));
        this.settings.setDatabase(c.getString("mysql.database"));
        this.settings.setUser(c.getString("mysql.user"));
        this.settings.setPassword(c.getString("mysql.password"));

        BaseDatabase.getInstance().init(
                this.settings.getHost(),
                Integer.parseInt(this.settings.getPort()),
                this.settings.getDatabase(),
                this.settings.getUser(),
                this.settings.getPassword()
        );
    }

    private void initHubPinger() {
        for (String str : getProxy().getServers().keySet()) {
            Utils.SERVERS.putIfAbsent(getProxy().getServerInfo(str), true);
        }

        task69 = getProxy().getScheduler().schedule(this, () -> {
            for(ServerInfo server : Utils.SERVERS.keySet()) {
                if(!Utils.SERVERS.get(server)) continue;
                getProxy().getScheduler().runAsync(this, () -> server.ping((serverPing, throwable) -> {
                    if(throwable != null) {
                        Utils.SERVERS.put(server, false);

                        String[] serv = getServerType(server.getName());
                        Slack.send(SlackChannel.PRODUCTION_ALERTS, SlackHook.SERVER_HEARTBEAT,
                                new SlackAttachment("Server down! " + server.getName())
                                    .setColor("#e01563")
                                    .setAuthorName("BungeeCord")
                                    .addFields(
                                            new SlackField().setTitle("Server Type").setValue(serv[0]).setShorten(true)
                                    )
                                    .addFields(
                                            new SlackField().setTitle("Identifier").setValue(serv[1]).setShorten(true)
                                    )
                                    .addFields(
                                            new SlackField().setTitle("Triggered By").setValue(throwable.getLocalizedMessage()).setShorten(false)
                                    )
                                    .setFooter("Timestamp")
                        );
                    }
                    else {
                        Utils.SERVERS.put(server, true);
                    }
                }));
            }
        }, 10, 30, TimeUnit.SECONDS);
    }

    public String[] getServerType(String server) {
        String id = "-1";
        ServerType type = ServerType.OPERATOR;
        if(server.toLowerCase().startsWith("hub")) {
            id = server.toLowerCase().split("hub")[1];
            type = ServerType.HUB;
        }

        if(server.toLowerCase().startsWith("gtm")) {
            id = server.toLowerCase().split("gtm")[1];
            type = ServerType.GTM;
        }

        if(server.toLowerCase().startsWith("vice")) {
            id = server.toLowerCase().split("vice")[1];
            type = ServerType.VICE;
        }

        if(server.toLowerCase().startsWith("creative")) {
            id = server.toLowerCase().split("creative")[1];
            type = ServerType.CREATIVE;
        }

        return new String[]{ type == ServerType.OPERATOR ? server.toUpperCase() : type.name(), id };
    }

    public class QueueListener implements JMessageListener<ServerJoinRequestMessage> {

        @Override
        public void onReceive(ServerTypeId serverTypeId, ServerJoinRequestMessage message) {
            if(!enabled) return;

            ProxiedPlayer player = instance.getProxy().getPlayer(message.getUniqueId());
            if(player == null) return;
            ServerInfo info = instance.getProxy().getServerInfo(message.getTargetServer().getServerType().getServerName() + message.getTargetServer().getId());
            if(info != null) {
                info.ping((serverPing, throwable) -> {
                    if(serverPing != null) {
                        player.connect(info);
                        player.sendMessage(Utils.f("&aSending you to server!"));
                    }
                });
            }
            else {
                player.sendMessage(Utils.f("&cThis server cannot be recognised!"));
            }
        }
    }
}
