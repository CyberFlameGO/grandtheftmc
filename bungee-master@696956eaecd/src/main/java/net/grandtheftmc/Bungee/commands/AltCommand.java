package net.grandtheftmc.Bungee.commands;

//import fr.Alphart.BAT.Modules.Core.LookupFormatter;
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
import java.util.UUID;

public class AltCommand extends Command {

    public AltCommand() {
        super("alt", "bat.lookup.ip", "alts");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        /*if (args.length != 1) {
            s.sendMessage(Utils.ft("&c/alt <player>"));
            return;
        }
        if (!(s instanceof ProxiedPlayer)) {
            s.sendMessage(Utils.ft("&cYou are not a player!"));
            return;
        }
        ProxiedPlayer p = (ProxiedPlayer) s;
        UUID sender = p.getUniqueId();

        if (!RequestRateLimiter.requestCmd(sender)) {
            s.sendMessage(Utils.ft("&cYou have issued this command recently, please wait a second."));
            return;
        }

        String player = args[0];
        s.sendMessage(Utils.ft("&7Looking up player &a" + player + "&7 in the database."));
        ProxyServer.getInstance().getScheduler().runAsync(Bungee.getInstance(), () -> {
            ResultSet rs = Bungee.getBATSQL()
                    .query("select BAT_player,lastip from BAT_players where BAT_player='" + player + "';");
            String name1 = null;
            String lastip = null;
            try {
                if (rs.next()) {
                    name1 = rs.getString("BAT_player");
                    lastip = rs.getString("lastip");
                }
                rs.close();
            } catch (SQLException ignored) {

            }
            ProxiedPlayer p1 = ProxyServer.getInstance().getPlayer(sender);
            if (p1 == null)
                return;
//            if (lastip == null || name1 == null || !fr.Alphart.BAT.Utils.Utils.validIP(lastip)) {
//                p1.sendMessage(Utils.ft("&7That player is not in the database!"));
//                return;
//            }
//            new LookupFormatter().getSummaryLookupIP(lastip).forEach(p1::sendMessage);
        });*/
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }

}
