package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.durability.DurabilityItems;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.DurabilityUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class FixCommand implements CommandExecutor {
    private final List<Material> repairableItems = Arrays.asList(
//          Material.LEATHER_CHESTPLATE,
//			Material.CHAINMAIL_CHESTPLATE, Material.IRON_CHESTPLATE,
//			Material.GOLD_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
//			Material.LEATHER_BOOTS, Material.LEATHER_LEGGINGS,
//			Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
//			Material.DIAMOND_HELMET,  Material.ELYTRA

            Material.WOOD_PICKAXE, Material.STONE_PICKAXE,Material.GOLD_PICKAXE,Material.IRON_PICKAXE,Material.DIAMOND_PICKAXE,
            Material.WOOD_SPADE, Material.STONE_SPADE,Material.GOLD_SPADE,Material.IRON_SPADE,Material.DIAMOND_SPADE,
            Material.WOOD_AXE, Material.STONE_AXE,Material.GOLD_AXE,Material.IRON_AXE,Material.DIAMOND_AXE,
            Material.WOOD_HOE, Material.STONE_HOE,Material.GOLD_HOE,Material.IRON_HOE,Material.DIAMOND_HOE
            );

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (args.length == 0 || args.length == 1 && "hand".equalsIgnoreCase(args[0])) {
            if(viceUser.getCheatCodeState(CheatCode.FIXHAND).getState()== State.LOCKED) {
                player.sendMessage(Lang.CHEAT_CODES.f(CheatCode.FIXHAND.getLockedLore()));
                return false;
            }
            if(user.isOnCooldown("fix_hand_command")) {
                player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(user.getCooldownTimeLeft("fix_hand_command"), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
                return false;
            }
        	ItemStack item = player.getInventory().getItemInMainHand();

			Optional<DurabilityItems> durabilityItem = DurabilityUtil.getDurabilityItem(item);

            if(this.repairableItems.contains(item.getType()) || durabilityItem.isPresent()) {

                if (durabilityItem.isPresent()) {
                    ItemStack newRepaired = repairDurability(item);
                    player.getInventory().setItem(player.getInventory().getHeldItemSlot(), newRepaired);
                } else {
                    item.setDurability((short)0);
                }

                player.sendMessage(Lang.VICE.f("&7You have repaired your " + (item.getItemMeta().getDisplayName() == null ? item.getType().name().toLowerCase().replace("_", "") : item.getItemMeta().getDisplayName()) + "&7!"));
                user.addCooldown("fix_hand_command", ViceUtils.getFixHandDelay(user.getUserRank()), false, true);
            } else {
                player.sendMessage(Lang.VICE.f("&7That item may not be repaired."));
            }
            return true;
        } else if(args.length == 1 && "all".equalsIgnoreCase(args[0])) {
            if(viceUser.getCheatCodeState(CheatCode.FIXALL).getState()== State.LOCKED) {
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
            player.sendMessage(Lang.VICE.f("&7You have repaired all damaged items in your inventory!"));
            user.addCooldown("fix_all_command", ViceUtils.getFixAllDelay(user.getUserRank()), false, true);
        } else {
            player.sendMessage(Lang.VICE.f("&7/fix hand - fixes the item in your hand."));
            player.sendMessage(Lang.VICE.f("&7/fix all  - fixes all the items in your inventory."));
            return true;
        }
        return true;

    }

    private ItemStack repairDurability(ItemStack item) {

		Optional<DurabilityItems> durabilityItems = DurabilityUtil.getDurabilityItem(item);

		if (item == null || !durabilityItems.isPresent()) {
			return null;
		}

		if (item.getItemMeta() != null && !item.getItemMeta().isUnbreakable()) {
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setUnbreakable(true);
			itemMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
			item.setItemMeta(itemMeta);
		}

		DurabilityItems durabilityItem = durabilityItems.get();

		ItemStack newArmour = DurabilityUtil.setDurability(item, durabilityItem.getMaximumDurability());
		DurabilityUtil.setDurabilityLore(newArmour, durabilityItem.getMaximumDurability(), durabilityItem);

		return newArmour;
    }
}