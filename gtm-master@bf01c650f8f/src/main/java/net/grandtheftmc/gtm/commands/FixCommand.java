package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.CheatCode;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FixCommand implements CommandExecutor {
    private final List<Material> repairableItems = Arrays.asList(Material.LEATHER_CHESTPLATE,
            Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
            Material.GOLD_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
            Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
            Material.DIAMOND_HELMET, Material.ELYTRA,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_BOOTS);

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        GTMUser GTMUser = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (args.length == 0 || args.length == 1 && "hand".equalsIgnoreCase(args[0])) {
            if(GTMUser.getCheatCodeState(CheatCode.FIXHAND).getState()== State.LOCKED) {
                player.sendMessage(Lang.CHEAT_CODES.f(CheatCode.FIXHAND.getLockedLore()));
                return false;
            }
            if(user.isOnCooldown("fix_hand_command")) {
                player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(user.getCooldownTimeLeft("fix_hand_command"), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
                return false;
            }
        	ItemStack item = player.getInventory().getItemInMainHand();

            if(this.repairableItems.contains(item.getType())) {

                item.setDurability((short)0);
                player.sendMessage(Lang.GTM.f("&7You have repaired your " + (item.getItemMeta().getDisplayName() == null ? item.getType().name().toLowerCase().replace("_", "") : item.getItemMeta().getDisplayName()) + "&7!"));
                user.addCooldown("fix_hand_command", GTMUtils.getFixHandDelay(user.getUserRank()), true, true);
            } else {
                player.sendMessage(Lang.GTM.f("&7That item may not be repaired."));
            }
            return true;
        } else if(args.length == 1 && "all".equalsIgnoreCase(args[0])) {
            if(GTMUser.getCheatCodeState(CheatCode.FIXALL).getState()== State.LOCKED) {
                player.sendMessage(Lang.CHEAT_CODES.f(CheatCode.FIXALL.getLockedLore()));
                return false;
            }
            if(user.isOnCooldown("fix_all_command")) {
                player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(user.getCooldownTimeLeft("fix_all_command"), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
                return false;
            }
            for(ItemStack item : player.getInventory().getContents()) {
                if(item == null) continue;
                if(!this.repairableItems.contains(item.getType())) continue;
                item.setDurability((short)0);
            }
            player.sendMessage(Lang.GTM.f("&7You have repaired all damaged items in your inventory!"));
            user.addCooldown("fix_all_command", GTMUtils.getFixAllDelay(user.getUserRank()), true, true);
        } else {
            player.sendMessage(Lang.GTM.f("&7/fix hand - fixes the item in your hand."));
            player.sendMessage(Lang.GTM.f("&7/fix all  - fixes all the items in your inventory."));
            return true;
        }
        return true;

    }


}