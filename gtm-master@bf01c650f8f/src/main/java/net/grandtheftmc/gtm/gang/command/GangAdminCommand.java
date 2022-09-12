package net.grandtheftmc.gtm.gang.command;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GangAdminCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.hasPermission("gangs.admin")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }

        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }

//        Player player = (Player) s;
//        UUID uuid = player.getUniqueId();
//        User user = Core.getUserManager().getLoadedUser(uuid);
//        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
//        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
//            int page = 1;
//            if (args.length > 1)
//                try {
//                    page = Integer.parseInt(args[1]);
//                } catch (NumberFormatException e) {
//                    page = 1;
//                }
//            if (page < 1) {
//                s.sendMessage(Lang.GANGS.f("&7The page must be a positive number!"));
//                return true;
//            }
//            s.sendMessage(Utils.f(" &7&m---------------&7[&a GangAdmin Help &7Page &a" + page + "&7/&a2 &7]&m---------------"));
//            switch (page) {
//                case 1:
//                    s.sendMessage(Utils.f("&a/ga help [page] &7Show this help page"));
//                    s.sendMessage(Utils.f("&a/ga set [player] [gang name] &7Forcefully add a player to a gang"));
//                    s.sendMessage(Utils.f("&a/ga setrank [player] [rank] &7Forcefully set a player's rank in their gang"));
//                    s.sendMessage(Utils.f("&a/ga kick [player] &7Forcefully kick a player from their gang"));
//                    s.sendMessage(Utils.f("&a/ga disband [gang] &7Forcefully disband a gang"));
//                    return true;
//            }
//        }
//        switch (args[0]) {
//            case "set": {
//                if (args.length != 3) {
//                    player.sendMessage(Lang.GANGS.f("&cError! &7Usage: &a/ga set [player] [gang name]"));
//                    return true;
//                }
//                String gangName = args[2];
//                String gangRank = "member";
//                if (Bukkit.getPlayer(args[1]) == null) {
////                    Core.sql.updateAsyncLater("update " + Core.name() + " set gang='" + gangName + "',gangRank='" + gangRank
////                            + "' where name='" + args[1] + "';");
//                    ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set gang='" + gangName + "',gangRank='" + gangRank + "' where name='" + args[1] + "';"));
//                } else {
//                    Player target = Bukkit.getPlayer(args[1]);
//                    GTMUser targetGTMUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
//                    targetGTMUser.setGang(gangName);
//                }
//                player.sendMessage(Lang.GANGS.f("&a" + args[1] + " &7is now a member of &a" + gangName));
//                return true;
//            }
//            case "setrank": {
//                return true;
//            }
//            case "kick": {
//                return true;
//            }
//            case "disband": {
//                return true;
//            }
//            default:
//                s.sendMessage(Utils.f("&a/ga help [page] &7Show this help page"));
//                s.sendMessage(Utils.f("&a/ga set [player] [gang name] &7Forcefully add a player to a gang"));
//                s.sendMessage(Utils.f("&a/ga setrank [player] [rank] &7Forcefully set a player's rank in their gang"));
//                s.sendMessage(Utils.f("&a/ga kick [player] &7Forcefully kick a player from their gang"));
//                s.sendMessage(Utils.f("&a/ga disband [gang] &7Forcefully disband a gang"));
//                return true;
//        }
        return true;
    }
}
