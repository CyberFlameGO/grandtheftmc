package net.grandtheftmc.gtm.listeners;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.j0ach1mmall3.wastedguns.api.events.NetgunHitEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponDamageEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponSneakEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponReloadEvent;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserManager;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.items.AmmoType;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.LockedWeapon;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class WeaponUse implements Listener {

    private final List<String> spawnBlocked = Arrays.asList("grenade", "molotovcocktail", "teargas", "stickybomb",
            "proximitymine", "grenadelauncher", "hominglauncher", "rpg", "netlauncher");

    private boolean blockedWhileFlyingJetpack(String weapon) {
    	
    	// allowed weapons
        if (Arrays.asList("baseballbat", "dildo", "katana", "knife", "nightstick", "rake", "pistol", "combatpistol", "heavypistol", "stun gun", "sawed-offshotgun", "microsmg").contains(weapon.toLowerCase())){ 
        	return false;        
        }
        
        return true;
    }

    private boolean blockedWhileWearingJetpack(String weapon) {
    	
    	// allowed weapons
        if (Arrays.asList("baseballbat", "dildo", "katana", "knife", "nightstick", "rake", "chainsaw", "pistol", "combatpistol", "heavypistol", "stun gun", "marksmanpistol", "sawed-offshotgun", "microsmg", "smg", "assaultsmg", "combatpdw").contains(weapon.toLowerCase())){
        	return false;
        }
        
        return true;
    }

    private boolean blockedWhileFlyingWingsuit(String weapon) {
        
    	// allowed weapons
    	if (Arrays.asList("baseballbat", "dildo", "katana", "knife", "nightstick", "rake", "chainsaw", "pistol", "combatpistol", "heavypistol", "stun gun", "sawed-offshotgun", "microsmg", "smg", "assaultsmg", "combatpdw", "gusenbergsweeper", "assaultrifle", "carbinerifle", "bullpuprifle", "advancedrifle", "specialcarbine").contains(weapon.toLowerCase())){
        	return false;
        }
        
        return true;
    }

    private boolean blockedWhileWearingWingsuit(String weapon) {
        
    	// allowed weapons
    	if (Arrays.asList("baseballbat", "dildo", "katana", "knife", "nightstick", "rake", "chainsaw", "pistol", "combatpistol", "heavypistol", "stun gun", "marksmanpistol", "sawed-offshotgun", "microsmg", "smg", "assaultsmg", "combatpdw", "gusenbergsweeper", "sawed-offshotgun", "pumpshotgun", "musket", "assaultshotgun", "heavyshotgun", "assaultrifle", "carbinerifle", "bullpuprifle", "advancedrifle", "specialcarbine").contains(weapon.toLowerCase())){
        	return false;
        }

        return true;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAmmoChange(AmmoUpdateEvent e) {
    	
    	// grab event variables
    	Player p = e.getPlayer();
    	
    	// grab user
        GTMUser user = GTMUserManager.getInstance().getUser(p.getUniqueId()).orElse(null);
        
        if (user != null){
        	
            for (AmmoType type : AmmoType.getTypes()){
                e.getAmmo().put(type.name(), user.getAmmo(type));
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReload(RangedWeaponReloadEvent e) {
        Weapon weapon = e.getWeapon();

        // if weapon does not have ammo, or if entity in event is not a player
        if (weapon.getAmmoType() == null || !(e.getLivingEntity() instanceof Player))
            return;

        AmmoType type = AmmoType.getAmmoType(weapon.getAmmoType().getType());

        // if unable to find ammo type
        if (type == null)
            return;

        // get the player
        Player player = (Player) e.getLivingEntity();
        GTMUser user = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);

        LockedWeapon lockedWeapon = LockedWeapon.getWeapon(weapon.getCompactName());

        // is this a locked weapon
        if (lockedWeapon != null) {

            // get core user
            User coreUser = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
            if (coreUser != null) {

                // if the core rank is not higher than or equal to locked weapon rank
                if (!(coreUser.getUserRank() == lockedWeapon.getUserRank() || coreUser.getUserRank().isHigherThan(lockedWeapon.getUserRank()))) {

                    if (user != null) {

                        // if the gtm rank is not higher than or weapon to locked weapon rank
                        if (!(user.getRank() == lockedWeapon.getGTMRank() || user.getRank().isHigherThan(lockedWeapon.getGTMRank()))) {
                            player.sendMessage(Lang.HEY.f("&7You need to rank up to " + lockedWeapon.getGTMRank().getColoredNameBold() + "&7 or donate for " + lockedWeapon.getUserRank().getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use this weapon!"));
                            e.setCancelled(true);
                            return;
                        }
                    }
                }
            }
        }
        
        if (user != null){
            int ammo = user.getAmmo(type);
            if (ammo <= 0) {
                e.setCancelled(true);
                player.sendMessage(Lang.AMMO.f("&7You are out of ammo for this weapon!"));
            } else if (ammo < e.getAmmoToReload()) {
                e.setAmmoToReload(ammo);
                user.removeAmmo(type, ammo);
            } else
                user.removeAmmo(type, e.getAmmoToReload());
        }
        else{
        	e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeaponUse(WeaponRightClickEvent e) {
        if (!(e.getLivingEntity() instanceof Player))
            return;

        Player player = (Player) e.getLivingEntity();
        Weapon weapon = e.getWeapon();
        if (Objects.equals("spawn", player.getWorld().getName())) {
            if (this.spawnBlocked.contains(weapon.getCompactName().toLowerCase())) {
                e.setCancelled(true);
                return;
            }
        }

        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
//        LockedWeapon lockedWeapon = LockedWeapon.getWeapon(e.getWeapon().getCompactName().toUpperCase());
//        if(lockedWeapon != null && !lockedWeapon.canUseWeapon(gtmUser.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
//            player.sendMessage(Lang.HEY.f("&7You need to rank up to " + lockedWeapon.getGTMRank().getColoredNameBold() + "&7 or donate for " + lockedWeapon.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use this weapon!"));
//            e.setCancelled(true);
//            return;
//        }

        LockedWeapon lockedWeapon = LockedWeapon.getWeapon(weapon.getCompactName());
        if (lockedWeapon != null) {
            //ServerUtil.debug("1 - " + lockedWeapon.name());
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            //ServerUtil.debug("2 - " + user.getUserRank().name() + " (has:" + (user.getUserRank() == lockedWeapon.getUserRank() || user.getUserRank().isHigherThan(lockedWeapon.getUserRank())) + ")");
            //ServerUtil.debug("3 - " + gtmUser.getRank().name() + " (has:" + (gtmUser.getRank() == lockedWeapon.getGTMRank() || gtmUser.getRank().isHigherThan(lockedWeapon.getGTMRank())) + ")");

            if (!(user.getUserRank() == lockedWeapon.getUserRank() || user.getUserRank().isHigherThan(lockedWeapon.getUserRank()))
                    && !(gtmUser.getRank() == lockedWeapon.getGTMRank() || gtmUser.getRank().isHigherThan(lockedWeapon.getGTMRank()))) {
                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + lockedWeapon.getGTMRank().getColoredNameBold() + "&7 or donate for " + lockedWeapon.getUserRank().getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use this weapon!"));
                e.setCancelled(true);
                //ServerUtil.debug("Blocked.");
                return;
            }
        }
        
        if (gtmUser != null){
            if (gtmUser.isArrested()) {
                player.sendMessage(Lang.JAIL.f("&7You can't use weapons in jail!"));
                e.setCancelled(true);
                return;
            }

            if (gtmUser.hasTeleportProtection()) {
                e.setCancelled(true);
                player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(gtmUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
                return;
            }
        }

        ItemStack chestPlate = player.getInventory().getChestplate();
        if (chestPlate != null && chestPlate.getType() == Material.ELYTRA) {
            if (this.blockedWhileWearingWingsuit(weapon.getCompactName())) {
                player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while wearing a wingsuit!"));
                e.setCancelled(true);
                return;
            }
            if (player.isGliding() && this.blockedWhileFlyingWingsuit(weapon.getCompactName())) {
                player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while flying a wingsuit!"));
                e.setCancelled(true);
                return;
            }
        }

        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE){
            if (this.blockedWhileWearingJetpack(weapon.getCompactName())) {
                player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while wearing a jetpack!"));
                e.setCancelled(true);
                return;
            }
	        if (player.isFlying() && this.blockedWhileFlyingJetpack(weapon.getCompactName())) {
	            player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while flying a jetpack!"));
	            e.setCancelled(true);
	            return;
	        }
        }

        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse() || user.isInsidePremiumHouse()){
            e.setCancelled(true);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWeaponUse(WeaponSneakEvent e) {
        if (!(e.getLivingEntity() instanceof Player))
            return;
        Player player = (Player) e.getLivingEntity();
        Weapon weapon = e.getWeapon();
        if (Objects.equals("spawn", player.getWorld().getName())) {
            if (this.spawnBlocked.contains(weapon.getCompactName().toLowerCase())) {
                e.setCancelled(true);
                return;
            }
        }
        
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        
        LockedWeapon lockedWeapon = LockedWeapon.getWeapon(weapon.getCompactName());
        if (lockedWeapon != null) {
        	
            User coreUser = UserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
            if (coreUser == null){
            	e.setCancelled(true);
            	return;
            }
            
            if (!(coreUser.getUserRank() == lockedWeapon.getUserRank() || coreUser.getUserRank().isHigherThan(lockedWeapon.getUserRank()))
                    && !(gtmUser.getRank() == lockedWeapon.getGTMRank() || gtmUser.getRank().isHigherThan(lockedWeapon.getGTMRank()))) {
                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + lockedWeapon.getGTMRank().getColoredNameBold() + "&7 or donate for " + lockedWeapon.getUserRank().getColoredNameBold() + "&7 at &a&l" + Core.getSettings().getStoreLink() + "&7 to use this weapon!"));
                e.setCancelled(true);
                return;
            }
        }
        
        if (gtmUser != null){
            if (gtmUser.isArrested()) {
                player.sendMessage(Lang.JAIL.f("&7You can't use weapons in jail!"));
                e.setCancelled(true);
                return;
            }
            if (gtmUser.hasTeleportProtection()) {
                e.setCancelled(true);
                player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(gtmUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
                return;
            }
        }

        ItemStack chestPlate = player.getInventory().getChestplate();

        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE)
            if (this.blockedWhileWearingJetpack(weapon.getCompactName())) {
                e.setCancelled(true);
                return;
            }
        if (player.isFlying() && this.blockedWhileFlyingJetpack(weapon.getCompactName())) {
            e.setCancelled(true);
            return;
        }
        HouseUser user = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isInsideHouse() || user.isInsidePremiumHouse())
            e.setCancelled(true);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShoot(WeaponDamageEvent e) {
        if (!(e.getLivingEntity() instanceof Player) || !(e.getEntity() instanceof Player) || Objects.equals("spawn", e.getEntity().getWorld().getName()))
            return;

        // grab event variables
        Weapon weapon = e.getWeapon();
        Player player = (Player) e.getLivingEntity();
        Player victim = (Player) e.getEntity();
        UUID victimUUID = victim.getUniqueId();
        
        GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
        GTMUser victimGtmUser = GTMUserManager.getInstance().getUser(victimUUID).orElse(null);
        
        if (victimGtmUser != null && victimGtmUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7That player has teleport protection for &c&l" + Utils.timeInMillisToText(victimGtmUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
            return;
        }

        if (gtmUser != null && gtmUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(gtmUser.getTimeUntilTeleportProtectionExpires()) + "&7 to damage players!"));
            return;
        }

        HouseUser victimHouseUser = Houses.getUserManager().getLoadedUser(victimUUID);
        HouseUser playerHouseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (victimHouseUser.isInsideHouse() || victimHouseUser.isInsidePremiumHouse()) {
            e.setCancelled(true);
            player.sendMessage(Lang.HOUSES.f("&7You can't hurt players inside a house!"));
            return;
        }

        if (playerHouseUser.isInsideHouse() || playerHouseUser.isInsidePremiumHouse()) {
            e.setCancelled(true);
            player.sendMessage(Lang.HOUSES.f("&7You can't hurt players while inside a house!"));
        }


//        Gang victimGang = victimGtmUser.getGang();
//        Gang damagerGang = gtmUser.getGang();
        Gang victimGang = GangManager.getInstance().getGangByMember(victimUUID).orElse(null);
        Gang damagerGang = GangManager.getInstance().getGangByMember(player.getUniqueId()).orElse(null);
        if (victimGang != null && damagerGang != null && !Objects.equals(victim, player)) {
            if (Objects.equals(victimGang, damagerGang)) {
                e.setCancelled(true);
                player.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in your gang!"));
                return;
            }

            if (victimGang.isAllied(damagerGang)) {
                e.setCancelled(true);
                player.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in an allied gang!"));
                return;
            }
        }

        if (weapon.getCompactName().equalsIgnoreCase("flamethrower")) {
            victim.setFireTicks(victim.getFireTicks() + 20);
            victim.getNearbyEntities(5, 0, 5).forEach(entity -> {
                if (entity == victim || entity.getType() != EntityType.PLAYER) return;
                Player target = (Player) entity;
                if (target.getGameMode() != GameMode.ADVENTURE) return;
                target.setFireTicks(target.getFireTicks() + 10);
            });
            return;
        }
        
        // grab the player jetpack
        ItemStack chestPlate = player.getInventory().getChestplate();
        
        // if player is wearing a chestplate of some sort
        if (chestPlate != null){
        	
            // if source player is wearing a jetpack
            if (chestPlate.getType().equals(Material.GOLD_CHESTPLATE)){
            	if (blockedWhileWearingJetpack(weapon.getCompactName())){
            		e.setCancelled(true);
            		player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while wearing a jetpack!"));
                	return;
            	}
            }
            
            // if source player is wearing a wingsuit
            if (chestPlate.getType().equals(Material.ELYTRA)){
            	if (blockedWhileWearingWingsuit(weapon.getCompactName())){
            		e.setCancelled(true);
            		player.sendMessage(Lang.VEHICLES.f("&7The weapon " + weapon.getCompactName() + " cannot be used while wearing a wingsuit!"));
                	return;
            	}
            }
        }
        
        if (weapon.getCompactName().equalsIgnoreCase("stungun") && victim.getInventory().getChestplate() != null && victim.getInventory().getChestplate().getType().equals(Material.GOLD_CHESTPLATE)) {
            victimGtmUser.disableJetpack();
            victim.setFlying(false);
        }

        if (gtmUser != null && gtmUser.getJobMode() != JobMode.COP) return;
        if (victimGtmUser != null && victimGtmUser.getJobMode() == JobMode.COP) {
            e.setCancelled(true);
            player.sendMessage(Utils.f(Lang.HEY + "&cYou can't kill cops!"));
            return;
        }

        if (victimGtmUser != null && victimGtmUser.getJobMode() == JobMode.CRIMINAL && victimGtmUser.getWantedLevel() == 0) {
            player.sendMessage(Lang.HEY.f("&7You can't damage citizens that are not wanted!"));
            e.setCancelled(true);
            return;
        }

        if (gtmUser != null && gtmUser.getJobMode() == JobMode.COP) {
            if (weapon.getCompactName().equalsIgnoreCase("nightstick")) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
            }
        }

        if (!"stungun".equalsIgnoreCase(weapon.getCompactName()) || victimGtmUser.getJobMode() != JobMode.CRIMINAL || victimGtmUser.getWantedLevel() == 0)
            return;

        if (victim.getLastDamageCause() == null || victim.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.DRAGON_BREATH)
            return;

        if ((chestPlate != null && (chestPlate.getType() == Material.GOLD_CHESTPLATE || chestPlate.getType() == Material.ELYTRA) && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) || player.isFlying() || player.isGliding()) {
            player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals during flight!"));
            return;
        }

        if (player.getVehicle() != null) {
            player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals while in a Vehicle!"));
            return;
        }

        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        User victimUser = Core.getUserManager().getLoadedUser(victimUUID);
        int wantedLevel = victimGtmUser.getWantedLevel();
        int timeInJail = GTMUtils.getTimeInJail(wantedLevel);
        victimGtmUser.jail(timeInJail, player);
        player.sendMessage(Lang.COP_MODE.f("&7You arrested &a" + victimUser.getColoredName(victim)
                + "&7! He will go to jail for &a" + Utils.timeInSecondsToText(timeInJail) + "&7!"));
        Utils.broadcastExcept(player, Lang.COP_MODE.f("&a" + victimUser.getColoredName(victim) + "&7 was arrested by &a"
                + user.getColoredName(player) + "&7!"));
        victimGtmUser.addDeaths(1);
        victimGtmUser.setLastTag(-1);
        victimGtmUser.setKillCounter(0);
        victimGtmUser.setKillStreak(0);
        victimGtmUser.unsetCompassTarget(victim, victimUser);
        if (GTM.getWarpManager().cancelTaxi(victim, victimGtmUser))
            victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));
        victim.setHealth(victim.getMaxHealth());
        victim.spigot().respawn();
        victim.setFireTicks(0);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.setFlying(true);
        victim.getActivePotionEffects().clear();
        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
        victim.setFoodLevel(20);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
        victim.setFlySpeed(0);
        GTMUtils.removeBoard(victim);
        double lostMoney = Utils.round(victimGtmUser.getMoney() / 2);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null)
                    return;

                User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                GTMUser victimGameUser = GTM.getUserManager().getLoadedUser(victim.getUniqueId());
                victim.sendMessage(Lang.JAIL.f("&7You were arrested and have to stay in jail for &a"
                        + Utils.timeInSecondsToText(timeInJail) + "&7!"));
                if (lostMoney > 0)
                    victim.sendMessage(Lang.MONEY_TAKE.f(String.valueOf(lostMoney)));
                victim.teleport(GTM.getWarpManager().getJail().getLocation());
                victim.setGameMode(GameMode.ADVENTURE);
                victim.getActivePotionEffects().clear();
                victim.setFoodLevel(20);
                victim.setFlying(false);
                victim.setFlySpeed(0.1F);
                GTMUtils.giveGameItems(victim);
                GTMUtils.updateBoard(victim, victimGameUser);
            }
        }.runTaskLater(GTM.getInstance(), 150);
        ItemStack[] contents = victim.getInventory().getContents();
        victim.getInventory().clear();
        Location loc = victim.getLocation();
        for (ItemStack item : contents)
            if (item != null && item.getType() != Material.WATCH && item.getType() != Material.COMPASS
                    && item.getType() != Material.CHEST)
                loc.getWorld().dropItemNaturally(loc, item);
        if (lostMoney > 0) {
            victimGtmUser.takeMoney(lostMoney);
            gtmUser.addMoney(lostMoney);
            player.sendMessage(Lang.MONEY.f("&7You confiscated &a$&l" + lostMoney + "&7 of &a"
                    + victimUser.getColoredName(victim) + "&7's money!"));
        }
        int copMoney = GTMUtils.getCopMoney(wantedLevel);
        gtmUser.addMoney(copMoney);
        player.sendMessage(Lang.COP_MODE.f("&7You were rewarded &a$&l" + copMoney + "&7 for arresting a player with &e"
                + GTMUtils.getWantedLevelStars(wantedLevel) + " (" + wantedLevel + ")&7!"));
        Utils.sendTitle(victim, "&c&lBUSTED", "&7Arrested by " + player.getName(), 80, 50, 20);
        GTMUtils.updateBoard(player, user, gtmUser);
        GTMUtils.arrestPlayer(e, weapon, player, victim);
    }
    
    /**
     * Listens in on netgun hit events.
     * <p>
     * This is fired when a netgun hits a target, and we listen 
     * on low priority so other events can mutate this for 
     * permission and changes later.
     * <p>
     * 
     * @param event - the event
     */
    @EventHandler (priority = EventPriority.LOW)
    public void onNetgunHitLow(NetgunHitEvent event){
    	
    	// grab event variables
    	Entity shooter = event.getShooter();
    	Entity target = event.getTarget().orElse(null);
    	Location loc = event.getLocation();
    	int duration = event.getDuration();
    	
    	// if target is alive and not dead
    	if (target != null && !target.isDead() && target instanceof LivingEntity){
    		
    		// grab target chestplate
    		LivingEntity livingTarget = (LivingEntity) target;
    		ItemStack chestPlate = livingTarget.getEquipment().getChestplate();
    		
    		if (chestPlate != null){
    			
    			// if wearing wingsuit
    			if (chestPlate.getType() == Material.ELYTRA){
    				duration = (int) (duration * 3.0);
    			}   			
    			// if wearing jetpack
    			else if (chestPlate.getType() == Material.GOLD_CHESTPLATE){
    				duration = (int) (duration * 2.0);
    			}
    		}
    	}
    	
    	// mutate duration for listeners of event
    	event.setDuration(duration);
    }
    
    /**
     * Listens in on netgun hit events.
     * <p>
     * This is fired when a netgun hits a target, and we listen 
     * on high priority as it's after a permission check
     * 
     * Note: If event is cancelled already, skip this code.
     * <p>
     * 
     * @param event - the event
     */
    @EventHandler (priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onNetgunHitHigh(NetgunHitEvent event){
    	
    	// grab event variables
    	Entity shooter = event.getShooter();
    	Entity target = event.getTarget().orElse(null);
    	Location loc = event.getLocation();
    	int duration = event.getDuration();
    	
    	// if target is valid
    	if (target != null && !target.isDead()){
    		if (target instanceof Player){
    			
    			// grab player
    			Player targetPlayer = (Player) target;
    			GTMUser gtmUser = GTMUserManager.getInstance().getUser(targetPlayer.getUniqueId()).orElse(null);
    			if (gtmUser != null){
    				
    				// disable jetpack for a quarter length of stun
    				int disableTicks = (int) (duration / 4.0);
    				// convert from ticks to milliseconds
    				gtmUser.setEnableJetpackTime(System.currentTimeMillis() + (disableTicks * 50));
    				
    				// disable fly
    				targetPlayer.setFlying(false);
    			}
    		}
    	}
    }
}
