package net.grandtheftmc.guns.weapon.ranged;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.BlockPosition;
import com.google.common.collect.Maps;
import com.j0ach1mmall3.jlib.methods.Random;
import com.j0ach1mmall3.jlib.methods.Sounds;
import com.j0ach1mmall3.wastedguns.MathUtil;
import com.j0ach1mmall3.wastedguns.api.events.WeaponDamageEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.AmmoUpdateEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponReloadEvent;
import com.j0ach1mmall3.wastedguns.api.events.ranged.RangedWeaponShootEvent;
import com.j0ach1mmall3.wastedvehicles.Main;

import de.slikey.effectlib.effect.ExplodeEffect;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.guns.DamageDataHandler;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.WeaponState;
import net.grandtheftmc.guns.cache.PlayerCache;
import net.grandtheftmc.guns.weapon.AmmoType;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.WeaponSkin;
import net.grandtheftmc.guns.weapon.WeaponType;
import net.grandtheftmc.guns.weapon.attribute.WeaponRPM;
import net.grandtheftmc.guns.weapon.ranged.attachment.Attachment;
import net.grandtheftmc.guns.weapon.ranged.guns.PistolWeapon;
import net.grandtheftmc.guns.weapon.ranged.guns.ShotgunWeapon;
import net.grandtheftmc.guns.weapon.ranged.guns.SpecialWeapon;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * Created by Luke Bingham on 17/07/2017.
 */
public abstract class RangedWeapon<T extends Weapon> extends Weapon<T> {

    protected HashMap<Attachment, Object> attachments;
    protected Attachment[] supportedAttachments = null;
    protected Effect effect;

    protected double damage = 0d, meleeDamage = 0d, accuracy = 0d, recoil = 0;
    protected int magSize = 1, reloadTime = 40, range = 20, penetration = 1, zoom = 0;
    protected boolean reloadShoot = false;
    /**
     *  Can this weapon fire multiple times even if it's not done firing the first round. 
     *  Typically burst weapons will burst over a task, and still allow to fire.
     */
    protected boolean multiShoot = false;

    /**
     * Construct a new Weapon.
     */
    public RangedWeapon(short uniqueIdentifier, String name, WeaponType weaponType, AmmoType ammoType, ItemStack itemStack, Sound[] sounds, Effect effect) {
        super(uniqueIdentifier, name, weaponType, ammoType, itemStack, sounds);
        this.attachments = Maps.newHashMap();
        this.effect = effect;

        this.weaponSkins = new WeaponSkin[] {
                new WeaponSkin(weaponType, itemStack.getDurability(), "&e&lDefault")
        };
    }

    public Effect getEffect() {
        return this.effect;
    }

    public Object getAttachment(Attachment attachment) {
        return this.attachments.getOrDefault(attachment, null);
    }

    public boolean hasAttachment(Attachment attachment) {
        return this.attachments.containsKey(attachment);
    }

    public void addAttachment(Attachment attachment) {
        if(this.supportedAttachments == null) return;

        boolean supported = false;
        for(Attachment a : this.supportedAttachments) {
            if (a != attachment) continue;
            supported = true;
            break;
        }

        if(!supported) return;
        Object obj = null;
        switch (attachment) {//TODO
            case SUPPRESSOR:
                obj = 0;
                break;
            case GRIP: obj = 0; break;
            case EXTENDED_MAGS: obj = 0; break;
            case SCOPE: obj = 0; break;
        }

        this.attachments.put(attachment, obj);
    }

    public Attachment[] getSupportedAttachments() {
        return supportedAttachments;
    }

    public void setSupportedAttachments(Attachment... supportedAttachments) {
        this.supportedAttachments = supportedAttachments;
    }

    @Override
	public WeaponSkin[] getWeaponSkins() {
        return weaponSkins;
    }

    protected void setWeaponSkins(WeaponSkin... skins) {
        WeaponSkin defaultSkin = this.weaponSkins[0];
        this.weaponSkins = new WeaponSkin[skins.length + 1];
        for(int i = 0; i < skins.length; i++)
            this.weaponSkins[i + 1] = skins[i];
        this.weaponSkins[0] = defaultSkin;
    }

    @Override
    public String[] getStatsBar() {
        String[] output = new String[4];
        String symbol = ":",
                done = ChatColor.GREEN.toString() + ChatColor.BOLD,
                empty = ChatColor.DARK_GRAY.toString() + ChatColor.BOLD;
        int bars = 10;
        double best = 0;
        double result;
        int stat;
        for(int i = 0; i < 4; i++) {
            switch (i) {
                case 0:
                    output[i] = "";
                    best = 30.0;
                    result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, (this instanceof ShotgunWeapon ? this.damage * ((ShotgunWeapon) this).getShellSize() : this.damage));
                    stat = (int) Math.floor(result) / 10;
                    for(int x = 0; x < bars; x++) {
                        output[i] += (x <= stat ? done : empty) + symbol;
                    }
                    output[i] += C.GRAY + " Damage";
                    break;

                case 1:
                    output[i] = "";
                    best = this instanceof WeaponRPM ? (500.0 - 300) / 60 + 1 : 2;
                    if(this instanceof ShotgunWeapon) {
                        result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValuesReverse(best, this.delay);
                    } else if(this instanceof WeaponRPM) {
                        result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, ((WeaponRPM) this).getRpm());
                    } else {
                        result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValuesReverse(best, this.delay);
                    }
                    stat = (int) Math.floor(result) / 10;
                    for(int x = 0; x < bars; x++) {
                        output[i] += (x <= stat ? done : empty) + symbol;
                    }
                    output[i] += C.GRAY + " Fire Rate";
                    break;

                case 2:
                    output[i] = "";
                    best = 0.008;
                    result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValuesReverse(best, this.accuracy);
                    stat = (int) Math.floor(result) / 10;
                    for(int x = 0; x < bars; x++) {
                        output[i] += (x <= stat ? done : empty) + symbol;
                    }
                    output[i] += C.GRAY + " Accuracy";
                    break;

                case 3:
                    output[i] = "";
                    best = 80.0;
                    result = net.grandtheftmc.core.util.MathUtil.getPercentBetweenValues(best, this.range);
                    stat = (int) Math.floor(result) / 10;
                    for(int x = 0; x < bars; x++) {
                        output[i] += (x <= stat ? done : empty) + symbol;
                    }
                    output[i] += C.GRAY + " Range";
                    break;
            }
        }

        return output;
    }

    /**
     * Get the generic damage of the Weapon.
     *
     * @return damage value
     */
    public double getDamage() {
        return this.damage;
    }

    /**
     * Get the Melee damage of the Weapon.
     *
     * @return damage value
     */
    public double getMeleeDamage() {
        return this.meleeDamage;
    }

    /**
     * Get the Accuracy of the Weapon.
     *
     * @return Accuracy value
     */
    public double getAccuracy() {
        return this.accuracy;
    }

    /**
     * Get the Magazine size of the Weapon.
     *
     * @return Magazine size
     */
    public int getMagazineSize() {
        return this.magSize + (int) this.attachments.getOrDefault(Attachment.EXTENDED_MAGS, 0);
    }

    /**
     * Get the reload time for the Weapon.
     *
     * @return Reload time in ticks
     */
    public int getReloadTime() {
        return this.reloadTime;
    }

    /**
     * Check if reloading while shooting is allowed.
     *
     * @return reload state
     */
    public boolean isAllowingReloadShooting() {
        return this.reloadShoot;
    }

    /**
     * Get the fire range of the Weapon.
     *
     * @return fire range (Measured in Blocks)
     */
    public int getRange() {
        return this.range;
    }

    /**
     * Get the amount of entities to pierce from a single Bullet.
     *
     * @return Penetration value
     */
    public int getPenetration() {
        return this.penetration;
    }

    /**
     * Get the Zoom identifier for the Weapon.
     *
     * @return Zoom value
     */
    public int getZoomValue() {
        return this.zoom;
    }

    /**
     * Get the Recoil of the Weapon.
     *
     * @return Recoil value
     */
    public double getRecoil() {
        return this.recoil;
    }

    public abstract boolean isAutomatic();

    private boolean minigun() {
        if(!(this instanceof SpecialWeapon)) return false;
        return ((SpecialWeapon) this).isMinigun();
    }

    @Override
    public void onSneak(Player player, boolean sneaking) {
        if(sneaking && this.weaponState != WeaponState.RELOADING) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, this.zoom - 1, true, false));
            return;
        }

        player.removePotionEffect(PotionEffectType.SLOW);
    }

    @Override
    public void onRightClick(Player player) {
        onWeaponShoot(player, true, GTMGuns.getInstance().getWeaponManager().getPlayerCache(player.getUniqueId()));
    }

    @Override
    public void onLeftClick(Player player) {
        int ammo = this.getAmmo(player.getInventory().getItemInMainHand());
        if(ammo >= magSize) return;
        reload(player, GTMGuns.getInstance().getWeaponManager().getPlayerCache(player.getUniqueId()));
    }

    public void onWeaponShoot(Player player, boolean subtractammo, PlayerCache playerCache) {
    	
    	// how many revolutions per second should this be
    	// convert to ticks, so 1 = 1 per 20 ticks
    	int rps = this instanceof WeaponRPM ? ((WeaponRPM) this).getRPS() : 0;
    	
    	// TODO remove for compatibility purposes
    	// if not defined
    	if (rps == 0){
    		rps = this instanceof WeaponRPM ? ((WeaponRPM) this).getRpm() : 0;
    	}
    	
    	// TODO debug remove
    	// Core.log("[RangedWeapon][DEBUG] onWeaponShoot for " + player.getName() + ", weapon=" + this.getName() + ", rps=" + rps + ", automatic=" + this.isAutomatic() + ", delay=" + this.delay + ", multiShoot=" + this.multiShoot);
    	
        if(player == null) return;
        if(playerCache == null) return;

        // if the weapon is reloading do not call
        if(this.weaponState == WeaponState.RELOADING) return;
        
        // if weapon cannot multi shoot, check state and cancel
        if (!multiShoot){
        	if(this.weaponState == WeaponState.BURSTING || weaponState == WeaponState.SHOOTING){
        		return;
        	}
        }

        RangedWeaponShootEvent rangedWeaponShootEvent = new RangedWeaponShootEvent(player, this);
        Bukkit.getPluginManager().callEvent(rangedWeaponShootEvent);
        if(rangedWeaponShootEvent.isCancelled()) return;

//        AmmoUpdateEvent ammoUpdateEvent = new AmmoUpdateEvent(player);
//        Bukkit.getPluginManager().callEvent(ammoUpdateEvent);
//        int totalAmmo = ammoUpdateEvent.getAmmo().getOrDefault(getAmmoType().getType(), 0);

        weaponState = WeaponState.SHOOTING;

        if(this.isAutomatic()) {
            playerCache.shooting = true;
            playerCache.burst = 1;

            new BukkitRunnable() {
                @Override public void run() {
                	
                	// TODO debug remove
                	// Core.log("[RangedWeapon][DEBUG] weaponState on tick= " + weaponState);
                	
                    if(RangedWeapon.this.weaponState == WeaponState.RELOADING) {
                        this.cancel();
                        playerCache.burst = 1;
                        playerCache.shooting = false;
                        return;
                    }

                    if (RangedWeapon.this instanceof WeaponRPM){
                    	
                    	// get the max burst tick possible
                    	int maxBurst = ((WeaponRPM) RangedWeapon.this).getBurstRate();
                    	
                    	// clamp to valid values
                    	if (maxBurst <= 0){
                    		maxBurst = 0;
                    	}
                    	if (maxBurst >= 20){
                    		maxBurst = 20;
                    	}
                    	
                    	if (playerCache.burst >= maxBurst){
	                    		
	                    	// TODO debug remove
	                    	// Core.log("[RangedWeapon][DEBUG] playerCache.burst=" + playerCache.burst + " is >= " + maxBurst);
	                    	this.cancel();
	                        playerCache.burst = 1;
	                        playerCache.shooting = false;
	                        weaponState = WeaponState.IDLE;
	                        return;
                    	}
                    }

                    playerCache.burst += 1;

                    if(isAutomatic()) {
                    	weaponState = WeaponState.BURSTING;
                    	
                        int tick = playerCache.tick;
                        playerCache.tick = (tick >= 20) ? 1 : (tick + 1);
                        if (!RangedWeapon.this.isValid(playerCache.tick)){
                        	// TODO debug remove
                        	// Core.log("[RangedWeapon][DEBUG] Ranged weapon is not valid, playerCache.tick=" + playerCache.tick);
                        	
                        	return;
                        }
                    }

                    Location origin = player.getEyeLocation();
                    Vector direction = origin.getDirection();

                    RangedWeapon.this.shoot(player, origin, direction, playerCache, true);
                }
            }.runTaskTimer(GTMGuns.getInstance(), 1, 1);
            return;
        }

        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();

        this.shoot(player, origin, direction, playerCache, false);

        if (this.recoil != 0 && !player.isSneaking())
            player.setVelocity(player.getVelocity().add(direction.setY(0).multiply(-this.recoil)));
    }

    public void onWeaponVehicleShoot(Player player, boolean subtractammo, PlayerCache playerCache) {
    	
    	// TODO debug remove
    	// Core.log("[RangedWeapon][DEBUG] onWeaponVehicleShoot for " + player.getName());
    	
        if(player == null) return;
        if(playerCache == null) return;

        // if the weapon is reloading do not call
        if(this.weaponState == WeaponState.RELOADING) return;
        
        // if weapon cannot multi shoot, check state and cancel
        if (!multiShoot){
        	if(this.weaponState == WeaponState.BURSTING || weaponState == WeaponState.SHOOTING){
        		return;
        	}
        }

        RangedWeaponShootEvent rangedWeaponShootEvent = new RangedWeaponShootEvent(player, this);
        Bukkit.getPluginManager().callEvent(rangedWeaponShootEvent);
        if(rangedWeaponShootEvent.isCancelled()) return;

//        AmmoUpdateEvent ammoUpdateEvent = new AmmoUpdateEvent(player);
//        Bukkit.getPluginManager().callEvent(ammoUpdateEvent);
//        int totalAmmo = ammoUpdateEvent.getAmmo().getOrDefault(getAmmoType().getType(), 0);

        weaponState = WeaponState.SHOOTING;

        if(this.isAutomatic()) {
            playerCache.shooting = true;
            playerCache.burst = 1;

            new BukkitRunnable() {
                @Override public void run() {
                    if(RangedWeapon.this.weaponState == WeaponState.RELOADING) {
                        this.cancel();
                        playerCache.burst = 1;
                        playerCache.shooting = false;
                        return;
                    }

                    if (RangedWeapon.this instanceof WeaponRPM){
                    	
                    	// get the max burst tick possible
                    	int maxBurst = ((WeaponRPM) RangedWeapon.this).getBurstRate();
                    	
                    	// clamp to valid values
                    	if (maxBurst <= 0){
                    		maxBurst = 0;
                    	}
                    	if (maxBurst >= 20){
                    		maxBurst = 20;
                    	}
                    	
                    	if (playerCache.burst >= maxBurst){
	                    		
	                    	// TODO debug remove
	                    	// Core.log("[RangedWeapon][DEBUG] playerCache.burst=" + playerCache.burst + " is >= " + (((WeaponRPM) RangedWeapon.this).getRpm() > 0 ? 5 : 1));
	                    	this.cancel();
	                        playerCache.burst = 1;
	                        playerCache.shooting = false;
	                        weaponState = WeaponState.IDLE;
	                        return;
                    	}
                    }

                    playerCache.burst += 1;

                    if(isAutomatic()) {
                    	weaponState = WeaponState.BURSTING;
                    	
                        int tick = playerCache.tick;
                        playerCache.tick = (tick >= 20) ? 1 : (tick + 1);
                        if (!RangedWeapon.this.isValid(playerCache.tick)){
                        	// TODO debug remove
                        	// Core.log("[RangedWeapon][DEBUG] Ranged weapon is not valid, playerCache.tick=" + playerCache.tick);
                        	return;
                        }
                    }

                    Location origin = player.getEyeLocation();
                    Vector direction = origin.getDirection();

                    RangedWeapon.this.vehicleShoot(player, origin, direction, true);
                }
            }.runTaskTimer(GTMGuns.getInstance(), 1, 1);

            return;
        }

        Location origin = player.getEyeLocation();
        Vector direction = origin.getDirection();

        this.shoot(player, origin, direction, playerCache, false);

//        if (this.recoil != 0 && !player.isSneaking())
//            player.setVelocity(player.getVelocity().add(direction.setY(0).multiply(-this.recoil)));
    }

    public void shoot(Player player, Location origin, Vector direction, PlayerCache playerCache, boolean auto) {
        origin = player.getEyeLocation();
        direction = origin.getDirection();

        User shooterUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(shooterUser == null) return;
        boolean useParticles = shooterUser.getPref(Pref.SHOW_PARTICLES);

        boolean flamethrower = getWeaponType() == WeaponType.FLAMETHROWER;
        int ammountOfBullets = 1;
        if(getWeaponType() == WeaponType.SHOTGUN && this instanceof ShotgunWeapon)
            ammountOfBullets = ((ShotgunWeapon) this).getShellSize();

        AmmoUpdateEvent ammoUpdateEvent = new AmmoUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(ammoUpdateEvent);
        int totalAmmo = ammoUpdateEvent.getAmmo().getOrDefault(getAmmoType().getType(), 0);

        ItemStack newHeldItem = player.getEquipment().getItemInMainHand();
        int ammo = this.getAmmo(newHeldItem);
        if(ammo <= 0) {
            if(getAmmoType() != AmmoType.NONE && totalAmmo > 0) {
                reload(player, playerCache);
                return;
            }
            weaponState = WeaponState.IDLE;
            return;
        }

        newHeldItem = this.setAmmo(newHeldItem, ammo - 1, totalAmmo);
        player.getEquipment().getItemInMainHand().setItemMeta(newHeldItem.getItemMeta());
        player.updateInventory();
        if (ammo - 1 <= 0) reload(player, playerCache);
        else {
            if(!auto) weaponState = WeaponState.IDLE;
        }
        
        // logic behind calculating accuracy: https://imgur.com/a/erSo2?
        for (int i = 0; i < ammountOfBullets; i++){
        	
        	// 2 TIMES accuracy gives the DIAMETER around the initial direction
        	// RANDOM TIMES this accuracy gives a value between the left lower
        	// limit and the right upper limit.
        	// subtracting the accuracy gets the point centered around the origin
        	
        	double dx = (2 * accuracy) * Math.random() - accuracy;
        	double dy = (2 * accuracy) * Math.random() - accuracy;
        	double dz = (2 * accuracy) * Math.random() - accuracy;
        	
        	if (getWeaponType() == WeaponType.SNIPER && player.isSneaking()){
        		dx = 0;
        		dy = 0;
        		dz = 0;
        	}
        	
        	double newX = direction.getX() + dx;
            double newY = direction.getY() + dy;
            double newZ = direction.getZ() + dz;

            Vector vector = new Vector(newX, newY, newZ);

            double range = this.range;
            Object[] o = MathUtil.getNearestTarget(player, origin, vector, this.range);
            Block b = MathUtil.getTargetBlock(origin, vector, this.range);

            if (o != null) {
                if (b == null || player.getLocation().distance((Location) o[1]) < player.getLocation().distance(b.getLocation())) {
                    LivingEntity target = (LivingEntity) o[0];
                    Location intersection = (Location) o[1];

                    //Blood particles on Hit.
//                    target.getWorld().spigot().playEffect(intersection, Effect.COLOURED_DUST, 0, 0, 255, 0, 0, 1, 0, 64);

                    // call weapon damage to see if we modify the event
                    WeaponDamageEvent weaponDamageEvent = new WeaponDamageEvent(player, this, newHeldItem, getDamage(), target, DamageCause.DRAGON_BREATH);
                    Bukkit.getPluginManager().callEvent(weaponDamageEvent);
                    
                    if (weaponDamageEvent.isCancelled()){
                    	return;
                    }
        			
                    // create entity damage by entity event and add to data handler
                    EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(player, target, DamageCause.DRAGON_BREATH, weaponDamageEvent.getDamage());
                    DamageDataHandler.getInstance().addData(target.getUniqueId(), edbee);
        			
                    // damage the entity and set last damage cause
                    target.damage(weaponDamageEvent.getDamage(), player);
                    // reset damage ticks so they can take damage again
        			target.setNoDamageTicks(0);
        			target.setLastDamageCause(edbee);
                    
        			// TODO this was removed because target.damage(amount, Entity) called EntityDamageByEntityEvent which called the logic for melee damage.
                    //target.setNoDamageTicks(0);
                    //target.damage(this.damage, player);
                    //target.setLastDamageCause(new EntityDamageEvent(player, EntityDamageEvent.DamageCause.DRAGON_BREATH, this.damage));

                    if (getWeaponType() == WeaponType.PISTOL && this instanceof PistolWeapon && ((PistolWeapon) this).isStun()) {
                        if(target.getLocation().getWorld() != null && !target.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
                            int stunDuration = ((PistolWeapon) this).getDuration();
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunDuration, 2, true, false));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, stunDuration, 2, true, false));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, stunDuration, 2, true, false));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, stunDuration, 2, true, false));
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, stunDuration, 2, true, false));
                        }
                    }

                    else if(getWeaponType() == WeaponType.FLAMETHROWER) {
                        if(target.getLocation().getWorld() != null && !target.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
                            target.setFireTicks(100);
                        }
                    }
                    else if(getWeaponType()==WeaponType.CLAUSINATOR) {
                        if(!target.getWorld().getName().equalsIgnoreCase("spawn")){
                            int cSlow = target.hasPotionEffect(PotionEffectType.SLOW) ? target.getPotionEffect(PotionEffectType.SLOW).getAmplifier() : -1;
                            cSlow += 1;
                            if(cSlow>=5)
                                cSlow = 5;
                            target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20*6, cSlow), true);
                            target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 10, 0), true);

                            ExplodeEffect explodeEffect = new ExplodeEffect(GTMGuns.getInstance().getEffectManager());
                            explodeEffect.sound = Sound.ENTITY_BLAZE_SHOOT;
                            explodeEffect.setLocation(target.getLocation());
                            explodeEffect.start();
                        }
                    }
                    range = player.getLocation().distance((Location) o[1]);
                }

            }
            if (b != null) {
                Location l = b.getLocation();
                if (!flamethrower) b.getWorld().playEffect(l, Effect.STEP_SOUND, b.getType());

                if (!flamethrower) {
                    //Block damage particle
                    b.getWorld().playEffect(l, Effect.STEP_SOUND, b.getType());

                    //Block damage crack
                    if (GTMGuns.getInstance().getWeaponManager().getIgnoredBlocks().contains(b.getType())) return;
                    PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
                    packetContainer.getIntegers().write(0, Random.getInt());
                    packetContainer.getBlockPositionModifier().write(0, new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
                    packetContainer.getIntegers().write(1, Random.getInt(10));
                    ProtocolLibrary.getProtocolManager().broadcastServerPacket(packetContainer);
                }
                range = player.getLocation().distance(l);
            }
            Location l = origin.clone();
            vector.multiply(0.5);
            for (double k = 0; k < range * 2; k++) {
                if (useParticles) l.getWorld().spigot().playEffect(l, getEffect(), 0, 0, 0, 0, 0, 0, 1, 64);
                l.add(vector.getX(), vector.getY(), vector.getZ());
            }
        }
        Sounds.broadcastSound(getSounds()[0], origin);
        if (RangedWeapon.this.recoil != 0 && !player.isSneaking())
            player.setVelocity(player.getVelocity().add(direction.setY(0).multiply(-(RangedWeapon.this.recoil/2))));
    }

    public void vehicleShoot(Player player, Location origin, Vector direction, boolean auto) {
//        origin = player.getEyeLocation();
//        direction = origin.getDirection();

        User shooterUser = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(shooterUser == null) return;
        boolean useParticles = shooterUser.getPref(Pref.SHOW_PARTICLES);

//        boolean flamethrower = getWeaponType() == WeaponType.FLAMETHROWER;
//        int ammountOfBullets = 1;
//        if(getWeaponType() == WeaponType.SHOTGUN && this instanceof ShotgunWeapon)
//            ammountOfBullets = ((ShotgunWeapon) this).getShellSize();

        AmmoUpdateEvent ammoUpdateEvent = new AmmoUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(ammoUpdateEvent);
        int totalAmmo = ammoUpdateEvent.getAmmo().getOrDefault(getAmmoType().getType(), 0);
//
//        ItemStack newHeldItem = player.getEquipment().getItemInMainHand();
//        int ammo = this.getAmmo(newHeldItem);
//        if(ammo <= 0) {
//            if(getAmmoType() != AmmoType.NONE && totalAmmo > 0) {
//                this.reload(player, playerCache);
//                return;
//            }
//            weaponState = WeaponState.IDLE;
//            return;
//        }

//        newHeldItem = this.setAmmo(newHeldItem, ammo - 1, totalAmmo);
//        player.getEquipment().getItemInMainHand().setItemMeta(newHeldItem.getItemMeta());
//        player.updateInventory();
//        if (ammo - 1 <= 0) this.reload(player, playerCache);
//        else {
//            if(!auto) weaponState = WeaponState.IDLE;
//        }

//        for (int i = 0; i < ammountOfBullets; i++) {
        double newX = direction.getX() + 2 * this.accuracy * Math.random() - this.accuracy;
        double newY = direction.getY() + 2 * this.accuracy * Math.random() - this.accuracy;
        double newZ = direction.getZ() + 2 * this.accuracy * Math.random() - this.accuracy;

        Vector vector = new Vector(newX, newY, newZ);

        double range = this.range;
        Object[] o = MathUtil.getNearestTarget(player, origin, vector, this.range);
        Block b = MathUtil.getTargetBlock(origin, vector, this.range);

        if (o != null) {
            if (b == null || player.getLocation().distance((Location) o[1]) < player.getLocation().distance(b.getLocation())) {
                LivingEntity target = (LivingEntity) o[0];
                Location intersection = (Location) o[1];

                //Blood particles on Hit.
//                    target.getWorld().spigot().playEffect(intersection, Effect.COLOURED_DUST, 0, 0, 255, 0, 0, 1, 0, 64);

                // create entity damage by entity event and add to data handler
                EntityDamageByEntityEvent edbee = new EntityDamageByEntityEvent(player, target, DamageCause.DRAGON_BREATH, this.damage);
                DamageDataHandler.getInstance().addData(target.getUniqueId(), edbee);
                
                target.damage(this.damage, player);
                target.setNoDamageTicks(0);
                target.setLastDamageCause(edbee);
                
                if (getWeaponType() == WeaponType.PISTOL && this instanceof PistolWeapon && ((PistolWeapon) this).isStun()) {
                    if(target.getLocation().getWorld() != null && !target.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
                        int stunDuration = ((PistolWeapon) this).getDuration();
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, stunDuration, 2, true, false));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, stunDuration, 2, true, false));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, stunDuration, 2, true, false));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, stunDuration, 2, true, false));
                        target.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_DIGGING, stunDuration, 2, true, false));
                    }
                }

                if(getWeaponType() == WeaponType.FLAMETHROWER) {
                    if(target.getLocation().getWorld() != null && !target.getLocation().getWorld().getName().equalsIgnoreCase("spawn")) {
                        target.setFireTicks(100);
                    }
                }
                range = player.getLocation().distance((Location) o[1]);
            }

        }
        if (b != null) {
            Location l = b.getLocation();
//            if (!flamethrower) b.getWorld().playEffect(l, Effect.STEP_SOUND, b.getType());
//
//            if (!flamethrower) {
//                //Block damage particle
//                b.getWorld().playEffect(l, Effect.STEP_SOUND, b.getType());
//
//                //Block damage crack
//                if (GTMGuns.getInstance().getWeaponManager().getIgnoredBlocks().contains(b.getType())) return;
//                PacketContainer packetContainer = new PacketContainer(PacketType.Play.Server.BLOCK_BREAK_ANIMATION);
//                packetContainer.getIntegers().write(0, Random.getInt());
//                packetContainer.getBlockPositionModifier().write(0, new BlockPosition(l.getBlockX(), l.getBlockY(), l.getBlockZ()));
//                packetContainer.getIntegers().write(1, Random.getInt(10));
//                ProtocolLibrary.getProtocolManager().broadcastServerPacket(packetContainer);
//            }
            range = player.getLocation().distance(l);
        }

        Location l = origin.clone();
        vector.multiply(0.5);
        for (double k = 0; k < range * 2; k++) {
            if (useParticles) l.getWorld().spigot().playEffect(l, getEffect(), 0, 0, 0, 0, 0, 0, 1, 64);
            l.add(vector.getX(), vector.getY(), vector.getZ());
        }
//        }

        Sounds.broadcastSound(getSounds()[0], origin);
//        if (RangedWeapon.this.recoil != 0 && !player.isSneaking())
//            player.setVelocity(player.getVelocity().add(direction.setY(0).multiply(-(RangedWeapon.this.recoil/2))));
    }

    public void reload(Player player, PlayerCache playerCache) {
        if(player == null || playerCache == null) return;
        if(this.getName().equalsIgnoreCase("clausinator")) {
            int ammo = getAmmo(player.getInventory().getItemInMainHand());
            if(ammo!=0) {
                player.sendMessage(Lang.GTM.f("&cThe clip must be empty in order to reload"));
                return;
            }
            TextComponent tc = new TextComponent("Click Here to Reload Clausinator For $100,000");
            tc.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            tc.setBold(true);
            tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/christmas clausinator"));
            player.spigot().sendMessage(tc);
            return;
        }
        if(this.weaponState == WeaponState.RELOADING) return;


        if(this.getWeaponType() == WeaponType.FLAMETHROWER) {
            ItemStack itemStack = Main.getPlugin(Main.class).getBabies().getJetpackFuelItem().clone();
            boolean found = false;
            for (ItemStack item : player.getInventory().getContents()) {
                if(item == null) continue;
                if(item.getType() != itemStack.getType()) continue;
                if(!item.hasItemMeta() || !itemStack.hasItemMeta()) continue;
                if(!item.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) continue;

                if (item.getAmount() == 1) player.getInventory().remove(item);
                else item.setAmount(item.getAmount() - 1);

                found = true;
                break;
            }

            if(!found) {
                player.sendMessage(Lang.AMMO.f("&7The &c&lFlamethrower &7requires (jetpack) fuel to use!"));
                return;
            }

            player.updateInventory();
        }

        this.weaponState = WeaponState.RELOADING;
        playerCache.burst = 0;
        playerCache.shooting = false;

        int ammo = getAmmo(player.getEquipment().getItemInMainHand());
        int ammoToReload = this.magSize - ammo;
        new BukkitRunnable() {
            private int i;

            @Override
            public void run() {
                if (RangedWeapon.this.weaponState != WeaponState.RELOADING) {
                    this.cancel();
                    return;
                }

                ItemStack heldItem = player.getEquipment().getItemInMainHand();
                if(heldItem == null || heldItem.getType() == Material.AIR) {
                    RangedWeapon.this.weaponState = WeaponState.IDLE;
                    playerCache.burst = 0;
                    playerCache.shooting = false;
                    this.cancel();
                    return;
                }

                Weapon<?> weaponInHand = playerCache.getOrAddWeapon(heldItem);
                
                boolean checkSkins = false;
                for(WeaponSkin skin : RangedWeapon.this.getWeaponSkins()) {
                    if(heldItem.getDurability() == skin.getIdentifier()) {
                        checkSkins = true;
                    }
                }
                
                if (weaponInHand == null || (!RangedWeapon.this.getName().equals(weaponInHand.getName()) && RangedWeapon.this.getWeaponIdentifier() != weaponInHand.getWeaponIdentifier() && !checkSkins)) {
                    RangedWeapon.this.weaponState = WeaponState.IDLE;
                    playerCache.burst = 0;
                    playerCache.shooting = false;
                    this.cancel();
                    return;
                }

                RangedWeaponReloadEvent event = new RangedWeaponReloadEvent(player, RangedWeapon.this, 1);
                Bukkit.getPluginManager().callEvent(event);
                if (event.isCancelled()) {
                    RangedWeapon.this.weaponState = WeaponState.IDLE;
                    playerCache.burst = 0;
                    playerCache.shooting = false;
                    this.cancel();
                    return;
                }

                this.i += event.getAmmoToReload();
                int totalAmmo = 0;

                AmmoUpdateEvent event1 = new AmmoUpdateEvent(player);
                Bukkit.getPluginManager().callEvent(event1);
                totalAmmo = event1.getAmmo().getOrDefault(RangedWeapon.this.getAmmoType().getType(), 0);

                heldItem = RangedWeapon.this.setAmmo(heldItem, ammo + this.i, totalAmmo);
                player.getEquipment().setItemInMainHand(heldItem);
                Sounds.broadcastSound(RangedWeapon.this.getSounds()[3], player.getEyeLocation());

                if (this.i >= ammoToReload) {
                    RangedWeapon.this.weaponState = WeaponState.IDLE;
                    playerCache.burst = 0;
                    playerCache.shooting = false;
                    this.cancel();
                }
            }
        }.runTaskTimer(GTMGuns.getInstance(), this.reloadTime / this.magSize, this.reloadTime / this.magSize);
    }

    public boolean isValid(int tick) {
    	
    	// how many revolutions per second should this be
    	// convert to ticks, so 1 = 1 per 20 ticks
    	int rps = this instanceof WeaponRPM ? ((WeaponRPM) this).getRPS() : 0;
    	
    	// TODO remove for compatibility purposes
    	// if not defined
    	if (rps == 0){
    		rps = this instanceof WeaponRPM ? ((WeaponRPM) this).getRpm() : 0;
    	}
        
        switch (rps) {
        	case 1:
        		// fires on tick 1
                return tick == 1;
            case 2:
            	// fires on tick 1,11
                return tick % 10 == 1;
            case 3:
            	// fires on tick 6,12,18
                return tick % 6 == 0;
            case 4:
            	// fires on tick 5,10,15,20
                return tick % 5 == 0;
            case 5:
            	// fires on tick 4,8,12,16,20
                return tick % 4 == 0;
            case 6:
            	// fires on tick 3,6,9,12,15,18
                return tick % 3 == 0;
            case 7:
            	// fires on tick 1,4,7,10,13,16,19
                return tick % 3 == 1;
            case 8:
            	// fires on tick 1,3,6,8,11,13,16,18
                return tick % 5 == 1 || tick % 5 == 3;
            case 9:
            	// fires on tick 1,3,5,8,10,12,15,17,19
            	return tick % 7 == 1 || tick % 7 == 3 || tick % 7 == 5;
            case 10:
            	// fires on tick 1,3,5,7,9,11,13,15,17,19
                return tick % 2 == 1;
            case 11:
            	// fires on tick 1,2,4,6,8,10,12,14,16,18,20
                return tick % 2 == 0 || tick == 1;
            case 12:
            	// fires on tick 1,2,4,6,8,10,11,12,14,16,18,20
                return tick % 2 == 0 || tick == 1 || tick == 11;
            case 13:
            	// fires on tick 1,2,4,6,8,10,11,12,14,16,18,19,20
                return tick % 2 == 0 || tick == 1 || tick == 11 || tick == 19;
            case 14:
            	// fires on tick 1,2,4,5,7,8,10,11,13,14,16,17,19,20
                return tick % 3 != 0;
            case 15:
            	// fires on tick 1,2,3,5,6,7,9,10,11,13,14,15,17,18,19
                return tick % 4 != 0; 
            case 16:
            	// fires on tick 1,2,3,4,6,7,8,9,11,12,13,14,16,17,18,19
                return tick % 5 != 0;
            case 17:
            	// fires on tick 1,2,3,4,5,6,7,8,9,11,12,13,14,16,17,18,19
                return tick != 10 && tick != 15 && tick != 20;
            case 18:
            	// fires on tick 1,2,3,4,5,6,7,8,9,11,12,13,14,15,16,17,18,19
                return tick != 10 && tick != 20;
            case 19:
            	// fires on tick 1,2,3,4,5,6,7,8,9,11,12,13,14,15,16,17,18,19,20
                return tick != 10;
            case 20:
            	// fires on ticks 1-20
                return tick > 0 && tick <= 20;
            default:
                return true;
//            case 1:
//                return tick % 4 == 1;
//            case 2:
//                tick %= 7;
//                return tick == 1 || tick == 4;
//            case 3:
//                return tick % 3 == 1;
//            case 4:
//                tick %= 5;
//                return tick == 1 || tick == 3;
//            case 5:
//                tick %= 7;
//                return tick == 1 || tick == 3 || tick == 5;
//            case 6:
//                return (tick & 1) == 1;
//            case 7:
//                return tick == 2 || (tick & 1) == 1;
//            case 8:
//                tick %= 5;
//                return tick == 1 || tick == 2 || tick == 4;
//            case 9:
//                tick %= 6;
//                return tick != 2 && tick != 0;
//            case 10:
//                return tick % 3 != 0;
//            case 11:
//                return tick % 4 != 0;
//            case 12:
//                return tick % 5 != 0;
//            case 13:
//                return tick % 6 != 0;
//            case 14:
//                return tick % 10 != 0;
//            case 15:
//                return tick != 20;
//            default:
//                return true;
        }
    }

    public int getAmmo(ItemStack weapon) {//GunName «16/1254» OR «5»
        if (weapon == null) return 0;
        if (!weapon.hasItemMeta()) return 0;
        if (weapon.getItemMeta().getDisplayName() == null) return 0;
        String s = ChatColor.stripColor(weapon.getItemMeta().getDisplayName()), middle = "";

        if(!s.contains("«") || !s.contains("»")) return 0;
        middle = s.split("«")[1].split("»")[0];

        try {
            if(middle.contains("/")) return Integer.valueOf(middle.split("/")[0]);
            else return Integer.valueOf(middle);
        } catch (NumberFormatException exception) {
            return 1;
        }
    }

    public ItemStack setAmmo(ItemStack weapon, int amount, int totalAmmo) {
        if (weapon == null) return null;
        if (!weapon.hasItemMeta()) return weapon;
        ItemMeta im = weapon.getItemMeta();
        String s = ChatColor.stripColor(im.getDisplayName());
        String displayName = getAmmoType() != AmmoType.NONE ? "&6" + s.split("«")[0] + "&8«&f" + amount + "&8/&7" + totalAmmo + "&8»" + (s.split("»").length == 1 ? "" : s.split("»")[1]) : "&6" + s.split("«")[0] + "&8«&f" + amount + "&8»" + (s.split("»").length == 1 ? "" : s.split("»")[1]);
        im.setDisplayName(Utils.f(displayName));
        weapon.setItemMeta(im);
        return weapon;
    }

    public ItemStack updateAmmo(ItemStack weapon, Player player) {
        AmmoUpdateEvent event = new AmmoUpdateEvent(player);
        Bukkit.getPluginManager().callEvent(event);
        return this.setAmmo(weapon, this.getAmmo(weapon), event.getAmmo().getOrDefault(this.getAmmoType().getType(), 0));
    }

    /**
     * Get whether or not this weapon can multi fire.
     * <p>
     * Typically weapons that have burst or automatic might be bursting over a task 
     * period and can be fired if this again with another burst task in concurrency.
     * </p>
     * 
     * @return {@code true} if the weapon can fire again even if it's already firing.
     */
	public boolean canMultiShoot() {
		return multiShoot;
	}
}
