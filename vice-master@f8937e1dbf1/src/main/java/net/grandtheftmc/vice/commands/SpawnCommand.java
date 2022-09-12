package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.world.warps.WarpManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        WarpManager wm = Vice.getWorldManager().getWarpManager();
        Player player = (Player) s;
        if (args.length == 0) {
            wm.warp(player, Core.getUserManager().getLoadedUser(player.getUniqueId()),
                    Vice.getUserManager().getLoadedUser(player.getUniqueId()), new TaxiTarget(wm.getSpawn()), 0, -1);

            return true;
        }
        if (!s.hasPermission("warps.admin")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        switch (args[0].toLowerCase()) {
        case "setspawn":
            wm.setSpawn(player.getLocation());
            s.sendMessage(Utils.f("&aYou set the spawn!"));
            return true;
            case "settutorialspawn":
            wm.setTutorialSpawn(player.getLocation());
            s.sendMessage(Utils.f("&aYou set the tutorial spawn!"));
            return true;
            case "setjail":
            wm.setJail(player.getLocation());
            s.sendMessage(Utils.f("&aYou set the jail spawn!"));
            return true;
            default:
            s.sendMessage(Utils.f("&c/spawn"));
            s.sendMessage(Utils.f("&c/spawn setspawn"));
            s.sendMessage(Utils.f("&c/spawn settutorialspawn"));
            return true;
        }
    }
}
