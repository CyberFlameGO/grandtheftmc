package net.grandtheftmc.core.anticheat.report;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.commands.CoreCommand;
import net.grandtheftmc.core.users.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ReportCommand extends CoreCommand<Player> {

    private final ReportManager reportManager;

    /**
     * Construct a new command.
     */
    public ReportCommand(ReportManager reportManager) {
        super("report", "Report a player if you think they're hacking.");
        this.reportManager = reportManager;
    }

    /**
     * This method is fired when the command is executed.
     *
     * @param sender sender type of the command
     * @param args   command arguments
     */
    @Override
    public void execute(Player sender, String[] args) {
        if(sender == null) return;

        if(args.length < 2) {
            sender.sendMessage(Lang.ANTICHEAT.f("&cIncorrect arguments!"));
            sender.sendMessage(Lang.ANTICHEAT.f("&7Usage: /report <&fplayer&7> <&freason&7>"));
            return;
        }

        Player victim = Bukkit.getPlayer(args[0]);
        if (victim == null) {
            sender.sendMessage(Lang.ANTICHEAT.f("&cPlayer cannot be found."));
            return;
        }

        User user = Core.getUserManager().getLoadedUser(victim.getUniqueId());
        if(user == null) {
            sender.sendMessage(Lang.ANTICHEAT.f("&cPlayer cannot be found."));
            return;
        }

        if(user.isStaff()) {
            sender.sendMessage(Lang.ANTICHEAT.f("&cYou cannot report staff members."));
            return;
        }

        StringBuilder reason = new StringBuilder("");
        for(int i = 1; i < args.length; i++)
            reason.append(args[i]).append(" ");
        reason.setLength(reason.length() - 1);

        reportManager.report(sender, victim, reason.toString());
    }
}
