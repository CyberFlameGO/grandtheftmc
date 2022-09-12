package net.grandtheftmc.core.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.Utils;
import net.grandtheftmc.core.users.eventtag.EventTag;
import net.grandtheftmc.core.users.UserDAO;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.users.eventtag.EventTagDAO;
import net.grandtheftmc.core.users.eventtag.TagVisibility;
import net.grandtheftmc.core.util.ServerUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;

/**
 * Created by Timothy Lampen on 1/4/2018.
 */
public class EventTagCommand extends CoreCommand<CommandSender> {
    public EventTagCommand() {
        super("eventtag", "Commands dealing with the cosmetic tag system.", "tag", "tags");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player)sender;
            if(!Core.getUserManager().getLoadedUser(player.getUniqueId()).getUserRank().isHigherThan(UserRank.ADMIN))
                return;
        }

        if(args.length==0){
            sender.sendMessage(Utils.f("&c/eventtag setvis <eventtag> <visibility> - &7Sets the visibility of the specified tag. 0 = everyone, 1 = people who have unlocked it, 2 = no one."));
            sender.sendMessage(Utils.f("&c/eventtag refresh - &7Refreshes the visibility cache."));
            sender.sendMessage(Utils.f("&c/eventtag give <player> <eventtag> - &7Gives the specified player the specified tag."));
            sender.sendMessage(Utils.f("&c/eventtag remove <player> <eventtag> - &7Removes specified tag from the player."));
            sender.sendMessage(Utils.f("&c/eventtag list <player> - &7Lists all the eventtags that the specified player has"));
            sender.sendMessage(Utils.f("&c/eventtag avaliable - &7Lists all avaliable event tags."));
            sender.sendMessage(Utils.f("&4&lNote that players may have to relog for the changes to take place."));
            return;
        }

        if(args[0].equalsIgnoreCase("avaliable")) {
            for(EventTag tag : EventTag.values()) {
                sender.sendMessage(Utils.f("&6Name: &a" + tag.toString() + " &6Visibility: &a" + EventTagDAO.getTagVisibility(tag)));
            }
            return;
        }
        if(args[0].equalsIgnoreCase("refresh")) {
            ServerUtil.runTaskAsync(EventTagDAO::refreshTagVisiblity);
            sender.sendMessage(Lang.REWARDS.f("&aYou have refreshed the local tag visibility cache."));
            return;
        }
        if(args[0].equalsIgnoreCase("setvis")) {
            if(args.length!=3) {
                sender.sendMessage(Utils.f("&c/eventtag setvis <eventtag> <visibility> - &7Sets the visibility of the specified tag. 0 = everyone, 1 = people who have unlocked it, 2 = no one."));
                return;
            }
            EventTag tag;
            try {
                tag = EventTag.valueOf(args[1].toUpperCase());
            }catch (IllegalArgumentException iae) {
                sender.sendMessage(Lang.REWARDS.f("&cThat event tag does not exist. Do the command /eventtag avaliable to view all event tags."));
                return;
            }

            TagVisibility vis;
            try {
                vis = TagVisibility.fromID(Integer.parseInt(args[2]));
            }catch (NumberFormatException nfe) {
                sender.sendMessage(Lang.REWARDS.f("&a" + args[2] + " &cis not a number!"));
                return;
            }

            if(vis==null) {
                sender.sendMessage(Lang.REWARDS.f("&a" + args[2] + " &cis not an id for visibility!"));
                return;
            }
            ServerUtil.runTaskAsync(() -> {
                EventTagDAO.setVisibility(tag, vis);
                EventTagDAO.refreshTagVisiblity();
            });
            sender.sendMessage(Lang.REWARDS.f("&aYou have set the visibility of &6" + tag + " &ato &6" + vis));
            return;
        }

        UUID targetUUID;
        if(Bukkit.getPlayer(args[1])!=null)
            targetUUID = Bukkit.getPlayer(args[1]).getUniqueId();
        else
            targetUUID = UserDAO.getUuidByName(args[1]);
        if(targetUUID==null) {
            sender.sendMessage(Lang.REWARDS.f("&cThe requested player cannot be found online or in the database. If trying to select player that is offline, the name is case-sensitive."));
            return;
        }

        if(args[0].equalsIgnoreCase("list")) {
            if(args.length!=2){
                sender.sendMessage(Utils.f("&c/eventtag remove <player> <eventtag> - &7Removes specified tag from the player."));
                return;
            }
            sender.sendMessage(Lang.REWARDS.f("&7Compiling list..."));
            ServerUtil.runTaskAsync(() -> {
                sender.sendMessage(Lang.REWARDS.f("&7Event Tags For &a" + args[1]));
                Set<String> tags = UserDAO.fetchReadablePlayerTags(targetUUID);
                for(String t: tags) {
                    sender.sendMessage(Utils.f(t));
                }
            });
            return;
        }


        try {
            EventTag.valueOf(args[2].toUpperCase());
        }catch (IllegalArgumentException iae) {
            sender.sendMessage(Lang.REWARDS.f("&cThat event tag does not exist. Do the command /eventtag avaliable to view all event tags."));
            return;
        }
        EventTag tag = EventTag.valueOf(args[2].toUpperCase());



        switch (args[0].toLowerCase()) {
            case "give":
                if(args.length!=3) {
                    sender.sendMessage(Utils.f("&c/eventtag give <player> <eventtag> - &7Gives the specified player the specified tag."));
                    return;
                }
                ServerUtil.runTaskAsync(() -> {
                    UserDAO.addPlayerTag(targetUUID, tag);
                });
                sender.sendMessage(Lang.REWARDS.f("&aYou have given the player the specified tag. The player will have to relog for the changes to take effect."));
                break;
            case "remove":
                if(args.length!=3) {
                    sender.sendMessage(Utils.f("&c/eventtag remove <player> <eventtag> - &7Removes specified tag from the player."));
                    return;
                }
                ServerUtil.runTaskAsync(() -> {
                    UserDAO.removePlayerTag(targetUUID, tag);
                });
                sender.sendMessage(Lang.REWARDS.f("&cYou have removed the player the specified tag. The player will have to relog for the changes to take effect."));
                break;
            default:
                sender.sendMessage(Utils.f("&c/eventtag give <player> <eventtag> - &7Gives the specified player the specified tag."));
                sender.sendMessage(Utils.f("&c/eventtag remove <player> <eventtag> - &7Removes specified tag from the player."));
                sender.sendMessage(Utils.f("&c/eventtag list <player> <eventtag> - &7Lists all the eventtags that the specified player has"));
                sender.sendMessage(Utils.f("&c/eventtag avaliable - &7Lists all avaliable event tags."));
                sender.sendMessage(Utils.f("&4&lNote that players may have to relog for the changes to take place."));
                break;
        }
    }
}
