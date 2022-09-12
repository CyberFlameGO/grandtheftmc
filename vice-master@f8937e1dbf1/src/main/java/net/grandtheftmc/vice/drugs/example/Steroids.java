package net.grandtheftmc.vice.drugs.example;

import com.j0ach1mmall3.jlib.player.JLibPlayer;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.DrugEffect;
import net.grandtheftmc.vice.drugs.categories.examples.AnabolicSteroids;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import net.grandtheftmc.vice.users.ViceUser;
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
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WEAKNESS, effectDuration / 2, 0)));
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration / 2, 2)));
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.WITHER, effectDuration / 2, 1)));
                Vice.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
            } else {
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration, 0)));
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 0)));
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, 1)));
                final long addTime = System.currentTimeMillis();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new JLibPlayer(player).setWorldborderTint(100);
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                if (!Vice.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                                    cancel();
                                    return;
                                }
                                ViceUser user = Vice.getUserManager().getLoadedUser(uuid);
                                user.updateTintHealth(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
                            }
                        }.runTaskLater(Vice.getInstance(), effectDuration / 20);
                    }
                }.runTaskLater(Vice.getInstance(), 1);
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
