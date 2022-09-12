package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Timothy Lampen on 2017-04-22.
 */
public class DrugCheckCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Lang.DRUGS.f("&7Syntax Error: /drugcheck <player>"));
            return false;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Lang.DRUGS.f("&7Error: The specified player is not online."));
            return false;
        }
        sender.sendMessage(Lang.DRUGS.f("&7Current potion effect portfolio for " + target.getName()));
        for (PotionEffect pe : target.getActivePotionEffects()) {
            sender.sendMessage(ChatColor.GREEN + pe.getType().getName() + ChatColor.BLUE + " : " + ChatColor.GREEN + pe.getAmplifier() + 1 + ChatColor.BLUE + " : " + ChatColor.GREEN + pe.getDuration() / 20 + "s");
        }
        return true;
    }
}
