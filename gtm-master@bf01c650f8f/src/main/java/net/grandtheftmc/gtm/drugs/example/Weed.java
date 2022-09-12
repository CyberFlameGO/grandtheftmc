package net.grandtheftmc.gtm.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.DrugUtil;
import net.grandtheftmc.gtm.drugs.categories.examples.Cannabinoids;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
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
        /*ShapelessRecipe rollingPaperRecipe = new ShapelessRecipe(GTM.getItemManager().getItem("rollingpaper").getItem());
        rollingPaperRecipe.addIngredient(3, Material.PAPER);
        Bukkit.getServer().addRecipe(rollingPaperRecipe);

        ShapelessRecipe jointRecipe = new ShapelessRecipe(GTM.getItemManager().getItem("joint").getItem());
        jointRecipe.addIngredient(GTM.getItemManager().getItem("rollingpaper").getItem().getType());
        jointRecipe.addIngredient(GTM.getItemManager().getItem("groundweed").getItem().getType());
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

            if (roll > 4 || GTM.getInstance().getDrugManager().inOD(player.getUniqueId())) {
                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 1)));
                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW_DIGGING, effectDuration, 0)));
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!GTM.getInstance().getDrugManager().getEffectManager().canRecieveOngoingEffect(player, addTime))
                            return;
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                GTM.getInstance().getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.HUNGER, effectDuration, 1)));
                            }
                        }.runTask(GTM.getInstance());
                        player.sendMessage(Lang.DRUGS.f("&7&oDamn I could go for some McDonalds right now..."));

                    }
                }.runTaskLaterAsynchronously(GTM.getInstance(), 20 * 60 * 2);
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
