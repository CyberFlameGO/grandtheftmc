package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.commands.CoreCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-08-11.
 */
public class PrestigeCommand extends CoreCommand<Player> {
    public PrestigeCommand() {
        super("prestige", "gives rewards to players for continuing after regular ranks.");
    }

    @Override
    public void execute(Player player, String[] args) {
        if(args.length==0){
            player.sendMessage(ChatColor.GRAY + "/prestige confirm " + ChatColor.YELLOW + " - prestige's your player account");
            player.sendMessage(ChatColor.GRAY + "/prestige list " + ChatColor.YELLOW + " - lists all prestige tiers avaliable and costs");
            player.sendMessage("");
            return;
        }
    }
}
