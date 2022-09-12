package net.grandtheftmc.gtm.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;

public class KillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (lbl.equalsIgnoreCase("suicide")) {
            if (player.getWorld().getName().equalsIgnoreCase("spawn")) return true;
            player.sendMessage(Lang.GTM.f("&7Have a nice &osuicide!"));
            player.getActivePotionEffects().forEach(effect -> player.removePotionEffect(effect.getType()));
            
            // grab current rank
            UserRank rank = user.getUserRank();
            if (rank != null){
            	// sr mod or builders and above should always have inventory cleared
            	if (rank.hasRank(UserRank.SRMOD) || rank.hasRank(UserRank.BUILDER)){
            		player.getInventory().clear();
            	}
            }

            player.damage(player.getHealth() * 10, player);
            return true;
        }
        if (!user.isRank(UserRank.ADMIN)) {
            player.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length != 1) {
            s.sendMessage(Utils.f("&c/kill <player>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Lang.GTM.f("&7That player is not online!"));
            return true;
        }
        if (target.isOp()) {
            s.sendMessage(Lang.GTM.f("&7That player can not be killed"));
            return true;
        }
        target.getActivePotionEffects().forEach(effect -> {
            target.removePotionEffect(effect.getType());
        });
        target.damage(target.getHealth());
        s.sendMessage(Lang.GTM.f("&7You killed &a"
                + Core.getUserManager().getLoadedUser(target.getUniqueId()).getColoredName(target) + "&7!"));
        return true;
    }
}

