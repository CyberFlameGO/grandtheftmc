package net.grandtheftmc.houses;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.houses.commands.HouseChestCommand;
import net.grandtheftmc.houses.commands.HouseDisableCommand;
import net.grandtheftmc.houses.commands.HouseDoorCommand;
import net.grandtheftmc.houses.commands.HouseSignCommand;
import net.grandtheftmc.houses.commands.HouseTrashcanCommand;
import net.grandtheftmc.houses.commands.HousesCommand;
import net.grandtheftmc.houses.houses.HousesManager;
import net.grandtheftmc.houses.listeners.BreakBlock;
import net.grandtheftmc.houses.listeners.Chat;
import net.grandtheftmc.houses.listeners.Damage;
import net.grandtheftmc.houses.listeners.Death;
import net.grandtheftmc.houses.listeners.Interact;
import net.grandtheftmc.houses.listeners.InventoryClose;
import net.grandtheftmc.houses.listeners.InventoryInteract;
import net.grandtheftmc.houses.listeners.Join;
import net.grandtheftmc.houses.listeners.Leave;
import net.grandtheftmc.houses.listeners.Login;
import net.grandtheftmc.houses.listeners.MenuListener;
import net.grandtheftmc.houses.listeners.PetListener;
import net.grandtheftmc.houses.listeners.Teleport;
import net.grandtheftmc.houses.listeners.WeaponShoot;
import net.grandtheftmc.houses.users.HouseUser;
import net.grandtheftmc.houses.users.HouseUserManager;
import net.grandtheftmc.houses.users.UserHouse;

public class Houses extends JavaPlugin {

    public static boolean ENABLED = true;

    private static Houses instance;

    private static HouseUserManager um;
    private static HousesManager hm;

    private static HousesSettings settings;
    private static WorldEditPlugin worldEditPlugin;

    public static Houses getInstance() {
        return instance;
    }

    public static HouseUserManager getUserManager() {
        return um;
    }

    public static HousesManager getManager() {
        return hm;
    }

    public static HousesManager getHousesManager() {
        return hm;
    }

    public static HousesSettings getSettings() {
        return settings;
    }

    public static void log(String s) {
        Houses.getInstance().getLogger().log(Level.ALL, s);
    }

    public static void error(String s) {
        Houses.getInstance().getLogger().log(Level.SEVERE, s);
    }

    public static Optional<WorldEditPlugin> getWorldEdit() {
        return Optional.of(worldEditPlugin);
    }

    @Override
    public void onEnable() {
        instance = this;
        settings = new HousesSettings();
        this.load();
        this.setupDatabase();
        um = new HouseUserManager();
        hm = new HousesManager();

        this.registerListeners();
        this.registerCommands();
    }

    @Override
    public void onDisable() {
    	
    	System.out.println("[Houses] Disabling houses...");
    	
        HandlerList.unregisterAll(this);

        // note that this is sync because any async calls will not run
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getOpenInventory() == null) return;
                
                // TODO remove later
                System.out.println("[Houses] Player " + player.getName() + " has open inventory: " + player.getOpenInventory().getTitle());
                
                // TODO remove later
                if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null){
                	System.out.println("[Houses] Player " + player.getName() + " has open TOP inventory: " + player.getOpenInventory().getTopInventory().getTitle());
                }

                HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
                if (user != null){
                	System.out.println("[Houses] Player " + player.getName() + " is inside house: " + user.isInsideHouse() + " and lastChest=" + user.getLastChestId());
                }
                
                if (user.isInsideHouse() && user.getLastChestId() != -1) {
                	
                	// TODO remove later
                	System.out.println("[Houses] Player " + player.getName() + " is inside house with last chest ID: " + user.getLastChestId());

                    // If chest is open.
                    if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null) {
                        String name = ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle()).toLowerCase();
                        
                        // TODO remove later
                        System.out.println("[Houses] Player " + player.getName() + " top inventory stripped: " + name);
                        
                        if (!name.contains("chest:")) continue;

                        UserHouse house = user.getUserHouse(user.getInsideHouse());
                        
                        // TODO remove later
                        System.out.println("[Houses] Player " + player.getName() + " is inside house id=" + house.getId() + " with chest id=" + user.getLastChestId());

                        try (PreparedStatement statement = connection.prepareStatement(
                                "UPDATE gtm_house_chest SET content=? WHERE house_id=? AND uuid=UNHEX(?) AND chest_id=?")) {
                            statement.setString(1, GTMUtils.toBase64(player.getOpenInventory().getTopInventory().getContents()));
                            statement.setInt(2, house.getUniqueId());
                            statement.setString(3, player.getUniqueId().toString().replaceAll("-", ""));
                            statement.setInt(4, user.getLastChestId());

                            statement.executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        player.closeInventory();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.save();
    }

    private void load() {
        settings.setHousesConfig(Utils.loadConfig("houses"));
        settings.setPremiumHousesConfig(Utils.loadConfig("premiumHouses"));
        this.setupDatabase();
        this.loadMenus();
        if(Bukkit.getPluginManager().getPlugin("WorldEdit") != null) {
            worldEditPlugin = (WorldEditPlugin)Bukkit.getPluginManager().getPlugin("WorldEdit");
        }
    }

    private void save() {
        hm.save();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new BreakBlock(), this);
        pm.registerEvents(new Damage(), this);
        pm.registerEvents(new Death(), this);
        pm.registerEvents(new Interact(), this);
        pm.registerEvents(new InventoryClose(), this);
        pm.registerEvents(new InventoryInteract(), this);
        pm.registerEvents(new Join(), this);
        pm.registerEvents(new Leave(), this);
        pm.registerEvents(new Login(this), this);
        pm.registerEvents(new Teleport(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new PetListener(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new WeaponShoot(), this);
    }

    private void registerCommands() {
        this.getCommand("houses").setExecutor(new HousesCommand());
        this.getCommand("premiumhouses").setExecutor(new HousesCommand());
        this.getCommand("housechest").setExecutor(new HouseChestCommand());
        this.getCommand("housedoor").setExecutor(new HouseDoorCommand());
        this.getCommand("housesign").setExecutor(new HouseSignCommand());
        this.getCommand("housetrashcan").setExecutor(new HouseTrashcanCommand());
        new HouseDisableCommand();
    }

    public void setupDatabase() {
//        BaseDatabase.runCustomQuery("create table if not exists " + Core.name() + "_houses(uuid varchar(255), name varchar(255), houseId integer)");
//        BaseDatabase.runCustomQuery("create table if not exists " + Core.name() + "_houses_chests(uuid varchar(255), houseId integer, chestId integer, contents blob);");
    }

    private void loadMenus() {
        MenuManager.addMenu("houses", 54, "&3&lMy Houses");
        MenuManager.addMenu("house", 54, "&3&lHouse");
        MenuManager.addMenu("premiumhouse", 54, "&3&lPremium House");
        MenuManager.addMenu("buyhouse", 54, "&3&lBuy House");
        MenuManager.addMenu("buypremiumhouse", 54, "&3&lBuy Premium House");
        MenuManager.addMenu("sellhouse", 54, "&3&lSell House");
        MenuManager.addMenu("sellpremiumhouse", 54, "&3&lSell Premium House");
        MenuManager.addMenu("guests", 54, "&3&lPremium House Guests");
        MenuManager.addMenu("removeguests", 54, "&3&lRemove Guests");
        MenuManager.addMenu("houseshelp", 54, "&3&lHouses Help");
        MenuManager.addMenu("editblocks", 54, "&3&lChange Blocks");
        MenuManager.addMenu("buytrashcan", 54, "&3&lBuy Trashcan");
        MenuManager.addMenu("confirmtrashcanbuy", 54, "&3&lConfirm Purchase");
    }
}
