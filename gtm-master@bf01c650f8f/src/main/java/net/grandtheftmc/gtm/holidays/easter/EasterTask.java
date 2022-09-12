package net.grandtheftmc.gtm.holidays.easter;

import net.grandtheftmc.gtm.GTM;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Rabbit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.ThreadLocalRandom;

public class EasterTask extends BukkitRunnable {
    private Easter easter;

    public EasterTask(Easter easter) {
        this.easter = easter;
        if (this.easter.getEasterTask().isPresent()) return;
        this.runTaskTimer(GTM.getInstance(), 0, 4800);
    }

    @Override
    public void run() {
        Location location = GTM.getWarpManager().getRandomWarp().getLocation();
        if (easter.getRabbitsByChunk(location.getChunk()).size() > 10) return;
        Rabbit rabbit = (Rabbit) location.getWorld().spawnEntity(location, EntityType.RABBIT);
        Rabbit.Type type = easter.getAllowedTypes()[ThreadLocalRandom.current().nextInt(easter.getAllowedTypes().length)];
        rabbit.setRabbitType(type);
        rabbit.setCustomName("");
        rabbit.setCustomNameVisible(false);
        rabbit.setAdult();
        rabbit.setBreed(true);
        rabbit.setRemoveWhenFarAway(true);
        location.getWorld().getLivingEntities()
                .stream()
                .filter(livingEntity -> livingEntity.getType() == EntityType.RABBIT)
                .forEach(livingEntity -> {
                    livingEntity.setRemoveWhenFarAway(true);
                    if (easter.getItemsByChunk(livingEntity.getLocation().getChunk()).size() > 50) return;
                    if (ThreadLocalRandom.current().nextInt(3) == 1) {
                        livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), easter.getEasterEgg());
                    }
                });
    }
}