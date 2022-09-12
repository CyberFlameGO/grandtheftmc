package net.grandtheftmc.gtm.listeners;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.achivements.Achievement;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.database.mutex.Mutexable;
import net.grandtheftmc.core.database.mutex.event.MutexLoadCompleteEvent;
import net.grandtheftmc.core.resourcepack.ResourcePack;
import net.grandtheftmc.core.resourcepack.ResourcePackEvent;
import net.grandtheftmc.core.resourcepack.ResourcePackManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.eventtag.PreTagEquipEvent;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.NMSVersion;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.database.dao.MutexDAO;
import net.grandtheftmc.gtm.database.mutex.common.LoadGTMUserTask;
import net.grandtheftmc.gtm.holidays.independenceday.IndependenceDay;
import net.grandtheftmc.gtm.items.Head;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class Join implements Listener {

	private final ResourcePackManager resourcePackManager;

	public Join(ResourcePackManager resourcePackManager) {
		this.resourcePackManager = resourcePackManager;
	}

	/**
	 * Listens in on player join events.
	 * <p>
	 * Note: This is LOW so that events can listen in BEFORE or AFTER this
	 * event is called since the player will be added to the container.
	 * </p>
	 * 
	 * @param event - the event
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onJoin(PlayerJoinEvent event) {

		// do not display join messages
		event.setJoinMessage(null);

		// grab event variables
		Player player = event.getPlayer();

		// General stuff
		GTMUtils.giveGameItems(player);
		player.setCollidable(false);
		player.setFlying(false);
		player.setWalkSpeed(0.2F);

//		long start = System.currentTimeMillis();
//
//		// create the GTMUser
//		GTMUser gtmUser = new GTMUser(player.getUniqueId(), player.getName());
//
//		// NOTE: This is called on an async thread
//		new LoadGTMUserTask(GTM.getInstance(), gtmUser) {
//
//			@Override
//			protected boolean onLoad() {
//				try (Connection conn = BaseDatabase.getInstance().getConnection()) {
//
//					// execute a data check to create new entries if needed
//					gtmUser.dataCheck();
//
//					// load the user
//					gtmUser.onLoad(conn);
//				}
//				catch (SQLException e) {
//					e.printStackTrace();
//					return false;
//				}
//
//				return true;
//			}
//
//			@Override
//			protected void onLoadFailure() {
//				// Back to main thread
//				Bukkit.getScheduler().runTask(getPlugin(), () -> {
//					player.kickPlayer("[GTM] Load failure; contact staff if this issue persists.");
//					
//					// TODO this is to reset mutex issues
//					Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
//						try (Connection conn = BaseDatabase.getInstance().getConnection()){
//							MutexDAO.setGTMUserMutex(conn, player.getUniqueId(), false);
//						}
//						catch(Exception e){
//							e.printStackTrace();
//						}
//					});
//				});
//			}
//
//			@Override
//			protected void onLoadComplete() {
//
//				// add to the container
//				GTMUserManager.getInstance().addUser(gtmUser);
//
//				// TODO check for user state transactions
//
//				// Back to main thread
//				Bukkit.getScheduler().runTask(plugin, () -> {
//
//					// can update name or other things on thread
//					// like teleport them
//
//					User coreUser = UserManager.getInstance().getUser(gtmUser.getUUID()).orElse(null);
//
//					// Timing related stuff
//					gtmUser.setJointime(System.currentTimeMillis());
//					if (gtmUser.getPlaytime() == 0L) {
//						int playOneTick = player.getStatistic(Statistic.PLAY_ONE_TICK);
//						gtmUser.setPlaytime((long) (playOneTick / 20));
//					}
//
//					if (player.getGameMode() == GameMode.SPECTATOR) {
//						player.getActivePotionEffects().clear();
//						player.setFoodLevel(20);
//						player.setGameMode(GameMode.ADVENTURE);
//						player.setFlying(false);
//						player.setFlySpeed(0.1F);
//					}
//
//					// if found core user
//					if (coreUser != null) {
//						GTMUtils.sendJoinMessage(player, coreUser);
//
//						if (!player.hasPlayedBefore()) {
//							Utils.broadcastExcept(player, Lang.GTM.f("&7Welcome " + coreUser.getColoredName(player) + "&7 to &7&l" + Core.getSettings().getServer_GTM_name() + "&r!"));
//							player.teleport(GTM.getWarpManager().getTutorialSpawn().getLocation());
//							player.spigot().sendMessage(new ComponentBuilder(Lang.TUTORIALS.s()).append("Welcome newcomer! Would you like to go through a simple tutorial?").color(ChatColor.GRAY).append(" [ACCEPT] ").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tutorial")).create());
//						}
//						else {
//							player.teleport(gtmUser.isArrested() ? GTM.getWarpManager().getJail().getLocation() : GTM.getWarpManager().getSpawn().getLocation());
//						}
//
//						GTM.getLottery().joinCheck(player, coreUser, gtmUser);
//					}
//					
//					GTMUtils.updateBoard(player, gtmUser);
//
//					Head head = GTM.getShopManager().getHead(player.getName());
//					if (head != null && head.getBidderUUID() != null && !Objects.equals(head.getBidderUUID(), player.getUniqueId()) && !head.isDone()) {
//						player.sendMessage(Lang.HEAD_AUCTION.f("&7Your head is currently being auctioned by &a&l" + head.getSellerName() + "&7! The last bidder was &a&l" + head.getBidderName() + "&7 for &a$&l" + head.getBid() + "&7!"));
//					}
//
//					// Achievement, idk why it's still here.
//					if (player.getUniqueId().toString().equals("0e4a6028-3d9a-4a2e-9797-eb1ddcb0aca9")) {
//						Bukkit.getOnlinePlayers().forEach(target -> {
//							User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
//							targetUser.addAchievement(Achievement.Witness);
//						});
//					}
//					
//					// TODO this isn't fired correctly all the time
//					try{
//						gtmUser.checkAchievements();
//					}
//					catch(Exception e){
//						// TODO fix
//					}
//
//					IndependenceDay independenceDay = GTM.getHolidayManager().getIndependenceDay();
//					if (independenceDay != null) {
//						if (independenceDay.isActive()) {
//							independenceDay.getBossBar().addPlayer(player.getUniqueId());
//						}
//					}
//
//					// attempt to send resource pack
//					attemptToSendPack(player);
//
//					// Log to console, version and time to join from [PostLogin
//					// ->
//					// JoinEvent]
//					NMSVersion ver = NMSVersion.getVersion(player);
//					Bukkit.getConsoleSender().sendMessage(C.LIGHT_PURPLE + player.getName() + " Logged in. (" + C.WHITE + "Version: " + C.GREEN + ver.name() + C.LIGHT_PURPLE + ") [" + C.YELLOW + (System.currentTimeMillis() - start) + "ms" + C.LIGHT_PURPLE + "]");
//				});
//			}
//		};
	}
	
	/**
	 * Listens in on mutex load complete events.
	 * <p>
	 * This can be fired when a mutex load event is finished loading, typically something like a core User.
	 * 
	 * @param event - the event to listen on
	 */
	@EventHandler
	public void onMutexLoadComplete(MutexLoadCompleteEvent event){
		
		// grab event variables
		Mutexable mutexable = event.getMutexable();
		if (!(mutexable instanceof User)){
			return;
		}
		
		// conver to user and grab player
		User user = (User) mutexable;
		Player player = Bukkit.getPlayer(user.getUUID());
		if (player == null){
			return;
		}
		
		long start = System.currentTimeMillis();

		// create the GTMUser
		GTMUser gtmUser = new GTMUser(player.getUniqueId(), player.getName());

		// NOTE: This is called on an async thread
		new LoadGTMUserTask(GTM.getInstance(), gtmUser) {

			@Override
			protected boolean onLoad() {
				try (Connection conn = BaseDatabase.getInstance().getConnection()) {

					// execute a data check to create new entries if needed
					gtmUser.dataCheck();

					// load the user
					gtmUser.onLoad(conn);
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
					player.kickPlayer("[GTM] Load failure; contact staff if this issue persists.");
					
					// TODO this is to reset mutex issues
					Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
						try (Connection conn = BaseDatabase.getInstance().getConnection()){
							MutexDAO.setGTMUserMutex(conn, player.getUniqueId(), false);
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
				GTMUserManager.getInstance().addUser(gtmUser);

				// TODO check for user state transactions

				// Back to main thread
				Bukkit.getScheduler().runTask(plugin, () -> {

					// can update name or other things on thread
					// like teleport them

					User coreUser = UserManager.getInstance().getUser(gtmUser.getUUID()).orElse(null);

					// Timing related stuff
					gtmUser.setJointime(System.currentTimeMillis());
					if (gtmUser.getPlaytime() == 0L) {
						int playOneTick = player.getStatistic(Statistic.PLAY_ONE_TICK);
						gtmUser.setPlaytime((long) (playOneTick / 20));
					}

					if (player.getGameMode() == GameMode.SPECTATOR) {
						player.getActivePotionEffects().clear();
						player.setFoodLevel(20);
						player.setGameMode(GameMode.ADVENTURE);
						player.setFlying(false);
						player.setFlySpeed(0.1F);
					}

					// if found core user
					if (coreUser != null) {
						GTMUtils.sendJoinMessage(player, coreUser);

						if (!player.hasPlayedBefore()) {
							Utils.broadcastExcept(player, Lang.GTM.f("&7Welcome " + coreUser.getColoredName(player) + "&7 to &7&l" + Core.getSettings().getServer_GTM_name() + "&r!"));
							player.teleport(GTM.getWarpManager().getTutorialSpawn().getLocation());
							player.spigot().sendMessage(new ComponentBuilder(Lang.TUTORIALS.s()).append("Welcome newcomer! Would you like to go through a simple tutorial?").color(ChatColor.GRAY).append(" [ACCEPT] ").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tutorial")).create());
						}
						else {
							player.teleport(gtmUser.isArrested() ? GTM.getWarpManager().getJail().getLocation() : GTM.getWarpManager().getSpawn().getLocation());
						}

						GTM.getLottery().joinCheck(player, coreUser, gtmUser);
					}
					
					GTMUtils.updateBoard(player, gtmUser);

					Head head = GTM.getShopManager().getHead(player.getName());
					if (head != null && head.getBidderUUID() != null && !Objects.equals(head.getBidderUUID(), player.getUniqueId()) && !head.isDone()) {
						player.sendMessage(Lang.HEAD_AUCTION.f("&7Your head is currently being auctioned by &a&l" + head.getSellerName() + "&7! The last bidder was &a&l" + head.getBidderName() + "&7 for &a$&l" + head.getBid() + "&7!"));
					}

					// Achievement, idk why it's still here.
					if (player.getUniqueId().toString().equals("0e4a6028-3d9a-4a2e-9797-eb1ddcb0aca9")) {
						Bukkit.getOnlinePlayers().forEach(target -> {
							User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
							targetUser.addAchievement(Achievement.Witness);
						});
					}
					
					// TODO this isn't fired correctly all the time
					try{
						gtmUser.checkAchievements();
					}
					catch(Exception e){
						// TODO fix
					}

					IndependenceDay independenceDay = GTM.getHolidayManager().getIndependenceDay();
					if (independenceDay != null) {
						if (independenceDay.isActive()) {
							independenceDay.getBossBar().addPlayer(player.getUniqueId());
						}
					}

					// attempt to send resource pack
					attemptToSendPack(player);

					// Log to console, version and time to join from [PostLogin
					// ->
					// JoinEvent]
					NMSVersion ver = NMSVersion.getVersion(player);
					Bukkit.getConsoleSender().sendMessage(C.LIGHT_PURPLE + player.getName() + " Logged in. (" + C.WHITE + "Version: " + C.GREEN + ver.name() + C.LIGHT_PURPLE + ") [" + C.YELLOW + (System.currentTimeMillis() - start) + "ms" + C.LIGHT_PURPLE + "]");
				});
			}
		};
		
	}

	/**
	 * Listens in on ResourcePackEvent.
	 * <p>
	 * This event is called after the player receives a PacketType of
	 * RESOURCE_PACK_STATUS.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	protected final void onResourcePack(ResourcePackEvent event) {
		Player player = event.getPlayer();
		if (player == null)
			return;

//        player.sendTitle(new Title(Utils.f("&6&lWelcome to &oGrand Theft Minecart&6&l!"), "", 60, 30, 40));
	}

	/**
	 * Stop EventTags from being set/changed when a JobMode is enabled. This is
	 * a fix for 'COP' & 'HITMAN' nametags being overridden.
	 */
	@EventHandler
	protected final void onTagChange(PreTagEquipEvent event) {
		if (event.getPlayer() == null){
			return;
		}

		GTMUser gameUser = GTM.getUserManager().getUser(event.getPlayer().getUniqueId()).orElse(null);
		
		if (gameUser != null){
			if (gameUser.getJobMode() == null || gameUser.getJobMode() == JobMode.CRIMINAL){
				return;
			}
		}

		event.setCancelled(true);
	}

	/**
	 * Attempt to send the pack to the player.
	 * 
	 * @param player - the player that needs the resource pack.
	 */
	private void attemptToSendPack(Player player) {

		//Send pack here in-case exceptions are thrown
        NMSVersion ver = NMSVersion.getVersion(player);
        
        // according to minecraft wiki on resource packs
        // Requires 1 for 1.6-1.9, 2 for 1.9 and 1.10, 3 for 1.11 and 1.12, and 4 for 1.13.
        
        // if its 1.13.x or higher
        if (ver.getProtocol() >= NMSVersion.MC_1_13.getProtocol()) {
        	Core.log("[GTM][Join] Texture pack code #4 (1.13.x AND ABOVE) due to NMS protocol version=" + ver.name());
       
        	ResourcePack pack = GTM.getResourcePackManager().getResourcePack(NMSVersion.MC_1_13);
            sendPackToPlayer(player, pack, resourcePackManager);
        }
        // if its 1.11.x or 1.12.x
        else if (ver.getProtocol() >= NMSVersion.MC_1_11.getProtocol() && ver.getProtocol() <= NMSVersion.MC_1_12_2.getProtocol()) {
        	Core.log("[GTM][Join] Texture pack code #3 (1.11.x TO 1.12.x) due to NMS protocol version=" + ver.name());
            
            ResourcePack pack = GTM.getResourcePackManager().getResourcePack(NMSVersion.MC_1_11);
            sendPackToPlayer(player, pack, resourcePackManager);
        } 
        // if its 1.9.x or 1.10.x
        else if (ver.getProtocol() >= NMSVersion.MC_1_9.getProtocol() && ver.getProtocol() <= NMSVersion.MC_1_10.getProtocol()) {
        	Core.log("[GTM][Join] Texture pack code #2 (1.9.x TO 1.10.x) due to NMS protocol version=" + ver.name());
            
        	ResourcePack pack = GTM.getResourcePackManager().getResourcePack(NMSVersion.MC_1_10);
            sendPackToPlayer(player, pack, resourcePackManager);
        }
        else if (ver.getProtocol() >= NMSVersion.MC_1_8.getProtocol() && ver.getProtocol() < NMSVersion.MC_1_9.getProtocol()) {
        	Core.log("[GTM][Join] Texture pack code #1 (1.6.x TO 1.9.x) due to NMS protocol version=" + ver.name());
       
        	// TODO Note: This is not supported by us.
        }
        else {
        	Core.log("[GTM][Join] Unknown texture pack code for NMS version=" + ver.name() + ", protocol=" + ver.getProtocol());
        }
	}

	/**
	 * Attempt to send the pack to the player using a runnable, and attempt to
	 * re-apply.
	 * 
	 * @param player - the player getting the pack
	 * @param pack - the resource pack to send
	 * @param resourcePackManager - the manager of the resource packs
	 */
	private void sendPackToPlayer(Player player, ResourcePack pack, ResourcePackManager resourcePackManager) {

		// grab the uuid of the player
		UUID uuid = player.getUniqueId();

		new BukkitRunnable() {
			@Override
			public void run() {
				Player p = Bukkit.getPlayer(uuid);
				if (p == null || !p.isOnline()) {
					return;
				}

				if (!p.isValid()) {
					// TODO note this could possibly be recursively forever
					// as player might log in and log out in 2 seconds.
					sendPackToPlayer(p, pack, resourcePackManager);
					return;
				}

				if (pack != null) {

					// setting player resource pack sends
					// PacketType.Play.Client.RESOURCE_PACK_STATUS
					// byte[] hash =
					// BaseEncoding.base16().lowerCase().decode(pack.getHash().toLowerCase());
					p.setResourcePack(pack.getPack());
				}
			}
		}.runTaskLater(GTM.getInstance(), 20 * 8);
	}
}
