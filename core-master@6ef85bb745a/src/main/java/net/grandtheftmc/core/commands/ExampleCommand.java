package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.users.UserRank;
import org.bukkit.entity.Player;

/**
 * Created by Luke Bingham on 06/07/2017.
 */
public class ExampleCommand extends CoreCommand<Player> implements RankedCommand {

    // (Q)  How to register this command?
    // (A)  Simply call the constructor in onEnable
    //new ExampleCommand();

    /**
     * Construct a new command.
     */
    public ExampleCommand() {
        super(
                "example",           //Command
                "Description here", //Description
                "ex1", "ex2"          //Aliases
        );

        setNoPermissionMessage("&c&lError&f: &7You cannot use this command!");
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        if(args.length <= 0) {
            sender.sendMessage("Hey, It works!");
        }
    }

    /**
     * Get the required rank to use said command.
     *
     * @return UserRank
     */
    @Override
    public UserRank requiredRank() {
        return UserRank.DEV;
    }
}
