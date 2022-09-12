package net.grandtheftmc.vice.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.drugs.DrugEffect;
import net.grandtheftmc.vice.drugs.DrugUtil;
import net.grandtheftmc.vice.drugs.categories.examples.Cannabinoids;
import net.grandtheftmc.vice.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class Weed extends Cannabinoids {

    public Weed() {
        super("weed",  120);
        /*ShapelessRecipe rollingPaperRecipe = new ShapelessRecipe(Vice.getItemManager().getItem("rollingpaper").getItem());
        rollingPaperRecipe.addIngredient(3, Material.PAPER);
        Bukkit.getServer().addRecipe(rollingPaperRecipe);

        ShapelessRecipe jointRecipe = new ShapelessRecipe(Vice.getItemManager().getItem("joint").getItem());
        jointRecipe.addIngredient(Vice.getItemManager().getItem("rollingpaper").getItem().getType());
        jointRecipe.addIngredient(Vice.getItemManager().getItem("groundweed").getItem().getType());
        Bukkit.getServer().addRecipe(jointRecipe);*/
    }

    @Override
    public boolean apply(Player p) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            int roll = ThreadLocalRandom.current().nextInt(100);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20;
            player.playSound(player.getLocation(), Sound.BLOCK_LAVA_POP, 1, 1);
            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            final long addTime = System.currentTimeMillis();

            if (roll > 4 || Vice.getInstance().getDrugManager().inOD(player.getUniqueId())) {
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 1)));
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW_DIGGING, effectDuration, 0)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!Vice.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                            return;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.HUNGER, effectDuration, 1)));
                            }
                        }.runTask(Vice.getInstance());
                        player.sendMessage(Lang.DRUGS.f("&7&oDamn I could go for some McDonalds right now..."));

                    }
                }.runTaskLaterAsynchronously(Vice.getInstance(), 20 * 60 * 2);
            } else {
                Vice.getInstance().getDrugManager().addOD(player.getUniqueId());
                Vice.getUserManager().getLoadedUser(player.getUniqueId()).setLastTag(System.currentTimeMillis());
                Vice.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.BLINDNESS, effectDuration, 0)));
                player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_THUNDER, 10, 1);
                final long endTime = System.currentTimeMillis() + (effectDuration / 20 * 1000);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (System.currentTimeMillis() >= endTime || !Vice.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime)) {
                            Vice.getInstance().getDrugManager().removeOD(player.getUniqueId());
                            cancel();
                        }
                        player.playSound(player.getLocation(), DrugUtil.getRandomParanoiaSound(), 10, ThreadLocalRandom.current().nextFloat() * 2);
                    }
                }.runTaskTimerAsynchronously(Vice.getInstance(), 5, 15);
                player.sendMessage(Lang.DRUGS.f("&7&oUghhh... That must've been a bad batch of K2..."));
            }
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }
}
