package net.grandtheftmc.gtm.listeners;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.drugs.Drug;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class Damage implements Listener {

	@EventHandler
	public void onDamage(EntityDamageEvent e) {
		Entity victimEntity = e.getEntity();
		if (!(victimEntity instanceof Player))
			return;
		Player victim = (Player) victimEntity;
		if (victim.getGameMode() == GameMode.SPECTATOR || victim.getWorld().equals(GTM.getWarpManager().getSpawn().getLocation().getWorld())) {
			e.setCancelled(true);
		}
		else if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) {
			
			GTMUser user = GTMUserManager.getInstance().getUser(victim.getUniqueId()).orElse(null);
			if (user != null && user.hasTeleportProtection()){
				e.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamageMonitor(EntityDamageEvent e) {

		if (!(e.getEntity() instanceof Player)) {
			return;
		}

		// grab event variables
		Player player = (Player) e.getEntity();

		// grab gtm user and and user
		GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
		if (gtmUser != null) {

			User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
			if (user != null) {

				// update tint health
				gtmUser.updateTintHealth(player, user);
			}
		}
	}

	/**
	 * Listens in on explosion damage entity event.
	 * <p>
	 * This is called when a launcher weapon or a throwable weapon explodes.
	 * This is mainly used as a permission listen.
	 * </p>
	 * 
	 * @param event - the event
	 */
	@EventHandler
	public void onExplosionDamageEntityEvent(ExplosionDamageEntityEvent event) {

		// if shooter not player, ignore
		if (event.getShooter().getType() != EntityType.PLAYER)
			return;

		// grab event variables
		Player damager = (Player) event.getShooter();

		// for each entity in event
		for (LivingEntity livingEntity : event.getVictims()) {

			// only iterate over players
			if (livingEntity.getType() != EntityType.PLAYER){
				continue;
			}

			Player victim = (Player) livingEntity;
			GTMUser victimGameUser = GTMUserManager.getInstance().getUser(victim.getUniqueId()).orElse(null);
			
			// if null victim, skip perm check
			if (victimGameUser == null){
				continue;
			}

			// if victim has protection
			if (victimGameUser.hasTeleportProtection()) {
				event.getVictims().remove(livingEntity);
				continue;
			}

			// if shooter has protection
			GTMUser damagerGameUser = GTMUserManager.getInstance().getUser(damager.getUniqueId()).orElse(null);
			
			// if null user, remove victim from being damaged
			if (damagerGameUser == null){
				event.getVictims().remove(livingEntity);
				continue;
			}
			
			if (damagerGameUser.hasTeleportProtection()) {
				event.getVictims().remove(livingEntity);
				continue;
			}

			// if player is in gang
			Optional<Gang> victimOpt = GangManager.getInstance().getGangByMember(victim.getUniqueId()),
					damagerOpt = GangManager.getInstance().getGangByMember(damager.getUniqueId());
			if (victimOpt.isPresent() && damagerOpt.isPresent() && !Objects.equals(victim, damager)) {
				if (Objects.equals(victimOpt.get(), damagerOpt.get())) {
					event.getVictims().remove(livingEntity);
					continue;
				}

				if (victimOpt.get().isAllied(damagerOpt.get())) {
					event.getVictims().remove(livingEntity);
					continue;
				}
			}

			// if they have a job mode
			switch (damagerGameUser.getJobMode()) {
				case COP:
					JobMode mode = victimGameUser.getJobMode();
					if (victimGameUser.getJobMode() == JobMode.COP) {
						event.getVictims().remove(livingEntity);
						continue;
					}
					if (mode == JobMode.CRIMINAL && victimGameUser.getWantedLevel() == 0) {
						event.getVictims().remove(livingEntity);
						continue;
					}
				case CRIMINAL:
					if (victimGameUser.getJobMode() == JobMode.COP && damagerGameUser.getKillCounter() == 0) {
						int wantedLevelBefore = damagerGameUser.getWantedLevel();
						damagerGameUser.addKillCounter(1);
						int wantedLevelAfter = damagerGameUser.getWantedLevel();
						if (wantedLevelBefore < wantedLevelAfter)
							damager.sendMessage(Utils.f(Lang.WANTED + "&7Oh " + (Core.getSettings().isSister() ? "snap" : "shit") + " the cops are onto you! &r" + GTMUtils.getWantedLevelStars(wantedLevelAfter) + " &7(&c" + wantedLevelAfter + "&7) "));
					}
				default:
					break;
			}
		}
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getDamager() == null)
			return;
		Entity entity = e.getDamager();
		if (entity == null)
			return;

		if (entity instanceof Projectile)
			entity = (Entity) ((Projectile) entity).getShooter();
		else if (entity instanceof Tameable)
			entity = (Entity) ((Tameable) entity).getOwner();

		if (!(e.getEntity() instanceof Player && entity instanceof Player))
			return;
		Player damager = (Player) entity;
		Player victim = (Player) e.getEntity();

		// if pvp is disabled, cancel event
		if (!GTM.getSettings().isPvp()) {
			e.setCancelled(true);
			return;
		}

		GTMUser victimGameUser = GTM.getUserManager().getLoadedUser(victim.getUniqueId());
		User coreVictimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
		
		// not loaded user should probably still take damage
		if (victimGameUser == null){
			return;
		}
		
		if (victimGameUser.hasTeleportProtection()) {
			e.setCancelled(true);
			long expires = TimeUnit.MILLISECONDS.toSeconds(victimGameUser.getTimeUntilTeleportProtectionExpires());
			if (expires <= 1) {
				victimGameUser.setLastTeleport(0);
				return;
			}
			damager.sendMessage(Lang.GTM.f(coreVictimUser.getColoredName(victim) + " &7has teleport protection for &a" + expires + "&7 seconds!"));
			return;
		}

		GTMUser damagerGameUser = GTM.getUserManager().getLoadedUser(damager.getUniqueId());
		if (damagerGameUser.hasTeleportProtection()) {
			e.setCancelled(true);
			damagerGameUser.setLastTeleport(0);
			if (damagerGameUser.getCheatCodeState(CheatCode.SNEAKY).getState() == State.ON && damager.hasPotionEffect(PotionEffectType.INVISIBILITY))
				damager.removePotionEffect(PotionEffectType.INVISIBILITY);
			damager.sendMessage(Lang.COMBATTAG.f("&7Your teleport protection has ended!"));
			return;
		}

		Optional<Gang> victimOpt = GangManager.getInstance().getGangByMember(victim.getUniqueId()),
				damagerOpt = GangManager.getInstance().getGangByMember(damager.getUniqueId());
		if (victimOpt.isPresent() && damagerOpt.isPresent() && !Objects.equals(victim, damager)) {
			if (Objects.equals(victimOpt.get(), damagerOpt.get())) {
				e.setCancelled(true);
				damager.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in your gang!"));
				return;
			}

			if (victimOpt.get().isAllied(damagerOpt.get()) || damagerOpt.get().isAllied(victimOpt.get())) {
				e.setCancelled(true);
				damager.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in an allied gang!"));
				return;
			}
		}

		switch (damagerGameUser.getJobMode()) {
			case COP: {
				JobMode mode = victimGameUser.getJobMode();
				if (victimGameUser.getJobMode() == JobMode.COP) {
					e.setCancelled(true);
					damager.sendMessage(Utils.f(Lang.HEY + "&cYou can't kill cops!"));
					break;
				}
				if (mode == JobMode.CRIMINAL && victimGameUser.getWantedLevel() == 0) {
					damager.sendMessage(Lang.HEY.f("&7You can't damage citizens that are not wanted!"));
					e.setCancelled(true);
					break;
				}
			}
			case CRIMINAL:
				if (victimGameUser.getJobMode() == JobMode.COP && damagerGameUser.getKillCounter() == 0) {
					int wantedLevelBefore = damagerGameUser.getWantedLevel();
					damagerGameUser.addKillCounter(1);
					int wantedLevelAfter = damagerGameUser.getWantedLevel();
					if (wantedLevelBefore < wantedLevelAfter)
						damager.sendMessage(Utils.f(Lang.WANTED + "&7Oh " + (Core.getSettings().isSister() ? "snap" : "shit") + " the cops are onto you! &r" + GTMUtils.getWantedLevelStars(wantedLevelAfter) + " &7(&c" + wantedLevelAfter + "&7) "));
				}
			default:
				break;
		}

	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onDamageByEntityMonitor(EntityDamageByEntityEvent e) {
		Entity damager = e.getDamager();
		Entity entity = e.getEntity();
		switch (entity.getType()) {
			case PLAYER: {
				if (e.isCancelled())
					return;
				Player player = (Player) entity;
				GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
				if (damager == null)
					return;
				if (damager instanceof Projectile)
					damager = (Entity) ((Projectile) damager).getShooter();
				else if (entity instanceof Tameable)
					damager = (Entity) ((Tameable) entity).getOwner();
				if (!(damager instanceof Player && entity instanceof Player))
					return;
				if (!user.isInCombat())
					player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are now in combat! Do not log out for 10 seconds!"));
				user.setLastTag(System.currentTimeMillis());
				if (GTM.getWarpManager().cancelTaxi(player, user))
					player.sendMessage(Utils.f(Lang.TAXI + "&eYour cab was cancelled!"));
				Player dmger = (Player) damager;
				GTMUser damagerUser = GTM.getUserManager().getLoadedUser(dmger.getUniqueId());
				if (!damagerUser.isInCombat())
					dmger.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are now in combat! Do not log out for 20 seconds!"));
				damagerUser.setLastTag(System.currentTimeMillis());
				if (GTM.getWarpManager().cancelTaxi(dmger, damagerUser))
					player.sendMessage(Utils.f(Lang.TAXI + "&eYour cab was cancelled!"));
				if (dmger.getInventory().getItemInMainHand() != null) {
					if (!Core.getSettings().isSister()) {
						Optional<Drug> heroin = ((DrugService) GTM.getDrugManager().getService()).getDrug("heroin");
						if (heroin.isPresent()) {
							if (dmger.getInventory().getItemInMainHand().getDurability() == 5 && dmger.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL) {
								heroin.get().apply(player);
								player.damage(2);
								player.sendMessage(Lang.DRUGS.f("&7You have been drugged."));
								if (dmger.getInventory().getItemInMainHand().getAmount() == 1)
									dmger.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
								else
									dmger.getInventory().getItemInMainHand().setAmount(dmger.getInventory().getItemInMainHand().getAmount() - 1);
								dmger.updateInventory();
							}
						}
					}
				}
				if (damager.getType() == EntityType.SNOWBALL) {
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20 * 3, 1));
				}
				return;

			}
			case ITEM_FRAME:
				if (!(damager instanceof Player))
					return;
				Player player = (Player) damager;
				ItemFrame frame = (ItemFrame) entity;
				if (Core.getUserManager().getLoadedUser(player.getUniqueId()).hasEditMode())
					return;
				ItemStack item = frame.getItem();
				if (item == null)
					return;
				switch (item.getType()) {
					case PAPER:
						MenuManager.openMenu(player, "atm");
						return;
					default:
						GTM.getShopManager().buy(player, frame.getItem());
						return;
				}
			default:
				break;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHealthChangeMonitor(EntityRegainHealthEvent e) {
		
		if (!(e.getEntity() instanceof Player))
			return;
		
		// grab event variables
		Player player = (Player) e.getEntity();
		
		GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
		if (gtmUser != null){
			
			User coreUser = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
			if (coreUser != null){
				gtmUser.updateTintHealth(player, coreUser);
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	protected final void onPlayerDamage(EntityDamageByEntityEvent event) {
		if (!(event.getDamager() instanceof Player))
			return;
		Player damager = (Player) event.getDamager();
		HouseUser user = Houses.getUserManager().getLoadedUser(damager.getUniqueId());
		if (user == null)
			return;

		if (!event.isCancelled() && (user.isInsideHouse() || user.isInsidePremiumHouse())) {
			user.setInsideHouse(-1);
			user.setInsidePremiumHouse(-1);
			user.updateVisibility(damager);
		}
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	protected final void onPlayerDamage(EntityDamageEvent event) {
		if (event.getEntity() == null)
			return;
		if (!(event.getEntity() instanceof Player))
			return;

		if (((Player) event.getEntity()).isBlocking() && ThreadLocalRandom.current().nextBoolean()) {
			((Player) event.getEntity()).damage(event.getDamage() / 2);
		}

		if (event.getCause() == EntityDamageEvent.DamageCause.CUSTOM || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) {
			((Player) event.getEntity()).closeInventory();
			((Player) event.getEntity()).updateInventory();
		}
	}
}