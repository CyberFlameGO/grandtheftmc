package net.grandtheftmc.gtm.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.categories.examples.Opioids;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-04-08.
 */
public class Heroin extends Opioids{

    public Heroin() {
        super("heroin", 15);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug,  duration, player) -> {
            int roll = ThreadLocalRandom.current().nextInt(100);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20; //to make it +/-5-25%

            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            player.playSound(player.getLocation(), Sound.ENTITY_SILVERFISH_AMBIENT, 1, 1);
            final long addTime = System.currentTimeMillis();
            if (roll > 6 || (GTM.getDrugManager().inOD(player.getUniqueId()))) {
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration, 1)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, 0)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.FIRE_RESISTANCE, effectDuration, 1)));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                            cancel();
                            return;
                        }
                        if (ThreadLocalRandom.current().nextInt(4) == 0) {
                            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, effectDuration * 3, 1)));
                            player.sendMessage(Lang.DRUGS.f("&7&oWhy does everything feel so heavy all of a sudden?"));
                        }
                    }
                }.runTaskLater(GTM.getInstance(), effectDuration);
            } else {
                GTM.getDrugManager().addOD(player.getUniqueId());
                GTM.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                player.sendMessage(Lang.DRUGS.f("&7&oThis doesn't feel right..."));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WITHER, 5 * 20, 1)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.BLINDNESS, 5 * 20, 0)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, 5 * 20, 0)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        GTM.getDrugManager().removeOD(player.getUniqueId());
                        if (ThreadLocalRandom.current().nextBoolean() && GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                            player.getActivePotionEffects().forEach(effect -> {
                                player.removePotionEffect(effect.getType());
                            });
                            player.damage(player.getHealth());
                            player.sendMessage(Lang.DRUGS.f("&7&oIm never doing heroin again..."));
                        }
                    }
                }.runTaskLater(GTM.getInstance(), 5 * 20);
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }
}
