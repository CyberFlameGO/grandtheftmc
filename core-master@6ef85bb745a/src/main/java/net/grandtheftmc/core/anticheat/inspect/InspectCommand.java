package net.grandtheftmc.core.anticheat.inspect;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import org.bukkit.entity.Player;

public class InspectCommand extends CoreCommand<Player> {

    /**
     * Construct a new command.
     */
    public InspectCommand() {
        super("inspect", "Inspect a hacker.");
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        User user = Core.getUserManager().getLoadedUser(sender.getUniqueId());
        if(!user.isStaff()) {
            sender.sendMessage(Lang.NOPERM.s());
            return;
        }
        sender.performCommand("gm3staff true");
        sender.performCommand("tp " + args[0]);
    }
}
