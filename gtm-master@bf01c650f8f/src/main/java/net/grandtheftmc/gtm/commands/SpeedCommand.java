package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpeedCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.GTM.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player)s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(SpectatorCommand.getActiveStaff().contains(player.getName()) || user.isRank(UserRank.ADMIN)) {
            if(args.length != 1) {
                player.sendMessage(Lang.GTM.f("&7Usage: /speed <1-10>"));
            } else {
                float speed = Float.valueOf(args[0]);
                if(user.isRank(UserRank.ADMIN) && !player.isFlying()) {
                    speed = getRealMoveSpeed(speed, false);
                    player.setWalkSpeed(speed);
                } else {
                    speed = getRealMoveSpeed(speed, true);
                    player.setFlySpeed(speed);
                }
                player.sendMessage(Lang.GTM.f("&7Your " + (player.isFlying() ? "fly" : "walk") +
                        " speed has been set to &a" + Integer.min(Integer.valueOf(args[0]), 10)));
            }
        } else if(user.isRank(UserRank.HELPOP)) {
            player.sendMessage(Lang.GTM.f("&7You must be in spectator mode to use this command."));
        }
        return true;
    }

    private float getRealMoveSpeed(float userSpeed, final boolean isFly) {
        final float defaultSpeed = isFly ? 0.1f : 0.2f;
        float maxSpeed = 1f;

        if (userSpeed > 10f) {
            userSpeed = 10f;
        } else if (userSpeed < 0.0001f) {
            userSpeed = 0.0001f;
        }

        if (userSpeed < 1f) {
            return defaultSpeed * userSpeed;
        } else {
            float ratio = ((userSpeed - 1) / 9) * (maxSpeed - defaultSpeed);
            return ratio + defaultSpeed;
        }
    }
}
