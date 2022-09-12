package net.grandtheftmc.vice.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.DrugEffect;
import net.grandtheftmc.vice.drugs.categories.examples.Stimulants;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-04-06.
 */
public class Meth extends Stimulants{
    private static final int ADDITIONAL_HALF_HEARTS = 10;
    public Meth() {
        super("meth", 120);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            Vice.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
            player.playSound(player.getLocation(), Sound.ENTITY_TNT_PRIMED, 1, 1);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20;
            Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration, 1)));
            Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, 0)));
            Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.HEALTH_BOOST, effectDuration, 1)));
            Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 0)));
            long addTime = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!Vice.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                        cancel();
                        return;
                    }
                    Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 0)));
                    Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, effectDuration, 0)));
                    Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WITHER, effectDuration, 0)));
                    long endTime = System.currentTimeMillis() + ((effectDuration / 20) * 1000);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (System.currentTimeMillis() >= endTime || !Vice.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                                player.setMaxHealth(20);
                                cancel();
                                return;
                            }
                            Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, new PotionEffect(PotionEffectType.BLINDNESS, ThreadLocalRandom.current().nextInt(2, 6) * 20, 0));
                        }
                    }.runTaskTimer(Vice.getInstance(), 0, ThreadLocalRandom.current().nextInt(15, 41) * 20);
                    player.setMaxHealth(20);
                    player.sendMessage(Lang.DRUGS.f("&7&oUggh, I shouldn't have tried meth..."));

                }
            }.runTaskLater(Vice.getInstance(), duration * 20);

            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }
}
