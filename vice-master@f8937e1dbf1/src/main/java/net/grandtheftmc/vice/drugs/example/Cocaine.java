package net.grandtheftmc.vice.drugs.example;

import de.slikey.effectlib.effect.TurnEffect;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.DrugEffect;
import net.grandtheftmc.vice.drugs.DrugUtil;
import net.grandtheftmc.vice.drugs.categories.examples.Stimulants;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import net.grandtheftmc.vice.utils.TitleBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Cocaine extends Stimulants {
    HashSet<UUID> unmoveable = new HashSet<>();

    public Cocaine() {
        super("cocaine", 120);
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            player.playSound(player.getLocation(), Sound.ENTITY_CAT_HISS, 1, 1);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20; //to make it +/-5-25%

            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            int roll = ThreadLocalRandom.current().nextInt(100);

            if (roll > 5 || Vice.getDrugManager().inOD(player.getUniqueId())) {
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, effectDuration / 2, 1)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.FAST_DIGGING, effectDuration, 0)));
                Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, 1)));
            } else {
                Vice.getDrugManager().addOD(player.getUniqueId());
                Vice.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                stageOne(player, System.currentTimeMillis());
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }

    public boolean cantMove(UUID uuid) {
        return unmoveable.contains(uuid);
    }

    public void stageOne(Player player, long addTime){
        int speedLength = ThreadLocalRandom.current().nextInt(100, 200);
        Vice.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SPEED, speedLength, 20)));
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!Vice.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                    return;
                stageTwo(player, addTime);
            }
        }.runTaskLater(Vice.getInstance(), speedLength);
    }

    public void stageTwo(Player player, long addTime){
        player.setPlayerTime(18000, false);
        unmoveable.add(player.getUniqueId());
        TurnEffect turnEffect = new TurnEffect(Vice.getEffectLib());
        turnEffect.setEntity(player);
        turnEffect.infinite();
        turnEffect.period = 2;
        turnEffect.start();
        int stageLength = ThreadLocalRandom.current().nextInt(10, 16);
        long endTime = System.currentTimeMillis() + (stageLength*1000);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, stageLength*20, 0));
        new BukkitRunnable() {
            boolean flip = false;
            @Override
            public void run() {
                if (System.currentTimeMillis() >= endTime || !Vice.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                    turnEffect.cancel();
                    cancel();
                    return;
                }
                if(flip){
                    player.playSound(player.getLocation(), DrugUtil.getRandomParanoiaSound(), 1, 1);
                }
                flip = !flip;
                new TitleBuilder().setTitleText(ChatColor.RED + "" + ChatColor.MAGIC + "asd" + ChatColor.RED + DrugUtil.getParanoiaMessage() + "" + ChatColor.MAGIC + "asd").setFadeIn(0).setDuration(10).setFadeOut(0).send(player);
            }
        }.runTaskTimer(Vice.getInstance(), 0, 15);

        new BukkitRunnable() {
            @Override
            public void run() {
                dispose(player);
                if (Vice.getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                    stageThree(player);
                    return;
                }}
        }.runTaskLater(Vice.getInstance(), stageLength*20);

    }

    public void stageThree(Player player){
        Vice.getDrugManager().getEffectManager().addEffect(player, new PotionEffect(PotionEffectType.SLOW, 20 * 5, 2));
    }

    public void dispose(Player player) {
        unmoveable.remove(player.getUniqueId());
        player.resetPlayerTime();
    }
}
