package net.grandtheftmc.Bungee.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Adam on 03/06/2017.
 */
public class HelpLog {

    //A list of users who have requested help.
    private static Map<String, Long> helpReqs = new HashMap<>();

    //1 Minutes timeout for receiving tokens.
    private static final int HELP_TOKENS_TIMEOUT = 1000 * 60 * 1;

    /**
     * Invoked from RedisListener when a player requests help and this gets forwarded to the staff chat.
     *
     * @param name
     */
    public static void requestHelp(String name) {
        helpReqs.put(name.toLowerCase(), System.currentTimeMillis());
    }

    /**
     * Invoked whenever a staff member messages a player who requested help.
     *
     * @param name The name of the player the staff member has gmsg'ed.
     * @return True if they should receive tokens. Let's set a 15 minute timeout on help requests in order to receive tokens.
     */
    public static boolean closeHelpTicket(String name) {
        name = name.toLowerCase();
        if (helpReqs.containsKey(name)) {
            long t = helpReqs.remove(name);
            long msElapsed = System.currentTimeMillis() - t;
            return msElapsed <= HELP_TOKENS_TIMEOUT;
        }

        else {
            return false;
        }
    }

    /**
     * Check whether the target of gmsg has an open help ticket.
     *
     * @param name
     * @return
     */
    public static boolean helpTicketExists(String name) {
        return name != null && helpReqs.containsKey(name.toLowerCase());
    }

}
