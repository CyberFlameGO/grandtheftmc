package com.j0ach1mmall3.wastedvehicles.commands;

import com.j0ach1mmall3.jlib.commands.CommandHandler;
import com.j0ach1mmall3.jlib.methods.General;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author j0ach1mmall3 (business.j0ach1mmall3@gmail.com)
 * @since 27/06/2016
 */
public final class GiveVehicleCommandHandler extends CommandHandler<Main> {
    public GiveVehicleCommandHandler(Main plugin) {
        super(plugin);
    }

    @Override
    protected boolean handleCommand(CommandSender commandSender, String[] strings) {
        if(strings.length < 2) {
            commandSender.sendMessage(ChatColor.RED + "Usage: /givevehicle <identifier> <player>");
            return true;
        }

        Optional<VehicleProperties> vehicle = this.plugin.getBabies().getVehicleProperties().stream().filter(v -> v.getIdentifier().equalsIgnoreCase(strings[0])).findFirst();
        if(!vehicle.isPresent()) {
            commandSender.sendMessage(ChatColor.RED + "Unknown vehicle " + strings[0]);
            return true;
        }

        Player p = General.getPlayerByName(strings[1], false);
        if(p == null) {
            commandSender.sendMessage(ChatColor.RED + "Unknown player " + strings[1]);
            return true;
        }

        p.getInventory().addItem(vehicle.get().getItem());
        commandSender.sendMessage(ChatColor.GREEN + "Successfully gave " + vehicle.get().getIdentifier() + " to " + p.getName());
        return true;
    }
}
