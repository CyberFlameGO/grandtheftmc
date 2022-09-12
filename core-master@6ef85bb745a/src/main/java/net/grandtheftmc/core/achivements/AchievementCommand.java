package net.grandtheftmc.core.achivements;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.users.UserRank;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

public class AchievementCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender s, Command c, String lbl, String[] args) {
        if (args.length == 0) {
            s.sendMessage(Lang.ACHIEVEMENT.f("&7&lAchievements Help"));
            s.sendMessage(Utils.f("&a/achievement &7locked List all Achievements that are still locked"));
            s.sendMessage(Utils.f("&a/achievement &7unlocked List all Achievements you have unlocked"));
            s.sendMessage(Utils.f("&a/achievement &7shown List your current shown Achievement"));
            s.sendMessage(Utils.f("&a/achievement &7setshown &a<achievement> &7Set your shown achievement"));
            return true;
        }
        String string;
        switch (args[0].toLowerCase()) {
            case "locked": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.toString());
                    return true;
                }
                Player player = (Player) s;
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                Collection<String> achievements = new ArrayList<>();
                for (Achievement achievement : Achievement.values()) {
                    if (user.getUnlockedAchievements().contains(achievement)) continue;
                    achievements.add(achievement.getColor() + achievement.getShortName() + " &7- " + achievement.getDescription());
                }
                string = StringUtils.join(achievements, " \n");
                player.sendMessage(Utils.f("&aLocked achievements: \n" + string));
                return true;
            }
            case "unlocked": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.toString());
                    return true;
                }
                Player player = (Player) s;
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                Collection<String> t = new ArrayList<>();
                user.getUnlockedAchievements().forEach(achievement -> t.add(achievement.getTitle()));
                string = StringUtils.join(t, "&7, &a");
                player.sendMessage(Utils.f("&7Unlocked achievements: &a" + string));
                return true;
            }
            case "shown": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.toString());
                    return true;
                }
                Player player = (Player) s;
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                player.sendMessage(Lang.ACHIEVEMENT.f("&7Shown Achievement: &a" + user.getShownAchievement().getTitle()));
                return true;
            }
            case "setshown": {
                if (!(s instanceof Player)) {
                    s.sendMessage(Lang.NOTPLAYER.toString());
                    return true;
                }
                Player player = (Player) s;
                User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                if (args.length != 2) {
                    player.sendMessage(Utils.f("&aUsage: &7/achievement &asetshown [achievement]"));
                    return true;
                }
                if (Achievement.getAchivement(args[1]).isPresent()) {
                    Achievement achievement = Achievement.getAchivement(args[1]).get();
                    if (!user.getUnlockedAchievements().contains(achievement)) {
                        player.sendMessage(Lang.ACHIEVEMENT.f("&cYou have not unlocked that Achievement yet!"));
                    } else {
                        user.setShownAchievement(Achievement.getAchivement(args[1]).get());
                        player.sendMessage(Utils.f("&7Your shown Achievement has been set to &a" + achievement.getTitle() + "&7!"));
                        user.updateNameTag(player);
                    }
                } else {
                    player.sendMessage(Lang.ACHIEVEMENT.f("&cAchievement not found!"));
                }
                return true;
            }
            case "give":
                if (s instanceof Player) {
                    Player player = (Player) s;
                    User user = Core.getUserManager().getLoadedUser(player.getUniqueId());
                    if (!user.isRank(UserRank.ADMIN)) {
                        player.sendMessage(Lang.NOPERM.s());
                        return true;
                    }
                }
                if (args.length != 3) {
                    s.sendMessage(Utils.f("&aUsage: &7/achievement give [player] [achievement]"));
                    return true;
                }
                if (Bukkit.getPlayer(args[1]) != null) {
                    Player target = Bukkit.getPlayer(args[1]);
                    User targetUser = Core.getUserManager().getLoadedUser(target.getUniqueId());
                    if (Achievement.getAchivement(args[2]).isPresent()) {
                        Achievement achievement = Achievement.getAchivement(args[2]).get();
                        if (targetUser.hasAchievement(achievement)) {
                            s.sendMessage(Lang.ACHIEVEMENT.f("&7Player already has achievement &a" + achievement.getShortName()));
                        } else {
                            targetUser.addAchievement(achievement);
                            s.sendMessage(Lang.ACHIEVEMENT.f("&7Achievement &a" +
                                    achievement.getShortName() + " &7given to &a" + target.getName()));
                        }
                    } else {
                        s.sendMessage(Lang.ACHIEVEMENT.f("&cAchievement not found!"));
                    }
                } else {
                    s.sendMessage(Lang.ACHIEVEMENT.f("&cPlayer not found!"));
                }
                return true;
            default:
                s.sendMessage(Lang.ACHIEVEMENT.f("&7&lAchievements Help"));
                s.sendMessage(Utils.f("&a/achievement &7locked - List all Achievements that are still locked"));
                s.sendMessage(Utils.f("&a/achievement &7unlocked - List all Achievements you have unlocked"));
                s.sendMessage(Utils.f("&a/achievement &7shown - List your current shown Achievement"));
                s.sendMessage(Utils.f("&a/achievement &7setshown &a<achievement> &7- Set your shown achievement"));
                return true;
        }
    }

}
