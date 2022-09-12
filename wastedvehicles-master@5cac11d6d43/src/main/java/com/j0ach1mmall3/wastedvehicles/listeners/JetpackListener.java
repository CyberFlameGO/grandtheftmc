package com.j0ach1mmall3.wastedvehicles.listeners;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.j0ach1mmall3.jlib.player.JLibPlayer;
import com.j0ach1mmall3.wastedguns.api.events.WeaponRightClickEvent;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.events.FuelUseEvent;
import com.j0ach1mmall3.wastedvehicles.api.events.JetpackFlyEvent;

import net.grandtheftmc.core.anticheat.check.CheatType;
import net.grandtheftmc.core.anticheat.event.MovementCheatEvent;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 5/05/2016
 */
public final class JetpackListener implements Listener {
    private final Main plugin;
    private final Set<UUID> active = new HashSet<>();

    public JetpackListener(Main plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this.plugin, () -> {
            Bukkit.getOnlinePlayers().stream()
                    .filter(p -> {
                        return p.isFlying() && p.getGameMode() != GameMode.CREATIVE && p.getInventory().getChestplate() != null && isJetpack(p.getInventory().getChestplate());
                    }).forEach(p -> new JLibPlayer(p).playSound(Sound.BLOCK_FIRE_EXTINGUISH));
        }, 5L, 5L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() == 38 && isJetpack(e.getCurrentItem()) && p.isFlying())
            p.setAllowFlight(false);
    }

    private void updateFly(Player p, boolean state) {
        if (p.getAllowFlight() != state) {
            p.setAllowFlight(state);
        }
    }

    private void updateFlying(Player p, boolean state) {
        if (p.isFlying() != state) {
            p.setFlying(state);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Location from = e.getFrom();
        Location to = e.getTo();

        if (from.getX() == to.getX() && from.getY() == to.getY() && from.getZ() == to.getZ()) {
            return;
        }

        if (!p.getAllowFlight()) {
            this.updateFly(p, false);
            p.setFlying(false);
        }

        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
            this.updateFly(p, true);
            return;
        }

        if (p.getInventory().getChestplate() != null && isJetpack(p.getInventory().getChestplate())) {
            if (p.isFlying() && p.getAllowFlight()) {
                p.getWorld().spigot().playEffect(p.getLocation().add(0, 1, 0), Effect.CLOUD, 0, 0, 0, 0, 0, 0, 1, 50);
                p.getWorld().spigot().playEffect(p.getLocation().add(0, 1, 0), Effect.FLAME, 0, 0, 0, 0, 0, 0, 1, 50);
            }
            if (this.plugin.getBabies().isJetpackFuelEnabled() && !containsAtLeast(p, 1)) {
                this.updateFly(p, false);
                p.setFlying(false);
            } else {
                boolean c = e.getTo().getY() < e.getFrom().getY() && e.getTo().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR;
                this.updateFly(p, !c);
            }
        } else {
            this.updateFly(p, false);
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player p = event.getPlayer();
        if(p.getInventory().getChestplate()!=null && isJetpack(p.getInventory().getChestplate())) {
            updateFlying(p, false);
            updateFly(p, false);
        }
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e) {
        Player p = e.getPlayer();

        JetpackFlyEvent event = new JetpackFlyEvent(p);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) {
            e.setCancelled(true);
            return;
        }

        if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
            p.setAllowFlight(true);
            return;
        }

        if (!this.active.contains(p.getUniqueId())){
            this.active.add(p.getUniqueId());
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!p.isOnline()) {
                        this.cancel();
                        return;
                    }
                    if (p.isFlying()) {
                        FuelUseEvent fuelUseEvent = new FuelUseEvent(p);
                        Bukkit.getPluginManager().callEvent(fuelUseEvent);
                        if(fuelUseEvent.isCancelled()) {//this means that fuel shouldnt be used
                            return;
                        }
                        else if (JetpackListener.this.plugin.getBabies().isJetpackFuelEnabled() && !containsAtLeast(p, 1)) {
                            Bukkit.getScheduler().runTask(JetpackListener.this.plugin, () -> {
                                p.setAllowFlight(false);
                                p.setFlying(false);
                            });
                            JetpackListener.this.active.remove(p.getUniqueId());
                            this.cancel();
                        } else {
                            ItemStack itemStack = JetpackListener.this.plugin.getBabies().getJetpackFuelItem().clone();
                            for (ItemStack item : p.getInventory().getContents()) {
                                if (item != null && Objects.equals(item.getType(), itemStack.getType())
                                        && Objects.equals(item.getItemMeta().getDisplayName(), itemStack.getItemMeta().getDisplayName())) {
                                    if (item.getAmount() == 1) {
                                        p.getInventory().remove(item);
                                    } else {
                                        item.setAmount(item.getAmount() - 1);
                                    }

                                    break;
                                }
                            }
                        }
                    } else {
                        JetpackListener.this.active.remove(p.getUniqueId());

                        if(p.isOnline()) {
                            Bukkit.getScheduler().runTask(JetpackListener.this.plugin, () -> {
                                p.setAllowFlight(false);
                                p.setFlying(false);
                            });
                            this.cancel();
                        }
                    }
                }
            }.runTaskTimerAsynchronously(this.plugin, 0, this.plugin.getBabies().getJetpackFuelInterval());
        }
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onWeaponRightClick(WeaponRightClickEvent e) {
        LivingEntity entity = e.getLivingEntity();
        if (entity.getEquipment().getChestplate() != null && isJetpack(entity.getEquipment().getChestplate()) && this.plugin.getBabies().getJetpackAllowedWeapons().contains(e.getWeapon().getCompactName()))//getIdentifier
            e.setCancelled(true);
    }

    private boolean isJetpack(ItemStack itemStack) {
        if(itemStack != null && this.plugin.getBabies().getJetpackItem() != null) {
            if (itemStack.getType() != this.plugin.getBabies().getJetpackItem().getType()) {
                return false;
            }

            if (itemStack.getItemMeta() == null || itemStack.getItemMeta().getDisplayName() == null || !itemStack.getItemMeta().getDisplayName().contains("Jetpack")) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean containsAtLeast(Player player, int amount) {

        for (int i = 0; i < player.getInventory().getSize(); i++) {

            ItemStack item = player.getInventory().getContents()[i];
            ItemStack fuelItem = this.plugin.getBabies().getJetpackFuelItem();

            if (item != null && item.getType().equals(fuelItem.getType()) && item.getItemMeta().getDisplayName().equals(fuelItem.getItemMeta().getDisplayName())) {

                if (item.getAmount() < 1 || item.getAmount() >= amount) {
                    return true;
                }
            }
        }

        return false;
    }

    @EventHandler
    protected final void onMovementCheat(MovementCheatEvent<Double> event) {
        Player player = event.getPlayerData().getPlayer();
        if(player == null) return;
        if(event.getCheatType().getType() != CheatType.Type.SPEED) return;

        //System.out.println("Max speed: " + event.getObj());

        if(player.isFlying() && player.getAllowFlight()) {
            if (player.getEquipment().getChestplate() != null && isJetpack(player.getEquipment().getChestplate()))
                event.setCancelled(true);
        }
    }

    @EventHandler
    protected final void onPlayerLeave(PlayerQuitEvent event) {
        if(this.active.contains(event.getPlayer().getUniqueId()))
            this.active.remove(event.getPlayer().getUniqueId());
    }
}
