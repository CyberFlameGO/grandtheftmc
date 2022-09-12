package net.grandtheftmc.vice.utils;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.TimeFormatter;
import net.grandtheftmc.vice.Vice;
import net.grandtheftmc.vice.users.ViceUser;
import org.bukkit.ChatColor;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Stats {
    private static Stats stats;

    public static Stats getInstance() {
        if (stats == null) stats = new Stats();
        return stats;
    }

    public List<String> getStats(Player target) {
        List<String> stats = new ArrayList<>();
        ViceUser user = Vice.getUserManager().getLoadedUser(target.getUniqueId());
        stats.add(Utils.f(target.getDisplayName()));
        stats.add(this.format("Playtime", this.getHoursPlayed(target)));
        stats.add(this.format("Money", '$' + this.numberFormat(user.getMoney())));
        // TODO replace Gang with Cartel
       /* if (user.getGang() != null) {
            stats.add(this.format("Gang", user.getGangName()));
        }
        if (user.getJobMode() == JobMode.CRIMINAL && user.getWantedLevel() > 0) {
            stats.add(this.format("Wanted Level", ChatColor.WHITE + ViceUtils.getWantedLevelStars(user.getWantedLevel())));
        } else if (user.getJobMode() != JobMode.CRIMINAL) {
            stats.add(this.format("Job", user.getJobMode().getColoredNameBold()));
        }*/
        stats.add(this.format("Kills", this.getKillAmount(target)));
        stats.add(this.format("Deaths", this.getDeathAmount(target)));
        stats.add(this.format("K/D", this.getKDRatio(target)));
        return stats;
    }

    public String getKDRatio(Player player) {
        int kills = player.getStatistic(Statistic.PLAYER_KILLS);
        int deaths = player.getStatistic(Statistic.DEATHS);
        if (kills == 0 && deaths == 0) {
            return "0.0";
        }
        double kd = (double) kills / deaths;
        return String.valueOf(kd).substring(0, 3);
    }

    public String getDeathAmount(Player player) {
        int deaths = player.getStatistic(Statistic.DEATHS);
        return this.numberFormat(deaths);
    }

    public String getKillAmount(Player player) {
        int kills = player.getStatistic(Statistic.PLAYER_KILLS);
        return this.numberFormat(kills);
    }

    public long getHoursPlayedRaw(Player player) {
        if (player == null) return 0;
        int ticks = player.getStatistic(Statistic.PLAY_ONE_TICK);
        long minutes = ticks / 20 / 60;
        TimeFormatter tf = Utils.timeFormatter(TimeUnit.MINUTES, minutes);
        return tf.getHours();
    }

    public String getHoursPlayed(Player player) {
        int ticks = player.getStatistic(Statistic.PLAY_ONE_TICK);
        long minutes = ticks / 20 / 60;
        TimeFormatter tf = Utils.timeFormatter(TimeUnit.MINUTES, minutes);
        return tf.getDays() + "d " + tf.getHours() + "h " + tf.getMinutes() + "m";
    }

    public String numberFormat(int num) {
        return NumberFormat.getInstance().format(num);
    }

    public String numberFormat(double num) {
        return NumberFormat.getInstance().format(num);
    }

    public String format(String key, String value) {
        return ChatColor.GRAY + key + ": " + ChatColor.GREEN + value;
    }
}