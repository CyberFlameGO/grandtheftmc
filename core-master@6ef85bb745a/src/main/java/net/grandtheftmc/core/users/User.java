package net.grandtheftmc.core.users;

import java.sql.Connection;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.earth2me.essentials.Essentials;
import com.j0ach1mmall3.ultimatecosmetics.api.storage.ParticleCosmeticStorage;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.currency.Purse;
import net.grandtheftmc.core.currency.component.CurrencySource;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.CurrencyDAO;
import net.grandtheftmc.core.database.dao.OldVoteDAO;
import net.grandtheftmc.core.database.dao.VoteDAO;
import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.events.DisplayNameUpdateEvent;
import net.grandtheftmc.core.events.MoneyEvent;
import net.grandtheftmc.core.events.MoneyEvent.MoneyEventType;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.nametags.Nametag;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.users.eventtag.PreTagEquipEvent;
import net.grandtheftmc.core.users.targets.TrackedTarget;
import net.grandtheftmc.core.util.CoreLocation;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.time.TimeUtil;
import net.grandtheftmc.core.voting.ShopItem;
import net.grandtheftmc.core.voting.VoteRecord;
import net.grandtheftmc.core.voting.crates.Crate;
import net.grandtheftmc.core.voting.crates.CrateReward;

public class User extends Mutexable {

	/** The uuid of the user */
    private final UUID uuid;
    /** The name of the user */
    private final String name;
    protected final EnumSet<Pref> prefs = EnumSet.noneOf(Pref.class);
    // COSMETICS
//    private final List<Cosmetic> cosmetics = new ArrayList<>();
//    private final EnumSet<CosmeticType> allCosmetics = EnumSet.noneOf(CosmeticType.class);
//    private final Map<Cosmetic, EnumSet<PetData>> petDataPerms = new EnumMap<>(Cosmetic.class);
//    private final Map<CosmeticType, String> lastCosmetics = new HashMap<>();
    private Set<CooldownPayload> cooldowns = null;
    private final List<Nametag> nametags = new ArrayList<>();
    private Set<EventTag> unlockedTags = new HashSet<>();
    protected final Set<Achievement> unlockedAchievements = new HashSet<>();

    /** The local rank of the user */
    protected UserRank ur;
    /** The trial rank for the user */
    protected UserRank trialRank;
    /** The global rank for the user */
    protected UserRank globalRank;
    private EventTag equipedTag;
    protected long trialRankExpiry;
    
    /** Holds currencies */
    private Purse purse;
    /** Vote record for the user */
    private VoteRecord voteRecord;

    protected int bucks; //Currency
    protected long dailyPlayTime;
    private long loginTime = System.currentTimeMillis();
//    private Cosmetic activatingCosmetic;
//    private Cosmetic buyingCosmetic;
    private ShopItem buyingShopItem;
    private int cosmeticVariant;
    private ParticleCosmeticStorage.Shape particleShape;
    //private EnumSet<PetData> petData = EnumSet.noneOf(PetData.class);
    private Nametag petNametag;
    private Nametag activatingNametag;
    private Nametag buyingNametag;
    // REWARDS
    /** The daily reward streak */
    protected int dailyStreak;
    protected long lastDailyReward;
    private long lastSpanked = 0L;
    protected String lastDonorReward;
    private Crate selectedCrate;
    private CrateReward confirmingCrateReward;
    private long lastPlayersToggle;
    private boolean editMode;
    private String tutorial;
    private int tutorialSlide;
    private boolean editingTutorial;
    private BossBar bossBar;
    protected Achievement shownAchievement;

    private PermissionAttachment pa;
    private final List<TrackedTarget> bossBarTargets = new ArrayList<>();
    private Scoreboard scoreboard;
    private UUID lastMessage;
    protected List<String> ignored = new ArrayList<>();
    private boolean hasMoved;

    private String language = "NONE";
    private CoreLocation location;

    private Long joinTime = 0L;
    private Long leaveTime = 0L;
    
    /** The join address they used to connect to the server */
    private String serverJoinAddress;

    private BukkitTask updateTrackerTask;

	/**
	 * Construct a new user object.
	 * 
	 * @param uuid - the uuid of the user
	 * @param name - the name of the user
	 */
    public User(UUID uuid, String name) {
        this.uuid = uuid;
        this.name = name;
        this.purse = new Purse();
        
        for (Currency curr : Currency.values()){
        	purse.registerCurrency(curr);
        }
    }
    
    /**
	 * Called when we need to load data pertaining to the user.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return {@code true} if the data was loaded, {@code false} otherwise.
	 */
	public boolean onLoad(Connection conn) {
		
		boolean result = false;

        result = UserDAO.fetchGeneralUser(this);
        if(!result) return false;
        
        for (Currency curr : Currency.values()){
        	switch(curr){
        		case TOKEN:
        		case CROWBAR:
        		case COUPON_CREDIT:
        		case VOTE_TOKEN:
        			// grab currency based off serverKey
        			int amount = CurrencyDAO.getCurrency(conn, curr.getServerKey(), getUUID(), curr);
        			// set in the purse
        			getPurse().set(curr, amount);
        			
        			// TODO remove debug messages
        			Core.log("[User] uuid=" + uuid.toString() + ", curr=" + curr.getId() + ", amount=" + getPurse().getBalance(curr));
        			break;
        	}
        }

        result = UserDAO.fetchServerStats(this);
        if(!result) return false;
		
        // load unlocked tags
        this.unlockedTags = UserDAO.fetchAndEquipServerPlayerTags(this);
        
        // load cooldowns
        this.cooldowns = CooldownDAO.loadCooldowns(this);
        
        // load their vote record
        voteRecord = VoteDAO.getUserVoteRecord(conn, getUUID());
		
		return result;
	}

	/**
	 * Call when we need to save data pertaining to the user.
	 * 
	 * @param conn - the database connection thread
	 * 
	 * @return {@code true} if the data was saved, {@code false} otherwise.
	 */
	public boolean onSave(Connection conn) {
		
		// clear preferences
		if (!this.prefs.isEmpty()) {
			this.prefs.clear();
		}

		// clear nametags
		if (!this.nametags.isEmpty()){
			this.nametags.clear();
		}
		
		// clear unlocked achievements
		if (!this.unlockedAchievements.isEmpty()){
			this.unlockedAchievements.clear();
		}
		
		// clear ignore list
		if (this.ignored != null && !this.ignored.isEmpty()){
			this.ignored.clear();
		}
		
		// save all the currencies
		for (Currency curr : getPurse().getCurrencies().keySet()) {
			
			// only save
			switch(curr){
				case TOKEN:
				case CROWBAR:
				case COUPON_CREDIT:
				case VOTE_TOKEN:
					int balance = getPurse().getBalance(curr);

					// TODO test remove
					Core.log("[User][CurrencyTest] Saving user uuid=" + getUUID().toString() + ", currency=: " + curr.getId() + ", amt=" + balance);
					CurrencyDAO.saveCurrency(conn, curr.getServerKey(), getUUID(), curr, balance);
					break;
			}
		}

		// save cooldowns
		CooldownDAO.saveCooldowns(conn, this);
		
		return true;
    }

    public void dataCheck() {

        UserDAO.insertUser(getUUID(), getName());
        UserDAO.insertVoter(getUUID(), getName());
        
        // if server is NOT Hub OR the default rank is global
        // this means likely that all ranks are global, and therefore we should create
        if (Core.getSettings().getType() != ServerType.HUB || UserRank.DEFAULT.getServerKey().equalsIgnoreCase("GLOBAL")){
        	try (Connection conn = BaseDatabase.getInstance().getConnection()){
            	
            	// create the users rank for this serverKey if they dont have one
            	UserDAO.createRank(conn, UserRank.DEFAULT.getServerKey(), uuid, UserRank.DEFAULT);
            }
            catch(Exception e){
            	e.printStackTrace();
            }
    	}

        // insert currencies if not exists
        for (Currency curr : Currency.values()){
        	
        	// do not create currencies for HUB, if its a server currency
        	if (Core.getSettings().getType() == ServerType.HUB && (!curr.getServerKey().equalsIgnoreCase("GLOBAL"))){
        		continue;
        	}
        	
        	// within because if one currency fails to create, we still wanna try others
        	try (Connection conn = BaseDatabase.getInstance().getConnection()){
        		CurrencyDAO.createCurrency(conn, curr.getServerKey(), uuid, curr);
            }
        	catch(Exception e){
            	e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the uuid of the user.
     * 
     * @return The uuid of the user.
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

    public Set<CooldownPayload> getCooldowns() {
        return this.cooldowns;
    }

    /**
     * @param target The target that the bossbar will point to, can be made using a new TrackedEntity or Tracked Target
     */
    public void addBossBarTarget(TrackedTarget target){
        this.bossBarTargets.add(target);
    }

    /**
     * @param target The target to remove from the bossbar
     */
    public void removeBossBarTarget(TrackedTarget target){
        this.bossBarTargets.add(target);
    }

    public void setEquipedTag(EventTag tag) {
        PreTagEquipEvent tagEquipEvent = new PreTagEquipEvent(Bukkit.getPlayer(this.uuid), this.equipedTag, tag);
        Bukkit.getPluginManager().callEvent(tagEquipEvent);
        if (tagEquipEvent.isCancelled()) return;

        this.equipedTag = tag;
        ServerUtil.runTaskAsync(() -> UserDAO.updatePlayerTags(getUUID(), tag));
    }

    public void giveEventTag(EventTag tag){
        if(!this.unlockedTags.contains(tag)) {
            this.unlockedTags.add(tag);
            ServerUtil.runTaskAsync(() -> UserDAO.addPlayerTag(getUUID(), tag));
        }
    }

    public EventTag getEquipedTag() {
        return this.equipedTag;
    }

    /**
     * @param loc the location of the target to remove from the bossbar
     */
    public boolean removeBossBarTarget(Location loc){
        List<TrackedTarget> clone = new ArrayList<>(this.bossBarTargets);
        Optional<TrackedTarget> optTarget = clone.stream().filter(target -> target.getLocation().equals(loc)).findFirst();
        if(optTarget.isPresent()) {
            this.bossBarTargets.remove(optTarget.get());
            return true;
        }
        return false;
    }

    public Set<EventTag> getUnlockedTags(){
        return this.unlockedTags;
    }

    private int getBossBarSlotForTarget(Player player, Location target) {
        Location clone = player.getEyeLocation().clone();
        clone.setPitch(0.0f);
        clone.setY(target.getY());
        Vector dirToDestination = target.toVector().subtract(clone.toVector());
        Vector playerDirection = clone.getDirection();
        double angle = Math.toDegrees(dirToDestination.angle(playerDirection));
        if(angle >= 90)
            return Integer.MAX_VALUE;
        Location pointA = player.getLocation();
        Location pointB = player.getLocation().clone().add(player.getLocation().clone().getDirection().setY(0).normalize().multiply(100000));
        double signum = Math.signum((target.getBlockX()-pointA.getX()) * (pointB.getZ()-pointA.getZ()) - ((target.getBlockZ()-pointA.getZ()) * (pointB.getX() - pointA.getX())));

        int posFromMiddle = (int)Math.round(angle/4.029);


        return signum >= 0 ? 23 - posFromMiddle : 23 + posFromMiddle;
    }

    public static final String EMPTY_BAR = "                                             ";
    public void refreshBossBar(){
        if(this.bossBar==null && this.bossBarTargets.size()==0)
            return;
        if(this.bossBar!=null && this.bossBarTargets.size()==0){
            this.bossBar.removeAll();
            return;
        }
        if(Bukkit.getPlayer(this.uuid)==null) {
            this.updateTrackerTask.cancel();
            this.updateTrackerTask.cancel();
            return;
        }
        if(this.bossBar == null) {
            this.bossBar = Bukkit.getServer().createBossBar("", BarColor.RED, BarStyle.SOLID);
            refreshBossBar();
            return;
        }
        Player player = Bukkit.getPlayer(this.uuid);
        if(!this.bossBar.getPlayers().contains(player))
            this.bossBar.addPlayer(player);
        HashMap<Integer, String> placeholders = new HashMap<>();
        for (TrackedTarget target : this.bossBarTargets) {
            if(!target.getLocation().getWorld().equals(player.getWorld()))
                continue;
            int slot = getBossBarSlotForTarget(player, target.getLocation());
            if(slot == Integer.MAX_VALUE)
                continue;
            placeholders.put(slot, getColorFromDistance(target.getLocation().distance(player.getLocation())) + "" + target.getPointer());
        }
        if(placeholders.size()==0) {
            this.bossBar.removeAll();
            return;
        }

        StringBuilder sb = new StringBuilder(EMPTY_BAR);
        for(Map.Entry<Integer, String> entry : placeholders.entrySet()) {
            sb.replace(entry.getKey(), entry.getKey()+1, entry.getValue());
        }

        this.bossBar.setTitle(sb.toString());
    }

    private static ChatColor getColorFromDistance(double distance){
        if(distance<=10)
            return ChatColor.GREEN;
        else if(distance<=50)
            return ChatColor.YELLOW;
        else if(distance<=100)
            return ChatColor.GOLD;
        else
            return ChatColor.RED;
    }

    public long getLoginTime() {
        return loginTime;
    }

    // GETTING


    public void setDailyPlayTime(long dailyPlayTime) {
        this.dailyPlayTime = dailyPlayTime;
    }

    public int getCouponCredits() {
    	return getPurse().getBalance(Currency.COUPON_CREDIT);
    }

    public void setCouponCredits(int couponCredits) {
    	getPurse().set(Currency.COUPON_CREDIT, couponCredits);
    }

    /**
     * Get the highest rank of the user possible.
     * <p>
     * This will traverse all the ranks of the user, and returns the highest rank for this user.
     * </p>
     * 
     * @return The highest rank for this user.
     */
    public UserRank getUserRank() {

    	// if no user rank specified, set as default
        if (this.ur == null){
            this.ur = UserRank.DEFAULT;
        }
        
        // the highest found rank
        UserRank highestRank = this.ur;
        
        // if they have a global rank, and it's HIGHER
        if (this.globalRank != null && this.getGlobalRank().isHigherThan(highestRank)){
        	highestRank = this.globalRank;
        }

        // if they have a trial rank, and it's HIGHER
        if (this.trialRank != null && this.hasTrialRank() && this.trialRank.isHigherThan(highestRank)){
        	highestRank = this.trialRank;
        }

        return highestRank;
    }

    public void setLoginTime(long loginTime) {
        this.loginTime = loginTime;
    }

    /**
     * Set the user's rank.
     * 
     * @param ur - the new rank to set
     */
    public void setUserRank(UserRank ur) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (this.prefs.contains(Pref.SOCIALSPY) && UserRank.MOD.isHigherThan(ur))
            this.setPref(player, Pref.SOCIALSPY, false);
        
        // async save of rank, based on rank's serverKey
        ServerUtil.runTaskAsync(() -> {
        	try (Connection conn = BaseDatabase.getInstance().getConnection()){
        		UserDAO.saveRank(conn, ur.getServerKey(), uuid, ur);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        });

        // if we're setting a global rank, set locally
        if (ur.getServerKey().equalsIgnoreCase("GLOBAL")){
        	this.globalRank = ur;
        }
        else{
        	this.ur = ur;
        }
        
        this.updateNameTag(player);
        this.updateDisplayName(player);
        this.setPerms(player);
    }
    
    /**
     * Set the user's rank.
     * 
     * @param ur - the new rank to set
     * @param serverKey - the rank on the server key
     */
    public void setUserRank(UserRank ur, String serverKey) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (this.prefs.contains(Pref.SOCIALSPY) && UserRank.MOD.isHigherThan(ur))
            this.setPref(player, Pref.SOCIALSPY, false);
        
        // async save of rank, based on rank's serverKey
        ServerUtil.runTaskAsync(() -> {
        	try (Connection conn = BaseDatabase.getInstance().getConnection()){
        		UserDAO.saveRank(conn, serverKey, uuid, ur);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        	}
        });

        // if we're setting a global rank, set locally
        if (serverKey.equalsIgnoreCase("GLOBAL")){
        	this.globalRank = ur;
        }
        else{
        	this.ur = ur;
        }
        
        this.updateNameTag(player);
        this.updateDisplayName(player);
        this.setPerms(player);
    }

    /**
     * Get the rank of the user, without taking into consideration their trial rank.
     * 
     * @return The rank of the user, without it being a trial.
     */
    public UserRank getUserRankNonTrial() {
        
        // if no user rank specified, set as default
        if (this.ur == null){
            this.ur = UserRank.DEFAULT;
        }
        
        // the highest found rank
        UserRank highestRank = this.ur;
        
        // if they have a global rank, and it's HIGHER
        if (this.globalRank != null && this.getGlobalRank().isHigherThan(highestRank)){
        	highestRank = this.globalRank;
        }

        return highestRank;
    }

    public boolean checkTrialRankExpiry() {
        if (this.trialRank == null || this.trialRankExpiry > System.currentTimeMillis())
            return false;
        Player player = Bukkit.getPlayer(this.uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player player = Bukkit.getPlayer(User.this.uuid);
                if (player != null)
                    player.sendMessage(Lang.RANKS.f("&7Your &a&lfree " + User.this.trialRank.getColoredNameBold() + "&7 trial has expired! You can buy it permanently for &a$&l" +
                            User.this.trialRank.getPrice() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7!"));
            }
        }.runTaskLater(Core.getInstance(), 20);

        ServerUtil.runTaskAsync(() -> UserDAO.updateUserTrialRank(this.uuid, null, 0));

        this.trialRank = null;
        this.trialRankExpiry = 0;
        this.updateNameTag(player);
        this.updateDisplayName(player);
        this.setPerms(player);
        return true;
    }

    public boolean hasTrialRank() {
        this.checkTrialRankExpiry();
        return this.trialRank != null;
    }

    public UserRank getTrialRank() {
        return this.hasTrialRank() ? this.trialRank : null;
    }

    public void setTrialRank(UserRank trialRank, long trialRankExpiry) {
        Player player = Bukkit.getPlayer(this.uuid);
        if (this.prefs.contains(Pref.SOCIALSPY))
            this.setPref(player, Pref.SOCIALSPY, false);

        ServerUtil.runTaskAsync(() -> UserDAO.updateUserTrialRank(this.uuid, trialRank, trialRankExpiry));

        this.trialRank = trialRank;
        this.trialRankExpiry = trialRankExpiry;
        this.updateNameTag(player);
        this.updateDisplayName(player);
        this.setPerms(player);
    }

    public long getTrialRankExpiry() {
        return this.trialRankExpiry;
    }
    
    /**
     * Get the global rank for this user.
     * 
     * @return The global rank for this user, if one exists.
     */
    public UserRank getGlobalRank(){
    	return globalRank;
    }
    
    /**
     * Sets the global rank of the specified user.
     * 
     * @param userRank - the rank to set their global rank to
     */
    public void setGlobalRank(UserRank userRank){
    	this.globalRank = userRank;
    }

    public long getTimeUntilTrialRankExpires() {
        return this.hasTrialRank() ? 0 : System.currentTimeMillis() - this.trialRankExpiry;
    }

    public boolean isSpecial() {
        return this.getUserRank() != UserRank.DEFAULT;
    }

    public boolean isRank(UserRank rank) {
        UserRank r = this.getUserRank();
        return r == rank || r.isHigherThan(rank);
    }

    public boolean isPremium() {
        return this.isRank(UserRank.PREMIUM);
    }

    public boolean isStaff() {
        return this.isRank(UserRank.HELPOP);
    }

    public boolean isAdmin() {
        return this.isRank(UserRank.ADMIN);
    }
    
    /**
     * Get the purse that holds the currency for this player.
     * 
     * @return The purse that holds the currency for this player.
     */
    public Purse getPurse() {
		return purse;
	}
    
    /**
     * Get the vote record for this player.
     * 
     * @return The vote record for this player.
     */
    public VoteRecord getVoteRecord(){
    	return voteRecord;
    }
    
    /**
     * Get the amonut of votes for this player.
     * 
     * @return The amount of votes they have.
     * @deprecated - This is used for compatibility purposes, please use {@link #getPurse()} instead.
     */
    @Deprecated
	public int getVotes() {
        return getPurse().getBalance(Currency.VOTE_TOKEN);
    }
    
    /**
     * Remove a vote for this player.
     * 
     * @deprecated - This is for compatibility purposes, please use {@link #getPurse()}.
     */
    @Deprecated
	public void removeVote() {
        getPurse().withdraw(CurrencySource.CUSTOM, Currency.VOTE_TOKEN, 1);
    }
    
    /**
     * Get the chance this user has to get double rewards.
     * 
     * @return The chance this user has to get double rewards.
     */
    public int getDoubleVoteChance(){
    	int chance = getVoteRecord().getStreak() * 5;
    	if (chance >= 100){
    		return 100;
    	}
    	
    	return chance;
    }
    
    /**
     * Get whether or not the player's vote streak has expired.
     * 
     * @return {@code true} if the vote streak has expired, {@code false} otherwise.
     * 
     * @deprecated - Please do not rely on this method to exist in the future.
     */
    @Deprecated
	public boolean voteStreakExpired() {
    	
    	VoteRecord vr = getVoteRecord();
    	
    	// if they never voted before
    	if (!vr.getLastVoted().isPresent()){
    		return false;
    	}
    	
        if (getVoteRecord().getStreak() > 0 && getVoteRecord().getLastVoted().get().getTime() + 172800000 < System.currentTimeMillis()) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Get whether or not this user can increment their vote streak if they were to vote.
     * 
     * @return {@code true} if they can extend their vote streak with another vote, {@code false} otherwise.
     */
    public boolean canVoteStreak() {
    	
    	// get the last time they ever voted
    	Timestamp lastVoteEver = getVoteRecord().getLastVoted().orElse(null);
    	if (lastVoteEver != null){
    		
    		Timestamp current = new Timestamp(System.currentTimeMillis());
    		
    		// don't count same days as vote streak increment
    		boolean isDiff = TimeUtil.isDifferentDay(current, lastVoteEver);
    		if (isDiff) {

				// if voted within timeframe
				int hoursDiff = TimeUtil.getDifferenceInHours(current, lastVoteEver);
				if (hoursDiff <= 48) {
					return true;
				}
				else {
					return false;
				}
    		}
    		
    		// if same day
    		return false;
    	}
    	
    	// never voted
    	return true;
    }
    
    /**
     * Get the timestamp for when the player can vote again to increment their vote streak.
     * 
     * @return The timestamp for when the player can vote again, if present, otherwise empty and they can vote now.
     */
    public Optional<Timestamp> getTimeUntilVoteStreak() {
    	
    	// get the last time they ever voted
    	Timestamp lastVoteEver = getVoteRecord().getLastVoted().orElse(null);
    	if (lastVoteEver != null){
    		
    		Timestamp current = new Timestamp(System.currentTimeMillis());
    		
    		// don't count same days as vote streak increment
    		boolean isDiff = TimeUtil.isDifferentDay(current, lastVoteEver);
    		if (isDiff) {
    			// can vote streak now
    			return Optional.empty();
    		}
    		else{
    			return Optional.of(new Timestamp(lastVoteEver.getNanos() + 86400000 - System.currentTimeMillis())); 
    		}
    	}
    	
    	// can vote streak now
    	return Optional.empty();
    }

	public int getBucks() {
        return this.bucks;
    }

    public void setBucks(int amnt) {
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserBucks(this.uuid, amnt));
        this.bucks = amnt;
    }

    public boolean hasBucks(int i) {
        return this.bucks >= i;
    }

    public void addBucks(int amnt) {
        this.bucks += amnt;
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserBucks(this.uuid, this.bucks));
    }

    public void takeBucks(int amnt) {
        this.bucks -= amnt;
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserBucks(this.uuid, this.bucks));
    }

    public long getLastSpanked() {
        return lastSpanked;
    }

    public void setLastSpanked(long lastSpanked) {
        this.lastSpanked = lastSpanked;
    }

    public int getTokens() {
    	return getPurse().getBalance(Currency.TOKEN);
        //return this.tokens;
    }

    public void setTokens(int amnt) {
    	getPurse().set(Currency.TOKEN, amnt);
    }

    public boolean hasTokens(int i) {
    	return getPurse().getBalance(Currency.TOKEN) >= i;
    }

    public void addTokens(int amnt) {
        getPurse().set(Currency.TOKEN, getPurse().getBalance(Currency.TOKEN) + amnt);
    }

    public void takeTokens(int amnt) {
    	getPurse().set(Currency.TOKEN, getPurse().getBalance(Currency.TOKEN) - amnt);
    }

    public int getCrowbars() {
    	return getPurse().getBalance(Currency.CROWBAR);
    }

    public void setCrowbars(int amnt) {
        getPurse().set(Currency.CROWBAR, amnt);
    }

    public boolean hasCrowbars(int i) {
    	return getPurse().getBalance(Currency.CROWBAR) >= i;
    }

    public void addCrowbars(int amnt) {
        getPurse().set(Currency.CROWBAR, getPurse().getBalance(Currency.CROWBAR) + amnt);
    }

    public void takeCrowbars(int amnt) {
    	getPurse().set(Currency.CROWBAR, getPurse().getBalance(Currency.CROWBAR) - amnt);
    }

    public double getMoney() {
        MoneyEvent e = new MoneyEvent(this.uuid);
        Bukkit.getPluginManager().callEvent(e);
        return e.getBalance();
    }

    public boolean hasMoney(double i) {
        return this.getMoney() >= i;
    }

    public boolean addMoney(double amount) {
        MoneyEvent e = new MoneyEvent(this.uuid, amount);
        Bukkit.getPluginManager().callEvent(e);
        return e.isSuccessfull();
    }

    public boolean takeMoney(double amount) {
        MoneyEvent e = new MoneyEvent(this.uuid, MoneyEventType.TAKE, amount);
        Bukkit.getPluginManager().callEvent(e);
        return e.isSuccessfull();
    }

    public long getLastPlayersToggle() {
        return this.lastPlayersToggle;
    }

    public void setLastPlayersToggle(long lastPlayersToggle) {
        this.lastPlayersToggle = lastPlayersToggle;
    }

    public boolean canTogglePlayers() {
        return this.lastPlayersToggle + 10000 < System.currentTimeMillis();
    }

    public boolean getPref(Pref pref) {
        return this.prefs.contains(pref);
    }

    public void togglePref(Player player, Pref pref) {
        this.setPref(player, pref, !this.prefs.contains(pref));
    }

    public void setPref(Player player, Pref pref, boolean b) {
        if (b && !this.prefs.contains(pref)) this.prefs.add(pref);
        else if (!b) this.prefs.remove(pref);

        ServerUtil.runTaskAsync(() -> UserDAO.updateUserPref(this.uuid, pref, this.prefs.contains(pref)));

        Bukkit.getPluginManager().callEvent(new UpdateEvent(player, pref));
    }

    public ParticleCosmeticStorage.Shape getParticleShape() {
        return this.particleShape;
    }

    public void setParticleShape(ParticleCosmeticStorage.Shape shape) {
        this.particleShape = shape;
    }

    public int getCosmeticVariant() {
        return this.cosmeticVariant;
    }

    public void setCosmeticVariant(int i) {
        this.cosmeticVariant = i;
    }

    public boolean hasNametag(Nametag tag) {
        return this.nametags.contains(tag);
    }

    public void giveNametag(Nametag tag) {
        if (!this.nametags.contains(tag)) this.nametags.add(tag);
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserNametag(this.uuid, tag.getName(), true));
    }

    public void takeNametag(Nametag tag) {
        this.nametags.remove(tag);
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserNametag(this.uuid, tag.getName(), false));
    }

    public Nametag getActivatingNametag() {
        return this.activatingNametag;
    }

    public void setActivatingNametag(Nametag tag) {
        this.activatingNametag = tag;
    }

    public Nametag getBuyingNametag() {
        return this.buyingNametag;
    }

    public void setBuyingNametag(Nametag buyingNametag) {
        this.buyingNametag = buyingNametag;
    }

    public ShopItem getBuyingShopItem() {
        return this.buyingShopItem;
    }

    public void setBuyingShopItem(ShopItem buyingShopItem) {
        this.buyingShopItem = buyingShopItem;
    }

    public boolean dailyStreakExpired() {
        if (this.dailyStreak > 0 && this.lastDailyReward + 172800000 < System.currentTimeMillis()) {
            this.setDailyStreak(0);
            this.setLastDailyReward(0);
            return true;
        }
        return false;
    }

    public int getDailyStreak() {
        this.dailyStreakExpired();
        return this.dailyStreak;
    }

    public long getDailyPlayTime() {
        return dailyPlayTime;
    }

    public void setDailyStreak(int dailyStreak) {
        this.dailyStreak = dailyStreak;
        ServerUtil.runTaskAsync(() -> OldVoteDAO.updateUserDailyStreak(this.uuid, this.dailyStreak));
    }

    public int getLuckyDailyChance() {
        this.dailyStreakExpired();
        return this.dailyStreak >= 20 ? 100 : this.dailyStreak * 5;
    }

    public void addDailyStreak(int i) {
        this.lastDailyReward = System.currentTimeMillis();
        this.dailyStreak += i;
        ServerUtil.runTaskAsync(() -> OldVoteDAO.updateUserDaily(this.uuid, this.dailyStreak, this.lastDailyReward));
    }

    public long getLastDailyReward() {
        return this.lastDailyReward;
    }

    public void setLastDailyReward(long l) {
        this.lastDailyReward = l;
        ServerUtil.runTaskAsync(() -> OldVoteDAO.updateUserLastDailyReward(this.uuid, this.lastDailyReward));
    }

    public void setLastDailyReward() {
        this.setLastDailyReward(System.currentTimeMillis());
    }

    public boolean canClaimDailyReward() {
        return this.lastDailyReward + 86400000 < System.currentTimeMillis();
    }

    public long getTimeUntilDailyReward() {
        return this.lastDailyReward + 86400000 - System.currentTimeMillis();
    }

    public String getLastDonorReward() {
        return this.lastDonorReward;
    }

    public void setLastDonorReward() {
        LocalDateTime now = LocalDateTime.now();
        this.lastDonorReward = now.getYear() + ":" + now.getMonthValue();
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserLastDonorReward(this.uuid, this.lastDonorReward));
    }

    public boolean canClaimMonthlyReward() {
    	// if their highest rank is a default user
        if (getUserRankNonTrial() == UserRank.DEFAULT) return false;
        
        if (this.lastDonorReward == null) return true;
        String[] a
                = this.lastDonorReward.split(":");
        if (a.length == 2)
            try {
                int year = Integer.parseInt(a[0]);
                int month = Integer.parseInt(a[1]);
                LocalDateTime time = LocalDateTime.now();
                return year < time.getYear() || month < time.getMonthValue();
            } catch (NumberFormatException ignored) {
            }
        return true;
    }

    public long getTimeUntilMonthlyReward() {
        if (this.canClaimMonthlyReward()) return 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next = now.getMonthValue() == 12 ? LocalDateTime.of(now.getYear() + 1, 1, 1, 0, 0)
                : LocalDateTime.of(now.getYear(), now.getMonthValue() + 1, 1, 0, 0);
        return ChronoUnit.MILLIS.between(now, next);
    }

    public boolean hasEditMode() {
        return this.editMode;
    }

    public void setEditMode(boolean b) {
        this.editMode = b;
        if (!b && !this.isRank(UserRank.ADMIN)) {
            Player player = Bukkit.getPlayer(this.uuid);
            player.getInventory().iterator().forEachRemaining(itemStack -> player.getInventory().remove(itemStack));
        }
    }

    public String getTutorial() {
        return this.tutorial;
    }

    public void setTutorial(String tutorial) {
        this.tutorial = tutorial;
    }

    public boolean isInTutorial() {
        return this.tutorial != null;
    }

    public int getTutorialSlide() {
        return this.tutorialSlide;
    }

    public void setTutorialSlide(int tutorialSlide) {
        this.tutorialSlide = tutorialSlide;
    }

    public boolean isEditingTutorial() {
        return this.editingTutorial;
    }

    public void setEditingTutorial(boolean editingTutorial) {
        this.editingTutorial = editingTutorial;
    }

    public Scoreboard getScoreboard() {
        if (this.scoreboard == null)
            this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        return this.scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    // UTILS

    public void updateVisibility(Player p) {
        if (p == null)
            p = Bukkit.getPlayer(this.uuid);
        if (this.isSpecial()) {
            for (Player pl : Bukkit.getOnlinePlayers()) {
                pl.showPlayer(p);
                if (this.prefs.contains(Pref.PLAYERS_SHOWN)) p.showPlayer(pl);
                else p.hidePlayer(pl);
            }
            return;
        }
        for (Player pl : Bukkit.getOnlinePlayers()) {
            if (this.prefs.contains(Pref.PLAYERS_SHOWN)) p.showPlayer(pl);
            else p.hidePlayer(pl);
            User u = Core.getUserManager().getLoadedUser(pl.getUniqueId());
            if (u.getPref(Pref.PLAYERS_SHOWN)) pl.showPlayer(p);
            else pl.hidePlayer(p);
        }
    }

    public void updateDisplayName(Player p) {
        if (p == null)
            return;
        DisplayNameUpdateEvent event = new DisplayNameUpdateEvent(p);
        UserRank rank = this.getUserRank();
        event.setPrefix("");
        event.setSuffix("");
        event.setRankPrefix(rank.getPrefix());
        event.setNameColor(rank.getColor());
        Bukkit.getPluginManager().callEvent(event);
        if(this.equipedTag!=null) {
            event.setPrefix(this.equipedTag.getBoldName());
        }
        p.setDisplayName(Utils.f(event.getRankPrefix() + ' ' + event.getPrefix()
                + (event.getPrefix().isEmpty() ? "" : " ") + event.getNameColor() + (this.isSpecial() ? "&l" : "")
                + p.getName() + (event.getSuffix().isEmpty() ? "" : " ") + event.getSuffix()));
    }

    public void updateFlyMode(Player p) {
        if (p == null)
            return;
        if (!this.isSpecial())
            return;
        p.setAllowFlight(true);
        p.setFlying(true);
    }

    public void setPerms(Player p) {
        if (p == null)
            p = Bukkit.getPlayer(this.uuid);
        if (p == null)
            return;
        List<String> ls = Core.getPermsManager().getAllPerms(this.getUserRank(), this.uuid);
        if (this.pa != null)
            p.removeAttachment(this.pa);
        this.pa = p.addAttachment(Core.getInstance());
        for (String s : ls)
            if (s.startsWith("-")) this.pa.setPermission(s.substring(1), false);
            else this.pa.setPermission(s, true);

    }

    public void removePerms(Player p) {
        if (this.pa != null)
            p.removeAttachment(this.pa);
        this.pa = null;
    }

    public void updateNameTag(Player p) {
        NametagManager.updateNametag(p);
    }

    public String getColoredName(Player player) {
        return this.getUserRank().getColor() + (this.isSpecial() ? "&l" : "") + player.getName();
    }

    public UUID getLastMessage() {
        return this.lastMessage;
    }

    public void setLastMessage(UUID lastMessage) {
        this.lastMessage = lastMessage;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }

    public void setHasMoved() {
        this.hasMoved = true;
    }

    public void insertLog(Player player, String action, String type, String reward, double amount, double price) {
        Utils.insertLogLater(player.getUniqueId(), player.getName(), action, type, reward, amount, price);
    }

    public void updateIgnored() {
        String s = "";
        for (String st : this.ignored)
            s += st + ',';
        if (s.endsWith(","))
            s = s.substring(0, s.length() - 1);

        String finalS = s;
        ServerUtil.runTaskAsync(() -> UserDAO.updateUserIgnore(this.uuid, finalS));
    }

    public void addIgnored(String name) {
        this.ignored.add(name);
        this.updateIgnored();
    }

    public void removeIgnored(String name) {
        this.ignored.remove(name);
        this.updateIgnored();
    }

    public List<String> getIgnored() {
        return this.ignored;
    }

    public boolean isIgnored(String name) {
        return this.ignored.contains(name);
    }

    public boolean isVanished(Player player) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("Essentials");
        return plugin != null && ((Essentials) plugin).getVanishedPlayers().contains(player.getName());
    }

    public Long getJoinTime() {
        return this.joinTime;
    }

    public void setJoinTime(Long joinTime) {
        this.joinTime = joinTime;
    }

    public Long getLeaveTime() {
        return this.leaveTime;
    }

    public void setLeaveTime(Long leaveTime) {
        this.leaveTime = leaveTime;
    }

    public void updateAchievements() {
        if (this.unlockedAchievements.isEmpty()) return;
        if (this.unlockedAchievements.size() > Achievement.values().length) return;
        Set<String> temp = new HashSet<>();
        this.unlockedAchievements.forEach(achievement -> temp.add(achievement.getShortName()));
        String s = StringUtils.join(temp, ",");

        ServerUtil.runTaskAsync(() -> UserDAO.updateUnlockedAchievements(this.uuid, s));

        if (this.shownAchievement != null) {
            ServerUtil.runTaskAsync(() -> UserDAO.updateShownAchievement(this.uuid, this.shownAchievement.getShortName()));
        }
    }

    public Set<Achievement> getUnlockedAchievements() {
        return this.unlockedAchievements;
    }

    public boolean hasAchievement(Achievement achievement) {
        return this.unlockedAchievements.contains(achievement);
    }

    public void addAchievement(Achievement achievement) {
        boolean disabled = true;
        if (disabled) return; // TODO
        if (achievement == null) return;
        if (hasAchievement(achievement)) return;
        this.unlockedAchievements.add(achievement);
        if (this.shownAchievement == null) this.setShownAchievement(achievement);
        if (achievement.ordinal() > this.getShownAchievement().ordinal()) this.setShownAchievement(achievement);
        this.updateAchievements();
    }

    public Achievement getShownAchievement() {
        if (!this.unlockedAchievements.contains(this.shownAchievement)) this.addAchievement(this.shownAchievement);
        return this.shownAchievement;
    }

    public void setShownAchievement(Achievement achievement) {
        if (achievement == null) return;
        if (!this.unlockedAchievements.contains(achievement)) return;
        this.shownAchievement = achievement;
        ServerUtil.runTaskAsync(() -> UserDAO.updateShownAchievement(this.uuid, this.shownAchievement.getShortName()));
    }

    public Crate getSelectedCrate() {
        return this.selectedCrate;
    }

    public void setSelectedCrate(Crate selectedCrate) {
        this.selectedCrate = selectedCrate;
    }

    public CrateReward getConfirmingCrateReward() {
        return this.confirmingCrateReward;
    }

    public void setConfirmingCrateReward(CrateReward c) {
        this.confirmingCrateReward = c;
    }

    /**
     * @param length how long the cooldown is (in seconds)
     * @param saveToMySQL if the cooldown should be saved to mySQL (if for more than 6 hours, it probably should)
     * @param id the id of the cooldown
     * @param serverSpecific if the cooldown should only effect the one server.
     */
    public void addCooldown(String id, long length, boolean saveToMySQL, boolean serverSpecific){
        this.cooldowns.add(new CooldownPayload(id, (length*1000) + System.currentTimeMillis(), serverSpecific, saveToMySQL));
    }


    /**
     * @param type the type of thing the cooldown falls under
     * @return true if the player still has a cooldown.
     */
    public boolean isOnCooldown(String type){
        if(this.cooldowns==null)
            return false;
        Set<CooldownPayload> clone = this.cooldowns;
        for(CooldownPayload obj : clone) {
            if(obj.getId().equals(type)) {
                if(System.currentTimeMillis()>=obj.getExpireTime().getTime()) {
                    this.cooldowns.remove(obj);
                    return false;
                }
                return true;
            }
        }
        return false;
    }

    public void removeCooldown(String type) {
        Set<CooldownPayload> clone = this.cooldowns;
        for(CooldownPayload o : clone) {
            if(o.getId().equalsIgnoreCase(type)) {
                this.cooldowns.remove(o);
            }
        }
    }

    /**
     * @param type the string identifier of the cooldown
     * @return a String formated #.# displaying the seconds remaining until expire
     */
    public String getFormattedCooldown(String type){
        DecimalFormat df = new DecimalFormat("#.#");
        Optional<CooldownPayload> optCD = this.cooldowns.stream().filter(obj -> obj.getId().equals(type)).findFirst();
        if(!optCD.isPresent()) {
            Core.error("Tried to get formatted cooldown of " + type + " but the player " + this.getName() + " does not have a cooldown of that type!");
            return ChatColor.RED + "ERROR: Could not find your cooldown! Report this to an admin id: " + type;
        }
        long time = optCD.map(c -> c.getExpireTime().getTime()).orElse(0L);
        time -= System.currentTimeMillis();
        time /= 1000;
        String returnStr;
        if(TimeUnit.SECONDS.toDays(time)>1)
            returnStr = TimeUnit.SECONDS.toDays(time) + " day";
        else if(TimeUnit.SECONDS.toHours(time)>1)
            returnStr = TimeUnit.SECONDS.toHours(time) + " hour";
        else if(TimeUnit.SECONDS.toMinutes(time)>1)
            returnStr = TimeUnit.SECONDS.toMinutes(time) + " minute";
        else
            returnStr = df.format(time) + " second";

        return returnStr + (returnStr.equals("1") || returnStr.equals("1.0") ? "" : "s");
    }

    public long getCooldownTimeLeft(String type) {
        Optional<CooldownPayload> optCD = this.cooldowns.stream().filter(obj -> obj.getId().equals(type)).findFirst();
        if(!optCD.isPresent()) {
            Core.error("Tried to get formatted cooldown of " + type+ " but the player " + this.getName() + " does not have a cooldown of that type!");
            throw new NullPointerException();
        }
        long time = optCD.map(c -> c.getExpireTime().getTime()).orElse(0L);
        time -= System.currentTimeMillis();
        time /= 1000;
        return time;
    }

    public CoreLocation getLocation() {
        return location;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get the address that the user used to join the server.
     * 
     * @return The IP address that the user used to join the server.
     */
	public String getServerJoinAddress() {
		return serverJoinAddress;
	}
	
	/**
	 * Set the address that the user used to join the server.
	 * 
	 * @param joinAddress - the join IP address
	 */
	public void setServerJoinAddress(String joinAddress){
		this.serverJoinAddress = joinAddress;
	}
}
