package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.menus.MenuManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Liam on 21/09/2016.
 */
public class RewardsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        MenuManager.openMenu((Player) s, "rewards");
        return true;
    }
}
