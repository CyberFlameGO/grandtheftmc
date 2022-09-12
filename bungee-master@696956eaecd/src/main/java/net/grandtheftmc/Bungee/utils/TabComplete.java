package net.grandtheftmc.Bungee.utils;

import com.google.common.collect.ImmutableSet;
import net.grandtheftmc.Bungee.Bungee;
import net.md_5.bungee.api.CommandSender;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Adam on 02/06/2017.
 */
public class TabComplete {

    /**
     * Match a list of players to a search string accross the Redis network.
     * @param sender Who executed the command.
     * @param args Arguments of player to search
     * @return A Set<String> of potential matches.
     */
    public static Set<String> onTabComplete(CommandSender sender, String[] args){
        if (args.length > 2 || args.length == 0)
            return ImmutableSet.of();

        Set<String> matches = new HashSet<>();

        if (args.length == 1) {
            String search = args[0].toLowerCase();

            //We search all redis online players for the autocomplete
            for (String name : Bungee.getRedisManager().getRedisAPI().getHumanPlayersOnline()) {
                if (name.toLowerCase().startsWith(search)) {
                    matches.add(name);
                }
            }
        }

        return matches;
    }
}
