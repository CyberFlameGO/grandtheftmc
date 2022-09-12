package net.grandtheftmc.gtm.commands;

import com.j0ach1mmall3.wastedvehicles.api.VehicleProperties;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.database.BaseDatabase;
import net.grandtheftmc.core.util.ServerUtil;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

/**
 * Created by Liam on 24/09/2016.
 */
public class VehicleCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (!s.hasPermission("command.vehicle")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Lang.VEHICLES.f("&7/vehicle list [player]"));
            s.sendMessage(Lang.VEHICLES.f("&7/vehicle give <player> <name>"));
            s.sendMessage(Lang.VEHICLES.f("&7/vehicle remove <player> <name>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list":
                if (args.length > 2) {
                    s.sendMessage(Utils.f("&c/vehicle list <type>"));
                    return true;
                }
                List<VehicleProperties> vehicles = GTM.getWastedVehicles().getBabies().getVehicleProperties();
                if (args.length == 2) {
                    Player player = Bukkit.getPlayer(args[1]);
                    if (player == null) {
                        s.sendMessage(Lang.VEHICLES.f("&7That player is not online!"));
                        return true;
                    }
                    GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                    vehicles = user.getVehicleProperties();
                    if (vehicles.isEmpty()) {
                        s.sendMessage(Lang.VEHICLES.f("&7That player has no vehicles!"));
                        return true;
                    }
                    String msg = "";
                    for (VehicleProperties v : vehicles)
                        if (player == null || user.hasVehicle(v.getIdentifier()))
                            msg += "&c" + v.getIdentifier() + "&7, ";
                    if (msg.endsWith("&7, "))
                        msg.substring(0, msg.length() - 4);
                    s.sendMessage(Lang.VEHICLES.f("&7List of vehicles of player " + player.getName() + "&7:"));
                    s.sendMessage(Utils.f(msg));
                    return true;
                } else if (vehicles.isEmpty()) {
                    s.sendMessage(Lang.VEHICLES.f("&7There are no vehicles!"));
                    return true;
                }
                String msg = "";
                for (VehicleProperties v : vehicles)
                    msg += "&c" + v.getIdentifier() + "&7, ";
                if (msg.endsWith("&7, "))
                    msg.substring(0, msg.length() - 4);
                s.sendMessage(Lang.VEHICLES.f("&7List of vehicles:"));
                s.sendMessage(Utils.f(msg));
                return true;
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/vehicle give <player> <name>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(args[2]);
                if (opt == null || !opt.isPresent() || opt.get() == null) {
                    s.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                    return true;
                }
                VehicleProperties vehicle = opt.get();
                if (player == null) {
//                    Core.sql.updateAsyncLater("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=true where name='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=true where name='" + args[1] + "';"));
                    s.sendMessage(Lang.VEHICLES.f("&7That player is not online, so his vehicles have been updated directly in the database!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.giveVehiclePerm(player, vehicle);
                s.sendMessage(Lang.VEHICLES.f("&7You gave vehicle &a" + vehicle.getIdentifier() + "&7 to player " + player.getName() + "&7!"));
                return true;
            }
            case "remove":
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/vehicle remove <player> <name>"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                Optional<VehicleProperties> opt = GTM.getWastedVehicles().getVehicle(args[2]);
                if (opt == null || !opt.isPresent() || opt.get() == null) {
                    s.sendMessage(Lang.VEHICLES.f("&7That vehicle does not exist!"));
                    return true;
                }
                VehicleProperties vehicle = opt.get();
                if (player == null) {
//                    Core.sql.updateAsyncLater("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=false where name='" + args[1] + "';");
                    ServerUtil.runTaskAsync(() -> BaseDatabase.runCustomQuery("update " + Core.name() + " set `" + vehicle.getIdentifier().toLowerCase() + "`=false where name='" + args[1] + "';"));
                    s.sendMessage(Lang.VEHICLES.f("&7That player is not online, so his vehicles have been updated directly in the database!"));
                    return true;
                }
                GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
                user.removeVehiclePerm(player, vehicle);
                s.sendMessage(Lang.VEHICLES.f("&7You removed vehicle &a" + vehicle.getIdentifier() + "&7 from player " + player.getName() + "&7!"));
                return true;
            default:
                s.sendMessage(Lang.VEHICLES.f("&7/vehicle list [player]"));
                s.sendMessage(Lang.VEHICLES.f("&7/vehicle give <player> <name>"));
                s.sendMessage(Lang.VEHICLES.f("&7/vehicle remove <player> <name>"));
                return true;
        }

    }
}
