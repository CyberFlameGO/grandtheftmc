package net.grandtheftmc.gtm.drugs.example;

import com.j0ach1mmall3.jlib.player.JLibPlayer;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.NMSUtil;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.categories.examples.AnabolicSteroids;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Timothy Lampen on 2017-04-01.
 */
public class Steroids extends AnabolicSteroids{

    private final HashMap<UUID, List<Long>> injectTimes = new HashMap<>();
    private static final int NEEDED_RATE = 3;//per X
    private static final int TIMEFRAME = 10;//seconds
    public Steroids() {
        super("steroids", 120);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_SNARE, 1, 1);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20;
            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            if (playerCanRage(uuid)) {
                for (PotionEffect type : player.getActivePotionEffects()) {
                    player.removePotionEffect(type.getType());
                }
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, effectDuration / 2, 0)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration / 2, 2)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WITHER, effectDuration / 2, 1)));
                GTM.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
            } else {
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration, 0)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 0)));
                GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, 1)));
                final long addTime = System.currentTimeMillis();
                new BukkitRunnable() {
                    @Override
                    public void run() {
//                        TODO: Not compatible, disabled for now.
//                        new JLibPlayer(player).setWorldborderTint(100);

                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!GTM.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                                    cancel();
                                    return;
                                }
                                GTMUser user = GTM.getUserManager().getLoadedUser(uuid);
                                user.updateTintHealth(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                            }
                        }.runTaskLater(GTM.getInstance(), effectDuration / 20);
                    }
                }.runTaskLater(GTM.getInstance(), 1);
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }

    private boolean playerCanRage(UUID uuid){
        if(injectTimes.containsKey(uuid)){
            List<Long> tempTimes = new ArrayList<>();
            ArrayList<Long> times = new ArrayList<>(injectTimes.get(uuid));
            times.add(System.currentTimeMillis());
            times.stream().forEach((l) -> {
                if((l+(TIMEFRAME*1000))<=System.currentTimeMillis()){
                    tempTimes.add(l);
                }
            });
            times.removeAll(tempTimes);
            injectTimes.put(uuid, times);
            return times.size()>=NEEDED_RATE;
        }
        injectTimes.put(uuid, Arrays.asList(System.currentTimeMillis()));
        return false;
    }
}
