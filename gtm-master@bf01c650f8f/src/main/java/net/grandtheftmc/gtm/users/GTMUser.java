package net.grandtheftmc.gtm.users;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.currency.Purse;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.NMSUtil;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.database.dao.AmmoDAO;
import net.grandtheftmc.gtm.events.WantedLevelChangeEvent;
import net.grandtheftmc.gtm.gang.member.GangMember;
import net.grandtheftmc.gtm.gang.member.GangRole;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.items.ArmorUpgrade;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.Head;
import net.grandtheftmc.gtm.items.Kit;
import net.grandtheftmc.gtm.utils.Stats;
import net.grandtheftmc.gtm.weapon.skins.WeaponSkinDAO;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class GTMUser extends Mutexable {

	/** The uuid of the user */
	private final UUID uuid;
	/** The name of the user */
	private final String name;
	private final Map<AmmoType, Integer> ammo = new HashMap<>();
	private final List<PersonalVehicle> vehicles = new ArrayList<>();
	private double sellInvConfirmAmt = 0;
	private ChatAction currentChatAction = null;
	private Object currentChatValue = null;
	private final int[] wantedLevels = new int[] { 0, 1, 2, 4, 10, 25 };
	private GTMRank rank;
	private int kills;
	private boolean dead = false;
	private int transferID = -1;
	private int deaths;
	// private double money;
	private double bank;
	private final HashMap<CheatCode, CheatCodeState> cheatCodes = new HashMap<>();
	private int killCounter;
	private int killStreak;
	// private int permits;
	private JobMode jobMode = JobMode.CRIMINAL;
	private long lastJobMode = -1;
	private ItemStack[] backpackContents;
	private HashMap<String, Long> kitExpiries = new HashMap<>();
	private String gang;
	private GangRole gangRole;
	private Long playtime;
	private Long jointime;
	private CompassTarget compassTarget;
	private long lastCompassRefresh;
	private PersonalVehicle personalVehicle;
	private int vehicleTaskId = -1;
	private int vehicleTimer;
	private PersonalVehicle nextVehicle;
	private boolean sendAway;
	private String actionVehicle;
	private int taxiTaskId = -1;
	private TaxiTarget taxiTarget;
	private int taxiPrice;
	private int taxiTimer;
	private UUID tpaRequestUUID;
	private UUID tpaRequestSentUUID;
	private boolean tpaHere;
	private long lastTpaRequest = -1;
	private long lastTeleport = -1;
	private UUID bountyUUID;
	private String bountyName;
	private int bountyAmount = -1;
	private List<String> gangInvites = new ArrayList<>();
	private String viewingGang;
	private GangMember viewingGangMember;
	private boolean gangChat;
	private boolean addingLootCrate;
	private boolean removingLootCrate;
	private boolean checkingLootCrate;
	private boolean restockingLootCrate;
	private int jailTimer;
	private UUID jailCop;
	private String jailCopName;
	private double bribe;
	private long lastBackupRequest;
	private ArmorUpgrade buyingArmorUpgrade;
	private boolean kicked;
	private long lastCopSpawn;
	private long lastTag = -1;
	private long lastJetpackCancel;
	private long enableJetpackTime;
    private String biddingHead;
	private long biddingExpiry;
	private boolean backpackOpen;
	private Map<Short, List<Short>> unlockedWeaponSkins = new HashMap<>();
	private Map<Short, Short> equippedWeaponSkins = new HashMap<>();

	// TODO test remove for debugging
	public Map<String, Integer> methodCallCounter = new HashMap<>();

	/**
	 * Holds currencies
	 */
	private Purse purse;

	/**
	 * Construct a new GTMUser.
	 * 
	 * @param uuid - the uuid of the user
	 * @param name - the name of the user
	 */
	public GTMUser(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.purse = new Purse();

		// initialize all currencies in purse
		for (Currency curr : Currency.values()) {
			purse.registerCurrency(curr);
		}

		// initialize all ammo types
		for (AmmoType type : AmmoType.getTypes()) {
			if (!type.isInInventory()) {
				this.ammo.put(type, 0);
			}
		}

		// TODO debug remove
		// seeing how many method calls are being invoked per player
		methodCallCounter.put("saveBackpackContents", 0);
		methodCallCounter.put("saveBackpackContentsSync", 0);
		methodCallCounter.put("setRank", 0);
		methodCallCounter.put("setKills", 0);
		methodCallCounter.put("addKills", 0);
		methodCallCounter.put("setDeaths", 0);
		methodCallCounter.put("addDeaths", 0);
		// methodCallCounter.put("setMoney", 0);
		// methodCallCounter.put("addMoney", 0);
		// methodCallCounter.put("takeMoney", 0);
		methodCallCounter.put("setBank", 0);
		methodCallCounter.put("addBank", 0);
		methodCallCounter.put("takeBank", 0);
		methodCallCounter.put("depositToBank", 0);
		methodCallCounter.put("withdrawFromBank", 0);
		methodCallCounter.put("setPlaytime", 0);
		// methodCallCounter.put("setKillCounter", 0);
		// methodCallCounter.put("addKillCounter", 0);
		// methodCallCounter.put("takeKillCounter", 0);
		// methodCallCounter.put("setKillStreak", 0);
		// methodCallCounter.put("addKillStreak", 0);
		// methodCallCounter.put("takeKillStreak", 0);
		// methodCallCounter.put("addPermits", 0);
		// methodCallCounter.put("takePermits", 0);
		// methodCallCounter.put("setPermits", 0);
		// methodCallCounter.put("setJobMode", 0);
		// methodCallCounter.put("setLastJobMode", 0);
		// methodCallCounter.put("setAmmo", 0);
		// methodCallCounter.put("addAmmo", 0);
		// methodCallCounter.put("resetJail", 0);
		// methodCallCounter.put("giveVehiclePerm", 0);
		// methodCallCounter.put("removeVehiclePerm", 0);
		// methodCallCounter.put("setCheatCodeState", 0);
	}

	/**
	 * Called when we need to load data pertaining to the user.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return {@code true} if the data was loaded, {@code false} otherwise.
	 */
	public boolean onLoad(Connection conn) {

		// TODO test remove
		long start = System.currentTimeMillis();

		String query = "SELECT * FROM " + Core.name() + " WHERE uuid=UNHEX(?) LIMIT 1;";

		try (PreparedStatement statement = conn.prepareStatement(query)) {
			statement.setString(1, getUUID().toString().replaceAll("-", ""));

			try (ResultSet result = statement.executeQuery()) {
				if (result.next()) {
					this.rank = GTMRank.fromString(result.getString("rank"));
					this.kills = result.getInt("kills");
					this.deaths = result.getInt("deaths");
					this.bank = result.getDouble("bank");
					this.killCounter = result.getInt("killCounter");
					this.killStreak = result.getInt("killStreak");
					this.jobMode = JobMode.getMode(result.getString("jobMode"));
					this.lastJobMode = result.getLong("lastJobMode");
					this.backpackContents = GTMUtils.fromBase64(result.getString("backpackContents"));
					this.kitExpiries = new HashMap<>();
					this.jailTimer = result.getInt("jailTimer");
					try {
						this.jailCop = result.getString("jailCop") == null ? null : UUID.fromString(result.getString("jailCop"));
					}
					catch (Exception ignored) {
					}
					this.playtime = (long) result.getInt("playtime");
					this.jailCopName = result.getString("jailCopName");
					String s = result.getString("kitExpiries");
					this.kitExpiries = new HashMap<>();
					if (s != null) {
						try {
							String[] expiries = s.split(",");
							for (String e : expiries) {
								String[] a = e.split(":");
								String kit = a[0];
								Kit k = GTM.getItemManager().getKit(kit);
								if (kit == null || a.length < 2)
									continue;
								long expiry;
								try {
									expiry = Long.parseLong(a[1]);
								}
								catch (NumberFormatException ex) {
									continue;
								}
								if (expiry > System.currentTimeMillis())
									this.kitExpiries.put(k.getName().toLowerCase(), expiry);
							}

						}
						catch (Exception e) {
							GTM.getInstance().getLogger().log(Level.ALL, "Error while loading kitExpiries for player " + result.getString("name"));
							e.printStackTrace();
						}
					}

					for (VehicleProperties properties : GTM.getWastedVehicles().getBabies().getVehicleProperties()) {
						if (result.getBoolean(properties.getIdentifier().toLowerCase())) {
							PersonalVehicle v = new PersonalVehicle(properties.getIdentifier());
							this.vehicles.add(v);
							String st = result.getString(properties.getIdentifier().toLowerCase() + ":info");
							if (st == null)
								continue;
							String[] a = st.split(":");
							if (a == null | a.length == 0)
								continue;
							v.setHealth(Double.parseDouble(a[0]));
						}
					}

					this.loadCheatCodes(conn, result);
				}
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		// load skins
		this.unlockedWeaponSkins = WeaponSkinDAO.getUnlockedSkins(conn, this.uuid);
		this.equippedWeaponSkins = WeaponSkinDAO.getEquippedSkins(conn, this.uuid);

		for (Currency curr : Currency.values()){
        	switch(curr){
        		case MONEY:
        		case PERMIT:
        			
        			// grab currency based off serverKey
        			int amount = CurrencyDAO.getCurrency(conn, curr.getServerKey(), getUUID(), curr);
        			// set in the purse
        			getPurse().set(curr, amount);
        			
        			// TODO remove debug messages
        			Core.log("[GTMUser] uuid=" + uuid.toString() + ", curr=" + curr.getId() + ", amount=" + getPurse().getBalance(curr));
        			break;
        	}
        }
		
		// load ammo
		Map<AmmoType, Integer> ammoMap = AmmoDAO.getAllAmmo(conn, getUUID(), Core.name().toUpperCase());

		// populate local variable
		// as each k/v is init to 0
		ammoMap.forEach((k, v) -> {
			// TODO remove
			Core.log("[GTMUser][AmmoTest] uuid=" + uuid.toString() + "Type=" + k + " was found, amt=" + v);
			ammo.put(k, v);
		});

		// TODO test remove
		Log.info("[GTMUser][TEST]", "Finished onLoad() for " + getName() + " in " + (System.currentTimeMillis() - start) + " msecs.");

		return true;
	}

	/**
	 * Call when we need to save data pertaining to the user.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return {@code true} if the data was saved, {@code false} otherwise.
	 */
	public boolean onSave(Connection conn) {

		// save all the currencies
		for (Currency curr : getPurse().getCurrencies().keySet()) {
			
			// only save
			switch(curr){
				case MONEY:
				case PERMIT:
					int balance = getPurse().getBalance(curr);

					// TODO test remove
					Core.log("[GTMUser][CurrencyTest] Saving user uuid=" + getUUID().toString() + ", currency=: " + curr.getId() + ", amt=" + balance);
					CurrencyDAO.saveCurrency(conn, curr.getServerKey(), getUUID(), curr, balance);
					break;
			}
		}

		// save all the ammo in user_ammo
		AmmoDAO.saveAllAmmo(conn, getUUID(), Core.name().toUpperCase(), getAmmo());

		// TODO debug statements please remove
		StringBuilder builder = new StringBuilder();
		for (AmmoType at : ammo.keySet()) {
			builder.append(at.name().toLowerCase() + "=");
			builder.append(ammo.get(at) + ",");
		}
		Core.log("[GTMUser][AmmoTest] Saving user ammo of: " + builder.toString());

		// TODO debug statements please remove
		StringBuilder methodBuilder = new StringBuilder();
		methodCallCounter.forEach((k, v) -> {
			methodBuilder.append(k + "=" + v + ", ");
		});
		Core.log("[GTMUser][MethodCounter] User " + uuid.toString() + " call counter: " + methodBuilder.toString());

		return true;
	}

	public void dataCheck() {

		// TODO remove
		Log.info("[GTMUser][TEST]", "Core name=" + getUUID().toString().replaceAll("-", ""));

		// TODO note: inline queries like this are subject to SQL injection
		BaseDatabase.runCustomQuery("INSERT INTO " + Core.name() + "(`uuid`,`name`,`bank`) VALUES(UNHEX('" + getUUID().toString().replaceAll("-", "") + "'),'" + name + "','5000') ON DUPLICATE KEY UPDATE `name`='" + name + "';");

		// TODO this shouldn't be how we set duplicate names

		// TODO note: inline queries like this are subject to SQL injection
		BaseDatabase.runCustomQuery("UPDATE " + Core.name() + " SET `name`='ERROR' WHERE `name`='" + name + "' AND uuid != UNHEX('" + getUUID().toString().replaceAll("-", "") + "');");
	}

	/**
	 * Get the UUID of the user.
	 * 
	 * @return The UUID of the user.
	 */
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * Get the name of the user.
	 * 
	 * @return The name of the user.
	 */
	public String getName() {
		return this.name;
	}

	public HashMap<CheatCode, CheatCodeState> getCheatCodes() {
		return cheatCodes;
	}

	private void loadCheatCodes(Connection conn, ResultSet rs) throws SQLException {

		if (rs.getBlob("cheatcodes") != null) {
			Blob b = rs.getBlob("cheatcodes");
			String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
			for (String serializedCheatCode : cheatCodesBlob.split("-")) {
				String[] split = serializedCheatCode.split("#");
				try {
					this.cheatCodes.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
				}
				catch (Exception e) {
				}
			}
		}

		// we get rank this way b/c user might not be loaded in container yet
		UserRank highestRank = UserDAO.getHighestRank(conn, getUUID());

		// TOOD user might not be loaded yet
		for (CheatCode code : CheatCode.getCodes()) {
			if (highestRank == code.getMinmumRank() || highestRank.isHigherThan(code.getMinmumRank())) {
				if (!this.cheatCodes.containsKey(code) || this.cheatCodes.get(code).getState() == State.LOCKED) {
					this.cheatCodes.put(code, new CheatCodeState(code.getDefaultState(), false));
				}
			}
			else {
				if (this.cheatCodes.containsKey(code) && !this.cheatCodes.get(code).isPurchased()) {
					this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
				}
			}
		}

		for (CheatCode code : CheatCode.getCodes())
			if (!this.cheatCodes.containsKey(code))
				this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));

		if (Core.getPermsManager().hasPerm(this.uuid, "kit.drugs")) {
			this.cheatCodes.put(CheatCode.DRUGS, new CheatCodeState(CheatCode.DRUGS.getDefaultState(), true));
			Core.getPermsManager().removePerm(this.uuid, "kits.drugs");
		}
	}

	public void saveBackpackContents() {
		Bukkit.getScheduler().runTaskAsynchronously(GTM.getInstance(), () -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `backpackContents`=? WHERE `uuid`=UNHEX(?);")) {
					statement.setString(1, this.backpackContents == null ? null : GTMUtils.toBase64(this.backpackContents));
					statement.setString(2, getUUID().toString().replaceAll("-", ""));
					statement.execute();
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}

		});

		// TODO remove test counter
		methodCallCounter.put("saveBackpackContents", methodCallCounter.get("saveBackpackContents") + 1);
	}

	public void saveBackpackContentsSync(Connection connection) {
//        try (Connection connection = BaseDatabase.getInstance().getConnection()) {
		try (PreparedStatement statement = connection.prepareStatement("UPDATE " + Core.name() + " SET `backpackContents`=? WHERE `uuid`=UNHEX(?);")) {
			statement.setString(1, this.backpackContents == null ? null : GTMUtils.toBase64(this.backpackContents));
			statement.setString(2, getUUID().toString().replaceAll("-", ""));
			statement.execute();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}

		// TODO remove test counter
		methodCallCounter.put("saveBackpackContentsSync", methodCallCounter.get("saveBackpackContentsSync") + 1);
	}

	public GTMRank getRank() {
		return this.rank;
	}

	public boolean isRank(GTMRank rank) {
		if (rank == null || this.rank == null)
			return false;
		return this.rank == rank || this.rank.isHigherThan(rank);
	}

	public void setTransferID(int transferID) {
		this.transferID = transferID;
	}

	public int getTransferID() {
		return transferID;
	}

	public void setRank(GTMRank r, Player player, User u) {
		this.rank = r;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setRank(this.uuid, r));

		NametagManager.updateNametag(Bukkit.getPlayer(this.uuid));
		u.setPerms(player);

		// TODO remove test counter
		methodCallCounter.put("setRank", methodCallCounter.get("setRank") + 1);
	}

	public void rankup(Player player, User u) {
		GTMRank nextRank = this.rank.getNext();
		if (nextRank == null) {
			player.sendMessage(Utils.f(Lang.RANKUP + "&7You can't rank up any more!"));
			return;
		}
		int price = nextRank.getPrice();
		if (!this.hasMoney(price)) {
			player.sendMessage(Utils.f(Lang.RANKUP + "&7You don't have the &c$&l" + price + "&7 required to rank up!"));
			return;
		}
		this.takeMoney(price);
		this.setRank(nextRank, player, u);
		GTMUtils.updateBoard(player, u, this);
		Utils.broadcastExcept(player, Lang.RANKUP + "&7" + u.getColoredName(player) + "&7 ranked up to " + nextRank.getColoredNameBold() + "&7!");
		player.sendMessage(Lang.MONEY_TAKE.toString() + price);
		player.sendMessage(Utils.f(Lang.RANKUP + "&7You ranked up to " + nextRank.getColoredNameBold() + "&7!"));
	}

	public int getKills() {
		return this.kills;
	}

	public void setKills(int i) {
		this.kills = i;
		
		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKills(this.uuid, this.kills));

		// TODO remove test counter
		methodCallCounter.put("setKills", methodCallCounter.get("setKills") + 1);
	}

	public void addKills(int i) {
		this.kills += i;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKills(this.uuid, this.kills));

		// TODO remove test counter
		methodCallCounter.put("addKills", methodCallCounter.get("addKills") + 1);
	}

	public int getDeaths() {
		return this.deaths;
	}

	public void setDeaths(int i) {
		this.deaths = i;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setDeaths(this.uuid, this.deaths));

		// TODO remove test counter
		methodCallCounter.put("setDeaths", methodCallCounter.get("setDeaths") + 1);
	}

	public void addDeaths(int i) {
		this.deaths += i;
		
		ServerUtil.runTaskAsync(() -> GTMUserDAO.setDeaths(this.uuid, this.deaths));

		// TODO remove test counter
		methodCallCounter.put("addDeaths", methodCallCounter.get("addDeaths") + 1);
	}

	/**
	 * Get the purse that holds the currency for this player.
	 *
	 * @return The purse that holds the currency for this player.
	 */
	public Purse getPurse() {
		return purse;
	}

	public double getMoney() {
		if (getPurse().getBalance(Currency.MONEY) < 0) {
			getPurse().set(Currency.MONEY, 0);
		}

		return getPurse().getBalance(Currency.MONEY);
	}

	public void setMoney(double amount) {
		getPurse().set(Currency.MONEY, (int) amount);
		
		GTMUtils.updateBoard(Bukkit.getPlayer(this.uuid), this);
	}

	public void addMoney(double amount) {
		if (amount < 0)
			return;

		getPurse().set(Currency.MONEY, getPurse().getBalance(Currency.MONEY) + (int) amount);
	}

	public void takeMoney(double amount) {
		getPurse().set(Currency.MONEY, getPurse().getBalance(Currency.MONEY) - (int) amount);
	}

	public boolean hasMoney(double amount) {
		return getPurse().getBalance(Currency.MONEY) >= amount;
	}

	public double getBank() {
		if (this.bank < 0)
			this.setBank(0);
		return this.bank;
	}

	public void setBank(double amount) {
		double old = this.bank;
		this.bank = amount;

		ServerUtil.runTaskAsync(() -> {
			GTMUserDAO.setBank(this.uuid, this.bank);
			String name = Bukkit.getPlayer(this.uuid) == null ? "ERROR" : Bukkit.getPlayer(this.uuid).getName();
			Utils.insertLog(this.uuid, name, "BANK", "SET", old + " -> " + this.bank, amount, 0);
		});

		// TODO remove test counter
		methodCallCounter.put("setBank", methodCallCounter.get("setBank") + 1);
	}

	public void addBank(double amount) {
		double old = this.bank;
		this.bank += amount;
		
		ServerUtil.runTaskAsync(() -> {
			GTMUserDAO.setBank(this.uuid, this.bank);
			String name = Bukkit.getPlayer(this.uuid) == null ? "ERROR" : Bukkit.getPlayer(this.uuid).getName();
			Utils.insertLog(this.uuid, name, "BANK", "ADD", old + " -> " + this.bank, amount, 0);
		});

		// TODO remove test counter
		methodCallCounter.put("addBank", methodCallCounter.get("addBank") + 1);
	}

	public void takeBank(double amount) {
		double old = this.bank;
		this.bank -= amount;

		ServerUtil.runTaskAsync(() -> {
			GTMUserDAO.setBank(this.uuid, this.bank);
			String name = Bukkit.getPlayer(this.uuid) == null ? "ERROR" : Bukkit.getPlayer(this.uuid).getName();
			Utils.insertLog(this.uuid, name, "BANK", "TAKE", old + " -> " + this.bank, amount, 0);
		});

		// TODO remove test counter
		methodCallCounter.put("takeBank", methodCallCounter.get("takeBank") + 1);
	}

	public void depositToBank(double amount) {
		double old = this.bank;
		this.bank += amount;

		getPurse().set(Currency.MONEY, getPurse().getBalance(Currency.MONEY) - (int) amount);
		// TODO stephen
		ServerUtil.runTaskAsync(() -> {
			try (Connection conn = BaseDatabase.getInstance().getConnection()) {
				CurrencyDAO.saveCurrency(conn, Currency.MONEY.getServerKey(), this.uuid, Currency.MONEY, getPurse().getBalance(Currency.MONEY));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		ServerUtil.runTaskAsync(() -> {
			GTMUserDAO.setBank(this.uuid, this.bank);
			String name = Bukkit.getPlayer(this.uuid) == null ? "ERROR" : Bukkit.getPlayer(this.uuid).getName();
			Utils.insertLog(this.uuid, name, "BANK", "DEPOSIT", old + " -> " + this.bank, amount, 0);
		});

		// TODO remove test counter
		methodCallCounter.put("depositToBank", methodCallCounter.get("depositToBank") + 1);
	}

	public void withdrawFromBank(double amount) {
		double old = this.bank;
		this.bank -= amount;

		getPurse().set(Currency.MONEY, getPurse().getBalance(Currency.MONEY) + (int) amount);
		// TODO stephen
		ServerUtil.runTaskAsync(() -> {
			try (Connection conn = BaseDatabase.getInstance().getConnection()) {
				CurrencyDAO.saveCurrency(conn, Currency.MONEY.getServerKey(), this.uuid, Currency.MONEY, getPurse().getBalance(Currency.MONEY));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});

		ServerUtil.runTaskAsync(() -> {
			GTMUserDAO.setBank(this.uuid, this.bank);
			String name = Bukkit.getPlayer(this.uuid) == null ? "ERROR" : Bukkit.getPlayer(this.uuid).getName();
			Utils.insertLog(this.uuid, name, "BANK", "WITHDRAW", old + " -> " + this.bank, amount, 0);
		});

		// TODO remove test counter
		methodCallCounter.put("withdrawFromBank", methodCallCounter.get("withdrawFromBank") + 1);
	}

	public boolean hasBank(double i) {
		return this.bank >= i;
	}

	public int getWantedLevel() {
		int wantedLevel = 0;
		for (int i = 0; i < this.wantedLevels.length; i++)
			if (this.killCounter >= this.wantedLevels[i])
				wantedLevel = i;
		return wantedLevel;
	}

	public Long getPlaytime() {
		return this.playtime;
	}

	public void setPlaytime(Long playtime) {
		this.playtime = playtime;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setPlaytime(this.uuid, this.playtime));

		// TODO remove test counter
		methodCallCounter.put("setPlaytime", methodCallCounter.get("setPlaytime") + 1);
	}

	public Long getJoinTime() {
		return this.jointime;
	}

	public void setJointime(Long jointime) {
		this.jointime = jointime;
	}

	public int getKillCounter() {
		return this.killCounter;
	}

	public void setKillCounter(int i) {
		int wantedLevel = this.getWantedLevel();
		this.killCounter = i;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillCounter(this.uuid, this.killCounter));

		Player player = Bukkit.getPlayer(this.uuid);
		int wantedLevelAfter = this.getWantedLevel();
		if (wantedLevel != wantedLevelAfter)
			Bukkit.getPluginManager().callEvent(new WantedLevelChangeEvent(player, this, wantedLevelAfter));
	}

	public void addKillCounter(int i) {
		int wantedLevel = this.getWantedLevel();
		this.killCounter += i;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillCounter(this.uuid, this.killCounter));

		Player player = Bukkit.getPlayer(this.uuid);
		int wantedLevelAfter = this.getWantedLevel();
		if (wantedLevel != wantedLevelAfter)
			Bukkit.getPluginManager().callEvent(new WantedLevelChangeEvent(player, this, wantedLevelAfter));
	}

	public void takeKillCounter(int i) {
		int wantedLevel = this.getWantedLevel();
		this.killCounter -= i;

		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillCounter(this.uuid, this.killCounter));

		Player player = Bukkit.getPlayer(this.uuid);
		int wantedLevelAfter = this.getWantedLevel();
		if (wantedLevel != wantedLevelAfter)
			Bukkit.getPluginManager().callEvent(new WantedLevelChangeEvent(player, this, wantedLevelAfter));
	}

	public int getKillStreak() {
		return this.killStreak;
	}

	public void setKillStreak(int i) {
		this.killStreak = i;
		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillstreak(this.uuid, this.killStreak));
	}

	public void addKillStreak(int i) {
		this.killStreak += i;
		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillstreak(this.uuid, this.killStreak));
	}

	public void takeKillStreak(int i) {
		this.killStreak -= i;
		ServerUtil.runTaskAsync(() -> GTMUserDAO.setKillstreak(this.uuid, this.killStreak));
	}

	public void addPermits(int amount) {
		getPurse().set(Currency.PERMIT, getPurse().getBalance(Currency.PERMIT) + amount);
	}

	public void takePermits(int amount) {
		getPurse().set(Currency.PERMIT, getPurse().getBalance(Currency.PERMIT) - amount);
	}

	public int getPermits() {
		return getPurse().getBalance(Currency.PERMIT);
	}

	public void setPermits(int amount) {
		getPurse().set(Currency.PERMIT, amount);
	}

	public boolean hasPermits(int amount) {
		return getPurse().getBalance(Currency.PERMIT) >= amount;
	}

	public JobMode getJobMode() {
		return this.jobMode == null ? JobMode.CRIMINAL : this.jobMode;
	}

	public void setJobMode(JobMode jobMode) {
		if (jobMode == null || JobMode.CRIMINAL != jobMode) {
			User u = Core.getUserManager().getLoadedUser(this.uuid);
			u.setEquipedTag(null);
		}

		this.jobMode = jobMode;
		this.lastJobMode = System.currentTimeMillis();
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jobMode='" + this.jobMode + "', lastJobMode=" + this.lastJobMode + " where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set jobMode='" + this.jobMode + "', lastJobMode=" + this.lastJobMode + " where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));

		if (Bukkit.getPlayer(this.uuid) != null) {
			Player player = Bukkit.getPlayer(this.uuid);
			player.setAllowFlight(false);
			player.setFlying(false);
		}
	}

	public boolean canSwitchJobMode(UserRank rank) {
		return this.lastJobMode <= 0 || this.lastJobMode + (GTMUtils.getJobModeDelay(rank) * 1000) < System.currentTimeMillis();
	}

	public long getTimeUntilJobModeSwitch(UserRank rank) {
		return this.lastJobMode + (GTMUtils.getJobModeDelay(rank) * 1000L) - System.currentTimeMillis();
	}

	public long getLastJobMode() {
		return this.lastJobMode;
	}

	public void setLastJobMode(int lastJobMode) {
		this.lastJobMode = lastJobMode;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jobMode='" + this.jobMode + "', lastJobMode=" + this.lastJobMode + " where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set jobMode='" + this.jobMode + "', lastJobMode=" + this.lastJobMode + " where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public boolean isInCombat() {
		return (this.lastTag + 20000) > System.currentTimeMillis();
	}

	public Long getLastTag() {
		return this.lastTag;
	}

	public void setLastTag(long i) {
		this.lastTag = i;
	}

	public CompassTarget getCompassTarget() {
		return this.compassTarget;
	}

	public boolean hasCompassTarget() {
		return this.compassTarget != null;
	}

	public void setCompassTarget(Player player, User u, CompassTarget compassTarget) {
		this.compassTarget = compassTarget;
		this.refreshCompassTarget(player, u);
	}

	public void unsetCompassTarget(Player player, User u) {
		this.compassTarget = null;
		player.setCompassTarget(player.getLocation());
	}

	public boolean refreshCompassTarget(Player player, User u) {
		if (this.compassTarget == null)
			return false;
		if (this.getCompassTarget().getExactLocation(player).equals(player.getLocation())) {
			this.unsetCompassTarget(player, Core.getUserManager().getLoadedUser(uuid));
			player.sendMessage(Utils.f(Lang.GPS + "&7Your GPS target was unset as you are in a different worlds compared to your target."));
			return false;
		}
		this.lastCompassRefresh = System.currentTimeMillis();
		player.setCompassTarget(this.compassTarget.getApproximateLocation(player, u));
		return true;
	}

	public Long getLastCompassRefresh() {
		return this.lastCompassRefresh;
	}

	public void setLastCompassRefresh(Long lastCompassRefresh) {
		this.lastCompassRefresh = lastCompassRefresh;
	}

	public boolean isUsingTaxi() {
		return this.taxiTarget != null;
	}

	public TaxiTarget getTaxiTarget() {
		return this.taxiTarget;
	}

	public void setTaxiTarget(TaxiTarget t) {
		this.taxiTarget = t;
	}

	public void unsetTaxiTarget() {
		this.taxiTarget = null;
		this.taxiTaskId = -1;
		this.taxiPrice = 0;
		this.taxiTimer = 0;
	}

	public int getTaxiPrice() {
		return this.taxiPrice;
	}

	public void setTaxiPrice(int i) {
		this.taxiPrice = i;
	}

	public int getTaxiTimer() {
		return this.taxiTimer;
	}

	public void setTaxiTimer(int i) {
		this.taxiTimer = i;
	}

	public UUID getTpaRequestUUID() {
		return this.tpaRequestUUID;
	}

	public void setTpaRequestUUID(UUID tpaRequestUUID) {
		this.tpaRequestUUID = tpaRequestUUID;
		this.lastTpaRequest = tpaRequestUUID == null ? -1 : System.currentTimeMillis();
	}

	public UUID getTpaRequestSentUUID() {
		return this.tpaRequestSentUUID;
	}

	public void setTpaRequestSentUUID(UUID uuid) {
		this.tpaRequestSentUUID = uuid;
	}

	public Long getLastTpaRequest() {
		return this.lastTpaRequest;
	}

	public void setLastTpaRequest(Long lastTpaRequest) {
		this.lastTpaRequest = lastTpaRequest;
	}

	public boolean hasTpaRequest() {
		return !(this.lastTpaRequest > 0 && this.lastTpaRequest + 60000 < System.currentTimeMillis());
	}

	public boolean isTpaHere() {
		return this.tpaHere;
	}

	public void setTpaHere(boolean b) {
		this.tpaHere = b;
	}

	public void unsetTpaRequests() {
		this.lastTpaRequest = -1L;
		this.tpaRequestSentUUID = null;
		this.tpaRequestUUID = null;
		this.tpaHere = false;
	}

	public UUID getBountyUUID() {
		return this.bountyUUID;
	}

	public void setBountyUUID(UUID bountyUUID) {
		this.bountyUUID = bountyUUID;
	}

	public String getBountyName() {
		return this.bountyName;
	}

	public void setBountyName(String bountyName) {
		this.bountyName = bountyName;
	}

	public ItemStack[] getBackpackContents() {
		return this.backpackContents;
	}

	public void setBackpackContents(ItemStack[] backpackContents) {
		this.backpackContents = backpackContents;
		this.saveBackpackContents();
	}

	public boolean canUseKit(String name) {
		return !(this.kitExpiries.containsKey(name.toLowerCase()) && this.kitExpiries.get(name.toLowerCase()) > System.currentTimeMillis());
	}

	public long getKitExpiry(String name) {
		return this.kitExpiries.containsKey(name.toLowerCase()) ? this.kitExpiries.get(name.toLowerCase()) : -1;
	}

	public void setKitExpiry(String name, int delay) { // delay is IN SECONDS
		this.kitExpiries.put(name.toLowerCase(), System.currentTimeMillis() + (delay * 1000));
		this.updateExpiries();
	}

	public Map<String, Long> getKitExpiries() {
		return this.kitExpiries;
	}

	public String getKitExpiriesString() {
		String expiries = "";
		for (Map.Entry<String, Long> entry : this.kitExpiries.entrySet()) {
			Long l = entry.getValue();
			if (l > System.currentTimeMillis())
				expiries += entry.getKey() + ':' + l + ',';
		}
		return expiries.endsWith(",") ? expiries.substring(0, expiries.length() - 1) : expiries;
	}

	public void updateExpiries() {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set kitExpiries='" + this.getKitExpiriesString() + "' where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set kitExpiries='" + this.getKitExpiriesString() + "' where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public long getLastCopSpawn() {
		return this.lastCopSpawn;
	}

	public void setLastCopSpawn(long lastCopSpawn) {
		this.lastCopSpawn = lastCopSpawn;
	}

	public double getKDR() {
		return Utils.round(this.deaths == 0 ? this.kills : this.kills / (double) this.deaths);
	}

	public boolean isAddingLootCrate() {
		return this.addingLootCrate;
	}

	public void setAddingLootCrate(boolean addingLootCrate) {
		this.addingLootCrate = addingLootCrate;
	}

	public boolean isRemovingLootCrate() {
		return this.removingLootCrate;
	}

	public void setRemovingLootCrate(boolean removingLootCrate) {
		this.removingLootCrate = removingLootCrate;
	}

	public boolean isCheckingLootCrate() {
		return this.checkingLootCrate;
	}

	public void setCheckingLootCrate(boolean checkingLootCrate) {
		this.checkingLootCrate = checkingLootCrate;
	}

	public boolean isRestockingLootCrate() {
		return this.restockingLootCrate;
	}

	public void setRestockingLootCrate(boolean restockingLootCrate) {
		this.restockingLootCrate = restockingLootCrate;
	}

	public Map<AmmoType, Integer> getAmmo() {
		return this.ammo;
	}

	public int getAmmoInInventory(Player player, AmmoType type) {
		GameItem gameItem = type.getGameItem();
		if (gameItem == null || player == null)
			return 0;
		return Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.isSimilar(gameItem.getItem())).mapToInt(ItemStack::getAmount).sum();
	}

	public int getAmmo(AmmoType type) {
		if (type.isInInventory())
			return this.getAmmoInInventory(Bukkit.getPlayer(this.uuid), type);
		else
			return this.ammo.containsKey(type) ? this.ammo.get(type) : 0;
	}

	public void setAmmoInInventory(Player player, AmmoType type, int amount) {
		int ammo = this.getAmmoInInventory(Bukkit.getPlayer(this.uuid), type);
		if (ammo > amount)
			this.removeAmmoFromInventory(player, type, ammo - amount);
		else if (ammo < amount)
			this.addAmmoToInventory(player, type, amount - ammo);
		this.updateAmmo();
	}

	public void setAmmo(AmmoType type, int amount) {
		if (type.isInInventory()) {
			this.setAmmoInInventory(Bukkit.getPlayer(this.uuid), type, amount);
			return;
		}
		this.ammo.put(type, amount);

//        Core.sql.updateAsyncLater("update " + Core.name() + " set " + type.name() + '=' + i + " where uuid='" + this.uuid + "';");
//		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set " + type.name() + '=' + amount + " where uuid='" + this.uuid + "';"));

		this.updateAmmo();
	}

	public void addAmmoToInventory(Player player, AmmoType type, int amount) {
		GameItem gameItem = type.getGameItem();
		if (gameItem == null)
			return;
		ItemStack item = gameItem.getItem();
		item.setAmount(amount);
		Utils.giveItems(player, item);
		this.updateAmmo();
	}

    public void addAmmo(AmmoType type, int amount) {
        if (type.isInInventory()) {
            this.addAmmoToInventory(Bukkit.getPlayer(this.uuid), type, amount);
            return;
        }
        this.ammo.put(type, this.ammo.get(type) + amount);

//        Core.sql.updateAsyncLater("update " + Core.name() + " set " + type.name() + '=' + type.name() + '+' + i
//                + " where uuid='" + this.uuid + "';");
//		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set " + type.name() + '=' + type.name() + '+' + amount + " where uuid='" + this.uuid + "';"));

        this.updateAmmo();
    }

	public void removeAmmoFromInventory(Player player, AmmoType type, int toRemove) {
		GameItem gameItem = type.getGameItem();
		if (gameItem == null)
			return;
		Utils.takeItems(player, toRemove, gameItem.getItem());
		this.updateAmmo();
	}

	public void removeAmmo(AmmoType type, int toRemove) {
		if (type.isInInventory()) {
			this.removeAmmoFromInventory(Bukkit.getPlayer(this.uuid), type, toRemove);
			return;
		}

		this.ammo.put(type, this.ammo.get(type) >= toRemove ? this.ammo.get(type) - toRemove : 0);

		// NOTE: THIS IS CALLED EVERY RELOAD WHICH IS BAD!
//        Core.sql.updateAsyncLater("update " + Core.name() + " set " + type.name() + '=' + type.name() + '-' + i
//                + " where uuid='" + this.uuid + "';");
		// ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " +
		// Core.name() + " set " + type.name() + '=' + type.name() + '-' + i + "
		// where uuid='" + this.uuid + "';"));

		this.updateAmmo();
	}

	public boolean hasAmmoInInventory(Player player, AmmoType type, int amount) {
		return this.getAmmoInInventory(player, type) >= amount;
	}

	public boolean hasAmmo(AmmoType type, int amount) {
		if (type.isInInventory())
			return this.hasAmmoInInventory(Bukkit.getPlayer(this.uuid), type, amount);
		return this.ammo.get(type) >= amount;
	}

	public void updateAmmo() {
		Bukkit.getPluginManager().callEvent(new AmmoUpdateEvent(Bukkit.getPlayer(this.uuid)));
	}

	public void updateAmmoLater() {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getPluginManager().callEvent(new AmmoUpdateEvent(Bukkit.getPlayer(GTMUser.this.uuid)));
			}
		}.runTaskLater(GTM.getInstance(), 1);

	}

	public boolean isKicked() {
		return this.kicked;
	}

	public void setKicked(boolean kicked) {
		this.kicked = kicked;
	}

	public void jail(int jailTimer, Player cop) {
		this.jailTimer = jailTimer;
		this.jailCop = cop.getUniqueId();
		this.jailCopName = cop.getName();

//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jailTimer=" + this.jailTimer + ", jailCop='" + this.jailCop + "', jailCopName='" + this.jailCopName + "' where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set jailTimer=" + this.jailTimer + ", jailCop='" + this.jailCop + "', jailCopName='" + this.jailCopName + "' where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public boolean isArrested() {
		return this.jailTimer >= 0;
	}

	public int getJailTimer() {
		return this.jailTimer;
	}

	public void setJailTimer(int jailTimer) {
		this.jailTimer = jailTimer;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jailTimer=" + jailTimer + " where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set jailTimer=" + jailTimer + " where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public UUID getJailCop() {
		return this.jailCop;
	}

	public void resetJail() {
		this.jailTimer = -1;
		this.jailCop = null;
		this.jailCopName = null;
		this.bribe = 0;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jailTimer=-1, jailCop=NULL, jailCopName=NULL where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set jailTimer=-1, jailCop=NULL, jailCopName=NULL where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public String getJailCopName() {
		return this.jailCopName;
	}

	public void updateTintHealth(Player player, User user) {
		if (!user.getPref(Pref.TINT_HEALTH) || player.getHealth() >= player.getMaxHealth()) {
//            new JLibPlayer(player).setWorldborderTint(0);
			NMSUtil.setWorldBoarderTint(player, 0);
			return;
		}
		UUID uuid = player.getUniqueId();
		new BukkitRunnable() {
			@Override
			public void run() {
				Player player = Bukkit.getPlayer(uuid);
				if (player != null) {
					NMSUtil.setWorldBoarderTint(player, (int) ((player.getHealth() * 100) / player.getMaxHealth()));
//                    new JLibPlayer(player).setWorldborderTint(100 - (int) (player.getHealth() * 100 / player.getMaxHealth()));
				}
			}
		}.runTaskLater(GTM.getInstance(), 1);
	}

	public PersonalVehicle getPersonalVehicle() {
		return this.personalVehicle;
	}

	public void setPersonalVehicle(PersonalVehicle personalVehicle) {
		this.personalVehicle = personalVehicle;
	}

	public boolean hasPersonalVehicle() {
		return this.personalVehicle != null;
	}

	public List<PersonalVehicle> getVehicles() {
		return this.vehicles;
	}

	public boolean isDead() {
		return dead;
	}

	public void setDead(boolean dead) {
		this.dead = dead;
	}

	public List<VehicleProperties> getVehicleProperties() {
		return this.vehicles.stream().filter(v -> v.getVehicleProperties() != null).map(PersonalVehicle::getVehicleProperties).collect(Collectors.toList());
	}

	public boolean hasVehicle(String vehicle) {
		return this.vehicles.stream().anyMatch(v -> v.getVehicle().equalsIgnoreCase(vehicle));
	}

	public void giveVehiclePerm(Player player, VehicleProperties vehicle) {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=true where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=true where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));

		if (!this.hasVehicle(vehicle.getIdentifier()))
			this.vehicles.add(new PersonalVehicle(vehicle.getIdentifier()));
	}

	public void removeVehiclePerm(Player player, VehicleProperties vehicle) {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=false where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=false where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));

		if (this.personalVehicle != null && this.personalVehicle.getVehicle().equalsIgnoreCase(vehicle.getIdentifier()))
			this.personalVehicle = null;
		new ArrayList<>(this.vehicles).stream().filter(v -> v.getVehicle().equalsIgnoreCase(vehicle.getIdentifier())).forEach(this.vehicles::remove);
	}

	public PersonalVehicle getPersonalVehicle(String s) {
		if (this.personalVehicle != null && this.personalVehicle.getVehicle().equalsIgnoreCase(s))
			return this.personalVehicle;
		return this.vehicles.stream().filter(v -> v.getVehicle().equalsIgnoreCase(s)).findFirst().orElse(null);
	}

	public void setPersonalVehicle(Player player, User user, PersonalVehicle vehicle) {
		if (this.personalVehicle != null && this.personalVehicle.onMap()) {
			this.nextVehicle = vehicle;
			this.personalVehicle.teleport(player, user, this, true);
			return;
		}
		if (!this.vehicles.contains(vehicle)) {
			player.sendMessage(Lang.VEHICLES.f("&7You do not own that vehicle!"));
			return;
		}
		this.personalVehicle = vehicle;
		this.nextVehicle = null;
		player.sendMessage(Lang.VEHICLES.f("&7You set " + vehicle.getDisplayName() + "&7 as your personal vehicle!"));
		this.personalVehicle.call(player, user, this);
	}

	public boolean cancelVehicleTeleport() {
		if (this.vehicleTaskId == -1)
			return false;
		Bukkit.getScheduler().cancelTask(this.vehicleTaskId);
		this.nextVehicle = null;
		this.sendAway = false;
		this.vehicleTimer = -1;
		return true;
	}

	public int getVehicleTaskId() {
		return this.vehicleTaskId;
	}

	public void setVehicleTaskId(int vehicleTaskId) {
		this.vehicleTaskId = vehicleTaskId;
	}

	public int getVehicleTimer() {
		return this.vehicleTimer;
	}

	public void setVehicleTimer(int vehicleTimer) {
		this.vehicleTimer = vehicleTimer;
	}

	public PersonalVehicle getNextVehicle() {
		return this.nextVehicle;
	}

	public void setNextVehicle(PersonalVehicle nextVehicle) {
		this.nextVehicle = nextVehicle;
	}

	public boolean isSendAway() {
		return this.sendAway;
	}

	public void setSendAway(boolean sendAway) {
		this.sendAway = sendAway;
	}

	public String getActionVehicle() {
		return this.actionVehicle;
	}

	public void setActionVehicle(String actionVehicle) {
		this.actionVehicle = actionVehicle;
	}

	public long getLastTeleport() {
		return this.lastTeleport;
	}

	public void setLastTeleport(long lastTeleport) {
		this.lastTeleport = lastTeleport;
	}

	public void setLastTeleport() {
		this.lastTeleport = System.currentTimeMillis();
	}

	public boolean hasTeleportProtection() {
		return this.lastTeleport + 10000 > System.currentTimeMillis();
	}

	public long getTimeUntilTeleportProtectionExpires() {
		return this.lastTeleport + 10000 - System.currentTimeMillis();
	}

	public ChatAction getCurrentChatAction() {
		return this.currentChatAction;
	}

	public long getBiddingExpiry() {
		return this.biddingExpiry;
	}

	public void setBiddingExpiry(long biddingExpiry) {
		this.biddingExpiry = biddingExpiry;
	}

	public Head getBiddingHead() {
		Head head = GTM.getShopManager().getHead(this.biddingHead, this.biddingExpiry);
		if (head == null) {
			this.biddingHead = null;
			this.biddingExpiry = -1;
		}
		return head;
	}

	public CheatCodeState getCheatCodeState(CheatCode code) {
		if (this.cheatCodes.containsKey(code))
			return this.cheatCodes.get(code);
		this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
		return new CheatCodeState(State.LOCKED, false);
	}

	public void setBribe(double bribe) {
		this.bribe = bribe;
	}

	public void setBountyAmount(int bountyAmount) {
		this.bountyAmount = bountyAmount;
	}

	public int getBountyAmount() {
		return bountyAmount;
	}

	public double getBribe() {
		return this.bribe;
	}

	/**
	 * @param code the enum cheatcode that you are setting
	 * @param state the state of the cheatcode (which can be gotten from new
	 *            CheatCodeState)
	 * @apiNote this method immediately updates ALL the cheatcodes to the MySQL
	 *          server.
	 */
	public void setCheatCodeState(CheatCode code, CheatCodeState state) {
		this.cheatCodes.put(code, state);
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set cheatcodes='" + CheatCode.seralizeCheatCodes(this.cheatCodes) + "' where uuid='" + this.uuid + "';");
		ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set cheatcodes='" + CheatCode.seralizeCheatCodes(this.cheatCodes) + "' where uuid=UNHEX('" + getUUID().toString().replaceAll("-", "") + "');"));
	}

	public void setBiddingHead(String biddingHead) {
		this.biddingHead = biddingHead;
	}

	public long getLastJetpackCancel() {
		return this.lastJetpackCancel;
	}

	public void setLastJetpackCancel(long lastJetpackCancel) {
		this.lastJetpackCancel = lastJetpackCancel;
	}

    public long getEnableJetpackTime() {
        return enableJetpackTime;
    }

    public void setEnableJetpackTime(long enableJetpackTime) {
        this.enableJetpackTime = enableJetpackTime;
    }

    public boolean canUseJetpack(){
        return System.currentTimeMillis()>this.enableJetpackTime;
    }

    public void disableJetpack(){
        if(System.currentTimeMillis() + 10000 > this.enableJetpackTime)this.enableJetpackTime=System.currentTimeMillis() + 10000;
    }

	public ArmorUpgrade getBuyingArmorUpgrade() {
		return this.buyingArmorUpgrade;
	}

	public void setBuyingArmorUpgrade(ArmorUpgrade buyingArmorUpgrade) {
		this.buyingArmorUpgrade = buyingArmorUpgrade;
	}

	public boolean getBackpackOpen() {
		return this.backpackOpen;
	}

	public void setBackpackOpen(boolean backpackOpen) {
		this.backpackOpen = backpackOpen;
	}

	public boolean hasRequestedBackup() {
		Player player = Bukkit.getPlayer(uuid);
		return this.lastBackupRequest + 60000 > System.currentTimeMillis();
	}

	public long getTimeUntilBackupRequestExpires() {
		return 60000 + this.lastBackupRequest - System.currentTimeMillis();
	}

	public void checkBackupExpiration(Player player) {
		if (this.lastBackupRequest > 0) {
			if (this.lastBackupRequest + 60000 < System.currentTimeMillis()) {
				this.lastBackupRequest = -1;
				player.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.s()).append("Your backup request has expired! Click to extend it: ").color(net.md_5.bungee.api.ChatColor.GRAY).append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup")).create());
			}

		}
	}

	public void setLastBackupRequest(long l) {
		this.lastBackupRequest = l;
	}

	public void checkAchievements() {
		User user = Core.getUserManager().getLoadedUser(getUUID());
		Player player = Bukkit.getPlayer(this.uuid);
		if (!user.getUnlockedAchievements().contains(Achievement.Hobo))
			user.addAchievement(Achievement.Hobo);
		if (!user.getUnlockedAchievements().contains(Achievement.CRIMINAL) && isRank(GTMRank.CRIMINAL))
			user.addAchievement(Achievement.CRIMINAL);
		if (!user.getUnlockedAchievements().contains(Achievement.HOMIE) && isRank(GTMRank.HOMIE))
			user.addAchievement(Achievement.HOMIE);
		if (!user.getUnlockedAchievements().contains(Achievement.THUG) && isRank(GTMRank.THUG))
			user.addAchievement(Achievement.THUG);
		if (!user.getUnlockedAchievements().contains(Achievement.GANGSTER) && isRank(GTMRank.GANGSTER))
			user.addAchievement(Achievement.GANGSTER);
		if (!user.getUnlockedAchievements().contains(Achievement.MUGGER) && isRank(GTMRank.MUGGER))
			user.addAchievement(Achievement.MUGGER);
		if (!user.getUnlockedAchievements().contains(Achievement.HUNTER) && isRank(GTMRank.HUNTER))
			user.addAchievement(Achievement.HUNTER);
		if (!user.getUnlockedAchievements().contains(Achievement.DEALER) && isRank(GTMRank.DEALER))
			user.addAchievement(Achievement.DEALER);
		if (!user.getUnlockedAchievements().contains(Achievement.PIMP) && isRank(GTMRank.PIMP))
			user.addAchievement(Achievement.PIMP);
		if (!user.getUnlockedAchievements().contains(Achievement.MOBSTER) && isRank(GTMRank.MOBSTER))
			user.addAchievement(Achievement.MOBSTER);
		if (!user.getUnlockedAchievements().contains(Achievement.GODFATHER) && isRank(GTMRank.GODFATHER))
			user.addAchievement(Achievement.GODFATHER);
		if (!user.getUnlockedAchievements().contains(Achievement.Psychopath) && getKills() >= 10000)
			user.addAchievement(Achievement.Psychopath);
		if (!user.getUnlockedAchievements().contains(Achievement.GTM_God) && Stats.getInstance().getHoursPlayedRaw(Bukkit.getPlayer(user.getUUID())) > 1000)
			user.addAchievement(Achievement.GTM_God);
		user.updateNameTag(Bukkit.getPlayer(user.getUUID()));
		if (!user.getUnlockedAchievements().contains(Achievement.Witness)) {
			Bukkit.getOnlinePlayers().forEach(target -> {
				if (target.getUniqueId().toString().equals("0e4a6028-3d9a-4a2e-9797-eb1ddcb0aca9")) {
					user.addAchievement(Achievement.Witness);
				}
			});
		}
	}

	private int chatActionExpireRunnable = -1;

	/**
	 * @param action the ChatAction that the next thing the player says will be
	 *            registered with.
	 * @param value if there is a 'second-level' the 'first-level' value should
	 *            be inserted here. For exmaple if a player chose an amount to
	 *            pay a player, then was asked to say the name of the player
	 *            they were sending the money to, 'first-level' would be the
	 *            amount, which would then run setCurrentChatAction(action,
	 *            amount) where the amount is the amount that was chosen by the
	 *            player. Then this amount would be passed to the 'second-level'
	 *            where the player is paid.
	 */
	public void setCurrentChatAction(ChatAction action, Object value) {
		Player player = Bukkit.getPlayer(this.uuid);
		if (this.chatActionExpireRunnable != -1)
			Bukkit.getScheduler().cancelTask(chatActionExpireRunnable);

		this.currentChatValue = value;
		this.currentChatAction = action;
		this.chatActionExpireRunnable = new BukkitRunnable() {
			@Override
			public void run() {
				if (currentChatAction != null) {
					if (player.isOnline())
						player.sendMessage(Lang.GTM.f("&7Your chat response for &a" + (action.toString().toLowerCase().replace("_", " ") + "&7 has &4expired&7.")));
					clearCurrentChatAction();
				}
			}
		}.runTaskLater(GTM.getInstance(), 20 * 20).getTaskId();
	}

	/**
	 * @param value the value to reset the timer with. Using example from above
	 *            if you were exiting out of the second-level because the string
	 *            inputted wasn't a player, you would do
	 *            resetCurrentChatActionTimer(amount) where amount is from the
	 *            first-level so that value isn't erased.
	 * @apiNote this method basically gives the player another 20 seconds to
	 *          answer the prompt before it expires.
	 */
	public void resetCurrentChatActionTimer(Object value) {
		if (this.chatActionExpireRunnable != -1) {
			Bukkit.getScheduler().cancelTask(chatActionExpireRunnable);
			this.currentChatValue = value;
		}
	}

	/**
	 * @apiNote clears and resets the chat action
	 */
	public void clearCurrentChatAction() {
		switch (this.currentChatAction) {
			case BIDDING_HEAD:
				biddingExpiry = -1;
				biddingHead = null;
		}
		currentChatAction = null;
		currentChatValue = null;
		if (chatActionExpireRunnable != -1)
			Bukkit.getScheduler().cancelTask(chatActionExpireRunnable);
	}

	public Object getCurrentChatValue() {
		return this.currentChatValue;
	}

	public double getSellInvConfirmAmt() {
		return sellInvConfirmAmt;
	}

	public void setSellInvConfirmAmt(double sellInvConfirmAmt) {
		this.sellInvConfirmAmt = sellInvConfirmAmt;
	}

	/*
	 * public void figureOutSpawn() { Player player = Bukkit.getPlayer(uuid);
	 * String s = (String)
	 * Core.getUserManager().getLoadedUser(uuid).getPref(Pref.SPAWN_LOCATION);
	 * if(!Core.getUserManager().getLoadedUser(uuid).isRank(UserRank.PREMIUM) &&
	 * !s.equals("spawn")){
	 * Core.getUserManager().getLoadedUser(uuid).setPref(player,
	 * Pref.SPAWN_LOCATION, "spawn");
	 * player.teleport(GTM.getWarpManager().getSpawn().getLocation()); return; }
	 * if (!s.equals("spawn")) { if (s.contains("warp")) { String name =
	 * s.split(":")[1]; if (GTM.getWarpManager().getWarp(name) == null) {
	 * player.sendMessage(Lang.PREFS.f(
	 * "&7Tried to teleport you to the warp with name: " + name +
	 * " but couldn't find the warp.")); } else
	 * player.teleport(GTM.getWarpManager().getWarp(name).getLocation()); } else
	 * if (Utils.isInteger(s.split(":")[1])) { int id =
	 * Integer.parseInt(s.split(":")[1]); if (s.contains("houseId")) { HouseUser
	 * houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
	 * if (!houseUser.ownsHouse(id)) { player.sendMessage(Lang.PREFS.f(
	 * "&7Tried to teleport you to your house with id " + id +
	 * " but it seems you do not own it."));
	 * player.teleport(GTM.getWarpManager().getSpawn().getLocation()); } else {
	 * houseUser.teleportInOrOutHouse(player,
	 * Houses.getHousesManager().getHouse(id)); } } else if
	 * (s.contains("premiumHouseId")) { HouseUser houseUser =
	 * Houses.getUserManager().getLoadedUser(player.getUniqueId()); if
	 * (houseUser.getPremiumHouse(id) == null) {
	 * player.sendMessage(Lang.PREFS.f(
	 * "&7Tried to teleport you to your premium house with id " + id +
	 * " but it seems you do not own it."));
	 * player.teleport(GTM.getWarpManager().getSpawn().getLocation()); } else {
	 * houseUser.teleportInOrOutPremiumHouse(player,
	 * houseUser.getPremiumHouse(id)); } } } } else {
	 * player.teleport(GTM.getWarpManager().getSpawn().getLocation()); } }
	 */

	public void lockWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
		if (this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()) != null) {
			this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()).remove((Object) (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));

			ServerUtil.runTaskAsync(() -> {
				try (Connection connection = BaseDatabase.getInstance().getConnection()) {
					WeaponSkinDAO.lockSkin(connection, this.uuid, weapon, skin);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void unlockWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
		if (this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()) == null) {
			this.unlockedWeaponSkins.put(weapon.getUniqueIdentifier(), new ArrayList<Short>());
		}

		if (!this.hasSkinUnlocked(weapon, skin)) {
			this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()).add((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));

			ServerUtil.runTaskAsync(() -> {
				try (Connection connection = BaseDatabase.getInstance().getConnection()) {
					WeaponSkinDAO.unlockSkin(connection, this.uuid, weapon, skin);
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			});
		}
	}

	public void equipWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
		short currentSkin = this.equippedWeaponSkins.containsKey(weapon.getUniqueIdentifier()) ? this.equippedWeaponSkins.get(weapon.getUniqueIdentifier()) : 0;

		ServerUtil.runTaskAsync(() -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				if (currentSkin != weapon.getWeaponIdentifier()) {
					WeaponSkinDAO.disableSkin(connection, this.uuid, weapon, currentSkin);
				}

				if (skin.getIdentifier() != weapon.getWeaponIdentifier()) {
					WeaponSkinDAO.enableSkin(connection, this.uuid, weapon, (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
				}
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		});

		this.equippedWeaponSkins.put(weapon.getUniqueIdentifier(), (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
	}

	public boolean hasSkinUnlocked(Weapon<?> weapon, WeaponSkin skin) {
		if (this.getUnlockedWeaponSkins(weapon) != null) {
			for (short skinID : this.getUnlockedWeaponSkins(weapon)) {
				if (skinID == (short) (skin.getIdentifier() - weapon.getWeaponIdentifier())) {
					return true;
				}
			}
		}

		return false;
	}

	public List<Short> getUnlockedWeaponSkins(Weapon<?> weapon) {
		return this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier());
	}

	public Map<Short, List<WeaponSkin>> getUnlockedWeaponSkins() {
		Map<Short, List<WeaponSkin>> skins = new HashMap<Short, List<WeaponSkin>>();

		for (short weaponID : this.unlockedWeaponSkins.keySet()) {
			Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);

			if (weaponOpt.isPresent()) {
				Weapon<?> weapon = weaponOpt.get();
				List<WeaponSkin> skinSubArray = new ArrayList<WeaponSkin>();

				for (short skinID : this.unlockedWeaponSkins.get(weaponID)) {
					skinSubArray.add(GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(weapon, skinID));
				}

				skins.put(weaponID, skinSubArray);
			}
		}

		return skins;
	}

	public Map<Short, List<Short>> getRawUnlockedWeaponSkins() {
		return this.unlockedWeaponSkins;
	}

	public WeaponSkin getEquippedWeaponSkin(Weapon<?> weapon) {
		if (equippedWeaponSkins != null) {
			if (equippedWeaponSkins.containsKey(weapon.getUniqueIdentifier())) {
				short weaponUnique = equippedWeaponSkins.get(weapon.getUniqueIdentifier());

				return GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(weapon, weaponUnique);
			}
		}

		if (weapon.getWeaponSkins() != null) {
			return weapon.getWeaponSkins()[0];
		}

		return null;
	}

	public Map<Short, WeaponSkin> getEquippedWeaponSkins() {
		Map<Short, WeaponSkin> skins = new HashMap<Short, WeaponSkin>();

		for (short weaponID : this.equippedWeaponSkins.values()) {
			Optional<Weapon<?>> weaponOpt = GTM.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);

			if (weaponOpt.isPresent()) {
				skins.put(weaponID, GTM.getWeaponSkinManager().getWeaponSkinFromIdentifier(weaponOpt.get(), this.equippedWeaponSkins.get(weaponID)));
			}
		}

		return skins;
	}
}