package net.grandtheftmc.gtm.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.CompassTarget;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;

public class PlayerTask extends BukkitRunnable {
    public static List<Block> fireBlocks = new ArrayList<>();
    private Long ct;

    @Override
    public void run() {
        this.ct = System.currentTimeMillis();
        if (!fireBlocks.isEmpty()) checkFire();
        for (Player player : Bukkit.getOnlinePlayers()) {
        	
            GTMUser gtmUser = GTMUserManager.getInstance().getUser(player.getUniqueId()).orElse(null);
            if (gtmUser != null){
            	this.checkCombatTagExpiration(player, gtmUser);
                this.checkCompassRefresh(player, gtmUser);
                this.checkTpaRequestExpiration(player, gtmUser);
                this.checkJailRelease(player, gtmUser);
                this.checkDualWield(player);
                this.checkElytra(player);
                this.checkHouses(player);
                gtmUser.checkBackupExpiration(player);
            }
        }
    }

    private void checkHouses(Player player) {
        if (player.getInventory().getChestplate() == null) return;
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(player.getUniqueId());
        if (houseUser.isInsidePremiumHouse() || houseUser.isInsideHouse()) {
            if (player.getInventory().getChestplate().getType() == Material.ELYTRA
                    || player.getInventory().getChestplate().getType() == Material.GOLD_CHESTPLATE) {
                Utils.giveItems(player, player.getInventory().getChestplate());
                player.getInventory().setChestplate(null);
                player.sendMessage(Lang.HOUSES.f("&7You cannot equip this gear while in a house!"));
            }
        }
    }

    private void checkFire() {
        Block block = fireBlocks.get(ThreadLocalRandom.current().nextInt(fireBlocks.size()));
        if (block.getChunk().isLoaded()) block.setType(Material.AIR);
        fireBlocks.remove(block);
    }

    private void checkElytra(Player player) {
        if (player.isGliding()) {
            if (player.getInventory().contains(Material.COAL)) {
                if (player.isSneaking()) {
                    ItemStack fuel = player.getInventory().getItem(player.getInventory().first(Material.COAL));
                    if (fuel.getAmount() <= 1 || fuel.getAmount() - 5 < 1) {
                        player.getInventory().remove(fuel);
                    } else {
                        fuel.setAmount(fuel.getAmount() - 5);
                    }
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
                    player.getInventory().getChestplate().setDurability((short) 0);
                }
            } else {
                player.sendMessage(Lang.VEHICLES.f("&7Elytra requires (jetpack) fuel to fly!"));
                player.setGliding(false);
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
                } else {
                    player.getInventory().addItem(player.getInventory().getChestplate());
                }
                player.getInventory().setChestplate(null);
                player.setFallDistance(-50);
            }
        }
    }

    private void checkDualWield(Player player) {
        if (player.getInventory().getItemInOffHand() != null && player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) return;
            player.getInventory().addItem(player.getInventory().getItemInOffHand());
        }
        player.getInventory().setItemInOffHand(null);
    }

    private void checkJailRelease(Player player, GTMUser gtmUser) {
        int timer = gtmUser.getJailTimer();
        if (!gtmUser.isArrested() || timer < 0)
            return;
        if (timer == 600 || timer == 300 || timer == 180 || timer == 120 || timer == 60 || timer == 30
                || timer == 15 || timer == 10 || (timer <= 5 && timer > 0)) {
            player.sendMessage(
                    Lang.JAIL.f("&7You will be released in &a" + Utils.timeInSecondsToText(timer) + "&7!"));
            if (timer == 1) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 50, 0));
                player.playSound(player.getLocation(), Sound.BLOCK_LAVA_EXTINGUISH, 0.5F, 1);
            } else
                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 4.0F / timer, 2);
        }
        if (timer == 0) {
            gtmUser.resetJail();
            player.teleport(GTM.getWarpManager().getSpawn().getLocation());
            player.sendMessage(Lang.JAIL.f("&7You were released from jail!"));
            player.removePotionEffect(PotionEffectType.SLOW);
            player.getActivePotionEffects().clear();
            return;
        }
        gtmUser.setJailTimer(timer - 1);
    }


    private void checkCombatTagExpiration(Player player, GTMUser user) {
        if (user.isInCombat() || user.getLastTag() == -1)
            return;
        user.setLastTag(-1);
        player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are no longer in combat. You may log out safely."));
    }

    private void checkCompassRefresh(Player player, GTMUser gtmUser) {
        if (gtmUser.hasCompassTarget() && gtmUser.getLastCompassRefresh() + 60000 < this.ct)
            gtmUser.refreshCompassTarget(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item == null || item.getType() != Material.COMPASS || !gtmUser.hasCompassTarget()) return;
        CompassTarget target = gtmUser.getCompassTarget();
        if(target.getTargetPlayer()==null && target.getType()== CompassTarget.TargetType.PLAYER) {
            player.sendMessage(Lang.GTM.f("&7The player that you were tracking has logged off or died, resetting your tracker."));
            gtmUser.unsetCompassTarget(player, Core.getUserManager().getLoadedUser(player.getUniqueId()));
            return;
        }
        double i = Utils.getAngleBetweenVectors(player, player.getCompassTarget());
        boolean negative = i < 0;
        i = Math.abs(i);
        String s = target.getType() == CompassTarget.TargetType.PLAYER ? Utils.f("&c&l" + target.getTargetPlayer().getName()) : "";
        if (i < 30)
            Utils.sendActionBar(player, "^ " + s + " ^");
        else if (i < 60)
            Utils.sendActionBar(player, negative ? "&e< " + s : s + " &e>");
        else if (i < 90)
            Utils.sendActionBar(player, negative ? "&e<< " + s : s + " &e>>");
        else if (i < 120)
            Utils.sendActionBar(player, negative ? "&c<&e< " + s : s + " &e>&c>");
        else if (i < 150)
            Utils.sendActionBar(player, negative ? "&c<&e<< " + s : s + " &e>>&c>");
        else
            Utils.sendActionBar(player, negative ? "&c<<< " + s : s + " &c>>>");
        // TODO make fancier and cooler and stuff
    }

    private void checkTpaRequestExpiration(Player player, GTMUser gtmUser) {
        if (gtmUser.getLastTpaRequest() > 0 && gtmUser.getLastTpaRequest() + 60000 < this.ct) {
            gtmUser.unsetTpaRequests();
        }
    }
}
