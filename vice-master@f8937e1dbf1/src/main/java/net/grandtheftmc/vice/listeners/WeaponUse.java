package net.grandtheftmc.vice.listeners;

import com.j0ach1mmall3.wastedguns.api.events.WeaponDamageEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponSneakEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponReloadEvent;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Relation;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.attribute.RankedWeapon;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.AmmoType;
import net.grandtheftmc.vice.users.LockedWeapon;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.world.ZoneFlag;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class WeaponUse implements Listener {
    private final List<String> spawnBlocked = Arrays.asList("grenade", "molotovcocktail", "teargas", "stickybomb",
            "proximitymine", "grenadelauncher", "hominglauncher", "rpg");
    private final List<String> jetpackAllowed = Arrays.asList("Pistol", "CombatPistol", "HeavyPistol", "MarksmanPistol",
            "TearGas", "SawedoffShotgun", "MicroSMG");
    private final List<String> jetpackDisallowedHeavy = Arrays.asList("CombatMG", "MG", "Minigun", "RPG",
            "HomingLauncher", "HeavySniper", "Flamethrower", "GoldMinigun", "NetLauncher");
    private final List<String> wingsuitDisallowed = Arrays.asList("Minigun", "GoldMinigun", "RPG",
            "SniperRifle", "HeavySniper", "NetLauncher", "HomingLauncher", "GrenadeLauncher",
            "CombatMG", "MG", "Flamethrower");

    private Collection<String> recentKatanaChops = new ArrayList<>();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAmmoChange(AmmoUpdateEvent e) {
        ViceUser user = Vice.getUserManager().getLoadedUser(e.getPlayer().getUniqueId());
        for (AmmoType type : AmmoType.getTypes())
            e.getAmmo().put(type.name(), user.getAmmo(type));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onReload(RangedWeaponReloadEvent e) {
        Weapon weapon = e.getWeapon();
        if (weapon.getAmmoType() == null || !(e.getLivingEntity() instanceof Player))
            return;

        AmmoType type = AmmoType.getAmmoType(weapon.getAmmoType().getType());
        if (type == null) return;

        Player player = (Player) e.getLivingEntity();
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        LockedWeapon l = LockedWeapon.getWeapon(weapon.getCompactName());
        if (e.getWeapon() instanceof RankedWeapon) {
            UserRank required = ((RankedWeapon) e.getWeapon()).requiredRank(), current = Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
            if (required != null && (!current.isHigherThan(required) && current != required)) {
                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + l.getViceRank().getColoredNameBold() + "&7 or donate for " + l.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use this weapon!"));
                e.setCancelled(true);
                return;
            }
        }

        int ammo = user.getAmmo(type);
        if (ammo <= 0) {
            e.setCancelled(true);
            player.sendMessage(Lang.AMMO.f("&7You are out of ammo for this weapon!"));
        }
        else if (ammo < e.getAmmoToReload()) {
            e.setAmmoToReload(ammo);
            user.removeAmmo(type, ammo);
        }
        else {
            user.removeAmmo(type, e.getAmmoToReload());
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

        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        LockedWeapon lockedWeapon = LockedWeapon.getWeapon(e.getWeapon().getCompactName().toUpperCase());
        if (lockedWeapon != null && !lockedWeapon.canUseWeapon(viceUser.getRank(), Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank())) {
            player.sendMessage(Lang.HEY.f("&7You need to rank up to " + lockedWeapon.getViceRank().getColoredNameBold() + "&7 or donate for " + lockedWeapon.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use this weapon!"));
            e.setCancelled(true);
            return;
        }

//        if(e.getWeapon() instanceof RankedWeapon) {
//            System.out.println("RankedWeapon: " + true);
//            UserRank required = ((RankedWeapon) e.getWeapon()).requiredRank(), current = Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
//            System.out.println(required.name() + " - " + current.name());
//            if (!current.isHigherThan(required) && current != required) {
//                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + ViceRank.JUNKIE.getColoredNameBold() + "&7 or donate for " + required.getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use this weapon!"));
//                e.setCancelled(true);
//                return;
//            }
//        }

        if (viceUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't use weapons in jail!"));
            e.setCancelled(true);
            return;
        }

        if (viceUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(viceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
            return;
        }

        boolean elytra = player.getInventory().getChestplate() != null && player.getInventory().getChestplate().getType() == Material.ELYTRA;
        if (elytra) {
            if (this.wingsuitDisallowed.contains(e.getWeapon().getCompactName())) {
                player.sendMessage(Lang.VEHICLES.f("&7This weapon cannot be used in a wingsuit!"));
                e.setCancelled(true);
                return;
            }
        }

        ItemStack chestPlate = player.getInventory().getChestplate();
        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE
                && !this.jetpackAllowed.contains(e.getWeapon().getCompactName())
                && player.isFlying()) {
            player.sendMessage(Lang.VEHICLES.f("&7You can't use this weapon in a jetpack!"));
            e.setCancelled(true);
            return;
        }

        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE
                && this.jetpackDisallowedHeavy.contains(e.getWeapon().getCompactName())) {
            player.sendMessage(Lang.VEHICLES.f("&7You can't use this weapon in a jetpack!"));
            e.setCancelled(true);
            return;
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
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        LockedWeapon l = LockedWeapon.getWeapon(weapon.getCompactName());
        if (e.getWeapon() instanceof RankedWeapon) {
            UserRank required = ((RankedWeapon) e.getWeapon()).requiredRank(), current = Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank();
            if (required != null && (!current.isHigherThan(required) && current != required)) {
                player.sendMessage(Lang.HEY.f("&7You need to rank up to " + l.getViceRank().getColoredNameBold() + "&7 or donate for " + l.getUserRank().getColoredNameBold() + "&7 at &a&lstore.grandtheftmc.net&7 to use this weapon!"));
                e.setCancelled(true);
                return;
            }
        }
        if (viceUser.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't use weapons in jail!"));
            e.setCancelled(true);
            return;
        }
        if (viceUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(viceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
            return;
        }
        ItemStack chestPlate = player.getInventory().getChestplate();
        if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR && !this.jetpackAllowed.contains(e.getWeapon().getCompactName())) {
            e.setCancelled(true);
            return;
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onShoot(WeaponDamageEvent e) {
        Weapon weapon = e.getWeapon();
        if (!(e.getLivingEntity() instanceof Player) || !(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getLivingEntity();
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        Player victim = (Player) e.getEntity();
        UUID victimUUID = victim.getUniqueId();
        ViceUser victimViceUser = Vice.getUserManager().getLoadedUser(victimUUID);
        if (victimViceUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7That player has teleport protection for &c&l" + Utils.timeInMillisToText(victimViceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
            return;
        }

        if (viceUser.hasTeleportProtection()) {
            e.setCancelled(true);
            player.sendMessage(Lang.COMBATTAG.f("&7Please wait &c&l" + Utils.timeInMillisToText(viceUser.getTimeUntilTeleportProtectionExpires()) + "&7!"));
            return;
        }

        // TODO make sure players in the same cartel cant shoot eachother
        FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player), fVictim = FPlayers.getInstance().getByPlayer(victim);
        if (fPlayer != null && fVictim != null) {
            if ((fPlayer.getFaction() != null || fVictim.getFaction() != null) && (fPlayer.getFactionId().equalsIgnoreCase(fVictim.getFactionId()) || fPlayer.getFaction().getRelationWish(fVictim.getFaction()) == Relation.ALLY)) {
                player.sendMessage(Utils.f(" &a&lCARTELS&8&l> &7You can't hurt players that are in your gang!"));
            }
        }
        /*
        Gang victimGang = victimViceUser.getGang();
        Gang damagerGang = viceUser.getGang();
        if (victimGang != null && damagerGang != null && !Objects.equals(victim, player)) {
            if (Objects.equals(victimGang, damagerGang)) {
                e.setCancelled(true);
                player.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in your gang!"));
                return;
            }
            if (victimGang.isAllied(damagerGang.getName())) {
                e.setCancelled(true);
                player.sendMessage(Lang.GANGS.f("&7You can't hurt players that are in an allied gang!"));
                return;
            }
        }*/
        if (!Objects.equals("spawn", e.getEntity().getWorld().getName())) {
            if ("stungun".equalsIgnoreCase(weapon.getCompactName()) && viceUser.isCop()) {
                player.sendMessage(Lang.COPS.f("&7You have no jurisdiction in this area!"));
                return;
            }
            if ("flamethrower".equalsIgnoreCase(weapon.getCompactName())) {
                victim.setFireTicks(victim.getFireTicks() + 20);
                victim.getNearbyEntities(5, 0, 5).forEach(entity -> {
                    if (Objects.equals(entity, victim) || entity.getType() != EntityType.PLAYER) return;
                    Player target = (Player) entity;
                    if (target.getGameMode() != GameMode.ADVENTURE && target.getGameMode() != GameMode.SURVIVAL) return;
                    target.setFireTicks(target.getFireTicks() + 10);
                });
                return;
            }
            if ("katana".equalsIgnoreCase(weapon.getCompactName())) {
                if (this.recentKatanaChops.contains(victim.getName())) return;
                if (ThreadLocalRandom.current().nextInt(30) < 5) {
                    ItemStack skull = Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3,
                            "&e&l" + victim.getName() + "'s Decapitated Head"),
                            victim.getName());
                    victim.getWorld().dropItemNaturally(victim.getLocation(), skull);
                    victim.setHealth(0);
                    this.recentKatanaChops.add(victim.getName());
                }
            }
            if (!viceUser.isCop()) return;
            if (victimViceUser.isCop()) {
                e.setCancelled(true);
                player.sendMessage(Utils.f(Lang.HEY + "&cYou can't kill cops!"));
                return;
            }
            if ("nightstick".equalsIgnoreCase(weapon.getCompactName())) {
                victim.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 1));
                victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 50, 1));
            }
        } else if ("stungun".equalsIgnoreCase(weapon.getCompactName())) {
            if (!viceUser.isCop()) return;
            if (ViceUtils.isInSpawnRange(victim, 10)) {
                player.sendMessage(Lang.COPS.f("&7You have no jurisdiction in this area!"));
                return;
            }
            if (victimViceUser.isCop()) {
                e.setCancelled(true);
                player.sendMessage(Utils.f(Lang.HEY + "&cYou can't arrest cops!"));
                return;
            }
            int timeInJail = ViceUtils.getTimeInJailForDrugs(victim);
            if (timeInJail == 0) return;
            if (victim.getLastDamageCause() == null || victim.getLastDamageCause().getCause() != EntityDamageEvent.DamageCause.DRAGON_BREATH)
                return;
            ItemStack chestPlate = player.getInventory().getChestplate();
            if (chestPlate != null && chestPlate.getType() == Material.GOLD_CHESTPLATE && player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals during flight!"));
                return;
            }
            if (player.getVehicle() != null) {
                player.sendMessage(Lang.COP_MODE.f("&fYou may not arrest criminals while in a Vehicle!"));
                return;
            }
            if (Vice.getWorldManager().getZones(player.getLocation()).stream().anyMatch(zone -> zone.getFlags().contains(ZoneFlag.COP_CANT_ARREST))) {
                player.sendMessage(Lang.COP_MODE.f("&7You may not arrest criminals in this area!"));
                return;
            }
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            User victimUser = Core.getUserManager().getLoadedUser(victimUUID);

            victimViceUser.jail(timeInJail, player);
            player.sendMessage(Lang.COP_MODE.f("&7You arrested &a" + victimUser.getColoredName(victim)
                    + "&7! He will go to jail for &a" + Utils.timeInSecondsToText(timeInJail) + "&7!"));
            Utils.broadcastExcept(player, Lang.COP_MODE.f("&a" + victimUser.getColoredName(victim) + "&7 was arrested by &a"
                    + user.getColoredName(player) + "&7!"));
            victimViceUser.addDeaths(1);
            victimViceUser.setLastTag(-1);
            victimViceUser.setKillStreak(0);
            if (Vice.getWorldManager().getWarpManager().cancelTaxi(victim, victimViceUser))
                victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));

            if (Core.getSettings().getType() == ServerType.VICE) {
                victim.setHealth(0);
            } else {
                victim.setHealth(victim.getMaxHealth());
                victim.spigot().respawn();
            }

            victim.setFireTicks(0);
            victim.setGameMode(GameMode.SPECTATOR);
            victim.setFlying(true);
            victim.getActivePotionEffects().clear();
            victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
            victim.setFoodLevel(20);
            victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
            victim.setFlySpeed(0);
            ViceUtils.removeBoard(victim);
//            victimUser.removeCosmetics(victim);
            new BukkitRunnable() {
                @Override
                public void run() {
                    Player victim = Bukkit.getPlayer(victimUUID);
                    if (victim == null)
                        return;
                    User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                    ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
                    victim.sendMessage(Lang.JAIL.f("&7You were arrested and have to stay in jail for &a"
                            + Utils.timeInSecondsToText(timeInJail) + "&7!"));
                    victim.teleport(Vice.getWorldManager().getWarpManager().getJail().getLocation());
                    victim.setGameMode(GameMode.SURVIVAL);
                    victim.getActivePotionEffects().clear();
                    victim.setFoodLevel(20);
                    victim.setFlying(false);
                    victim.setFlySpeed(0.1F);
                    ViceUtils.giveGameItems(victim);
                    ViceUtils.updateBoard(victim, victimGameUser);
//                    victimUser.loadLastCosmetics(victim);
                }
            }.runTaskLater(Vice.getInstance(), 150);
            HashSet<ItemStack> bannedItems = new HashSet<>();
            for (ItemStack is : victim.getInventory().getContents()) {
                if (Vice.getItemManager().getItem(is) != null && Vice.getItemManager().getItem(is).isScheduled()) {
                    bannedItems.add(is);
                    victim.getInventory().removeItem(is);
                }
            }
            ViceUtils.giveGameItems(victim);
            for (ItemStack item : bannedItems)
                Utils.giveItems(player, item);
            Utils.sendTitle(victim, "&c&lBUSTED", "&7Arrested by " + player.getName(), 80, 50, 20);
            ViceUtils.updateBoard(player, user, viceUser);
        }
    }

}
