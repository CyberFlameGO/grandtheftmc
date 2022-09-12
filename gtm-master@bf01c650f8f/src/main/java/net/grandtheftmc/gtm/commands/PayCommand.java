package net.grandtheftmc.gtm.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.users.GTMUser;

public class PayCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.toString());
            return true;
        }
        if (args.length != 2) {
            s.sendMessage(Utils.f("&c/pay <player> <amount>"));
            return true;
        }
        
        if (!GTM.getSettings().isPayCommand()){
        	s.sendMessage(Utils.f("&cPay is currently disabled!"));
        	return true;
        }
        
        Player player = (Player) s;
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        double amnt;
        try {
            amnt = Utils.round(Double.parseDouble(args[1]));
        } catch (NumberFormatException e) {
            s.sendMessage(Utils.f(Lang.MONEY + "&7The amount must be a number!"));
            return true;
        }
        if (amnt <= 0) {
            s.sendMessage(Lang.MONEY.f("&7The amount must be greater than 0!"));
            return true;
        }
        if (!user.hasMoney(amnt)) {
            s.sendMessage(Utils.f(Lang.MONEY + "&7You don't have &c$&l" + amnt + "&7!"));
            return true;
        }
        if (amnt % 1 != 0) {
            s.sendMessage(Utils.f(Lang.MONEY + "&7Whole numbers only! No pennies."));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Utils.f(Lang.MONEY + "&7That player is not online!"));
            return true;
        }
        GTMUser targetUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
        User tu = Core.getUserManager().getLoadedUser(target.getUniqueId());
        user.takeMoney(amnt);
        targetUser.addMoney(amnt);
        GTMUtils.updateBoard(player, u, user);
        GTMUtils.updateBoard(target, tu, targetUser);
        player.sendMessage(Utils.f(Lang.MONEY + "&7You sent &a$&l" + amnt + "&7 to " + tu.getColoredName(target) + "&7!"));
        target.sendMessage(Utils.f(Lang.MONEY + "&7You received &a$&l" + amnt + "&7 from " + u.getColoredName(player) + "&7!"));
        if (amnt > 100000)
            GTMUtils.moneylog(player, target, amnt);
        return true;
    }

}
