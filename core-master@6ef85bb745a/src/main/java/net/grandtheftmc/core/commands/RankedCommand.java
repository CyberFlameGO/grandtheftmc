package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.users.UserRank;

/**
 * Created by Luke Bingham on 06/07/2017.
 */
public interface RankedCommand {

    /**
     * Get the required rank to use said command.
     *
     * @return UserRank
     */
    UserRank requiredRank();
}
