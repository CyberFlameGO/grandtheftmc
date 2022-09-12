package net.grandtheftmc.gtm.tasks;

import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class GlassesTask extends BukkitRunnable {
    private Long ct;

    @Override
    public void run() {
        this.ct = System.currentTimeMillis();
        Bukkit.getOnlinePlayers().forEach(player -> checkGlasses(player));
    }

    public void checkGlasses(Player player) {
        if(player.getInventory().getHelmet() == null) return;
        if(player.getInventory().getHelmet().getType() != Material.CHAINMAIL_HELMET) return;
        player.getNearbyEntities(30, 30, 30).forEach(entity -> {
            if(entity.getType() != EntityType.PLAYER) return;
            Player target = (Player)entity;
            new BukkitRunnable() {
                @Override
                public void run() {
                    GTMUtils.sendGlow(player, target, 12);
                }
            }.runTaskAsynchronously(GTM.getInstance());
        });
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 20, 20);
    }
}