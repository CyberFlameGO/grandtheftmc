package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.State;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.CheatCode;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class FeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
       /* if (args.length > 0 && s.hasPermission("command.feed.others")) {
            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                s.sendMessage(Lang.VICE.f("&7That player is not online!"));
                return true;
            }
            player.setFoodLevel(20);
            player.setSaturation(20);
            s.sendMessage(Lang.VICE.f("&7You fed &a" + player.getName() + "&7!"));
            return true;
        }*/
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (viceUser.getCheatCodeState(CheatCode.FEED).getState()== State.LOCKED) {
            player.sendMessage(Lang.CHEAT_CODES.f(CheatCode.FEED.getLockedLore()));
            return true;
        }

        if(user.isOnCooldown("feed_command")) {
            player.sendMessage(Lang.CHEAT_CODES.f("&7You must wait &a" + Utils.timeInSecondsToText(user.getCooldownTimeLeft("feed_command"), C.RED, C.RED, C.GRAY) + " &7before using this cheatcode again!"));
            return true;
        }

        user.addCooldown("feed_command", ViceUtils.getFeedDelay(user.getUserRank()), false, true);
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.sendMessage(Lang.VICE.f("&7You fed yourself!"));
        return true;

    }
}
