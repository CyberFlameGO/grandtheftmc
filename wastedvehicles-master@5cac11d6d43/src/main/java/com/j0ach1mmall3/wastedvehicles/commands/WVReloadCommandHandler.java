package com.j0ach1mmall3.wastedvehicles.commands;

import com.j0ach1mmall3.jlib.commands.CommandHandler;
import com.j0ach1mmall3.wastedvehicles.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 31/08/2016
 */
public final class WVReloadCommandHandler extends CommandHandler<Main> {
    public WVReloadCommandHandler(Main plugin) {
        super(plugin);
    }

    @Override
    protected boolean handleCommand(CommandSender commandSender, String[] strings) {
        this.plugin.reload();
        commandSender.sendMessage(ChatColor.GREEN + "Reloaded config!");
        return true;
    }
}
