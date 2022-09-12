package net.grandtheftmc.vice.tasks;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class PlayerTask extends BukkitRunnable {
    public static List<Block> fireBlocks = new ArrayList<>();
    private Long ct;

    @Override
    public void run() {
        this.ct = System.currentTimeMillis();
        if (!fireBlocks.isEmpty()) checkFire();
        for (Player player : Bukkit.getOnlinePlayers()) {
            ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
            this.checkCombatTagExpiration(player, viceUser);
            this.checkTpaRequestExpiration(player, viceUser);
            this.checkJailRelease(player, viceUser);
            this.checkDualWield(player);
//            this.checkElytra(player);
            viceUser.checkBackupExpiration(player);
        }
    }


    private void checkFire() {
        Block block = fireBlocks.get(ThreadLocalRandom.current().nextInt(fireBlocks.size()));
        if (block.getChunk().isLoaded()) block.setType(Material.AIR);
        fireBlocks.remove(block);
    }

//    private void checkElytra(Player player) {
//        if (player.isGliding()) {
//            if (player.getInventory().contains(Material.COAL)) {
//                if (player.isSneaking()) {
//                    ItemStack fuel = player.getInventory().getItem(player.getInventory().first(Material.COAL));
//                    if (fuel.getAmount() <= 1 || fuel.getAmount() - 5 < 1) {
//                        player.getInventory().remove(fuel);
//                    } else {
//                        fuel.setAmount(fuel.getAmount() - 5);
//                    }
//                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
//                    player.getInventory().getChestplate().setDurability((short) 0);
//                }
//            } else {
//                player.sendMessage(Lang.VEHICLES.f("&7Elytra requires (jetpack) fuel to fly!"));
//                player.setGliding(false);
//                if (player.getInventory().firstEmpty() == -1) {
//                    player.getWorld().dropItem(player.getLocation(), player.getInventory().getChestplate());
//                } else {
//                    player.getInventory().addItem(player.getInventory().getChestplate());
//                }
//                player.getInventory().setChestplate(null);
//                player.setFallDistance(-50);
//            }
//        }
//    }

    private void checkDualWield(Player player) {
        if (player.getInventory().getItemInOffHand() != null && player.getInventory().firstEmpty() != -1) {
            if (player.getInventory().getItemInOffHand().getType() == Material.SHIELD) return;
            player.getInventory().addItem(player.getInventory().getItemInOffHand());
        }
        player.getInventory().setItemInOffHand(null);
    }

    private void checkJailRelease(Player player, ViceUser viceUser) {
        int timer = viceUser.getJailTimer();
        if (!viceUser.isArrested() || timer < 0)
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
            viceUser.resetJail();
            player.teleport(Vice.getWorldManager().getWarpManager().getSpawn().getLocation());
            player.sendMessage(Lang.JAIL.f("&7You were released from jail!"));
            player.removePotionEffect(PotionEffectType.SLOW);
            player.getActivePotionEffects().clear();
            return;
        }
        viceUser.setJailTimer(timer - 1);
    }


    private void checkCombatTagExpiration(Player player, ViceUser user) {
        if (user.isInCombat() || user.getLastTag() == -1)
            return;
        user.setLastTag(-1);
//        Core.getUserManager().getLoadedUser(player.getUniqueId()).showPet(player);
        player.sendMessage(Utils.f(Lang.COMBATTAG + "&7You are no longer in combat. You may log out safely."));
    }


    private void checkTpaRequestExpiration(Player player, ViceUser viceUser) {
        if (viceUser.getLastTpaRequest() > 0 && viceUser.getLastTpaRequest() + 60000 < this.ct) {
            viceUser.unsetTpaRequests();
        }
    }
}
