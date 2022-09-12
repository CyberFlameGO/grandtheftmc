package net.grandtheftmc.core.nametags;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.menus.MenuManager;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


/**
 * Created by Liam on 17/11/2016.
 */
public class NametagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {
        if (Core.getSettings().loadCosmetics() && args.length == 0 && s instanceof Player) {
            MenuManager.openMenu((Player) s, "nametags");
            return true;
        }
        if (!s.hasPermission("command.nametag")) {
            s.sendMessage(Lang.NOPERM.s());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Lang.NAMETAGS.f("&7/nametag list"));
            s.sendMessage(Lang.NAMETAGS.f("&7/nametag give <player> <name>"));
            s.sendMessage(Lang.NAMETAGS.f("&7/nametag remove <player> <name>"));
            s.sendMessage(Lang.NAMETAGS.f("&7/nametag reload"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "list": {
                String msg = "";
                for (Nametag t : Core.getNametagManager().getNametags())
                    msg += "&a" + t.toString().toLowerCase() + "&7, ";
                if (msg.endsWith("&7, "))
                    msg.substring(0, msg.length() - 4);
                s.sendMessage(Lang.NAMETAGS.f("&7List of &e&lNametags&7:"));
                s.sendMessage(Utils.f(msg));
                return true;
            }
            case "give": {
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/nametag give <player> <name>"));
                    return true;
                }
                Nametag tag = Core.getNametagManager().getNametag(args[2]);
                if (tag == null) {
                    s.sendMessage(Lang.NAMETAGS.f("&7That is not a nametag! Do &a/nametag list&7 to see a list!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
//                    Core.sql.updateAsyncLater("update nametags set `" + tag + "`=true where name='" + args[1] + "';");
                    s.sendMessage(Lang.NAMETAGS.f("&7That player is not online, so his nametags have been updated directly in the database!"));
                    return true;
                }
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.giveNametag(tag);
                s.sendMessage(Lang.NAMETAGS.f("&7You gave nametag &a" + tag.getDisplayName() + "&7 to player " + u.getColoredName(player) + "&7!"));
                return true;
            }
            case "remove":
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&c/nametag remove <player> <name>"));
                    return true;
                }
                Nametag tag = Core.getNametagManager().getNametag(args[2]);
                if (tag == null) {
                    s.sendMessage(Lang.NAMETAGS.f("&7That is not a nametag! Do &a/nametag list&7 to see a list!"));
                    return true;
                }
                Player player = Bukkit.getPlayer(args[1]);
                if (player == null) {
//                    Core.sql.updateAsyncLater("update nametags set `" + tag + "`=false where name='" + args[1] + "';");
                    s.sendMessage(Lang.NAMETAGS.f("&7That player is not online, so his nametags have been updated directly in the database!"));
                    return true;
                }
                User u = Core.getUserManager().getLoadedUser(player.getUniqueId());
                u.takeNametag(tag);
                s.sendMessage(Lang.NAMETAGS.f("&7You removed nametag &a" + tag.getDisplayName() + "&7 from player " + u.getColoredName(player) + "&7!"));
                return true;
            case "reload":
                Core.getSettings().setNametagsConfig(Utils.loadConfigFromMaster("nametags"));
                Core.getNametagManager().loadNametags();
                s.sendMessage(Utils.f("&7The nametags config was reloaded!"));
                return true;
            default:
                s.sendMessage(Lang.NAMETAGS.f("&7/nametag list"));
                s.sendMessage(Lang.NAMETAGS.f("&7/nametag give <player> <name>"));
                s.sendMessage(Lang.NAMETAGS.f("&7/nametag remove <player> <name>"));
                s.sendMessage(Lang.NAMETAGS.f("&7/nametag reload"));
                return true;
        }

    }
}
