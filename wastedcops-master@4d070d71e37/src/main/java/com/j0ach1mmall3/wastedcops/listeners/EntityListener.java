package com.j0ach1mmall3.wastedcops.listeners;

import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.j0ach1mmall3.wastedcops.Main;
import com.j0ach1mmall3.wastedcops.api.Cop;
import com.j0ach1mmall3.wastedcops.api.events.CopDamagePlayerEvent;
import com.j0ach1mmall3.wastedcops.api.events.CopKillPlayerEvent;
import com.j0ach1mmall3.wastedcops.api.events.PlayerKillCopEvent;
import com.j0ach1mmall3.wastedguns.api.weapons.Weapon;

import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;

public final class EntityListener implements Listener {
    private final Main plugin;
    private int multiplier = 800;

    public EntityListener(Main plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
        /*Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> Bukkit.getOnlinePlayers().stream().filter(p -> plugin.getWantedLevel(p) > 0).forEach(p -> {
            Map<String, int[]> odds = plugin.getBabies().getGroups().get(plugin.getWantedLevel(p));
            if(odds != null) {
                odds.entrySet().forEach(e -> {
                    Location spawnLocation = p.getLocation().add(20 * (Random.getBoolean() ? 1 : -1), 0, 20 * (Random.getBoolean() ? 1 : -1));
                    plugin.getBabies().getCopProperties().stream().filter(c -> c.getIdentifier().equalsIgnoreCase(e.getKey())).findFirst().ifPresent(c -> {
                        int count = e.getValue()[0] == e.getValue()[1] ? e.getValue()[0] : Random.getInt(e.getValue()[0], e.getValue()[1]);
                        for(int i = 0;i < count;i++) {
                            plugin.spawnCopAtLocation(spawnLocation, c, p);
                        }
                    });
                });
            }
        }), 300, 600);*/
        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
            @Override
            public void run() {
                for(Map.Entry<Player, Set<LivingEntity>> e : plugin.getCops().entrySet() ) {
                    try {
                        Player p = e.getKey();
                        boolean isVisible = false;
                        for (LivingEntity z : e.getValue()) {
                            if (((Cop) z.getMetadata("Cop").get(0).value()).onTick(z, p)) isVisible = true;
                        }
                        if (isVisible) {
                            plugin.getNoTargetTicks().put(p, 0L);
                        } else {
                            plugin.getNoTargetTicks().put(p, plugin.getNoTargetTicks().getOrDefault(p, 0L) + 1);
                        }
                        if (plugin.getNoTargetTicks().getOrDefault(p, 0L) / EntityListener.this.multiplier >= 1) {
                            double level = plugin.getNoTargetTicks().getOrDefault(p, 0L) / EntityListener.this.multiplier;
                            int lostLevel = plugin.getWantedLevel(p) - (int)level;
                            if(lostLevel <= 0) {
                                plugin.getCops().getOrDefault(p, new HashSet<>()).forEach(LivingEntity::remove);
                                plugin.getCops().remove(p);
                                plugin.getNoTargetTicks().remove(p);
                                plugin.setWantedLevel(p, 0);
                                p.sendMessage(ChatColor.RED + "You lost the cops!");
                                EntityListener.this.multiplier = 800;
                                return;
                            } else {
                                EntityListener.this.multiplier -= 150;
                                p.sendMessage(ChatColor.YELLOW + "The cops are losing you!");
                                plugin.setWantedLevel(p, lostLevel);
                                plugin.getNoTargetTicks().put(p, plugin.getNoTargetTicks().get(p) - EntityListener.this.multiplier);
                                return;
                            }
                        }
                        continue;
                    } catch(ConcurrentModificationException exception) {
                        continue;
                    }
                }
            }
        }, 1, 1);
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for(Entity entity : e.getChunk().getEntities()) {
            if(entity.hasMetadata("Cop")) {
                this.plugin.getCops().values().forEach(s -> s.remove(entity));
                entity.remove();
            }
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if(!(event.getEntity() instanceof LivingEntity)) return;
        if(event.getEntity().getKiller() == null) return;
        if(event.getEntity().hasMetadata("Cop") && event.getEntity().getKiller().getType() == EntityType.PLAYER) {
            LivingEntity victim = event.getEntity();
            Player killer = victim.getKiller();
            Cop cop = (Cop)victim.getMetadata("Cop").get(0).value();
            PlayerKillCopEvent playerKillCopEvent = new PlayerKillCopEvent(cop, victim, killer, event.getDrops());
            Bukkit.getPluginManager().callEvent(playerKillCopEvent);
            event.getDrops().clear();
            event.getDrops().addAll(playerKillCopEvent.getDrops());
            event.setDroppedExp(0);
        } else if(event.getEntity().getKiller().hasMetadata("Cop") && event.getEntity().getType() == EntityType.PLAYER) {
            if(event.getEntity().getType() != EntityType.PLAYER) return;
            Cop killer = (Cop)event.getEntity().getKiller().getMetadata("Cop").get(0).value();
            Player victim = (Player)event.getEntity();
            CopKillPlayerEvent killPlayerEvent = new CopKillPlayerEvent(killer, victim, event.getDrops());
            Bukkit.getPluginManager().callEvent(killPlayerEvent);
            event.getDrops().clear();
            event.getDrops().addAll(killPlayerEvent.getDrops());
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if(event.getEntity().hasMetadata("Cop") && event.getDamager() instanceof Player) {
        	
        	// grab event variables 
            Player damager = (Player)event.getDamager();
            Creature cop = (Creature)event.getEntity();
            
            GTMUser gtmUser = GTMUserManager.getInstance().getUser(damager.getUniqueId()).orElse(null);
            if (gtmUser != null){
            	if (gtmUser.getJobMode() == JobMode.COP){
            		event.setCancelled(true);
                    return;
            	}
            }

            if(this.plugin.getWantedLevel(damager) == 0) {
                this.plugin.addKill(damager);
            }
            this.plugin.getNoTargetTicks().put(damager, 0L);
            cop.setTarget(damager);
        } else if(event.getDamager().hasMetadata("Cop") && event.getEntity().getType() == EntityType.PLAYER) {
            Player victim = (Player)event.getEntity();
            Cop cop = (Cop)event.getDamager().getMetadata("Cop").get(0).value();
            this.plugin.getNoTargetTicks().put(victim, 0L);
            Weapon weapon = cop.getWeapon();
            CopDamagePlayerEvent damageEvent = new CopDamagePlayerEvent(cop, victim, weapon, event.getDamage());
            this.plugin.getServer().getPluginManager().callEvent(damageEvent);
            if(damageEvent.isCancelled()) {
                event.setCancelled(true);
                return;
            }
            event.setDamage(damageEvent.getDamageDealt());
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
        if(e.getRightClicked().hasMetadata("Cop")) e.setCancelled(true);
    }

    @EventHandler
    public void onEntityTarget(EntityTargetEvent e) {
        if(e.getEntity().hasMetadata("Cop")) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        this.plugin.getCops().getOrDefault(p, new HashSet<>()).forEach(LivingEntity::remove);
        this.plugin.getCops().remove(p);
        this.plugin.getNoTargetTicks().remove(p);
        this.plugin.resetWantedLevel(p);
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        this.plugin.getCops().getOrDefault(p, new HashSet<>()).forEach(LivingEntity::remove);
        this.plugin.getCops().remove(p);
        this.plugin.getNoTargetTicks().remove(p);
        this.plugin.resetWantedLevel(p);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        this.plugin.getCops().getOrDefault(p, new HashSet<>()).forEach(LivingEntity::remove);
        this.plugin.getCops().remove(p);
        this.plugin.getNoTargetTicks().remove(p);
        this.plugin.resetWantedLevel(p);
    }
}
