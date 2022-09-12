package com.j0ach1mmall3.wastedguns.listeners;

import com.j0ach1mmall3.wastedguns.Main;
import org.bukkit.event.Listener;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 6/05/2016
 */
public final class PlayerListener implements Listener {
    private final Main plugin;

    public PlayerListener(Main plugin) {
        this.plugin = plugin;
//        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

//    @EventHandler
//    public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
//        Player p = e.getPlayer();
//        ItemStack heldItem = p.getInventory().getItemInMainHand();
//        Optional<Weapon> weapon = this.plugin.getWeapon(heldItem);
//        if(!weapon.isPresent()) return;
//
//        WeaponRightClickEvent event = new WeaponRightClickEvent(p, weapon.get());
//        Bukkit.getPluginManager().callEvent(event);
//        if(event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerToggleSneak(PlayerToggleSneakEvent e) {
//        Player p = e.getPlayer();
//        PlayerCache playerCache = this.plugin.getPlayerCache(p.getUniqueId());
//        if(playerCache == null) return;
//
//        if(e.isSneaking() && (playerCache.stickyBombs != null && !playerCache.stickyBombs.isEmpty())) {
//            playerCache.stickyBombs.forEach(entity -> ((ThrowableWeapon) entity.getMetadata("StickyExplosive").get(0).value()).onExplode(entity, p));
//            playerCache.stickyBombs.clear();
//        }
//
//        Optional<Weapon> weapon = this.plugin.getWeapon(p.getInventory().getItemInMainHand());
//        if(!weapon.isPresent()) return;
//
//        WeaponSneakEvent event = new WeaponSneakEvent(p, weapon.get(), e.isSneaking());
//        Bukkit.getPluginManager().callEvent(event);
//        if(event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerDropItem(PlayerDropItemEvent e) {
//        Player p = e.getPlayer();
//        ItemStack itemStack = e.getItemDrop().getItemStack();
//        Optional<Weapon> weapon = this.plugin.getWeapon(itemStack);
//        if(!weapon.isPresent()) return;
//
//        WeaponDropEvent event = new WeaponDropEvent(p, weapon.get(), e.getItemDrop());
//        Bukkit.getPluginManager().callEvent(event);
//        if(event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerPickupItem(PlayerPickupItemEvent e) {
//        Player p = e.getPlayer();
//
//        if(e.getItem().hasMetadata("StickyExplosive") || e.getItem().hasMetadata("Explosive") || e.getItem().hasMetadata("ProximityExplosive")) {
//            e.setCancelled(true);
//            return;
//        }
//
//        Optional<Weapon> weapon = this.plugin.getWeapon(e.getItem().getItemStack());
//        if(!weapon.isPresent()) return;
//
//        WeaponPickupEvent event = new WeaponPickupEvent(p, weapon.get(), e.getItem());
//        Bukkit.getPluginManager().callEvent(event);
//        if(event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent e) {
//        Player p = e.getPlayer();
//        ItemStack previousItem = e.getMainHandItem();
//        ItemStack nextItem = e.getOffHandItem();
//        Optional<Weapon> previousWeapon = this.plugin.getWeapon(previousItem);
//        Optional<Weapon> nextWeapon = this.plugin.getWeapon(nextItem);
//        WeaponEquipEvent event = new WeaponEquipEvent(p, previousWeapon.isPresent() ? previousWeapon.get() : null, nextWeapon.isPresent() ? nextWeapon.get() : null, previousItem, nextItem);
//
//        Bukkit.getPluginManager().callEvent(event);
//        if (event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerItemHeld(PlayerItemHeldEvent e) {
//        Player p = e.getPlayer();
//        ItemStack previousItem = p.getInventory().getItem(e.getPreviousSlot());
//        ItemStack nextItem = p.getInventory().getItem(e.getNewSlot());
//        Optional<Weapon> previousWeapon = this.plugin.getWeapon(previousItem);
//        Optional<Weapon> nextWeapon = this.plugin.getWeapon(nextItem);
//        WeaponEquipEvent event = new WeaponEquipEvent(p, previousWeapon.isPresent() ? previousWeapon.get() : null, nextWeapon.isPresent() ? nextWeapon.get() : null, previousItem, nextItem);
//
//        Bukkit.getPluginManager().callEvent(event);
//        if (event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onPlayerInteract(PlayerInteractEvent e) {
//        Player p = e.getPlayer();
//        ItemStack heldItem = p.getInventory().getItemInMainHand();
//        Optional<Weapon> weapon = this.plugin.getWeapon(heldItem);
//        if(!weapon.isPresent()) return;
//        WeaponEvent event;
//        switch (e.getAction()) {
//            case LEFT_CLICK_AIR:
//            case LEFT_CLICK_BLOCK:
//                event = new WeaponLeftClickEvent(p, weapon.get());
//                break;
//            case RIGHT_CLICK_AIR:
//            case RIGHT_CLICK_BLOCK:
//                event = new WeaponRightClickEvent(p, weapon.get());
//                break;
//            default:
//                return;
//        }
//
//        Bukkit.getPluginManager().callEvent(event);
//        if(event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler(priority = EventPriority.LOWEST)
//    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
//        if(!(e.getDamager() instanceof LivingEntity)) return;
//
//        LivingEntity l = (LivingEntity) e.getDamager();
//
//        Optional<Weapon> weapon = this.plugin.getWeapon(l.getEquipment().getItemInMainHand());
//        if(!weapon.isPresent() || e.getCause() != EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;
//
//        WeaponDamageEvent event = new WeaponDamageEvent(l, weapon.get(), e.getDamage(), e.getEntity(), e.getCause());
//        Bukkit.getPluginManager().callEvent(event);
//        e.setDamage(event.getDamage());
//        if (event.isCancelled()) e.setCancelled(true);
//    }
//
//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent e) {
//        Player p = (Player) e.getWhoClicked();
//        PlayerCache playerCache = this.plugin.getPlayerCache(p.getUniqueId());
//        if(playerCache == null) return;
//        WeaponCache weaponCache = playerCache.getWeaponCache(p);
//        if(weaponCache == null) return;
//
//        if(playerCache.shootingLivingEntities != null && !playerCache.shootingLivingEntities.isEmpty())
//            e.setCancelled(true);
//
//        weaponCache.reloading = false;
//
//        if(!(e.getClickedInventory() instanceof PlayerInventory) && e.getCursor().getType() != Material.AIR) {
//            ItemStack itemStack = e.getCursor();
//            Optional<Weapon> weapon = this.plugin.getWeapon(itemStack);
//            if(weapon.isPresent() && weapon.get() instanceof RangedWeapon) {
//                RangedWeapon rangedWeapon = (RangedWeapon) weapon.get();
//                rangedWeapon.setAmmo(itemStack, rangedWeapon.getAmmo(itemStack), 0);
//            }
//        }
//    }
//
//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent e) {
//        Player p = e.getPlayer();
//        ItemStack heldItem = p.getInventory().getItemInMainHand();
//        Optional<Weapon> weapon = this.plugin.getWeapon(heldItem);
//        if(!weapon.isPresent()) return;
//
//        e.getPlayer().getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(16.0);
//    }
//
//    @EventHandler
//    public void onPlayerMove(PlayerMoveEvent e) {
//        Player p = e.getPlayer();
//        if(p.getGameMode() != GameMode.ADVENTURE) return;
//        p.getNearbyEntities(2.0, 2.0, 2.0).stream().filter(ent -> ent instanceof Item && ent.hasMetadata("ProximityExplosive")).forEach(ent -> ((ThrowableWeapon) ent.getMetadata("ProximityExplosive").get(0).value()).onExplode(ent, p));
//    }
//
//    @EventHandler
//    public void onPlayerDeath(PlayerDeathEvent e) {
//        Player p = e.getEntity();
//        Bukkit.getOnlinePlayers().forEach(player -> {
//            player.hidePlayer(p);
//            player.showPlayer(p);
//        });
//        p.removePotionEffect(PotionEffectType.SLOW);
//        p.updateInventory();
//
//        this.plugin.getReloadingLivingEntities().remove(p);
//
//        Player killer = p.getKiller();
//        if(killer == null) return;
//
//        ItemStack heldItem = killer.getInventory().getItemInMainHand();
//        Optional<Weapon> weapon = this.plugin.getWeapon(heldItem);
//        if(!weapon.isPresent()) return;
//
//        weapon.get().getDeathMessages().entrySet().stream().filter(d -> d.getKey() == p.getLastDamageCause().getCause()).forEach(d -> e.setDeathMessage(Placeholders.parse(d.getValue(), p).replace("%victim%", p.getName()).replace("%killer%", killer.getName())));
//    }
//
//    @EventHandler
//    public void onPlayerQuit(PlayerQuitEvent e) {
//        Player p = e.getPlayer();
//        this.plugin.getReloadingLivingEntities().remove(p);
//        if(this.plugin.getShootingLivingEntities().containsKey(p)) this.plugin.getShootingLivingEntities().get(p).forEach(Bukkit.getScheduler()::cancelTask);
//        this.plugin.getShootingLivingEntities().remove(p);
//        this.plugin.getHomingTargets().remove(p);
//        this.plugin.getWeapons().forEach(w -> this.plugin.getCooldownLivingEntities().remove(p.getName() + '/' + w.getIdentifier()));
//        this.plugin.getBurstShots().remove(p);
//        this.plugin.getBurstTicks().remove(p);
//        if(this.plugin.getStickyBombs().containsKey(p)) this.plugin.getStickyBombs().get(p).forEach(Entity::remove);
//        this.plugin.getStickyBombs().remove(p);
//        p.setWalkSpeed(0.2F);
//    }
//
//    @EventHandler
//    public void onPlayerKick(PlayerKickEvent e) {
//        Player p = e.getPlayer();
//        this.plugin.getReloadingLivingEntities().remove(p);
//        if(this.plugin.getShootingLivingEntities().containsKey(p)) this.plugin.getShootingLivingEntities().get(p).forEach(Bukkit.getScheduler()::cancelTask);
//        this.plugin.getShootingLivingEntities().remove(p);
//        this.plugin.getHomingTargets().remove(p);
//        this.plugin.getWeapons().forEach(w -> this.plugin.getCooldownLivingEntities().remove(p.getName() + '/' + w.getIdentifier()));
//        this.plugin.getBurstShots().remove(p);
//        this.plugin.getBurstTicks().remove(p);
//        if(this.plugin.getStickyBombs().containsKey(p)) this.plugin.getStickyBombs().get(p).forEach(Entity::remove);
//        this.plugin.getStickyBombs().remove(p);
//        p.setWalkSpeed(0.2F);
//    }
//
//    @EventHandler
//    public void onAmmoUpdate(AmmoUpdateEvent e) {
//        Map<String, Integer> ammo = e.getAmmo();
//        Arrays.asList(e.getPlayer().getInventory().getContents()).stream().filter(i -> i != null).forEach(i -> {
//            Optional<Weapon> weapon = this.plugin.getWeapon(i);
//            if(weapon.isPresent() && weapon.get() instanceof RangedWeapon) {
//                RangedWeapon rangedWeapon = (RangedWeapon) weapon.get();
//                rangedWeapon.setAmmo(i, rangedWeapon.getAmmo(i), ammo.getOrDefault(rangedWeapon.getAmmoType(), 0));
//            }
//        });
//    }
}
