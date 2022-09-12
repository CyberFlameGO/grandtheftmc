package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.drugs.DrugDealer;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DrugDealerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission("drugdealer.command")) {
            sender.sendMessage(Lang.DRUGS + "" + ChatColor.RED + "Error: You do not have permission to execute this command.");
            return false;
        }
        if (args.length < 1) {
            sender.sendMessage(ChatColor.RED + "/drugdealer add - sets a drugdealer location");
            sender.sendMessage(ChatColor.RED + "/drugdealer tp - teleports you to the drugdealer");
            sender.sendMessage(ChatColor.RED + "/drugdealer next - teleports the drug dealer to a new location");
            sender.sendMessage(ChatColor.RED + "/drugdealer restock - restocks the drug dealer");
            sender.sendMessage(ChatColor.RED + "/drugdealer remove - removes the nearest dealer location from the drug dealer");
            sender.sendMessage(ChatColor.RED + "/drugdealer save - saves the drug dealer locations to config");
            sender.sendMessage(ChatColor.RED + "/drugdealer load - loads the drug dealer locations from config");

            return false;
        }
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.DRUGS + "" + ChatColor.GRAY + "Console cannot execute this command.");
            return false;
        }
        Player player = (Player) sender;
        DrugDealer drugDealer = GTM.getDrugManager().getDrugDealer();
        switch (args[0]) {
            case "add": {
                player.sendMessage(Lang.DRUGS.f("&7You have added a location for the dealer to spawn!"));
                drugDealer.addDealerLoc(player.getLocation());
                break;
            }
            case "tp": {
                player.teleport(GTM.getDrugManager().getDrugDealer().dealerStand());
                player.sendMessage(Lang.DRUGS.f("&7You have been teleported to the current drug dealer location."));
                break;
            }
            case "next": {
                Optional<Location> randomLoc = drugDealer.getRandomLoc();
                randomLoc.ifPresent(location -> drugDealer.setLocation(location));
                player.sendMessage(Lang.DRUGS.f("&7You have changed the location of the drug dealer."));
                break;
            }
            case "restock": {
                drugDealer.rerollStock();
                player.sendMessage(Lang.DRUGS.f("&7You have restocked the drug dealer."));
                break;
            }
            case "remove": {
                Location nearestLocation = null;
                if (drugDealer.getDealerLocations().isEmpty()) {
                    player.sendMessage(Lang.DRUGS.f("&7No drug dealer locations exist"));
                    return true;
                }
                for (Location location : drugDealer.getDealerLocations()) {
                    if (player.getWorld() != location.getWorld()) continue;
                    if (nearestLocation == null ||
                            location.distance(player.getLocation()) < nearestLocation.distance(player.getLocation()))
                        nearestLocation = location;
                }
                drugDealer.getDealerLocations().remove(nearestLocation);
                player.sendMessage(Lang.DRUGS.f("&7Drug Dealer location nearest to you has been removed."));
                break;
            }
            case "save": {
                drugDealer.saveLocations();
                player.sendMessage(Lang.DRUGS.f("&7You have saved the locations / items for the drug dealer to file"));
                break;
            }
            case "load": {
                drugDealer.loadLocations();
                drugDealer.loadDealerItems();
                player.sendMessage(Lang.DRUGS.f("&7You have loaded the locations / items for the drug dealer from file"));
                break;
            }
            default: {
                return false;
            }
        }
        return true;
    }
}
