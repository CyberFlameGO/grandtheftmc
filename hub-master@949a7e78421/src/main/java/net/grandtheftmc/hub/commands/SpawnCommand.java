package net.grandtheftmc.hub.commands;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.hub.Hub;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
		if (!(s instanceof Player)) {
			s.sendMessage(Utils.f("&cYou are not a player!"));
			return true;
		}
		Player player = (Player) s;
        if(args.length == 1) {
            if(args[0].equalsIgnoreCase("add")) {
                Hub.getInstance().getSpawnPoints().add(player.getLocation());
                player.sendMessage(player.getLocation().toString() + " added");
            } else if(args[0].equalsIgnoreCase("remove")) {
                Location loc = getNearestLocation(player.getLocation());
                Hub.getInstance().getSpawnPoints().remove(loc);
                player.sendMessage(loc.toString() + " removed");
            }
        } else {
            player.teleport(Hub.getInstance().getSpawn());
            player.sendMessage(Utils.f("&aYou were teleported to spawn!"));
        }
		return true;
	}

    public static Location getNearestLocation(Location location) {
        Location nearestLocation = null;
        for(Location point : Hub.getInstance().getSpawnPoints()) {
            if(nearestLocation == null || point.distance(location) < point.distance(location)) {
                nearestLocation = point;
            }
        }
        return nearestLocation;
    }

}
