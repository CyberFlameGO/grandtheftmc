package net.grandtheftmc.gtm.commands;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.tasks.Lottery;
import net.grandtheftmc.gtm.tasks.LotteryPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class LotteryCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String l, String[] args) {
        if (!(s instanceof Player)) {
            s.sendMessage(Lang.NOTPLAYER.toString());
            return true;
        }
        Player player = (Player) s;
        User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
        if (!user.isRank(UserRank.SRMOD)) {
            player.sendMessage(Lang.NOPERM.toString());
            return true;
        }
        if (args.length == 0) {
            s.sendMessage(Utils.f("&c/lottery sethologram"));
            s.sendMessage(Utils.f("&c/lottery start <year> <month> <day> <hour> <minute>"));
            s.sendMessage(Utils.f("&c/lottery end"));
            s.sendMessage(Utils.f("&c/lottery time"));
            s.sendMessage(Utils.f("&c/lottery tickets balance <player>"));
            s.sendMessage(Utils.f("&c/lottery tickets give/take <player> <amount>"));
            s.sendMessage(Utils.f("&c/lottery tickets top"));
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "sethologram":
                if (!user.isRank(UserRank.MANAGER)) {
                    player.sendMessage(Lang.NOPERM.toString());
                    return true;
                }
                GTM.getLottery().setHologramLocation(player.getLocation());
                s.sendMessage(Lang.LOTTERY.f("&7The hologram has been set at your location."));
                return true;
            case "start":
                if (!user.isRank(UserRank.MANAGER)) {
                    player.sendMessage(Lang.NOPERM.toString());
                    return true;
                }
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
                GTM.getLottery().setEnd(end);
                return true;
            case "end": {
                if (!user.isRank(UserRank.ADMIN)) {
                    player.sendMessage(Lang.NOPERM.toString());
                    return true;
                }
                GTM.getLottery().end();
                return true;
            }
            case "test": {
                if (!user.isRank(UserRank.MANAGER)) {
                    player.sendMessage(Lang.NOPERM.toString());
                    return true;
                }
                Lottery.test();
                return true;
            }
            case "time": {
                if (!user.isRank(UserRank.MANAGER)) {
                    player.sendMessage(Lang.NOPERM.toString());
                    return true;
                }
                s.sendMessage(Lang.LOTTERY.f("&7Current time: " + LocalDateTime.now(ZoneId.of("UTC"))));
                s.sendMessage(Lang.LOTTERY.f("&7Time of end: " + GTM.getLottery().getEnd()));
                s.sendMessage(Lang.LOTTERY.f("&7Time until end: " + GTM.getLottery().timeToEnd()));
                return true;
            }
            case "tickets": {
                if (args.length == 1) {
                    s.sendMessage(Utils.f("&7/lottery tickets balance &a<player>"));
                    s.sendMessage(Utils.f("&7/lottery tickets give/take &a<player> <amount>"));
                    s.sendMessage(Utils.f("&7/lottery tickets top"));
                    return true;
                }
                switch (args[1]) {
                    case "balance": {
                        if (args.length != 3) {
                            s.sendMessage(Utils.f("&7/lottery tickets balance &a<player>"));
                            return true;
                        }
                        String targetName = args[2];
                        LotteryPlayer lotteryPlayer = GTM.getLottery().getLotteryPlayer(targetName);
                        if (lotteryPlayer == null) {
                            s.sendMessage(Lang.LOTTERY.f("&cThat player has not bought any tickets!"));
                            return true;
                        }
                        int tickets = lotteryPlayer.getTickets();
                        s.sendMessage(Lang.LOTTERY.f("&7" + targetName));
                        s.sendMessage(Utils.f("&7Tickets: " + tickets));
                        return true;
                    }
                    case "give": {
                        if (!user.isRank(UserRank.ADMIN)) {
                            player.sendMessage(Lang.NOPERM.toString());
                            return true;
                        }
                        if (args.length != 4) {
                            s.sendMessage(Utils.f("&7/lottery tickets give &a<player> <amount>"));
                            return true;
                        }
                        String targetName = args[2];
                        Integer amount = Integer.valueOf(args[3]);
                        LotteryPlayer lotteryPlayer = GTM.getLottery().getLotteryPlayer(targetName);
                        if (lotteryPlayer == null) {
                            s.sendMessage(Lang.LOTTERY.f("&cPlayer not found"));
                            return true;
                        }
                        lotteryPlayer.setTickets(lotteryPlayer.getTickets() + amount);
                        s.sendMessage(Lang.LOTTERY.f("&7Tickets of &a" + targetName + " &7set to &a" + lotteryPlayer.getTickets()));
                        return true;
                    }
                    case "take": {
                        if (args.length != 4) {
                            s.sendMessage(Utils.f("&7/lottery tickets take &a<player> <amount>"));
                            return true;
                        }
                        String targetName = args[2];
                        int amount = Integer.valueOf(args[3]);
                        LotteryPlayer lotteryPlayer = GTM.getLottery().getLotteryPlayer(targetName);
                        if (lotteryPlayer == null) {
                            s.sendMessage(Lang.LOTTERY.f("&cPlayer not found"));
                            return true;
                        }
                        int result = lotteryPlayer.getTickets() - amount;
                        if (result <= 0) result = 1;
                        lotteryPlayer.setTickets(result);
                        s.sendMessage(Lang.LOTTERY.f("&7Tickets of &a" + targetName + " &7set to &a" + result));
                        return true;
                    }
                    case "top": {
                        Map<String, Integer> ticketCounts = new HashMap<>();
                        GTM.getLottery().getLotteryPlayers().forEach(lotteryPlayer -> {
                            ticketCounts.put(lotteryPlayer.getName(), lotteryPlayer.getTickets());
                        });
                        Map<String, Integer> topTickets = GTMUtils.sortByValue(ticketCounts);
                        Iterator iterator = topTickets.entrySet().iterator();
                        int loop = 0;
                        int i = 1;
                        while (iterator.hasNext()) {
                            if (loop > 24) break;
                            Map.Entry pair = (Map.Entry) iterator.next();
                            s.sendMessage(Utils.f("&7#" + i++ + " &a" + pair.getKey() + " &7- " + pair.getValue() + " &7tickets"));
                            iterator.remove();
                            loop += 1;
                        }
                    }
                }
                return true;
            }
            default:
                s.sendMessage(Utils.f("&c/lottery sethologram"));
                s.sendMessage(Utils.f("&c/lottery start <year> <month> <day> <hour> <minute>"));
                s.sendMessage(Utils.f("&c/lottery end"));
                s.sendMessage(Utils.f("&c/lottery time"));
                s.sendMessage(Utils.f("&c/lottery tickets balance <player>"));
                s.sendMessage(Utils.f("&c/lottery tickets give/take <player> <amount>"));
                s.sendMessage(Utils.f("&c/lottery tickets top"));
                return true;
        }
    }

}
