package net.grandtheftmc.gtm.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.DrugUtil;
import net.grandtheftmc.gtm.drugs.categories.examples.Cannabinoids;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class Joint extends Cannabinoids {

    public Joint() {
        super("joint", 120);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            final DrugService service = (DrugService) GTM.getInstance().getDrugManager().getService();
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20; //to make it +/-5-25%
            player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            final long addTime = System.currentTimeMillis();
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll > 4 || GTM.getInstance().getDrugManager().inOD(player.getUniqueId())) {
                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 1)));
                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW_DIGGING, effectDuration, 0)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!GTM.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                            return;
                        GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.HUNGER, effectDuration, 1)));
                        player.sendMessage(Lang.DRUGS.f("&7&oDamn I could go for some McDonalds right now..."));

                    }
                }.runTaskLaterAsynchronously(GTM.getInstance(), 20 * 60 * 2);
                final long startTime = System.currentTimeMillis();
                final Set<Player> nearby = player.getNearbyEntities(10, 10, 10).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toSet());

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (startTime + (1000 * 2) <= System.currentTimeMillis()) cancel();
                        for (int i = 0; i < 5; i++) {
                            double locX = player.getEyeLocation().getX() + (ThreadLocalRandom.current().nextDouble(0, 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1));
                            double locZ = player.getEyeLocation().getZ() + (ThreadLocalRandom.current().nextDouble(0, 1) * (ThreadLocalRandom.current().nextBoolean() ? 1 : -1));
                            double locY = player.getLocation().getY() + ThreadLocalRandom.current().nextDouble(1, 1.2);
                            nearby.forEach(p -> {
                                p.spawnParticle(Particle.FLAME, locX, locY, locZ, 1);
                                p.spawnParticle(Particle.SMOKE_NORMAL, locX, locY, locZ, 1);
                            });
                        }
                    }
                }.runTaskTimerAsynchronously(GTM.getInstance(), 0, 12);
            } else {
                GTM.getInstance().getDrugManager().addOD(player.getUniqueId());
                GTM.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 0)));
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 10, 1);
                final long endTime = System.currentTimeMillis() + (effectDuration / 20 * 1000);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() >= endTime || !GTM.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                            GTM.getInstance().getDrugManager().removeOD(player.getUniqueId());
                            cancel();
                        }
                        player.playSound(player.getLocation(), DrugUtil.getRandomParanoiaSound(), 10, ThreadLocalRandom.current().nextFloat() * 2);
                    }
                }.runTaskTimerAsynchronously(GTM.getInstance(), 5, 15);
                player.sendMessage(Lang.DRUGS.f("&7&oUghhh... That must've been a bad batch of K2..."));
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }
}