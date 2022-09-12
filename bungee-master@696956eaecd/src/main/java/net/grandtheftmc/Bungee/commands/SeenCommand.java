package net.grandtheftmc.Bungee.commands;

//import fr.Alphart.BAT.Utils.EnhancedDateFormat;
import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.utils.RequestRateLimiter;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class SeenCommand extends Command {

//    private final EnhancedDateFormat dateFormat = new EnhancedDateFormat(true);

    public SeenCommand() {
        super("seen", null, "lastonline", "lastlogin");
    }

    /*
        Redis procedure:
        ----------------
        1) Checks if player is on the local instance, if not checks redis distributed.
        2) If player is not found online we check the database. Again TODO: limitations on frequency of access calls.
     */

    @Override
    public void execute(CommandSender s, String[] args) {
        /*if (args.length != 1) {
            s.sendMessage(Utils.ft("&c/seen <player>"));
            return;
        }
        if (!(s instanceof ProxiedPlayer)) {
            s.sendMessage(Utils.ft("&cYou are not a player!"));
            return;
        }

        UUID sender = ((ProxiedPlayer) s).getUniqueId();
        String player = args[0];
        ProxiedPlayer p = ProxyServer.getInstance().getPlayer(player);

        boolean online = p != null || Bungee.getRedisManager().isPlayerOnline(player);

        if (online) {
            s.sendMessage(Utils.ft("&7The player &a" + (p != null ? p.getName() : args[0]) + "&7 is &aonline&7!"));
            return;
        }

        if (!RequestRateLimiter.requestCmd(sender)) {
            s.sendMessage(Utils.ft("&cYou have issued this command recently, please wait a second."));
            return;
        }

        s.sendMessage(Utils.ft("&7Looking up player &a" + player + "&7 in the database."));
        ProxyServer.getInstance().getScheduler().runAsync(Bungee.getInstance(), () -> {
            ResultSet rs = Bungee.getBATSQL().query("select BAT_player,lastlogin from BAT_players where BAT_player='" + player + "';");
            String name1 = null;
            Timestamp lastlogin = null;
            ProxiedPlayer p1 = ProxyServer.getInstance().getPlayer(sender);
            try {
                if (rs.isBeforeFirst()) {
                    rs.next();
                    name1 = rs.getString("BAT_player");
                    lastlogin = rs.getTimestamp("lastlogin");
                }
                rs.close();
            } catch (SQLException ignored) {
                p1.sendMessage(Utils.ft("&7Oops, something went wrong! Please try again."));
                return;
            }

            if (p1 == null)
                return;
            if (lastlogin == null || name1 == null) {
                p1.sendMessage(Utils.ft("&7That player is not in the database!"));
                return;
            }
//            p1.sendMessage(Utils.ft("&7The player &a" + name1 + "&7 last logged in &a" + this.dateFormat.format(lastlogin) + "&7!"));

        });*/

    }
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }
}
