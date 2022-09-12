package net.grandtheftmc.gtm.holidays.independenceday;

import net.grandtheftmc.gtm.GTMUtils;
import net.grandtheftmc.gtm.holidays.Holiday;
import org.bukkit.ChatColor;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

import java.time.Month;
import java.util.Optional;

public class IndependenceDay extends Holiday {
    private IndependenceDayTask independenceDayTask;
    private BossBar bossBar;
    private Integer voteCount = 0;

    public IndependenceDay() {
        if (isActive()) {
            new IndependenceDayListener(this);
            if (this.independenceDayTask == null) this.independenceDayTask = new IndependenceDayTask(this);
            if (this.bossBar == null) {
                this.bossBar = Via.getAPI().createBossBar(
                        getNextChatColor() + "HAPPY JULY FOURTH! VOTES UNTIL REWARD DROP: " + String.valueOf(100 - getVoteCount()),
                        1F,
                        BossColor.BLUE,
                        BossStyle.SEGMENTED_10);
            }
        }
    }

    public boolean isActive() {
        return GTMUtils.getMonth() == Month.JULY && GTMUtils.getDay() == 4;
    }

    public Optional<IndependenceDayTask> getIndependenceDayTask() {
        return Optional.ofNullable(this.independenceDayTask);
    }

    public Integer getVoteCount() {
        return this.voteCount;
    }

    public void addVote(int amount) {
        if (this.voteCount == 100) return;
        this.voteCount += amount;
    }

    public void resetVotes() {
        this.voteCount = 0;
        this.getBossBar().setHealth(1F);
    }

    public BossBar getBossBar() {
        return this.bossBar;
    }

    public BossColor getNextBossColor() {
        BossColor currentColor = bossBar.getColor();
        switch (currentColor) {
            case RED:
                return BossColor.WHITE;
            case BLUE:
                return BossColor.RED;
            case WHITE:
                return BossColor.BLUE;
        }
        return BossColor.RED;
    }

    public ChatColor getNextChatColor() {
        if (bossBar == null) return ChatColor.RED;
        switch (bossBar.getColor()) {
            case RED:
                return ChatColor.RED;
            case BLUE:
                return ChatColor.BLUE;
            case WHITE:
                return ChatColor.WHITE;
        }
        return ChatColor.RED;
    }

}