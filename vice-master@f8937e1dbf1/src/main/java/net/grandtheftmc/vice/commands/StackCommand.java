package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.items.ArmorUpgrade;
import net.grandtheftmc.vice.items.GameItem;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.DurabilityUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StackCommand implements CommandExecutor {
    private static final List<Material> UNUSUAL_UNSTACKABLE_MATERIALS = new ArrayList<>(Arrays.asList(Material.CHEST, Material.WATCH, Material.SKULL, Material.SKULL_ITEM));
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.toString());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUser ViceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());

        if (ViceUser.getCheatCodeState(CheatCode.STACK).getState()== State.LOCKED) {
            player.sendMessage(Lang.CHEAT_CODES.f(CheatCode.STACK.getLockedLore()));
            return false;
        }
        if(user.isOnCooldown("stack_command")) {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(user.getCooldownTimeLeft("stack_command"), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
            return false;
        }
        int affected = 0;
        for(int i = 0; i<player.getInventory().getSize(); i++) {
            ItemStack is = player.getInventory().getItem(i);
            if(is==null || is.getType()==Material.AIR)
                continue;
            if(UNUSUAL_UNSTACKABLE_MATERIALS.contains(is.getType()) && is.getAmount()>=1)
                continue;
            GameItem gameItem = Vice.getItemManager().getItem(is);
            if(ViceUtils.isArmorPiece(is.getType()) && (DurabilityUtil.getDurability(is)-1!=is.getType().getMaxDurability()))
                continue;
            int maxStackSize = (ViceUtils.isTool(is.getType()) && is.getEnchantments().size()==0) || ViceUtils.isArmorPiece(is.getType()) || (gameItem!=null && (gameItem.getType()== GameItem.ItemType.DRUG || gameItem.getType()== GameItem.ItemType.WEAPON)) ? 64 : is.getMaxStackSize();
            if(is.getAmount()>=maxStackSize)
                continue;
            int amountNeeded = maxStackSize-is.getAmount();
            for(int j = i+1; j<player.getInventory().getSize(); j++) {
                ItemStack compare = player.getInventory().getItem(j);
                if(compare==null || compare.getDurability()!=is.getDurability() || compare.getType()!=is.getType() || (ViceUtils.isArmorPiece(compare.getType()) && ( DurabilityUtil.getDurability(compare)-1!=compare.getType().getMaxDurability()) || (is.getEnchantments().size()!=compare.getEnchantments().size() || !is.getEnchantments().keySet().containsAll(compare.getEnchantments().keySet())) || (ArmorUpgrade.getArmorUpgrades(is).size()!=ArmorUpgrade.getArmorUpgrades(compare).size() || !ArmorUpgrade.getArmorUpgrades(is).containsAll(ArmorUpgrade.getArmorUpgrades(compare)))))
                    continue;
                if(is.getAmount()>=maxStackSize)
                    break;
                if(compare.getAmount()>amountNeeded) {
                    is.setAmount(maxStackSize);
                    compare.setAmount(compare.getAmount()-amountNeeded);
                    affected++;
                    break;
                }
                else {
                    is.setAmount(is.getAmount()+compare.getAmount());
                    amountNeeded -= compare.getAmount();
                    player.getInventory().setItem(j, null);
                    affected++;
                }
            }
            player.getInventory().setItem(i, is);
        }

        if (affected > 0) {
           //  user.addCooldown("stack_command", ViceUtils.getStackDelay(user.getUserRank()), false, true);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.updateInventory();
                }
            }.runTaskLater(Core.getInstance(), 5);
            player.sendMessage(Lang.VICE.f("&7Items compacted into stacks!"));
        } else {
            player.sendMessage(Lang.VICE.f("&7No stackable items found!"));
        }
        return true;
    }


}