package net.grandtheftmc.guns.weapon.ranged.guns;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.SmallFireball;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.google.common.collect.Sets;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.jlib.player.JLibPlayer;
import com.j0ach1mmall3.wastedguns.MathUtil;
import com.j0ach1mmall3.wastedguns.api.events.NetgunHitEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponDamageEvent;
import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedvehicles.api.VehicleType;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.DamageDataHandler;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.WeaponState;
import net.grandtheftmc.guns.cache.PlayerCache;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.WeaponExplosive;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

/**
 * Created by Luke Bingham on 21/07/2017.
 */
public class LauncherWeapon extends RangedWeapon<LauncherWeapon> implements WeaponExplosive {

    protected boolean homingLauncher = false, blowOnHit = true;
    /** Does damage scale off of distance from projectile landing */
    protected boolean scaledDamage = false;
    protected int blowDelay = 0;
    protected double explosionStrength = 1d, rocketSpeed = 1d, explosionSize = 1d;
    /** The number of ticks that an entity will remain stunned */
    protected int baseNetgunStun = 0;

    protected LivingEntity lockedTarget;
    protected final Set<Projectile> rockets = Sets.newHashSet();

    /**
     * Construct a new Weapon.
     */
    public LauncherWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds, Effect effect) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds, effect);
    }

    @Override
    public void onRightClick(Player player) {
        super.onWeaponShoot(player, true, GTMGuns.getInstance().getWeaponManager().getPlayerCache(player.getUniqueId()));
    }

    @Override
    public void onSneak(Player player, boolean sneaking) {
        if(!this.homingLauncher) return;
        if(getAmmo(player.getEquipment().getItemInMainHand()) <= 0) return;
        if(!sneaking) return;
        if(super.weaponState == WeaponState.RELOADING) return;

        new BukkitRunnable() {
            private int i = 0;
            @Override
            public void run() {
                JLibPlayer libPlayer = new JLibPlayer(player);
                if(!player.isSneaking() || weaponState == WeaponState.RELOADING) {
                    libPlayer.playSound(Sound.ENTITY_ENDERDRAGON_GROWL);
                    //libPlayer.sendActionBar(Utils.f("&7Cancelled lock!"));
                    Utils.sendActionBar(player, Utils.f("&7Cancelled lock!"));
                    this.cancel();
                    return;
                }

                if(this.i >= 3) {
                    this.cancel();
                    Location origin = player.getEyeLocation();
                    Vector direction = origin.getDirection();
                    Object[] objs = MathUtil.getNearestTarget(player, origin, direction, LauncherWeapon.this.range);
                    Block block = MathUtil.getTargetBlock(origin, direction, LauncherWeapon.this.range);
                    if (objs == null || (block != null && player.getLocation().distance((Location) objs[1]) > player.getLocation().distance(block.getLocation()))) {
                        libPlayer.playSound(Sound.ENTITY_ENDERDRAGON_GROWL);
                        //libPlayer.sendActionBar(Utils.f("&7Cancelled failed!"));
                        Utils.sendActionBar(player, Utils.f("&7Cancelled lock!"));
                        return;
                    }

                    LivingEntity target = (LivingEntity) objs[0];
                    libPlayer.playSound(Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
                    libPlayer.sendActionBar(ChatColor.GRAY + "Locked on " + ChatColor.GREEN + ChatColor.BOLD + (target.getCustomName() == null ? target.getName() : target.getCustomName()) + ChatColor.GRAY + '!');
                    LauncherWeapon.this.lockedTarget = target;
                    return;
                }

                this.i += 1;
                libPlayer.playSound(Sound.BLOCK_METAL_PRESSUREPLATE_CLICK_ON);
                //libPlayer.sendActionBar(Utils.f("&7Locking..."));
                Utils.sendActionBar(player, Utils.f("&7Locking..."));
            }
        }.runTaskTimer(GTMGuns.getInstance(), 0, 5);
    }

    @Override
    public void shoot(Player player, Location origin, Vector direction, PlayerCache playerCache, boolean auto) {
        AmmoUpdateEvent ammoUpdateEvent = new AmmoUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(ammoUpdateEvent);
        int totalAmmo = ammoUpdateEvent.getAmmo().getOrDefault(getAmmoType().getType(), 0);

        ItemStack newHeldItem = player.getEquipment().getItemInMainHand();
        int ammo = this.getAmmo(newHeldItem);
        if (ammo <= 0) {
            if (getAmmoType() != AmmoType.NONE && totalAmmo > 0) {
                reload(player, playerCache);
                return;
            }
            super.weaponState = WeaponState.IDLE;
            return;
        }

        newHeldItem = this.setAmmo(newHeldItem, ammo - 1, totalAmmo);
        player.getEquipment().getItemInMainHand().setItemMeta(newHeldItem.getItemMeta());
        player.updateInventory();
        if (ammo - 1 <= 0) reload(player, playerCache);
        else {
            if (!auto) super.weaponState = WeaponState.IDLE;
        }

        double newX = direction.getX() + 2 * this.accuracy * Math.random() - this.accuracy;
        double newY = direction.getY() + 2 * this.accuracy * Math.random() - this.accuracy;
        double newZ = direction.getZ() + 2 * this.accuracy * Math.random() - this.accuracy;

        Vector vector = new Vector(newX, newY, newZ);

        if(player.isInsideVehicle() && player.getVehicle().hasMetadata("WastedVehicle")) {
            WastedVehicle vehicle = (WastedVehicle) player.getVehicle().getMetadata("WastedVehicle").get(0).value();
            if(vehicle.getVehicleProperties().getVehicleType() == VehicleType.CAR) {
                if(player.getLocation().getPitch() >= 20)
                    origin.setPitch(Math.min(player.getLocation().getPitch(), 35));
                origin.setPitch(Math.min(player.getLocation().getPitch(), -10));
            }
        }

        final Projectile[] projectile = {null};
        if(super.getAmmoType() == AmmoType.GRENADE) {
            projectile[0] = origin.getWorld().spawn(origin, Snowball.class);
            projectile[0].setVelocity(vector.multiply(this.rocketSpeed));
            projectile[0].setShooter(player);
            projectile[0].setMetadata("Rocket", new FixedMetadataValue(GTMGuns.getInstance(), this));
            projectile[0].setMetadata("Shooter", new FixedMetadataValue(GTMGuns.getInstance(), player));
            projectile[0].setInvulnerable(true);
        }
        else {
            projectile[0] = origin.getWorld().spawn(origin, SmallFireball.class);
            projectile[0].setVelocity(vector.multiply(this.rocketSpeed));
            projectile[0].setShooter(player);
            projectile[0].setMetadata("Rocket", new FixedMetadataValue(GTMGuns.getInstance(), this));
            projectile[0].setMetadata("Shooter", new FixedMetadataValue(GTMGuns.getInstance(), player));
            projectile[0].setInvulnerable(true);
            ((SmallFireball)projectile[0]).setIsIncendiary(false);
        }

        rockets.add(projectile[0]);
        Sounds.broadcastSound(getSounds()[0], origin);
        if(getName().equalsIgnoreCase("net launcher") && player.getWorld().getName().equalsIgnoreCase("minesantos")) {
            Location eyeLocation = player.getEyeLocation();
            Vector originDirection = origin.getDirection();
            Object[] objs = MathUtil.getNearestTarget(player, eyeLocation, originDirection, this.range);
            if(objs != null) {
                if(objs[0] == null) {
                    if(objs[1] instanceof Location) {
                        onNetgunHit((Location) objs[1], player, null);}
                } else {
                    if(objs[0] instanceof LivingEntity) {
                        LivingEntity target = (LivingEntity) objs[0];
                        HouseUser houseUser = Houses.getUserManager().getLoadedUser(target.getUniqueId());
                        if (houseUser != null && (houseUser.isInsidePremiumHouse() || houseUser.isInsideHouse())) return;
                        if (!target.hasMetadata("WastedBarrel") && !(target.getLocation().distance(player.getLocation()) < 5)) {
                            
                        	// create entity damage by entity event and add to data handler
                            EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(player, target, DamageCause.DRAGON_BREATH, getDamage());
                            DamageDataHandler.getInstance().addData(target.getUniqueId(), edbee);
                            
                            // damage the entity and set last damage cause
                            target.damage(getDamage(), player);
                            // reset damage ticks so they can take damage again
                			target.setNoDamageTicks(0);
                            target.setLastDamageCause(edbee);

                            onNetgunHit(target.getLocation(), player, target);
                        }
                    }
                }

                rockets.remove(projectile[0]);
                projectile[0].remove();
                return;
            }
            else {
                Optional<LivingEntity> optional = MathUtil.getNearbyEntities(player, this.range).filter(e -> {
                    if(!(e instanceof Player)) return false;
                    return MathUtil.getCrossProduct(player, e.getLocation()) < 0.12;
                }).findFirst();

                if(optional.isPresent()) {
                    LivingEntity target = optional.get();
                    
                    // create entity damage by entity event and add to data handler
                    EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(player, target, DamageCause.DRAGON_BREATH, getDamage());
                    DamageDataHandler.getInstance().addData(target.getUniqueId(), edbee);
                    
                    // damage the entity and set last damage cause
                    target.damage(getDamage(), player);
                    // reset damage ticks so they can take damage again
        			target.setNoDamageTicks(0);
                    target.setLastDamageCause(edbee);

                    onNetgunHit(target.getLocation(), player, target);
                    rockets.remove(projectile[0]);
                    projectile[0].remove();
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                if(projectile[0] == null || projectile[0].isDead() || !projectile[0].isValid()) {
                    this.cancel();
                    return;
                }

                if(projectile[0].getWorld() != player.getWorld()) return;
                if(projectile[0].getLocation().distance(player.getLocation()) > getRange()) {
                    onExplode(projectile[0], (Player) projectile[0].getShooter());
                    this.cancel();
                }

                Optional<Entity> nearestTarget = projectile[0].getNearbyEntities(1, 1, 1).stream().filter(entity -> entity instanceof LivingEntity).findFirst();
                if(nearestTarget.isPresent() && nearestTarget.get() != player) {
                    onExplode(projectile[0], (Player) projectile[0].getShooter());
                    this.cancel();
                }
            }
        }.runTaskTimer(GTMGuns.getInstance(), 0, 1);

        new BukkitRunnable() {
            private int i;

            @Override
            public void run() {
                if(projectile[0] == null || !projectile[0].isValid() || projectile[0].isDead() || this.i++ > 200) {
                    rockets.remove(projectile[0]);
                    projectile[0].remove();
                    this.cancel();
                }
                else {
                    if(!getName().equalsIgnoreCase("net launcher")) projectile[0].getWorld().spigot().playEffect(projectile[0].getLocation(), LauncherWeapon.this.getEffect(), 0, 0, 0, 0, 0, 0, 1, 64);
                    if(lockedTarget != null && LauncherWeapon.this.homingLauncher) {
                        if(!lockedTarget.getWorld().equals(player.getWorld()) || lockedTarget.getLocation().distance(player.getLocation())>200){
                            lockedTarget = null;
                            new JLibPlayer(player).sendActionBar(Utils.f("&cTarget lock fail! The player is too far away!"));
                            return;
                        }
                        Vector vec = MathUtil.getVelocity(projectile[0].getLocation(), lockedTarget.getLocation());
                        projectile[0].setVelocity(vec.multiply(LauncherWeapon.this.rocketSpeed));
                    }
                }
            }
        }.runTaskTimerAsynchronously(GTMGuns.getInstance(), 0, 1);
    }
    
    /**
     * Called when the netgun hits an entity.
     * 
     * @param location - the location of the hit
     * @param shooter - the shooter of the netgun
     * @param target - the target being shot, if one is specified
     */
    public void onNetgunHit(Location location, Player shooter, LivingEntity target) {
        
    	// base stun duration
    	int netDuration = baseNetgunStun;
    	
    	// create netgun hit event and fire it
    	NetgunHitEvent nhe = new NetgunHitEvent(shooter, target, location, netDuration);
    	Bukkit.getPluginManager().callEvent(nhe);
    	
    	if (nhe.isCancelled()){
    		return;
    	}

    	if (nhe.getDuration() > 0){
    		createNetgunWeb(location, nhe.getDuration());
    	}
    }

    /**
     * Create the netgun web blocks at the specified location for the given duration.
     * 
     * @param location - the location to create the web blocks
     * @param netDuration - the number of ticks to keep the web blocks up for
     */
    public void createNetgunWeb(Location location, int netDuration) {
    	
    	// create a list of cobwebs
        Collection<Block> cobs = new ArrayList<>();
        
        // grab initial location
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        
        // iterate around location
        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 3; y++) {
                for (int z = 0; z < 2; z++) {
                	
                	// grab blocks and set to web
                    Block block = location.getWorld().getBlockAt(blockX + x, blockY + y, blockZ + z);
                    if (block.isEmpty()) {
                        block.setType(Material.WEB);
                        cobs.add(block);
                    }
                }
            }
        }
        
        // delay task and reset to normal
        new BukkitRunnable() {
            @Override
            public void run() {
                cobs.forEach(block -> block.setType(Material.AIR));
            }
        }.runTaskLater(GTMGuns.getInstance(), netDuration);
    }

    @Override
    public void onExplode(Entity projectile, Player shooter) {
    	
    	// get nearby entities to explosion
        Collection<LivingEntity> eventVictims = MathUtil.getNearbyEntities(projectile, this.explosionSize).collect(Collectors.toList());
        
        // call event
        ExplosionDamageEntityEvent damageEntityEvent = new ExplosionDamageEntityEvent(shooter, projectile, eventVictims, this);
        Bukkit.getPluginManager().callEvent(damageEntityEvent);
        
        // if event is cancelled, remove projectile
        if (damageEntityEvent.isCancelled()) {
            rockets.remove(projectile);
            projectile.remove();
            return;
        }

        // for each victim
        Collection<LivingEntity> victims = damageEntityEvent.getVictims();
        if (!victims.isEmpty()) {
            victims.forEach(e -> {
            	
            	// if not the same world
                if (projectile.getWorld() != e.getWorld()) return;
            	
            	// send entities flying away from this explosion
                if (!(e instanceof ArmorStand)) e.setVelocity(e.getLocation().getDirection().multiply(-this.explosionStrength));
                
                // call weapon damage to see if we modify the event
                WeaponDamageEvent weaponDamageEvent = new WeaponDamageEvent(shooter, this, getDamage(), e, DamageCause.DRAGON_BREATH);
                Bukkit.getPluginManager().callEvent(weaponDamageEvent);
                
                if (weaponDamageEvent.isCancelled()){
                	return;
                }

                // the initial damage modifier they should take
                double scaledDamage = 1.0;
                
                if (isScaledDamage()){

                	// the distance sq from the origin
                    double distanceSq = projectile.getLocation().distanceSquared(e.getLocation());
                    
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
                double totalDamage = Math.abs(scaledDamage * weaponDamageEvent.getDamage());
                
                // create entity damage by entity event and add to data handler
                EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(shooter, e, DamageCause.DRAGON_BREATH, totalDamage);
                DamageDataHandler.getInstance().addData(e.getUniqueId(), edbee);
                
                // damage the entity and set last damage cause
                e.damage(totalDamage, shooter);
                // reset damage ticks so they can take damage again
    			e.setNoDamageTicks(0);
                e.setLastDamageCause(edbee);
                
                // if this is a net launcher, add effects
                if (getName().equalsIgnoreCase("net launcher")) {
                    e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 80, 2, true, false));
                    e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 2, true, false));
                    e.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 80, 2, true, false));
                }
            });
        }

        if(getName().equalsIgnoreCase("net launcher")) onNetgunHit(projectile.getLocation(), shooter, null);
        else {
//            Vice
            TNTPrimed entity = (TNTPrimed) projectile.getWorld().spawnEntity(projectile.getLocation(), EntityType.PRIMED_TNT);
            entity.setCustomName("EXPLOSIVE");
            entity.setCustomNameVisible(false);
            entity.setFuseTicks(1);
            
            // add meta about the damage type of the explosion
            entity.setMetadata("entity_damage", new FixedMetadataValue(GTMGuns.getInstance(), false));
            entity.setMetadata("entity_damage", new FixedMetadataValue(GTMGuns.getInstance(), false));

            projectile.getWorld().spigot().playEffect(projectile.getLocation(), Effect.EXPLOSION_HUGE, 0, 0, 0, 0, 0, 0.01F, 1, 50);
            projectile.getWorld().playSound(projectile.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 5.0F, 5.0F);
        }

        rockets.remove(projectile);
        projectile.remove();
    }

    @Override
    public boolean isAutomatic() {
        return false;
    }

    public boolean isHomingLauncher() {
        return homingLauncher;
    }

    public boolean isBlowOnHit() {
        return blowOnHit;
    }

    public int getBlowDelay() {
        return blowDelay;
    }

    public double getExplosionStrength() {
        return explosionStrength;
    }

    public double getRocketSpeed() {
        return rocketSpeed;
    }

    public double getExplosionSize() {
        return explosionSize;
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
    public LauncherWeapon clone() {
        LauncherWeapon weapon = new LauncherWeapon(getUniqueIdentifier(), getName(), getWeaponType(), getAmmoType(), getBaseItemStack().clone(), getSounds(), getEffect());
        weapon.oldItemStack = super.oldItemStack.clone();
        weapon.deathMessages = super.deathMessages;
        weapon.walkSpeed = super.walkSpeed;
        weapon.delay = super.delay;

        weapon.attachments = super.attachments;
        weapon.supportedAttachments = super.supportedAttachments;
        weapon.weaponSkins = super.weaponSkins;
        weapon.effect = super.effect;
        weapon.damage = super.damage;
        weapon.meleeDamage = super.meleeDamage;
        weapon.accuracy = super.accuracy;
        weapon.recoil = super.recoil;
        weapon.magSize = super.magSize;
        weapon.reloadTime = super.reloadTime;
        weapon.range = super.range;
        weapon.penetration = super.penetration;
        weapon.zoom = super.zoom;
        weapon.reloadShoot = super.reloadShoot;

        weapon.homingLauncher = this.homingLauncher;
        weapon.blowOnHit = this.blowOnHit;
        weapon.blowDelay = this.blowDelay;
        weapon.explosionStrength = this.explosionStrength;
        weapon.rocketSpeed = this.rocketSpeed;
        weapon.explosionSize = this.explosionSize;
        weapon.scaledDamage = this.scaledDamage;
        weapon.multiShoot = super.multiShoot;
        weapon.baseNetgunStun = this.baseNetgunStun;

        return weapon;
    }
}
