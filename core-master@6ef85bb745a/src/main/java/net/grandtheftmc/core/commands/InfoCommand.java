package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.tutorials.Help;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class InfoCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        if(args.length == 1) {
            if (args[0].equalsIgnoreCase("list")) {
                String topics = StringUtils.join(Help.getHelpData().keySet(), "&7, &c");
                player.sendMessage(Lang.GTM.f("&7Topics: &c" + topics));
                return true;
            }
            if(Help.getHelpMessage(args[0]).isPresent()) {
                player.sendMessage(Core.getAnnouncer().getHeader());
                for(String msg : Help.getHelpMessage(args[0]).get()) {
                    player.sendMessage(Utils.f(msg));
                }
                player.sendMessage(Core.getAnnouncer().getFooter());
            } else {
                player.sendMessage(Lang.HEY.f("&7Sorry! We don't have any information on that subject. \n&7Ask a staff member directly with /help <your question>"));
            }
        } else {
            player.sendMessage(Lang.HEY.f("&7You must specify what you need help with! \n&7Example: &c/how permits"));
        }
        return true;
    }
}