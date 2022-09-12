package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class RulesCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.toString());
            return true;
        }
        Player player = (Player) s;
        List<String> rules = new ArrayList<>();
        Core.getSettings().getRulesConfig().getStringList("rules").forEach(rule -> rules.add(ChatColor.translateAlternateColorCodes('&', rule)));
        player.sendMessage(rules.toArray(new String[rules.size()]));
        return true;
    }
}