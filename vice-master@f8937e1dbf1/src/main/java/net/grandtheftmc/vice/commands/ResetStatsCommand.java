package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

/**
 * Created by Timothy Lampen on 2017-08-10.
 */
public class ResetStatsCommand implements CommandExecutor{
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(!sender.hasPermission("command.resetstats")) return false;
        if(args.length==1) {
            Player player = Bukkit.getPlayer(args[0]);
            if (player == null){
                sender.sendMessage(Lang.VICE.f("&7That player is not online!"));
                return false;
            }
            sender.sendMessage(Lang.VICE.f("&7You have reset the stats of &b" + player.getName() + "&7!"));
            player.sendMessage(Lang.VICE.f("&7Your stats have been reset by &b" + sender.getName() + "&b!"));
            player.setMaxHealth(20);
            for(PotionEffect e : player.getActivePotionEffects()){
                player.removePotionEffect(e.getType());
            }
        }
        return true;
    }
}
