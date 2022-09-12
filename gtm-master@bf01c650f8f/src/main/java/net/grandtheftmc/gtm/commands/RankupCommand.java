package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMRank;
import net.grandtheftmc.gtm.users.GTMUser;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RankupCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.toString());
            return true;
        }
        Player player = (Player) s;
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (user.isArrested()) {
            player.sendMessage(Lang.JAIL.f("&7You can't rank up in jail!"));
            return true;
        }
        if (args.length == 1 && "confirm".equalsIgnoreCase(args[0])) {
            user.rankup(player, u);
            return true;
        }
        GTMRank nextRank = user.getRank().getNext();
        if (nextRank == null) {
            player.sendMessage(Utils.f(Lang.RANKUP + "&7You can't rank up any more!"));
            return true;
        }
        int price = nextRank.getPrice();

        if (!user.hasMoney(price)) {
            player.sendMessage(Utils.f(Lang.RANKUP + "&7You don't have the &c$&l" + price + "&7 required to rank up!"));
            return true;
        }
        player.sendMessage(Utils.f(Lang.RANKUP + "&7Ranking up to " + nextRank.getColoredNameBold() + "&7 costs &a$&l" + price + "&7."));
        player.sendMessage(Utils.f(Lang.RANKUP + "&7Type &a/rankup confirm&7 to rank up!"));
        return true;
    }

}
