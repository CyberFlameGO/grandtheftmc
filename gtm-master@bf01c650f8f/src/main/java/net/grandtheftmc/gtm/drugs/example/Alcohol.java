package net.grandtheftmc.gtm.drugs.example;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugEffect;
import net.grandtheftmc.gtm.drugs.DrugService;
import net.grandtheftmc.gtm.drugs.categories.examples.AAlcohol;
import net.grandtheftmc.gtm.drugs.events.DrugUseEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Remco on 25-3-2017.
 */
public class Alcohol extends AAlcohol {
    private final HashMap<UUID, List<Long>> drinkTimes = new HashMap<>();
    private static final int NEEDED_RATE = 3;//per X
    private static final int TIMEFRAME = 300;//seconds

    public Alcohol() {
        super("alcohol", 60);
    }

    public boolean potentApply(Player p, boolean vodka) {
        boolean[] failed = {false};
        UUID uuid = p.getUniqueId();
        DrugEffect effect = (drug, duration, player) -> {
            final DrugService service = (DrugService) GTM.getInstance().getDrugManager().getService();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_BURP, 1, 1);
            int effectDuration = (int) Math.round(duration + (duration * ThreadLocalRandom.current().nextDouble(-.25, .25))) * 20; //to make it +/-5-25%
            Bukkit.getPluginManager().callEvent(new DrugUseEvent(player, this));
            if (isPlayerDrunk(uuid)) {
                ItemStack item = player.getInventory().getItemInMainHand();
                if (item != null)
                    if (item.getAmount() > 1) {
                        player.getInventory().getItemInMainHand().setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }
                for (ItemStack is : player.getInventory().getStorageContents()) {
                    if (is != null && is.getType()!= Material.COMPASS && is.getType() != Material.CHEST && is.getType() != Material.WATCH) {
                        Item drop = player.getWorld().dropItemNaturally(player.getLocation().add(ThreadLocalRandom.current().nextDouble(-1, 1), 1, ThreadLocalRandom.current().nextDouble(-1, 1)), is);
                        Vector velocity = player.getEyeLocation().getDirection().normalize();
                        velocity.multiply(1.01);
                        drop.setVelocity(velocity);
                        player.getInventory().remove(is);
                    }
                }
                player.sendMessage(Lang.DRUGS.f("&2&oYou dont feel so good..."));
            }

            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.SLOW, effectDuration, 1 + (vodka ? 2 : 1))));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.CONFUSION, effectDuration, 0)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, effectDuration, 1)));
            GTM.getDrugManager().getEffectManager().addEffect(player, (new PotionEffect(PotionEffectType.INCREASE_DAMAGE, effectDuration, (vodka ? 1 : 0))));
        };
        effect.apply(this, this.getDuration(), p);
        return !failed[0];
    }

    @Override
    public boolean apply(Player p) {
        return potentApply(p, false);
    }

    private boolean isPlayerDrunk(UUID uuid) {
        if (drinkTimes.containsKey(uuid)) {
            List<Long> tempTimes = new ArrayList<>();
            ArrayList<Long> times = new ArrayList<>(drinkTimes.get(uuid));
            times.add(System.currentTimeMillis());
            times.forEach(l -> {
                if ((l + (TIMEFRAME * 1000)) <= System.currentTimeMillis()) {
                    tempTimes.add(l);
                }
            });

            times.removeAll(tempTimes);
            drinkTimes.put(uuid, times);
            return times.size() >= NEEDED_RATE;
        }
        drinkTimes.put(uuid, Arrays.asList(System.currentTimeMillis()));
        return false;
    }
}
