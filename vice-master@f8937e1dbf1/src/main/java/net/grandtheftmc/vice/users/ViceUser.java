package net.grandtheftmc.vice.users;

import com.j0ach1mmall3.jlib.player.JLibPlayer;
import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.items.ArmorUpgrade;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.items.Kit;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import net.grandtheftmc.vice.users.storage.IntStorageType;
import net.grandtheftmc.vice.users.storage.LongStorageType;
import net.grandtheftmc.vice.weapon.skins.WeaponSkinDAO;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ViceUser {

    protected final UUID uuid;
    protected final HashMap<AmmoType, Integer> ammo = new HashMap<>();
    private final HashMap<String, Location> homes = new HashMap<>();
    protected final List<PersonalVehicle> vehicles = new ArrayList<>();
    private final HashMap<BooleanStorageType, Boolean> booleanStorage = new HashMap<>();
    private final HashMap<LongStorageType, Long> longStorage = new HashMap<>();
    private final HashMap<IntStorageType, Integer> intStorage = new HashMap<>();
    protected final HashMap<CheatCode, CheatCodeState> cheatCodes = new HashMap<>();

    protected ViceRank rank = ViceRank.JUNKIE;
    protected CopRank copRank = null;
    protected long lastCopSalary = 0l;
    protected int kills = 0;
    protected int deaths = 0;
    protected double money = 0;
    protected int killStreak = 0;
    protected int bonds = 0;
    protected ItemStack[] backpackContents = null;
    protected HashMap<String, Long> kitExpiries = new HashMap<>();
    protected Long playtime = 0L;
    private Long jointime;
    private PersonalVehicle personalVehicle;
    private int vehicleTaskId = -1;
    private int vehicleTimer;
    private PersonalVehicle nextVehicle;
    private String actionVehicle;
    private int taxiTaskId = -1;
    private TaxiTarget taxiTarget;
    private int taxiPrice;
    private int taxiTimer;
    private UUID tpaRequestUUID;
    private UUID tpaRequestSentUUID;
    private long lastTpaRequest = -1;
    private long lastTeleport = -1;
    private double bankTransferring = -1;
    protected int jailTimer;
    protected UUID jailCop;
    protected String jailCopName;
    private double bribe;
    private long lastBackupRequest;
    private ArmorUpgrade buyingArmorUpgrade;
    private long lastCopSpawn;
    private long lastTag = -1;
    private long lastJetpackCancel;
    private long lastRTP;
    private Villager changingJob;
    protected Map<Short, List<Short>> unlockedWeaponSkins = new HashMap<>();
    protected Map<Short, Short> equippedWeaponSkins = new HashMap<>();
    
    public ViceUser(UUID uuid) {
        this.uuid = uuid;
        this.load();
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public void dataCheck(String name, UserRank rank) {
//        MySQL sql = Core.sql;
//        sql.update("insert into " + Core.name() + "(uuid,name,money) values('" + this.uuid + "','" + name
//                + "','5000') on duplicate key update name='" + name + "';");
//        sql.update("update " + Core.name() + " set name='ERROR' where name='" + name + "' and uuid!='" + this.uuid + "';");

        ViceUserDAO.insertViceUser(this, name);
        ViceUserDAO.insertViceUser(this, name);
    }

    public boolean updateDataFromDb() {
//        MySQL sql = Core.sql;
        boolean b = ViceUserDAO.getGeneralViceUser(this);

//        try (ResultSet rs = sql.query("select * from " + Core.name() + " where uuid='" + this.uuid + "' LIMIT 1;")) {
//            if (rs.next()) {
//                this.rank = ViceRank.fromString(rs.getString("rank"));
//                this.copRank = CopRank.getRankOrNull(rs.getString("copRank"));
//                this.lastCopSalary = rs.getLong("lastCopSalary");
//                this.kills = rs.getInt("kills");
//                this.deaths = rs.getInt("deaths");
//                this.money = rs.getDouble("money");
//                this.killStreak = rs.getInt("killStreak");
//                this.bonds = rs.getInt("bonds");
//                this.backpackContents = ViceUtils.fromBase64(rs.getString("backpackContents"));
//                this.kitExpiries = new HashMap<>();
//                this.jailTimer = rs.getInt("jailTimer");
//                try {
//                    this.jailCop = rs.getString("jailCop") == null ? null : UUID.fromString(rs.getString("jailCop"));
//                } catch (Exception ignored) {
//                }
//                this.playtime = rs.getLong("playtime");
//                this.jailCopName = rs.getString("jailCopName");
//                String s = rs.getString("kitExpiries");
//                this.kitExpiries = new HashMap<>();
//                if (s != null)
//                    try {
//                        String[] expiries = s.split(",");
//                        for (String e : expiries) {
//                            String[] a = e.split(":");
//                            String kit = a[0];
//                            Kit k = Vice.getItemManager().getKit(kit);
//                            if (kit == null || a.length < 2)
//                                continue;
//                            long expiry;
//                            try {
//                                expiry = Long.parseLong(a[1]);
//                            } catch (NumberFormatException ex) {
//                                continue;
//                            }
//                            if (expiry > System.currentTimeMillis()) {
//                                this.kitExpiries.put(k.getName().toLowerCase(), expiry);
//                            }
//                        }
//
//                    } catch (Exception e) {
//                        Vice.getInstance().getLogger().log(Level.ALL, "Error while loading kitExpiries for player " + rs.getString("name"));
//                        e.printStackTrace();
//                    }
//                for (AmmoType type : AmmoType.getTypes())
//                    if (!type.isInInventory())
//                        this.ammo.put(type, rs.getInt(type.name().toLowerCase()));
//                for (VehicleProperties properties : Vice.getWastedVehicles().getBabies().getVehicleProperties()) {
//                    if (rs.getBoolean(properties.getIdentifier().toLowerCase())) {
//                        PersonalVehicle v = new PersonalVehicle(properties.getIdentifier());
//                        this.vehicles.add(v);
//                        String st = rs.getString(properties.getIdentifier().toLowerCase() + ":info");
//                        if (st == null) continue;
//                        String[] a = st.split(":");
//                        if (a == null | a.length == 0) continue;
//                        v.setHealth(Double.parseDouble(a[0]));
//                    }
//                }
//                this.loadCheatCodes(rs);
//            } else
//                b = false;
//            rs.close();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            b = false;
//        }
        this.setBooleanToStorage(BooleanStorageType.HAS_UPDATED, b);
        return b;
    }

    public void save(){
        this.saveHomes();
       // this.saveCheatCodes();
    }

    public HashMap<CheatCode, CheatCodeState> getCheatCodes() {
        return cheatCodes;
    }

    public void load(){
        this.loadHomes();
    }

//    private void loadCheatCodes(ResultSet rs) throws SQLException {
//
//        if(rs.getBlob("cheatcodes")!=null) {
//            Blob b = rs.getBlob("cheatcodes");
//            String cheatCodesBlob = new String(b.getBytes(1, (int) b.length()));
//            for (String serializedCheatCode : cheatCodesBlob.split("-")) {
//                String[] split = serializedCheatCode.split("#");
//                this.cheatCodes.put(CheatCode.valueOf(split[0]), new CheatCodeState(State.valueOf(split[1]), Boolean.valueOf(split[2])));
//            }
//        }
//
//        User user = Core.getUserManager().getLoadedUser(this.uuid);
//        for(CheatCode code : CheatCode.values()) {
//            if (user.getUserRank() == code.getMinmumRank() || user.getUserRank().isHigherThan(code.getMinmumRank())) {
//                if (!this.cheatCodes.containsKey(code) || this.cheatCodes.get(code).getState() == State.LOCKED) {
//                    this.cheatCodes.put(code, new CheatCodeState(code.getDefaultState(), false));
//                }
//            }
//            else {
//                if(this.cheatCodes.containsKey(code) && !this.cheatCodes.get(code).isPurchased()) {
//                    this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
//                }
//            }
//        }
//        for(CheatCode code : CheatCode.values())
//            if(!this.cheatCodes.containsKey(code))
//                this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
//    }

    private void saveHomes(){
        YamlConfiguration config = Vice.getSettings().getHomesConfig();
        config.set(this.uuid.toString(), null);
        for(Map.Entry<String, Location> e : this.homes.entrySet())
            config.set(this.uuid + "." + e.getKey(), Utils.blockLocationToString(e.getValue()));
        Utils.saveConfig(config, "homes");
    }

    private void loadHomes(){
        YamlConfiguration config = Vice.getSettings().getHomesConfig();
        if(!config.contains(this.uuid.toString()))
            return;
        for(String id : config.getConfigurationSection(this.uuid.toString()).getKeys(false)){
            this.homes.put(id, Utils.blockLocationFromString(config.getString(this.uuid + "." + id)));
        }
    }

    private void saveBackpackContents() {
//        Bukkit.getScheduler().runTaskAsynchronously(Vice.getInstance(), () -> {
//            try (PreparedStatement stmt = Core.sql.prepareStatement("update " + Core.name() + " set backpackContents=? where uuid=?;")) {
//                stmt.setString(1, this.backpackContents == null ? null : ViceUtils.toBase64(this.backpackContents));
//                stmt.setString(2, this.uuid.toString());
//                stmt.execute();
//                stmt.close();
//            } catch (SQLException e) {
//                e.printStackTrace();
//            }
//
//        });
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setBackpackContents(this.uuid, this.backpackContents));
    }

    public ViceRank getRank() {
        return this.rank;
    }

    public CopRank getCopRank() {
        return this.copRank;
    }

    public boolean isCop() {
        return this.copRank != null;
    }

    public boolean isRank(ViceRank rank) {
        return !(rank == null || this.rank == null) && (this.rank == rank || this.rank.isHigherThan(rank));
    }

    public boolean isCopRank(CopRank copRank) {
        return !(copRank == null || this.copRank == null) && (this.copRank == copRank || this.copRank.isHigherThan(copRank));
    }

    public void setRank(ViceRank r, Player player, User u) {
        this.rank = r;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set rank='" + r.getName() + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setRank(this.uuid, r));
        NametagManager.updateNametag(Bukkit.getPlayer(this.uuid));
        u.setPerms(player);
    }

    public void setCopRank(CopRank r, Player player, User u) {
        this.copRank = r;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set copRank='" + (r == null ? null : r.getName()) + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setCopRank(this, r));
        NametagManager.updateNametag(Bukkit.getPlayer(this.uuid));
        u.setPerms(player);
    }

    public long getLastCopSalary() {
        return this.lastCopSalary;
    }

    public void setLastCopSalary(long lastCopSalary) {
        this.lastCopSalary = lastCopSalary;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set lastCopSalary=" + this.lastCopSalary + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setLastCopSalary(this, lastCopSalary));
    }

    public void rankup(Player player, User u) {
        ViceRank nextRank = this.rank.getNext();
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
        ViceUtils.updateBoard(player, u, this);
        Utils.broadcastExcept(player, Lang.RANKUP + "&7" + u.getColoredName(player) + "&7 ranked up to "
                + nextRank.getColoredNameBold() + "&7!");
        player.sendMessage(Lang.MONEY_TAKE.toString() + price);
        player.sendMessage(Utils.f(Lang.RANKUP + "&7You ranked up to " + nextRank.getColoredNameBold() + "&7!"));
    }

    public int getKills() {
        return this.kills;
    }

    public void setKills(int i) {
        this.kills = i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set kills=" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setKills(this, i));
    }

    public void addKills(int i) {
        this.kills += i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set kills=kills+" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setKills(this, this.kills));
    }

    public int getDeaths() {
        return this.deaths;
    }

    public void setDeaths(int i) {
        this.deaths = i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set deaths=" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setDeaths(this, i));
    }

    public void addDeaths(int i) {
        this.deaths += i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set deaths=deaths+" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setDeaths(this, this.deaths));
    }

    public double getMoney() {
        return this.money;
    }

    public void setMoney(double i) {
        this.money = i;
        if(Bukkit.getPlayer(this.uuid)!=null)
            ViceUtils.updateBoard(Bukkit.getPlayer(this.uuid), this);
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set money=" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setMoney(this.uuid, i));
    }

    public void addMoney(double i) {
        this.money += i;
        if(Bukkit.getPlayer(this.uuid)!=null)
            ViceUtils.updateBoard(Bukkit.getPlayer(this.uuid), this);
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set money=money+" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setMoney(this.uuid, this.money));
    }

    public void takeMoney(double i) {
        this.money -= i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set money=money-" + i + " where uuid='" + this.uuid + "';");
        if(Bukkit.getPlayer(this.uuid)!=null)
            ViceUtils.updateBoard(Bukkit.getPlayer(this.uuid), this);
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setMoney(this.uuid, this.money));
    }

    public boolean hasMoney(double i) {
        return this.money >= i;
    }

    public Long getPlaytime() {//
        return this.playtime;
    }

    public void setPlaytime(Long playtime) {
        this.playtime = playtime;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set playtime=" + this.playtime + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setPlaytime(this, playtime));
    }

    public Long getJoinTime() {
        return this.jointime;
    }

    public void setJointime(Long jointime) {
        this.jointime = jointime;
    }

    public int getKillStreak() {
        return this.killStreak;
    }

    public void setKillStreak(int i) {
        this.killStreak = i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set killStreak=" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setKillStreak(this, i));
    }

    public void addKillStreak(int i) {
        this.killStreak += i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set killStreak=killStreak+" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setKillStreak(this, this.killStreak));
    }

    public void takeKillStreak(int i) {
        this.killStreak -= i;
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set killStreak=killStreak-" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setKillStreak(this, this.killStreak));
    }

    public void giveBonds(int i) {
        this.bonds += i;
//        Core.sql.update(
//                "update " + Core.name() + " set bonds=bonds+" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setBonds(this.uuid, this.bonds));
    }

    public void takeBonds(int i) {
        this.bonds -= i;
//        Core.sql.update(
//                "update " + Core.name() + " set bonds=bonds-" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setBonds(this.uuid, this.bonds));
    }

    public int getBonds() {
        return this.bonds;
    }

    public void setBonds(int i) {
        this.bonds = i;
//        Core.sql.update(
//                "update " + Core.name() + " set bonds=" + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setBonds(this.uuid, this.bonds));
    }

    public boolean hasbonds(int i) {
        return this.bonds >= i;
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

    public int getTaxiTaskId() {
        return this.taxiTaskId;
    }

    public void setTaxiTaskId(int i) {
        this.taxiTaskId = i;
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


    public void unsetTpaRequests() {
        this.lastTpaRequest = -1L;
        this.tpaRequestSentUUID = null;
        this.tpaRequestUUID = null;
        setBooleanToStorage(BooleanStorageType.TPA_HERE, false);
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
        StringBuilder expiries = new StringBuilder();
        for (Map.Entry<String, Long> entry : this.kitExpiries.entrySet()) {
            Long l = entry.getValue();
            if (l > System.currentTimeMillis())
                expiries.append(entry.getKey()).append(':').append(l).append(',');
        }
        return expiries.toString().endsWith(",") ? expiries.substring(0, expiries.length() - 1) : expiries.toString();
    }

    public void updateExpiries() {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set kitExpiries='" + this.getKitExpiriesString() + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.updateKitExpiries(this.uuid, this.getKitExpiriesString()));
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

    public HashMap<AmmoType, Integer> getAmmo() {
        return this.ammo;
    }

    public int getAmmoInInventory(Player player, AmmoType type) {
        GameItem gameItem = type.getGameItem();
        if (gameItem == null) return 0;
        if (gameItem.getItem() == null) return 0;

        if (player == null) return 0;
        if (player.getInventory() == null) return 0;
        if (player.getInventory().getContents() == null || player.getInventory().getContents().length == 0) return 0;

        return Arrays.stream(player.getInventory().getContents()).filter(item -> item != null && item.isSimilar(gameItem.getItem())).mapToInt(ItemStack::getAmount).sum();
    }

    public int getAmmo(AmmoType type) {
        return type.isInInventory() ? this.getAmmoInInventory(Bukkit.getPlayer(this.uuid), type) : this.ammo.getOrDefault(type, 0);
    }

    public void setAmmoInInventory(Player player, AmmoType type, int i) {
        int ammo = this.getAmmoInInventory(Bukkit.getPlayer(this.uuid), type);
        if (ammo > i)
            this.removeAmmoFromInventory(player, type, ammo - i);
        else if (ammo < i)
            this.addAmmoToInventory(player, type, i - ammo);
        this.updateAmmo();
    }

    public void setAmmo(AmmoType type, int i) {
        if (type.isInInventory()) {
            this.setAmmoInInventory(Bukkit.getPlayer(this.uuid), type, i);
            return;
        }
        this.ammo.put(type, i);
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set " + type.name() + '=' + i + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setAmmo(this, type.name(), i));
        this.updateAmmo();
    }

    public void addAmmoToInventory(Player player, AmmoType type, int i) {
        GameItem gameItem = type.getGameItem();
        if (gameItem == null)
            return;
        ItemStack item = gameItem.getItem();
        item.setAmount(i);
        Utils.giveItems(player, item);
        this.updateAmmo();
    }

    public void addAmmo(AmmoType type, int i) {
        if (type.isInInventory()) {
            this.addAmmoToInventory(Bukkit.getPlayer(this.uuid), type, i);
            return;
        }
        this.ammo.put(type, this.ammo.get(type) + i);
//        Core.sql.updateAsyncLater("update " + Core.name() + " set " + type.name() + '=' + type.name() + '+' + i
//                + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setAmmo(this, type.name(), this.ammo.get(type)));
        this.updateAmmo();
    }

    public void removeAmmoFromInventory(Player player, AmmoType type, int i) {
        GameItem gameItem = type.getGameItem();
        if (gameItem == null)
            return;
        Utils.takeItems(player, i, gameItem.getItem());
        this.updateAmmo();
    }

    public void removeAmmo(AmmoType type, int i) {
        if (type.isInInventory()) {
            this.removeAmmoFromInventory(Bukkit.getPlayer(this.uuid), type, i);
            return;
        }
        this.ammo.put(type, this.ammo.get(type) >= i ? this.ammo.get(type) - i : 0);
//        Core.sql.updateAsyncLater("update " + Core.name() + " set " + type.name() + '=' + type.name() + '-' + i
//                + " where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setAmmo(this, type.name(), this.ammo.get(type)));
        this.updateAmmo();
    }

    public boolean hasAmmoInInventory(Player player, AmmoType type, int i) {
        return this.getAmmoInInventory(player, type) >= i;
    }

    public boolean hasAmmo(AmmoType type, int i) {
        if (type.isInInventory())
            return this.hasAmmoInInventory(Bukkit.getPlayer(this.uuid), type, i);
        return this.ammo.get(type) >= i;
    }

    public void updateAmmo() {
        Bukkit.getPluginManager().callEvent(new AmmoUpdateEvent(Bukkit.getPlayer(this.uuid)));
    }

    public void updateAmmoLater() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Bukkit.getPluginManager().callEvent(new AmmoUpdateEvent(Bukkit.getPlayer(ViceUser.this.uuid)));
            }
        }.runTaskLater(Vice.getInstance(), 1);

    }

    public void jail(int jailTimer, Player cop) {
        this.jailTimer = jailTimer;
        this.jailCop = cop.getUniqueId();
        this.jailCopName = cop.getName();
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set jailTimer=" + this.jailTimer + ", jailCop='" + this.jailCop + "', jailCopName='" + this.jailCopName + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setjailed(this, this.jailTimer, this.jailCop, this.jailCopName));
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
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setJailTimer(this, jailTimer));
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
        ServerUtil.runTaskAsync(() -> ViceUserDAO.resetJail(this.uuid));
    }

    public String getJailCopName() {
        return this.jailCopName;
    }

    public double getBribe() {
        return this.bribe;
    }

    public void setBribe(double bribe) {
        this.bribe = bribe;
    }


    public double getBankTransferring() {
        return this.bankTransferring;
    }

    public boolean isBankTransferring() {
        return this.bankTransferring >= 0;
    }

    public void setBankTransferring(double bankTransferring) {
        this.bankTransferring = bankTransferring;
    }

    public void updateTintHealth(Player player, User user) {
        if (Utils.returnTrue()) return;
        if (!user.getPref(Pref.TINT_HEALTH)) {
            new JLibPlayer(player).setWorldborderTint(0);
            return;
        }
        UUID uuid = player.getUniqueId();
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(uuid);
                if (player != null)
                    new JLibPlayer(player).setWorldborderTint(100 - (int) (player.getHealth() * 100 / player.getMaxHealth()));
            }
        }.runTaskLater(Vice.getInstance(), 1);
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

    public List<VehicleProperties> getVehicleProperties() {
        return this.vehicles.stream().filter(v -> v.getVehicleProperties() != null).map(PersonalVehicle::getVehicleProperties).collect(Collectors.toList());
    }

    public boolean hasVehicle(String vehicle) {
        return this.vehicles.stream().anyMatch(v -> v.getVehicle().equalsIgnoreCase(vehicle));
    }

    public void giveVehiclePerm(Player player, VehicleProperties vehicle) {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=true where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setVehiclePerm(this.uuid, vehicle, true));
        if (!this.hasVehicle(vehicle.getIdentifier()))
            this.vehicles.add(new PersonalVehicle(vehicle.getIdentifier()));
    }

    public void removeVehiclePerm(Player player, VehicleProperties vehicle) {
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=false where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setVehiclePerm(this.uuid, vehicle, false));
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
        if (this.vehicleTaskId == -1) return false;
        Bukkit.getScheduler().cancelTask(this.vehicleTaskId);
        this.nextVehicle = null;
        this.setBooleanToStorage(BooleanStorageType.SEND_AWAY, false);
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
    public long getLastRTP() {
        return this.lastRTP;
    }

    public void setLastRTP(long lastRTP) {
        this.lastRTP = lastRTP;
    }

    public void setLastRTP() {
        this.lastRTP = System.currentTimeMillis();
    }

    public boolean canRTP() {
        return this.lastRTP + 60000 < System.currentTimeMillis();
    }

    public long getTimeUntilRTP() {
        return this.lastRTP + 60000 - System.currentTimeMillis();
    }

    public long getLastJetpackCancel() {
        return this.lastJetpackCancel;
    }

    public void setLastJetpackCancel(long lastJetpackCancel) {
        this.lastJetpackCancel = lastJetpackCancel;
    }

    public ArmorUpgrade getBuyingArmorUpgrade() {
        return this.buyingArmorUpgrade;
    }

    public void setBuyingArmorUpgrade(ArmorUpgrade buyingArmorUpgrade) {
        this.buyingArmorUpgrade = buyingArmorUpgrade;
    }

    public boolean hasRequestedBackup() {
        return this.copRank != null && this.lastBackupRequest + 60000 > System.currentTimeMillis();
    }

    public long getTimeUntilBackupRequestExpires() {
        return 60000 + this.lastBackupRequest - System.currentTimeMillis();
    }

    public void checkBackupExpiration(Player player) {
        if (this.lastBackupRequest > 0) {
            if (this.copRank == null) {
                this.lastBackupRequest = -1;
                return;
            }
            if (this.lastBackupRequest + 60000 < System.currentTimeMillis()) {
                this.lastBackupRequest = -1;
                player.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.s()).append("Your backup request has expired! Click to extend it: ").color(net.md_5.bungee.api.ChatColor.GRAY).
                        append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup")).create());
            }

        }
    }

    public void setLastBackupRequest(long l) {
        this.lastBackupRequest = l;
    }

    public Location getHomeLocation(String id){
        id = id.toLowerCase();
        return this.homes.getOrDefault(id, null);
    }

    public void setHomeLocation(String id, Location loc){
        id = id.toLowerCase();
        this.homes.put(id, loc);
    }

    public boolean removeHomeLocation(String id){
        id = id.toLowerCase();
        if(this.homes.containsKey(id)){
            this.homes.remove(id);
            return true;
        }
        return false;
    }

    public HashMap<String, Location> getHomes() {
        return homes;
    }

    public boolean getBooleanFromStorage(BooleanStorageType type) {
        return this.booleanStorage.getOrDefault(type, type.getDefaultValue());
    }

    public CheatCodeState getCheatCodeState(CheatCode code){
        if(this.cheatCodes.containsKey(code))
            return this.cheatCodes.get(code);
        this.cheatCodes.put(code, new CheatCodeState(State.LOCKED, false));
        return new CheatCodeState(State.LOCKED, false);
    }

    public void setCheatCodeState(CheatCode code, CheatCodeState state){
        this.cheatCodes.put(code, state);
//        Core.sql.updateAsyncLater(
//                "update " + Core.name() + " set cheatcodes='" + CheatCode.seralizeCheatCodes(this.cheatCodes) + "' where uuid='" + this.uuid + "';");
        ServerUtil.runTaskAsync(() -> ViceUserDAO.setCheatCodeState(this));
    }


    public void setBooleanToStorage(BooleanStorageType type, boolean value) {
        booleanStorage.put(type, value);
    }

    public void setChangingJob(Villager changingJob) {
        this.changingJob = changingJob;
    }

    public Villager getChangingJob() {
        return changingJob;
    }

    public void checkAchievements() {
        // TODO Colt
        /*
        User user = Core.getUserManager().getLoadedUser(getUUID());
        if (!user.getUnlockedAchievements().contains(Achievement.Hobo)) user.addAchievement(Achievement.Hobo);
        if (!user.getUnlockedAchievements().contains(Achievement.CRIMINAL) &&
                isRank(ViceRank.CRIMINAL)) user.addAchievement(Achievement.CRIMINAL);
        if (!user.getUnlockedAchievements().contains(Achievement.HOMIE) &&
                isRank(ViceRank.HOMIE)) user.addAchievement(Achievement.HOMIE);
        if (!user.getUnlockedAchievements().contains(Achievement.THUG) &&
                isRank(ViceRank.THUG)) user.addAchievement(Achievement.THUG);
        if (!user.getUnlockedAchievements().contains(Achievement.GANGSTER) &&
                isRank(ViceRank.GANGSTER)) user.addAchievement(Achievement.GANGSTER);
        if (!user.getUnlockedAchievements().contains(Achievement.MUGGER) &&
                isRank(ViceRank.MUGGER)) user.addAchievement(Achievement.MUGGER);
        if (!user.getUnlockedAchievements().contains(Achievement.HUNTER) &&
                isRank(ViceRank.HUNTER)) user.addAchievement(Achievement.HUNTER);
        if (!user.getUnlockedAchievements().contains(Achievement.DEALER) &&
                isRank(ViceRank.DEALER)) user.addAchievement(Achievement.DEALER);
        if (!user.getUnlockedAchievements().contains(Achievement.PIMP) &&
                isRank(ViceRank.PIMP)) user.addAchievement(Achievement.PIMP);
        if (!user.getUnlockedAchievements().contains(Achievement.MOBSTER) &&
                isRank(ViceRank.MOBSTER)) user.addAchievement(Achievement.MOBSTER);
        if (!user.getUnlockedAchievements().contains(Achievement.GODFATHER) &&
                isRank(ViceRank.GODFATHER)) user.addAchievement(Achievement.GODFATHER);
        if (!user.getUnlockedAchievements().contains(Achievement.Psychopath) &&
                getKills() >= 10000) user.addAchievement(Achievement.Psychopath);
        if (!user.getUnlockedAchievements().contains(Achievement.GTM_God) &&
                Stats.getInstance().getHoursPlayedRaw(Bukkit.getPlayer(user.getUUID())) > 1000)
            user.addAchievement(Achievement.GTM_God);
        user.updateNameTag(Bukkit.getPlayer(user.getUUID()));
        if (!user.getUnlockedAchievements().contains(Achievement.Witness)) {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (player.getUniqueId().toString().equals("0e4a6028-3d9a-4a2e-9797-eb1ddcb0aca9")) {
                    user.addAchievement(Achievement.Witness);
                }
            });
        }*/
    }

    public void lockWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
        if (this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()) != null) {
            this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()).remove((Object) (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));

            ServerUtil.runTaskAsync(() -> {
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    WeaponSkinDAO.lockSkin(connection, this.uuid, weapon, skin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public void unlockWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
        if (this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()) == null) {
            this.unlockedWeaponSkins.put(weapon.getUniqueIdentifier(), new ArrayList<Short>());
        }

        if(!this.hasSkinUnlocked(weapon, skin)) {
            this.unlockedWeaponSkins.get(weapon.getUniqueIdentifier()).add((short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));

            ServerUtil.runTaskAsync(() -> {
                try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                    WeaponSkinDAO.unlockSkin(connection, this.uuid, weapon, skin);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
        }
    }
    
    public void equipWeaponSkin(Weapon<?> weapon, WeaponSkin skin) {
        short currentSkin = this.equippedWeaponSkins.containsKey(weapon.getUniqueIdentifier()) ? this.equippedWeaponSkins.get(weapon.getUniqueIdentifier()) : 0;
        
        ServerUtil.runTaskAsync(() -> {
            try (Connection connection = BaseDatabase.getInstance().getConnection()) {
                if(currentSkin != weapon.getWeaponIdentifier()) {
                    WeaponSkinDAO.disableSkin(connection, this.uuid, weapon, currentSkin);
                }
                
                if(skin.getIdentifier() != weapon.getWeaponIdentifier()) {
                    WeaponSkinDAO.enableSkin(connection, this.uuid, weapon, (short) (skin.getIdentifier() - weapon.getWeaponIdentifier()));
                }
            } catch (SQLException e) {
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
            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);
            
            if (weaponOpt.isPresent()) {
                Weapon<?> weapon = weaponOpt.get();
                List<WeaponSkin> skinSubArray = new ArrayList<WeaponSkin>();
                
                for (short skinID : this.unlockedWeaponSkins.get(weaponID)) {
                    skinSubArray.add(Vice.getWeaponSkinManager().getWeaponSkinFromIdentifier(weapon, skinID));
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
        return this.equippedWeaponSkins.containsKey(weapon.getUniqueIdentifier()) ? Vice.getWeaponSkinManager().getWeaponSkinFromIdentifier(weapon, this.equippedWeaponSkins.get(weapon.getUniqueIdentifier())) : weapon.getWeaponSkins()[0];
    }

    public Map<Short, WeaponSkin> getEquippedWeaponSkins() {
        Map<Short, WeaponSkin> skins = new HashMap<Short, WeaponSkin>();

        for (short weaponID : this.equippedWeaponSkins.values()) {
            Optional<Weapon<?>> weaponOpt = Vice.getWastedGuns().getWeaponManager().getWeaponFromUniqueIdentifier(weaponID);

            if (weaponOpt.isPresent()) {
                skins.put(weaponID, Vice.getWeaponSkinManager().getWeaponSkinFromIdentifier(weaponOpt.get(), this.equippedWeaponSkins.get(weaponID)));
            }
        }

        return skins;
    }
}