package net.grandtheftmc.Creative;

import net.grandtheftmc.Creative.commands.CreativeCommand;
import net.grandtheftmc.Creative.commands.CreativeRankCommand;
import net.grandtheftmc.Creative.commands.WorldCommand;
import net.grandtheftmc.Creative.listeners.*;
import net.grandtheftmc.Creative.users.CreativeUserManager;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Settings;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Creative extends JavaPlugin {

    private static Creative instance;
    private static CreativeUserManager um;

    private static Location spawn;

    private static CreativeSettings settings;

    @Override
    public void onEnable() {
        instance = this;
        this.load();
        um = new CreativeUserManager();
        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
        this.save();
        Bukkit.getScheduler().cancelTasks(this);
    }

    public void load() {
        settings = new CreativeSettings();
        YamlConfiguration c = Utils.loadConfig("creative");
        settings.setCreativeConfig(c);
        spawn = Utils.teleportLocationFromString(c.getString("spawn"));
        this.loadSettings();

    }

    private void loadSettings() {
        Settings s = Core.getSettings();
        s.setUseEditMode(false);
        s.setDefaultGameMode(GameMode.CREATIVE);
        s.setServerWarperEnabled(false);
        s.setStatsMenuEnabled(false);
        s.setCanCraft(true);
        s.setCanOpenChests(true);
        s.setCanInteractInventory(true);
        s.setLoadCosmetics(true);
    }

    public void save() {
        YamlConfiguration c = settings.getCreativeConfig();
        c.set("spawn", Utils.teleportLocationToString(spawn));
        Utils.saveConfig(c, "creative");
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new Join(), this);
        pm.registerEvents(new Leave(), this);
        pm.registerEvents(new Login(), this);
        pm.registerEvents(new PotionUse(), this);
        pm.registerEvents(new UpdateListener(), this);
        pm.registerEvents(new SwitchWorld(), this);
    }

    private void registerCommands() {
        this.getCommand("creative").setExecutor(new CreativeCommand());
        this.getCommand("creativerank").setExecutor(new CreativeRankCommand());
        new WorldCommand();
    }

    public static Creative getInstance() {
        return instance;
    }

    public static CreativeUserManager getUserManager() {
        return um;
    }

    public static Location getSpawn() {
        return spawn;
    }

    public static CreativeSettings getSettings() {
        return settings;
    }

}
