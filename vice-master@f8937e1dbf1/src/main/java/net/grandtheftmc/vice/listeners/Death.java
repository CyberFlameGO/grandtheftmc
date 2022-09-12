package net.grandtheftmc.vice.listeners;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.events.ArmorEquipEvent;
import net.grandtheftmc.vice.events.EquipArmorType;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.users.storage.BooleanStorageType;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.*;

public class Death implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        new ArrayList<>(e.getDrops()).stream().filter(stack -> stack != null && (stack.getType() == Material.WATCH || stack.getType() == Material.COMPASS || stack.getType() == Material.CHEST)).forEach(e.getDrops()::remove);

        Player victim = e.getEntity();
        Player killer = victim.getKiller();
        UUID victimUUID = victim.getUniqueId();
        ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victimUUID);
        User victimUser = Core.getUserManager().getLoadedUser(victimUUID);

        Vice.getDrugManager().getEffectManager().cancelEffects(victim);

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

        victimGameUser.setKillStreak(0);
        if (Vice.getWorldManager().getWarpManager().cancelTaxi(victim, victimGameUser))
            victim.sendMessage(Utils.f(Lang.TAXI + "&eThe taxi was cancelled!"));
        victim.setHealth(victim.getMaxHealth());
        victim.spigot().respawn();
        victim.setFireTicks(0);
        for (PotionEffect p : victim.getActivePotionEffects()) {
            victim.removePotionEffect(p.getType());
        }

        if(victimGameUser.getVehicleTaskId()!=-1) {
            victimGameUser.cancelVehicleTeleport();
            victim.sendMessage(Lang.VEHICLES.f("&7You can't " + (victimGameUser.getBooleanFromStorage(BooleanStorageType.SEND_AWAY) ? "send away" : "call") + " while you're dead!"));
        }

        victim.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 180, 0), false);
        victim.setGameMode(GameMode.SPECTATOR);
        victim.setVelocity(new Vector());
        victim.setFlying(true);
        victim.setFoodLevel(20);
        victim.playSound(victim.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1, 0.5F);
        victim.setFlySpeed(0);
        ViceUtils.removeBoard(victim);
        //new JLibPlayer(victim).setWorldborderTint(0);
//        victimUser.removeCosmetics(victim);
        new BukkitRunnable() {
            @Override
            public void run() {
                Player victim = Bukkit.getPlayer(victimUUID);
                if (victim == null) return;
                User victimUser = Core.getUserManager().getLoadedUser(victim.getUniqueId());
                ViceUser victimGameUser = Vice.getUserManager().getLoadedUser(victim.getUniqueId());
                victim.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
                for (PotionEffect p : victim.getActivePotionEffects()) {
                    victim.removePotionEffect(p.getType());
                }

                hiddenStaff.forEach(target -> {
                    if (target == null || !target.isOnline()) return;
                    victim.showPlayer(target);
                });

                hiddenStaff.clear();
                victim.setFoodLevel(20);
                victim.setGameMode(GameMode.SURVIVAL);
                victim.setFlying(false);
                victim.setFlySpeed(0.1F);
                ViceUtils.giveGameItems(victim);
                ViceUtils.updateBoard(victim, victimGameUser);
//                victimUser.loadLastCosmetics(victim);

                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.equals(victim)) continue;
                    player.showPlayer(victim);
                }
            }
        }.runTaskLater(Vice.getInstance(), 150);

        Utils.sendTitle(victim, "&c&lWASTED", killer == null ? null : "&7" + ViceUtils.getMessageKilledBy(killer.getName()), 80, 50, 20);

        if (killer == null || killer.equals(victim)) {
            e.setDeathMessage(Utils.f("&e" + victim.getName() + "&7 " + (victim.getLastDamageCause() instanceof EntityDamageByEntityEvent ? "was killed by &c" + ((EntityDamageByEntityEvent) victim.getLastDamageCause()).getDamager().getCustomName() + "&7!" : "died!")));
            return;
        }

        if (Utils.calculateChance(10)) {
            e.getDrops().add(Utils.setSkullOwner(Utils.createItem(Material.SKULL_ITEM, 3, "&e&l" + victim.getName() + "'s Head"), victim.getName()));
            killer.sendMessage(Lang.HEY.f(victimUser.getColoredName(victim) + "&7's head dropped on the ground!"));
        }

        e.setDeathMessage(Utils.f("&e" + victim.getName() + "&7 was killed by &c" + killer.getName() + "&7!"));
        UUID killerUUID = killer.getUniqueId();
        ViceUser killerGameUser = Vice.getUserManager().getLoadedUser(killerUUID);
        User killerUser = Core.getUserManager().getLoadedUser(killerUUID);
        killerGameUser.addKills(1);
        killerGameUser.addKillStreak(1);
        ViceUtils.updateBoard(killer, killerGameUser);
        victim.setBedSpawnLocation(Vice.getWorldManager().getWarpManager().getSpawn().getLocation(), true);
        killer.setBedSpawnLocation(Vice.getWorldManager().getWarpManager().getSpawn().getLocation(), true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeathMonitor(PlayerDeathEvent e) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
            if (!user.getPref(Pref.DEATH_MESSAGES)) {
                if (Objects.equals(player, e.getEntity())) {
                    player.sendMessage(e.getDeathMessage());
                }
                continue;
            }
            player.sendMessage(e.getDeathMessage());
        }

        e.setDeathMessage(null);

        Player p = e.getEntity();
        for(ItemStack i : p.getInventory().getArmorContents()){
            if(i != null && !i.getType().equals(Material.AIR)){
                Bukkit.getServer().getPluginManager().callEvent(new ArmorEquipEvent(p, ArmorEquipEvent.EquipMethod.DEATH, EquipArmorType.matchType(i), i, null));
            }
        }

        Iterator<ItemStack> iter  = e.getDrops().iterator();
        while (iter.hasNext()) {
            ItemStack is = iter.next();
            if(is==null)
                continue;

            switch (is.getType()) {
                case BOW:
                    iter.remove();
                    break;
            }

            if(!is.hasItemMeta()) continue;

            switch (ChatColor.stripColor(is.getItemMeta().getDisplayName())) {
                case "Backpack":
                case "Ammo Pouch":
                case "Phone": {
                    iter.remove();
                    break;
                }
            }
        }
    }
}
