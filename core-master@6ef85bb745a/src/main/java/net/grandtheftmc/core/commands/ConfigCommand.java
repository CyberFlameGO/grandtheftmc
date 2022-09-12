package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ConfigCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!s.isOp()) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("/config reload - Reload the config. Beware: this will delete changes made since the last save/restart!"));
            s.sendMessage(Utils.f("/config save - Save the config. Beware: this will delete changes made in the config since the last reload/restart"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "reload": {
                Core.getInstance().reload();
                s.sendMessage(Utils.f("Reloaded the config."));
                return true;
            }
            case "save": {
                Core.getInstance().save(false);
                s.sendMessage(Utils.f("Saved the config."));
                return true;
            }
            default:
                s.sendMessage(Utils.f("/config reload - Reload the config. Beware: this will delete changes made since the last save/restart!"));
                s.sendMessage(Utils.f("/config save - Save the config. Beware: this will delete changes made in the config since the last reload/restart"));
                return true;
        }
    }

}
