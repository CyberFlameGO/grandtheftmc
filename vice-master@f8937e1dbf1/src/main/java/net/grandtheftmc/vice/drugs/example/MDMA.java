package net.grandtheftmc.vice.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.DrugEffect;
import net.grandtheftmc.vice.drugs.categories.examples.Hallucinogens;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class MDMA extends Hallucinogens {

    public MDMA() {
        super("mdma", 120);
    }

    private ArrayList<UUID> using = new ArrayList<>();

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            if (this.using.contains(player.getUniqueId())) {
                failed[0] = true;
                player.sendMessage(Lang.DRUGS + "" + ChatColor.RED + "I don't think its a good idea to do more than one...");
                return;
            }
            this.using.add(player.getUniqueId());
            int roll = ThreadLocalRandom.current().nextInt(100);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20;//to make it +/-5-25%
            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));

            if (roll > 4 || !Vice.getDrugManager().inOD(player.getUniqueId())) {
                final long addTime = System.currentTimeMillis();
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 0)));
                //less anxiety? Or I might have to fool around with .setWalkSpeed etc
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, effectDuration, 0)));
                final Set<Player> nearby = player.getNearbyEntities(10, 10, 10).stream().filter(entity -> entity instanceof Player).map(entity -> (Player) entity).collect(Collectors.toSet());
                nearby.add(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() >= (addTime + effectDuration / 20 * 1000) || !Vice.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                            cancel();
                            using.remove(player.getUniqueId());
                            return;
                        }
                        for (int i = 0; i < 5; i++) {//hopefully blinds the player's view with hearts. Haven't tested how many particles it will actually take. Could do a more systematic approach like draw a panel of hearts, but I think this is better.
                            double locX = player.getEyeLocation().getX() + (ThreadLocalRandom.current().nextDouble(-.5, .5));
                            double locZ = player.getEyeLocation().getZ() + (ThreadLocalRandom.current().nextDouble(-.5, .5));
                            double locY = player.getLocation().getY() + ThreadLocalRandom.current().nextDouble(2.1, 2.3);
                            nearby.forEach(p -> p.spawnParticle(Particle.HEART, locX, locY, locZ, 1));
                        }
                    }
                }.runTaskTimerAsynchronously(Vice.getInstance(), 0, 5);

            } else {
                Vice.getDrugManager().addOD(player.getUniqueId());
                Vice.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, 25 * 20, 0)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.HUNGER, 25 * 20, 2)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.POISON, 25 * 20, 2)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.BLINDNESS, 25 * 20, 0)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, 25 * 20, 2)));
                //message
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Vice.getDrugManager().removeOD(player.getUniqueId());
                        using.remove(player.getUniqueId());
                    }
                }.runTaskLaterAsynchronously(Vice.getInstance(), 25 * 20);
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }

}
