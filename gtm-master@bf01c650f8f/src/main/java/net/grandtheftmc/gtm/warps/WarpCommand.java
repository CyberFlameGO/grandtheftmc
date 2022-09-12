package net.grandtheftmc.gtm.warps;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.TaxiTarget;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class WarpCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        WarpManager wm = GTM.getWarpManager();
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (args.length == 0) {
            if (!s.hasPermission("warps.admin")) {
                GTM.getWarpManager().warp(player, user, GTM.getUserManager().getLoadedUser(player.getUniqueId()),
                        new TaxiTarget(GTM.getWarpManager().getRandomWarp()), 0, user.isPremium() ? 1 : 10);
                return true;
            }
            s.sendMessage(Utils.f("&c/warp list"));
            s.sendMessage(Utils.f("&c/warp set <name>"));
            s.sendMessage(Utils.f("&c/warp delete <name>"));
            s.sendMessage(Utils.f("&c/warp <warp>"));
            s.sendMessage(Utils.f("&c/warp load"));
            s.sendMessage(Utils.f("&c/warp save"));
            return true;
        }
        if (!s.hasPermission("warps.admin")) return true;
        switch (args[0].toLowerCase()) {
        case "list":
            List<Warp> list = wm.getWarps();
            s.sendMessage(Utils.f("&aWarps&7: (&a" + list.size() + "&7)"));
            if (list.isEmpty()) {
                s.sendMessage(Utils.f("&cNone!"));
                return true;
            }
            String msg = "&a" + list.get(0).getName();
            for (int i = 1; i < wm.getWarps().size(); i++)
                msg = msg + "&7, &a" + list.get(i).getName();
            s.sendMessage(Utils.f(msg));
            return true;
            case "set": {
            if (args.length != 2) {
                s.sendMessage(Utils.f("&c/warp set <name>"));
                return true;
            }

            String warpName = args[1];
            switch (warpName.toLowerCase()) {
            case "spawn":
                wm.setSpawn(player.getLocation());
                s.sendMessage(Utils.f("&aYou set the spawn!"));
                return true;
            case "tutorialspawn":
                wm.setTutorialSpawn(player.getLocation());
                s.sendMessage(Utils.f("&aYou set the tutorial spawn!"));
                return true;
            case "jail":
                wm.setJail(player.getLocation());
                s.sendMessage(Utils.f("&aYou set the jail spawn!"));
                return true;
                default:
                break;
            }
            Warp warp = wm.getWarp(warpName);
            if (warp != null) {
                warp.setLocation(player.getLocation());
                warp.setName(warpName);
                s.sendMessage(Utils.f("&7Warp &a" + warpName + "&7 was set to your current location!"));
                return true;
            }
            warp = new Warp(warpName, player.getLocation());
            wm.addWarp(warp);
            s.sendMessage(Utils.f("&7A new warp with the name &a" + warpName + "&7 was set to your current location!"));
            return true;
        }
        case "delete": {
            if (args.length != 2) {
                s.sendMessage(Utils.f("&c/warp delete <name>"));
                return true;
            }
            String warpName = args[1];
            Warp warp = wm.getWarp(warpName);
            if (warp == null) {
                s.sendMessage(Utils.f("&cThat warp does not exist!"));
                return true;
            }
            Location loc = warp.getLocation();
            wm.removeWarp(warp);
            s.sendMessage(Utils.f("&cWarp &a" + warpName + "&c at &a" + loc.getX() + "&c," + loc.getY() + "&c,"
                    + loc.getZ() + "&c was removed."));
            return true;
        }
        case "load":
            GTM.getSettings().setWarpsConfig(Utils.loadConfig("warps"));
            GTM.getWarpManager().loadWarps();
            s.sendMessage(Lang.WARP.f("&7Loaded Warps!"));
            return true;
            case "save":
            GTM.getWarpManager().saveWarps();
            s.sendMessage(Lang.WARP.f("&7Saved Warps!"));
            return true;
            default:
            if (args.length != 1) {
                s.sendMessage(Utils.f("&c/warp list"));
                s.sendMessage(Utils.f("&c/warp set <name>"));
                s.sendMessage(Utils.f("&c/warp delete <name>"));
                s.sendMessage(Utils.f("&c/warp <warp>"));
                s.sendMessage(Utils.f("&c/warp load"));
                s.sendMessage(Utils.f("&c/warp save"));
                return true;
            }
            String warpName = args[0];
            Warp warp = wm.getWarp(warpName);
            if (warp == null) {
                s.sendMessage(Utils.f("&cThat warp does not exist!"));
                return true;
            }
            s.sendMessage(Utils.f("&7Warping to warp &a" + warp.getName() + "&7!"));
            player.teleport(warp.getLocation());
            return true;
        }
    }
}
