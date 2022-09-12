package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class KillCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (args.length == 0 || "suicide".equalsIgnoreCase(lbl)) {
            if (!(s instanceof Player)) {
                s.sendMessage(Lang.VICE.f("&cYou are not a player!"));
                return true;
            }
            Player player = (Player) s;
            if (Vice.getUserManager().getLoadedUser(player.getUniqueId()).isArrested()) {
                player.sendMessage(Lang.JAIL.f("&7You can't kill yourself in jail!"));
                return true;
            }
            player.getActivePotionEffects().forEach(effect -> {
                player.removePotionEffect(effect.getType());
            });
            player.damage(player.getHealth());
            return true;
        }
        if (!s.hasPermission("command.kill")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length != 1) {
            s.sendMessage(Utils.f("&c/kill <player>"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            s.sendMessage(Lang.VICE.f("&7That player is not online!"));
            return true;
        }
        target.getActivePotionEffects().forEach(effect -> {
            target.removePotionEffect(effect.getType());
        });
        target.damage(target.getHealth());
        s.sendMessage(Lang.VICE.f("&7You killed &a"
                + Core.getUserManager().getLoadedUser(target.getUniqueId()).getColoredName(target) + "&7!"));
        return true;
    }
}

