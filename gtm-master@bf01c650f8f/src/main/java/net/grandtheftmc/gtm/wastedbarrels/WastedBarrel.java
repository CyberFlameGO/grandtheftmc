package net.grandtheftmc.gtm.wastedbarrels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

import com.j0ach1mmall3.wastedguns.MathUtil;
import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;

import net.grandtheftmc.gtm.GTM;

public class WastedBarrel {
    private final ArmorStand armorStand;
    private final Collection<Integer> tasks;

    public WastedBarrel(ArmorStand armorStand) {
        this.armorStand = armorStand;
        this.tasks = new ArrayList<>();
        GTM.getBarrelManager().getWastedBarrels().add(this);
        if (!armorStand.hasMetadata("WastedBarrel")) {
            this.armorStand.setMetadata("WastedBarrel", new FixedMetadataValue(GTM.getInstance(), this));
        }
    }

    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    public Collection<Integer> getTasks() {
        return this.tasks;
    }

    public void onDamage(double amount, LivingEntity shooter) {
        double afterDamage = this.armorStand.getHealth() - amount;
        if (afterDamage < 1) {
            this.onDestroy(shooter);
        } else if (afterDamage > 0 && afterDamage <= 15) {
            this.armorStand.setHealth(afterDamage);
            this.damageEffects(2, shooter);
        } else if (afterDamage > 15 && afterDamage <= 25) {
            this.armorStand.setHealth(afterDamage);
            this.damageEffects(3, shooter);
        } else if (afterDamage > 25 && afterDamage <= 50) {
            this.armorStand.setHealth(afterDamage);
            this.armorStand.getWorld().playSound(this.armorStand.getLocation(), Sound.ENTITY_SHULKER_BULLET_HIT, 3.0F, 3.0F);
        } else {
            this.armorStand.setHealth(50.0D);
        }
    }

    public void onDestroy(LivingEntity shooter) {
        this.damageEffects(1, shooter);
        this.armorStand.setHelmet(null);
        this.armorStand.setFireTicks(0);
        new BukkitRunnable() {
            @Override
            public void run() {
                WastedBarrel.this.respawn();
            }
        }.runTaskLater(GTM.getInstance(), 6000);
        this.tasks.forEach(task -> GTM.getInstance().getServer().getScheduler().cancelTask(task));
    }

    public void respawn() {
        this.armorStand.setHelmet(new ItemStack(Material.TNT));
        this.armorStand.setMaxHealth(50.0D);
        this.armorStand.setHealth(50.0D);
    }

    public void damageEffects(int tier, LivingEntity shooter) {
        BukkitScheduler scheduler = GTM.getInstance().getServer().getScheduler();
        if (tier == 1) {
            this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.EXPLOSION_LARGE, 0, 0, 0, 0, 0, 0.01F, 50, 50);
            this.armorStand.getWorld().playSound(this.armorStand.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 10.0F, 10.0F);
            
            // get nearby entities
            Collection<LivingEntity> eventVictims = MathUtil.getNearbyEntities(this.armorStand, 7).collect(Collectors.toList());
            
            // call explosion evnet
            ExplosionDamageEntityEvent damageEntityEvent = new ExplosionDamageEntityEvent(shooter, null, eventVictims, null);
            Bukkit.getServer().getPluginManager().callEvent(damageEntityEvent);
            if (damageEntityEvent.isCancelled()) return;
            
            // for each entity
            Collection<LivingEntity> victims = damageEntityEvent.getVictims();
            if (!victims.isEmpty()) {
                victims.forEach(e -> {
                    
                	// damage them
                	double damage = 40.0 / this.armorStand.getLocation().distance(e.getLocation());
                    e.damage(damage);
                    if (!(e instanceof ArmorStand)) e.setVelocity(e.getLocation().getDirection().multiply(-2.5F));
                });
            }
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation().add(x, 0, z),
                            Effect.FLAME, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 50, 50);
                    this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation().add(x, 0, z),
                            Effect.MOBSPAWNER_FLAMES, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 50, 50);
                    this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation().add(x, 0, z),
                            Effect.LARGE_SMOKE, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 50, 50);
                }
            }
        } else if (tier == 2) {
            this.tasks.add(scheduler.scheduleSyncRepeatingTask(GTM.getInstance(), () -> {
                if (this.armorStand.getHelmet().getType() != Material.TNT || this.armorStand.isDead())
                    this.tasks.forEach(scheduler::cancelTask);
                this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.SMALL_SMOKE, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 50, 50);
                this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.FLAME, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 30, 30);
                this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.LARGE_SMOKE, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 30, 50);
            }, 0L, 20L));
        } else if (tier == 3) {
            this.tasks.add(scheduler.scheduleSyncRepeatingTask(GTM.getInstance(), () -> {
                if (this.armorStand.getHelmet().getType() != Material.TNT || this.armorStand.isDead())
                    this.tasks.forEach(scheduler::cancelTask);
                this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.FLAME, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 3, 10);
                this.armorStand.getWorld().spigot().playEffect(this.armorStand.getLocation(), Effect.LARGE_SMOKE, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 5, 40);
            }, 0L, 20L));
        }
    }
}
