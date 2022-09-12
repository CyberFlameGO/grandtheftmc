package com.j0ach1mmall3.wastedcops.listeners;

import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.wastedcops.Main;
import com.j0ach1mmall3.wastedcops.api.Cop;
import com.j0ach1mmall3.wastedcops.api.events.CopSpawnEvent;
import com.j0ach1mmall3.wastedcops.api.events.PlayerKillCopEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CopListener implements Listener {
    private final Main plugin;

    public CopListener(Main plugin) {
        this.plugin = plugin;
        this.plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    @EventHandler
    public void onCopSpawnEvent(CopSpawnEvent event) {
        if(event.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerKillCopEvent(PlayerKillCopEvent event) {
        Player killer = event.getPlayer();
        LivingEntity victim = event.getCopKiled();
        Cop cop = event.getCop();
        if (Random.getInt(100) < cop.getCopProperties().getWeaponDropChance())
            victim.getWorld().dropItemNaturally(victim.getLocation(), victim.getEquipment().getItemInMainHand());
        event.getDrops().clear();
        int reward = cop.getCopProperties().getKillReward();
        this.plugin.addMoney(killer, reward);
        ArmorStand as = (ArmorStand) victim.getWorld().spawnEntity(victim.getLocation(), EntityType.ARMOR_STAND);
        as.setCustomName(ChatColor.translateAlternateColorCodes('&', "&a&l$" + reward));
        as.setCustomNameVisible(true);
        as.setCanPickupItems(false);
        as.setVisible(false);
        as.setSmall(true);
        as.setBasePlate(false);
        as.setInvulnerable(true);
        as.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 30, 0));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
            @Override
            public void run() {
                as.damage(0);
                as.remove();
            }
        }, 30L);
        this.plugin.addKill(killer);
        cop.onDestroy(victim);
    }
}
