package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.ViceUtils;
import net.grandtheftmc.vice.users.ViceUser;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Liam on 11/10/2016.
 */
public class BribeCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        if (args.length == 0) {
            s.sendMessage(Lang.BRIBE.f("&7Help Command"));
            s.sendMessage(Utils.f("&3/bribe &a<amount>&7 - Send a bribe offer to the cop who arrested you!"));
            s.sendMessage(Utils.f("&3/bribe accept &a<prisoner>&7 - Accept the bribe of a prisoner you arrested!"));
            s.sendMessage(Utils.f("&3/bribe deny &a<prisoner>&7 - Deny the bribe of a prisoner you arrested!"));
            return true;
        }
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        switch (args[0]) {
            case "accept": {
                if (!user.isCop()) {
                    player.sendMessage(Lang.BRIBE.f("&7You need to be in &3&lCOP Mode&7 to accept bribes!"));
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage(Lang.BRIBE.f("&7Please specify the prisoner of whom you would like to accept the bribe!"));
                    return true;
                }
                Player prisoner = Bukkit.getPlayer(args[1]);
                ViceUser prisonerUser = prisoner == null ? null : Vice.getUserManager().getLoadedUser(prisoner.getUniqueId());
                if (prisoner == null || !prisonerUser.isArrested()) {
                    player.sendMessage(Lang.BRIBE.f("&7That player is not in jail!"));
                    return true;
                }
                if (prisonerUser.getJailTimer() <= 5) {
                    player.sendMessage(Lang.BRIBE.f("&7That prisoner is already being released!"));
                    return true;
                }
                if (prisonerUser.getBribe() <= 0) {
                    player.sendMessage(Lang.BRIBE.f("&7That prisoner has not sent a bribe offer to you! You can negotiate with them using &a\"/msg " + prisoner.getName() + "\"&7!"));
                    return true;
                }
                if (!prisonerUser.hasMoney(prisonerUser.getBribe())) {
                    player.sendMessage(Lang.BRIBE.f("&7That prisoner does not have enough money to pay for his bribe!"));
                    return true;
                }
                double bribe = prisonerUser.getBribe();
                prisonerUser.takeMoney(bribe);
                user.addMoney(bribe);
                prisonerUser.setBribe(0);
                prisonerUser.setJailTimer(5);
                ViceUtils.updateBoard(prisoner, prisonerUser);
                ViceUtils.updateBoard(player, user);
                player.sendMessage(Lang.BRIBE.f("&7You accepted a bribe of &a$&l" + bribe + "&7 from &e&l" + prisoner.getName() + "&7!"));
                prisoner.sendMessage(Lang.BRIBE.f("&3&l" + player.getName() + "&7 accepted your bribe of &a$&l" + bribe + "&7!"));
                return true;
            }
            case "deny":
                if (!user.isCop()) {
                    player.sendMessage(Lang.BRIBE.f("&7You need to be in &3&lCOP Mode&7 to accept bribes!"));
                    return true;
                }
                if (args.length != 2) {
                    player.sendMessage(Lang.BRIBE.f("&7Please specify the prisoner of whom you would like to accept the bribe!"));
                    return true;
                }
                Player prisoner = Bukkit.getPlayer(args[1]);
                ViceUser prisonerUser = prisoner == null ? null : Vice.getUserManager().getLoadedUser(prisoner.getUniqueId());
                if (prisoner == null || !prisonerUser.isArrested()) {
                    player.sendMessage(Lang.BRIBE.f("&7That player is not in jail!"));
                    return true;
                }
                if (prisonerUser.getJailTimer() <= 5) {
                    player.sendMessage(Lang.BRIBE.f("&7That prisoner is already being released!"));
                    return true;
                }
                if (prisonerUser.getBribe() <= 0) {
                    player.sendMessage(Lang.BRIBE.f("&7That prisoner has not sent a bribe offer to you! You can negotiate with them using &a\"/msg " + prisoner.getName() + "\"&7!"));
                    return true;
                }
                double bribe = prisonerUser.getBribe();
                player.sendMessage(Lang.BRIBE.f("&7You denied a bribe of &a$&l" + bribe + "&7 from &e&l" + prisoner.getName() + "&7! You can negotiate with them using &a\"/msg " + prisoner.getName() + "\"&7!"));
                player.sendMessage(Lang.BRIBE.f("&3&l" + player.getName() + "&7 denied your bribe of &a$&l" + bribe + "&7! You can negotiate with them using &a\"/msg " + prisoner.getName() + "\"&7!"));
                return true;
            default:
                if (!user.isArrested()) {
                    player.sendMessage(Lang.BRIBE.f("&7You are not in jail!"));
                    return true;
                }
                if (user.getJailTimer() < 5) {
                    player.sendMessage(Lang.BRIBE.f("&7You are already being released!"));
                    return true;
                }
                Player cop = Bukkit.getPlayer(user.getJailCop());
                ViceUser copUser = cop == null ? null : Vice.getUserManager().getLoadedUser(cop.getUniqueId());
                if (cop == null || !copUser.isCop()) {
                    player.sendMessage(Lang.BRIBE.f("&7The cop who arrested you (&3&l" + user.getJailCopName() + "&7) is off duty!"));
                    return true;
                }
                double amnt;
                try {
                    amnt = Double.parseDouble(args[0]);
                } catch (NumberFormatException e) {
                    player.sendMessage(Lang.BRIBE.f("&7The amount must be a number! (double)"));
                    return true;
                }
                if (amnt < 5000) {
                    player.sendMessage(Lang.BRIBE.f("&7Bribes must be at least &a$&l5,000!"));
                    return true;
                }
                if (user.getBribe() * 1.05 > amnt) {
                    player.sendMessage(Lang.BRIBE.f("&7You must raise the bribe by at least &a&l5%&7 of &a$&l" + user.getBribe() + "&7 (&a$&l" + (user.getBribe() * 1.05) + "&7)!"));
                    return true;
                }
                if (!user.hasMoney(amnt)) {
                    player.sendMessage(Lang.BRIBE.f("&7You don't have &c$&l" + amnt + "&7! Please enter a valid number or type &a\"quit\"&7!"));
                    return true;
                }
                user.setBribe(amnt);
                player.sendMessage(Lang.BRIBE.f("&7You sent a bribe offer of &a$&l" + amnt + "&7 to &3&l" + cop.getName() + "&7. You can negotiate with them using &a\"/msg " + cop.getName() + "\"&7!"));
                cop.spigot().sendMessage(new ComponentBuilder(Lang.BRIBE.f("&7A bribe offer of &a$&l" + amnt + "&7 was sent to you by &3&l" + player.getName() + "&7!")).append(" [ACCEPT] ").color(ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe accept " + player.getName())).append("[DENY]").color(ChatColor.DARK_RED).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/bribe deny " + player.getName())).create());
                return true;
        }

    }
}
