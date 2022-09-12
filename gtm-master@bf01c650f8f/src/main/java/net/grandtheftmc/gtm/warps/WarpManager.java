package net.grandtheftmc.gtm.warps;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.events.TPEvent;
import net.grandtheftmc.gtm.events.TPEvent.TPType;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.TaxiTarget;
import net.grandtheftmc.gtm.users.TaxiTarget.TargetType;

public class WarpManager {

    private Warp tutorialSpawn;
    private Warp spawn;
    private Warp jail;
    private List<Warp> warps = new ArrayList<>();
    private Map<Location, String> warpPads = new HashMap<>();
    private final HashMap<UUID, WarpCache> warpCache = Maps.newHashMap();
    private final HashSet<UUID> toRemove = Sets.newHashSet();

    public WarpManager() {
        this.loadWarps();

        new BukkitRunnable() {
            @Override public void run() {
                if(!warpCache.isEmpty()) {
                	
                	// for each player 
                    for (UUID uuid : warpCache.keySet()) {
                    	
                    	try{
	                        Player player = Bukkit.getPlayer(uuid);
	                        if (player == null || !player.isOnline()) {
	                            toRemove.add(uuid);
	                            continue;
	                        }
	
	                        WarpCache cache = warpCache.get(uuid);
	                        if (cache == null) {
	                            toRemove.add(uuid);
	                            continue;
	                        }
	
	                        int timer = cache.getGtmUser().getTaxiTimer();
	                        if (cache.getGtmUser().isInCombat()) {
	                            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You can't call a cab while in combat!"));
	                            cache.getGtmUser().unsetTaxiTarget();
	                            toRemove.add(uuid);
	                            continue;
	                        }
	
	                        if (timer == 15 || timer == 10 || (timer <= 5 && timer > 0)) {
	                            player.sendMessage(Utils.f(Lang.TAXI + "&eYour taxi is arriving in &a" + timer + " &esecond"
	                                    + (timer == 1 ? "" : "s") + '!'));
	                            if (timer == 1) {
	                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0));
	                                player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.5F, 1);
	                            } else
	                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F / timer, 2);
	                        }
	
	                        if (timer == 0) {
	                            if (!cache.getUser().isRank(UserRank.ELITE)) {
	                                if (!cache.getGtmUser().hasMoney(cache.getGtmUser().getTaxiPrice())) {
	                                    if (cache.getGtmUser().hasBank(cache.getGtmUser().getTaxiPrice())) {
	                                        cache.getGtmUser().withdrawFromBank(cache.getGtmUser().getTaxiPrice());
	                                    } else {
	                                        player.sendMessage(Utils.f(Lang.TAXI + "&eYou can't afford to pay &a$&l"
	                                                + cache.getGtmUser().getTaxiPrice() + "&e for the ride! Taxi cancelled."));
	                                        toRemove.add(uuid);
	                                        continue;
	                                    }
	                                }
	                            }
	
	                            TaxiTarget target = cache.getGtmUser().getTaxiTarget();
	                            if (target == null) {
	                                cache.getGtmUser().unsetTaxiTarget();
	                                player.sendMessage(Lang.TAXI.f("&eYour target could not be reached!"));
	                                toRemove.add(uuid);
	                                continue;
	                            }
	
	                            Location tpLoc = target.getExactLocation();
	                            TPEvent e = new TPEvent(player, target.getTargetPlayer(),
	                                    target.getType() == TargetType.PLAYER ? TPType.TP_COMPLETE : TPType.WARP).call();
	                            if (e.isCancelled()) {
	                                cache.getGtmUser().unsetTaxiTarget();
	                                toRemove.add(uuid);
	                                player.sendMessage(Lang.TAXI.f(e.getCancelMessage()));
	                                continue;
	                            }
	
	                            if (e.targetLocationIsChanged())
	                                tpLoc = e.getTargetLocation();
	                            
	                            // if warping an entity or player
	                            if (target.getType() == TargetType.ENTITY || target.getType() == TargetType.PLAYER){
	                            	
	                            	// if they are tping to air
	                            	if (tpLoc != null && tpLoc.getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR){
	                            		tpLoc = tpLoc.getWorld().getHighestBlockAt(tpLoc).getLocation();
	                            	}
	                            }
	
	                            if (tpLoc == null) {
	                                cache.getGtmUser().unsetTaxiTarget();
	                                toRemove.add(uuid);
	                                player.sendMessage(Lang.TAXI.f("&eYour destination could not be reached!"));
	                                continue;
	                            }
	
	                            player.teleport(tpLoc);
	                            cache.getGtmUser().setLastTeleport();
	                            if(cache.getGtmUser().getCheatCodeState(CheatCode.SNEAKY).getState()== State.ON) {
	                                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 20*10, 0));
	                            }
	                            int price = cache.getGtmUser().getTaxiPrice();
	                            cache.getGtmUser().unsetTaxiTarget();
	                            player.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi dropped you off at your destination" + (target.getWarp() != null ? " (" + target.getWarp().getName() + ")" : ".")));
	                            if (price > 0 && !cache.getUser().isRank(UserRank.ELITE)) {

	                            	// If user has money on hand, take it.
	                            	if (cache.getGtmUser().hasMoney(price))
	                                    cache.getGtmUser().takeMoney(price);

	                            	// If user has money in bank, take it.
	                            	else if (cache.getGtmUser().hasBank(price))
	                            		cache.getGtmUser().takeBank(price);

	                                GTMUtils.updateBoard(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), cache.getGtmUser());
	                                Lang.MONEY_TAKE.f(String.valueOf(price));
	                            }
	
	                            toRemove.add(uuid);
	                            continue;
	                        }
	
	                        cache.getGtmUser().setTaxiTimer(timer - 1);
                    	}
	                    catch(Exception e){
	                    	// if we get here there's a null pointer somewhere
	                    	// stops bug affecting multiple users
	                    	e.printStackTrace();
	                    	toRemove.add(uuid);
	                    }
                    }
                }

                if(toRemove.isEmpty()) return;
                for(UUID uuid : toRemove) warpCache.remove(uuid);
                toRemove.clear();
            }
        }.runTaskTimer(GTM.getInstance(), 20L, 20L);
    }

    public Warp getTutorialSpawn() {
        return this.tutorialSpawn;
    }

    public void setTutorialSpawn(Location location) {
        this.tutorialSpawn = new Warp("tutorialSpawn", location);
    }

    public Warp getSpawn() {
        return this.spawn;
    }

    public void setSpawn(Location location) {
        this.spawn = new Warp("spawn", location);
    }

    public Warp getJail() {
        return this.jail;
    }

    public void setJail(Location location) {
        this.jail = new Warp("jail", location);
    }

    public List<Warp> getWarps() {
        return this.warps;
    }

    public Warp getRandomWarp() {
        if (this.warps.isEmpty())
            return null;
        return this.warps.get(Utils.getRandom().nextInt(this.warps.size()));
    }

    public Warp getWarp(String warpName) {
        return this.warps.stream().filter(warp -> warp.getName().equalsIgnoreCase(warpName)).findFirst().orElse(null);
    }

    public void addWarp(Warp warp) {
        this.warps.add(warp);
    }

    public void removeWarp(Warp warp) {
        this.warps.remove(warp);
    }

    public boolean cancelTaxi(Player player, GTMUser gtmUser) {
//        if (gtmUser.getTaxiTaskId() == -1)
//            return false;
//        Bukkit.getScheduler().cancelTask(gtmUser.getTaxiTaskId());
        if(player == null || gtmUser == null) return false;
        if(!warpCache.containsKey(player.getUniqueId())) return false;

        gtmUser.unsetTaxiTarget();
        warpCache.remove(player.getUniqueId());

        return true;
    }

    public void warp(Player player, User user, GTMUser gtmUser, TaxiTarget target) {
        this.warp(player, user, gtmUser, target, 0, -1, null);
    }

    public void warp(Player player, User user, GTMUser gtmUser, TaxiTarget target, int price) {
        this.warp(player, user, gtmUser, target, price, -1, null);
    }

    public void warp(Player player, User user, GTMUser gtmUser, TaxiTarget target, int price, int delay) {
        this.warp(player, user, gtmUser, target, price, delay, null);
    }

    public void warp(Player player, User user, GTMUser gtmUser, TaxiTarget target, int price, int delay, String msg) {
        GTMUtils.giveGameItems(player);
        if (delay < 0) delay = GTMUtils.getWarpDelay(user.getUserRank());
        UUID uuid = player.getUniqueId();

        if (gtmUser.cancelVehicleTeleport())
            player.sendMessage(Lang.VEHICLES.f("&7You cancelled " + (gtmUser.isSendAway() ? "sending away" : "calling") + " your personal vehicle!"));

        if (this.cancelTaxi(player, gtmUser))
            player.sendMessage(Utils.f(Lang.TAXI + "&eThe previous taxi was cancelled."));

        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Lang.TAXI.f("&7You can't teleport to players while you're dead!"));
            return;
        }

        if (gtmUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You can't call a cab while in combat!"));
            return;
        }

        if (user.isInTutorial()) return;
        if (gtmUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't teleport in jail!"));
            return;
        }

        if (price > 0 && !user.isRank(UserRank.ELITE)) {
            if (!gtmUser.hasMoney(price) && !gtmUser.hasBank(price)) {
                player.sendMessage(Utils.f(Lang.TAXI + "&eYou can't afford to pay &a$&l"
		                + price + "&e for the ride! Taxi cancelled."));
            }
        }

        if (target == null || target.getExactLocation() == null) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eThat location does not exist!"));
            return;
        }

        player.sendMessage(Utils.f(Lang.TAXI + "&eYou called a taxi!"));
        gtmUser.setTaxiTimer(delay);
        gtmUser.setTaxiTarget(target);
        gtmUser.setTaxiPrice(price);

        this.warpCache.put(uuid, new WarpCache(user, gtmUser, target, price, delay));
    }

    public void tpa(Player player, User user, GTMUser gtmUser, Player target) {
        GTMUtils.giveGameItems(player);
        if (target == null) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eThat player is not online!"));
            return;
        }
        GTMUser targetGTMUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (!user.isSpecial()) {
            player.sendMessage(Utils.f(Lang.TAXI
                    + "&eYou don't have access to teleport to other players! Buy &6&lVIP&e to unlock it!"));
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Lang.TAXI.f("&7You can't teleport to players while you're dead!"));
            return;
        }
        if (gtmUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You can't call a cab while in combat!"));
            return;
        }
        if (targetGTMUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + target.getDisplayName() + "&7 is in combat!"));
            return;
        }
        if (user.isInTutorial()) return;
        if (gtmUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't teleport in jail!"));
            return;
        }
        UUID uuid = player.getUniqueId();
        if (gtmUser.cancelVehicleTeleport())
            player.sendMessage(Lang.VEHICLES.f("&7You cancelled " + (gtmUser.isSendAway() ? "sending away" : "calling") + " your personal vehicle!"));
        if (this.cancelTaxi(player, gtmUser))
            player.sendMessage(Utils.f(Lang.TAXI + "&eThe previous taxi was cancelled."));

        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        if (!targetUser.getPref(Pref.TP_REQUESTS)) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eThat player has disabled teleport requests!"));
            return;
        }
        if (targetUser.isInTutorial()) {
            player.sendMessage(Lang.TUTORIALS.f("&7That player is in a tutorial!"));
            return;
        }
        TPEvent e = new TPEvent(player, target, TPType.TPA_REQ).call();
        if (e.isCancelled()) {
            gtmUser.unsetTpaRequests();
            player.sendMessage(Lang.TAXI.f(e.getCancelMessage()));
            return;
        }
        player.sendMessage(
                Utils.f(Lang.TAXI + "&eYou requested to teleport to " + targetUser.getColoredName(target) + "&e!"));
        target.sendMessage(Utils.f(Lang.TAXI + user.getColoredName(player)
                + "&e requested to teleport to you. Use &a\'/tpaccept\'&e to accept."));
        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        gtmUser.setTpaRequestSentUUID(target.getUniqueId());
        gtmUser.setTpaHere(false);
        targetGtmUser.setTpaRequestUUID(uuid);
    }

    public void tpaHere(Player player, User user, GTMUser gtmUser, Player target) {
        GTMUtils.giveGameItems(player);
        if (target == null) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eThat player is not online!"));
            return;
        }
        GTMUser targetGTMUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (!user.getUserRank().isHigherThan(UserRank.PREMIUM)) {
            player.sendMessage(Utils.f(Lang.TAXI
                    + "&eYou don't have access to teleport other players to yourself! Buy &b&lELITE&e to unlock it!"));
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Lang.TAXI.f("&7You can't teleport players to you while you're dead!"));
            return;
        }
        if (gtmUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't teleport players to yourself in jail!"));
            return;
        }
        if (gtmUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You can't request teleportation while in combat!"));
            return;
        }
        if (targetGTMUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + target.getDisplayName() + "&7 is in combat!"));
            return;
        }
        if (user.isInTutorial()) return;
        if (target.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Lang.TAXI.f("&7You can't teleport dead players to you!"));
            return;
        }
        UUID uuid = player.getUniqueId();
        if (gtmUser.cancelVehicleTeleport())
            player.sendMessage(Lang.VEHICLES.f("&7You cancelled " + (gtmUser.isSendAway() ? "sending away" : "calling") + " your personal vehicle!"));
        if (this.cancelTaxi(player, gtmUser))
            player.sendMessage(Utils.f(Lang.TAXI + "&eThe previous taxi was cancelled."));
        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        if (!targetUser.getPref(Pref.TP_REQUESTS)) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eThat player has disabled teleport requests!"));
            return;
        }
        if (targetUser.isInTutorial()) {
            player.sendMessage(Lang.TUTORIALS.f("&7That player is in a tutorial!"));
            return;
        }
        TPEvent e = new TPEvent(player, target, TPType.TPAHERE_REQ).call();
        if (e.isCancelled()) {
            gtmUser.unsetTpaRequests();
            player.sendMessage(Lang.TAXI.f(e.getCancelMessage()));
            return;
        }
        player.sendMessage(
                Utils.f(Lang.TAXI + "&eYou requested " + targetUser.getColoredName(target) + "&e to teleport to you!"));
        target.sendMessage(Utils.f(Lang.TAXI + user.getColoredName(player)
                + "&e requested you to teleport to them. Use &a\'/tpaccept\'&e to accept."));
        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        gtmUser.setTpaRequestSentUUID(target.getUniqueId());
        gtmUser.setTpaHere(true);
        targetGtmUser.setTpaRequestUUID(uuid);
    }

    public void tpDeny(Player player, User user, GTMUser gtmUser) {
        GTMUtils.giveGameItems(player);
        if (!gtmUser.hasTpaRequest()) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eNobody has requested to teleport to you!"));
            return;
        }
        if (user.isInTutorial()) return;
        Player target = Bukkit.getPlayer(gtmUser.getTpaRequestUUID());
        if (target == null) {
            player.sendMessage(Utils.f(Lang.TAXI + "&eNobody has requested to teleport to you!"));
            return;
        }
        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        GTMUser targetGtmUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (!targetUser.isInTutorial())
            target.sendMessage(Lang.TAXI.f("&a" + user.getColoredName(player) + "&e denied your request!"));
        player.sendMessage(Lang.TAXI.f("&e You denied &a" + targetUser.getColoredName(target) + "&e's request!"));
        targetGtmUser.unsetTpaRequests();
        gtmUser.unsetTpaRequests();
    }

    public void tpAccept(Player target, User targetUser, GTMUser targetGtmUser) {
        GTMUtils.giveGameItems(target);
        if (!targetGtmUser.hasTpaRequest()) {
            target.sendMessage(Utils.f(Lang.TAXI + "&eNobody has requested to teleport to you!"));
            return;
        }
        if (target.getGameMode() == GameMode.SPECTATOR) {
            target.sendMessage(Lang.VEHICLES.f("&7You can't teleport while you're dead!"));
            return;
        }
        if (targetGtmUser.isArrested()) {
            target.sendMessage(Lang.JAIL.f("&7You can't teleport in jail!"));
            return;
        }
        if (targetUser.isInTutorial()) return;
        UUID uuid = targetGtmUser.getTpaRequestUUID();
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            target.sendMessage(Utils.f(Lang.TAXI + "&eNobody has requested to teleport to you!"));
            return;
        }
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
        if (gtmUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7Teleport cancelled due to combat tag!"));
            return;
        }
        if (targetGtmUser.isInCombat()) {
            player.sendMessage(Utils.f(Lang.COMBATTAG + "&7Teleport cancelled due to combat tag!"));
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            target.sendMessage(Lang.VEHICLES.f("&7You can't teleport to a dead player!"));
            return;
        }
        if (gtmUser.isArrested()) {
            target.sendMessage(Lang.JAIL.f("&7You can't teleport in jail!"));
            return;
        }
        User user = Core.getUserManager().getLoadedUser(uuid);
        if (user.isInTutorial()) {
            target.sendMessage(Lang.TUTORIALS.f("&7That player is in a tutorial!"));
            return;
        }
        if (gtmUser.isTpaHere()) {
            int delay = GTMUtils.getWarpDelay(targetUser.getUserRank());
            TPEvent e = new TPEvent(target, player, TPType.TPAHERE_ACCEPT).call();
            if (e.isCancelled()) {
                targetGtmUser.unsetTpaRequests();
                gtmUser.unsetTpaRequests();
                target.sendMessage(Lang.TAXI.f(e.getCancelMessage()));
                return;
            }
            target.sendMessage(Utils.f(Lang.TAXI + "&eYou accepted &a" + user.getColoredName(player)
                    + "&e's request to teleport to them!"));
            player.sendMessage(Utils.f(Lang.TAXI + "&a" + targetUser.getColoredName(target) + "&e accepted your request to teleport to them! Their cab will arrive in &a" + delay + "&e second" + (delay == 1 ? "" : "s")));
            targetGtmUser.unsetTpaRequests();
            gtmUser.unsetTpaRequests();
            this.warp(target, targetUser, targetGtmUser, new TaxiTarget(player), 0, delay);
            return;
        }
        int delay = GTMUtils.getWarpDelay(user.getUserRank());
        TPEvent e = new TPEvent(player, target, TPType.TPA_ACCEPT).call();
        if (e.isCancelled()) {
            targetGtmUser.unsetTpaRequests();
            gtmUser.unsetTpaRequests();
            player.sendMessage(Lang.TAXI.f(e.getCancelMessage()));
            return;
        }
        target.sendMessage(Utils.f(Lang.TAXI + "&eYou accepted &a" + user.getColoredName(player)
                + "&e's teleport request. Their cab will arrive in &a" + delay + "&e second" + (delay == 1 ? "" : "s")
                + '!'));
        player.sendMessage(
                Utils.f(Lang.TAXI + "&a" + targetUser.getColoredName(target) + "&e accepted your teleport request!"));
        targetGtmUser.unsetTpaRequests();
        gtmUser.unsetTpaRequests();
        this.warp(player, user, gtmUser, new TaxiTarget(target), 0, delay);
    }

    public void backupAccept(Player player, GTMUser gtmUser, Player target, GTMUser targetGtmUser) {
        User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
        GTMUtils.giveGameItems(target);
        if (targetUser.isInTutorial()) return;
        if (gtmUser.getJobMode() != JobMode.COP) {
            player.sendMessage(Lang.COP_MODE.f("&7You must be in &3&lCOP Mode&7 to request backup!"));
            return;
        }
        if (player.getGameMode() == GameMode.SPECTATOR) {
            player.sendMessage(Lang.VEHICLES.f("&7You can't teleport while you're dead!"));
            return;
        }
        if (target.getGameMode() == GameMode.SPECTATOR || !targetGtmUser.hasRequestedBackup()) {
            player.sendMessage(Lang.COP_MODE.f("&7That player has not requested backup!"));
            return;
        }
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInTutorial())
            return;

        int delay = GTMUtils.getWarpDelay(user.getUserRank());
        TPEvent e = new TPEvent(player, target, TPType.BACKUP).call();
        if (e.isCancelled()) {
            player.sendMessage(Lang.COP_MODE.f(e.getCancelMessage()));
            return;
        }
        target.sendMessage(Lang.COP_MODE.f("&7" + user.getColoredName(player) + "&7 has accepted your backup request. They will arrive in &a" + delay + "&e second" + (delay == 1 ? "" : "s")
                + '!'));
        player.sendMessage(
                Utils.f(Lang.COP_MODE.f("&7You are providing backup to " + targetUser.getColoredName(target) + "&7!")));
        this.warp(player, user, gtmUser, new TaxiTarget(target), 0, delay);
    }


    public Warp getWarpFromPad(Location blockLocation) {
        String name = this.warpPads.get(blockLocation);
        if (name == null)
            return null;
        if ("random".equalsIgnoreCase(name))
            return this.warps.get(Utils.getRandom().nextInt(this.warps.size()));
        return this.getWarp(name);
    }

    public void loadWarps() {
        YamlConfiguration c = GTM.getSettings().getWarpsConfig();
        this.tutorialSpawn = new Warp("spawn", Utils.teleportLocationFromString(c.getString("tutorialSpawn")));
        this.spawn = new Warp("tutorialSpawn", Utils.teleportLocationFromString(c.getString("spawn")));
        this.jail = new Warp("jail", Utils.teleportLocationFromString(c.getString("jail")));
        this.warps = new ArrayList<>();
        if (c.get("warps") != null)
            this.warps.addAll(c.getConfigurationSection("warps").getKeys(false).stream().map(s -> new Warp(s, Utils.teleportLocationFromString(c.getString("warps." + s)))).collect(Collectors.toList()));
        this.warpPads = new HashMap<>();
        if (c.get("warpPads") != null)
            for (String loc : c.getConfigurationSection("warpPads").getKeys(false))
                this.warpPads.put(Utils.blockLocationFromString(loc), c.getString("warpPads." + loc));

    }

    public void saveWarps() {
        YamlConfiguration c = GTM.getSettings().getWarpsConfig();
        c.set("spawn", Utils.teleportLocationToString(this.spawn.getLocation()));
        c.set("tutorialSpawn", Utils.teleportLocationToString(this.tutorialSpawn.getLocation()));
        c.set("jail", Utils.teleportLocationToString(this.jail.getLocation()));
        c.set("warps", null);
        for (Warp warp : this.warps)
            c.set("warps." + warp.getName(), Utils.teleportLocationToString(warp.getLocation()));
        c.set("warpPads", null);
        for (Map.Entry<Location, String> locationStringEntry : this.warpPads.entrySet())
            c.set(Utils.blockLocationToString(locationStringEntry.getKey()), this.warpPads.get(locationStringEntry.getKey()));
        Utils.saveConfig(c, "warps");
    }

}