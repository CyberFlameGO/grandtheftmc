package net.grandtheftmc.vice;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.massivecraft.factions.Factions;

import de.slikey.effectlib.EffectManager;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Settings;
import net.grandtheftmc.core.casino.CoreCasino;
import net.grandtheftmc.core.casino.slot.SlotMachine;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.resourcepack.RSPack_1_12;
import net.grandtheftmc.core.resourcepack.ResourcePack;
import net.grandtheftmc.core.resourcepack.ResourcePackManager;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.vice.areas.AreaManager;
import net.grandtheftmc.vice.combatlog.CombatLogManager;
import net.grandtheftmc.vice.commands.AmmoCommand;
import net.grandtheftmc.vice.commands.AntiAuraCommand;
import net.grandtheftmc.vice.commands.AreaCommand;
import net.grandtheftmc.vice.commands.BackpackCommand;
import net.grandtheftmc.vice.commands.BackupCommand;
import net.grandtheftmc.vice.commands.BaltopCommand;
import net.grandtheftmc.vice.commands.BondsCommand;
import net.grandtheftmc.vice.commands.BribeCommand;
import net.grandtheftmc.vice.commands.CheatCodeCommand;
import net.grandtheftmc.vice.commands.ChunkUnloadCommand;
import net.grandtheftmc.vice.commands.ClearCommand;
import net.grandtheftmc.vice.commands.CopCommand;
import net.grandtheftmc.vice.commands.CoreNPCCommand;
import net.grandtheftmc.vice.commands.DrugCheckCommand;
import net.grandtheftmc.vice.commands.FeedCommand;
import net.grandtheftmc.vice.commands.FixCommand;
import net.grandtheftmc.vice.commands.HomeCommand;
import net.grandtheftmc.vice.commands.KillCommand;
import net.grandtheftmc.vice.commands.LotteryCommand;
import net.grandtheftmc.vice.commands.MoneyCommand;
import net.grandtheftmc.vice.commands.PayCommand;
import net.grandtheftmc.vice.commands.PrestigeCommand;
import net.grandtheftmc.vice.commands.RTPCommand;
import net.grandtheftmc.vice.commands.RankupCommand;
import net.grandtheftmc.vice.commands.ResetCommand;
import net.grandtheftmc.vice.commands.ResetStatsCommand;
import net.grandtheftmc.vice.commands.ResourcePackCommand;
import net.grandtheftmc.vice.commands.SellCommand;
import net.grandtheftmc.vice.commands.SkinCommand;
import net.grandtheftmc.vice.commands.SkinsCommand;
import net.grandtheftmc.vice.commands.SpawnCommand;
import net.grandtheftmc.vice.commands.SpectatorCommand;
import net.grandtheftmc.vice.commands.SpeedCommand;
import net.grandtheftmc.vice.commands.StackCommand;
import net.grandtheftmc.vice.commands.StatsCommand;
import net.grandtheftmc.vice.commands.TeleportCommand;
import net.grandtheftmc.vice.commands.TokenShopCommand;
import net.grandtheftmc.vice.commands.TopKillersCommand;
import net.grandtheftmc.vice.commands.TpaCommand;
import net.grandtheftmc.vice.commands.VehicleCommand;
import net.grandtheftmc.vice.commands.ViceAdminCommand;
import net.grandtheftmc.vice.commands.ViceRankCommand;
import net.grandtheftmc.vice.commands.ViceRanksCommand;
import net.grandtheftmc.vice.commands.WarpCommand;
import net.grandtheftmc.vice.commands.ZoneCommand;
import net.grandtheftmc.vice.display.DisplayManager;
import net.grandtheftmc.vice.dropship.DropShipManager;
import net.grandtheftmc.vice.drugs.DrugCommand;
import net.grandtheftmc.vice.drugs.DrugComponent;
import net.grandtheftmc.vice.drugs.DrugManager;
import net.grandtheftmc.vice.drugs.events.listener.DrugListener;
import net.grandtheftmc.vice.drugs.events.listener.DrugPlacementListener;
import net.grandtheftmc.vice.durability.DurabilityListener;
import net.grandtheftmc.vice.holidays.HolidayManager;
import net.grandtheftmc.vice.hologram.HologramManager;
import net.grandtheftmc.vice.items.BackpackManager;
import net.grandtheftmc.vice.items.GameItemCommand;
import net.grandtheftmc.vice.items.ItemManager;
import net.grandtheftmc.vice.items.KitCommand;
import net.grandtheftmc.vice.items.ShopCommand;
import net.grandtheftmc.vice.items.ShopManager;
import net.grandtheftmc.vice.listeners.ArmorEquip;
import net.grandtheftmc.vice.listeners.BlockDispense;
import net.grandtheftmc.vice.listeners.BlockPlace;
import net.grandtheftmc.vice.listeners.BreakBlock;
import net.grandtheftmc.vice.listeners.ChangeWorld;
import net.grandtheftmc.vice.listeners.Chat;
import net.grandtheftmc.vice.listeners.ChunkLoad;
import net.grandtheftmc.vice.listeners.CommandPreProcess;
import net.grandtheftmc.vice.listeners.CraftItem;
import net.grandtheftmc.vice.listeners.Damage;
import net.grandtheftmc.vice.listeners.Death;
import net.grandtheftmc.vice.listeners.Dispense;
import net.grandtheftmc.vice.listeners.Drop;
import net.grandtheftmc.vice.listeners.Enchant;
import net.grandtheftmc.vice.listeners.FoodChange;
import net.grandtheftmc.vice.listeners.Interact;
import net.grandtheftmc.vice.listeners.InventoryClick;
import net.grandtheftmc.vice.listeners.InventoryOpen;
import net.grandtheftmc.vice.listeners.InventoryPickupItem;
import net.grandtheftmc.vice.listeners.ItemBreak;
import net.grandtheftmc.vice.listeners.ItemComponent;
import net.grandtheftmc.vice.listeners.ItemSpawn;
import net.grandtheftmc.vice.listeners.ItemStack;
import net.grandtheftmc.vice.listeners.JetpackFuelUse;
import net.grandtheftmc.vice.listeners.Join;
import net.grandtheftmc.vice.listeners.Leave;
import net.grandtheftmc.vice.listeners.Login;
import net.grandtheftmc.vice.listeners.MenuListener;
import net.grandtheftmc.vice.listeners.MobSpawn;
import net.grandtheftmc.vice.listeners.Move;
import net.grandtheftmc.vice.listeners.PetListener;
import net.grandtheftmc.vice.listeners.Pickup;
import net.grandtheftmc.vice.listeners.PlayerEnterZone;
import net.grandtheftmc.vice.listeners.PlayerLeaveZone;
import net.grandtheftmc.vice.listeners.PortalEnter;
import net.grandtheftmc.vice.listeners.RenameComponent;
import net.grandtheftmc.vice.listeners.SmeltItem;
import net.grandtheftmc.vice.listeners.SwapHandItems;
import net.grandtheftmc.vice.listeners.Teleport;
import net.grandtheftmc.vice.listeners.UpdateListener;
import net.grandtheftmc.vice.listeners.VehicleUse;
import net.grandtheftmc.vice.listeners.VoteReward;
import net.grandtheftmc.vice.listeners.WeaponShoot;
import net.grandtheftmc.vice.listeners.WeaponUse;
import net.grandtheftmc.vice.lootcrates.CrateManager;
import net.grandtheftmc.vice.lootcrates.LootCrateCommand;
import net.grandtheftmc.vice.machine.MachineManager;
import net.grandtheftmc.vice.pickers.PickerCommand;
import net.grandtheftmc.vice.pickers.PickerManager;
import net.grandtheftmc.vice.redstone.RedstoneManager;
import net.grandtheftmc.vice.season.SeasonManager;
import net.grandtheftmc.vice.tasks.Lottery;
import net.grandtheftmc.vice.tasks.TaskManager;
import net.grandtheftmc.vice.users.AntiAfkTimer;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.ViceUserDAO;
import net.grandtheftmc.vice.users.ViceUserManager;
import net.grandtheftmc.vice.users.npcs.MachineNPC;
import net.grandtheftmc.vice.users.npcs.SkinsNPC;
import net.grandtheftmc.vice.users.npcs.TaxiNPC;
import net.grandtheftmc.vice.users.npcs.TrashCanManager;
import net.grandtheftmc.vice.users.npcs.shopnpc.ShopNPC;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.grandtheftmc.vice.weapon.WeaponRegistry;
import net.grandtheftmc.vice.weapon.skins.WeaponSkinManager;
import net.grandtheftmc.vice.world.WorldManager;

public class Vice extends JavaPlugin {

    private static DrugManager drugManager;
    private static Vice instance;
    private static ViceUserManager um;
    private static TaskManager tm;
    private static BackpackManager bam;
    private static WorldManager worldm;
    private static ItemManager im;
    private static ShopManager sm;
    private static CrateManager cm;
    private static TrashCanManager tcm;
    private static Lottery lottery;
    private static HolidayManager hm;
    private static GTMGuns wg;
    private static com.j0ach1mmall3.wastedvehicles.Main wv;
    private static CombatLogManager clm;
    private static ProtocolManager pm;
    private MachineManager machineManager;
    private static ViceSettings settings;
    private static EffectManager effectLib;
    private static Factions cartels;
    private static PickerManager pim;
    private static WeaponSkinManager wsm;
    private CoreCasino<Vice> coreCasino;
    private HologramManager hologramManager;
    private SeasonManager seasonManager;
    private DisplayManager displayManager;
    private AreaManager areaManager;

    public static boolean WEAPON_SKINS_FEATURE_FLAG = false;
    
//    private TagManager<Vice> tagManager;


    public static WorldManager getWorldManager() {
        return worldm;
    }

    public static CombatLogManager getCombatLogManager() {
        return clm;
    }

    public static Vice getInstance() {
        return instance;
    }

    public MachineManager getMachineManager() {
        return machineManager;
    }

    public static ViceUserManager getUserManager() {
        return um;
    }

    public static EffectManager getEffectLib() {
        return effectLib;
    }

    public static TaskManager getTaskManager() {
        return tm;
    }

    public static BackpackManager getBackpackManager() {
        return bam;
    }

    public static ItemManager getItemManager() {
        return im;
    }

    public static ViceSettings getSettings() {
        return settings;
    }

    public static ShopManager getShopManager() {
        return sm;
    }

    public static CrateManager getCrateManager() {
        return cm;
    }

    public static ProtocolManager getProtocolManager() {
        return pm;
    }

    public static HolidayManager getHolidayManager() {
        return hm;
    }

    public static DrugManager getDrugManager() {
        return drugManager;
    }

    public static WeaponSkinManager getWeaponSkinManager() {
        return wsm;
    }

    public static Factions getCartels() {
        if (cartels == null) cartels = Factions.getInstance();
        return cartels;
    }


    public static TrashCanManager getTrashCanManager() {
        return tcm;
    }

    public static GTMGuns getWastedGuns() {
        return wg;
    }

    public static com.j0ach1mmall3.wastedvehicles.Main getWastedVehicles() {
        return wv;
    }

    public static Lottery getLottery() {
        return lottery;
    }

    public static PickerManager getPickerManager() {
        return pim;
    }

    public static ResourcePackManager getResourcePackManager() {
        return Core.resourcePackManager;
    }

    public static void log(Level level, String message) {
        System.out.println("[Vice][" + level.toString() + "] " + message);
    }

    public static void log(String message) {
        log(Level.INFO, message);
    }

    public static void error(String message) {
        log(Level.SEVERE, message);
    }

    public DisplayManager getDisplayManager() {
        return this.displayManager;
    }

    public SeasonManager getSeasonManager() {
        return this.seasonManager;
    }

    public AreaManager getAreaManager() {
        return this.areaManager;
    }

    @Override
    public void onEnable() {
    	
    	// TODO remove
    	System.out.println("[Vice] Test.");
    	
        instance = this;
        settings = new ViceSettings();
        this.load();
        this.loadDependencies();
        effectLib = new EffectManager(this);
        um = new ViceUserManager();
        tm = new TaskManager();
        drugManager = new DrugManager();
        drugManager.start();
        im = new ItemManager();
        drugManager.loadDrugRecipes();
        bam = new BackpackManager();
        sm = new ShopManager();
        cm = new CrateManager();
        lottery = new Lottery();
        hm = new HolidayManager();
        wsm = new WeaponSkinManager();
        worldm = new WorldManager();
        worldm.load();
        clm = new CombatLogManager();
        clm.load();
        tcm = new TrashCanManager();
        pm = ProtocolLibrary.getProtocolManager();
        this.pim = new PickerManager();
//        this.tagManager = new TagManager<Vice>(this, NMSVersion.MC_1_12).onEnable(this);
//        new CartelsComponent(this, this.tagManager);
//        new NametagComponent(this, this.tagManager);
        Core.resourcePackManager = new ResourcePackManager(this, new RSPack_1_12(), new NMSTitle());
        Core.resourcePackManager.setResourcePack(NMSVersion.UNKNOWN, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_12_2, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_12_1, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_12, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_11_2, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_11, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip", "27D482BCBAB4431CEAD764728543B42F"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_10, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip", "33862C9199CFFB217D6808EFE06392DB"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_4, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip", "33862C9199CFFB217D6808EFE06392DB"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_2, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip", "33862C9199CFFB217D6808EFE06392DB"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_1, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip", "33862C9199CFFB217D6808EFE06392DB"));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9, new ResourcePack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip", "33862C9199CFFB217D6808EFE06392DB"));

        this.coreCasino = new CoreCasino<Vice>(this, new NMSTitle(), NMSVersion.MC_1_12);

        ServerUtil.runTaskLater(() -> {
            coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), 131, 77.1875, 173.5, 0f, 0f)));
            coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), 144, 77.1875, 173.5, 0f, 0f)));

            coreCasino.enabledAllGames();
        }, 15*20);

        Bukkit.getScheduler().runTaskTimer(this, new AntiAfkTimer(), 0, 20*60*10);

        new WeaponRegistry(wg.getWeaponManager());

        this.hologramManager = new HologramManager(this);
        this.displayManager = new DisplayManager(this, this.hologramManager);
        this.seasonManager = new SeasonManager(this, this.hologramManager);

        this.areaManager = new AreaManager(this);

        this.machineManager = new MachineManager(this);
//        new GunTestingListener().onEnable(this); //TODO: TESTING.
        new DrugComponent(this);
        new DropShipManager(this, this.areaManager, im);
        new RedstoneManager();

        this.registerCommands();
        this.registerListeners();

        // Utils.startEnchantmentShineRemover(pm, this);

        //EventCatcher
//        detector.addListener((plugin, event) -> {
//            log(event.getEventName() + " cancelled by: " + plugin.getName());
//        });

        World spawn = Bukkit.getWorld("spawn");
        Core.getNPCManager().registerCoreNPC(new ShopNPC(new Location(spawn, 91, 79, 214.5)));
        Core.getNPCManager().registerCoreNPC(new MachineNPC(this.machineManager, new Location(spawn, 159, 77, 217)));
        Core.getNPCManager().registerCoreNPC(new TaxiNPC(new Location(spawn, 137.5, 77, 230.5)));
        
        if(WEAPON_SKINS_FEATURE_FLAG) {
            Core.getNPCManager().registerCoreNPC(new SkinsNPC(new Location(spawn, -325.5, 25, 206.5)));
        }
        
        for (World world : Bukkit.getWorlds()) {
            world.setGameRuleValue("announceAdvancements", "false");
            if (world.getName().equals("world")) {
                for (Chunk chunk : world.getLoadedChunks()) {
                    chunk.unload();
                }
            }
        }
    }

    private void loadDependencies() {
        PluginManager plm = Bukkit.getPluginManager();
        Plugin wastedGunsPlugin = plm.getPlugin("WastedGuns");
        if (wastedGunsPlugin == null) {
            log("Error while enabling WastedGuns dependency. Is it installed?");
        } else {
            wg = (GTMGuns) wastedGunsPlugin;
        }
        Plugin wastedVehiclesPlugin = plm.getPlugin("WastedVehicles");
        if (wastedVehiclesPlugin == null) {
            log("Error while enabling WastedVehicles dependency. Is it installed?");
        } else {
            wv = (com.j0ach1mmall3.wastedvehicles.Main) wastedVehiclesPlugin;
        }
    }

    @Override
    public void onDisable() {
        Bukkit.getScheduler().cancelTasks(this);

        this.seasonManager.onDisable(this);
        this.hologramManager.onDisable(this);
        this.machineManager.onDisable(this);

//        this.tagManager.onDisable(this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            user.setBooleanToStorage(BooleanStorageType.KICKED, true);
            if (user.isInCombat()) user.setLastTag(-1);
            Vice.getUserManager().unloadUser(player.getUniqueId());
        }

        this.save();
        this.coreCasino.removeAllGames();
    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Death(), this);
        pm.registerEvents(new ItemStack(), this);
        pm.registerEvents(new Login(), this);
        pm.registerEvents(new Leave(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new Join(this.hologramManager), this);
        pm.registerEvents(new Pickup(), this);
        pm.registerEvents(new WeaponUse(), this);
        pm.registerEvents(new Drop(), this);
        pm.registerEvents(new Interact(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new InventoryOpen(), this);
        pm.registerEvents(new BlockDispense(), this);
        pm.registerEvents(new BackpackManager(), this);
        pm.registerEvents(new UpdateListener(), this);
        pm.registerEvents(new Move(), this);
        pm.registerEvents(new Damage(), this);
        pm.registerEvents(new ChangeWorld(), this);
        pm.registerEvents(new FoodChange(), this);
        pm.registerEvents(new VehicleUse(), this);
        pm.registerEvents(new PetListener(), this);
        pm.registerEvents(new ItemBreak(), this);
        pm.registerEvents(new BreakBlock(), this);
        pm.registerEvents(new WeaponShoot(), this);
        pm.registerEvents(new SwapHandItems(), this);
        pm.registerEvents(new DrugListener(), this);
        pm.registerEvents(new DrugPlacementListener(), this);
//        pm.registerEvents(new ResourcePack(), this);
//        pm.registerEvents(new FireListener(), this);
        pm.registerEvents(new VoteReward(), this);
        pm.registerEvents(new CraftItem(), this);
        pm.registerEvents(new SmeltItem(), this);
        pm.registerEvents(new Enchant(), this);
        pm.registerEvents(new DurabilityListener(), this);
        pm.registerEvents(new InventoryPickupItem(), this);
        pm.registerEvents(new CommandPreProcess(), this);
        pm.registerEvents(new ArmorEquip(), this);
        pm.registerEvents(new BlockPlace(), this);
        pm.registerEvents(new ChunkLoad(), this);
        pm.registerEvents(new Dispense(), this);
        pm.registerEvents(new MobSpawn(), this);
        pm.registerEvents(new JetpackFuelUse(), this);
        pm.registerEvents(new Teleport(), this);
        pm.registerEvents(new JetpackFuelUse(), this);
        pm.registerEvents(new PlayerEnterZone(), this);
        pm.registerEvents(new PlayerLeaveZone(), this);
        pm.registerEvents(new PortalEnter(), this);
        pm.registerEvents(new ItemSpawn(), this);
        pm.registerEvents(tcm, this);
        new HomeCommand();
        new PrestigeCommand();
        new CoreNPCCommand(this.machineManager);
        //  new LogoutCommand();
        new ItemComponent();
        new RenameComponent(this);

        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerCommands() {
        this.getCommand("ammo").setExecutor(new AmmoCommand());
        this.getCommand("feed").setExecutor(new FeedCommand());
        this.getCommand("lootcrate").setExecutor(new LootCrateCommand());
        this.getCommand("spawn").setExecutor(new SpawnCommand());
        this.getCommand("warp").setExecutor(new WarpCommand());
        this.getCommand("viceadmin").setExecutor(new ViceAdminCommand());
        this.getCommand("gameitem").setExecutor(new GameItemCommand());
        this.getCommand("kit").setExecutor(new KitCommand());
        this.getCommand("shop").setExecutor(new ShopCommand());
        this.getCommand("vicerank").setExecutor(new ViceRankCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        new BaltopCommand();
        this.getCommand("pay").setExecutor(new PayCommand());
        this.getCommand("rankup").setExecutor(new RankupCommand());
        this.getCommand("tpa").setExecutor(new TpaCommand());
        this.getCommand("tpahere").setExecutor(new TpaCommand());
        this.getCommand("tpaccept").setExecutor(new TpaCommand());
        this.getCommand("tpdeny").setExecutor(new TpaCommand());
        this.getCommand("bonds").setExecutor(new BondsCommand());
        this.getCommand("kill").setExecutor(new KillCommand());
        this.getCommand("suicide").setExecutor(new KillCommand());
        this.getCommand("picker").setExecutor(new PickerCommand());
        this.getCommand("vehicle").setExecutor(new VehicleCommand());
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("bribe").setExecutor(new BribeCommand());
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("tokenshop").setExecutor(new TokenShopCommand());
        this.getCommand("antiaura").setExecutor(new AntiAuraCommand());
        this.getCommand("clear").setExecutor(new ClearCommand());
        this.getCommand("fix").setExecutor(new FixCommand());
//        this.getCommand("near").setExecutor(new NearCommand()); Not wanted on vice.
        this.getCommand("teleport").setExecutor(new TeleportCommand());
        this.getCommand("spectator").setExecutor(new SpectatorCommand());
        this.getCommand("backup").setExecutor(new BackupCommand());
        this.getCommand("resetstats").setExecutor(new ResetStatsCommand());
        this.getCommand("lottery").setExecutor(new LotteryCommand());
        this.getCommand("speed").setExecutor(new SpeedCommand());
        this.getCommand("chunkunload").setExecutor(new ChunkUnloadCommand());
        this.getCommand("topkillers").setExecutor(new TopKillersCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("drugs").setExecutor(new DrugCommand());
        this.getCommand("resourcepack").setExecutor(new ResourcePackCommand());
        this.getCommand("drugcheck").setExecutor(new DrugCheckCommand());
        this.getCommand("viceranks").setExecutor(new ViceRanksCommand());
        this.getCommand("stack").setExecutor(new StackCommand());
        this.getCommand("cop").setExecutor(new CopCommand());
        this.getCommand("rtp").setExecutor(new RTPCommand());
        this.getCommand("area").setExecutor(new AreaCommand(this));
        new CheatCodeCommand();
        new ZoneCommand();
        new SellCommand();
        
        if(WEAPON_SKINS_FEATURE_FLAG) {
            new SkinCommand();
            new SkinsCommand();
        }
    }

    private void load() {
        this.setupTables();
        settings.setPlayerCacheConfig(Utils.loadConfig("playercache"));
        settings.setViceConfig(Utils.loadConfig("vice"));
        settings.setWarpsConfig(Utils.loadConfig("warps"));
        settings.setItemsConfig(Utils.loadConfig("items"));
        settings.setKitsConfig(Utils.loadConfig("kits"));
        settings.setLootConfig(Utils.loadConfig("loot"));
        settings.setLootCratesConfig(Utils.loadConfig("lootcrates"));
        settings.setLotteryConfig(Utils.loadConfig("lottery"));
        settings.setDrugBlocksConfig(Utils.loadConfig("drugblocks"));
        settings.setDrugDealersConfig(Utils.loadConfig("drugdealers"));
        settings.setPickersConfig(Utils.loadConfig("pickers"));
        settings.setUpgradeContainersConfig(Utils.loadConfig("upgradedcontainers"));
        settings.setHomesConfig(Utils.loadConfig("homes"));
        settings.setZoneConfig(Utils.loadConfig("zones"));
        YamlConfiguration c = settings.getViceConfig();
        settings.setMap(c.getString("map"));
        this.loadMenus();
        this.loadSettings();
        settings.setOneElevenRespack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.11.zip");
        settings.setOneElevenHash("27D482BCBAB4431CEAD764728543B42F");
        settings.setOneTenRespack("http://cdn.grandtheftmc.net/VICE-2.0.8-1.10.zip");
        settings.setOneTenHash("33862C9199CFFB217D6808EFE06392DB");
    }

    public void reload() {
        this.load();
        im.loadItems();
        im.loadKits();
        cm.loadCrates();
        worldm.load();
        lottery.loadConfig();
    }

    public void save() {
        im.saveItems();
        cm.saveCrates();
        im.saveKits();
        worldm.save();
        // lottery.saveConfig();
        this.drugManager.stop();
        effectLib.dispose();
        pim.save();
        clm.save();
    }

    public void setupTables() {
        // TODO redo this shit
//        Core.sql.updateAsyncLater("CREATE TABLE IF NOT EXISTS " + Core.name() + "(uuid varchar(40) NOT NULL, name varchar(17) NOT NULL, rank varchar(255) DEFAULT 'HOBO', copRank varchar(366) DEFAULT NULL, kills int(11) default 0, deaths int(11) default 0, money double default 0, killStreak int(11) default 0, bonds int(11) default 0, backpackContents longtext, kitExpiries varchar(255), houses varchar(255), gang varchar(255), gangRank varchar(255) NOT NULL DEFAULT 'member', jailTimer int(11) DEFAULT -1, jailCop varchar(255) default NULL, jailCopName varchar(255) default NULL, personalVehicle varchar(255), cheatcodes BLOB, PRIMARY KEY (uuid))");
        ServerUtil.runTaskAsync(() -> {
            ViceUserDAO.createTable();
            ViceUserDAO.managePlaytime();
        });

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                try (ResultSet rs = Core.sql.query("select * from " + Core.name() + " LIMIT 1;")) {
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    List<String> columns = new ArrayList<>();
//
//                    for (int i = 1; i <= metaData.getColumnCount(); i++)
//                        columns.add(metaData.getColumnName(i).toLowerCase());
//
//                    if (!columns.contains("playtime"))
//                        Core.sql.updateAsyncLater("ALTER TABLE " + Core.name() + " ADD COLUMN playtime BIGINT(20) NOT NULL DEFAULT 0;");
//
//                    for (AmmoType type : AmmoType.values())
//                        if (!type.isInInventory() && !columns.contains(type.toString().toLowerCase()))
//                            Core.sql.update("alter table " + Core.name() + " add column " + type.toString().toLowerCase() + " int(11) default 0;");
//
//                    for (VehicleProperties vehicle : Vice.getWastedVehicles().getBabies().getVehicleProperties()) {
//                        if (!columns.contains(vehicle.getIdentifier().toLowerCase()))
//                            Core.sql.update("alter table " + Core.name() + " add column " + vehicle.getIdentifier().toLowerCase() + " BOOLEAN not null default 0;");
//
//                        if (!columns.contains(vehicle.getIdentifier().toLowerCase() + ":info"))
//                            Core.sql.update("alter table " + Core.name() + " add column `" + vehicle.getIdentifier().toLowerCase() + ":info` VARCHAR(255);");
//                    }
//                    rs.close();
//                } catch (SQLException e) {
//                    Core.error("Error while altering " + Core.name() + " table: ");
//                    e.printStackTrace();
//                }
//            }
//        }.runTaskAsynchronously(this);
    }

    private void loadMenus() {
        MenuManager.addMenu("phone", 54, "&7&lPhone");

        MenuManager.addMenu("account", 54, "&d&lMy Account");
        MenuManager.addMenu("ranks", 54, "&a&lRanks");
        MenuManager.addMenu("vicestats", 54, "&d&lStats");
        MenuManager.addMenu("prefs", 54, "&5&lPreferences");
        MenuManager.addMenu("contacts", 54, "&6&lContacts");

        MenuManager.addMenu("kits", 54, "&b&lKits");

        MenuManager.addMenu("taxi", 54, "&e&lTaxi Service");
        MenuManager.addMenu("taxiplayers", 54, "&e&lTaxi Service: Players");
        MenuManager.addMenu("taxihouses", 54, "&e&lTaxi Service: &3&lHouses");
        MenuManager.addMenu("taxiotherplayers", 54, "&e&lPick up a player!");
        MenuManager.addMenu("taxiwarps", 54, "&e&lTaxi Service: Warps");

        MenuManager.addMenu("ammopouch", 36, "&c&lAmmo Pouch");

        MenuManager.addMenu("jail", 54, "&c&lJail");

        MenuManager.addMenu("property", 54, "&2&lProperty");
        MenuManager.addMenu("vehicles", 54, "&4&lVehicles");
        MenuManager.addMenu("vehicleshop", 54, "&4&lVehicle Shop");
        MenuManager.addMenu("buyvehicle", 54, "&4&lBuy Vehicle Shop");
        MenuManager.addMenu("sellvehicle", 54, "&4&lSell Vehicle");
        MenuManager.addMenu("repairvehicle", 54, "&4&lRepair Vehicle");
        MenuManager.addMenu("personalvehicle", 54, "&4&lPersonal Vehicle");
        MenuManager.addMenu("mechanic", 54, "&4&lMechanic");

        MenuManager.addMenu("cheatcodes", 54, "&2&lCheat Codes");
        MenuManager.addMenu("choose_villager_type", 54, "&2&lChoose The Villager's Job");

        MenuManager.addMenu("armorupgrade", 54, "&b&lArmor Upgrade");

        MenuManager.addMenu("lottery", 54, "&e&lLottery");

        MenuManager.addMenu("drugdealer", 54, "&c&lDrug Dealer");

    }

    private void loadSettings() {
        Settings settings = Core.getSettings();
        settings.setDefaultGameMode(GameMode.SURVIVAL);
        settings.setPetsVulnerable(true);
        settings.setServerWarperEnabled(false);
        settings.setTokenShopEnabled(true);
        settings.setCanOpenChests(true);
        settings.setCanInteractInventory(true);
        settings.setLoadCosmetics(true);
        settings.setCanCraft(true);
        //World plots = Bukkit.getWorld(Vice.getSettings().getPlotsWorld());
        //plots.setPVP(false);
    }
}
