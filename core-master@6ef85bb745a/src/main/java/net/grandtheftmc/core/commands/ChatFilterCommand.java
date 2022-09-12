package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.handlers.chat.ChatManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatFilterCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        if (!s.hasPermission("chatfilter.admin")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&7/chatfilter &areload"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                ChatManager.getSettings().setConfig(Utils.loadConfig("chatsettings"));
                ChatManager.getSettings().loadSettings();
                s.sendMessage(Utils.f("&aConfig reloaded"));
                return true;
            }
            default:
                s.sendMessage(Utils.f("&7/chatfilter &areload"));
                return true;
        }
    }

}