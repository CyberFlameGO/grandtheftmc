package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.users.ViceUser;
import net.grandtheftmc.vice.utils.LocationUtil;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-08-11.
 */
public class HomeCommand extends CoreCommand<Player> {


    public HomeCommand() {
        super("home", "sets / changes predefined homes for players", "vicesethome", "vicedelhome");
    }

    @Override
    public void execute(Player player, String[] args) {
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        UserRank rank = Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRankNonTrial();
        if (args.length == 0) {
            player.sendMessage(ChatColor.GRAY + "Your " + rank.getColoredNameBold() + ChatColor.GRAY  + " and " + user.getRank().getColoredNameBold() + ChatColor.GRAY + " ranks allows you to set " + ChatColor.YELLOW + ChatColor.BOLD + ((ViceUtils.getSetHomes(rank) + ViceUtils.getSetHomes(user.getRank())) - user.getHomes().size()) + ChatColor.GRAY + " more home(s)");
            player.sendMessage(ChatColor.YELLOW + "/home set <name> " + ChatColor.GRAY + "- sets your current location to a home");
            player.sendMessage(ChatColor.YELLOW + "/home delete <name> " + ChatColor.GRAY + "- deletes one of your existing homes");
            player.sendMessage(ChatColor.YELLOW + "/home <name> " + ChatColor.GRAY + "- teleports you to the predefined location");
            player.sendMessage(ChatColor.YELLOW + "/home list " + ChatColor.GRAY + "- lists all of your current homes");
            return;
        }

        if(player.getGameMode() != GameMode.SURVIVAL) {
            player.sendMessage(Lang.VICE.f("&cYou cannot do this command unless you are in survival!"));
            return;
        }

        if (args.length == 1) {//theyre teleporting
            String id = args[0].toLowerCase();
            switch (args[0].toLowerCase()) {
                case "list": {
                    StringBuilder sb = new StringBuilder("&7Your homes: ");
                    int counter = 1;
                    for (String homeName : user.getHomes().keySet()) {
                        if (counter == user.getHomes().size()) {
                            sb.append("&e").append(homeName).append("&7.");
                        } else {
                            sb.append("&e").append(homeName).append("&7, ");
                        }
                        counter++;
                    }
                    player.sendMessage(Lang.VICE.f(sb.toString()));
                    break;
                }
                default: {
                    if (user.getHomeLocation(id) != null) {
                        if (user.isInCombat()) {
                            player.sendMessage(Lang.COMBATTAG.f("&7You cannot issue this command while in combat!"));
                            return;
                        }

                        Location destination = user.getHomeLocation(id);
                        Vice.getWorldManager().getWarpManager().warp(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user, new TaxiTarget(LocationUtil.isBlockUnsafe(destination.getWorld(), destination.getBlockX(), destination.getBlockY(), destination.getBlockZ()) ? LocationUtil.getSafeDestination(destination) : destination));
                    }

                    else {
                        player.sendMessage(Lang.VICE.f("&7You don't have a home named &e" + id));
                    }
                    break;
                }
            }
        } else if (args.length == 2) {
            String id = args[1].toLowerCase();
            switch (args[0].toLowerCase()) {

                case "add":
                case "set": {
                    if (user.getHomes().size() >= (ViceUtils.getSetHomes(rank) + ViceUtils.getSetHomes(user.getRank()))) {
                        player.sendMessage(Lang.VICE.f("&7You already have the maximum amount of homes!"));
                        return;
                    }

                    if (Core.getWorldManager().usesEditMode(player.getWorld().getName())) {
                        player.sendMessage(Lang.VICE.f("&7You cannot set your home in this world!"));
                        return;
                    }

                    user.setHomeLocation(id, player.getLocation());
                    player.sendMessage(Lang.VICE.f("&7You have created a new home named &e" + id + " &7at your current location."));
                    break;
                }

                case "remove":
                case "delete": {
                    if (!user.removeHomeLocation(id)) {
                        player.sendMessage(Lang.VICE.f("&7You don't have a home named &e" + id));
                        break;
                    }

                    player.sendMessage(Lang.VICE.f("&7You have deleted your home named &e" + id));
                    break;
                }
                default: {
                    player.sendMessage(ChatColor.RED + "Your " + rank.getColoredNameBold() + ChatColor.RED + " rank(s) allows you to set " + ChatColor.YELLOW + (ViceUtils.getSetHomes(rank) + ViceUtils.getSetHomes(user.getRank())) + ChatColor.RED + " homes");
                    player.sendMessage(ChatColor.RED + "/home set <name> - sets your current location to a home");
                    player.sendMessage(ChatColor.RED + "/home delete <name> - deletes one of your existing homes");
                    player.sendMessage(ChatColor.RED + "/home <name> - teleports to the predefined location");
                    break;
                }
            }
        } else {
            player.sendMessage(ChatColor.RED + "Your " + rank.getColoredNameBold() + ChatColor.RED + " rank(s) allows you to set " + ChatColor.YELLOW + (ViceUtils.getSetHomes(rank) + ViceUtils.getSetHomes(user.getRank())) + ChatColor.RED + " homes");
            player.sendMessage(ChatColor.RED + "/home set <name> - sets your current location to a home");
            player.sendMessage(ChatColor.RED + "/home delete <name> - deletes one of your existing homes");
            player.sendMessage(ChatColor.RED + "/home <name> - teleports to the predefined location");
        }
    }
}
