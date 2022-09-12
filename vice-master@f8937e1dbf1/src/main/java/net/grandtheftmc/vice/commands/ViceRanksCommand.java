package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.users.ViceRank;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ViceRanksCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        player.sendMessage(Lang.RANKS.s());
        for (ViceRank rank : ViceRank.values()) {
            player.sendMessage(Utils.f(rank.getColoredNameBold() + " &7costs &a&l$" + rank.getPrice()));
        }
        return true;
    }
}
