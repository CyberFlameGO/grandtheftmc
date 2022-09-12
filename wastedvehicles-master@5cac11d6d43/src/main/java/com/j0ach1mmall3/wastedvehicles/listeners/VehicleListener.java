package com.j0ach1mmall3.wastedvehicles.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Creature;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spigotmc.event.entity.EntityDismountEvent;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.j0ach1mmall3.jlib.integration.protocollib.ProtocolLibHook;
import com.j0ach1mmall3.jlib.inventory.JLibItem;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedguns.api.events.explosives.ExplosionDamageEntityEvent;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.SpeedBoost;
import com.j0ach1mmall3.wastedvehicles.api.VehicleType;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleDamageEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleEnterEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleImpactEntityEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleImpactVehicleEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleLeaveEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleShootEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.VehicleSpeedBoostEvent;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.Submarine;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import com.j0ach1mmall3.wastedvehicles.util.MiscUtil;
import com.j0ach1mmall3.wastedvehicles.util.SteerDirection;
import com.j0ach1mmall3.wastedvehicles.util.VehicleUtils;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.anticheat.event.MovementCheatEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;
import net.grandtheftmc.guns.weapon.ranged.RangedWeapon;

public final class VehicleListener implements Listener {
    private final MiscUtil miscUtil = MiscUtil.getMiscUtil();
    private final Main plugin;

    public VehicleListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        new ProtocolLibHook().addPacketAdapter(new PacketAdapter(plugin, PacketType.Play.Client.STEER_VEHICLE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player p = event.getPlayer();
                if (p.isInsideVehicle()) {
                    Entity e = p.getVehicle();
                    if (e.hasMetadata("WastedVehiclePassenger")) return;
                    if (e.hasMetadata("WastedVehicle")) {
                        ArmorStand armorStand = (ArmorStand) e;
                        SteerDirection steerDirection = new SteerDirection(event.getPacket());
                        WastedVehicle wastedVehicle = (WastedVehicle) armorStand.getMetadata("WastedVehicle").get(0).value();
                        if (Objects.equals(armorStand.getWorld().getName(), "spawn")) {
                            wastedVehicle.onDestroy(armorStand);
                            return;
                        }
                        Location l = armorStand.getLocation();
                        l.setX(l.getX() + wastedVehicle.getVehicleProperties().getLaunchLocationMultiplier() * l.getDirection().getX());
                        l.setY(l.getY() + wastedVehicle.getVehicleProperties().getLaunchLocationYOffset());
                        l.setZ(l.getZ() + wastedVehicle.getVehicleProperties().getLaunchLocationMultiplier() * l.getDirection().getZ());
                        Vector v = wastedVehicle.getWeaponDirection(armorStand, p);
                        if (wastedVehicle.getVehicleProperties().getVehicleType() == VehicleType.PLANE) {
                            if (Objects.equals(VehicleListener.this.miscUtil.getCardinalDirection(l), "East")) {
                                l.setX(l.getX() - 3);
                            } else if (Objects.equals(VehicleListener.this.miscUtil.getCardinalDirection(l), "North")) {
                                l.setZ(l.getZ() - 3);
                            } else if (Objects.equals(VehicleListener.this.miscUtil.getCardinalDirection(l), "West")) {
                                l.setX(l.getX() + 3);
                            } else if (Objects.equals(VehicleListener.this.miscUtil.getCardinalDirection(l), "South")) {
                                l.setZ(l.getZ() + 3);
                            }
                            if (l.getPitch() >= 30) {
                                l.setPitch(l.getPitch() + 15);
                            } else if (l.getPitch() >= -30) {
                                l.setPitch(l.getPitch() - 15);
                            }
                            l.setY(l.getY() - 3);
                        }
                        if (VehicleUtils.getBoundingBox(armorStand) != null) {
                            Location loc1 = VehicleUtils.getBoundingBox(armorStand)[0];
                            Location loc2 = VehicleUtils.getBoundingBox(armorStand)[1];
                            Collection<LivingEntity> nearbyEntities = new ArrayList<>();
                            Collection<ArmorStand> nearbyVehicles = new ArrayList<>();
                            for (Entity entity : loc1.getWorld().getNearbyEntities(loc1, loc2.getX(), loc2.getY(), loc2.getZ())) {
                                if (entity.getType() == EntityType.ARMOR_STAND && entity.hasMetadata("WastedVehicle")) {
                                    if (entity == armorStand) continue;
                                    if (entity.hasMetadata("WastedVehiclePassenger")) continue;
                                    nearbyVehicles.add((ArmorStand) entity);
                                    continue;
                                }
                                if (entity.getType() != EntityType.PLAYER && !(entity instanceof Creature)) continue;
                                if (entity == p) continue;
                                if (entity.getVehicle() != null && entity.getVehicle().hasMetadata("WastedVehiclePassenger"))
                                    continue;
                                nearbyEntities.add((LivingEntity) entity);
                            }
                            for (ArmorStand as : nearbyVehicles) {
                                Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
                                Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());
                                Vector asVector = new Vector(as.getLocation().getX(), as.getLocation().getY(),
                                        as.getLocation().getZ());
                                if (asVector.isInAABB(vec1, vec2)) {
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!(as.getMetadata("WastedVehicle").get(0).value() instanceof WastedVehicle))
                                                return;
                                            WastedVehicle collidedWith = (WastedVehicle) as.getMetadata("WastedVehicle").get(0).value();
                                            if (as.getPassenger() == null || as.getPassenger().getType() != EntityType.PLAYER)
                                                return;
                                            Player collidedWithPlayer = (Player) as.getPassenger();
                                            VehicleImpactVehicleEvent impactEvent =
                                                    new VehicleImpactVehicleEvent(wastedVehicle, p, collidedWith,
                                                            collidedWithPlayer, armorStand, as, steerDirection);
                                            plugin.getServer().getPluginManager().callEvent(impactEvent);
                                        }
                                    }, 0L);
                                }
                            }
                            for (LivingEntity livingEntity : nearbyEntities) {
                                Vector vec1 = new Vector(loc1.getX(), loc1.getY(), loc1.getZ());
                                Vector vec2 = new Vector(loc2.getX(), loc2.getY(), loc2.getZ());
                                Vector pVector = new Vector(livingEntity.getLocation().getX(), livingEntity.getLocation().getY(),
                                        livingEntity.getLocation().getZ());
                                if (pVector.isInAABB(vec1, vec2)) {
                                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.plugin, new Runnable() {
                                        @Override
                                        public void run() {
                                            VehicleImpactEntityEvent entityEvent = new VehicleImpactEntityEvent(wastedVehicle, p, livingEntity, armorStand, steerDirection);
                                            plugin.getServer().getPluginManager().callEvent(entityEvent);
                                        }
                                    }, 0L);
                                }
                            }
                        }
                        if (steerDirection.isJump() && !wastedVehicle.isCooldown() && armorStand.getHealth() > 1 && l != null && v != null) {
                            if (wastedVehicle.getVehicleProperties().getVehicleType() == VehicleType.PLANE && armorStand.getTicksLived() <= 140) {
                                p.sendMessage(Lang.VEHICLES.f("&7Get in the air before firing your weapon!"));
                                event.setCancelled(true);
                                return;
                            }
                            Optional<Weapon<?>> weapon = GTMGuns.getInstance().getWeaponManager().getWeapon(wastedVehicle.getVehicleProperties().getWastedGunsWeapon());
                            // TODO remove debug
                            ServerUtil.debug("[VehicleListener][DEBUG] - " + (weapon.isPresent() ? "exists as=" + weapon.get().getName() : "does not exist"));
                            if (weapon.isPresent() && weapon.get() instanceof RangedWeapon) {
                                Bukkit.getScheduler().callSyncMethod(this.plugin, () -> {
                                    ((RangedWeapon) weapon.get()).vehicleShoot(p, l, v/*, GTMGuns.getInstance().getWeaponManager().getPlayerCache(p.getUniqueId())*/, ((RangedWeapon) weapon.get()).isAutomatic());
                                    return null;
                                });
                                wastedVehicle.setCooldown(true);
                                Bukkit.getScheduler().scheduleSyncDelayedTask(this.plugin, () -> wastedVehicle.setCooldown(false), weapon.get().getDelay());
                            }
                        }
                        if (wastedVehicle.getVehicleProperties().getDisplayBlocks().size() > 1 && wastedVehicle.isTick()){
                        	armorStand.setHelmet(wastedVehicle.getNextDisplayBlock());
                        }

                        wastedVehicle.setTick(!wastedVehicle.isTick());
                        wastedVehicle.onSteer(armorStand, p, steerDirection);
                    }
                }
            }
        });
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, () -> this.plugin.getEntityQueue().forEach(a -> ((WastedVehicle) a.getMetadata("WastedVehicle").get(0).value()).onTick(a)), 1, 1);
    }


    /**
     * You may be thinking "Holy fuck Tim, this code is absolutely disgusting, why don't you just add a method to the rhino / hydra vehicle class??? Oh wait.."
     * Yea, there are no vehicle-specific classes like how there are with weapons. So the cooldowns / shooting has to be hardcoded. This can be changed in the
     * future, but this plugin would have to be rewritten.
     */
    @EventHandler
    public void onVehicleShoot(VehicleShootEvent event) {
        Player player = event.getShooter();
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        WastedVehicle wastedVehicle = event.getVehicle();
        switch (wastedVehicle.getVehicleProperties().getIdentifier().toLowerCase()) {
            case "hydra":
                if(user.isOnCooldown("hydra_shoot", true))
                    break;
                user.addCooldown("hydra_shoot", 1, false, true);
                wastedVehicle.shootWeapon(player);
                break;
            case "rhino":
                if(user.isOnCooldown("rhino_shoot", true))
                    break;
                user.addCooldown("rhino_shoot", 2, false, true);
                wastedVehicle.shootWeapon(player);
                break;
        }
    }

    @EventHandler
    public void onVehicleImpactEntity(VehicleImpactEntityEvent event) {
        WastedVehicle vehicle = event.getVehicle();
        Player driver = event.getDriver();
        LivingEntity livingEntity = event.getImpactedEntity();
        vehicle.getPassengers().forEach(passenger -> {
            if (passenger.getPassenger() != null) {
                if (Objects.equals(livingEntity, passenger.getPassenger())) {
                    event.setCancelled(true);
                }
            }
        });
        if (Objects.equals(vehicle.getVehicleProperties().getIdentifier(), "Rhino")) {
            livingEntity.damage(3.0, driver);
        } else if (vehicle.getVehicleProperties().getVehicleType() == VehicleType.CAR) {
            Location loc = event.getArmorStand().getEyeLocation();
            loc.setPitch(-8);
            if (event.getSteerDirection().isForward()) {
                livingEntity.setVelocity(loc.getDirection().multiply(vehicle.getSpeed() * 2));
            } else if (event.getSteerDirection().isBackward()) {
                livingEntity.setVelocity(loc.getDirection().multiply(vehicle.getSpeed() * 2 - 1));
            }
            livingEntity.damage(5.0, driver);
            event.getArmorStand().damage(2.0);
        }
    }

    @EventHandler
    public void onVehicleImpactVehicle(VehicleImpactVehicleEvent event) {
        WastedVehicle vehicle = event.getVehicle();
        WastedVehicle collided = event.getCollidedWith();
        Player player = event.getDriver();
        Player collidedDriver = event.getCollidedWithDriver();
        if (collided.getVehicleProperties().getVehicleType() != VehicleType.CAR) return;
        if (event.getSteerDirection().isForward() || event.getSteerDirection().isBackward()) {
            vehicle.explode(event.getArmorStand());
        } else {
            player.damage(10.0, collidedDriver);
            collidedDriver.damage(10.0, player);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for (Entity entity : e.getChunk().getEntities()) {
            if (entity.hasMetadata("WastedVehicle")) {
                ArmorStand armorStand = (ArmorStand) entity;
                armorStand.setHealth(0);
                entity.remove();
                this.plugin.getEntityQueue().remove(entity);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        player.removePotionEffect(PotionEffectType.INVISIBILITY);
        Bukkit.getOnlinePlayers().stream().filter(p -> p.getVehicle() != null && p.getVehicle().hasMetadata("WastedVehicle")).forEach(player::hidePlayer);
        if (player.isInsideVehicle()) {
            player.getVehicle().remove();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (p.isInsideVehicle() && p.getVehicle().hasMetadata("WastedVehicle")) {
            ArmorStand armorStand = (ArmorStand) p.getVehicle();
//            armorStand.eject();
            WastedVehicle wastedVehicle = (WastedVehicle) armorStand.getMetadata("WastedVehicle").get(0).value();
            VehicleLeaveEvent event = new VehicleLeaveEvent(wastedVehicle, p, armorStand);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent e) {
        Player p = e.getPlayer();
        if (p.isInsideVehicle() && p.getVehicle().hasMetadata("WastedVehicle")) {
            ArmorStand armorStand = (ArmorStand) p.getVehicle();
//            armorStand.eject();
            WastedVehicle wastedVehicle = (WastedVehicle) armorStand.getMetadata("WastedVehicle").get(0).value();
            VehicleLeaveEvent event = new VehicleLeaveEvent(wastedVehicle, p, armorStand);
            Bukkit.getPluginManager().callEvent(event);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack heldItem = p.getInventory().getItemInMainHand();
        if (e.getAction() == Action.RIGHT_CLICK_AIR && p.getVehicle() != null && p.getVehicle().hasMetadata("WastedVehicle")) {
            WastedVehicle wastedVehicle = (WastedVehicle) p.getVehicle().getMetadata("WastedVehicle").get(0).value();
            //speed boost
            Optional<SpeedBoost> speedBoost = wastedVehicle.getVehicleProperties().getSpeedBoosts().stream().filter(s -> new JLibItem(s.getItemStack()).isSimilar(heldItem)).findFirst();
            speedBoost.ifPresent(s -> {
                VehicleSpeedBoostEvent event = new VehicleSpeedBoostEvent(wastedVehicle, p, (ArmorStand) p.getVehicle(), s);
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    wastedVehicle.onSpeedBoost(s);
                    heldItem.setAmount(heldItem.getAmount() - 1);
                    p.getInventory().setItemInMainHand(heldItem);
                }
            });

            VehicleShootEvent event = new VehicleShootEvent(wastedVehicle, (ArmorStand)p.getVehicle(), p);
            Bukkit.getPluginManager().callEvent(event);
            if(event.isCancelled())
                e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onVehicleEnterEvent(VehicleEnterEvent event){
        WastedVehicle vehicle = event.getVehicle();
        Player player = event.getPlayer();
        switch (vehicle.getVehicleProperties().getIdentifier().toLowerCase()) {
            case "rhino":
            case "hydra":
                player.sendMessage(Lang.VEHICLES.f("&6In order to use the launcher in this vehicle, you must have an item in your hand and right click."));
                break;
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractAtEntityEvent event) {
        Entity rightClicked = event.getRightClicked();
        Player player = event.getPlayer();
        if (player.getVehicle() != null) return;
        if (rightClicked.hasMetadata("WastedVehiclePassenger")) {
            event.setCancelled(true);
            WastedVehicle wastedVehicle = (WastedVehicle) rightClicked.getMetadata("WastedVehiclePassenger").get(0).value();
            if (rightClicked.getPassenger() == null) {
                wastedVehicle.passengerMount((ArmorStand) rightClicked, player);
            }
            return;
        }
        if (rightClicked.hasMetadata("WastedVehicle")) {
            event.setCancelled(true);
            WastedVehicle wastedVehicle = (WastedVehicle) rightClicked.getMetadata("WastedVehicle").get(0).value();
            if (rightClicked.getPassenger() != null) {
                wastedVehicle.passengerMount((ArmorStand) rightClicked, player);
            }

            if (player.getFireTicks() > 0) {
                player.sendMessage(Lang.VEHICLES.f("&cYou cannot enter a vehicle when you're on fire."));
                return;
            }

            VehicleEnterEvent vehicleEnterEvent = new VehicleEnterEvent(wastedVehicle, player, (ArmorStand) rightClicked);
            Bukkit.getPluginManager().callEvent(vehicleEnterEvent);
            if (!vehicleEnterEvent.isCancelled()) wastedVehicle.onRightClick((ArmorStand) rightClicked, player);
        }
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent e) {
        Entity dismounted = e.getDismounted();
        Entity entity = e.getEntity();
        if (dismounted.hasMetadata("WastedVehiclePassenger")) {
            WastedVehicle wastedVehicle = (WastedVehicle) dismounted.getMetadata("WastedVehiclePassenger").get(0).value();
            wastedVehicle.onDismount((ArmorStand) dismounted);

            entity.setVelocity(dismounted.getLocation().getDirection().multiply(-0.7));
            return;
        }
        if (dismounted.hasMetadata("WastedVehicle") && entity instanceof Player) {
            WastedVehicle wastedVehicle = (WastedVehicle) dismounted.getMetadata("WastedVehicle").get(0).value();
            VehicleLeaveEvent event = new VehicleLeaveEvent(wastedVehicle, (Player) entity, (ArmorStand) dismounted);
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled() && !(wastedVehicle instanceof Submarine && !((Player) entity).isSneaking()))
                ((WastedVehicle) dismounted.getMetadata("WastedVehicle").get(0).value()).onDismount((ArmorStand) dismounted);

            entity.setVelocity(dismounted.getLocation().getDirection().multiply(-0.7));
        }
    }

    @EventHandler
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        Entity victim = event.getEntity();
        Entity damager = event.getDamager();
        if (victim.hasMetadata("WastedVehicle")) {
            if (damager.getVehicle() != null && damager.getVehicle().hasMetadata("WastedVehiclePassenger")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (entity.hasMetadata("WastedVehiclePassenger")) {
            e.setCancelled(true);
            return;
        }
        if (entity.isInsideVehicle() && entity.getVehicle().hasMetadata("WastedVehicle")) {
            if (e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
            WastedVehicle wastedVehicle = (WastedVehicle) entity.getVehicle().getMetadata("WastedVehicle").get(0).value();
            if (wastedVehicle.getVehicleProperties().isDamageVehicleOnPlayerHit()) {
                e.setCancelled(true);
                VehicleDamageEvent event = new VehicleDamageEvent(wastedVehicle, (ArmorStand) entity.getVehicle(), e.getCause(), e.getDamage());
                Bukkit.getPluginManager().callEvent(event);
                if (!event.isCancelled())
                    wastedVehicle.onDamage((ArmorStand) entity.getVehicle(), e.getCause(), e.getDamage());
            }
        }
        if (entity.hasMetadata("WastedVehicle")) {
            e.setCancelled(true);
            WastedVehicle wastedVehicle = (WastedVehicle) entity.getMetadata("WastedVehicle").get(0).value();
            VehicleDamageEvent event = new VehicleDamageEvent(wastedVehicle, (ArmorStand) entity, e.getCause(), e.getDamage());
            Bukkit.getPluginManager().callEvent(event);
            if (!event.isCancelled()) wastedVehicle.onDamage((ArmorStand) entity, e.getCause(), e.getDamage());
        }
    }

    @EventHandler
    public void onExplosionDamageEntity(ExplosionDamageEntityEvent event) {
        event.getVictims().forEach(entity -> {
            if (entity.getType() != EntityType.ARMOR_STAND) return;
            if (entity.hasMetadata("WastedVehicle")) {
                WastedVehicle wastedVehicle = (WastedVehicle) entity.getMetadata("WastedVehicle").get(0).value();
                if (!Objects.equals(wastedVehicle.getVehicleProperties().getIdentifier(), "ArmoredKuruma")) {
                    wastedVehicle.explode((ArmorStand) entity);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWeaponRightClick(WeaponRightClickEvent event) {
        LivingEntity entity = event.getLivingEntity();
        if (!entity.isInsideVehicle()) return;
        if (entity.getVehicle().hasMetadata("WastedVehicle") ||
                entity.hasMetadata("WastedVehiclePassenger")) {
            WastedVehicle wastedVehicle = entity.getVehicle().hasMetadata("WastedVehicle")
                    ? (WastedVehicle) entity.getVehicle().getMetadata("WastedVehicle").get(0).value()
                    : (WastedVehicle) entity.getVehicle().getMetadata("WastedVehiclePassenger").get(0).value();
            if (!wastedVehicle.getVehicleProperties().getAllowedWeapons().contains(event.getWeapon().getCompactName())) {//getIdentifier()
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    protected final void onMovementCheat(MovementCheatEvent event) {
        Player player = event.getPlayerData().getPlayer();
        if (!player.isInsideVehicle()) return;
        if (player.getVehicle().hasMetadata("WastedVehicle") || player.hasMetadata("WastedVehiclePassenger"))
            event.setCancelled(true);
    }
}
