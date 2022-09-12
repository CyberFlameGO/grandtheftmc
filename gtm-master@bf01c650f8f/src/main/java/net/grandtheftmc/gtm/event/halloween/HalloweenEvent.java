package net.grandtheftmc.gtm.event.halloween;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.event.BaseEvent;
import net.grandtheftmc.core.event.EventType;
import net.grandtheftmc.core.gui.ConfirmationMenu;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.houses.PremiumHouse;

public class HalloweenEvent extends BaseEvent implements Listener {

	/** Random instance */
	private static final Random RANDOM = new Random();
	/** The cost to play trick or treat, in tokens */
	public static final int TRICK_COST = 5;

	// spawning variables
	/** How often to check to spawn monsters */
	private long spawnTick = 20L * 1;
	/** The maximum amount of monsters for this event */
	private int maxMonsters = 100;
	/** The spawn variance, how far from the player can we spawn monsters */
	private int spawnVariance = 15;
	/** Maps UUID to the entity */
	private Map<UUID, Entity> uuidToEntity;
	/** Cache of what users had entities spawned near */
	private Set<UUID> playersTargeted;
	/** The last timestamp when we cleared the players target */
	private long targetClearTimestamp;
	/** The spawn task for the entities */
	private BukkitTask spawnTask;
	/** The world for this event */
	private World world;

	/** Maps UUID to redeemed houses */
	private Map<UUID, Set<Integer>> uuidToRedeemed;

	/** The boss bar for when the event is going on */
	private BossBar bossBar;
	/** The total amount of tokens the event has earned in a pot */
	private int tokens;

	/**
	 * Construct a new HalloweenEvent.
	 * 
	 * @param plugin - the owning plugin
	 * @param startTime - the time, in millis since epoch, that this event
	 *            starts
	 * @param endTime - the time, in millis since epoch, that this event ends
	 */
	public HalloweenEvent(Plugin plugin, long startTime, long endTime) {
		super(plugin, EventType.HALLOWEEN.getId(), startTime, endTime);
		this.uuidToEntity = new HashMap<>();
		this.playersTargeted = new HashSet<>();
		this.targetClearTimestamp = System.currentTimeMillis();
		this.world = Bukkit.getWorld("minesantos");
		this.uuidToRedeemed = new HashMap<>();
		this.bossBar = Bukkit.createBossBar("Halloween", BarColor.PINK, BarStyle.SOLID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onInit() {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onStart() {

		// register as plugin
		getPlugin().getServer().getPluginManager().registerEvents(this, getPlugin());

		// set the world time to night
		if (world != null) {
			world.setTime(12000);
		}

		for (Player player : Bukkit.getOnlinePlayers()) {

			// play sound and title
			player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 0.3f, 0f);
			NMSTitle.sendTitle(player, ChatColor.GOLD + "Halloween!", ChatColor.WHITE + "Spooky...", 10, 20 * 5, 10);
			bossBar.addPlayer(player);
		}

		// start the spawn task
		this.spawnTask = getSpawnTask();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEnd() {

		// end the spawn task
		if (this.spawnTask != null) {
			this.spawnTask.cancel();
		}

		// remove all from boss bar
		bossBar.removeAll();

		// remove all spawned skeletons
		removeSpawnedEntities();

		// unregister this listener
		HandlerList.unregisterAll(this);
	}

	/**
	 * Listens in on entity spawn events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent event) {

	}

	/**
	 * Listens in on entity death events.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event) {

		LivingEntity le = event.getEntity();

		// if custom entity
		if (uuidToEntity.containsKey(le.getUniqueId())) {
			uuidToEntity.remove(le.getUniqueId());

			// clear drops
			event.getDrops().clear();
			event.setDroppedExp(0);

			// always drop roofied chocolate
			GameItem rc = GTM.getItemManager().getItem("roofied_chocolate");
			if (rc != null) {
				event.getDrops().add(rc.getItem());
			}

			int chance = RANDOM.nextInt(100);
			if (chance < 5) {

				// 5% chance to drop candy bag
				GameItem cb = GTM.getItemManager().getItem("candy_bag");
				if (cb != null) {
					event.getDrops().add(cb.getItem());
				}
			}
		}
	}

	/**
	 * Listens in on entity combust event.
	 * <p>
	 * This stops entities from getting lit on fire.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onEntityCombust(EntityCombustEvent event) {

		// grab entity
		Entity entity = event.getEntity();

		// stop custom entities from lit on fire
		if (uuidToEntity.containsKey(entity.getUniqueId())) {
			event.setCancelled(true);
		}
	}

	/**
	 * Listens in on entity damage events for fire tick damage.
	 * <p>
	 * This stops entities from getting lit on fire.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onFireTick(EntityDamageEvent event) {

		// grab entity
		Entity entity = event.getEntity();

		// stop custom entities from lit on fire
		if (uuidToEntity.containsKey(entity.getUniqueId())) {
			if (event.getCause() == DamageCause.FIRE_TICK) {
				event.getEntity().setFireTicks(0);
			}
		}
	}

	/**
	 * Listens in on player join.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {

		// grab the player
		Player player = event.getPlayer();

		// play sound and title
		player.playSound(player.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 0.3f, 0f);
		NMSTitle.sendTitle(player, ChatColor.GOLD + "Halloween!", ChatColor.WHITE + "Spooky...", 10, 20 * 5, 10);
		bossBar.addPlayer(player);

		// async fetch for house information
		Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {

			Set<Integer> houses = new HashSet<>();
			try (Connection conn = BaseDatabase.getInstance().getConnection()) {
				houses.addAll(HalloweenDAO.getRedeemedHouses(conn, player.getUniqueId()));
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			// sync to get back on thread
			Bukkit.getScheduler().runTask(getPlugin(), () -> {
				uuidToRedeemed.put(player.getUniqueId(), houses);
			});
		});
	}

	/**
	 * Listens in on player quits.
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {

		// grab event variables
		Player player = event.getPlayer();

		// remove boss bar from playing
		bossBar.removePlayer(player);

		if (uuidToRedeemed.containsKey(player.getUniqueId())) {
			uuidToRedeemed.remove(player.getUniqueId());
		}
	}

	/**
	 * Listens in on the player interact events.
	 * 
	 * @param event - the event
	 */
	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
	public void onPlayerInteract(PlayerInteractEvent event) {

		// grab the event variables
		Player p = event.getPlayer();
		Action a = event.getAction();
		ItemStack is = event.getItem();

		// if right click block
		if (a == Action.RIGHT_CLICK_BLOCK && event.getHand() == EquipmentSlot.HAND) {

			// make sure they have a candy bag
			GameItem gi = GTM.getItemManager().getItem(is);
			if (gi != null && gi.getName().equalsIgnoreCase("candy_bag")) {

				if (isDay()) {
					p.sendMessage(ChatColor.RED + "You can only " + ChatColor.GOLD + "Trick or Treat" + ChatColor.RED + " at night.");
					return;
				}

				Block block = event.getClickedBlock();
				if (block != null) {

					// grab block state
					BlockState state = block.getState();

					// grab the block underneath
					Block underneath = block.getRelative(BlockFace.DOWN);
					if (underneath.getType() == Material.IRON_DOOR_BLOCK) {
						state = underneath.getState();
					}

					// see if this is a premium house
					Object[] data = Houses.getHousesManager().getHouseAndDoor(state.getLocation());
					if (data != null && data[0] instanceof PremiumHouse) {

						// deny interact events
						event.setCancelled(true);
						event.setUseInteractedBlock(Result.DENY);
						event.setUseItemInHand(Result.DENY);

						// make sure they have tokens
						User user = Core.getUserManager().getLoadedUser(p.getUniqueId());
						if (user != null) {
							if (user.getTokens() > TRICK_COST) {

								// grab premium house id
								PremiumHouse premHouse = (PremiumHouse) data[0];
								int houseID = premHouse.getId();

								// get list of redeemed houses for the user
								Set<Integer> redeemed = null;
								if (uuidToRedeemed.containsKey(p.getUniqueId())) {
									redeemed = uuidToRedeemed.get(p.getUniqueId());
								}
								else {
									redeemed = new HashSet<>();
								}
								uuidToRedeemed.put(p.getUniqueId(), redeemed);

								if (!redeemed.contains(houseID)) {

									// confirm menu
									ConfirmationMenu confirm = new ConfirmationMenu(getPlugin(), getInfoItemStack()) {

										@Override
										public void onConfirm(InventoryClickEvent e, Player p) {

											// do this check again
											if (user.getTokens() > TRICK_COST) {

												if (uuidToRedeemed.containsKey(p.getUniqueId())) {
													Set<Integer> redeemed = uuidToRedeemed.get(p.getUniqueId());

													redeemed.add(houseID);
													uuidToRedeemed.put(p.getUniqueId(), redeemed);

													p.sendMessage(ChatColor.WHITE + "--- " + ChatColor.GOLD + "Trick or Treat" + ChatColor.WHITE + "---");
													p.sendMessage(ChatColor.WHITE + "Trick or Treating uses " + ChatColor.GOLD + TRICK_COST + " tokens" + ChatColor.WHITE + ".");
													p.sendMessage(ChatColor.WHITE + "You'll have a chance to win rewards plus a large jackpot of tokens!");
													p.sendMessage(ChatColor.RED.toString() + ChatColor.BOLD + "-" + TRICK_COST + " Tokens");

													// subtract user's tokens
													// and add to global
													// pot
													user.setTokens(user.getTokens() - TRICK_COST);
													setTokens(getTokens() + TRICK_COST);

													// don't let them hit the
													// door again
													Bukkit.getScheduler().runTaskAsynchronously(getPlugin(), () -> {
														try (Connection conn = BaseDatabase.getInstance().getConnection()) {
															HalloweenDAO.createRedeemedHouse(conn, p.getUniqueId(), houseID);
														}
														catch (Exception exc) {
															exc.printStackTrace();
														}
													});

													// execute trick or treat
													new TrickOrTreatTask(getPlugin(), p, block.getLocation());
												}
											}
										}

										@Override
										public void onDeny(InventoryClickEvent e, Player p) {

										}
									};
									confirm.open(p);
								}
								else {
									p.sendMessage(ChatColor.RED + "You have already played " + ChatColor.GOLD + "Trick or Treat" + ChatColor.RED + " at this house.");
								}
							}
							else {
								p.sendMessage(ChatColor.RED + "You do not have enough tokens to Trick or Treat! You need " + ChatColor.WHITE + TRICK_COST + ChatColor.RED + " tokens to play!");
							}
						}
					}
					else {
						p.sendMessage(ChatColor.RED + "You can only Trick or Treat at Premium Houses!");
					}
				}
			}
		}
	}

	/**
	 * Get the spawn task that handles spawning entities.
	 * 
	 * @return The spawn task that handles the entities spawning.
	 */
	protected BukkitTask getSpawnTask() {

		return Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {

			// change time cycle
			world.setTime(world.getTime() + 20);

			if (isDay()) {
				removeSpawnedEntities();
				bossBar.removeAll();
				return;
			}
			else {
				bossBar.addFlag(BarFlag.CREATE_FOG);
				bossBar.addFlag(BarFlag.DARKEN_SKY);

				Bukkit.getOnlinePlayers().forEach(p -> {
					bossBar.addPlayer(p);
				});
			}

			updateBossBar();

			// clear targets event 30 secs
			if (System.currentTimeMillis() - targetClearTimestamp > 30 * 1000) {
				targetClearTimestamp = System.currentTimeMillis();
				playersTargeted.clear();
			}

			// clamp size
			if (uuidToEntity.size() < maxMonsters) {

				// attempt to spawn around a player
				for (Player p : Bukkit.getOnlinePlayers()) {

					// skip targetted players
					if (playersTargeted.contains(p.getUniqueId())) {
						continue;
					}

					// must be in minesantos
					if (p.getWorld().getName().equalsIgnoreCase("minesantos")) {

						Location spawnLoc = getRandomSpawnPoint(p.getLocation(), spawnVariance);
						if (spawnLoc != null) {

							Skeleton skeleton = (Skeleton) spawnLoc.getWorld().spawnEntity(spawnLoc, EntityType.SKELETON);
							skeleton.setMaxHealth(30);
							skeleton.setHealth(30);

							GameItem cr = GTM.getItemManager().getItem("baseballbat");
							if (cr != null) {
								skeleton.getEquipment().setItemInHand(cr.getItem());
							}

							// add to uuid mapping
							uuidToEntity.put(skeleton.getUniqueId(), skeleton);

							// cache as a target, will clear every so interval
							playersTargeted.add(p.getUniqueId());
						}
					}
				}
			}

		}, 0L, spawnTick);
	}

	/**
	 * Remove the spawned entities from this event.
	 */
	protected void removeSpawnedEntities() {

		for (World world : Bukkit.getWorlds()) {

			for (Entity entity : world.getEntities()) {

				// if this is a custom entity
				if (uuidToEntity.containsKey(entity.getUniqueId())) {
					uuidToEntity.remove(entity.getUniqueId());

					// set health to 0
					if (entity instanceof LivingEntity) {
						LivingEntity le = (LivingEntity) entity;
						le.setHealth(0);
					}

					// remove the entity
					entity.remove();
				}
			}
		}

		// clean up the entities if still exist
		uuidToEntity.clear();
	}

	/**
	 * Get a random spawn point around a specific location.
	 * 
	 * @param loc - the location to get a spawn point around
	 * @param variance - the variance, or the radius from the location
	 * 
	 * @return The location that is a random spawn point candidate.
	 */
	protected Location getRandomSpawnPoint(Location loc, int variance) {

		// assume the location is somewhat near the ground
		Location initial = loc.clone();

		// add random variance around location
		// we roll between 0 and 1, and then offset by 0.5
		// then we multiple that number (say its -0.3) by 2 times the variance
		// so we get a negative x of variance length
		initial.setX(initial.getX() + ((RANDOM.nextDouble() - 0.5) * 2 * variance));
		initial.setZ(initial.getZ() + ((RANDOM.nextDouble() - 0.5) * 2 * variance));

		// add 15 blocks to account for being underground or in a building
		initial.add(0, 15, 0);

		// search for at least 25 blocks down
		Location spawnLoc = findFloor(initial, 25);
		if (spawnLoc != null) {

			// get 1.25 block up from the floor, so they can "drop" a bit
			spawnLoc.add(0, 1.25, 0);

			// attempt to find a ceiling at least 10 blocks up
			Location ceiling = findCeiling(spawnLoc, 10);
			if (ceiling == null) {

				// no ceiling is good, we don't want to spawn in buildings
				return spawnLoc;
			}
		}

		return null;
	}

	/**
	 * Given a location, attempt to find the floor, but only iterate up to x
	 * blocks down.
	 * 
	 * @param loc - the location to find the floor for
	 * @param maxSearch - the max number of blocks we iterate straight down
	 * 
	 * @return The location of the floor, if one was found, otherwise null.
	 */
	protected Location findFloor(Location loc, int maxSearch) {

		Location initial = loc.clone();

		int tries = maxSearch;
		// while we're touching air
		while (initial.getBlock().getType() == Material.AIR) {

			// if no more attempts to find
			if (tries <= 0) {
				break;
			}
			// go down one
			initial.add(0, -1, 0);

			tries--;
		}

		// if it's still air, return null
		if (initial.getBlock().getType() == Material.AIR) {
			return null;
		}

		return initial;
	}

	/**
	 * Given a location, attempt to find the ceiling, but only iterate up to x
	 * blocks down.
	 * 
	 * @param loc - the location to find the ceiling for
	 * @param maxSearch - the max number of blocks we iterate straight up
	 * 
	 * @return The location of the ceiling, if one was found, otherwise null.
	 */
	protected Location findCeiling(Location loc, int maxSearch) {

		Location initial = loc.clone();

		int tries = maxSearch;
		// while we're touching air
		while (initial.getBlock().getType() == Material.AIR) {

			// if no more attempts to find
			if (tries <= 0) {
				break;
			}
			// go up one
			initial.add(0, 1, 0);

			tries--;
		}

		// if it's still air, return null
		if (initial.getBlock().getType() == Material.AIR) {
			return null;
		}

		return initial;
	}

	/**
	 * Updates the boss bar.
	 */
	protected void updateBossBar() {

		// how many seconds left until over
		int secondsLeft = (int) ((getEndTime() - System.currentTimeMillis()) / 1000.0);

		int day = (int) TimeUnit.SECONDS.toDays(secondsLeft);
		long hours = TimeUnit.SECONDS.toHours(secondsLeft) - (day * 24);
		long minute = TimeUnit.SECONDS.toMinutes(secondsLeft) - (TimeUnit.SECONDS.toHours(secondsLeft) * 60);
		long second = TimeUnit.SECONDS.toSeconds(secondsLeft) - (TimeUnit.SECONDS.toMinutes(secondsLeft) * 60);

		String timeLeft = day + "d " + hours + "h " + minute + "m";
		String title = "" + ChatColor.LIGHT_PURPLE + ChatColor.BOLD + "Halloween Event! " + ChatColor.GOLD + ChatColor.BOLD + timeLeft;

		double start = System.currentTimeMillis() - getStartTime();
		double end = getEndTime() - getStartTime();

		// this gives us how through we are
		double through = start / end;
		double progress = 1 - through;

		bossBar.setTitle(title);
		bossBar.setProgress(progress);
	}

	/**
	 * Get the info itemstack for the confirm menu.
	 * 
	 * @return The generic info item stack.
	 */
	private static ItemStack getInfoItemStack() {
		ItemStack is = new ItemStack(Material.SIGN);
		ItemMeta im = is.getItemMeta();
		List<String> lore = new ArrayList<>();
		lore.add("");
		lore.add(ChatColor.GRAY + "Trick or Treat at this house?");
		lore.add("");
		lore.add(ChatColor.GRAY + "Cost: " + ChatColor.GOLD + TRICK_COST + " tokens");
		im.setLore(lore);
		is.setItemMeta(im);
		return is;
	}

	/**
	 * Get the total amount of tokens that this halloween event currently has.
	 * <p>
	 * This pot changes based off of trick or treat.
	 * 
	 * @return The total amount of tokens that this halloween event currently
	 *         has.
	 */
	public int getTokens() {
		return tokens;
	}

	/**
	 * Set the total amount of tokens that this halloween event currently has.
	 * 
	 * @param tokens - the new amount tokens
	 */
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	/**
	 * Get whether or not it is day in the main halloween world.
	 * 
	 * @return Whether or not it is day.
	 */
	public boolean isDay() {
		if (world.getTime() > 0 && world.getTime() < 12000) {
			return true;
		}

		return false;
	}
}
