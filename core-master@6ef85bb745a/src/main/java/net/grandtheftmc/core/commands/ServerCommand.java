package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.servers.menu.GTMTranzitMenu;
import net.grandtheftmc.core.servers.menu.TranzitMenu;
import org.bukkit.entity.Player;

/**
 * Created by Luke Bingham on 20/08/2017.
 */
public class ServerCommand extends CoreCommand<Player> {

    /**
     * Construct a new command.
     */
    public ServerCommand() {
        super("server", "Travel to different servers.", "serv");
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        MenuManager.openMenu(sender, "serverwarper");
//        new TranzitMenu(new GTMTranzitMenu()).openInventory(sender);
    }
}
