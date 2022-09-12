package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class SpectatorCommand implements CommandExecutor {
    private static final List<String> ACTIVE_STAFF = new ArrayList<>();

    public static List<String> getActiveStaff() {
        return ACTIVE_STAFF;
    }

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player sender = (Player) s;
        User coreSender = Core.getUserManager().getLoadedUser(sender.getUniqueId());
        if (!coreSender.isRank(UserRank.ADMIN)) {
            sender.sendMessage(Lang.VICE.f("&7Permission denied!"));
            return true;
        }
        if (ACTIVE_STAFF.contains(sender.getName())) {
            ACTIVE_STAFF.remove(sender.getName());
            sender.sendMessage(Lang.VICE.f("&bSpectator Mode disabled!"));
            sender.setGameMode(GameMode.SURVIVAL);
            sender.teleport(Bukkit.getWorld("spawn").getSpawnLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            sender.setFlySpeed(0.1F);
        } else {
            ACTIVE_STAFF.add(sender.getName());
            sender.sendMessage(Lang.VICE.f("&bSpectator Mode enabled!"));
            sender.teleport(this.getMapLocation(), PlayerTeleportEvent.TeleportCause.COMMAND);
            sender.setGameMode(GameMode.SPECTATOR);
        }
        return true;
    }

    private Location getMapLocation() {
        Location loc = Bukkit.getWorlds().get(0).getSpawnLocation();
        if (Bukkit.getWorld("minesantos") == null) {
            return loc;
        }
        loc.setWorld(Bukkit.getWorld("minesantos"));
        loc.setX(-133.59);
        loc.setY(96.000000);
        loc.setZ(244.431);
        return loc;
    }
}