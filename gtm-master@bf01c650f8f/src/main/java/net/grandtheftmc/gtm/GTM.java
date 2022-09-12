package net.grandtheftmc.gtm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

import de.slikey.effectlib.EffectManager;
import net.buycraft.plugin.bukkit.BuycraftPlugin;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Settings;
import net.grandtheftmc.core.casino.CoreCasino;
import net.grandtheftmc.core.casino.slot.SlotMachine;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.event.EventCommand;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.resourcepack.RSPack_1_12;
import net.grandtheftmc.core.resourcepack.ResourcePack;
import net.grandtheftmc.core.resourcepack.ResourcePackManager;
import net.grandtheftmc.core.util.ItemStackManager;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.gtm.armor.ArmorShopManager;
import net.grandtheftmc.gtm.bounties.BountyManager;
import net.grandtheftmc.gtm.commands.AmmoCommand;
import net.grandtheftmc.gtm.commands.AntiAuraCommand;
import net.grandtheftmc.gtm.commands.BackpackCommand;
import net.grandtheftmc.gtm.commands.BackupCommand;
import net.grandtheftmc.gtm.commands.BribeCommand;
import net.grandtheftmc.gtm.commands.CheatCodeCommand;
import net.grandtheftmc.gtm.commands.ChestCheckCommand;
import net.grandtheftmc.gtm.commands.ChristmasCommand;
import net.grandtheftmc.gtm.commands.ChunkUnloadCommand;
import net.grandtheftmc.gtm.commands.ClearCommand;
import net.grandtheftmc.gtm.commands.CoreNPCCommand;
import net.grandtheftmc.gtm.commands.DrugCheckCommand;
import net.grandtheftmc.gtm.commands.DrugDealerCommand;
import net.grandtheftmc.gtm.commands.FeedCommand;
import net.grandtheftmc.gtm.commands.FixCommand;
import net.grandtheftmc.gtm.commands.GTMAdminCommand;
import net.grandtheftmc.gtm.commands.GTMRankCommand;
import net.grandtheftmc.gtm.commands.GTMRanksCommand;
import net.grandtheftmc.gtm.commands.HalloweenCommand;
import net.grandtheftmc.gtm.commands.KillCommand;
import net.grandtheftmc.gtm.commands.LotteryCommand;
import net.grandtheftmc.gtm.commands.MoneyCommand;
import net.grandtheftmc.gtm.commands.NearCommand;
import net.grandtheftmc.gtm.commands.PayCommand;
import net.grandtheftmc.gtm.commands.PermitsCommand;
import net.grandtheftmc.gtm.commands.PickerCommand;
import net.grandtheftmc.gtm.commands.RankupCommand;
import net.grandtheftmc.gtm.commands.ResetCommand;
import net.grandtheftmc.gtm.commands.ResourcePackCommand;
import net.grandtheftmc.gtm.commands.SellCommand;
import net.grandtheftmc.gtm.commands.SetRarityCommand;
import net.grandtheftmc.gtm.commands.SettingsCommand;
import net.grandtheftmc.gtm.commands.SkinCommand;
import net.grandtheftmc.gtm.commands.SkinsCommand;
import net.grandtheftmc.gtm.commands.SpectatorCommand;
import net.grandtheftmc.gtm.commands.SpeedCommand;
import net.grandtheftmc.gtm.commands.StatsCommand;
import net.grandtheftmc.gtm.commands.TeleportCommand;
import net.grandtheftmc.gtm.commands.TokenShopCommand;
import net.grandtheftmc.gtm.commands.TopKillersCommand;
import net.grandtheftmc.gtm.commands.TransferCommand;
import net.grandtheftmc.gtm.commands.VehicleCommand;
import net.grandtheftmc.gtm.database.dao.MutexDAO;
import net.grandtheftmc.gtm.drugs.DrugCommand;
import net.grandtheftmc.gtm.drugs.DrugManager;
import net.grandtheftmc.gtm.drugs.events.listener.DrugListener;
import net.grandtheftmc.gtm.drugs.events.listener.DrugPlacementListener;
import net.grandtheftmc.gtm.event.EventManager;
import net.grandtheftmc.gtm.event.christmas.ChristmasListener;
import net.grandtheftmc.gtm.event.easter.EasterEggCommand;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.gang.command.GangAdminCommand;
import net.grandtheftmc.gtm.holidays.HolidayManager;
import net.grandtheftmc.gtm.items.BackpackManager;
import net.grandtheftmc.gtm.items.GameItemCommand;
import net.grandtheftmc.gtm.items.ItemManager;
import net.grandtheftmc.gtm.items.KitCommand;
import net.grandtheftmc.gtm.items.ShopCommand;
import net.grandtheftmc.gtm.items.ShopManager;
import net.grandtheftmc.gtm.listeners.ArmorEquip;
import net.grandtheftmc.gtm.listeners.BreakBlock;
import net.grandtheftmc.gtm.listeners.ChangeWorld;
import net.grandtheftmc.gtm.listeners.Chat;
import net.grandtheftmc.gtm.listeners.CommandPreProcess;
import net.grandtheftmc.gtm.listeners.Craft;
import net.grandtheftmc.gtm.listeners.Damage;
import net.grandtheftmc.gtm.listeners.Death;
import net.grandtheftmc.gtm.listeners.Dispense;
import net.grandtheftmc.gtm.listeners.Drop;
import net.grandtheftmc.gtm.listeners.DrugBlockRemovalListener;
import net.grandtheftmc.gtm.listeners.FireListener;
import net.grandtheftmc.gtm.listeners.FoodChange;
import net.grandtheftmc.gtm.listeners.GamemodeChange;
import net.grandtheftmc.gtm.listeners.Interact;
import net.grandtheftmc.gtm.listeners.InventoryClick;
import net.grandtheftmc.gtm.listeners.InventoryOpen;
import net.grandtheftmc.gtm.listeners.ItemBreak;
import net.grandtheftmc.gtm.listeners.Join;
import net.grandtheftmc.gtm.listeners.Leave;
import net.grandtheftmc.gtm.listeners.Login;
import net.grandtheftmc.gtm.listeners.MenuListener;
import net.grandtheftmc.gtm.listeners.MobSpawn;
import net.grandtheftmc.gtm.listeners.Move;
import net.grandtheftmc.gtm.listeners.MovementCheat;
import net.grandtheftmc.gtm.listeners.PetListener;
import net.grandtheftmc.gtm.listeners.Pickup;
import net.grandtheftmc.gtm.listeners.PortalEnter;
import net.grandtheftmc.gtm.listeners.PotionSplash;
import net.grandtheftmc.gtm.listeners.PrepareItemCraft;
import net.grandtheftmc.gtm.listeners.SwapHandItems;
import net.grandtheftmc.gtm.listeners.UpdateListener;
import net.grandtheftmc.gtm.listeners.VehicleUse;
import net.grandtheftmc.gtm.listeners.VoteReward;
import net.grandtheftmc.gtm.listeners.WeaponShoot;
import net.grandtheftmc.gtm.listeners.WeaponUse;
import net.grandtheftmc.gtm.lootcrates.CrateManager;
import net.grandtheftmc.gtm.lootcrates.LootCrateCommand;
import net.grandtheftmc.gtm.tasks.Lottery;
import net.grandtheftmc.gtm.tasks.TaskManager;
import net.grandtheftmc.gtm.trashcan.TrashCanManager;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.npcs.ArmorNPC;
import net.grandtheftmc.gtm.users.npcs.BankTellerNPC;
import net.grandtheftmc.gtm.users.npcs.CarNPC;
import net.grandtheftmc.gtm.users.npcs.CasinoNPC;
import net.grandtheftmc.gtm.users.npcs.CriminalNPC;
import net.grandtheftmc.gtm.users.npcs.FoodNPC;
import net.grandtheftmc.gtm.users.npcs.HeadSellerNPC;
import net.grandtheftmc.gtm.users.npcs.HitmanNPC;
import net.grandtheftmc.gtm.users.npcs.MechanicNPC;
import net.grandtheftmc.gtm.users.npcs.PoliceNPC;
import net.grandtheftmc.gtm.users.npcs.RewardsNPC;
import net.grandtheftmc.gtm.users.npcs.ShopNPC;
import net.grandtheftmc.gtm.users.npcs.SkinsNPC;
import net.grandtheftmc.gtm.users.npcs.TaxiNPC;
import net.grandtheftmc.gtm.warps.SpawnCommand;
import net.grandtheftmc.gtm.warps.TpaCommand;
import net.grandtheftmc.gtm.warps.WarpCommand;
import net.grandtheftmc.gtm.warps.WarpManager;
import net.grandtheftmc.gtm.wastedbarrels.BarrelListener;
import net.grandtheftmc.gtm.wastedbarrels.BarrelManager;
import net.grandtheftmc.gtm.weapon.WeaponRegistry;
import net.grandtheftmc.gtm.weapon.skins.WeaponSkinManager;
import net.grandtheftmc.guns.GTMGuns;
import net.minecraft.server.v1_12_R1.WorldServer;

public class GTM extends JavaPlugin {

    private static final String[][] RES_PACKS = {
//    		{"http://cdn.grandtheftmc.net/GTM-2.4.12-1.10.zip", "9982D598B60D926F1CE6"}, //1.9  - 1.10
//            {"http://cdn.grandtheftmc.net/GTM-2.4.12-1.11.zip", "FA2C2DBEB6FF0CFB991D"}  //1.11 - 1.12+
// -- original is above
    		// SHA-1 hash is usually rendered as HEX (base16), 40 digits long.
    		// need to convert to 20 bytes long
    		{"http://cdn.grandtheftmc.net/GTM-2.4.12-1.10.zip", "2F0A69EBFB74C513B8371894BAC7C2FD0B8EE2A2"}, //1.9  - 1.10
    		{"http://cdn.grandtheftmc.net/GTM-2.4.13-1.11.zip", "8E3C7FE3A16E00261C4054241BBC166BF23239DE"},  //1.11 - 1.12
    		{"http://cdn.grandtheftmc.net/GTM-2.4.13-1.13.zip", "2A3BA442DA89B43DF6207C3DF829A1E6D9D43CD2"}  //1.13+
    };

    private final Set<UUID> transferingPlayers = new HashSet<UUID>();

    private static DrugManager drugManager;
    private static GTM instance;
    private static GTMUserManager um;
    private static WarpManager wm;
    private static BountyManager bm;
    private static TaskManager tm;
    private static BackpackManager bam;
    private static ItemManager im;
    private static ShopManager sm;
    private static GangManager gm;
    private static CrateManager cm;
    private static BarrelManager wbm;
    private static EffectManager effectLib;
    private static Lottery lottery;
    private static HolidayManager hm;
    private static WeaponSkinManager wsm;
    /** The handler for all events, like christmas etc. */
    private EventManager eventManager;
    private static GTMGuns wg;
    private static com.j0ach1mmall3.wastedvehicles.Main wv;
    private static com.j0ach1mmall3.wastedcops.Main wc;
    private static ProtocolManager pm;
    private static GTMSettings settings;
    private static BuycraftPlugin bp;
    private CoreCasino<GTM> coreCasino;
    
    public static boolean WEAPON_SKINS_FEATURE_FLAG = true;

    public Set<UUID> getTransferingPlayers() {
        return transferingPlayers;
    }

    public static GTM getInstance() {
        return instance;
    }

    /**
     * @deprecated - Please use {@link GTMUserManager#getInstance} instead.
     */
    @Deprecated
	public static GTMUserManager getUserManager() {
        return um;
    }

    public static WarpManager getWarpManager() {
        return wm;
    }

    public static BountyManager getBountyManager() {
        return bm;
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

    public static GTMSettings getSettings() {
        return settings;
    }

    public static ShopManager getShopManager() {
        return sm;
    }

    public static GangManager getGangManager() {
        return gm;
    }

    public static CrateManager getCrateManager() {
        return cm;
    }

    public static BarrelManager getBarrelManager() {
        return wbm;
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

    public static GTMGuns getWastedGuns() {
        return wg;
    }

    public static com.j0ach1mmall3.wastedvehicles.Main getWastedVehicles() {
        return wv;
    }

    public static com.j0ach1mmall3.wastedcops.Main getWastedCops() {
        return wc;
    }

    public static Lottery getLottery() {
        return lottery;
    }

    public static void log(String s) {
        GTM.getInstance().getLogger().log(Level.ALL, s);
    }

    public static void error(String s) {
        GTM.getInstance().getLogger().log(Level.SEVERE, s);
    }

    public static EffectManager getEffectLib() {
        return effectLib;
    }

    public static BuycraftPlugin getBuycraftX() {
        return bp;
    }

    public static ResourcePackManager getResourcePackManager() {
        return Core.resourcePackManager;
    }

    @Override
    public void onEnable() {
        instance = this;
        settings = new GTMSettings();

        this.load();
        this.loadDependencies();

        if (!Core.getSettings().isSister()) {
            drugManager = new DrugManager();
        }

        effectLib = new EffectManager(this);
        um = GTMUserManager.getInstance();
        wm = new WarpManager();
        bm = new BountyManager();
        tm = new TaskManager();
        bam = new BackpackManager();
        wsm = new WeaponSkinManager();
        new WeaponRegistry(this, wg.getWeaponManager());
        im = new ItemManager();
        sm = new ShopManager();
        gm = new GangManager(this);
        cm = new CrateManager();
        wbm = new BarrelManager();
        lottery = new Lottery();
        hm = new HolidayManager();
        bp = (BuycraftPlugin) Bukkit.getPluginManager().getPlugin("BuycraftX");

        if (!Core.getSettings().isSister()) {
            drugManager.start();
        }

        Core.resourcePackManager = new ResourcePackManager(this, new RSPack_1_12(), new NMSTitle());
        Core.resourcePackManager.setResourcePack(NMSVersion.UNKNOWN, new ResourcePack(RES_PACKS[1][0], RES_PACKS[1][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_13, new ResourcePack(RES_PACKS[2][0], RES_PACKS[2][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_12_1, new ResourcePack(RES_PACKS[1][0], RES_PACKS[1][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_12, new ResourcePack(RES_PACKS[1][0], RES_PACKS[1][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_11_2, new ResourcePack(RES_PACKS[1][0], RES_PACKS[1][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_11, new ResourcePack(RES_PACKS[1][0], RES_PACKS[1][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_10, new ResourcePack(RES_PACKS[0][0], RES_PACKS[0][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_4, new ResourcePack(RES_PACKS[0][0], RES_PACKS[0][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_2, new ResourcePack(RES_PACKS[0][0], RES_PACKS[0][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9_1, new ResourcePack(RES_PACKS[0][0], RES_PACKS[0][1]));
        Core.resourcePackManager.setResourcePack(NMSVersion.MC_1_9, new ResourcePack(RES_PACKS[0][0], RES_PACKS[0][1]));

        this.coreCasino = new CoreCasino<GTM>(this, new NMSTitle(), NMSVersion.MC_1_12);
        if (!Core.getSettings().isSister()) {
            ServerUtil.runTaskLater(() -> {
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -293, 29.1, 252.3, 0f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -293, 29.1, 263.7, -180f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -287, 29.1, 253.3, 0f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -287, 29.1, 262.7, -180f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -273.3, 29.1, 259, 90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -270.3, 29.1, 271, 90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -281.7, 29.1, 269, -90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -270.3, 29.1, 275, 90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -281.7, 29.1, 277, -90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -273.3, 29.1, 288, 90f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -277, 29.1, 290.7, -180f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -289, 29.1, 292.7, -180f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -289, 29.1, 283.3, 0f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -298, 29.1, 294.7, -180f, 0f)));
                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -298, 29.1, 281.3, 0f, 0f)));
//                coreCasino.addGame(new SlotMachine(coreCasino, new Location(Bukkit.getWorld("spawn"), -289, 29.1, 283.5, 0f, 0f)));

                coreCasino.enabledAllGames();
            }, 15 * 20);
        }

        //Spawn NPC's
        ServerUtil.runTaskLater(() -> {
            World spawn = Bukkit.getWorld("spawn");
            Core.getNPCManager().registerCoreNPC(new TaxiNPC(new Location(spawn, -351.5, 25, 207.5)));
            Core.getNPCManager().registerCoreNPC(new RewardsNPC(new Location(spawn, -319.5, 26, 183.5)));
            Core.getNPCManager().registerCoreNPC(new CarNPC(new Location(spawn, -278.5, 26, 239.5)));
            Core.getNPCManager().registerCoreNPC(new BankTellerNPC(new Location(spawn, -392.5, 27, 231.5)));
            Core.getNPCManager().registerCoreNPC(new ShopNPC(wg.getWeaponManager(), new Location(spawn, -371.5, 26.6250, 239.5)));
            Core.getNPCManager().registerCoreNPC(new FoodNPC(new Location(spawn, -361.5, 26, 255.5)));
            Core.getNPCManager().registerCoreNPC(new ArmorNPC(new Location(spawn, -363.5, 26, 267.5)));
            Core.getNPCManager().registerCoreNPC(new HeadSellerNPC(new Location(spawn, -292.5, 26, 312.5)));
            Core.getNPCManager().registerCoreNPC(new PoliceNPC(new Location(spawn, -399.5, 26, 171.5)));
            Core.getNPCManager().registerCoreNPC(new HitmanNPC(new Location(spawn, -410.5, 26, 160.5)));
            Core.getNPCManager().registerCoreNPC(new CriminalNPC(new Location(spawn, -385.5, 25.5, 159.5)));
            Core.getNPCManager().registerCoreNPC(new MechanicNPC(new Location(spawn, -306.5, 25, 224.5)));
            Core.getNPCManager().registerCoreNPC(new CasinoNPC(new Location(spawn, -306.7, 29, 257.6)));

            if(WEAPON_SKINS_FEATURE_FLAG) {
                Core.getNPCManager().registerCoreNPC(new SkinsNPC(new Location(spawn, -296.5, 25.5, 150.5)));
            }
        }, 40L);

        new ArmorShopManager(this, im);
        new EasterEggCommand(null);

        if (Core.getSettings().isSister()) {
            ProtocolLibrary.getProtocolManager().addPacketListener(new DrugBlockRemovalListener());
        }

        this.registerCommands();
        this.registerListeners();
        // this.loadShopMenus();

        // initialize the event manager
        EventManager.init(this, Core.getSettings().getType().toString().toUpperCase() + Core.getSettings().getNumber());

//       This needs updating to 1.12
//       Utils.startEnchantmentShineRemover(Core.getProtocolLib(), this);
        
        new ItemStackManager();
        
        // every 5 mins attempt to remove specific entities
        Bukkit.getScheduler().runTaskTimer(this, () -> {
        	
        	List<Entity> toRemove = new ArrayList<>();
        	
        	for (World world : Bukkit.getWorlds()){
        		Core.log("[GTM][RemoveTask] Searching world " + world.getName());
        		for (LivingEntity le : world.getLivingEntities()){
        			if (le.getType() == EntityType.ENDER_DRAGON){
        				Core.log("[GTM][RemoveTask] Found enderdragon in " + world.getName());
        				toRemove.add(le);
        			}
        		}
        	}
        	
        	Core.log("[GTM][RemoveTask] Attempting to remove " + toRemove.size() + " ender dragons...");
        	
        	for (Entity ent : toRemove){
        		
        		// if chunk is not loaded, load it
        		Chunk chunk = ent.getWorld().getChunkAt(ent.getLocation());
        		if (chunk != null && !chunk.isLoaded()){
        			chunk.load();
        		}
        		
        		// get craft world
        		WorldServer craftWorld = ((CraftWorld) ent.getWorld()).getHandle();
        		
        		// get the craft entity
        		net.minecraft.server.v1_12_R1.Entity craftEntity = ((CraftEntity) ent).getHandle();
        		
        		// attempt to remove entity from world
        		craftWorld.removeEntity(craftEntity);
        		ent.remove();
        	}
        	
        }, 0L, 20L * 300);
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
        Plugin wastedCopsPlugin = plm.getPlugin("WastedCops");
        if (wastedCopsPlugin == null) {
            log("Error while enabling WastedCops dependency. Is it installed?");
        } else {
            wc = (com.j0ach1mmall3.wastedcops.Main) wastedCopsPlugin;
        }
        pm = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onDisable() {
    	
    	System.out.println("[GTM] Disabling GTM...");
    	
        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
            	
            	// grab user
                GTMUser user = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
                if (user != null){
                	
                	// set as kicked
                    user.setKicked(true);
                    
                    System.out.println("[GTM] Attempting to save player=" + player.getName());
                    if (user.isInCombat()) user.setLastTag(-1);

                    if (player.getOpenInventory() != null && player.getOpenInventory().getTopInventory() != null) {
                        String name = ChatColor.stripColor(player.getOpenInventory().getTopInventory().getTitle());
                        
                        // if backpack is open
                        if (name.equalsIgnoreCase("Backpack")){
                        	System.out.println("[GTM] Saving backpack contents for player=" + player.getName());

                            ItemStack[] backpackContents = player.getOpenInventory().getTopInventory().getContents();
                            try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `backpackContents`=? WHERE `uuid`=UNHEX(?);")) {
                                statement.setString(1, backpackContents == null ? null : GTMUtils.toBase64(backpackContents));
                                statement.setString(2, player.getUniqueId().toString().replaceAll("-", ""));
                                statement.execute();
                            }

                            player.closeInventory();
                        }
                    }
                    
                    if (user.isLocked()){
                        user.onSave(connection);
                        MutexDAO.setGTMUserMutex(connection, user.getUUID(), false);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        System.out.println("[GTM] Done saving GTMUsers...");

        this.save();

        //Casino games.
        if (this.coreCasino != null)
            this.coreCasino.removeAllGames();

    }

    private void registerListeners() {
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new Death(), this);
        pm.registerEvents(new Login(), this);
        pm.registerEvents(new Leave(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new Join(getResourcePackManager()), this);
        pm.registerEvents(new Pickup(), this);
        pm.registerEvents(new WeaponUse(), this);
        pm.registerEvents(new Drop(), this);
        pm.registerEvents(new Interact(), this);
        pm.registerEvents(new Chat(), this);
        pm.registerEvents(new InventoryClick(), this);
        pm.registerEvents(new InventoryOpen(), this);
        pm.registerEvents(new BackpackManager(), this);
        pm.registerEvents(new TrashCanManager(), this);
        pm.registerEvents(new UpdateListener(), this);
        pm.registerEvents(new Move(), this);
        pm.registerEvents(new Damage(), this);
        pm.registerEvents(new ChangeWorld(), this);
        pm.registerEvents(new FoodChange(), this);
        pm.registerEvents(new VehicleUse(), this);
        pm.registerEvents(new PetListener(), this);
        pm.registerEvents(new BreakBlock(), this);
        pm.registerEvents(new WeaponShoot(), this);
        pm.registerEvents(new BarrelListener(), this);
        pm.registerEvents(new SwapHandItems(), this);
        pm.registerEvents(new PrepareItemCraft(), this);
        pm.registerEvents(new ChristmasListener(), this);
        pm.registerEvents(new MovementCheat(), this);

        if (!Core.getSettings().isSister()) {
            pm.registerEvents(new DrugListener(), this);
            pm.registerEvents(new DrugPlacementListener(), this);
        }

        pm.registerEvents(new FireListener(), this);
        pm.registerEvents(new PortalEnter(), this);
        pm.registerEvents(new VoteReward(), this);
        pm.registerEvents(new ItemBreak(), this);
        pm.registerEvents(new ArmorEquip(), this);
        pm.registerEvents(new Craft(), this);
        pm.registerEvents(new PotionSplash(), this);
        pm.registerEvents(new GamemodeChange(), this);
        pm.registerEvents(new Dispense(), this);
        pm.registerEvents(new CommandPreProcess(), this);
        pm.registerEvents(new MobSpawn(), this);
//        pm.registerEvents(new PlayerScare(), this);//todo: remove on release
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void registerCommands() {
        this.getCommand("ammo").setExecutor(new AmmoCommand());
        this.getCommand("feed").setExecutor(new FeedCommand());
        this.getCommand("lootcrate").setExecutor(new LootCrateCommand());
        this.getCommand("spawn").setExecutor(new SpawnCommand());
        this.getCommand("warp").setExecutor(new WarpCommand());
        this.getCommand("gtmadmin").setExecutor(new GTMAdminCommand());
        this.getCommand("gameitem").setExecutor(new GameItemCommand());
        this.getCommand("kit").setExecutor(new KitCommand());
        this.getCommand("shop").setExecutor(new ShopCommand());
        this.getCommand("gtmrank").setExecutor(new GTMRankCommand());
        this.getCommand("money").setExecutor(new MoneyCommand());
        this.getCommand("pay").setExecutor(new PayCommand());
        this.getCommand("rankup").setExecutor(new RankupCommand());
        this.getCommand("tpa").setExecutor(new TpaCommand());
        this.getCommand("tpahere").setExecutor(new TpaCommand());
        this.getCommand("tpaccept").setExecutor(new TpaCommand());
        this.getCommand("tpdeny").setExecutor(new TpaCommand());
        this.getCommand("permits").setExecutor(new PermitsCommand());
        this.getCommand("kill").setExecutor(new KillCommand());
        this.getCommand("suicide").setExecutor(new KillCommand());
        this.getCommand("gangadmin").setExecutor(new GangAdminCommand());
        this.getCommand("picker").setExecutor(new PickerCommand());
        this.getCommand("vehicle").setExecutor(new VehicleCommand());
        this.getCommand("backpack").setExecutor(new BackpackCommand());
        this.getCommand("bribe").setExecutor(new BribeCommand());
        this.getCommand("reset").setExecutor(new ResetCommand());
        this.getCommand("tokenshop").setExecutor(new TokenShopCommand());
        this.getCommand("antiaura").setExecutor(new AntiAuraCommand());
        this.getCommand("clear").setExecutor(new ClearCommand());
        this.getCommand("fix").setExecutor(new FixCommand());
        this.getCommand("near").setExecutor(new NearCommand());
        this.getCommand("teleport").setExecutor(new TeleportCommand());
        this.getCommand("spectator").setExecutor(new SpectatorCommand());
        this.getCommand("backup").setExecutor(new BackupCommand());
        this.getCommand("lottery").setExecutor(new LotteryCommand());
        this.getCommand("speed").setExecutor(new SpeedCommand());
        this.getCommand("chunkunload").setExecutor(new ChunkUnloadCommand());
        this.getCommand("topkillers").setExecutor(new TopKillersCommand());
        this.getCommand("stats").setExecutor(new StatsCommand());
        this.getCommand("resourcepack").setExecutor(new ResourcePackCommand());

        if (!Core.getSettings().isSister()) {
            this.getCommand("drugs").setExecutor(new DrugCommand());
            this.getCommand("drugdealer").setExecutor(new DrugDealerCommand());
            this.getCommand("drugcheck").setExecutor(new DrugCheckCommand());
        }

        this.getCommand("gtmranks").setExecutor(new GTMRanksCommand());
//        this.getCommand("stack").setExecutor(new StackCommand()); Major DUPE glitch, FIX BEFORE ENABLING!
        this.getCommand("event").setExecutor(new EventCommand(this));
        new CheatCodeCommand();
        new HalloweenCommand();
        new ChristmasCommand();
        new ChestCheckCommand();
        new CoreNPCCommand();
        new SellCommand();
        
        if(WEAPON_SKINS_FEATURE_FLAG) {
            new SkinCommand();
            new SkinsCommand();
        }
        
        new SetRarityCommand();
        new SettingsCommand();
        new TransferCommand();
    }

    private void load() {
        settings.setChristmasDropsConfig(Utils.loadConfig("christmasdrops"));
        settings.setGtmConfig(Utils.loadConfig("gtm"));
        settings.setWarpsConfig(Utils.loadConfig("warps"));
        settings.setBountiesConfig(Utils.loadConfig("bounties"));
        settings.setItemsConfig(Utils.loadConfig("items"));
        settings.setKitsConfig(Utils.loadConfig("kits"));
        settings.setLootConfig(Utils.loadConfig("loot"));
        settings.setLootCratesConfig(Utils.loadConfig("lootcrates"));
        settings.setBarrelsConfig(Utils.loadConfig("barrels"));
        settings.setLotteryConfig(Utils.loadConfig("lottery"));

        if (!Core.getSettings().isSister()) {
            settings.setDrugBlocksConfig(Utils.loadConfig("drugblocks"));
        }

        settings.setGtmShopMenuConfig(Utils.loadConfig("salemenus"));
        YamlConfiguration c = settings.getGtmConfig();
        settings.setMap(c.getString("map"));
        // can players pvp eachother
        settings.setPvp(c.getBoolean("pvp", true));
        // can players transfer data to other servers
        settings.setServerTransfer(c.getBoolean("server-transfer", false));
        // can players use cheatcodes
        settings.setGlobalCheatcodes(c.getBoolean("cheatcodes", false));
        // can players use /pay
        settings.setPayCommand(c.getBoolean("pay", true));
        // can players transfer bank money to other players bank
        settings.setBankToBankTransfer(c.getBoolean("bank-to-bank-transfer", true));
        // can players buy items at shops
        settings.setBuy(c.getBoolean("buy", true));
        // can players trade eachother
        settings.setTrade(c.getBoolean("player-trade", true));
        // can players use the bounty system
        settings.setBountySystem(c.getBoolean("bounty-system", true));
        // server take money when new bounties are placed
        settings.setBountyTax(c.getBoolean("bounty-system-tax", true));
        // base percent of money taken from new bounties placed
        settings.setBountyTaxPercent(c.getDouble("bounty-tax-percent", 20.0));
        // can players use kits
        settings.setKitSystem(c.getBoolean("kit-system", true));
        // server take money from players that the players drop on death
        settings.setServerDeathTax(c.getBoolean("server-death-tax", true));
        // does the tax scale with GTMRank
        settings.setServerDeathTaxScaled(c.getBoolean("server-death-tax-scaled", true));
        // the base percent of money taken from the dropped money
        settings.setServerDeathBasePercent(c.getDouble("server-death-tax-percent", 20.0));
        // the min cash that must be taken by the server
        settings.setServerDeathTaxMin(c.getInt("server-death-tax-min", 1000));
        // the max cash that must be taken by the server
        settings.setServerDeathTaxMax(c.getInt("server-death-tax-max", 50000));
        
       // this.setupTables(); todo: throwing errors with new core DAO set up, lukas / stephen might need a look.
        this.loadMenus();
        this.loadSettings();
        settings.setOneElevenRespack(RES_PACKS[1][0]);
        settings.setOneElevenHash(RES_PACKS[1][1]);
        settings.setOneTenRespack(RES_PACKS[0][0]);
        settings.setOneTenHash(RES_PACKS[0][1]);
    }

    public void reload() {
        this.load();
        wm.loadWarps();
        bm.loadBounties();
        im.loadItems();
        im.loadKits();
        cm.loadCrates();
        lottery.loadConfig();
    }

    public void save() {
        wm.saveWarps();
        bm.saveBounties();
        cm.saveCrates();
        im.saveItems();
        im.saveKits();
        wbm.unloadBarrels();
        lottery.saveConfig();

        if (!Core.getSettings().isSister()) {
            drugManager.stop();
        }

        FireListener.clearFire();
    }

    public void setupTables() {

        ServerUtil.runTaskAsync(() -> {
            BaseDatabase.runCustomQuery("CREATE TABLE IF NOT EXISTS " + Core.name() + "(" +
                    "`uuid` varchar(40) NOT NULL," +
                    "`name` varchar(17) NOT NULL," +
                    "`rank` varchar(255) DEFAULT 'HOBO'," +
                    "`kills` int(11) default 0," +
                    "`deaths` int(11) default 0," +
                    "`money` double default 0," +
                    "`bank` double default 0," +
                    "`killCounter` int(11) default 0," +
                    "`killStreak` int(11) default 0," +
                    "`permits` int(11) default 0," +
                    "`jobMode` varchar(255) default NULL," +
                    "`lastJobMode` bigint(20) default 0," +
                    "`backpackContents` longtext," +
                    "`kitExpiries` varchar(255)," +
                    "`houses` varchar(255)," +
                    "`gang` varchar(255)," +
                    "`gangRank` varchar(255) NOT NULL DEFAULT 'member'," +
                    "`jailTimer` int(11) DEFAULT -1," +
                    "`jailCop` varchar(255) default NULL," +
                    "`jailCopName` varchar(255) default NULL," +
                    "`personalVehicle` varchar(255)," +
                    "`cheatcodes` BLOB," +
                    "PRIMARY KEY (`uuid`));");

            BaseDatabase.runCustomQuery("CREATE TABLE IF NOT EXISTS " + Core.name() + "_gangs (" +
                    "`name` varchar(255) NOT NULL," +
                    "`leader` varchar(255) NOT NULL," +
                    "`leaderName` varchar(255) NOT NULL," +
                    "`description` varchar(255) NOT NULL DEFAULT 'Your default gang description'," +
                    "`maxMembers` int(11) NOT NULL," +
                    "PRIMARY KEY (`name`));");

            BaseDatabase.runCustomQuery("CREATE TABLE IF NOT EXISTS " + Core.name() + "_gangs_relations (" +
                    "`gang1` varchar(255) NOT NULL," +
                    "`gang2` varchar(255) NOT NULL," +
                    "`relation` varchar(255) NOT NULL);");

            BaseDatabase.runCustomQuery("CREATE TABLE IF NOT EXISTS " + Core.name() + "_heads (" +
                    "`sellerUUID` varchar(40) NOT NULL," +
                    "`sellerName` varchar(17) NOT NULL," +
                    "`head` varchar(255) NOT NULL," +
                    "`expiry` bigint(20)," +
                    "`done` boolean default '0'," +
                    "`paid` boolean default '0'," +
                    "`gaveHead` boolean default '0'," +
                    "`bidderUUID` varchar(40)," +
                    "`bidderName` varchar(17)," +
                    "`bid` double default '0');");

//            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
//                try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + Core.name() + " LIMIT 1")) {
//                    try (ResultSet result = statement.executeQuery()) {
//                        ResultSetMetaData resultMetaData = result.getMetaData();
//                        List<String> columns = Lists.newArrayList();
//                        for (int i = 1; i <= resultMetaData.getColumnCount(); i++) {
//                            columns.add(resultMetaData.getCatalogName(i).toLowerCase());
//                            System.out.println(resultMetaData.getCatalogName(i).toLowerCase());
//                        }
//
//                        if (!columns.contains("playtime"))
//                            BaseDatabase.runCustomQuery("ALTER TABLE " + Core.name() + " ADD COLUMN playtime BIGINT(20) NOT NULL DEFAULT 0;");
//
//                        for (AmmoType type : AmmoType.values())
//                            if (!type.isInInventory() && !columns.contains(type.toString().toLowerCase()))
//                                BaseDatabase.runCustomQuery("ALTER TABLE " + Core.name() + " ADD COLUMN " + type.toString().toLowerCase() + " INT(11) DEFAULT 0;");
//
//                        if (GTM.getWastedVehicles() != null && GTM.getWastedVehicles().getBabies() != null && GTM.getWastedVehicles().getBabies().getVehicleProperties() != null) {
//                            for (VehicleProperties vehicle : GTM.getWastedVehicles().getBabies().getVehicleProperties()) {
//                                if (!columns.contains(vehicle.getIdentifier().toLowerCase()))
//                                    BaseDatabase.runCustomQuery("ALTER TABLE " + Core.name() + " ADD COLUMN " + vehicle.getIdentifier().toLowerCase() + " BOOLEAN NOT NULL DEFAULT 0;");
//
//                                if (!columns.contains(vehicle.getIdentifier().toLowerCase() + ":info"))
//                                    BaseDatabase.runCustomQuery("ALTER TABLE " + Core.name() + " ADD COLUMN `" + vehicle.getIdentifier().toLowerCase() + ":info` VARCHAR(255);");
//                            }
//                        }
//                    }
//                }
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
        });

//        Core.sql.updateAsyncLater("create table if not exists " + Core.name()
//                + "(uuid varchar(40) NOT NULL, name varchar(17) NOT NULL, rank varchar(255) DEFAULT 'HOBO', kills int(11) default 0, deaths int(11) default 0," +
//                " money double default 0, bank double default 0, killCounter int(11) default 0, killStreak int(11) default 0, permits int(11) default 0, jobMode varchar(255) default NULL, lastJobMode bigint(20) default 0," +
//                " backpackContents longtext, kitExpiries varchar(255), houses varchar(255), gang varchar(255), gangRank varchar(255) NOT NULL DEFAULT 'member'," +
//                " jailTimer int(11) DEFAULT -1, jailCop varchar(255) default NULL, jailCopName varchar(255) default NULL, personalVehicle varchar(255), cheatcodes BLOB, PRIMARY KEY (uuid))");
//        Core.sql.updateAsyncLater("create table if not exists " + Core.name()
//                + "_gangs (name varchar(255) NOT NULL, leader varchar(255) NOT NULL, leaderName varchar(255) NOT NULL," +
//                " description varchar(255) NOT NULL DEFAULT 'Your default gang description', maxMembers int(11) NOT NULL, PRIMARY KEY (name));");
//        Core.sql.updateAsyncLater("create table if not exists " + Core.name()
//                + "_gangs_relations (gang1 varchar(255) NOT NULL, gang2 varchar(255) NOT NULL, relation varchar(255) NOT NULL);");
//        Core.sql.updateAsyncLater("create table if not exists " + Core.name() + "_heads (sellerUUID varchar(40) NOT NULL, sellerName varchar(17) NOT NULL, head varchar(255) NOT NULL, expiry bigint(20), done boolean default '0', paid boolean default '0', gaveHead boolean default '0', bidderUUID varchar(40), bidderName varchar(17), bid double default '0');");

//        new BukkitRunnable() {
//            @Override
//            public void run() {
//                try {
//                    ResultSet rs = Core.sql.query("select * from " + Core.name() + " LIMIT 1;");
//                    ResultSetMetaData metaData = rs.getMetaData();
//                    List<String> columns = new ArrayList<>();
//                    for (int i = 1; i <= metaData.getColumnCount(); i++)
//                        columns.add(metaData.getColumnName(i).toLowerCase());
//                    if (!columns.contains("playtime"))
//                        Core.sql.updateAsyncLater("ALTER TABLE " + Core.name() + " ADD COLUMN playtime BIGINT(20) NOT NULL DEFAULT 0;");
//                    for (AmmoType type : AmmoType.values())
//                        if (!type.isInInventory() && !columns.contains(type.toString().toLowerCase()))
//                            Core.sql.update("alter table " + Core.name() + " add column " + type.toString().toLowerCase() + " int(11) default 0;");
//
//                    if(GTM.getWastedVehicles()!=null && GTM.getWastedVehicles().getBabies()!=null && GTM.getWastedVehicles().getBabies().getVehicleProperties()!=null) {
//                        for (VehicleProperties vehicle : GTM.getWastedVehicles().getBabies().getVehicleProperties()) {
//                            if (!columns.contains(vehicle.getIdentifier().toLowerCase()))
//                                Core.sql.update("alter table " + Core.name() + " add column " + vehicle.getIdentifier().toLowerCase() + " BOOLEAN not null default 0;");
//                            if (!columns.contains(vehicle.getIdentifier().toLowerCase() + ":info"))
//                                Core.sql.update("alter table " + Core.name() + " add column `" + vehicle.getIdentifier().toLowerCase() + ":info` VARCHAR(255);");
//                        }
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
        MenuManager.addMenu("gtmstats", 54, "&d&lStats");
        MenuManager.addMenu("prefs", 54, "&5&lPreferences");
        MenuManager.addMenu("contacts", 54, "&6&lContacts");

        MenuManager.addMenu("bounties", 54, "&5&lBounties");
        MenuManager.addMenu("bountieslist", 54, "&5&lBounties List");
        MenuManager.addMenu("bountieshelp", 54, "&5&lBounties Help");
        MenuManager.addMenu("bountiesplace", 54, "&5&lPlace Bounties");

        MenuManager.addMenu("kits", 54, "&b&lKits");

        MenuManager.addMenu("taxi", 54, "&e&lTaxi Service");
        MenuManager.addMenu("taxiplayers", 54, "&e&lTaxi Service: Players");
        MenuManager.addMenu("taxihouses", 54, "&e&lTaxi Service: &3&lHouses");
        MenuManager.addMenu("taxiotherplayers", 54, "&e&lPick up a player!");
        MenuManager.addMenu("taxiwarps", 54, "&e&lTaxi Service: Warps");

        MenuManager.addMenu("bank", 54, "&3&lBanking");
        MenuManager.addMenu("bankwithdraw", 54, "&3&lBanking: Withdraw Money");
        MenuManager.addMenu("bankdeposit", 54, "&3&lBanking: Deposit Money");
        MenuManager.addMenu("banktransfer", 54, "&3&lBanking: Transfer Money");

        MenuManager.addMenu("gps", 54, "&8&lGPS Tracker");
        MenuManager.addMenu("gpsgangs", 54, "&8&lGPS Tracker: &a&lGangs");
        MenuManager.addMenu("gpshouses", 54, "&8&lGPS Tracker: &3&lHouses");
        MenuManager.addMenu("gpscops", 54, "&8&lGPS Tracker: &b&lCops");
        MenuManager.addMenu("gpscriminals", 54, "&8&lGPS Tracker: &e&lCriminals");
        MenuManager.addMenu("gpsbounties", 54, "&8&lGPS Tracker: &5&lBounties");

        MenuManager.addMenu("mygang", 54, "&a&lMy Gang");
        MenuManager.addMenu("gang", 54, "&a&lGang");
        MenuManager.addMenu("disbandgang", 54, "&c&lDisband Gang");
        MenuManager.addMenu("leavegang", 54, "&c&lLeave Gang");
        MenuManager.addMenu("mygangmembers", 54, "&a&lMy Gang Members");
        MenuManager.addMenu("mygangrelations", 54, "&a&lMy Gang Relations");
        MenuManager.addMenu("gangmembers", 54, "&a&lGang Members");
        MenuManager.addMenu("gangmember", 54, "&a&lGang Member");
        MenuManager.addMenu("gangrelations", 54, "&a&lGang Relations");
        MenuManager.addMenu("gangs", 54, "&a&lGang List");

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

        MenuManager.addMenu("heads", 54, "&e&lHead Auction");
        MenuManager.addMenu("auctionhead", 54, "&e&lAuction Head");

        MenuManager.addMenu("armorupgrade", 54, "&b&lArmor Upgrade");
        MenuManager.addMenu("christmasshop", 54, "&cC&2h&cr&2i&cs&2t&cm&2a&cs &2S&ch&2o&cp");

        MenuManager.addMenu("transferconfirm", 54, "&c&lTransfer Confirmation");

        MenuManager.addMenu("lottery", 54, "&e&lLottery");
        MenuManager.addMenu("sellinvconfirm", 54, "&a&lConfirm Inventory Sell");

        if (!Core.getSettings().isSister()) {
            MenuManager.addMenu("drugdealer", 54, "&3&lDrug Dealer");
            MenuManager.addMenu("drugseller", 54, "&3&lDrug Seller");
        }

        MenuManager.addMenu("cheatcodes", 54, "&2&lCheat Codes");

        MenuManager.addMenu("realestateagent", 27, "&3&lDynasty 8 Real Estate");
        MenuManager.addMenu("realestate-premium", 54, "&3&lDynasty 8 &a&lPremium Houses");
        MenuManager.addMenu("realestate-nonpremium", 54, "&3&lDynasty 8 &b&lNon-Premium Houses");
    }

    private void loadSettings() {
        Settings settings = Core.getSettings();
        settings.setDefaultGameMode(GameMode.ADVENTURE);
        settings.setPetsVulnerable(true);
        settings.setServerWarperEnabled(false);
        settings.setUseEditMode(true);
        settings.setTokenShopEnabled(true);
        settings.setCanOpenChests(true);
        settings.setCanInteractInventory(true);
        settings.setLoadCosmetics(true);
        settings.setCanCraft(false);
        if (GTM.getSettings().getMap() != null) {
            World map = Bukkit.getWorld(GTM.getSettings().getMap());
            settings.setStopChunkLoad(map.getName());
            map.setPVP(true);
        }
        World spawn = Bukkit.getWorlds().get(0);
        settings.setStopChunkLoad(spawn.getName());
        settings.setStopHungerChange(spawn.getName());
        settings.setStopWeatherChange(spawn.getName());
        spawn.setPVP(false);
        spawn.setSpawnFlags(false, false);
    }

   /* public void loadShopMenus(){
        YamlConfiguration menuConfig = settings.getGtmShopMenuConfig();
        Core.log("started loading shop menus");
        for(String saleMenuName : menuConfig.getConfigurationSection("").getKeys(false)) {
            Core.log("Current salemenu is " + saleMenuName);
            String saleMenuTitle = menuConfig.getString(saleMenuName + ".menu-title");
            ShopMenu saleMenu = new ShopMenu(saleMenuName, 54, saleMenuTitle);
            for (String subCategoryName : menuConfig.getConfigurationSection(saleMenuName + ".subcategories").getKeys(false)) {
                Core.log("Current subCategory is " + subCategoryName);
                String currentPath = saleMenuName + ".subcategories." + subCategoryName;
                SubCategoryMenu subCategoryMenu = parseMenuDisplayItemString(saleMenu, null, currentPath, subCategoryName, menuConfig.getString(currentPath + ".display-item"));
                saleMenu.addCategory(subCategoryName, subCategoryMenu);

                if (!menuConfig.contains(currentPath + ".subcategories")) {
                    for (String gameItemString : menuConfig.getStringList(currentPath + ".sale-items")) {
                        GameItem gameItem = getItemManager().getItem(gameItemString);
                        ShopMenuItem saleMenuItem = new ShopMenuItem(gameItem.getItem(), gameItem.getSellPrice());
                        subCategoryMenu.addSellingItem(saleMenuItem);
                    }
                }
                else{
                    loopThroughSubCategories(saleMenu, subCategoryMenu, menuConfig, currentPath + ".subcategories");
                }
            }
            MenuManager.addMenu(saleMenu);
        }
    }

    private void loopThroughSubCategories(ShopMenu shopMenu, SubCategoryMenu categoryMenu, YamlConfiguration menuConfig, String path){//path should end in .categories
        for (String subCategoryName : menuConfig.getConfigurationSection(path).getKeys(false)) {
            Core.log("current subcategory is " + subCategoryName);
            String currentPath = path + "." + subCategoryName;
            SubCategoryMenu subCategoryMenu = parseMenuDisplayItemString(shopMenu, categoryMenu, currentPath, subCategoryName, menuConfig.getString(currentPath + ".display-item"));
            categoryMenu.addSubCategory(subCategoryName, subCategoryMenu);

            if (!menuConfig.contains(currentPath + ".subcategories")) {
                for (String gameItemString : menuConfig.getStringList(currentPath + ".sale-items")) {
                    GameItem gameItem = getItemManager().getItem(gameItemString);
                    ShopMenuItem saleMenuItem = new ShopMenuItem(gameItem.getItem(), gameItem.getSellPrice());
                    subCategoryMenu.addSellingItem(saleMenuItem);
                }
            }
            else{
                loopThroughSubCategories(shopMenu, subCategoryMenu, menuConfig, currentPath + ".subcategories");
            }
        }
    }

    private SubCategoryMenu parseMenuDisplayItemString(ShopMenu saleMenu, SubCategoryMenu previousSubCategory, String currentPath, String subCategoryName, String displayItemString){
        int materialID = 0;
        short dataID = 0;
        if(displayItemString.contains(":")){
            if(!Utils.isInteger(displayItemString.split(":")[0]) || !Utils.isInteger(displayItemString.split(":")[1])){
                Core.error("Unable to load item located in menu config at path: " + currentPath + ".display-item");
                return null;
            }
            materialID = Integer.parseInt(displayItemString.split(":")[0]);
            dataID = Short.parseShort(displayItemString.split(":")[1]);
        }
        else{
            if(!Utils.isInteger(displayItemString)){
                Core.error("Unable to load item located in menu config at path: " + currentPath + ".display-item");
                return null;
            }
            materialID = Integer.parseInt(displayItemString);
        }
        return new SubCategoryMenu(saleMenu, previousSubCategory, subCategoryName, ServerType.GTM, Material.getMaterial(materialID), dataID);
    }*/
}
