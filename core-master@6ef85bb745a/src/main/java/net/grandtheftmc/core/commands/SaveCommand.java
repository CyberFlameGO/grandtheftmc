package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.events.ServerSaveEvent;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SaveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (s instanceof Player) {
            User user = Core.getUserManager().getLoadedUser(((Player) s).getUniqueId());
            if(!user.isRank(UserRank.ADMIN)) {
                return true;
            }
        }
        ServerSaveEvent saveEvent = new ServerSaveEvent();
        Bukkit.getPluginManager().callEvent(saveEvent);
        s.sendMessage(Lang.SAVE.f("&fSave Event called."));
        return true;
    }
}