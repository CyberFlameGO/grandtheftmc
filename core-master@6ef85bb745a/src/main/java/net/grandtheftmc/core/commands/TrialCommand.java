package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.C;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-11-06.
 */
public class TrialCommand extends CoreCommand<Player> {
    public TrialCommand() {
        super("trial", "displays the amount of time left for your trial rank");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length==0 || !args[0].equalsIgnoreCase("time")) {
            player.sendMessage(Lang.SHOP.f("&6/trial time &7- displays the amount of time remaining for your trial rank."));
            return;
        }
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(user.getTrialRankExpiry()==0) {
            player.sendMessage(Lang.SHOP.f("&7You do not have a trial rank!"));
            return;
        }

        long timeLeft = user.getTrialRankExpiry()-System.currentTimeMillis();
        player.sendMessage(Lang.SHOP.f("&7You have &6" + Utils.timeInMillisToText(timeLeft, C.GOLD, C.GRAY, C.GRAY) + " &7until your trial rank expires."));
    }
}
