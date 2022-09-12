package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.vice.ViceUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.util.Map;

public class TopKillersCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.VICE.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player)s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        Map<String, Integer> topKillers = ViceUtils.sortByValue(ViceUtils.getTopKillers(5));
        Iterator iterator = topKillers.entrySet().iterator();
        int i = 1;
        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            player.sendMessage(Utils.f("&7#" + i++ + " &a" + pair.getKey() + " &7- " + pair.getValue() + " &7kills"));
            iterator.remove();
        }
        return true;
    }
}