package net.grandtheftmc.core.voting;

import java.sql.Connection;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import com.j0ach1mmall3.jlib.methods.Parsing;
import com.vexsoftware.votifier.model.Vote;
import com.vexsoftware.votifier.model.VotifierEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.currency.Currency;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.VoteDAO;
import net.grandtheftmc.core.events.UpdateEvent;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.nametags.Nametag;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.transaction.state.user.UserStateTransactionDAO;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.PluginAssociated;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.debug.Log;
import net.grandtheftmc.core.util.json.JSONBuilder;
import net.grandtheftmc.core.util.time.TimeUtil;
import net.grandtheftmc.jedis.JedisChannel;
import net.grandtheftmc.jedis.message.UserStateTransactionCheck;
import net.grandtheftmc.jedis.message.VoteNotificationMessage;
import net.md_5.bungee.api.ChatColor;

/**
<<<<<<< HEAD
 * 
 * @deprecated `voters` field is traversed everytime someone votes, and
 *             therefore if we have 10,000 voters in one day, just inserting a
 *             vote looks through 10,000 records. This should be stored as a
 *             HashMap. Also this data is never cleaned up. Why do we have it
 *             here? Player data should be read/write to the database and not
 *             handled.
=======
 * @deprecated `voters` field is traversed everytime someone votes, and therefore if we have 10,000 voters in one day, just inserting a vote looks through 10,000 records. This should be stored as a HashMap. Also this data is never cleaned up. Why do we have it here? Player data should be read/write to the database and not handled.
>>>>>>> refs/heads/develop
 */
@Deprecated
public class VoteManager implements Component<VoteManager, Core>, PluginAssociated {

	/** The owning plugin */
	private final Plugin plugin;
	/** List of voters that have been registered as "voted" */
	private final List<Voter> voters = new ArrayList<>();
	/** Possible vote rewards */
	private final List<VoteReward> voteRewards = new ArrayList<>();
	/** Possible daily rewards */
	private final List<RewardPack> dailyRewards = new ArrayList<>();
	/** Possible lucky daily rewards */
	private final List<RewardPack> luckyDailyRewards = new ArrayList<>();
	/** Rewards per user rank */
	private final Map<UserRank, List<RewardPack>> monthlyRewards = new HashMap<>();
	private final List<ShopItem> shopItems = new ArrayList<>();
	private String voteLink = "vote.grandtheftmc.net";
	private long lastMonthlyReset;
	private int taskId = -1;
	
	/** The top voters, populated by a repeating task */
	private VoteDAO.VoteUser[] topVoters;
	/** The last month top voters, populated by a repeating task */
	private VoteDAO.VoteUser[] lastTopVoters;

	/**
	 * Construct a new VoteManager.
	 * <p>
	 * This handles the receiving of votes and credits the user with rewards as user state transactions.
	 * 
	 * Note: When a vote is received it also forwards a vote message via redis to all servers. 
	 * Therefore only hub1 should really be getting any VotifierEvents.
	 * </p>
	 * 
	 * @param plugin - the owning plugin
	 */
	public VoteManager(Plugin plugin) {
		this.plugin = plugin;
		this.loadLinksAndRewards();
		this.loadTokenShop();
		this.startSchedule();
		
		// every 10 minutes
		Bukkit.getScheduler().runTaskTimerAsynchronously(getPlugin(), () -> {
			
			// local cache result
			VoteDAO.VoteUser[] topVotersLocal = null;
			// local cache result
			VoteDAO.VoteUser[] lastVotersLocal = null;
			
			try (Connection conn = BaseDatabase.getInstance().getConnection()){
				topVotersLocal = VoteDAO.getTopVoters(conn, 10).orElse(null);
				lastVotersLocal = VoteDAO.getLastTopVoters(conn, 10).orElse(null);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			
			final VoteDAO.VoteUser[] finalTopVoters = topVotersLocal;
			final VoteDAO.VoteUser[] finalLastVoters = lastVotersLocal;
			Bukkit.getScheduler().runTask(getPlugin(), () -> {
				topVoters = finalTopVoters;
				lastTopVoters = finalLastVoters;
			});
			
		} , 0L, 20 * 600L);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public VoteManager onDisable(Core plugin) {
		if (!this.voters.isEmpty())
			this.voters.clear();
		if (!this.voteRewards.isEmpty())
			this.voteRewards.clear();
		if (!this.dailyRewards.isEmpty())
			this.dailyRewards.clear();
		if (!this.luckyDailyRewards.isEmpty())
			this.luckyDailyRewards.clear();
		if (!this.monthlyRewards.isEmpty())
			this.monthlyRewards.clear();
		if (!this.shopItems.isEmpty())
			this.shopItems.clear();
		return this;
	}

	/**
	 * Start the schedule task of broadcasting votes every 5 minutes.
	 */
	public void startSchedule() {
		if (this.taskId > 0)
			Bukkit.getScheduler().cancelTask(this.taskId);
		this.taskId = new BukkitRunnable() {
			@Override
			public void run() {
				VoteManager.this.broadcastVoteMessage();
			}
		}.runTaskTimer(Core.getInstance(), 300, 300).getTaskId();
	}

	/**
	 * Iterate through all the voters that this manager knows about and send notifications across the server.
	 */
	public void broadcastVoteMessage() {
		
		// if no voters to display
		if (this.voters.isEmpty())
			return;
		
		// build the broadcast string
		StringBuilder s = new StringBuilder(Lang.VOTE.s());
		
		int size = this.voters.size();
		for (int i = 0; i < this.voters.size(); i++) {
			Voter voter = this.voters.get(i);
			s.append("&a").append(voter.getName()).append(voter.getVotes() > 1 ? " &7(&e" + voter.getVotes() + "&7)" : "&7");
			if (size - i == 1){
				s.append(" voted for the server and received awesome rewards! Do &a\"/vote\"&7 to find out more! Vote on all &e5 &7sites for maximum rewards!");
			}
			else if (size - i == 2){
				s.append(" and ");
			}
			else{
				s.append(", ");
			}
		}
		
		// broadcast to the server
		Utils.broadcast(s.toString());
		
		// clear voters list
		this.voters.clear();
	}

	/**
	 * Listens in on the Votifier events.
	 * <p>
	 * This event is on low so we can run it first.
	 *
	 * @param event - the event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onVotifierEvent(VotifierEvent event) {
		
		// grab event variables
		Vote vote = event.getVote();
		String userName = vote.getUsername();
	
		Log.info("VoteManager", "Received vote from " + vote.getServiceName() + " for user=" + userName);
		
		// get the site that they voted on
        VoteSite voteSite = VoteSite.find(vote.getServiceName()).orElse(null);
        
        // if not a valid site, log message
        if (voteSite == null){
        	Log.info("VoteManager", "Voting site '" + vote.getServiceName() + "' not found in our database!");
            return;
        }

        // async fetch
        Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {

			try (Connection conn = BaseDatabase.getInstance().getConnection()) {

				UUID uuid = UserDAO.getUUID(conn, userName);
				if (uuid != null) {
					
					// grab the user vote record
					VoteRecord vr = VoteDAO.getUserVoteRecord(conn, uuid);
					
					// check if this vote site has already been voted on
		        	if (isVoteSiteCooldown(vr, voteSite, userName)){
		        		return;
		        	}
		        	
		        	// handle the vote streak increment
		        	handleVoteStreakIncrement(conn, vr);
				
		            // add a total vote
		            VoteDAO.incrementTotalVotes(conn, uuid);
		            
		            // update the timestamp for this vote site
		            VoteDAO.updateVoteSiteTimestamp(conn, uuid, voteSite);
		            
		            int numVoteTokens = 1;
		            
		            // determine chance of getting higher streak
		            int rollChance = vr.getStreak() * 5;
		            if (rollChance >= 100){
		            	rollChance = 100;
		            }
		    		
		    		// what additional message to send along in the payload
		    		String message = "&7Remember to vote daily to increase your vote streak for more rewards! &e&lCurrent Vote Streak&7: " + (vr.getStreak() + 1);
		            
		            // if double reward
		            if (Utils.calculateChance(rollChance)) {
		            	numVoteTokens = 2;
		            	message = "&7You got lucky and earned a &e&lDouble Vote&7! Chance: &a&l" + rollChance + "%";
		            } 
		            // if first three days of month
		            else if (new Timestamp(System.currentTimeMillis()).getDate() <= 3) {
		            	numVoteTokens = 2;
		            	message = "&7Vote rewards are doubled in the first three days of every month! Make sure to keep voting all month to build up your &e&lVote Streak&7!";
		            } 
		            
		            // log the vote to the database
		            VoteDAO.logUserVote(conn, vr.getOwner(), numVoteTokens, voteSite != null ? voteSite.getId() : -1);
		            
		            // create in the format of
					// {"type": "currency", "uuid": "0xDD", "currency": "VotingTokens", "amount": 1}
					JSONObject payload = new JSONBuilder().set("type", "currency").set("uuid", uuid.toString()).set("currency", Currency.VOTE_TOKEN.getId()).set("amount", numVoteTokens).create();
					UserStateTransactionDAO.addUserStateTransaction(conn, uuid, payload);
					
					// get the global messaging channel and send a vote message payload through
					Core.getJedisManager().getModule(JedisChannel.GLOBAL).sendMessage(new VoteNotificationMessage(uuid, message));
					// get the global messaging channel and send a check user state payload through
					Core.getJedisManager().getModule(JedisChannel.GLOBAL).sendMessage(new UserStateTransactionCheck(uuid));
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		});
	}

	/**
	 * Claim the daily reward for the given player.
	 * 
	 * @param player - the player claiming the reward
	 * @param user - the user object representation for this player
	 */
	public void claimDaily(Player player, User user) {
		if (Core.getSettings().getType() == ServerType.HUB) {
			player.sendMessage(Lang.REWARDS.f("&7Please claim this reward on " + Core.getSettings().getServer_GTM_shortName() + " to receive extra money!"));
			return;
		}
		if (!user.canClaimDailyReward()) {
			player.sendMessage(Lang.REWARDS.f("&7You need to wait &c&l" + Utils.timeInMillisToText(user.getTimeUntilDailyReward()) + "&7 until you can claim your daily reward again!"));
			return;
		}
		user.setLastDailyReward();
		if (Utils.calculateChance(user.getLuckyDailyChance())) {
			player.sendMessage(Lang.REWARDS.f("&7You got lucky and earned a &e&lLucky Reward&7 of &e&l5 Tokens&7! Chance: &a%&l" + user.getLuckyDailyChance()));
			user.addTokens(5);
			for (RewardPack pack : this.luckyDailyRewards) {
				pack.give(player, user, null, 0, true);
			}
		}
		else {
			player.sendMessage(Lang.REWARDS.f("&7You received &e&l2 Tokens&7 as a daily reward!"));
			user.addTokens(2);
			for (RewardPack pack : this.dailyRewards) {
				pack.give(player, user, null, 0, true);
			}
		}
		user.addDailyStreak(1);
		MenuManager.updateMenu(player, "rewards");
		Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS));
	}

	/**
	 * Claim the monthly reward for the given player.
	 * 
	 * @param player - the player claiming the reward
	 * @param user - the user object representation for this player
	 */
	public void claimMonthly(Player player, User user) {

		// If user rank specifically is DEFAULT (exclude trial)
		if (user.getUserRankNonTrial() == UserRank.DEFAULT) {
			player.sendMessage(Lang.REWARDS.f("&7Only donators can claim Donor Rewards! Buy a rank at &a&l" + Core.getSettings().getStoreLink() + "&7!"));
			return;
		}

		// If server type is HUB
		if (Core.getSettings().getType() == ServerType.HUB) {
			player.sendMessage(Lang.REWARDS.f("&7Please claim this reward on " + Core.getSettings().getServer_GTM_shortName() + " to receive extra money and exclusive items!"));
			return;
		}

		// If user cannot claim rewards
		if (!user.canClaimMonthlyReward()) {
			player.sendMessage(Lang.REWARDS.f("&7You need to wait &c&l" + Utils.timeInMillisToText(user.getTimeUntilMonthlyReward()) + "&7 until you can claim your donor reward again!"));
			return;
		}

		user.setLastDonorReward();
		UserRank rank = user.getUserRankNonTrial().isHigherThan(UserRank.SUPREME) ? UserRank.SUPREME : user.getUserRankNonTrial();
		int tokens = rank.getMonthlyTokens();
		user.addTokens(tokens);
		user.insertLog(player, "claimDonorReward", rank.toString(), "tokens", tokens, 0);
		if (this.monthlyRewards.containsKey(rank))
			for (RewardPack pack : this.monthlyRewards.get(rank)) {
				player.sendMessage(Lang.REWARDS.f(pack.getDisplayName()));
				pack.give(player, user, "claimDonorReward", 0, true);
			}
		player.sendMessage(Lang.REWARDS.f("&7You claimed &e&l" + tokens + " Tokens&7 from your " + rank.getColoredNameBold() + "&7 rank!"));
		Utils.broadcastExcept(player, Lang.REWARDS.f(user.getColoredName(player) + "&7 claimed &e&l" + tokens + " Tokens&7 and a bunch of other cool items from their " + rank.getColoredNameBold() + "&7 rank!"));
		MenuManager.updateMenu(player, "rewards");
		Bukkit.getPluginManager().callEvent(new UpdateEvent(player, UpdateEvent.UpdateReason.TOKENS));
	}

	/**
	 * Spend amount of votes for the given player
	 * 
	 * @param player - the player claiming the reward
	 * @param user - the user object representation for this player
	 * @param amount - the amount of votes to spend
	 */
	public void spendVote(Player player, User user, int amount) {
		if (Core.getSettings().getType() == ServerType.HUB) {
			player.sendMessage(Lang.REWARDS.f("&7Please claim this reward on " + Core.getSettings().getServer_GTM_shortName() + " to receive extra money and rare rewards!"));
			return;
		}
		if (user.getVotes() <= 0) {
			player.sendMessage(Lang.VOTE.f("&7You don't have any votes left to claim!"));
			return;
		}
		for (int i = 0; i < amount; i++) {
			this.spendVote(player, user);
		}
	}

	/**
	 * Spend only one vote for the given player.
	 * 
	 * @param player - the player claiming the reward
	 * @param user - the user object representation for this player
	 */
	public void spendVote(Player player, User user) {
		if (Core.getSettings().getType() == ServerType.HUB) {
			player.sendMessage(Lang.REWARDS.f("&7Please claim this reward on " + Core.getSettings().getServer_GTM_shortName() + " to receive extra money and rare rewards!"));
			return;
		}
		if (user.getVotes() <= 0) {
			player.sendMessage(Lang.VOTE.f("&7You don't have any votes left to claim!"));
			return;
		}
		user.removeVote();
		player.sendMessage(Lang.VOTE.f("&7Thank you for voting! Here are your rewards:"));
		for (VoteReward reward : this.voteRewards)
			if (reward.getChance() == 100.0000 || Utils.calculateChance(reward.getChance()))
				reward.give(player, user);
		MenuManager.updateMenu(player, "vote");
	}

	/**
	 * Spend all the votes possible for the given player.
	 * 
	 * @param player - the player claiming the reward
	 * @param user - the user object representation for this player
	 */
	public void spendAllVotes(Player player, User user) {
		if (user.getVotes() <= 0) {
			player.sendMessage(Lang.VOTE.f("&7You don't have any votes left to claim!"));
			return;
		}
		for (int i = 0; i < user.getVotes(); i++) {
			this.spendVote(player, user);
		}
	}

	/**
	 * Get the voter based off the username from the voters list.
	 * 
	 * @param username - the username to lookup
	 * 
	 * @return The voter, if one exists, otherwise {@code null}
	 */
	public Voter getVoter(String username) {
		return this.voters.stream().filter(voter -> Objects.equals(voter.getName(), username)).findFirst().orElse(null);
	}

	/**
	 * Create the voter in the voters list, if they don't already exist.
	 * 
	 * @param username - the username for the voter
	 * 
	 * @return The voter object.
	 */
	public Voter createVoter(String username) {
		for (Voter voter : this.voters)
			if (Objects.equals(voter.getName(), username))
				return voter;
		Voter v = new Voter(username);
		this.voters.add(v);
		return v;
	}

	/**
	 * Save the settings of the vote manager.
	 * <p>
	 * This will save the last reset timing to the voting.yml.
	 * 
	 * @param shutdown - {@code true} if this is called via shutdown, {@code false} otherwise.
	 */
	public void save(boolean shutdown) {
		if (Core.getSettings().getType() == ServerType.HUB) {
			Core.getSettings().getVotingConfig().set("lastreset", this.lastMonthlyReset);
			Utils.saveConfig(Core.getSettings().getVotingConfig(), "voting");
		}
	}

	/**
	 * Load the rewards from the yml files.
	 */
	public void loadLinksAndRewards() {
		this.voters.clear();
		YamlConfiguration c = Core.getSettings().getVotingConfig();
		if (c.get("votelink") != null)
			this.voteLink = c.getString("votelink");
//        this.lastMonthlyReset = c.get("lastreset") == null ? 0 : c.getLong("lastreset");
//        if (Core.getSettings().getType() == ServerType.HUB) {
//            if (this.lastMonthlyReset + TimeUnit.DAYS.toMillis(30) < System.currentTimeMillis()) {
//                this.resetMonthlyVotes();
//                this.setLastMonthlyReset(System.currentTimeMillis());
//            }
//        }
		this.voteRewards.clear();
		if (c.get("rewards") != null)
			for (String name : c.getConfigurationSection("rewards").getKeys(false)) {
				try {
					double chance = c.get("rewards." + name + ".chance") == null ? 100 : c.getDouble("rewards." + name + ".chance");
					String item = c.getString("rewards." + name + ".item");
					RewardPack pack = this.getRewardPack(c, name, "rewards." + name);
					if (pack == null)
						Core.error("Error while loading RewardPack for vote reward: " + name);
					else
						this.voteRewards.add(new VoteReward(pack, item, chance));
				}
				catch (Exception ex) {
					Core.error("Error while loading vote reward: " + name);
					ex.printStackTrace();
				}
			}
		this.dailyRewards.clear();
		this.luckyDailyRewards.clear();
		this.monthlyRewards.clear();
		c = Core.getSettings().getRewardsConfig();
		if (c.get("daily") != null)
			for (String name : c.getConfigurationSection("daily").getKeys(false)) {
				try {
					RewardPack pack = this.getRewardPack(c, name, "daily." + name);
					if (pack == null)
						Core.error("Error while loading RewardPack for daily reward: " + name);
					else
						this.dailyRewards.add(pack);
				}
				catch (Exception e) {
					Core.error("Error while loading daily reward: " + name);
					e.printStackTrace();
				}
			}
		if (c.get("luckyDaily") != null)
			for (String name : c.getConfigurationSection("luckyDaily").getKeys(false)) {
				try {
					RewardPack pack = this.getRewardPack(c, name, "luckyDaily." + name);
					if (pack == null)
						Core.error("Error while loading RewardPack for luckyDaily reward: " + name);
					else
						this.luckyDailyRewards.add(pack);
				}
				catch (Exception e) {
					Core.error("Error while loading luckyDaily reward: " + name);
					e.printStackTrace();
				}
			}
		if (c.get("monthly") != null)
			for (String rankName : c.getConfigurationSection("monthly").getKeys(false)) {
				try {
					UserRank rank = UserRank.getUserRankOrNull(rankName);
					if (rank == null) {
						Core.error("Error while loading monthly reward with invalid name: " + rankName);
						continue;
					}
					List<RewardPack> packs = new ArrayList<>();
					for (String name : c.getConfigurationSection("monthly." + rankName).getKeys(false)) {
						RewardPack pack = this.getRewardPack(c, name, "monthly." + rankName + '.' + name);
						if (pack == null)
							Core.error("Error while loading RewardPack for monthly reward for " + rank + ": " + name);
						else
							packs.add(pack);
					}
					if (packs.isEmpty())
						Core.log("Error while loading monthly reward for rank: " + rankName + " has no rewards");
					else
						this.monthlyRewards.put(rank, packs);
				}
				catch (Exception e) {
					Core.error("Error while loading monthly reward for rank: " + rankName);
					e.printStackTrace();
				}
			}
	}

	/**
	 * Get the reward pack from the given yaml config with the given name and path.
	 * 
	 * @param c - the yaml config to read from
	 * @param name - the name of the reward
	 * @param path - the path to look for the reward
	 * 
	 * @return The reward pack, if one was found, otherwise {@code null}.
	 */
	public RewardPack getRewardPack(YamlConfiguration c, String name, String path) {
		if (c.getString(path + ".list") == null)
			return new RewardPack(this.getReward(c, name, path), c.getString(path + ".description"));
		List<Reward> rewards = c.getConfigurationSection(path + ".list").getKeys(false).stream().map(s -> this.getReward(c, s, path + ".list." + s)).filter(Objects::nonNull).collect(Collectors.toList());
		if (rewards.isEmpty())
			return null;
		if (rewards.size() == 1)
			return new RewardPack(rewards.get(0), c.getString(path + ".description"));
		return new RewardPack(name, rewards, c.getString(path + ".description"));
	}

	/**
	 * Get the reward object from the given yaml config, name, and path.
	 * 
	 * @param c - the yaml config
	 * @param name - the name of the reward
	 * @param path - the path to lookup
	 * 
	 * @return The reward object, if one was found, otherwise {@code null}.
	 */
	public Reward getReward(YamlConfiguration c, String name, String path) {
		Reward.RewardType type = Reward.RewardType.fromString(c.getString(path + ".type"));
		String disp = Utils.f(name);
		switch (type) {
			case CHEATCODE:
				return new Reward(disp, Reward.RewardType.CHEATCODE, c.getString(path + ".cheatcode"));
			case BUCKS:
			case TOKENS:
			case MONEY:
			case CROWBARS:
				return new Reward(disp, c.getDouble(path + ".amount"), type);
			case CUSTOM:
				String customType = c.getString(path + ".customType");
				if (c.get(path + ".customList") != null)
					return new Reward(disp, customType, c.getStringList(path + ".customList"));
				return new Reward(disp, customType, c.getString(path + ".customName"), c.get(path + ".amount") == null ? 1 : c.getDouble(path + ".amount"));
			case ITEMS:
				List<ItemStack> items = c.getStringList(path + ".items").stream().map(Parsing::parseItemStack).collect(Collectors.toList());
				return new Reward(disp, Utils.toArray(items));
//            case COSMETIC:
//                String ct = c.getString(path + ".cosmeticType");
//                CosmeticType cosmeticType = CosmeticType.getType(c.getString(path + ".cosmeticType"));
//                if (ct == null) {
//                    int minTokens = c.get(path + ".minTokens") == null ? 0 : c.getInt(path + ".minTokens");
//                    int maxTokens = c.get(path + ".maxTokens") == null ? -1 : c.getInt(path + ".maxTokens");
//                    return new Reward(disp, null, minTokens, maxTokens);
//                }
//                if (cosmeticType == null) {
//                    Core.error("Error while loading vote reward " + name + "! The type is not a valid CosmeticType.");
//                    return null;
//                }
//                String co = c.getString(path + ".cosmetic");
//                if (co == null || "random".equalsIgnoreCase(co)) {
//                    int minTokens = c.get(path + ".minTokens") == null ? 0 : c.getInt(path + ".minTokens");
//                    int maxTokens = c.get(path + ".maxTokens") == null ? -1 : c.getInt(path + ".maxTokens");
//                    return new Reward(disp, cosmeticType, minTokens, maxTokens);
//                }
//                Cosmetic cos = cosmeticType.getCosmetic(co);
//                if (cos == null) {
//                    Core.error("Error while loading vote reward " + name + "! The cosmetic is not a valid cosmetic of CosmeticType " + type + '!');
//                    return null;
//                }
//                return new Reward(disp, cos);

			case TRIAL_RANK: {
				UserRank rank = UserRank.getUserRankOrNull(c.getString(path + ".rank"));
				if (rank == null) {
					Core.error("Error while loading shop item " + name + "! The rank is not a valid UserRank!");
					return null;
				}
				int days = c.get(path + ".days") == null ? 1 : c.getInt(path + ".days");
				return new Reward(name, rank, days);
			}
			case NAMETAG:
				Nametag tag = Core.getNametagManager().getNametag(c.getString(path + ".nametag"));
				if (tag == null) {
					Core.error("Error while loading shop item " + name + "! The nametag is not a valid Nametag!");
					return null;
				}
				return new Reward(disp, tag);
			case PERMISSION:
				String permission = c.getString(path + ".permission");
				if (permission == null) {
					Core.error("Error while loading shop item " + name + "! The permission was not specified!");
					return null;
				}
				return new Reward(disp, permission, Reward.RewardType.PERMISSION);
			case RANK:
				UserRank rank = UserRank.getUserRankOrNull(c.getString(path + ".rank"));
				if (rank == null) {
					Core.error("Error while loading shop item " + name + "! The rank is not a valid UserRank!");
					return null;
				}
				return new Reward(name, rank);
			case COMMAND:
				String command = c.getString(path + ".command");
				if (command == null) {
					Core.error("Error while loading shop item " + name + "! The command was not specified!");
					return null;
				}
				return new Reward(disp, command, Reward.RewardType.COMMAND);
			case ACHIEVEMENT:
				Achievement achievement = Achievement.valueOf(c.get(path + ".achievement") == null ? null : c.getString(path + ".achievement").toUpperCase());
				if (achievement == null) {
					Core.error("Error while loading shop item " + name + "! The command was not specified!");
					return null;
				}
				return new Reward(disp, achievement);
			case VEHICLE:
				String vehicleIdentifier = c.getString(path + ".customName");
				return new Reward(disp, vehicleIdentifier, Reward.RewardType.VEHICLE);
			case WEAPON:
				String weaponName = c.getString(path + ".name");
				int stars = c.getInt(path + ".stars");
				short weaponSkinID = (short) c.getInt(path + ".weaponSkinId", 0);
				return new Reward(weaponName, disp, stars, weaponSkinID, Reward.RewardType.WEAPON);
			case SKIN:
				return new Reward(name, Reward.RewardType.SKIN, c.getString(path + ".customName"));
		}
		return null;
	}

	/**
	 * Load the token shop items from the token shop config.
	 */
	public void loadTokenShop() {
		YamlConfiguration c = Core.getSettings().getTokenShopConfig();
		this.shopItems.clear();
		if (c.get("shopItems") != null)
			for (String name : c.getConfigurationSection("shopItems").getKeys(false)) {
				try {
					int price = c.get("shopItems." + name + ".price") == null ? 1 : c.getInt("shopItems." + name + ".price");
					String item = c.get("shopItems." + name + ".item") == null ? "1" : c.getString("shopItems." + name + ".item");
					RewardPack pack = this.getRewardPack(c, name, "shopItems." + name);
					if (pack == null)
						Core.log("Error while loading RewardPack for tokenshop item: " + name);
					else
						this.shopItems.add(new ShopItem(name, price, item, pack));
				}
				catch (Exception e) {
					Core.error("Error while loading tokenshop item " + name);
					e.printStackTrace();
				}
			}
	}

	/**
	 * Get the voting link.
	 * 
	 * @return The full http link that users can click to go to vote.
	 */
	public String getVoteLink() {
		return this.voteLink;
	}

	/**
	 * Get the list of rewards that are possible for voting.
	 * 
	 * @return The list of rewards for voting.
	 */
	public List<VoteReward> getVoteRewards() {
		return this.voteRewards;
	}

	/**
	 * Get the vote rewards they are guaranteed to get.
	 * 
	 * @return The list of vote rewards they are guaranteed to receive.
	 */
	public List<VoteReward> getGuaranteedVoteRewards() {
		return this.voteRewards.stream().filter(v -> v.getChance() == 100).collect(Collectors.toList());
	}

	/**
	 * Get the rewards that are based off chance.
	 * 
	 * @return The possible rewards that they can get but must roll for the chance.
	 */
	public List<VoteReward> getChanceVoteRewards() {
		return this.voteRewards.stream().filter(v -> v.getChance() != 100).collect(Collectors.toList());
	}

	/**
	 * Get the daily rewards.
	 * 
	 * @return The list of daily rewards.
	 */
	public List<RewardPack> getDailyRewards() {
		return this.dailyRewards;
	}

	/**
	 * Get the lucky daily rewards.
	 * 
	 * @return The list of daily rewards that are considered lucky.
	 */
	public List<RewardPack> getLuckyDailyRewards() {
		return this.luckyDailyRewards;
	}

	/**
	 * Get the monthly rewards.
	 * 
	 * @return The monthly rewards mapped by key userrank.
	 */
	public Map<UserRank, List<RewardPack>> getMonthlyRewards() {
		return this.monthlyRewards;
	}

	/**
	 * Get the vote reward based off the given name.
	 * 
	 * @param name - the name to lookup
	 * 
	 * @return The vote reward with the given name, if one exists, otherwise {@code null}
	 */
	public VoteReward getVoteReward(String name) {
		return this.voteRewards.stream().filter(reward -> reward.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
	}

	/**
	 * Get the shop items for the token shop.
	 * 
	 * @return
	 */
	public List<ShopItem> getShopItems() {
		return this.shopItems;
	}

	/**
	 * Get the shop item based off the name.
	 * 
	 * @param s - the name to lookup
	 * 
	 * @return The shop item that was found with the given name, otherwise {@code null}.
	 */
	public ShopItem getShopItem(String s) {
		return this.shopItems.stream().filter(item -> ChatColor.stripColor(Utils.f(item.getName())).equalsIgnoreCase(ChatColor.stripColor(s))).findFirst().orElse(null);
	}

	/**
	 * Get the long representation of the timestamp of when the monthly reset occurred.
	 * 
	 * @return The long timestamp of when the last monthly reset was.
	 */
	public Long getLastMonthlyReset() {
		return this.lastMonthlyReset;
	}

	/**
	 * Set the last monthly reset to the given long representation.
	 * 
	 * @param time - the new last monthly reset.
	 */
	public void setLastMonthlyReset(Long time) {
		this.lastMonthlyReset = time;
		this.save(false);
	}

	/**
	 * Get the owning plugin.
	 * 
	 * @return The plugin that owns this manager.
	 */
	@Override
	public Plugin getPlugin() {
		return plugin;
	}

	/**
	 * Get the locally cached result of the top voters.
	 * 
	 * @return The top voters, in order, where element 1 is the first place top voter.
	 */
	public VoteDAO.VoteUser[] getTopVoters() {
		return topVoters;
	}
	
	/**
	 * Get the locally cached result of the last months top voters.
	 * 
	 * @return The last top voters, in order, where element 1 is the first place top voter.
	 */
	public VoteDAO.VoteUser[] getLastTopVoters() {
		return lastTopVoters;
	}

	/**
	 * Check whether or not the specified vote record and the specified voteSite
	 * are on cooldown.
	 * 
	 * @param vr - the vote record in question
	 * @param voteSite - the vote site that is attempting to be voted on
	 * @param userName - the name of the user doing the voting
	 * 
	 * @return {@code true} if the vote site is on cooldown, {@code false}
	 *         otherwise.
	 */
	protected boolean isVoteSiteCooldown(VoteRecord vr, VoteSite voteSite, String userName) {

		// ALL votes site cannot ensure valid votes
		// VoteSite.FOUR is a 12 hour cooldown
		// Ask Stephen for details, but there was a lot of data mining required

		// get the last voting time for THIS site
		long lastVote = vr.getSiteTimestamps().get(voteSite) != null ? vr.getSiteTimestamps().get(voteSite).getTime() : 0;
		long current = System.currentTimeMillis();

		// VoteSite.FOUR is 12 hours, so lets check for 8
		if (voteSite.equals(VoteSite.FOUR)) {

			// if less 8 hours between the votes
			if (current - lastVote < 28800000) {
				Core.log("[VoteManager] Player '" + userName + "' has voted on site=" + voteSite.getName() + " #" + voteSite.getId() + " within the last 8 hours. Disregarding vote. Difference between now and last vote (in msec) is: " + (current - lastVote));
				return true;
			}
		}
		// all others are 24 hours, but lets check for 16
		else {
			// if less 16 hours between the votes
			if (current - lastVote < 57600000) {
				Core.log("[VoteManager] Player '" + userName + "' has voted on site=" + voteSite.getName() + " #" + voteSite.getId() + " within the last 16 hours. Disregarding vote. Difference between now and last vote (in msec) is: " + (current - lastVote));
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Handle whether or not to increment or reset the voting streak.
	 * 
	 * @param conn - the database connection thread
	 * @param vr - the vote record
	 */
	protected void handleVoteStreakIncrement(Connection conn, VoteRecord vr) {

		// the uuid of the user
		UUID uuid = vr.getOwner();

		// have they voted EVER
		if (vr.getLastVoted().isPresent()) {

			// determine if this is a different day
			Timestamp lastVoted = vr.getLastVoted().get();
			Timestamp current = new Timestamp(System.currentTimeMillis());

			// don't count same days as vote streak increment
			boolean isDiff = TimeUtil.isDifferentDay(current, lastVoted);
			if (isDiff) {

				// if voted within timeframe
				int hoursDiff = TimeUtil.getDifferenceInHours(current, lastVoted);
				if (hoursDiff <= 48) {
					VoteDAO.incrementVoteStreak(conn, uuid);
				}
				else {

					int maxStreak = vr.getStreak();
					if (maxStreak > vr.getMaxStreak()) {
						VoteDAO.updateMaxStreak(conn, uuid, maxStreak);
					}

					// reset streak
					VoteDAO.resetVoteStreak(conn, uuid);
					VoteDAO.incrementVoteStreak(conn, uuid);
				}
			}
		}
		else {
			// if never voted before, increment vote streak
			VoteDAO.incrementVoteStreak(conn, uuid);
		}
	}
}
