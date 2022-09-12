package com.j0ach1mmall3.wastedvehicles.api.vehicles;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.j0ach1mmall3.jlib.methods.Parsing;
import com.j0ach1mmall3.jlib.methods.ReflectionAPI;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.SpeedBoost;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleDestroyEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleIgniteEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehiclePassengerEnterEvent;
import com.j0ach1mmall3.wastedvehicles.util.MiscUtil;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;

import net.grandtheftmc.core.Lang;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 17/05/2016
 */
public abstract class WastedVehicle {
    public static final double MINECRAFT_GRAVITY = -0.9;

    protected final Main plugin;
    protected final VehicleProperties vehicleProperties;
    protected final List<Double> speedBoosts = new ArrayList<>();
    protected ArmorStand driver;
    protected double speed;
    protected boolean cooldown;
    protected boolean tick = true;
    protected int displayBlock;
    protected UUID creator;
    protected List<ArmorStand> passengers = new ArrayList<>();

    protected WastedVehicle(Main plugin, VehicleProperties vehicleProperties) {
        this.plugin = plugin;
        this.vehicleProperties = vehicleProperties;
    }

    public final VehicleProperties getVehicleProperties() {
        return this.vehicleProperties;
    }

    public final double getSpeed() {
        return this.speed;
    }

    public final void setSpeed(double speed) {
        this.speed = speed;
    }

    public final boolean isCooldown() {
        return this.cooldown;
    }

    public final void setCooldown(boolean cooldown) {
        this.cooldown = cooldown;
    }

    public final boolean isTick() {
        return this.tick;
    }

    public final void setTick(boolean tick) {
        this.tick = tick;
    }

    public final UUID getCreator() {
        return this.creator;
    }

    public final void setCreator(UUID creator) {
        this.creator = creator;
    }

    public final ItemStack getNextDisplayBlock() {
        if(this.displayBlock >= this.vehicleProperties.getDisplayBlocks().size()) this.displayBlock = 0;
        return Parsing.parseItemStack(this.vehicleProperties.getDisplayBlocks().get(this.displayBlock++));
    }

    public void shootWeapon(Player player){
        double speed, blocksAhead;
        switch (getVehicleProperties().getIdentifier().toLowerCase()) {
            case "hydra":
                speed = 15;
                blocksAhead = 20;
            case "rhino":
                speed = 3;
                blocksAhead =4;
                Vector vector = this.driver.getEyeLocation().clone().getDirection().setY(-.03);


                Projectile projectile = this.driver.getEyeLocation().clone().add(0,1,0).getWorld().spawn(this.driver.getEyeLocation().clone().add(0,1,0).add(this.driver.getEyeLocation().getDirection().clone().setY(0).normalize().multiply(blocksAhead)), SmallFireball.class);
                projectile.setVelocity(vector.multiply(speed));
                projectile.setShooter(player);
                projectile.setInvulnerable(true);
                ((SmallFireball)projectile).setIsIncendiary(false);



                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(projectile.isDead() || !projectile.isValid() || projectile.getLocation().distance(player.getLocation())>200) {
                            this.cancel();
                            return;
                        }

                        if(projectile.getWorld() != player.getWorld()){
                            this.cancel();
                            return;
                        }

                        Optional<Entity> nearestTarget = projectile.getNearbyEntities(1, 1, 1).stream().filter(entity -> entity instanceof LivingEntity).findFirst();
                        if((nearestTarget.isPresent() && nearestTarget.get() != player && nearestTarget.get() != driver) || MiscUtil.getMiscUtil().getBlocksInRadius(projectile.getLocation(), 2).stream().anyMatch(block -> block.getType()!= Material.AIR)) {
                            TNTPrimed entity = (TNTPrimed) projectile.getWorld().spawnEntity(projectile.getLocation(), EntityType.PRIMED_TNT);
                            entity.setCustomName("EXPLOSIVE");
                            entity.setCustomNameVisible(false);
                            entity.setFuseTicks(1);
                            projectile.getWorld().spigot().playEffect(projectile.getLocation(), Effect.EXPLOSION_HUGE, 0, 0, 0, 0, 0, 0.01F, 1, 50);
                            projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 5.0F);
                            this.cancel();
                        }
                    }
                }.runTaskTimer(plugin, 0, 1);
                break;
        }
    }

    public final void onSpeedBoost(SpeedBoost speedBoost) {
        this.speedBoosts.add(speedBoost.getSpeedBoost());
        Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> this.speedBoosts.remove(0), speedBoost.getDuration());
    }

    public final void onCreate(ArmorStand armorStand, Player player, Location l, double health) {
        this.driver = armorStand;
        armorStand.setVisible(false);
        armorStand.setArms(false);
        armorStand.setRemoveWhenFarAway(true);
        armorStand.setCanPickupItems(false);
        armorStand.setBasePlate(false);
        armorStand.setMaxHealth(this.vehicleProperties.getMaxHealth());
        armorStand.setHealth(health);
        armorStand.setHelmet(this.getNextDisplayBlock());
        armorStand.setCustomNameVisible(true);
        this.updateName(armorStand, null);

        for(int i = 0; i < this.vehicleProperties.getPassengers(); i++) {
            ArmorStand passenger = (ArmorStand)armorStand.getWorld().spawnEntity(l.add(1, 0, 0), EntityType.ARMOR_STAND);
            passenger.setVisible(false);
            passenger.setArms(false);
            passenger.setRemoveWhenFarAway(true);
            passenger.setCanPickupItems(false);
            passenger.setBasePlate(false);
            passenger.setGravity(false);
            passenger.setMaxHealth(this.vehicleProperties.getMaxHealth());
            passenger.setHealth(health);
            passenger.setMetadata("WastedVehiclePassenger", new FixedMetadataValue(this.plugin, this));
            passenger.setCollidable(true);
            this.passengers.add(passenger);
            try {
                Object handle = ReflectionAPI.getHandle((Object) passenger);
                handle.getClass().getMethod("setSize", float.class, float.class).invoke(handle, this.vehicleProperties.getBoundingBoxWidth(), this.vehicleProperties.getBoundingBoxHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            Object handle = ReflectionAPI.getHandle((Object) armorStand);
            handle.getClass().getMethod("setSize", float.class, float.class).invoke(handle, this.vehicleProperties.getBoundingBoxWidth(), this.vehicleProperties.getBoundingBoxHeight());
        } catch (Exception e) {
            e.printStackTrace();
        }

        armorStand.teleport(l);
        armorStand.setMetadata("WastedVehicle", new FixedMetadataValue(this.plugin, this));
        this.creator = player.getUniqueId();
    }

    public final void onDestroy(ArmorStand armorStand) {
        VehicleDestroyEvent event = new VehicleDestroyEvent(this, armorStand);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()) {
            this.onDismount(armorStand);
            this.plugin.getEntityQueue().remove(armorStand);
            armorStand.setHelmet(null);
            armorStand.remove();
            this.passengers.forEach(passenger -> {
                if(passenger.getPassenger() != null) passenger.eject();
                passenger.setHealth(0);
                passenger.remove();
            });
        }
    }

    public final void passengerMount(ArmorStand armorStand, Player player) {
        if(armorStand.getHealth() <= 1) return;
        if(!armorStand.hasMetadata("WastedVehiclePassenger")) return;
        WastedVehicle wastedVehicle = (WastedVehicle)armorStand.getMetadata("WastedVehiclePassenger").get(0).value();
        VehiclePassengerEnterEvent enterEvent = new VehiclePassengerEnterEvent(wastedVehicle, player, armorStand);
        Bukkit.getPluginManager().callEvent(enterEvent);
        if(enterEvent.isCancelled()) return;
        if(armorStand.getPassenger() == null) {
            if(wastedVehicle.getDriver().getPassenger() == null) {
                if(Objects.equals(wastedVehicle.getCreator().toString(), player.getUniqueId().toString())) {
                    wastedVehicle.onRightClick(wastedVehicle.getDriver(), player);
                    return;
                }
            }
            if(this.vehicleProperties.isInvisible()) {
                Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
            }
            armorStand.setPassenger(player);
            player.sendMessage(Lang.VEHICLES.f("&7You entered the passenger seat of a Vehicle"));
        } else {
            if(this.passengers.stream().noneMatch(passenger -> passenger.getPassenger() == null)) {
                player.sendMessage(Lang.VEHICLES.f("&7No seats left!"));
                return;
            }
            this.passengers.forEach(passenger -> {
                if(passenger.getPassenger() == null) {
                    passenger.setPassenger(player);
                    Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
                    player.sendMessage(Lang.VEHICLES.f("&7You entered the passenger seat of a Vehicle"));
                    return;
                }
            });
        }
    }

    public final void onRightClick(ArmorStand armorStand, Player player) {
        if(armorStand.getHealth() <= 1) return;
        if(armorStand.hasMetadata("WastedVehiclePassenger")) return;
        if(armorStand.getPassenger() == null) {
            Location l = armorStand.getLocation();
            l.setYaw(player.getEyeLocation().getYaw());
            l.setPitch(0);
            armorStand.teleport(l);
            if(this.vehicleProperties.isInvisible()) {
                Bukkit.getOnlinePlayers().forEach(p -> p.hidePlayer(player));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0, true, false));
            }
            armorStand.setPassenger(player);
            this.updateName(armorStand, player);
            this.speed = this.vehicleProperties.getDefaultSpeed();
        }
    }

    public final void onDismount(ArmorStand armorStand) {
        Player player = (Player) armorStand.getPassenger();
        if(player != null) {
//            armorStand.eject();
            Bukkit.getOnlinePlayers().forEach(p -> p.showPlayer(player));
            player.removePotionEffect(PotionEffectType.INVISIBILITY);
//            player.teleport(armorStand.getLocation().add(0, 1, 0));
        }
        if(!armorStand.hasMetadata("WastedVehiclePassenger")) this.updateName(armorStand, null);
    }

    public final void onDamage(ArmorStand armorStand, EntityDamageEvent.DamageCause damageCause, double damage) {
        if(armorStand.hasMetadata("WastedVehiclePassenger")) return;
        if(armorStand.getHealth() <= 1) return;

        switch (damageCause) {
            case FALL:
            case SUFFOCATION:
            case DROWNING:
                return;
            case ENTITY_EXPLOSION:
            case BLOCK_EXPLOSION:
                if(!this.vehicleProperties.isExplode()) break;
                armorStand.setHealth(1);
                this.explode(armorStand);
                return;
            case CONTACT:
                armorStand.setHealth(damage);
                return;
        }

        if(armorStand.getHealth() - damage > 1) armorStand.setHealth(armorStand.getHealth() - damage);
        else armorStand.setHealth(1);
        this.updateName(armorStand, (Player) armorStand.getPassenger());
        if(armorStand.getHealth() <= 1) {
            if(this.vehicleProperties.isExplode()) {
                VehicleIgniteEvent event = new VehicleIgniteEvent(this, armorStand);
                Bukkit.getPluginManager().callEvent(event);
                if(!event.isCancelled()) {
                    new BukkitRunnable() {
                        private int i;
                        @Override
                        public void run() {
                            if(this.i > 100) {
                                WastedVehicle.this.explode(armorStand);
                                this.cancel();
                            } else {
                                this.i++;
                                armorStand.getWorld().spigot().playEffect(armorStand.getLocation().add(0, WastedVehicle.this.vehicleProperties.getBoundingBoxHeight() / 2, 0), Effect.FLAME, 0, 0, 0.5F, 0.5F, 0.5F, 0.001F, 20, 50);
                            }
                        }
                    }.runTaskTimer(this.plugin, 1, 1);
                }
            } else this.onDestroy(armorStand);
        }
    }

    public final void explode(ArmorStand armorStand) {
        if(armorStand.hasMetadata("WastedVehiclePassenger")) return;
        if(this.vehicleProperties.isExplode()) {
            armorStand.getNearbyEntities(2.0, 2.0, 2.0).stream().filter(e -> e instanceof LivingEntity).map(e -> (LivingEntity) e).forEach(l -> {
                
            	// TODO distance uses square root function, inefficient
            	double distance = l.getLocation().distance(armorStand.getLocation());
                
            	if (distance <= 0.2){
            		// kill the entity
            		l.setHealth(0);
            	}
            	else{
            		double newHealth = l.getHealth() - (20 / distance);
            		
            		// bounds check
            		if (newHealth >= l.getMaxHealth()){
            			newHealth = l.getMaxHealth();
            		}
            		if (newHealth <= 0){
            			newHealth = 0;
            		}
            		
            		l.setHealth(newHealth);
            	}
            });
            armorStand.getWorld().createExplosion(armorStand.getLocation(), 2, false);
            if(armorStand.getPassenger() != null && armorStand.getPassenger().getType() == EntityType.PLAYER) {
//                armorStand.getPassenger().eject();
                ((Player) armorStand.getPassenger()).setHealth(0);
            }
        }
        this.onDestroy(armorStand);
    }

    public final void updateName(ArmorStand armorStand, Player passenger) {
        armorStand.setCustomName((passenger == null
                ?
                this.vehicleProperties.getItem().getItemMeta().getDisplayName()
                :
                ChatColor.GREEN.toString() + ChatColor.BOLD + passenger.getName())
                +
                " (" + this.plugin.formatHealth(armorStand.getHealth(), this.vehicleProperties.getMaxHealth()) + ChatColor.GRAY + ')');
    }

    public ArmorStand getDriver() {
        return this.driver;
    }

    public abstract Vector getWeaponDirection(ArmorStand armorStand, Player player);

    public abstract void onSteer(ArmorStand armorStand, Player player, SteerDirection steerDirection);

    public abstract void onTick(ArmorStand armorStand);

    public abstract List<ArmorStand> getPassengers();

    public void setPassengers(List<ArmorStand> passengers) {
        this.passengers = passengers;
    }
}
