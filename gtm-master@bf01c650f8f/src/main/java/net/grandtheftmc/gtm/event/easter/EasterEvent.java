package net.grandtheftmc.gtm.event.easter;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import de.slikey.effectlib.util.ParticleEffect;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.event.BaseEvent;
import net.grandtheftmc.core.event.EventType;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.util.Component;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public final class EasterEvent extends BaseEvent implements Listener {

	final HashMap<UUID, EasterPlayerData> playerCache = Maps.newHashMap();
	final Set<EasterEgg> eggs = Sets.newHashSet();
	private final Set<Chunk> chunks = Sets.newHashSet();
	final HashMap<Integer, EasterEgg> eggEntityIds = Maps.newHashMap();

	private final String[] eggSkins = {
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjEzYTI3OGFlOTkwMmY3YzE1YmYzOTY5OWM2YTE0MjYxNDI1Y2NhZmVkYWIyNGZhNmE4NTljNDE1YTMwNWQ0YSJ9fX0=",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjVlZDNhZmJkNjk2NDk5ZGVkY2NmMmRmZDY2NWZkY2VmMDQyOWE4OTk0MjhiNmZkODczNWNiZGNiMjViYjgifX19",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDYwZjY5NTU1N2YxYzEzZTNlNjJlNWNhMDVmYWY0YTczMGRiNzcyYzhmYWIxZjA3MmE3MzI5N2YyMCJ9fX0=",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvOWQ1MGZlMTVhMjEyODY0OGM3YmQxYTk4NWMzMjBiYWRiZmRmNmViYjQ2ODJjYWM0OTEyOTA5NjIwY2NmIn19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2MzNTI4NzA0YTI3MjkxODEzZDk4OWRjMDFmYmI2ODg0YzE2OGI0ZDA5YTkxMzQ2OTE4OGU0N2Y0MGMyZDAifX19",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmE4YWU0NGU4ZDUwM2NjYjhlMGJmNTEzYTI1YjllOGI2MTVlYzkwNGM0ZjgyOTNkOTk1ZjE0Y2Q4NjllZTEifX19",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzg2MDg5Y2Q5NjhmYzY1YTkxNmY2OGIyNGU3YzYzMzcwZDA3Y2JmNjMyZDRhOWQxMmUzYzI4YjlkYTM4In19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjIyYjMzMTliNTk5MzQ4MTcyMzAzNThjZjMzZDAzOGM5ODNjOWI2NzdhZDE1YjBhM2Y1ZjFmZTNiNWMxNTdiIn19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzEyNThmOTg5ZTAzMjNiZmUzODJlOGViMjIzODQ5YjRkY2RiMGRlODg3MjczMTQxNWMxYzliYmI0YTgyOTE4In19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWQ5MDlkM2U5ZGFmZGI2OGU1NDdhODNhNzQ5NDg2YmFjNmVjZTA5YjhmNjA0ZThmODY1ODE2ZjVhN2E5MjE3In19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTk1ZWJjMGYxMTUyYmFiZjJhNTU1YWFjNjc0ZTk2OGUyZjEyOTU4Nzc3NDFmMmM4ZjhiZWJjYjE5NTI4YyJ9fX0=",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjJlODdmODgzMzg2NmExNjcyZTFmMmFhODk1NjYxODBmYzkxZmZiZTRiYzg2Mzc5M2ViNWU2MTYyNjQ4NSJ9fX0=",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmFlZWNhMzYxNTczNGYyZGRlNjUyYzc1YzhmYjUyMGVmZjNiYTQwNWM2NTVhNWZmM2E4N2FiYzY4Yzk4MWIyIn19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZWE1ZWFiYjA5NTc3NTljZjYxODRhMjk0MWNmZGEwMWI5MWZkMjFmMGQxNTIxMzM1YmYxNDMyZDczYWViZWFiIn19fQ==",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA5NTU4N2FhNTY1ODM3N2VmYmU1ZmY3ZGFmNTRmZWZmZTZkNWY2YmRhYmEzZGMxOWVlN2QyZjE4NjI2MjQ3ZCJ9fX0=",
			"eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI4ODRkNjFmMjM1MjM1MDQ3NDgzYWM0YmE0Y2U1Mjg2OTFlNjQyNGJhYzEzODE0MTU5MjcyZDk2NzNhYyJ9fX0="
	};

	private final Random random;
	private BossBar bossBar = null;
	private BukkitTask updateEventBarTask, runEggEffectsTask;

	public EasterEvent(Plugin plugin, long startTime, long endTime) {
		super(plugin, EventType.EASTER.getId(), startTime, endTime);
		this.random = new Random();
	}

	@Override
	public void onInit() {
		HandlerList.unregisterAll(this);
		Bukkit.getPluginManager().registerEvents(this, super.getPlugin());
//		new EasterEggCommand(this);
	}

	@Override
	public void onStart() {
		this.bossBar = Bukkit.createBossBar(Utils.f("&c&lE&6&la&e&ls&c&lt&6&le&e&lr &c&lE&6&lg&e&lg &c&lE&6&lv&e&le&c&ln&6&lt&e&l!"), BarColor.YELLOW, BarStyle.SOLID);

		this.updateEventBarTask = new BukkitRunnable() {
			@Override
			public void run() {
				updateBossBar();

				for (Player player : Bukkit.getOnlinePlayers()) {
					if (bossBar == null) continue;
					bossBar.addPlayer(player);
				}
			}
		}.runTaskTimerAsynchronously(getPlugin(), 0, 20*60);


		for(Player player : Bukkit.getOnlinePlayers())
			this.bossBar.addPlayer(player);

		ServerUtil.runTaskAsync(() -> {
			try (Connection connection = BaseDatabase.getInstance().getConnection()) {
				this.eggs.addAll(EasterDAO.getEasterEggs(connection));

				ServerUtil.runTask(() -> {
					for (EasterEgg egg : this.eggs) {
						if (egg == null) continue;

						if (!egg.getLocation().getChunk().isLoaded())
							egg.getLocation().getChunk().load();

						for (Entity entity : egg.getLocation().getWorld().getNearbyEntities(egg.getLocation(), 0.2, 2, 0.2)) {
							if (entity == null) continue;
							if (entity.getType() != EntityType.ARMOR_STAND) continue;
							entity.remove();
						}

						ArmorStand armorStand = egg.getLocation().getWorld().spawn(egg.getLocation().clone().subtract(0, 1.4, 0), ArmorStand.class);
						armorStand.setRemoveWhenFarAway(false);
						armorStand.setGravity(false);
						armorStand.setVisible(false);
						armorStand.setInvulnerable(true);
						armorStand.setHelmet(this.createEggHead());
						armorStand.setCustomName(Utils.f("&e&lClick Me"));
						armorStand.setCustomNameVisible(true);
						armorStand.setMetadata("easteregg", new FixedMetadataValue(super.getPlugin(), egg));

						egg.setArmorStand(armorStand);

						eggEntityIds.put(armorStand.getEntityId(), egg);
						chunks.add(egg.getLocation().getChunk());
					}
				});
			} catch (SQLException e) {
				e.printStackTrace();
			}
		});

		this.runEggEffectsTask = new BukkitRunnable() {
			@Override
			public void run() {
				for (EasterEgg egg : EasterEvent.this.eggs) {
					if (egg.getArmorStand() == null) continue;
//					egg.getLocation().getWorld().spawnParticle(Particle.VILLAGER_HAPPY, egg.getArmorStand().getEyeLocation(), 25, 0.7, 1, 0.7);
					List<Player> players = getPlayersHasntUnlocked(egg.getUniqueIdentifier());
					if (players.isEmpty()) break;
					ParticleEffect.VILLAGER_HAPPY.display(0.7f, 1f, 0.7f, 1, 25, egg.getArmorStand().getEyeLocation(), players);
					//255f/255f,102f/255f,102f/255f
				}
			}
		}.runTaskTimerAsynchronously(getPlugin(), 100, 20*5);

		ProtocolLibrary.getProtocolManager().addPacketListener(new EasterPacketListener(super.getPlugin(), this));
	}

	@Override
	public void onEnd() {
		this.updateEventBarTask.cancel();
		this.runEggEffectsTask.cancel();

		this.bossBar.removeAll();
		this.bossBar = null;

		for (EasterEgg egg : EasterEvent.this.eggs) {
			if (egg.getArmorStand() == null) continue;
			egg.getArmorStand().remove();
		}
	}

	private List<Player> getPlayingPlayers() {
		return Bukkit.getOnlinePlayers().stream().filter(player -> this.playerCache.containsKey(player.getUniqueId()) && !this.playerCache.get(player.getUniqueId()).hasFoundAllEggs()).collect(Collectors.toList());
	}

	private List<Player> getPlayersHasntUnlocked(int easterEggId) {
		return Bukkit.getOnlinePlayers().stream().filter(player -> {
			return !player.getWorld().getName().equals("spawn") && this.playerCache.containsKey(player.getUniqueId()) && !this.playerCache.get(player.getUniqueId()).hasFoundEgg(easterEggId);
		}).collect(Collectors.toList());
	}

	private void updateBossBar() {

		// how many seconds left until over
		int secondsLeft = (int) ((getEndTime() - System.currentTimeMillis()) / 1000.0);

		int day = (int) TimeUnit.SECONDS.toDays(secondsLeft);
		long hours = TimeUnit.SECONDS.toHours(secondsLeft) - (day * 24);
		long minute = TimeUnit.SECONDS.toMinutes(secondsLeft) - (TimeUnit.SECONDS.toHours(secondsLeft) * 60);

		String timeLeft = day + "d " + hours + "h " + minute + "m";
		String title = Utils.f("&c&lE&6&la&e&ls&c&lt&6&le&e&lr &c&lE&6&lg&e&lg &c&lE&6&lv&e&le&c&ln&6&lt&e&l! &6&l") + timeLeft;

		double start = System.currentTimeMillis() - getStartTime();
		double end = getEndTime() - getStartTime();

		// this gives us how through we are
		double through = start / end;
		double progress = 1 - through;

		this.bossBar.setTitle(title);
		this.bossBar.setProgress(progress);
	}

	private ItemStack createEggHead() {
		GameProfile profile = new GameProfile(UUID.randomUUID(), null);
		profile.getProperties().clear();
		profile.getProperties().put("textures", new Property("texture", this.eggSkins[this.random.nextInt(this.eggSkins.length)], null));

		ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
		SkullMeta headMeta = (SkullMeta) head.getItemMeta();

		try {
			Field profileField = headMeta.getClass().getDeclaredField("profile");
			profileField.setAccessible(true);
			profileField.set(headMeta, profile);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		head.setItemMeta(headMeta);
		return head;
	}

	@EventHandler
	protected final void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
		try (Connection connection = BaseDatabase.getInstance().getConnection()) {
			this.playerCache.putIfAbsent(event.getUniqueId(), new EasterPlayerData(super.getPlugin(), this, EasterDAO.getFoundEggs(connection, event.getUniqueId())));
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@EventHandler
	protected final void onPlayerQuit(PlayerQuitEvent event) {
		this.playerCache.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	protected final void onEntityInteract(PlayerInteractAtEntityEvent event) {
		if (event.getRightClicked() == null) return;
		if (!this.playerCache.containsKey(event.getPlayer().getUniqueId())) return;
		if (!event.getRightClicked().hasMetadata("easteregg")) return;

		ArmorStand armorStand = (ArmorStand) event.getRightClicked();
		if (armorStand.getMetadata("easteregg").size() <= 0) return;
		if (armorStand.getMetadata("easteregg").get(0) == null) return;

		EasterEgg easterEgg = (EasterEgg) armorStand.getMetadata("easteregg").get(0).value();
		if (easterEgg == null) return;

		EasterPlayerData playerData = this.playerCache.get(event.getPlayer().getUniqueId());
		playerData.find(event.getPlayer(), easterEgg);
	}

	@EventHandler
	protected final void onEasterEggFind(EasterFoundEggEvent event) {
		User user = Core.getUserManager().getLoadedUser(event.getPlayer().getUniqueId());
		if (!this.playerCache.containsKey(event.getPlayer().getUniqueId())) return;
		EasterPlayerData playerData = this.playerCache.get(event.getPlayer().getUniqueId());

		if (event.isFoundAll()) {
			user.giveEventTag(EventTag.EGG);

			user.addCrowbars(2);
			event.getPlayer().sendMessage(Lang.CROWBARS_ADD.f("2"));

			event.getPlayer().sendTitle(Utils.f("&6&lEaster Event"), Utils.f("&rAll eggs found, You've unlocked '&c&lE&6&lG&e&lG&r' tag!"));

			Bukkit.broadcastMessage(Utils.f("&c&lE&6&la&e&ls&c&lt&6&le&e&lr &c&lE&6&lg&e&lg &c&lE&6&lv&e&le&c&ln&6&lt&r"));
			Bukkit.broadcastMessage(Utils.f("&e" + event.getPlayer().getName() + "&r has found all Easter Eggs and has been rewarded the '&c&lE&6&lG&e&lG&r' tag!"));

			playerData.startAnimation(event.getPlayer(), event.getEasterEgg());
			return;
		}

		user.addCrowbars(2);
		event.getPlayer().sendMessage(Lang.CROWBARS_ADD.f("2"));

		event.getPlayer().sendTitle(Utils.f("&6&lEaster Event"), Utils.f("&rYou found an easter egg, &l" + Math.abs(playerData.getEggsFounds() - this.eggs.size()) + "&r left!"));

		playerData.startAnimation(event.getPlayer(), event.getEasterEgg());
	}

	@EventHandler(ignoreCancelled = true)
	protected final void onChunkUnload(ChunkUnloadEvent event) {
		if (chunks.contains(event.getChunk()))
			event.setCancelled(true);
	}

	@EventHandler(ignoreCancelled = true)
	protected final void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() == null) return;

		if (event.getEntity().hasMetadata("easteregg"))
			event.setCancelled(true);
	}
}
