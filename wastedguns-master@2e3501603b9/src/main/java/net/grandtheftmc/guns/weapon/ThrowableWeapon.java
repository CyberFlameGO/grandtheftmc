package net.grandtheftmc.guns.weapon;

import java.util.Collection;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.j0ach1mmall3.jlib.inventory.CustomItem;
import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.wastedguns.MathUtil;
import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;

import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.guns.DamageDataHandler;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.cache.PlayerCache;
import net.grandtheftmc.guns.weapon.attribute.WeaponExplosive;

/**
 * Created by Luke Bingham on 18/07/2017.
 */
public class ThrowableWeapon extends Weapon<ThrowableWeapon> implements WeaponExplosive {

    protected Effect particles;
    /** Does damage scale off of distance from projectile landing */
    protected boolean scaledDamage = false;
    protected boolean sticky = false, proximity = false, flammable = false, teargas = false;
    protected double damage = 0.0, meleeDamage = 0.0, explosionSize = 0.0, explosionStrength = 0.0;
    protected int explosionDelay = 0, duration = 0, tntFuseDelay = 1;

    /**
     * Construct a new Weapon.
     */
    public ThrowableWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds);
    }

    @Override
    public String[] getStatsBar() {
        String[] output = new String[2];
        String symbol = ":",
                done = ChatColor.GREEN.toString() + ChatColor.BOLD,
                empty = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD;
        int bars = 10;
        double best = 0;
        double result;
        int stat;
        for(int i = 0; i < 2; i ++) {
            if(i == 0) {
                output[i] = "";
                best = 55.0;

                result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, this.damage);
                stat = (int) Math.floor(result) / 10;
                for(int x = 0; x < bars; x++) {
                    output[i] += (x <= stat ? done : empty) + symbol;
                }
                output[i] += C.GRAY + " Damage";
            }

            if(i == 1) {
                output[i] = "";
                best = 3.0;

                result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, this.explosionStrength);
                stat = (int) Math.floor(result) / 10;
                for(int x = 0; x < bars; x++) {
                    output[i] += (x <= stat ? done : empty) + symbol;
                }
                output[i] += C.GRAY + " Strength";
            }
//            else if(x == 1) {
//                output[x] = "Range ";
//                best = 100.0;
//                for(int i = 1; i < (bars+1); i++) {
//                    if(i * (best / bars) > this.range) output[x] += done+symbol;
//                    else output[x] += empty+symbol;
//                }
//            }
        }

        return output;
    }

    public Effect getParticle() {
        return particles;
    }

    public boolean isSticky() {
        return sticky;
    }

    public boolean isProximity() {
        return proximity;
    }

    /**
     * Get the generic damage of the Weapon.
     *
     * @return damage value
     */
    public double getDamage() {
        return damage;
    }

    /**
     * Get the Melee damage of the Weapon.
     *
     * @return damage value
     */
    public double getMeleeDamage() {
        return meleeDamage;
    }

    /**
     * Get the size of the explosion.
     *
     * @return explosion size
     */
    public double getExplosionSize() {
        return explosionSize;
    }

    /**
     * Get the strength of the explosion.
     *
     * @return explosion strength
     */
    public double getExplosionStrength() {
        return explosionStrength;
    }

    /**
     * Get the delay time of the explosion.
     *
     * @return delay value (in ticks)
     */
    public int getExplosionDelay() {
        return explosionDelay;
    }

    public boolean isFlammable() {
        return flammable;
    }

    public boolean isTeargas() {
        return teargas;
    }

    public int getDuration() {
        return duration;
    }
    
    /**
     * Get whether or not this launcher weapon does scaled damage.
     * 
     * @return {@code true} if the damage is scaled from origin of projectile hit, {@code false} otherwise.
     */
    public boolean isScaledDamage() {
		return scaledDamage;
	}

    @Override
    public void onRightClick(Player player) {
        PlayerCache cache = GTMGuns.getInstance().getWeaponManager().getPlayerCache(player.getUniqueId());
        if(this.sticky && cache.stickyBombs.size() >= 5) return;

        this.launchProjectile(player);

        ItemStack heldItem = player.getEquipment().getItemInMainHand();
        heldItem.setAmount(heldItem.getAmount() - 1);
        player.getEquipment().setItemInMainHand(heldItem);
        Sounds.broadcastSound(this.getSounds()[0], player.getEyeLocation());
    }

    @Override
    public void onExplode(Entity explosive, Player shooter) {
    	
        Collection<LivingEntity> eventVictims = MathUtil.getNearbyEntities(explosive, this.explosionSize, 1, this.explosionSize).collect(Collectors.toList());
        ExplosionDamageEntityEvent damageEntityEvent = new ExplosionDamageEntityEvent(shooter, explosive, eventVictims, this);
        Bukkit.getServer().getPluginManager().callEvent(damageEntityEvent);
        if (damageEntityEvent.isCancelled()) {
            GTMGuns.getInstance().getWeaponManager().getEntityQueue().remove(explosive);
            if (!(explosive instanceof Player)) explosive.remove();
            return;
        }

        Collection<LivingEntity> victims = damageEntityEvent.getVictims();
        if (this.duration != 0) {
            AreaEffectCloud areaEffectCloud = null;
            if(this.isFlammable())
                areaEffectCloud = explosive.getWorld().spawn(explosive.getLocation().add(0, 0.5, 0), AreaEffectCloud.class);

            if(this.isTeargas())
                areaEffectCloud = explosive.getWorld().spawn(explosive.getLocation().add(0, 1.0, 0), AreaEffectCloud.class);

            if(areaEffectCloud == null) return;

            areaEffectCloud.setWaitTime(0);
            areaEffectCloud.setDuration(this.duration);
            areaEffectCloud.setParticle(Particle.valueOf(this.particles.name()));
            areaEffectCloud.setRadius((float) this.explosionSize);

            new BukkitRunnable() {
                private int count = 0;
                @Override public void run() {
                	
                	// every tick, get nearby entities
                	victims.clear();
                	victims.addAll(MathUtil.getNearbyEntities(explosive, explosionSize, 1, explosionSize).collect(Collectors.toList()));

                    victims.forEach(e -> {
                        if(isTeargas()) {
                            PotionEffectType[] effectTypes = new PotionEffectType[] {
                                    PotionEffectType.SLOW,
                                    PotionEffectType.BLINDNESS,
                                    PotionEffectType.WEAKNESS,
                                    PotionEffectType.CONFUSION,
                                    PotionEffectType.SLOW_DIGGING
                            };

                            for (PotionEffectType effectType : effectTypes) {
                                if (e.hasPotionEffect(effectType)) {
                                    PotionEffect effect = e.getPotionEffect(effectType);

                                    // if no effect, add it, 1/4 of the duration
                                    if (effect == null) {
                                        e.addPotionEffect(new PotionEffect(effectType, duration / 4, 0, true, false));
                                        continue;
                                    }

                                    // if less than 1/4 of the duration remains
                                    if (effect.getDuration() < (duration / 4)){
                                    	e.removePotionEffect(effectType);
                                        e.addPotionEffect(new PotionEffect(effectType, duration / 4, 0, true, false));
                                    }

                                    continue;
                                }

                                e.addPotionEffect(new PotionEffect(effectType, duration / 4, 0, true, false));
                            }
                        }

                        if(isFlammable())
                            e.setFireTicks(duration);
                    });

                    if(this.count++ * 10 > duration) {
                        this.cancel();
                        GTMGuns.getInstance().getWeaponManager().getEntityQueue().remove(explosive);
                        if (!(explosive instanceof Player)) explosive.remove();
                    }
                }
            }.runTaskTimer(GTMGuns.getInstance(), 0, 10L);
        }

        else {
//            Vice
            if (tntFuseDelay <= 1) {
                TNTPrimed entity = (TNTPrimed) explosive.getWorld().spawnEntity(explosive.getLocation(), EntityType.PRIMED_TNT);
                entity.setCustomName("EXPLOSIVE");
                entity.setCustomNameVisible(false);
                entity.setFuseTicks(1);
                // add metadata to see if this should do entity damage
                entity.setMetadata("entity_damage", new FixedMetadataValue(GTMGuns.getInstance(), false));

            } else {
                ServerUtil.runTaskLater(() -> {
                    TNTPrimed entity = (TNTPrimed) explosive.getWorld().spawnEntity(explosive.getLocation(), EntityType.PRIMED_TNT);
                    entity.setCustomName("EXPLOSIVE");
                    entity.setCustomNameVisible(false);
                    entity.setFuseTicks(1);
                    // add metadata to see if this should do entity damage
                    entity.setMetadata("entity_damage", new FixedMetadataValue(GTMGuns.getInstance(), false));

                }, tntFuseDelay);
            }
//           explosive.getWorld().createExplosion(explosive.getLocation(), 1.8f);

//            GTM
//            explosive.getWorld().spigot().playEffect(explosive.getLocation(), this.particles, 0, 0, 0, 0, 0, 0.01F, 1, 50);
            if(!victims.isEmpty()) {
            	
                for (LivingEntity e : victims) {
                	
                    if(e.getType() == EntityType.PLAYER) {
                        Player victim = (Player) e;
                        if (victim.getGameMode() != GameMode.ADVENTURE && victim.getGameMode() != GameMode.SURVIVAL) return;
                    }
                	
                	// send entities flying away from this explosion
                    if (!(e instanceof ArmorStand)) e.setVelocity(e.getLocation().getDirection().multiply(-this.explosionStrength));

                    // the initial damage modifier they should take
                    double scaledDamage = 1.0;
                    
                    if (isScaledDamage()){
                    	
                        // the distance sq from the origin
                        double distanceSq = explosive.getLocation().distanceSquared(e.getLocation());
                        
                        // if there is an explosion size
                        if (getExplosionSize() > 0){
                        	
                        	// ratio is (explosionSize squared - distanceSq) DIVIDED BY explosionSize squared
                        	// look below for example calculations
                        	// assume explosionSize for all is 10
                        	// if distanceSq = 4, 100 - 4 = 96/100 = 96%
                        	// if distanceSq = 9, 100 - 9 = 91/100 = 91%
                        	// if distanceSq = 81, 100 - 81 = 19/100 = 19%
                        	double ratio = (Math.pow(explosionSize, 2) - distanceSq) / Math.pow(explosionSize, 2);
                        	
                        	// CLAMP percent of damage to always be at least 10%
                        	if (ratio <= 0.10){
                        		ratio = 0.10;
                        	}
                        	// 90% accurate should do 100% damage
                        	// b/c to the user it always does less than max damage
                        	if (ratio >= 0.90){
                        		ratio = 1.0;
                        	}
                        	
                        	scaledDamage = ratio;
                        }
                    }
                    
                    // absolute value this just in case
                    // 19% of getDamage would be the total damage
                    double totalDamage = Math.abs(scaledDamage * getDamage());
                    
                    // create entity damage by entity event and add to data handler
                    EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(shooter, e, DamageCause.DRAGON_BREATH, totalDamage);
                    DamageDataHandler.getInstance().addData(e.getUniqueId(), edbee);
                    
                    // damage the entity and set last damage cause
                    e.damage(totalDamage, shooter);
                    // reset damage ticks so they can take damage again
        			e.setNoDamageTicks(0);
                    e.setLastDamageCause(edbee);
                }
            }
            GTMGuns.getInstance().getWeaponManager().getEntityQueue().remove(explosive);
            if (!(explosive instanceof Player)) explosive.remove();
        }
        Sounds.broadcastSound(this.getSounds()[3], explosive.getLocation());
    }

    public void launchProjectile(Player player) {
        PlayerCache cache = GTMGuns.getInstance().getWeaponManager().getPlayerCache(player.getUniqueId());
        Entity projectile;

        if(this.sticky) {
            projectile = player.launchProjectile(Arrow.class);
            projectile.setVelocity(projectile.getVelocity().multiply(1.2).setY(player.getEyeHeight()));
            ((Arrow) projectile).setBounce(false);
            projectile.setMetadata("StickyExplosive", new FixedMetadataValue(GTMGuns.getInstance(), this));
            cache.stickyBombs.add(projectile);
        }
        else {
        	
        	// TODO add skin support here (if we change say grenade skins)
            ItemStack stack = super.createItemStack();
            CustomItem ci = new CustomItem(stack);
            ci.setAmount(1);
            ci.setName(Random.getString(16, true, true));
            projectile = player.getWorld().dropItemNaturally(player.getEyeLocation(), ci);
            if (!this.proximity) ((Item) projectile).setPickupDelay(Integer.MAX_VALUE);
            projectile.setMetadata("Explosive", new FixedMetadataValue(GTMGuns.getInstance(), this));
        }

        projectile.setVelocity(player.getEyeLocation().getDirection().multiply(1.5f));
        projectile.setMetadata("Shooter", new FixedMetadataValue(GTMGuns.getInstance(), player));
        GTMGuns.getInstance().getWeaponManager().getEntityQueue().add(projectile);

        if(this.explosionDelay != 0) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(GTMGuns.getInstance(), () ->
                    this.onExplode(projectile, player), this.explosionDelay);
        }
    }

    @Override
    public void onLand(Entity explosive) {
        if(!isProximity()) return;
        explosive.removeMetadata("Explosive", GTMGuns.getInstance());
        explosive.setMetadata("ProximityExplosive", new FixedMetadataValue(GTMGuns.getInstance(), this));
    }

    @Override
    public ThrowableWeapon clone() {
        ThrowableWeapon weapon = new ThrowableWeapon(getUniqueIdentifier(), getName(), getWeaponType(), getAmmoType(), getBaseItemStack().clone(), getSounds());
        weapon.oldItemStack = super.oldItemStack.clone();
        weapon.deathMessages = super.deathMessages;
        weapon.walkSpeed = super.walkSpeed;
        weapon.delay = super.delay;

        weapon.particles = this.particles;
        weapon.sticky = this.sticky;
        weapon.tntFuseDelay = this.tntFuseDelay;
        weapon.proximity = this.proximity;
        weapon.flammable = this.flammable;
        weapon.teargas = this.teargas;
        weapon.damage = this.damage;
        weapon.meleeDamage = this.meleeDamage;
        weapon.explosionSize = this.explosionSize;
        weapon.explosionStrength = this.explosionStrength;
        weapon.explosionDelay = this.explosionDelay;
        weapon.duration = this.duration;
        weapon.scaledDamage = this.scaledDamage;
        return weapon;
    }
}
