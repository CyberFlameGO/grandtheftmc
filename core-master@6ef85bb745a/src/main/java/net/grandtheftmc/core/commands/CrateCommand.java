package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.voting.crates.Crate;
import net.grandtheftmc.core.voting.crates.listeners.CrateOpenListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Created by Timothy Lampen on 2017-04-24.
 */
public class CrateCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.CRATES.f("&7Error: You have to be a player to execute this command!"));
            return false;
        }

        Player player = (Player) sender;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if(!user.getUserRank().isHigherThan(UserRank.ADMIN)) {
            player.sendMessage(Lang.VOTE.f("&cYou do not have access to this command."));
        }
        if(args.length==0 || args[0].equalsIgnoreCase("help")) {
            if(user.getUserRank().isHigherThan(UserRank.ADMIN)){
                sender.sendMessage(Utils.f("&7/crate add <stars> - Creates a crate at your feet location with the specified amount of stars."));
                sender.sendMessage(Utils.f("&7/crate remove - Removes the crate that you are currently looking towards. &4/&7Uses your location so be very close to the crate&4/"));
                sender.sendMessage(Utils.f("&7/crate load - Loads the rewards and crates from the config file."));
                sender.sendMessage(Utils.f("&7/crate save - Saves the rewards and crates to the config file."));
                sender.sendMessage(Utils.f("&7/crate open <stars> - Opens a specific crate at spawn."));
            }
            else {
                sender.sendMessage(Utils.f("&7/crate open <stars> - Opens a specific crate at spawn."));
            }
            return true;

        }
        if (!user.getUserRank().isHigherThan(UserRank.ADMIN) && !args[0].equalsIgnoreCase("open")) {
            sender.sendMessage(Lang.CRATES.f("&7You do not have permission to execute this command."));
            return false;
        }
        switch (args[0].toLowerCase()) {
            case "add": {
                if (args.length != 2) {
                    sender.sendMessage(Lang.CRATES.f("&7/crate add <stars>"));
                    return false;
                }
                if (!Utils.isInteger(args[1])) {
                    sender.sendMessage(Lang.CRATES.f("&7Error: The argument is not an integer!"));
                    return false;
                }
                int stars = Integer.parseInt(args[1]);
                player.sendMessage(Lang.CRATES.f("&7You have created a new vote crate with " + stars + " stars!"));
                Core.getCrateManager().addCrate(new Crate(player.getLocation(), stars));
                return true;
            }
            case "load":
                Core.getSettings().setCratesConfig(Utils.loadConfig("crates"));
                Core.getSettings().setCrateRewardsConfig(Utils.loadConfig("craterewards"));
                Core.getCrateManager().loadCrates();
                Core.getCrateManager().loadRewards();
                player.sendMessage(Lang.CRATES.f("&7You have loaded crates & crate rewards."));
                return true;
            case "save":
                Core.getCrateManager().save(false);
                player.sendMessage(Lang.CRATES.f("&7You have saved crates."));
                return true;
            case "remove":
                int amountRemoved = 0;
                Crate crate;
                for (Entity e : player.getNearbyEntities(1, 1, 1)) {
                    if (e.getType() == EntityType.ARMOR_STAND) {
                        if (Core.getCrateManager().getCrate((LivingEntity) e).isPresent()) {
                            crate = Core.getCrateManager().getCrate((LivingEntity) e).get();
                            Core.getCrateManager().removeCrate(crate);
                            amountRemoved += 1;
                        } else {
                            ((LivingEntity) e).setHealth(0);
                            e.remove();
                        }
                    }
                }
                player.sendMessage(Lang.CRATES.f("&7You have removed " + amountRemoved + " crates."));
                return true;
            case "open": {
                if(args.length != 2) {
                    sender.sendMessage(Lang.CRATES.f("&7/crate open <stars>"));
                    return false;
                }
                if(!Utils.isInteger(args[1])) {
                    sender.sendMessage(Lang.CRATES.f("&7Error: The argument is not an integer!"));
                    return false;
                }
                int stars = Integer.parseInt(args[1]);
                Optional<Crate> optCrate = Core.getCrateManager().getCrates().stream().filter(c -> c.getCrateStars().getStars()==stars).findFirst();
                if(!optCrate.isPresent()) {
                    sender.sendMessage(Lang.CRATES.f("&7Unable to find crate with that amount of stars!"));
                    return false;
                }
                player.openInventory(CrateOpenListener.generateCratePreview(user, optCrate.get().getCrateStars()));//i know this is horrible but its only suppose to be temp.
                user.setSelectedCrate(optCrate.get());
                return true;
            }
            default:
                sender.sendMessage(Utils.f("&7/crate open <stars> - Creates a crate at your feet location with the specified amount of stars."));
                sender.sendMessage(Utils.f("&7/crate add <stars> - Creates a crate at your feet location with the specified amount of stars."));
                sender.sendMessage(Utils.f("&7/crate remove - Removes the crate that you are currently looking towards."));
                sender.sendMessage(Utils.f("&7/crate load - Loads the rewards and crates from the config file."));
                sender.sendMessage(Utils.f("&7/crate save - Saves the rewards and crates to the config file."));
                return false;
        }
    }
}
