package net.grandtheftmc.core.commands;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Timothy Lampen on 2017-09-04.
 */
public class BungeeCommand extends CoreCommand<CommandSender> {
    public BungeeCommand() {
        super("bungeecommand", "to do bungeecommands on a bukkit server");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!sender.isOp()) {
            sender.sendMessage(Lang.NOPERM.f(""));
            return;
        }
        if(args.length==0) {
            sender.sendMessage(Utils.f("&7/bungeecommand <command> &e- Executes the selected command on the bungee server."));
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i<args.length; i++) {
            sb.append(args[i] + " ");
        }
        sb.deleteCharAt(sb.length()-1);
        sender.sendMessage(Lang.HUB.f("&7Sending the command &e" + sb.toString() + " &7to the bungee server..."));
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        Player player = sender instanceof Player ? (Player)sender : Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
        out.writeUTF("BukkitCommand");
        out.writeUTF(sb.toString());
        out.writeUTF(player.getName());
        out.writeBoolean((sender instanceof Player));

        if(player==null) {
            sender.sendMessage(Lang.HUB.f("&7Sorry, but this command cannot be executed as there are no players online. Due to limitations within the Bungee API, a player is required to be present."));
            return;
        }
        sender.sendMessage(Lang.HUB.f("&aSent the command to the hub server! waiting on a response..."));
        if(!(sender instanceof Player))
            sender.sendMessage(Lang.HUB.f("&4&lNOTE: &7Because you are console, you will not recieve any information if the command worked. However, you can check using the Bungee console."));
        sender.sendMessage(Lang.HUB.f("&4&l"));
        player.sendPluginMessage(Core.getInstance(), "BungeeCord", out.toByteArray());
    }
}
