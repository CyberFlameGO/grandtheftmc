package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.Pref;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SocialSpyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Utils.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        UUID uuid = player.getUniqueId();
        User u = Core.getUserManager().getLoadedUser(uuid);
        if (!Pref.SOCIALSPY.isEnabled(player, u, Core.getSettings().getType())) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            if (u.getPref(Pref.SOCIALSPY))
                u.setPref(player, Pref.SOCIALSPY, false);
            return true;
        }
        boolean mode;
        if (args.length == 1) {
            if(Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                u = Core.getUserManager().getLoadedUser(target.getUniqueId());
                u.setPref(target, Pref.SOCIALSPY, false);
                player.sendMessage(Lang.SOCIALSPY.f("&7You have disabled the socialspy of " + u.getColoredName(target)));
            } else {
                player.sendMessage(Lang.SOCIALSPY.f("&7Invalid arguments! Use /socialspy (to toggle it for yourself) or /socialspy <player>"));
            }
        } else {
            mode = !u.getPref(Pref.SOCIALSPY);
            u.setPref(player, Pref.SOCIALSPY, mode);
            s.sendMessage(Lang.SOCIALSPY.f("&7You " + (mode ? "enabled" : "disabled") + " socialspy."));
        }
        return true;
    }
}