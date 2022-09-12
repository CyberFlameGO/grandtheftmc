package net.grandtheftmc.Bungee.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Adam on 05/06/2017.
 */
public class RequestRateLimiter {

    /*
        This class serves to limit requests made by staff members to the database.

        No more than 1 execution every second.

        Still allows frequent use, but without spamming.

        Only applies to the /seen,  /alt commands
     */

    //Store the issuing player and the time of the last command
    private static Map<UUID, Long> lastReq = new HashMap<>();

    /**
     * Attempt to make a request to execute a database related command.
     * @param u The UUID of the player executing the command.
     * @return A boolean representing whether they have been granted use of said command or not.
     */
    public static boolean requestCmd(UUID u) {
        long t = System.currentTimeMillis();

        if (!lastReq.containsKey(u)) {
            lastReq.put(u, t);
            return true;
        }

        long last = lastReq.get(u);
        if (t - last >= 1000) {
            //if at least 1 second has passed allow the command.
            lastReq.put(u, t);
            return true;
        }

        return false;
    }

}
