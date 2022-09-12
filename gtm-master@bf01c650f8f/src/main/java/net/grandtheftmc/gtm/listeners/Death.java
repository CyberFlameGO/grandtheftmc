package net.grandtheftmc.gtm.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.nametags.NametagManager;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.bounties.Bounty;
import net.grandtheftmc.gtm.bounties.BountyManager;
import net.grandtheftmc.gtm.items.events.ArmorEquipEvent;
import net.grandtheftmc.gtm.items.events.EquipArmorType;
import net.grandtheftmc.gtm.users.CompassTarget;
import net.grandtheftmc.gtm.users.CompassTarget.TargetType;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.guns.weapon.Weapon;

public class Death implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        
        if (killer == null){
        	
        	// TODO test remove
        	Entity last = getLastDamageCauseSource(victim).orElse(null);
        	if (last != null){
        		Core.log("[Death][DEBUG] No killer found for the death of " + victim.getName() + ", last damage entity was=" + last.toString());
        	}
        }

        boolean brokenDeath = killer == null || victim.getUniqueId().equals(killer.getUniqueId());

        UUID victimUUID = victim.getUniqueId();
        GTMUser victimGameUser = GTM.getUserManager().getLoadedUser(victimUUID);
        User victimUser = Core.getUserManager().getLoadedUser(victimUUID);

        if (!Core.getSettings().isSister()) {
            GTM.getDrugManager().getEffectManager().cancelEffects(victim);
        }

        int wantedLevel = victimGameUser.getWantedLevel();
        JobMode jobMode = victimGameUser.getJobMode();

        victimGameUser.addDeaths(1);
        victimGameUser.setLastTag(-1);

        Collection<Player> hiddenStaff = new ArrayList<>();
        victim.getNearbyEntities(30, 30, 30).forEach(entity -> {
            if (entity.getType() != EntityType.PLAYER) return;
            Player target = (Player) entity;
            if (target.getGameMode() == GameMode.SPECTATOR) {
                hiddenStaff.add(target);
                victim.hidePlayer(target);
            }
        });
        victimGameUser.setDead(true);
        if (victimGameUser.getVehicleTaskId() != -1) {
            victimGameUser.cancelVehicleTeleport();
            victim.sendMessage(Lang.VEHICLES.f("&7You can't " + (victimGameUser.isSendAway() ? "send away" : "call") + " while you're dead!"));
        }
        victimGameUser.setKillCounter(0);
        victimGameUser.setKillStreak(0);
        victimGameUser.unsetCompassTarget(victim, victimUser);
        if (GTM.getWarpManager().cancelTaxi(victim, victimGameUser))
            victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));
        victim.setHealth(victim.getMaxHealth());
        victim.spigot().respawn();
        victim.setFireTicks(0);
        for (PotionEffect p : victim.getActivePotionEffects()) {
            victim.removePotionEffect(p.getType());
        }
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.setFlying(true);
        victim.setFoodLevel(20);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
        victim.setFlySpeed(0);
        GTMUtils.removeBoard(victim);
//        new JLibPlayer(victim).setWorldborderTint(0);

        // how much money is available to be "lost", half their money bal
        double availableLostMoney = (int) (victimGameUser.getMoney() / 2.0);

        // cannot lose more than this amount
        if (availableLostMoney > 50000) {
            GTMUtils.log("moneyalert", victim.getName() + " has died and been prevented from dropping " + Utils.formatMoney(availableLostMoney));
            availableLostMoney = 50000;
        }

        // how much money should be lost forever (to the server)
        double serverTax = 0;

        // is the server taking some of the "lost" money
        if (GTM.getSettings().isServerDeathTax()) {

            // get the base tax percent
            double taxPercent = GTM.getSettings().getServerDeathBasePercent();

            // if tax is scaled off rank
            if (GTM.getSettings().isServerDeathTaxScaled()) {
                taxPercent += victimGameUser.getRank().getServerTax();
            }

            // taxPercent is typically 20.0, so convert to percent, and multiply
            serverTax = (int) ((taxPercent / 100.0) * availableLostMoney);

            // if there is a minimum death tax set, i.e. $500
            if (GTM.getSettings().getServerDeathTaxMin() > 0) {
                if (serverTax < GTM.getSettings().getServerDeathTaxMin()) {
                    serverTax = GTM.getSettings().getServerDeathTaxMin();
                }
            }

            // if there is a maximum death tax set, i.e. $1000
            // and it's higher than the deathMin
            if (GTM.getSettings().getServerDeathTaxMax() > 0 && GTM.getSettings().getServerDeathTaxMax() > GTM.getSettings().getServerDeathTaxMin()) {
                if (serverTax > GTM.getSettings().getServerDeathTaxMax()) {
                    serverTax = GTM.getSettings().getServerDeathTaxMax();
                }
            }
        }
        
        // TODO remove
        Core.log("[Death][DEBUG] serverTax final=" + serverTax);
        
        // how much money the user is dropping on floor
        double dropMoney = (int) (availableLostMoney - serverTax);

        // TODO remove
        Core.log("[Death][DEBUG] init dropMoney=" + dropMoney);
        if (dropMoney < 0) {
            dropMoney = 0;
        }

        // how much money the USER must lose
        double finalLostMoney = availableLostMoney;
        // how much money the USER "drops" to other players
        double finalDropMoney = dropMoney;

        // TODO remove
        Core.log("[Death][DEBUG] Player " + victim.getName() + " has died and is attempting to drop $" + finalDropMoney + " because of finalLostMoney=$" + finalLostMoney);

        if (finalLostMoney > 2)
            victimGameUser.takeMoney(finalLostMoney);
        String money = String.valueOf(finalLostMoney);

        new BukkitRunnable() {
            @Override
            public void run() {

                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null) return;

                User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                GTMUser victimGameUser = GTM.getUserManager().getLoadedUser(victim.getUniqueId());

                if (Double.valueOf(money) > 0) {
                    victim.sendMessage(Utils.f(Lang.MONEY_TAKE.toString() + money));
                }

                // victimGameUser.figureOutSpawn();
                victim.teleport(GTM.getWarpManager().getSpawn().getLocation());
                for (PotionEffect p : victim.getActivePotionEffects()) {
                    victim.removePotionEffect(p.getType());
                }
                hiddenStaff.forEach(target -> {
                    if (target == null || !target.isOnline()) return;
                    victim.showPlayer(target);
                });
                hiddenStaff.clear();
                victim.setFoodLevel(20);
                victim.setGameMode(GameMode.ADVENTURE);
                victim.setFlying(false);
                victim.setFlySpeed(0.1F);
                GTMUtils.giveGameItems(victim);
                JobMode job = victimGameUser.getJobMode();
                if (job != JobMode.CRIMINAL) {
                    victimGameUser.setJobMode(JobMode.CRIMINAL);
                    UserRank rank = victimUser.getUserRank();
                    victim.sendMessage(
                            Lang.JOBS.f("&7You are no longer a " + job.getColoredNameBold() + "&7! Please wait &c&l" + Utils.timeInMillisToText(victimGameUser.getTimeUntilJobModeSwitch(rank)) + "&7 before switching Job Mode again!"
                                    + (rank.isHigherThan(UserRank.SPONSOR) ? "" : " Buy a rank a &a&l" + Core.getSettings().getStoreLink() + "&7 to be able to switch faster!")));
                    NametagManager.updateNametag(victim);
                }
                GTMUtils.updateBoard(victim, victimGameUser);
                victimGameUser.setDead(false);
            }
        }.runTaskLater(GTM.getInstance(), 150);

        double moneyToDrop = ((killer == null || killer.equals(victim)) ? 0 : Utils.randomNumber(250, 1000)) + finalDropMoney;
        new ArrayList<>(e.getDrops()).stream().filter(stack -> stack != null && (stack.getType() == Material.WATCH || stack.getType() == Material.COMPASS || stack.getType() == Material.CHEST)).forEach(e.getDrops()::remove);

        if (!brokenDeath && moneyToDrop > 0){
            e.getDrops().add(Utils.createItem(Material.PAPER, "&a$&l" + moneyToDrop));
        }

        Utils.sendTitle(victim, "&c&lWASTED", brokenDeath ? null : "&7" + GTMUtils.getMessageKilledBy(killer.getName()), 80, 50, 20);
        if (killer == null || killer.equals(victim)) {
            e.setDeathMessage(Utils.f("&e" + victim.getName() + "&7 " + (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent ? "was killed by &c" + ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager().getCustomName() + "&7!" : "died!")));
            return;
        }
        Weapon killerWeapon = GTM.getWastedGuns().getWeaponManager().getWeaponInHand(killer);
        Utils.b((killerWeapon != null && killerWeapon.getCompactName().equalsIgnoreCase("katana"))+"");
        if (Utils.calculateChance(killerWeapon != null && killerWeapon.getCompactName().equalsIgnoreCase("katana") ? 5 : 2)) {

            e.getDrops().add(Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + victim.getName() + "'s Head", "&7Value: &a$&l10,000", "&7Sell me in the sewer!"), victim.getName()));
            killer.sendMessage(Lang.HEY.f(victimUser.getColoredName(victim) + "&7's head dropped on the ground! Sell it at the auction house for a guaranteed &a$&l10,000&7 or more!"));
        }

        e.setDeathMessage(Utils.f("&e" + victim.getName() + "&7 was killed by &c" + (brokenDeath ? "Unkown" : killer.getName()) + "&7!"));
        UUID killerUUID = killer.getUniqueId();
        GTMUser killerGameUser = GTM.getUserManager().getLoadedUser(killerUUID);
        User killerUser = Core.getUserManager().getLoadedUser(killerUUID);
        killerGameUser.addKills(1);
        killerGameUser.addKillStreak(1);
        switch (killerGameUser.getJobMode()) {
            case CRIMINAL:
                int wantedLevelBefore = killerGameUser.getWantedLevel();
                if (jobMode == JobMode.COP) {
                    killerGameUser.addKillCounter(2);
                } else {
                    if (ThreadLocalRandom.current().nextInt(1, 4) == 2) {
                        killerGameUser.addKillCounter(1);
                    }
                }
                int wantedLevelAfter = killerGameUser.getWantedLevel();
                killer.sendMessage(Lang.GTM.f("&7You killed &a" + victimUser.getColoredName(victim) + "&7! &a$&l"
                        + moneyToDrop + "&7 was dropped on the ground!"));
                if (wantedLevelBefore < wantedLevelAfter)
                    killer.sendMessage(Utils.f(Lang.WANTED + "&7Oh " + (Core.getSettings().isSister() ? "snap" : "shit") + " the cops are onto you! &r"
                            + GTMUtils.getWantedLevelStars(wantedLevelAfter) + "&7 (&c" + wantedLevelAfter + "&7) "));
                break;
            case COP:
                int copMoney = GTMUtils.getCopMoney(wantedLevel);
                if (copMoney > 0) {
                    killerGameUser.addMoney(copMoney);
                    killer.sendMessage(Utils.f(Lang.COP_MODE + "&7You were rewarded &a$&l" + copMoney + "&7 for killing &c"
                            + victimUser.getColoredName(victim) + " &7with &e" + GTMUtils.getWantedLevelStars(wantedLevel)
                            + " (" + wantedLevel + ")&7!"));
                } else
                    killer.sendMessage(Lang.GTM.f("&7You killed &a" + victimUser.getColoredName(victim) + "&7! &a$&l"
                            + moneyToDrop + "&7 was dropped on the ground!"));
                break;
            case HITMAN:
                CompassTarget compassTarget = killerGameUser.getCompassTarget();
                if (compassTarget != null && compassTarget.getType() == TargetType.PLAYER
                        && Objects.equals(compassTarget.getTargetPlayer(), victim))
                    killerGameUser.unsetCompassTarget(killer, killerUser);
                BountyManager bm = GTM.getBountyManager();
                Bounty bounty = bm.getBounty(victimUUID);
                if (bounty == null) {
                    killer.sendMessage(Lang.GTM.f("&7You killed &a" + victimUser.getColoredName(victim) + "&7! &a$&l"
                            + moneyToDrop + "&7 was dropped on the ground!"));
                    break;
                }
                killerGameUser.addMoney(bounty.getAmount());
                bm.removeBounty(bounty);
                Utils.broadcastExcept(killer, Lang.BOUNTIES + "&a" + killer.getName() + "&7 claimed the bounty of &a$&l"
                        + bounty.getAmount() + "&7 on &c" + bounty.getName() + "&7!");
                killer.sendMessage(Utils.f(Lang.HITMAN_MODE + "&7You claimed the bounty of &a$&l" + bounty.getAmount()
                        + "&7 on &a" + victim.getName() + "&7!"));
                killer.sendMessage(Utils.f(Lang.MONEY_ADD + String.valueOf(bounty.getAmount())));
                break;
        }
        GTMUtils.updateBoard(killer, killerGameUser);
        victim.setBedSpawnLocation(GTM.getWarpManager().getSpawn().getLocation(), true);
        killer.setBedSpawnLocation(GTM.getWarpManager().getSpawn().getLocation(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(PlayerDeathEvent e) {
    	
    	// send death message to everyone online
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
            if (user != null){
                if (!Objects.equals(player, e.getEntity().getKiller()) && user.getPref(Pref.DEATH_MESSAGES)){
                    player.sendMessage(e.getDeathMessage());
                }
            }
        }
        
        // set event death message to null
        e.setDeathMessage(null);

        Player p = e.getEntity();
        int slot = 39;
        for (ItemStack i : p.getInventory().getArmorContents()) {
            if (i != null && !i.getType().equals(Material.AIR)) {
                ArmorEquipEvent event = new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, EquipArmorType.matchType(i), i, new ItemStack(Material.AIR), EquipArmorType.fromSlot(slot));
                Bukkit.getServer().getPluginManager().callEvent(event);
                slot--;
            }
        }
    }
    
    public static Optional<Entity> getLastDamageCauseSource(Entity entity){
    	
    	// grab the last damage cause
    	EntityDamageEvent ede = entity.getLastDamageCause();
    	
    	if (ede == null){
    		return Optional.empty();
    	}
    	
		if (ede instanceof EntityDamageByEntityEvent) {

			EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) ede;

			Entity damager = edbee.getDamager();
			if (damager instanceof Player) {

				Player killer = (Player) damager;
				return Optional.of(killer);
			}
		}
		
		return Optional.of(ede.getEntity());
    }

}
