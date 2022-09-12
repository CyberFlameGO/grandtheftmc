package net.grandtheftmc.core.listeners;

import java.sql.Connection;
import java.sql.SQLException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.dao.MutexDAO;
import net.grandtheftmc.core.database.mutex.common.LoadUserTask;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.transaction.state.user.UserStateTransaction;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.NMSUtil;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;

public class Join implements Listener {

	/**
	 * Listens in on player join events.
	 * <p>
	 * Note: This is LOW so that events can listen in BEFORE or AFTER this event
	 * is called since the player will be added to the container.
	 * </p>
	 * 
	 * @param event - the event
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {

		// grab event variables
		Player player = event.getPlayer();

		// create a new user
		User user = new User(player.getUniqueId(), player.getName());

		// NOTE: This is called on an async thread
		new LoadUserTask(Core.getInstance(), user) {

			@Override
			protected boolean onLoad() {
				try (Connection conn = BaseDatabase.getInstance().getConnection()) {

					// execute a data check to create new entries if needed
					user.dataCheck();

					// load the user
					user.onLoad(conn);
				}
				catch (SQLException e) {
					e.printStackTrace();
					return false;
				}

				return true;
			}

			@Override
			protected void onLoadFailure() {
				// Back to main thread
				Bukkit.getScheduler().runTask(getPlugin(), () -> {
					player.kickPlayer("[Core] Load failure; contact staff if this issue persists.");
					
					// TODO this is to reset mutex issues
					Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
						try (Connection conn = BaseDatabase.getInstance().getConnection()){
							MutexDAO.setUserMutex(conn, player.getUniqueId(), false);
						}
						catch(Exception e){
							e.printStackTrace();
						}
					});
				});
			}

			@Override
			protected void onLoadComplete() {

				// add to the container
				UserManager.getInstance().addUser(user);

				try (Connection conn = BaseDatabase.getInstance().getConnection()){
					// check for user state transactions
					UserStateTransaction.process(conn, getPlugin(), player, Core.getSettings().getType().getName(), Core.getSettings().getNumber());
				}
				catch (Exception e){
					e.printStackTrace();
				}
					
				// Back to main thread
				Bukkit.getScheduler().runTask(plugin, () -> {

					// can update name or other things on thread
					// like teleport them

					// make sure still online
					Player player = Bukkit.getPlayer(user.getUUID());
					if (player != null) {

						// bind bukkit player object to user object
						user.setPerms(player);

						// update nametag
						NametagManager.updateNametag(player);
						NametagManager.updateNametagsTo(player, user);

						// update display name
						user.updateDisplayName(player);

						if (user.dailyStreakExpired())
							player.sendMessage(Lang.REWARDS.f("&7Your Daily Reward Streak has expired! Claim it once every 24h to build up your streak!"));
						else if (user.canClaimDailyReward())
							player.sendMessage(Lang.REWARDS.f("&7Your Daily Reward is waiting for you! Claim it once every 24h to build up your streak!"));
						if (user.voteStreakExpired())
							player.sendMessage(Lang.VOTE.f("&7Your Vote Streak has expired! Vote once every 24h to build up your streak!"));
						else if (user.getVoteRecord().getStreak() > 0 && user.canVoteStreak())
							player.sendMessage(Lang.VOTE.f("&7Remember to vote to raise your Vote Streak! Vote on all &e5 &7sites for maximum rewards! You currently have a &a&l" + user.getDoubleVoteChance() + "&a%&7 chance to get a Double Vote&7!"));
						if (user.isSpecial() && user.canClaimMonthlyReward())
							player.sendMessage(Lang.REWARDS.f("&7Thank you for being a loyal supporter of " + Core.getSettings().getServer_GTM_shortName() + "! Your monthly reward of &e&l" + user.getUserRank().getMonthlyTokens() + " Tokens&7 is waiting for you!"));

						// if trial rank, let them know they can buy it
						UserRank trial = user.getTrialRank();
						if (trial != null) {
							player.sendMessage(Lang.RANKS.f("&7You are currently on the &a&lfree " + trial.getColoredNameBold() + "&7 trial! You can buy it permanently for &a$&l" + trial.getPrice() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7!"));
						}

						// if at least help op, set join time
						if (user.isRank(UserRank.HELPOP)) {
							user.setJoinTime(System.currentTimeMillis());
						}

						if (user.getPref(Pref.ANNOUNCEMENTS)) {

							// TODO this should be loaded during the loading of
							// the user
							// as its being called for each login anyways
							ServerUtil.runTaskAsync(() -> {
								if (UserDAO.isLastMonthsVoteWinner(player.getName())) {
									Bukkit.getScheduler().runTask(getPlugin(), () -> {
										player.sendMessage(Lang.VOTE.f("&7You have won store credit for being a top voter! Please visit the store to claim your prize!. (You can turn this notification off by toggling the annoucements pref)"));
									});
								}
							});
						}
					}
				});
			}
		};
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinMonitor(PlayerJoinEvent e) {

		// grab event variables
		Player p = e.getPlayer();

		// send join title
		NMSUtil.sendTabTitle(p, Core.getSettings().getDisplayName(), Utils.f("&aCheck out our site at &6&l" + Core.getSettings().getWebsiteLink() + "&a!"));
		// set join message
		e.setJoinMessage(Core.getSettings().getJoinLeaveMessagesEnabled() ? Utils.f(p.getDisplayName() + " &ejoined the game!") : null);

		// for all players online, show them this player
		e.getPlayer().getWorld().getEntitiesByClass(Player.class).forEach(target -> e.getPlayer().showPlayer(target));
	}
}
