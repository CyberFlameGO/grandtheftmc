package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 12/7/2017.
 */
public class CouponCreditsCommand extends CoreCommand<CommandSender> implements RankedCommand {
    public CouponCreditsCommand() {
        super("couponcredits", "Commands dealing with coupon credits");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length<3) {
            sender.sendMessage("&7-/couponcredits give <player> <amt>");
            return;
        }
        Player target = Bukkit.getPlayer(args[1]);
        if(target==null) {
            sender.sendMessage(Lang.REWARDS.f("&7That player is not currently online!"));
            return;
        }
        switch (args[0].toLowerCase()) {//in case we have to add more functionality
            case "give": {
                int amt = 0;
                try {
                    amt = Integer.parseInt(args[2]);
                }catch (NumberFormatException nfe) {
                    sender.sendMessage(Lang.REWARDS.f("&6" + args[2] + " &7is not a number."));
                    return;
                }
                User user = Core.getUserManager().getLoadedUser(target.getUniqueId());
                user.setCouponCredits(user.getCouponCredits()+amt);
                target.sendMessage(Lang.REWARDS.f("&7Thanks for watching the ad! Here are &a" + amt + " &7coupon credits!"));
                return;
            }
        }
    }

    /**
     * Get the required rank to use said command.
     *
     * @return UserRank
     */
    @Override
    public UserRank requiredRank() {
        return UserRank.DEV;
    }
}
