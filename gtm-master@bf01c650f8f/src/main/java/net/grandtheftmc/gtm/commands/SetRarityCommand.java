package net.grandtheftmc.gtm.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.commands.RankedCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.items.GameItem;
import net.grandtheftmc.gtm.items.GameItem.ItemType;
import net.grandtheftmc.gtm.items.ItemManager;
import net.grandtheftmc.guns.GTMGuns;
import net.grandtheftmc.guns.weapon.Weapon;

public class SetRarityCommand extends CoreCommand<CommandSender> implements RankedCommand {
    public SetRarityCommand() {
        super("setrarity", "Set the rarity of a weapon", "setstars", "setstar");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(!Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN))
                return;
            
            // get item in main hand
            ItemStack is = player.getInventory().getItemInMainHand();
            if (is == null){
            	sender.sendMessage(ChatColor.RED + "Please put a weapon in hand to set the rarity for.");
            	return;
            }

            if(args.length == 0){
                sender.sendMessage(Utils.f("&c/setrarity <rarity> - &7Sets the rarity level for the item in hand."));
                return;
            }

            int rarity = 0;
            try {
            	rarity = Integer.parseInt(args[0]);
            }
            catch(Exception e){
            	sender.sendMessage(ChatColor.RED + "Please use a valid number to set the rarity of the item in hand to.");
            	e.printStackTrace();
            }
            
            // clamp bounds of rarity
            if (rarity < 0){
            	rarity = 0;
            }
            if (rarity > GTMGuns.MAX_STARS){
            	rarity = GTMGuns.MAX_STARS;
            }
 
            ItemManager im = GTM.getItemManager();
            GameItem gi = im.getItem(is);
            if (gi == null){
            	sender.sendMessage(ChatColor.RED + "Unable to find a game item similar to " + is.toString());
            	return;
            }
            
            if (gi.getType() != ItemType.WEAPON){
            	sender.sendMessage(ChatColor.RED + "Cannot set rarity of this item, as it's not a weapon.");
            	return;
            }
            
            Weapon weapon = GTMGuns.getInstance().getWeaponManager().getWeapon(is).orElse(null);
            if (weapon == null){
            	sender.sendMessage(ChatColor.RED + "Unable to find a weapon similar to " + is.toString());
            	return;
            }
            
            if (rarity == 0){
            	player.getInventory().setItemInHand(weapon.createItemStack());
            }
            else{
            	player.getInventory().setItemInMainHand(weapon.createItemStack(rarity, null));
            }
        }
        else{
        	sender.sendMessage(ChatColor.RED + "Player only command.");
        }
    }

	@Override
	public UserRank requiredRank() {
		return UserRank.ADMIN;
	}
}
