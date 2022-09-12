package net.grandtheftmc.gtm.commands;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.GTMUserManager;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.gtm.users.TaxiTarget;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;

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
        GTMUser user = GTM.getUserManager().getLoadedUser(player.getUniqueId());
        if (args.length == 0) {
            if(user.getWantedLevel()!=0) {
                player.sendMessage(Lang.COP_MODE.f("&cSorry, only player's without a wanted level can do this command."));
                return true;
            }
            if (user.hasRequestedBackup()) {
                s.sendMessage(Lang.COP_MODE.f("&7You have already called " + (user.getJobMode() == JobMode.COP ? "for backup" : "the police") + "! Please wait &c&l" + Utils.timeInSecondsToText(Math.round(user.getTimeUntilBackupRequestExpires()/1000.0), "&3", "&7", "&7")+ "&7 to request backup again!"));
                return true;
            }
            player.sendMessage(Lang.COP_MODE.f("&7You have called " + (user.getJobMode() == JobMode.COP ? "for backup" : "the police") + "! A message has been sent to all officers, and they can teleport to you for 1 minute!"));
            user.setLastBackupRequest(System.currentTimeMillis());
            for (GTMUser u : GTMUserManager.getInstance().getUsers()) {
                if (u.getJobMode() == JobMode.COP) {
                    Player p = Bukkit.getPlayer(u.getUUID());
                    if (!Objects.equals(player, p))
                        p.spigot().sendMessage(new ComponentBuilder(Lang.COP_MODE.f((user.getJobMode() == JobMode.COP ? "&3&lCop " : "&7Citizen ") + Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player))).append(" is requesting " + (user.getJobMode() == JobMode.COP ? "backup" : "police assistance") + "! Teleport: ").color(net.md_5.bungee.api.ChatColor.GRAY).
                                append(" [ACCEPT] ").color(net.md_5.bungee.api.ChatColor.GREEN).bold(true).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/backup " + player.getName())).create());
                }
            }
            return true;
        }
        if (user.getJobMode() != JobMode.COP) {
            s.sendMessage(Lang.COP_MODE.f("&7You must be in &3&lCOP Mode&7 to provide backup!"));
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(Lang.COP_MODE.f("&7That player is not online!"));
            return true;
        }
        GTMUser targetUser = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        if (target.getGameMode() == GameMode.SPECTATOR || !targetUser.hasRequestedBackup()) {
            player.sendMessage(Lang.COP_MODE.f("&7That player has not requested backup!"));
            return true;
        }
        GTM.getWarpManager().warp(player, Core.getUserManager().getLoadedUser(player.getUniqueId()), user, new TaxiTarget(target), 0, -1);
        target.sendMessage(Lang.COP_MODE.f("&7" + Core.getUserManager().getLoadedUser(player.getUniqueId()).getColoredName(player) + "&7 has accepted your " + (user.getJobMode() == JobMode.COP ? "backup" : "police assistance") + " request."));
        return true;
    }

}