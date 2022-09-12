package net.grandtheftmc.gtm.utils;

import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.util.TimeFormatter;
import net.grandtheftmc.gtm.GTM;
import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.gang.Gang;
import net.grandtheftmc.gtm.gang.GangManager;
import net.grandtheftmc.gtm.users.GTMUser;
import net.grandtheftmc.gtm.users.JobMode;
import net.grandtheftmc.houses.Houses;
import net.grandtheftmc.houses.users.HouseUser;
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
        GTMUser user = GTM.getUserManager().getLoadedUser(target.getUniqueId());
        HouseUser houseUser = Houses.getUserManager().getLoadedUser(target.getUniqueId());
        stats.add(Utils.f(target.getDisplayName()));
        stats.add(this.format("Playtime", this.getHoursPlayed(target)));
        stats.add(this.format("Money", '$' + this.numberFormat(user.getMoney())));
        stats.add(this.format("Bank", '$' + this.numberFormat(user.getBank())));

        Gang gang = GangManager.getInstance().getGangByMember(target.getUniqueId()).orElse(null);
        if (gang != null) {
            stats.add(this.format("Gang", gang.getName()));
        }

        if (user.getJobMode() == JobMode.CRIMINAL && user.getWantedLevel() > 0) {
            stats.add(this.format("Wanted Level", ChatColor.WHITE + GTMUtils.getWantedLevelStars(user.getWantedLevel())));
        }
        else if (user.getJobMode() != JobMode.CRIMINAL) {
            stats.add(this.format("Job", user.getJobMode().getColoredNameBold()));
        }

        if (!houseUser.getPremiumHouses().isEmpty()) {
            stats.add(this.format("Premium Houses", String.valueOf(houseUser.getPremiumHouses().size())));
        }

        if (!houseUser.getHouses().isEmpty()) {
            stats.add(this.format("Houses", String.valueOf(houseUser.getHouses().size())));
        }

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