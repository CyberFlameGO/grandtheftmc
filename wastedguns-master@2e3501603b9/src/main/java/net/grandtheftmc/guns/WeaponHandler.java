package net.grandtheftmc.guns;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.lang.math.RandomUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.google.common.collect.Maps;
import com.j0ach1mmall3.wastedguns.api.events.WeaponDamageEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponDropEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponEquipEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponLeftClickEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponPickupEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedguns.api.events.WeaponSneakEvent;
import com.j0ach1mmall3.wastedvehicles.Main;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.servers.ServerType;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.nbt.CoreNbt;
import net.grandtheftmc.core.util.nbt.NBTUtil1_12_2;
import net.grandtheftmc.guns.cache.PlayerCache;
import net.grandtheftmc.guns.weapon.MeleeWeapon;
import net.grandtheftmc.guns.weapon.ThrowableWeapon;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.attribute.WeaponExplosive;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;

/**
 * Created by Luke Bingham on 19/07/2017.
 */
public class WeaponHandler implements Listener {
    private final HashMap<UUID, List<WeaponCooldown>> cooldownMap;
    private final HashMap<UUID, Long> fastplace;

    private final GTMGuns plugin;
    private final WeaponManager weaponManager;

    public WeaponHandler(GTMGuns plugin, WeaponManager weaponManager) {
        this.plugin = plugin;
        this.weaponManager = weaponManager;
        this.cooldownMap = Maps.newHashMap();
        this.fastplace = Maps.newHashMap();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);


        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> {
            for (World world : Bukkit.getWorlds()) {
                for(Entity entity : world.getEntities()){
                    if(entity.hasMetadata("Explosive") && entity.isOnGround()){
                        ((WeaponExplosive) entity.getMetadata("Explosive").get(0).value()).onLand(entity);
                    }
//                    else if(entity.hasMetadata("Airstrike") && entity.isOnGround()) {
//                        ((AirstrikeWeapon) entity.getMetadata("Airstrike").get(0).value()).onLand(entity);
//                    }
                }
            }
        }, 5, 5);
        
//        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
//            for(UUID uuid : weaponManager.playerCacheMap.keySet()) {
//                Player player = Bukkit.getPlayer(uuid);
//                if(player == null) {
//                    weaponManager.playerCacheMap.remove(uuid);
//                    continue;
//                }
//
//                PlayerCache playerCache = weaponManager.getPlayerCache(uuid);
//                if(playerCache == null) continue;
//                if(!playerCache.shooting) continue;
//
//                ItemStack heldItem = player.getEquipment().getItemInMainHand();
//                if(heldItem == null || heldItem.getType() == Material.AIR) {
//                    playerCache.burst = 0;
//                    playerCache.shooting = false;
//                    continue;
//                }
//                WeaponCache weaponCache = weaponManager.getPlayerWeaponCache(player, heldItem.getDurability());
//                if(weaponCache == null) {
//                    playerCache.burst = 0;
//                    playerCache.shooting = false;
//                    continue;
//                }
//
//                Weapon<?> weapon = weaponManager.getPlayerWeapon(player, heldItem.getDurability());
//                if(weapon == null) continue;
//                if(!(weapon instanceof RangedWeapon)) continue;
//                RangedWeapon<?> rangedWeapon = (RangedWeapon<?>) weapon;
//
//                if(rangedWeapon instanceof WeaponRPM) {
//                    if (playerCache.burst >= (((WeaponRPM) rangedWeapon).getRpm() > 0 ? 5 : 1)) {
//                        playerCache.burst = 0;
//                        playerCache.shooting = false;
//                        continue;
//                    }
//                }
//
//                playerCache.burst += 1;
//
//                Location origin = player.getEyeLocation();
//                Vector direction = origin.getDirection();
//
//                rangedWeapon.shoot(player, origin, direction, playerCache, weaponCache);
//
//                if (rangedWeapon.getRecoil() != 0 && !player.isSneaking())
//                    player.setVelocity(player.getVelocity().add(direction.setY(0).multiply(-rangedWeapon.getRecoil())));
//            }
//        }, 1, 1);
    }

    @EventHandler
    protected final void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = event.getItem();
        if(player==null || heldItem==null)
            return;
        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());
        Weapon<?> weapon = cache.getOrAddWeapon(heldItem);
        if(weapon == null) {
            event.getPlayer().setWalkSpeed(0.2f);
            if(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
                event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return;
        }
        if(heldItem.getAmount()>1 && !(weapon instanceof ThrowableWeapon)) {
            player.sendMessage(Lang.AMMO.f("&7You cannot do this action with a stacked " + weapon.getName()));
            return;
        }

        if(GTMGuns.DEBUG)
            event.getPlayer().sendMessage(weapon.getName());

        WeaponEvent weaponEvent = null;
        switch (event.getAction()) {
            case LEFT_CLICK_BLOCK:
            case LEFT_CLICK_AIR:
                weaponEvent = new WeaponLeftClickEvent(event.getPlayer(), weapon);
                break;

            case RIGHT_CLICK_BLOCK:
            case RIGHT_CLICK_AIR:
                // Fast-Place check.
                long lastClicked = this.fastplace.getOrDefault(player.getUniqueId(), -1L);
                if (lastClicked == -1L)
                    this.fastplace.put(player.getUniqueId(), System.currentTimeMillis() + 150);
                else {
                    if (System.currentTimeMillis() < lastClicked) {
//                        ServerUtil.debug("Weapon right click DENIED. (" + (System.currentTimeMillis() - lastClicked) + "ms)");
                        break;
                    }
                    this.fastplace.put(player.getUniqueId(), System.currentTimeMillis() + 150);
                }

                weaponEvent = new WeaponRightClickEvent(event.getPlayer(), weapon);
                break;

            default: return;
        }

        if (weaponEvent == null) return;

        Bukkit.getPluginManager().callEvent(weaponEvent);
        if(weaponEvent.isCancelled()) event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack heldItem = player.getInventory().getItemInMainHand();

        PlayerCache cache = weaponManager.getPlayerCache(player.getUniqueId());
        Weapon<?> weapon = cache.getOrAddWeapon(heldItem);
        if(weapon == null) {
            event.getPlayer().setWalkSpeed(0.2f);
            if(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
                event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return;
        }
        if(heldItem.getAmount()>1) {
            player.sendMessage(Lang.AMMO.f("&7You cannot do this action with a stacked weapon!"));
            return;
        }
        if(GTMGuns.DEBUG)
            event.getPlayer().sendMessage(weapon.getName());

        WeaponRightClickEvent weaponRightClickEvent = new WeaponRightClickEvent(event.getPlayer(), weapon);
        Bukkit.getPluginManager().callEvent(weaponRightClickEvent);

        if(weaponRightClickEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
    	
    	// if the damager of the event is living
        if (event.getDamager() != null && event.getDamager() instanceof LivingEntity) {
        	
        	// ignore targets that aren't living
            if(!(event.getEntity() instanceof LivingEntity)){ 
            	return;
            }
            
            // grab event variables
            LivingEntity damager = (LivingEntity) event.getDamager();
            LivingEntity target = (LivingEntity) event.getEntity();
            DamageCause damageCause = event.getCause();
        	
        	// get damage data if exists, queued up by ranged weapons/explosions etc.
        	EntityDamageByEntityEvent edbee = DamageDataHandler.getInstance().getData(target.getUniqueId()).orElse(null);
        	if (edbee != null){
        		event.setDamage(edbee.getDamage());
        		
        		// remove the data from the handler
        		DamageDataHandler.getInstance().removeData(target.getUniqueId());
        		return;
        	}
        	else{
        		
                // ONLY HANDLE MELEE ATTACKS
                if (damageCause == DamageCause.ENTITY_ATTACK){
                	
                    // grab the weapon itemstack from their hand
                    ItemStack weaponItemStack = damager.getEquipment().getItemInMainHand();
                    
                    // get the weapon
                    Weapon<?> heldWeapon = weaponManager.getWeaponByItem(weaponItemStack);
                    
                    // if no held weapon
                    if(heldWeapon == null) {
                        if(damager instanceof Player) {
                            Player p = (Player) damager;
                            p.setWalkSpeed(0.2f);
                            if (p.hasPotionEffect(PotionEffectType.SLOW))
                                p.removePotionEffect(PotionEffectType.SLOW);

                            if (p.getInventory().getItemInMainHand() != null && p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_SWORD) {
                                event.setDamage(1);
                            }
                        }
                        return;
                    }

                    // if melee weapon set melee damage
                    if (heldWeapon instanceof MeleeWeapon){
                        event.setDamage(((MeleeWeapon) heldWeapon).getMeleeDamage());
                    }

                    // this case only happens when melee hitting with a ranged weapon
                    if (heldWeapon instanceof RangedWeapon) {
                        event.setDamage(((RangedWeapon) heldWeapon).getMeleeDamage());
                    }

                    // this case only happens when melee hitting players with throwable 
                    if (heldWeapon instanceof ThrowableWeapon){
                        event.setDamage(((ThrowableWeapon) heldWeapon).getMeleeDamage());
                    }

                    // call a weapon damage event to see if we can carry out this damage
                    WeaponDamageEvent weaponDamageEvent = new WeaponDamageEvent(damager, heldWeapon, weaponItemStack, event.getDamage(), target, event.getCause());
                    Bukkit.getPluginManager().callEvent(weaponDamageEvent);
                    
                    // if damage was cancelled
                    if (weaponDamageEvent.isCancelled()){
                    	event.setCancelled(true);
                    }
                    else{
                    	// set the damage of the weapon damage event
                    	event.setDamage(weaponDamageEvent.getDamage());
                    }
                }
        	}
        }
    }

    @EventHandler
    protected final void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        if(event.getPlayer() == null) return;

        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());

        if(event.isSneaking() && !cache.stickyBombs.isEmpty()) {
            cache.stickyBombs.forEach(bomb -> {
                if(bomb.hasMetadata("StickyExplosive")) {
                    ((WeaponExplosive) bomb.getMetadata("StickyExplosive").get(0).value()).onExplode(bomb, (Player) bomb.getMetadata("Shooter").get(0).value());
                }
                bomb.remove();
            });
            cache.stickyBombs.clear();
        }

        Weapon<?> weapon = cache.getOrAddWeapon(event.getPlayer().getEquipment().getItemInMainHand());
        if(weapon == null) {
            event.getPlayer().setWalkSpeed(0.2f);
            if(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
                event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return;
        }

        WeaponSneakEvent weaponSneakEvent = new WeaponSneakEvent(event.getPlayer(), weapon, event.isSneaking());
        Bukkit.getPluginManager().callEvent(weaponSneakEvent);

        if(weaponSneakEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerDropItem(PlayerDropItemEvent event) {
        if(event.getItemDrop() == null) return;

        if (Core.getSettings().getType() != ServerType.VICE) {
            weaponManager.updateOldWeapon(event.getPlayer(), event.getItemDrop().getItemStack());
        }

        Weapon<?> weapon = weaponManager.getWeaponByItem(event.getItemDrop().getItemStack());
        if(weapon == null) return;

        WeaponDropEvent weaponDropEvent = new WeaponDropEvent(event.getPlayer(), weapon, event.getItemDrop());
        Bukkit.getPluginManager().callEvent(weaponDropEvent);

        if(weaponDropEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerItemPickup(PlayerPickupItemEvent event) {
        Item item = event.getItem();
        if(item == null || item.getItemStack() == null) return;

        CoreNbt nbt = new NBTUtil1_12_2(item.getItemStack());//testing
        if(nbt.hasNBTTag("weapon_type")) {
            Core.log(nbt.getNBTTag("weapon_type").toString());
        }

        if (Core.getSettings().getType() != ServerType.VICE) {
            weaponManager.updateOldWeapon(event.getPlayer(), item.getItemStack());
        }

        if(item.hasMetadata("StickyExplosive") ||
                item.hasMetadata("Explosive") ||
                item.hasMetadata("ProximityExplosive")) {
            event.setCancelled(true);
            return;
        }

        Weapon<?> weapon = weaponManager.getWeaponByItem(item.getItemStack());
        if(weapon == null) return;

        WeaponPickupEvent weaponPickupEvent = new WeaponPickupEvent(event.getPlayer(), weapon, item);
        Bukkit.getPluginManager().callEvent(weaponPickupEvent);

        if(weaponPickupEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());

        Weapon<?> current = cache.getOrAddWeapon(event.getMainHandItem());
        if(current != null && current.getWeaponState() == WeaponState.SHOOTING) {
            event.setCancelled(true);
            return;
        }

        Weapon<?> next = cache.getOrAddWeapon(event.getOffHandItem());
        if(next == null) {
            event.getPlayer().setWalkSpeed(0.2f);
            if(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
                event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return;
        }

        WeaponEquipEvent weaponEquipEvent = new WeaponEquipEvent(event.getPlayer(), next, event.getMainHandItem(), event.getOffHandItem());
        Bukkit.getPluginManager().callEvent(weaponEquipEvent);

        if(weaponEquipEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerItemHeld(PlayerItemHeldEvent event) {
        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());

        ItemStack next = event.getPlayer().getInventory().getItem(event.getNewSlot()),
                previous = event.getPlayer().getInventory().getItem(event.getPreviousSlot());
        Weapon<?> currentWeapon = cache.getOrAddWeapon(previous);
        if(currentWeapon != null && currentWeapon.getWeaponState() == WeaponState.SHOOTING) {
            event.setCancelled(true);
            return;
        }

        Weapon<?> weapon = cache.getOrAddWeapon(next);
        if(weapon == null) {
            event.getPlayer().setWalkSpeed(0.2f);
            if(event.getPlayer().hasPotionEffect(PotionEffectType.SLOW))
                event.getPlayer().removePotionEffect(PotionEffectType.SLOW);
            return;
        }

        WeaponEquipEvent weaponEquipEvent = new WeaponEquipEvent(event.getPlayer(), weapon, previous, next);
        Bukkit.getPluginManager().callEvent(weaponEquipEvent);

        if(weaponEquipEvent.isCancelled())
            event.setCancelled(true);
    }

    @EventHandler
    protected final void onPlayerMove(PlayerMoveEvent event) {
        if(event.getPlayer().getGameMode() != GameMode.ADVENTURE && event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
        if(event.getFrom().getWorld().getName().equalsIgnoreCase("spawn")) return;

        //This event will fire even when moving your mouse.
        //We only want this to continue if a player is stood on a different block.
        if(event.getFrom().getBlockX() == event.getTo().getBlockX() &&
                event.getFrom().getBlockZ() == event.getTo().getBlockZ() &&
                event.getFrom().getBlockY() == event.getTo().getBlockY()) return;

        event.getPlayer().getNearbyEntities(2.0, 2.0, 2.0).stream()
                .filter(entity -> entity instanceof Item && entity.hasMetadata("ProximityExplosive"))
                .forEach(entity -> ((ThrowableWeapon) entity.getMetadata("ProximityExplosive").get(0).value()).onExplode(entity, event.getPlayer()));
    }

    @EventHandler
    protected final void onPlayerDeath(PlayerDeathEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            player.hidePlayer(event.getEntity());
            player.showPlayer(event.getEntity());
        });

        event.getEntity().removePotionEffect(PotionEffectType.SLOW);
        event.getEntity().updateInventory();
        
        Player killer = event.getEntity().getKiller();
        if(killer != null) {
            ItemStack heldItem = killer.getInventory().getItemInMainHand();
            
            PlayerCache cache = weaponManager.getPlayerCache(killer.getUniqueId());
            cache.stickyBombs.forEach(Entity::remove);
            cache.stickyBombs.clear();
            
            Weapon<?> weapon = cache.getOrAddWeapon(heldItem);
            if (weapon == null) return;
            
            if(GTMGuns.KILL_COUNT_SYSTEM) {
                ItemStack newItem = Weapon.setKills(heldItem, Weapon.getKills(heldItem) + 1);
                Weapon.updateLore(newItem);
                
                killer.getInventory().setItemInMainHand(newItem);
            }
            
            //TODO Display death message
        }

        PlayerCache cache = weaponManager.getPlayerCache(event.getEntity().getUniqueId());
        cache.getPlayerWeapons().forEach(w -> {
            w.setWeaponCooldown(null);
            w.setWeaponState(WeaponState.IDLE);
        });

        cache.getPlayerWeapons().clear();
    }

    @EventHandler
    protected final void onPlayerJoin(PlayerJoinEvent event) {
        if(event.getPlayer() == null) return;
        event.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0);

        if (Core.getSettings().getType() != ServerType.VICE) {
            for (ItemStack itemStack : event.getPlayer().getInventory().getContents())
                weaponManager.updateOldWeapon(event.getPlayer(), itemStack);
        }

        event.getPlayer().updateInventory();
    }

    /**
     * Remove from PlayerCache when player quits.
     */
    @EventHandler
    protected final void onPlayerQuit(PlayerQuitEvent event) {
        if(event.getPlayer() == null) return;
        event.getPlayer().setWalkSpeed(0.2f);
        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());

        this.fastplace.remove(event.getPlayer().getUniqueId());

        cache.getPlayerWeapons().clear();

        if(cache.stickyBombs != null && !cache.stickyBombs.isEmpty()) {
            cache.stickyBombs.forEach(Entity::remove);
            cache.stickyBombs.clear();
        }

        //TODO Remove bullets

        weaponManager.playerCacheMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler
    protected final void onPlayerKick(PlayerKickEvent event) {
        if(event.getPlayer() == null) return;
        event.getPlayer().setWalkSpeed(0.2f);
        PlayerCache cache = weaponManager.getPlayerCache(event.getPlayer().getUniqueId());

        cache.getPlayerWeapons().clear();

        if(cache.stickyBombs != null && !cache.stickyBombs.isEmpty()) {
            cache.stickyBombs.forEach(Entity::remove);
            cache.stickyBombs.clear();
        }

        //TODO Remove bullets

        weaponManager.playerCacheMap.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected final void onWeaponRightClick(WeaponRightClickEvent event) {
        if(event.getLivingEntity() == null || event.getWeapon() == null) return;
        LivingEntity entity = event.getLivingEntity();
        Weapon<?> weapon = event.getWeapon();

        event.setCancelled(true);

        if(weapon.getWeaponCooldown() != null && !weapon.getWeaponCooldown().hasElapsed()) return;

        if(entity instanceof Player) {
            if(((Player) entity).getGameMode() == GameMode.SPECTATOR) {
                event.setCancelled(true);
                return;
            }

            //Check for Flamethrower ammo (Fuel)
            if(weapon.getName().equalsIgnoreCase("flamethrower")) {

                ItemStack itemStack = Main.getPlugin(Main.class).getBabies().getJetpackFuelItem().clone();
                boolean found = false;
                for (ItemStack item : ((Player) entity).getInventory().getContents()) {
                    if(item == null) continue;
                    if(item.getType() != itemStack.getType()) continue;
                    if(!item.hasItemMeta() || !itemStack.hasItemMeta()) continue;
                    if(!item.getItemMeta().getDisplayName().equals(itemStack.getItemMeta().getDisplayName())) continue;

                    found = true;
                    break;
                }

                if(!found) {
                    entity.sendMessage(Lang.AMMO.f("&7The &c&lFlamethrower &7requires (jetpack) fuel to use!"));
                    return;
                }
            }
            weapon.onRightClick((Player) entity);

            if(weapon.getDelay() > 0)
                weapon.setWeaponCooldown(new WeaponCooldown(weapon, weapon.getDelay() * 50));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected final void onWeaponLeftClick(WeaponLeftClickEvent event) {
        event.setCancelled(true);
        if(event.getWeapon().getWeaponState() == WeaponState.SHOOTING) return;
        event.getWeapon().onLeftClick((Player) event.getLivingEntity());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected final void onWeaponSneak(WeaponSneakEvent event) {
        if(!(event.getLivingEntity() instanceof Player)) return;
        event.getWeapon().onSneak((Player) event.getLivingEntity(), event.isSneaking());
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    protected final void onWeaponDamage(WeaponDamageEvent event) {
    	
        // grab event variables
        LivingEntity shooter = event.getLivingEntity();
        Weapon<?> weapon = event.getWeapon();
        LivingEntity target = (LivingEntity) event.getEntity();
        DamageCause cause = event.getCause();
        
        if(!(weapon instanceof RangedWeapon) && (weapon.getWeaponCooldown() != null && !weapon.getWeaponCooldown().hasElapsed())) {
        	event.setCancelled(true);
            return;
        }
        
        // initial modifier for damage is 1x
        double initialModifier = 1.0;
        
        // if star system
        if (GTMGuns.STAR_SYSTEM){
        	ItemStack is = event.getWeaponItemStack();
        	
        	if (is != null){
        		
        		// get the number of stars on this itemstack
        		int numStars = Weapon.getRarity(is);
        		if (numStars > 0){
        			switch(numStars){
        				case 1:
        					initialModifier = 1.0;
        					break;
        				case 2:
        					initialModifier = 1.1;
        					break;
        				case 3:
        					initialModifier = 1.2;
        					break;
        				default:
        					initialModifier = 1.0;
        					break;
        			}
        		}
        	}
        }

        if(!(weapon instanceof RangedWeapon)){
            weapon.setWeaponCooldown(new WeaponCooldown(weapon, weapon.getDelay() * 50));
        }
        
        // Note: LauncherWeapons and grenades call this with a specific damage cause
        // instead of relying of last damage, which allows us to hook into the actual damage caused
        if (cause == DamageCause.DRAGON_BREATH){
            
        	if(weapon instanceof RangedWeapon){
                event.setDamage(((RangedWeapon) weapon).getDamage() * initialModifier);
            }

            else if(weapon instanceof MeleeWeapon){
                event.setDamage(((MeleeWeapon) weapon).getMeleeDamage() * initialModifier);
            }

            else if(weapon instanceof ThrowableWeapon){
                event.setDamage(((ThrowableWeapon) weapon).getDamage() * initialModifier);
            }
        }
        // else melee damage
        else{
        	
        	if(weapon instanceof RangedWeapon){
                event.setDamage(((RangedWeapon) weapon).getMeleeDamage() * initialModifier);
            }

            else if(weapon instanceof MeleeWeapon){
                event.setDamage(((MeleeWeapon) weapon).getMeleeDamage() * initialModifier);
            }

            else if(weapon instanceof ThrowableWeapon){
                event.setDamage(((ThrowableWeapon) weapon).getMeleeDamage() * initialModifier);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    protected final void onWeaponEquip(WeaponEquipEvent event) {
        Weapon<?> weapon = weaponManager.getPlayerCache(event.getLivingEntity().getUniqueId()).getOrAddWeapon(event.getPreviousItem());
        if(weapon != null) {
            event.getLivingEntity().removePotionEffect(PotionEffectType.SLOW);
            if(event.getLivingEntity() instanceof Player) {
                if(weapon instanceof RangedWeapon)
                    ((RangedWeapon) weapon).updateAmmo(event.getPreviousItem(), (Player) event.getLivingEntity());
                ((Player) event.getLivingEntity()).setWalkSpeed(0.2f);
                weapon.onSneak((Player) event.getLivingEntity(), false);
            }
        }

        if(event.getWeapon() != null) {
            if(event.getWeapon() instanceof RangedWeapon)
                ((RangedWeapon) event.getWeapon()).updateAmmo(event.getNewItem(), (Player) event.getLivingEntity());
            ((Player) event.getLivingEntity()).setWalkSpeed((float) event.getWeapon().getWalkSpeed());
        }
    }

//    @EventHandler
//    protected final void onAmmoUpdate(AmmoUpdateEvent event) {
////        Arrays.stream(event.getPlayer().getInventory().getContents()).filter(item -> item != null && item.getType() != Material.AIR).forEach(item -> {
////            Optional<Weapon> weaponOptional = WeaponHandler.this.weaponManager.getPlayerWeapon(event.getPlayer(), item.getType(), item.getDurability());
////            if(weaponOptional.isPresent() && weaponOptional.get() instanceof RangedWeapon) {
////                RangedWeapon rangedWeapon = (RangedWeapon) weaponOptional.get();
////                rangedWeapon.setAmmo(item, rangedWeapon.getAmmo(item), event.getAmmo().getOrDefault(rangedWeapon.getAmmoType(), 0));
////            }
////        });
//    }
//
    @EventHandler
    protected final void onProjectileHit(ProjectileHitEvent event) {
        if(event.getEntity() == null) return;
        if(event.getEntity().getShooter() == null) return;
        if(!(event.getEntity().getShooter() instanceof LivingEntity)) return;

        if(event.getEntity().hasMetadata("Rocket")) ((WeaponExplosive) event.getEntity().getMetadata("Rocket").get(0).value()).onExplode(event.getEntity(), (Player) event.getEntity().getShooter());
        if(event.getEntity().hasMetadata("Explosive")) ((WeaponExplosive) event.getEntity().getMetadata("Explosive").get(0).value()).onExplode(event.getEntity(), (Player) event.getEntity().getShooter());
    }

//    @EventHandler
//    protected final void onEntityDamage(EntityDamageEvent event) {
//        if(event.getEntity() == null) return;
//        if(event.getEntity().hasMetadata("AirstrikeBomb") && event.getEntity() instanceof ArmorStand) {
//            event.setDamage(0);
////            ((AirstrikeWeapon) event.getEntity().getMetadata("AirstrikeBomb").get(0).value()).onExplode(event.getEntity(), (LivingEntity) event.getEntity().getMetadata("Shooter").get(0).value());
//            event.getEntity().remove();
//        }
//    }

    @EventHandler
    protected final void onBlockExplode(BlockExplodeEvent event) {
        if (Core.getSettings().getType() == ServerType.GTM) {
            event.setCancelled(true);
            return;
        }

        for(Block block : event.blockList()) {
            if(!block.getType().isSolid()) continue;
            if(RandomUtils.nextInt(10) != 1) continue;//Only spawn a few visuals

            Location l = block.getLocation();
            l.setPitch(-Utils.getRandom().nextInt(180));
            l.setYaw(Utils.getRandom().nextInt(360));
            FallingBlock fb = l.getWorld().spawnFallingBlock(l, block.getType(), block.getData());
            fb.setMetadata("EXPLOSION", new FixedMetadataValue(this.plugin, true));
            fb.setDropItem(false);
            fb.setHurtEntities(false);
            float x = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
            float y = -2.0F + (float)(Math.random() * 4.0D + 1.0D);
            float z = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
            fb.setVelocity(new Vector(x, y, z));
            this.weaponManager.entityQueue.add(fb);
//            block.getState().update();
        }
//        event.blockList().clear();
    }

    //@EventHandler Disabled because it doesn't work. Makes the potency of the explosions very small.
    protected final void onEntityExplode(EntityExplodeEvent event) {
        if (Core.getSettings().getType() == ServerType.GTM) {
//            event.setCancelled(true);
            return;
        }

        for (Block block : event.blockList()) {
            if(!block.getType().isSolid()) continue;
            if(RandomUtils.nextInt(10) != 1) continue;//Only spawn a few visuals

            Location l = block.getLocation();
            l.setPitch(-Utils.getRandom().nextInt(180));
            l.setYaw(Utils.getRandom().nextInt(360));
            FallingBlock fb = l.getWorld().spawnFallingBlock(l, block.getType(), block.getData());
            fb.setMetadata("EXPLOSION", new FixedMetadataValue(this.plugin, true));
            fb.setDropItem(false);
            fb.setHurtEntities(false);
            float x = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
            float y = -2.0F + (float)(Math.random() * 4.0D + 1.0D);
            float z = -1.0F + (float)(Math.random() * 2.0D + 1.0D);
            fb.setVelocity(new Vector(x, y, z));
            this.weaponManager.entityQueue.add(fb);
//            block.getState().update();
        }
//        event.blockList().clear();
    }

    @EventHandler
    protected final void onEntityBlockChange(EntityChangeBlockEvent event) {
        if(event.getEntity() == null) return;
        if(!(event.getEntity() instanceof FallingBlock)) return;
        if(!event.getEntity().hasMetadata("EXPLOSION"))
            return;

        if(this.weaponManager == null || this.weaponManager.entityQueue == null)
            return;

        this.weaponManager.entityQueue.remove(event.getEntity());
        event.setCancelled(true);
        event.getBlock().getWorld().playEffect(event.getBlock().getLocation(), Effect.STEP_SOUND, event.getTo());
    }

//    @EventHandler
//    protected final void onWeaponStack(ItemStackEvent event) {
//        if(event.getItemStack() == null) return;
//        if(event.getItemStack().getType() != Material.DIAMOND_SWORD) return;
//
//        if(event.getItemStack().getDurability() <= 751 || event.getItemStack().getDurability() >= 800) {
//            event.setCancelled(true);
//            event.setClickOnly(true);
//        }
//    }

    @EventHandler(ignoreCancelled = true)
    protected final void onInventoryOpen(InventoryOpenEvent event) {
        if(event.getInventory() == null) return;
        if(event.getPlayer() == null) return;

        if (Core.getSettings().getType() == ServerType.VICE) return;

        String name = ChatColor.stripColor(event.getInventory().getName()).toLowerCase();
        if(name.contains("kits")
                || name.contains("stats")
                || name.contains("cheat codes")
                || name.contains("ranks")
                || name.contains("choose the villager's job")
                || name.contains("token shop")
                || name.contains("crate rewards")
                || name.contains("christmas shop")
                || name.contains("preferences")
                || name.contains("contacts")
                || name.contains("vehicle shop")
                || name.contains("choose category")
                || name.contains("sell drugs")
                || name.contains("buy machines")
                || name.contains("choose category")
                || name.contains("buy supplies")
                || name.contains("choose gun ategory")
                || name.contains("choose gun category")
                || name.contains("buy blocks")
                || name.contains("purchase throwable")
                || name.contains("purchase melee")
                || name.contains("purchase pistol")
                || name.contains("purchase lmg")
                || name.equals("Chest")
                || name.equals("")
                || name.contains("purchase smg")
                || name.contains("purchase shotgun")
                || name.contains("purchase assault")
                || name.contains("purchase launcher")
                || name.contains("purchase sniper")
                || name.contains("purchase special")
                || name.contains("taxi")
                || name.contains("machine mechanic")
                || name.contains("chamber")
                || name.contains("distillery")
                || name.contains("processor")
                || name.contains("condenser")
                || name.contains("producer")
                || name.contains("sugar box")){
            return;
        }

        for(ItemStack item : event.getInventory().getContents())
            weaponManager.updateOldWeapon((Player) event.getPlayer(), item);

        ((Player) event.getPlayer()).updateInventory();
    }

    @EventHandler
    protected final void onPlayerExplode(EntityDamageByEntityEvent event) {
        if (Core.getSettings().getType() == ServerType.GTM) {
            if (event.getDamager() != null && event.getDamager() instanceof TNTPrimed) {
                TNTPrimed tntPrimed = (TNTPrimed) event.getDamager();
                
                if (tntPrimed.hasMetadata("entity_damage")){
                	List<MetadataValue> md = tntPrimed.getMetadata("entity_damage");
                	if (md != null && !md.isEmpty()){
                		boolean damage = md.get(0).asBoolean();

                		if (damage){
                        	//event.setDamage((event.getDamage() / 4) * 3);
                		}
                		else{
                			// ignore damage
                			//event.setDamage(0);
                			event.setCancelled(true);
                		}
                	}
                }
            }
        }
    }

    @EventHandler
    protected final void onEntityDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FIRE_TICK) return;
        if (Core.getSettings().getType() != ServerType.GTM) return;
        event.setCancelled(ThreadLocalRandom.current().nextBoolean());
    }
}
