package net.grandtheftmc.gtm.gang.command;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.C;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.users.GTMUser;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

public class GangCommand extends CoreCommand<Player> {

    private final GangManager gangManager;

    public GangCommand(GangManager gangManager) {
        super("gang", "Gangs base command.", "g");
        this.gangManager = gangManager;
    }

    @Override
    public void execute(Player sender, String[] args) {
        UUID uuid = sender.getUniqueId();
        User user = Core.getUserManager().getLoadedUser(uuid);
        GTMUser gtmUser = GTM.getUserManager().getLoadedUser(uuid);
        Gang playerGang = gangManager.getGangByMember(uuid).orElse(null);

        if (args.length == 0 || "help".equalsIgnoreCase(args[0])) {
            int page = 1;
            if (args.length > 1) {
                try {
                    page = Integer.parseInt(args[1]);
                } catch (NumberFormatException e) {
                    page = 1;
                }
            }

            if (page < 1) {
                sender.sendMessage(Lang.GANGS.f("&7The page must be a positive number!"));
                return;
            }

            sender.sendMessage(Utils.f(" &7&m---------------&7[&a Gangs Help &7Page &a" + page + "&7/&a2 &7]&m---------------"));
            this.sendHelp(sender, page);
        }

        if(args.length < 1) return;

        switch (args[0]) {
            case "create":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs create <name>"));
                    return;
                }

                gangManager.createGang(sender, args[1]);
                return;

            case "i":
            case "s":
            case "info":
            case "show": {
//                Gang gang = args.length == 1 ? gtmUser.getGang() : GTM.getGangManager().getAlreadyLoadedGang(args[1]);
                Optional<Gang> optional;
                if(args.length == 1) optional = gangManager.getGangByMember(uuid);
                else optional = this.gangManager.getGang(args[1]);

                if (!optional.isPresent()) {
                    sender.sendMessage(Lang.GANGS.f(args.length == 1 ? "&7You are not in any gang!" : "&7That gang does not exist or no one in that gang is online!"));
                    return;
                }

                gangManager.info(optional.get(), sender, user, gtmUser);
                return;
            }

            case "p":
            case "player": {
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <name>"));
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(Lang.GANGS.f("&7That player is not online!"));
                    return;
                }

//                Gang gang = GTM.getUserManager().getLoadedUser(target.getUniqueId()).getGang();
                Gang gang = gangManager.getGangByMember(target.getUniqueId()).orElse(null);
                if (gang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7That player is not in any gang!"));
                    return;
                }

                gangManager.info(gang, sender, user, gtmUser);
                return;
            }

            case "l":
            case "list": {
                if (true) {
                    sender.sendMessage(C.ERROR + "This command is currently disabled.");
                    return;
                }

                Set<Gang> gangs = this.gangManager.getGangs();
                int page = 0;
                if (args.length > 1) {
                    try {
                        page = Integer.parseInt(args[1]) - 1;
                    } catch (NumberFormatException e) {
                        sender.sendMessage(Lang.GANGS.f("&7The page must be a number!"));
                        return;
                    }
                }

                if (page < 0) {
                    sender.sendMessage(Lang.GANGS.f("&7The page must be a positive number!"));
                    return;
                }

                int pages = gangs.size() / 6;
                sender.sendMessage(Utils.f(" &7&m---------------&7[&a&l Gangs List &7Page &a" + (page + 1) + "&7/&a" + pages + " &7&m]---------------"));
                Iterator<Gang> it = gangs.iterator();
                for (int i = 0; i < page * 6; i++) {
                    if (!it.hasNext()) return;
                    Gang g = it.next();
                    g.list(playerGang);
                }
                return;
            }

            case "invite": {
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs invite <name>"));
                    return;
                }
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }
                Player target = Bukkit.getPlayer(args[1]);
                playerGang.invite(sender, user, target);
                return;
            }

            case "accept":
            case "join":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <name>"));
                    return;
                }

                Optional<Gang> gang = this.gangManager.getGang(args[1]);
                if (!gang.isPresent()) {
                    sender.sendMessage(Lang.GANGS.f("&7That gang does not exist!"));
                    return;
                }

                gang.get().accept(sender, user, gtmUser);
                return;

            case "leave":
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                playerGang.leave(sender, user, gtmUser);
                return;

            case "leader": {
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs leader <name>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                Player target = Bukkit.getPlayer(args[1]);
                playerGang.setOwner(sender, user, gtmUser, target);
                return;
            }

            case "promote":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs promote <name>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                playerGang.promote(sender, user, gtmUser, args[1]);
                return;

            case "demote":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs demote <name>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                playerGang.demote(sender, user, gtmUser, args[1]);
                return;

            case "kick":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs kick <name>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                playerGang.kick(sender, user, gtmUser, args[1]);
                return;

            case "disband":
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }
                if (args.length == 2 && "confirm".equalsIgnoreCase(args[1])) {
                    playerGang.disbandConfirm(sender, user, gtmUser);
                    return;
                }
                playerGang.disband(sender);
                return;

            case "rename":
            case "name":
                if (args.length != 2) {
                    sender.sendMessage(Utils.f("&c/gangs name <name>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                playerGang.rename(sender, user, gtmUser, args[1]);
                return;

            case "desc":
            case "description":
                if (args.length < 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <description>"));
                    return;
                }

                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }

                Collection<String> descArgs = new ArrayList<>();
                for (int i = 1; i < args.length; i++) {
                    descArgs.add(args[i]);
                }

                String desc = StringUtils.join(descArgs, " ");
                playerGang.description(sender, user, gtmUser, desc);
                return;

            case "ally":
                if (args.length < 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <name>"));
                    return;
                }
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }
                playerGang.ally(sender, user, gtmUser, args[1]);
                return;

            case "neutral":
                if (args.length < 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <name>"));
                    return;
                }
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }
                playerGang.neutral(sender, user, gtmUser, args[1]);
                return;

            case "enemy":
                if (args.length < 2) {
                    sender.sendMessage(Utils.f("&c/gangs " + args[0] + " <name>"));
                    return;
                }
                if (playerGang == null) {
                    sender.sendMessage(Lang.GANGS.f("&7You are not in any gang!"));
                    return;
                }
                playerGang.enemy(sender, user, gtmUser, args[1]);
                return;

            default:
                this.sendHelp(sender, 1);
                break;
        }
    }

    private void sendHelp(Player player, int page) {
        if(page == 1) {
            player.sendMessage(Utils.f("&a/gang help [page] &7Show this help page"));
            player.sendMessage(Utils.f("&a/gang list [page] &7List the most powerful online gangs"));
            player.sendMessage(Utils.f("&a/gang info <gang> &7Show information about your/a Gang"));
            player.sendMessage(Utils.f("&a/gang join <gang> &7Join a Gang you were invited to"));
            player.sendMessage(Utils.f("&a/gang invite <name> &7Invite a player to your Gang"));
            player.sendMessage(Utils.f("&a/gang create <name> &7Create a new Gang"));
            player.sendMessage(Utils.f("&a/gang player <name> &7Show info about the Gang of another player"));
            player.sendMessage(Utils.f("&a/gang leader <name> &7Transfer leadership of your Gang"));
            player.sendMessage(Utils.f("&a/gang leave &7Leave your Gang"));
            return;
        }

        player.sendMessage(Utils.f("&a/gang promote <name> &7Promote a player to coleader of your Gang"));
        player.sendMessage(Utils.f("&a/gang demote <name> &7Demote a coleader of your Gang"));
        player.sendMessage(Utils.f("&a/gang kick <name> &7Kick a player from your Gang"));
        player.sendMessage(Utils.f("&a/gang disband &7Kick all members and delete your Gang"));
        player.sendMessage(Utils.f("&a/gang rename <name> &7Change the name of your Gang"));
        player.sendMessage(Utils.f("&a/gang desc <text> &7Change the description of your Gang"));
        player.sendMessage(Utils.f("&a/gang ally <gang> &7Declare a Gang your ally"));
        player.sendMessage(Utils.f("&a/gang neutral <gang> &7Designate a Gang as neutral"));
        player.sendMessage(Utils.f("&a/gang enemy <gang> &7Declare a Gang your enemy"));
    }
}
