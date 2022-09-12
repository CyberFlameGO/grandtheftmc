package net.grandtheftmc.vice.drugs;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class DrugCommand implements CommandExecutor {
    public static Collection<String> addingBlocks = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("vice.drugs.admin")) {
            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
            return true;
        }
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.VICE.f("&cYou are not a player!"));
            return true;
        }
        Player player = (Player) s;
        if (args.length != 1) {
            sendHelp(player);
            return true;
        }
        switch (args[0]) {
            case "lb":
            case "lockedblock":
                if (addingBlocks.contains(player.getName())) {
                    player.sendMessage(Lang.DRUGS.f("&cYou are no longer adding/removing locked blocks"));
                    addingBlocks.remove(player.getName());
                } else {
                    player.sendMessage(Lang.DRUGS.f("&aYou are now adding/removing locked blocks"));
                    addingBlocks.add(player.getName());
                }
                return true;
            default:
                sendHelp(player);
        }
        return true;
    }

    public void sendHelp(Player player) {
        player.sendMessage(Lang.DRUGS.f("&7Usage:"));
        player.sendMessage(Utils.f("&a/drugs [lb/lockedblock] &7- Enable/Disable the Adding or Removal of locked blocks"));
    }
}
