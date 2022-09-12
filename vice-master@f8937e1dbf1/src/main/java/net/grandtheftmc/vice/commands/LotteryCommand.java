package net.grandtheftmc.vice.commands;

import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.tasks.Lottery;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class LotteryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!s.hasPermission("command.lottery")) {
            s.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/lottery sethologram"));
            s.sendMessage(Utils.f("&c/lottery start <year> <month> <day> <hour> <minute>"));
            s.sendMessage(Utils.f("&c/lottery end"));
            s.sendMessage(Utils.f("&c/lottery time"));
            s.sendMessage(Utils.f("&c/lottery tickets balance <player>"));
            s.sendMessage(Utils.f("&c/lottery tickets give/take <player> <amount>"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "sethologram":
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.s());
                    return true;
                }
                Player player = (Player) s;
                Vice.getLottery().setHologramLocation(player.getLocation());
                s.sendMessage(Lang.LOTTERY.f("&7The hologram has been set at your location."));
                return true;
            case "start":
                if (args.length != 6) {
                    s.sendMessage(Lang.LOTTERY.f("&7Please specify when the lottery should end in the following format: /lottery start <year> <month> <day> <hour> <minute>"));
                    return true;
                }
                int year = Integer.parseInt(args[1]);
                int month = Integer.parseInt(args[2]);
                int day = Integer.parseInt(args[3]);
                int hour = Integer.parseInt(args[4]);
                int minute = Integer.parseInt(args[5]);
                LocalDateTime end = LocalDateTime.of(year, month, day, hour, minute);
                Vice.getLottery().setEnd(end);
                return true;
            case "end": {
                Vice.getLottery().end();
                return true;
            }
            case "test": {
                Lottery.test();
                return true;
            }
            case "time": {
                s.sendMessage(Lang.LOTTERY.f("&7Current time: " + LocalDateTime.now(ZoneId.of("UTC"))));
                s.sendMessage(Lang.LOTTERY.f("&7Time of end: " + Vice.getLottery().getEnd()));
                s.sendMessage(Lang.LOTTERY.f("&7Time until end: " + Vice.getLottery().timeToEnd()));
                return true;
            }
            case "tickets": {
                if (args.length == 1) {
                    s.sendMessage(Utils.f("&c/lottery tickets balance <player>"));
                    s.sendMessage(Utils.f("&c/lottery tickets give/take <player> <amount>"));
                    return true;
                }
                switch (args[1]) {
                    case "balance":
                        return true;
                    case "give":
                        return true;
                    case "take":
                        return true;
                }
                return true;
            }
            default:
                s.sendMessage(Utils.f("&c/lottery"));
                return true;
        }
    }

}
