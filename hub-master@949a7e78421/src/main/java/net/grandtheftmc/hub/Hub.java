package net.grandtheftmc.hub;

import net.grandtheftmc.ServerType;
import net.grandtheftmc.ServerTypeId;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Settings;
import net.grandtheftmc.core.alert.AlertManager;
import net.grandtheftmc.core.servers.menu.GTMTranzitMenu;
import net.grandtheftmc.core.servers.menu.TranzitMenu;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.hub.commands.SpawnCommand;
import net.grandtheftmc.hub.listeners.*;
import net.grandtheftmc.hub.patch.InventoryFillPatch;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.JedisManager;
import net.grandtheftmc.jedis.message.ServerQueueNotifyMessage;
import org.bukkit.*;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Hub extends JavaPlugin {
    private static Hub instance;
    private YamlConfiguration hubConfig;
    private Collection<Location> spawnPoints;
    private Location spawn;
    private AlertsComponent alertsComponent;
    private int progress = 0;

    private TranzitMenu tranzitMenu;

    public static Hub getInstance() {
        return instance;
    }

    public TranzitMenu getTranzitMenu() {
        return tranzitMenu;
    }

    @Override
    public void onEnable() {
        instance = this;

        this.load();
        this.alertsComponent = new AlertsComponent(Core.getInstance().getAlertManager()).onEnable(this);

        GTMTranzitMenu gtmTranzitMenu = new GTMTranzitMenu();
        this.tranzitMenu = new TranzitMenu(gtmTranzitMenu);

        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            tranzitMenu.rotate();
            tranzitMenu.refreshEdge();

            gtmTranzitMenu.rotate();
            gtmTranzitMenu.refreshEdge();

            if (progress == 3) {
                tranzitMenu.refreshButtons();
                gtmTranzitMenu.refreshButtons();
                progress = 0;
                return;
            }

            progress++;
        }, 60, 6);

        new PortalComponent(this).onEnable(this);

        this.registerCommands();
        this.registerListeners();
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);
        this.unload();
        this.alertsComponent.onDisable(this);
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Damage(), this);
        pm.registerEvents(new Death(), this);
        pm.registerEvents(new Drop(), this);
        pm.registerEvents(new Interact(), this);
        pm.registerEvents(new Join(), this);
        pm.registerEvents(new Update(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new Move(), this);
        pm.registerEvents(new PortalEnter(), this);
        pm.registerEvents(new BlockPlace(), this);

        //Protocol Listeners
        new InventoryFillPatch();
    }

    private void registerCommands() {
        this.getCommand("spawn").setExecutor(new SpawnCommand());
    }

    public void load() {
        this.spawnPoints = new ArrayList<>();
        this.hubConfig = Utils.loadConfig("hub");
        YamlConfiguration c = this.hubConfig;
        this.spawn = Utils.teleportLocationFromString(c.getString("spawn"));
        c.getStringList("spawnpoints").forEach(point -> {
            this.spawnPoints.add(HubUtils.deserializeLocation(point));
        });
        this.loadSettings();
    }

    public void unload() {
        List<String> points = new ArrayList<>();
        this.spawnPoints.forEach(point -> points.add(HubUtils.serializeLocation(point)));
        this.hubConfig.set("spawnpoints", points);
        Utils.saveConfig(this.hubConfig, "hub");
    }

    private void loadSettings() {
        Settings settings = Core.getSettings();
        settings.setDefaultGameMode(GameMode.ADVENTURE);
        settings.setUseEditMode(true);
        settings.setJoinLeaveMessagesEnabled(false);
        settings.setLoadCosmetics(true);
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("commandBlockOutput", "false");
            world.setGameRuleValue("doDaylightCycle", "true");
            world.setGameRuleValue("doFireTick", "false");
            world.setGameRuleValue("doMobSpawning", "false");
            world.setGameRuleValue("doTileDrops", "false");
            world.setGameRuleValue("keepInventory", "false");
            world.setGameRuleValue("mobGriefing", "false");
            world.setGameRuleValue("naturalRegeneration", "true");
            world.setPVP(false);
            world.setAutoSave(false);
            world.setDifficulty(Difficulty.NORMAL);
            world.setTime(18000);
            settings.setStopChunkLoad(world.getName());
            settings.setStopHungerChange(world.getName());
            settings.setStopWeatherChange(world.getName());
        }
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public Collection<Location> getSpawnPoints() {
        return this.spawnPoints;
    }
}
