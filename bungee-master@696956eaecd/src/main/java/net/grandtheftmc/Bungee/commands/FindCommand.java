package net.grandtheftmc.Bungee.commands;

import net.grandtheftmc.Bungee.Bungee;
import net.grandtheftmc.Bungee.Lang;
import net.grandtheftmc.Bungee.Utils;
import net.grandtheftmc.Bungee.utils.TabComplete;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.plugin.Command;

public class FindCommand extends Command {

    public FindCommand() {
        super("find");
    }

    @Override
    public void execute(CommandSender s, String[] args) {
        if (args.length != 1) {
            s.sendMessage(Utils.ft("&c/find <player>"));
            return;
        }

        String target = args[0];
        if (Bungee.getRedisManager().isPlayerOnline(target) && !Utils.isStaff(target)) {
            ServerInfo serverInfo = Bungee.getRedisManager().getRedisAPI().getServerFor(Bungee.getRedisManager().getUUIDFromName(target));
            s.sendMessage(Lang.GTM.ft("&a" + target + " &7was found on server &a" + serverInfo.getName().toUpperCase()));
        }
        else {
            s.sendMessage(Lang.GTM.ft("&a" + target + " &7is not online!"));
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        return TabComplete.onTabComplete(sender, args);
    }
}