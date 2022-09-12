package net.grandtheftmc.gtm.event.halloween;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import de.slikey.effectlib.util.ParticleEffect;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.event.Event;
import net.grandtheftmc.core.event.EventManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.PluginAssociated;
import net.grandtheftmc.core.util.Title;
import net.grandtheftmc.core.util.factory.FireworkFactory;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;

public class TrickOrTreatTask implements PluginAssociated {

	/** Random instance for this class */
	public static final Random RANDOM = new Random();
	/** Collection of no responses */
	public static final List<String> NO_RESPONSES = Arrays.asList("Fuck off!", "I don't have any candy you kid!", "No... I want free candy. You're not getting any here.");
	/** Collection of meh responses */
	public static final List<String> MEH_RESPONSES = Arrays.asList("I guess that was a cool trick, for a brat.", "Okay you little ass. I'll give you something.", "Here's some drugs. Enjoy.", "Don't snort all this at once.");
	/** Collection of yes responses */
	public static final List<String> YES_RESPONSES = Arrays.asList("Haha that shit was great.", "Probably the best trick i've seen all day.", "That was a really smooth trick.", "Got me. Now fuck off.");

	/** The owning plugin for this task */
	private final Plugin plugin;
	/** The player involved in the event */
	private final Player player;
	/** The location for the event */
	private final Location location;

	/**
	 * Construct a new TrickOrTreatTask.
	 * 
	 * @param plugin - the owning plugin
	 * @param player - the player involved in the event
	 * @param location - the location where the player knocked
	 */
	public TrickOrTreatTask(Plugin plugin, Player player, Location location) {
		this.plugin = plugin;
		this.player = player;
		this.location = location;

		// start the effect task
		start(player, location);
	}

	/**
	 * Start the trait or treat task.
	 * 
	 * @param p - the player knocking on the door
	 * @param loc - the location of the door
	 */
	protected void start(Player p, Location loc) {

		// notify player that they are participating
		p.sendMessage(ChatColor.GOLD + "You ring the doorbell...");
		p.playSound(p.getLocation(), Sound.BLOCK_NOTE_CHIME, 1f, 0f);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.6f, 0f);
		}, 10L);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.7f, 0f);
		}, 20L);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.8f, 0f);
		}, 30L);
		
		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.9f, 0f);
		}, 40L);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 1.0f, 0f);
		}, 50L);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_OPEN, 0.3f, 0f);
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_TRADING, 0.6f, 0f);
		}, 80L);

		Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
			determineOutcome(p, loc);
		}, 100L);
	}

	/**
	 * Determine the outcome for the specified player/location.
	 * 
	 * @param p - the player
	 * @param loc - the location to play effects
	 */
	protected void determineOutcome(Player p, Location loc) {

		// roll from 0 - 999
		int chance = RANDOM.nextInt(1000);

		// 2.5% chance to get LOVE IT
		if (chance < 25) {

			User user = Core.getUserManager().getLoadedUser(p.getUniqueId());
			if (user != null) {

				Event event = EventManager.getInstance().getEvent().orElse(null);
				if (event != null && event instanceof HalloweenEvent) {
					HalloweenEvent hween = (HalloweenEvent) event;
					
					
					// half goes to play, half is wiped
					int pot = hween.getTokens();
					int toGive = (int) (pot / 2.0);

					// reset tokens
					hween.setTokens(0);

					// give them tokens
					user.setTokens(user.getTokens() + toGive);

					Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Halloween Event - " + ChatColor.WHITE + "A villager loved " + ChatColor.GOLD + p.getName() + "'s TRICK" + ChatColor.WHITE + " and gave them " + ChatColor.GOLD + toGive + " tokens!");
					Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Halloween Event - " + ChatColor.WHITE + "You can participate by knocking on any " + ChatColor.YELLOW + "Premium House's door" + ChatColor.WHITE + " with a " + ChatColor.GOLD + "Candy Bag" + ChatColor.WHITE + " item in hand (Costs " + HalloweenEvent.TRICK_COST + " tokens.");
					Bukkit.broadcastMessage(ChatColor.GOLD.toString() + ChatColor.BOLD + "Halloween Event - " + ChatColor.WHITE + "Skeleton's drop " + ChatColor.GOLD + "Candy Bags" + ChatColor.WHITE + " at a rare rate.");
				}
			}

			p.sendMessage(ChatColor.GREEN + YES_RESPONSES.get(RANDOM.nextInt(YES_RESPONSES.size())));

			// heart particles everywhere
			for (int i = 0; i < 5; i++) {
				ParticleEffect.HEART.display(loc, 10, RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 10, 1);
			}

			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_AMBIENT, 1f, 0f);
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_YES, 1f, 0f);
			p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0f);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.6f, 0.1f);
			}, 10L);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.8f, 0.2f);
			}, 20L);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.3f);
			}, 30L);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 0.5f);
				p.playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 0.1f, 0.1f);

				// multi colored fireworks firework
				new FireworkFactory(location.clone().add(0, 1, 0)).setPower(0).setColor(Color.ORANGE).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL).build();
				new FireworkFactory(location.clone().add(RANDOM.nextDouble() - 0.5, RANDOM.nextDouble() + 1, RANDOM.nextDouble() - 0.5)).setPower(0).setColor(Color.YELLOW).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BALL).build();
				new FireworkFactory(location.clone().add(RANDOM.nextDouble() - 0.5, 0, RANDOM.nextDouble() - 0.5)).setPower(0).setColor(Color.YELLOW).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BURST).build();
				new FireworkFactory(location.clone().add(RANDOM.nextDouble() - 0.5, 0, RANDOM.nextDouble() - 0.5)).setPower(0).setColor(Color.ORANGE).setFadeColor(Color.WHITE).setFlicker(true).setTrail(true).setType(FireworkEffect.Type.BURST).build();

				// play title to player
				Title title = new Title(ChatColor.GOLD + "Nice Trick!", ChatColor.WHITE + "", 10, 20 * 5, 10);
				title.play(p);
			}, 40L);
		}
		// 47.5% chacnce to get EH
		else if (chance < 500) {
			// tell player their trick was cool
			p.sendMessage(ChatColor.YELLOW + MEH_RESPONSES.get(RANDOM.nextInt(MEH_RESPONSES.size())));

			// TODO throw plant particles everywhere
			for (int i = 0; i < 5; i++) {
				// p.getWorld().playEffect(loc, Effect.TILE_BREAK,
				// Material.VINE.getId());
				// ParticleEffect.BLOCK_CRACK.display(new
				// ParticleData(Material.WOOL, (byte) 15){}, 1F, 1F, 1F, 1F, 30,
				// loc, 32);
			}

			// player receives 1-3 roofied chocolate
			int numRewards = 1 + RANDOM.nextInt(2);
			for (int i = 0; i < numRewards; i++) {
				GameItem rc = GTM.getItemManager().getItem("roofied_chocolate");
				if (rc != null) {
					p.getInventory().addItem(rc.getItem());
				}
			}
		}
		// 50% chance to get NO
		else {
			// player gets nothing
			p.sendMessage(ChatColor.RED + NO_RESPONSES.get(RANDOM.nextInt(NO_RESPONSES.size())));

			// play loud villager hurt sound
			p.playSound(p.getLocation(), Sound.ENTITY_VILLAGER_HURT, 0.6f, 0f);
			// throw green plant particles everywhere
			for (int i = 0; i < 5; i++) {
				ParticleEffect.VILLAGER_ANGRY.display(loc, 10, RANDOM.nextFloat(), RANDOM.nextFloat(), RANDOM.nextFloat(), 10, 1);
			}

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				// slam door sound
				p.playSound(p.getLocation(), Sound.BLOCK_IRON_DOOR_CLOSE, 1f, 0f);
			}, 10L);

			// follow by fading footsteps
			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 1f, 0f);
			}, 20L);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.8f, 0f);
			}, 30L);

			Bukkit.getScheduler().runTaskLater(getPlugin(), () -> {
				p.playSound(p.getLocation(), Sound.BLOCK_STONE_STEP, 0.6f, 0f);
			}, 40L);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Plugin getPlugin() {
		return plugin;
	}
}
