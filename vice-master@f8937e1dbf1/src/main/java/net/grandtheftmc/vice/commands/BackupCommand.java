package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.CopRank;
import net.grandtheftmc.vice.users.TaxiTarget;
import net.grandtheftmc.vice.users.ViceUser;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

/**
 * Created by Liam on 9/12/2016.
 */
public class BackupCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.s());
            return true;
        }
        Player player = (Player) s;
        ViceUser user = Vice.getUserManager().getLoadedUser(player.getUniqueId());
        if (args.length == 0) {
            if (user.hasRequestedBackup()) {
                s.sendMessage(Lang.COP_MODE.f("&7You have already called " + (user.isCop() ? "for backup" : "the police") + "! Please wait &c&l" + Utils.timeInMillisToText(user.getTimeUntilBackupRequestExpires()) + "&7 to request backup again!"));
                return true;
            }
            player.sendMessage(Lang.COP_MODE.f("&7You have called " + (user.isCop() ? "for backup" : "the police") + "! A message has been sent to all officers, and they can teleport to you for 1 minute!"));
            user.setLastBackupRequest(System.currentTimeMillis());
            for (ViceUser u : Vice.getUserManager().getLoadedUsers()) {
                if (u.isCop()) {
                    Player p = Bukkit.getPlayer(u.getUUID());
                    if (!Objects.equals(player, p))
                        p.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.f((user.isCop() ? "&3&lCop " : "&7Citizen ") + Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player))).append(" is requesting " + (user.isCop() ? "backup" : "police assistance") + "! Teleport: ").color(net.md_5.bungee.api.ChatColor.GRAY).
                                append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup " + player.getName())).create());
                }
            }
            return true;
        }
        if (!user.isCop()) {
            s.sendMessage(Lang.COP_MODE.f("&7You must be a " + CopRank.COP.getColoredNameBold() + "&7 to provide backup!"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Lang.COP_MODE.f("&7That player is not online!"));
            return true;
        }
        ViceUser targetUser = Vice.getUserManager().getLoadedUser(target.getUniqueId());
        if (target.getGameMode() == GameMode.SPECTATOR || !targetUser.hasRequestedBackup()) {
            player.sendMessage(Lang.COP_MODE.f("&7That player has not requested backup!"));
            return true;
        }
        Vice.getWorldManager().getWarpManager().warp(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user, new TaxiTarget(target), 0, -1);
        target.sendMessage(Lang.COP_MODE.f("&7" + Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player) + "&7 has accepted your " + (user.isCop() ? "backup" : "police assistance") + " request."));
        return true;
    }

}