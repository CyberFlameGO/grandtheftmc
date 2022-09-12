package net.grandtheftmc.Creative.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.grandtheftmc.Creative.Creative;
import net.grandtheftmc.Creative.users.CreativeRank;
import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.editmode.WorldConfig;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;

public class WorldCommand extends CoreCommand {
    public WorldCommand() {
        super("world", "Switch worlds", "switchworld");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Lang.NOPERM.s());
            return;
        }
        if (args.length == 0) {
            sender.sendMessage(Utils.f("&c/world tp <world> [x] [y] [z] [pitch] [yaw]"));
            return;
        }
        Player p = (Player) sender;
        switch (args[0].toLowerCase()) {
            case "tp": {
                if (args.length == 1) {
                    sender.sendMessage(Utils.f("&c/world tp <world> [x] [y] [z] [yaw] [pitch]"));
                    return;
                }
                World world = Bukkit.getWorld(args[1]);
                if (world == null) {
                    p.sendMessage(Utils.f("&cThat world does not exist!"));
                    return;
                }
                WorldConfig config = Core.getWorldManager().getWorldConfig(world.getName());
                if (config.isRestricted()) {
                    switch (config.getType()) {
                        case USERRANK: {
                            UserRank rank = UserRank.getUserRank(config.getRestricted());
                            if (!Core.getUserManager().getLoadedUser(p.getUniqueId()).isRank(rank)) {
                                p.sendMessage(Lang.RANKS.f("&7You must be " + rank.getColoredNameBold() + "&7 to enter this world!"));
                                return;
                            }
                            break;
                        }
                        case RESTRICTED:
                            p.sendMessage(Lang.NOPERM.s());
                            return;
                        case GAMERANK:
                            CreativeRank rank = CreativeRank.getRankOrNull(config.getRestricted());
                            if (rank == null) rank = CreativeRank.CREATOR;
                            if (!Creative.getUserManager().getLoadedUser(p.getUniqueId()).isRank(rank)) {
                                p.sendMessage(Lang.RANKS.f("&7You must be " + rank.getColoredNameBold() + "&7 to enter this world!"));
                                return;
                            }
                            break;
                        case NONE:
                            break;
                    }

                }
                double x = 0, y = 0, z = 0;
                float pitch = 0, yaw = 0;
                if (args.length > 2) try {
                    x = Double.valueOf(args[2]);
                    if (args.length > 3)
                        y = Double.valueOf(args[3]);
                    if (args.length > 4)
                        z = Double.valueOf(args[4]);
                    if (args.length > 5)
                        yaw = Float.valueOf(args[5]);
                    if (args.length > 6)
                        pitch = Float.valueOf(args[6]);

                } catch (NumberFormatException e) {
                    p.sendMessage(Utils.f("&cMake sure all of the numbers are Doubles (e.g. 0, 90, 150.7, 6.969)"));
                    return;
                }
                p.sendMessage(Utils.f("&aTeleporting..."));
                Location location = new Location(world, x, y, z, yaw, pitch);
                p.teleport(location);
            }
        }
    }
}
