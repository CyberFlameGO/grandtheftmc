package net.grandtheftmc.gtm.holidays.independenceday;

import net.grandtheftmc.core.Core;
import net.grandtheftmc.core.Lang;
import net.grandtheftmc.core.util.Utils;
import net.grandtheftmc.core.users.User;
import net.grandtheftmc.core.util.title.NMSTitle;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class IndependenceDayTask extends BukkitRunnable {
    private IndependenceDay independenceDay;

    public IndependenceDayTask(IndependenceDay independenceDay) {
        this.independenceDay = independenceDay;
        if (this.independenceDay.getIndependenceDayTask().isPresent()) return;
        this.runTaskTimer(GTM.getInstance(), 300, 20);
    }

    @Override
    public void run() {
        if (independenceDay.getBossBar() == null) return;
        independenceDay.getBossBar().setTitle(
                independenceDay.getNextChatColor() + "" + ChatColor.BOLD +
                        "HAPPY 4TH OF JULY! VOTES UNTIL REWARD DROP: " +
                        ChatColor.GREEN + String.valueOf(100 - independenceDay.getVoteCount()));
        independenceDay.getBossBar().setColor(independenceDay.getNextBossColor());
        if (independenceDay.getVoteCount() != 0 && independenceDay.getVoteCount() % 100 == 0) {
            List<? extends Player> onlinePlayers = new ArrayList<>(Bukkit.getOnlinePlayers());
            Player randomPlayer = onlinePlayers.get(ThreadLocalRandom.current().nextInt(onlinePlayers.size()));
            User randomUser = Core.getUserManager().getLoadedUser(randomPlayer.getUniqueId());
            randomUser.addCrowbars(100);
            Bukkit.broadcastMessage(Lang.REWARDS.f(randomUser.getColoredName(randomPlayer) + " &7WON &9&l100 CROWBARS&7!"));
            randomPlayer.sendMessage(Lang.CROWBARS_ADD.f("100"));

            onlinePlayers.forEach(onlinePlayer -> {
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_FIREWORK_TWINKLE, 1.0F, 1.0F);
                onlinePlayer.playSound(onlinePlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                if (onlinePlayer == randomPlayer) return;
                User user = Core.getUserManager().getLoadedUser(onlinePlayer.getUniqueId());
                user.addCrowbars(5);
                onlinePlayer.sendMessage(Lang.CROWBARS_ADD.f("5"));
                onlinePlayer.sendTitle("", Utils.f("&9&l+5 Crowbars"), 1, 1, 3);
            });

            independenceDay.resetVotes();
        }
    }
}