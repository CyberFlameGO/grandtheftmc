package net.grandtheftmc.vice.holidays.easter;

import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Rabbit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.ThreadLocalRandom;

public class EasterListener implements Listener {
    private Easter easter;

    public EasterListener(Easter easter) {
//        Bukkit.getPluginManager().registerEvents(this, Vice.getInstance());
        this.easter = easter;
    }

    @EventHandler
    public void playerEggThrow(PlayerEggThrowEvent event) {
        Player player = event.getPlayer();
        if (event.isHatching()) {
            if (easter.isActive()) event.setHatchingType(EntityType.RABBIT);
            if (player.getWorld().getName().equalsIgnoreCase("spawn") || !easter.isActive()) event.setHatching(false);
            if (easter.getRabbitsByChunk(player.getLocation().getChunk()).size() > 50) event.setHatching(false);
            player.playSound(event.getEgg().getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 5.0F, 5.0F);
            player.playSound(event.getEgg().getLocation(), Sound.ENTITY_FIREWORK_BLAST, 5.0F, 5.0F);
            player.getWorld().spigot().playEffect(event.getEgg().getLocation(), Effect.FIREWORKS_SPARK);
            player.getWorld().dropItemNaturally(event.getEgg().getLocation(), ViceUtils.getRandomGameItem().getItem());
        }
    }

    @EventHandler
    public void playerItemConsume(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item.getType() == easter.getChocolateBunny().getType()) {
            Vice.getDrugManager().getEffectManager().cancelEffects(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 600, 1));
            player.setFoodLevel(20);
            player.setSaturation(20);
            player.setExhaustion(0);
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent event) {
        if (!easter.isActive()) return;
        if (event.getEntity().getType() != EntityType.RABBIT) return;
        if (event.getEntity().getKiller() == null) return;
        event.getDrops().clear();
        event.getDrops().add(Vice.getHolidayManager().getEaster().getChocolateBunny());
    }

    @EventHandler
    public void entitySpawn(EntitySpawnEvent event) {
        if (!easter.isActive()) return;
        if (event.getEntityType() != EntityType.RABBIT) return;
        Rabbit rabbit = (Rabbit) event.getEntity();
        rabbit.setAdult();
        rabbit.setCustomName("");
        rabbit.setCustomNameVisible(false);
        Rabbit.Type targetType = easter.getAllowedTypes()[ThreadLocalRandom.current().nextInt(easter.getAllowedTypes().length)];
        rabbit.setRabbitType(targetType);
    }
}
