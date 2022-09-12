package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player sender = (Player) s;
        User coreSender = Core.getUserManager().getLoadedUser(sender.getUniqueId());
        if (!coreSender.isRank(UserRank.HELPOP) || !coreSender.isRank(UserRank.ADMIN) && !SpectatorCommand.getActiveStaff().contains(sender.getName())) {
            sender.sendMessage(Lang.VICE.f("&7Permission denied!"));
            return true;
        }
        if (args.length == 1) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(Lang.VICE.f("&7That player is not online!"));
                return true;
            }
            if (Core.getUserManager().getLoadedUser(target.getUniqueId()).isRank(UserRank.ADMIN) && !coreSender.isRank(UserRank.ADMIN)) {
                sender.sendMessage(Lang.VICE.f("&7You may not teleport to that player."));
                return true;
            }
            sender.teleport(target.getLocation());
            sender.sendMessage(Lang.VICE.f("&7You have been teleported to" + target.getDisplayName() + "&7!"));
        } else if (args.length == 2) {
            if (!coreSender.isRank(UserRank.ADMIN)) {
                sender.sendMessage(Lang.VICE.f("&7Permission denied!"));
                return true;
            }
            Player targetFrom = Bukkit.getPlayer(args[0]);
            Player targetTo = Bukkit.getPlayer(args[1]);
            if (targetFrom == null || targetTo == null) {
                sender.sendMessage(Lang.VICE.f("&7Player(s) not found!"));
                return true;
            }
            if (Core.getUserManager().getLoadedUser(targetFrom.getUniqueId()).isRank(UserRank.ADMIN) ||
                    Core.getUserManager().getLoadedUser(targetTo.getUniqueId()).isRank(UserRank.ADMIN)) {
                sender.sendMessage(Lang.VICE.f("&7You may not teleport that player."));
                return true;
            }
            targetFrom.teleport(targetTo.getLocation());
            sender.sendMessage(Lang.VICE.f("&7You teleported " + targetFrom.getDisplayName() +
                    " to " + targetTo.getDisplayName() + '!'));
        }
        else if(args.length==3){
            int[] coords = new int[3];
            for (int i = 0; i < 3; i++) {
                try{
                    coords[i] = Integer.parseInt(args[i]);
                }catch (NumberFormatException nfe){
                    sender.sendMessage(Lang.VICE.f("&7You did not input a number!"));
                    return false;
                }
            }

            sender.teleport(new Location(sender.getWorld(), coords[0], coords[1], coords[2]));
            sender.sendMessage(Lang.VICE.f("&7You have been teleported to the selected coordinates."));
        }
        else {
            sender.sendMessage(Lang.VICE.f("&7Usage:"));
            sender.sendMessage(Utils.f("&a/teleport <player>"));
            sender.sendMessage(Utils.f("&a/teleport <playerfrom> <playerto>"));
            sender.sendMessage(Utils.f("&a/teleport <x> <y> <z>"));
        }
        return true;
    }
}