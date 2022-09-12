package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.users.User;
import net.grandtheftmc.Bungee.users.UserRank;
import net.grandtheftmc.Bungee.utils.PlaytimeManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.util.Optional;

public class PlaytimeCommand extends Command {

    public PlaytimeCommand() {
        super("playtime", "playtime.admin", "playtime");
    }

    /*
        MySQL DB:
        ---------
        Typical Row: <uuid> <username> <session-time> <date-time>

        UUID - UUID of player.
        Username - Username of player.
        Session Time - Time, in MS that the player was connected to this proxy.
        Date Time - System time in MS upon disconnect, used to purge old playtime records.
     */

    @Override
    public void execute(CommandSender s, String[] args) {
        if (!(s instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) s;

        //Don't allow non-admins to use this command.
        Optional<User> userOptional = Bungee.getUserManager().getLoadedUser(player.getUniqueId());
        if (!userOptional.isPresent() || !userOptional.get().isRank(UserRank.ADMIN)) {
            player.sendMessage(Lang.NOPERM.st());
            return;
        }

        Bungee.getUserManager().getSortedUsers().forEach(user -> {
            PlaytimeManager.lookupPlaytime(player, user.getUsername());
        });
    }
}