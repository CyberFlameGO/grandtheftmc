package net.grandtheftmc.core.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.data.CompactLoc;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;

public class Move implements Listener {

    //Used to compare locations for AFKers
    private static Map<UUID, CompactLoc> playerLocations = new HashMap<>();

    //Remove player the AFK tracker.
    public static void logout(UUID u) {
        playerLocations.remove(u);
    }

    public Move(){
        //Run every 30 seconds
        Bukkit.getScheduler().runTaskTimer(Core.getInstance(), () -> {
            for (Player p : Bukkit.getOnlinePlayers()) {
                CompactLoc last = playerLocations.get(p.getUniqueId());

                if (last == null) {
                    //Register them
                    playerLocations.put(p.getUniqueId(), new CompactLoc(p.getLocation()));
                } else {
                    //Compare the difference
                    CompactLoc current = new CompactLoc(p.getLocation());

                    if (current.differs(last)) {
                        //we can try to update the afk list as they have moved.
                        User u = Core.getUserManager().getLoadedUser(p.getUniqueId());
                        if(!u.hasTrialRank() && !u.isRank(UserRank.VIP)){
                            Core.getAntiAFK().refreshAfk(p);
                        }
                    }

                    playerLocations.put(p.getUniqueId(), current);
                }
            }
        }, 0L, 20 * 30);
    }

    //Store a list of players opening crates who shouldn't be affected
    private static Set<UUID> excludeCrate = new HashSet<>();
    //Store a list of active crate openings to scan, use a list because it's quicker to iterate
    private static List<Location> activeCrates = new ArrayList<>();

    public static void setOpening(UUID u, Location l) {
        excludeCrate.add(u);
        activeCrates.add(l);
        activeWorldHashcodes.add(l.getWorld().hashCode());
        //Bukkit.broadcastMessage(ChatColor.GREEN + Bukkit.getPlayer(u).getName() + " opening crate, added to exclusion list.");
    }

    public static void stopOpening(UUID u, Location l) {
        excludeCrate.remove(u);
        activeCrates.remove(l);

        //do we need to remove it from the active hashset?
        for (Location acl : activeCrates) {
            if (acl.getWorld().hashCode() == l.getWorld().hashCode()) {
                //there is stil lan active crate with this world stored so we can't remove it
                return;
            }
        }

        activeWorldHashcodes.remove(l.getWorld().hashCode());
        //Bukkit.broadcastMessage(ChatColor.RED + Bukkit.getPlayer(u).getName() + " STOPPED opening crate, REMOVED from exclusion list.");
    }

    //All active crates are present in this hashset
    private static Set<Integer> activeWorldHashcodes = new HashSet<>();

    @EventHandler
    public void onMoveOptimised(PlayerMoveEvent e) {
        Location to = e.getTo(), from = e.getFrom();

        if (to.getBlockX() == from.getBlockX() && to.getBlockY() == from.getBlockY() && to.getBlockZ() == from.getBlockZ()) {
            //Player has not moved block
            return;
        }

        Player player = e.getPlayer();
        //Hashmap lookup so this is fast enough.
        User u = Core.getUserManager().getLoadedUser(player.getUniqueId());

        //Better ways to refresh afk than on move, this has been moved to a scheduler task above ^.
        //if (!u.hasTrialRank() && !u.isRank(UserRank.VIP)) Core.getAntiAFK().refreshAfk(player);

        if (u != null && u.isInTutorial()) {
            e.setTo(e.getFrom());
            //Don't allow them to move.
            return;
        }

        //player.sendMessage("HC (spawn/curWorld) = (" + spawnWorldHashCode + "/" + player.getWorld().hashCode() + ")");

        if (activeWorldHashcodes.isEmpty() || !activeWorldHashcodes.contains(player.getWorld().hashCode()) || activeCrates.isEmpty()) {
            //Comparing integer hashcodes is faster than strings.
            //Also return if no crates are being opened.
            //Crates only opened in spawn world, so if they dont match exit
            return;
        }

        //Crates must not be empty, a crate is being opened. Check if we can exclude the player first
        if (excludeCrate.contains(player.getUniqueId())) {
            //player.sendMessage("You are on the exclusion list, not checked!");
            return;
        }

        //Otherwise do a scan
        for (Location l : activeCrates) {
            if (player.getLocation().getWorld().hashCode() != l.getWorld().hashCode()) {
                //Only compare locations in the same world.
                //player.sendMessage("Not in same world as crate.");
                continue;
            }

            /*
                Now remember, that for most players they won't actually be near the crates that are being opened.

                So we can speed up the comparison by doing some quick eliminations as calculating the distance is expensive.

                Do quick checks on the x, y, z to determine if the player is nearby.
             */
            if (Math.abs(l.getBlockX() - player.getLocation().getBlockX()) >= 6 ||
                    Math.abs(l.getBlockZ() - player.getLocation().getBlockZ()) >= 6 ||
                    Math.abs(l.getBlockY() - player.getLocation().getBlockY()) >= 6) {
                //Player can't be within 5 units of the crate if distance between two is >= 5.
                //Check y first as this is the least likely to differ.
                //player.sendMessage("Outside of active crate range.");
                return;
            }

            if (player.getLocation().distance(l) < 7) {
                //They must be in range, so lets push them back
                //Removed .clone() it's unnecessary.
                //player.sendMessage("THROWING AWAY");
                Vector ce = l.toVector();
                Vector toThrow = player.getLocation().toVector();
                double x = toThrow.getX() - ce.getX();
                double z = toThrow.getZ() - ce.getZ();
                //Multiplication by 1 achieves nothing so remove that also.
                Vector v = new Vector(x, 1, z).normalize();
                player.setVelocity(v);
            }

        }

    }
}
