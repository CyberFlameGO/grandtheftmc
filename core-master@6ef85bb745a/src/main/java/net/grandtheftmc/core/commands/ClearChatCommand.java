package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.IconConverter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class ClearChatCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
        if (!(s instanceof Player)) {
            for (int i = 0; i < 300; i++)
                onlinePlayers
                        .stream()
                        .filter(target -> !target.hasPermission("clearchat.staff"))
                        .forEach(target -> target.sendMessage(""));
            s.sendMessage(Lang.GTM.f("&7Chat Cleared!"));
            return true;
        }
        Player player = (Player) s;
        if (!player.hasPermission("clearchat.staff")) {
            player.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        for (int i = 0; i < 300; i++)
            onlinePlayers
                    .stream()
                    .filter(target -> !target.hasPermission("clearchat.staff"))
                    .forEach(target -> target.sendMessage(""));
        Bukkit.broadcastMessage(Utils.f(IconConverter.convertInput("&7Chat has been cleared, sorry for the inconvenience &r:pig:")));
        return true;
    }
}