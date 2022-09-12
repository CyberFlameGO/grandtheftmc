package com.j0ach1mmall3.wastedvehicles.commands;

import com.j0ach1mmall3.jlib.commands.CommandHandler;
import com.j0ach1mmall3.wastedvehicles.Main;
import com.j0ach1mmall3.wastedvehicles.api.vehicles.WastedVehicle;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by colt on 12/21/16.
 */
public class FixVehiclesCommandHandler extends CommandHandler<Main> {
    public FixVehiclesCommandHandler(Main plugin) {
        super(plugin);
    }

    @Override
    protected boolean handleCommand(CommandSender commandSender, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Players only!");
            return true;
        }
        Player sender = (Player) commandSender;
        if(sender.getWorld().getName().equalsIgnoreCase("spawn")) {
            sender.sendMessage("This cannot be used in spawn!");
            return true;
        }
        sender.getNearbyEntities(5, 5, 5).forEach(entity -> {
            if (entity.getType() != EntityType.ARMOR_STAND) return;
            ArmorStand armorStand = (ArmorStand) entity;
            if(armorStand.hasMetadata("WastedVehiclePassenger")) return;
            if (armorStand.hasMetadata("WastedVehicle")) {
                if(armorStand.getPassenger() != null) return;
                Optional<WastedVehicle> wastedVehicle = Optional.of((WastedVehicle) armorStand.getMetadata("WastedVehicle").get(0).value());
                if (wastedVehicle.isPresent()) {
                    wastedVehicle.get().onDestroy(armorStand);
                } else {
                    armorStand.setHelmet(null);
                    armorStand.remove();
                }
            } else if(armorStand.getHelmet() != null) {
                armorStand.setHelmet(null);
                armorStand.remove();
            }
        });
        for(Entity entity : Bukkit.getWorld("minesantos").getEntities()) {
            if(entity.getType() != EntityType.ARMOR_STAND) continue;
            if(entity.hasMetadata("WastedVehicle") || entity.hasMetadata("WastedVehiclePassenger")) continue;
            if(entity.getCustomName() == null || entity.getCustomName().isEmpty()) {
                entity.remove();
            }
        }
        commandSender.sendMessage(ChatColor.GREEN + "Broken vehicle(s) cleared!");
        return true;
    }
}
