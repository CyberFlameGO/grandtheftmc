package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.world.warps.WarpManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class TpaCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f(Lang.TAXI + "&eYou are not a player!"));
            return true;
        }
        WarpManager wm = Vice.getWorldManager().getWarpManager();
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        User user = Core.getUserManager().getLoadedUser(uuid);
        ViceUser viceUser = Vice.getUserManager().getLoadedUser(uuid);
        switch (lbl) {
        case "tpa":
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/tpa <player>"));
                return true;
            }
            wm.tpa(player, user, viceUser, Bukkit.getPlayer(args[0]));
            return true;
        case "tpahere":
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/tpahere <player>"));
                return true;
            }
            wm.tpaHere(player, user, viceUser, Bukkit.getPlayer(args[0]));
            return true;
        case "tpdeny":
        case "tpno":
            wm.tpDeny(player, user, viceUser);
            return true;
            default:
            if (args.length != 0) {
                s.sendMessage(Utils.f("&c/" + lbl));
                return true;
            }
            wm.tpAccept(player, user, viceUser);
            return true;
        }
    }

}
