package net.grandtheftmc.core.commands;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.core.util.Utils;

public class EditModeCommand extends CoreCommand<Player> implements RankedCommand {

    /**
     * Construct a new command.
     */
    public EditModeCommand() {
        super("editmode", "A staff command for allowing map editing.");
        super.setNoPermissionMessage(Utils.f("&cYou do not have permission to execute this command!"));
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        if (args.length >= 1) {
            if (Bukkit.getPlayer(args[0]) != null) {
                Player target = Bukkit.getPlayer(args[0]);
                User u = Core.getUserManager().getLoadedUser(target.getUniqueId());
                if (u.hasEditMode()) {
                    u.setEditMode(false);
                    target.setGameMode(Core.getSettings().getDefaultGameMode());
                } else {
                    u.setEditMode(true);
                    target.setGameMode(GameMode.CREATIVE);
                }

                sender.sendMessage(Utils.f("You " + (u.hasEditMode() ? "enabled" : "disabled") + " editmode for " + u.getColoredName(target)));
                return;
            }
        }

        UUID uuid = sender.getUniqueId();
        User u = Core.getUserManager().getLoadedUser(uuid);

        boolean mode = !u.hasEditMode();
        u.setEditMode(mode);
        sender.setGameMode(mode ? GameMode.CREATIVE : Core.getSettings().getDefaultGameMode());

        sender.sendMessage(Utils.f("&a&lEDITMODE&7&l> &fYou " + (mode ? "enabled" : "disabled") + " edit mode."));
    }

    /**
     * Get the required rank to use said command.
     *
     * @return UserRank
     */
    @Override
    public UserRank requiredRank() {
        //return UserRank.ADMIN;
    	return UserRank.BUILDER;
    }

    //    @Override
//    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
//        if (!(s instanceof Player)) {
//            if (args.length == 1) {
//                if (Bukkit.getPlayer(args[0]) != null) {
//                    Player target = Bukkit.getPlayer(args[0]);
//                    User u = Core.getUserManager().getLoadedUser(target.getUniqueId());
//                    if (u.hasEditMode()) {
//                        u.setEditMode(false);
//                        target.setGameMode(Core.getSettings().getDefaultGameMode());
//                    } else {
//                        u.setEditMode(true);
//                        target.setGameMode(GameMode.CREATIVE);
//                    }
//                    s.sendMessage(Utils.f("You " + (u.hasEditMode() ? "enabled" : "disabled") +
//                            " editmode for " + u.getColoredName(target)));
//                    return true;
//                }
//            }
//            s.sendMessage(Utils.f("&cYou are not a player!"));
//            return true;
//        }
//        Player player = (Player) s;
//        UUID uuid = ((Player) s).getUniqueId();
//        User u = Core.getUserManager().getLoadedUser(uuid);
//        if (!u.isAdmin() && !s.hasPermission("editmode")) {
//            s.sendMessage(Utils.f("&cYou do not have permission to execute this command!"));
//            u.setEditMode(false);
//            return true;
//        }
//        boolean mode = !u.hasEditMode();
//        u.setEditMode(mode);
//        if (mode) {
//            player.setGameMode(GameMode.CREATIVE);
//        } else {
//            player.setGameMode(Core.getSettings().getDefaultGameMode());
//        }
//
//        s.sendMessage(Utils.f("&a&lEDITMODE&7&l> &fYou " + (mode ? "enabled" : "disabled") + " edit mode."));
//        return true;
//    }
}
