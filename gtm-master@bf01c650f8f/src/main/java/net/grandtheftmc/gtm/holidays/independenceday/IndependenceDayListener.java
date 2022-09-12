package net.grandtheftmc.gtm.holidays.independenceday;

import net.grandtheftmc.core.voting.events.PlayerVoteEvent;
import net.grandtheftmc.gtm.GTM;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class IndependenceDayListener implements Listener {
    private IndependenceDay independenceDay;

    public IndependenceDayListener(IndependenceDay independenceDay) {
        Bukkit.getPluginManager().registerEvents(this, GTM.getInstance());
        this.independenceDay = independenceDay;
    }

    @EventHandler
    public void playerVoteEvent(PlayerVoteEvent event) {
        float health = independenceDay.getBossBar().getHealth() - 0.01F;
        if (health < 0) health = 0F;
        independenceDay.getBossBar().setHealth(health);
        independenceDay.addVote(1);
    }
}